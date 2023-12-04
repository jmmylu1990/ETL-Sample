package com.example.strategy.impl.etl;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.example.exception.ResourceException;
import com.example.exception.ResourceFormatErrorException;
import com.example.exception.ResourceNotUpdateException;
import com.example.model.dto.etl.EncapsulationFile;
import com.example.model.dto.etl.ExtractResult;
import com.example.model.dto.etl.ResourceInfo;
import com.example.model.dto.etl.TransformResult;
import com.example.model.dto.source.traffic.device.VD;
import com.example.model.dto.source.traffic.device.VDDetectionlinkForKHH;
import com.example.model.dto.source.traffic.device.VDForKHH;
import com.example.model.dto.source.traffic.live.VDLive;
import com.example.model.dto.source.traffic.live.VDLiveLane;
import com.example.model.dto.source.traffic.live.VDLiveLinkFlow;
import com.example.model.dto.source.traffic.live.VDLiveVehicle;
import com.example.utils.ClassUtils;
import com.example.utils.DateUtils;
import com.example.utils.ETLHelper;
import com.example.utils.FileOperationUtils;
import com.example.utils.HttpUtils;
import com.example.utils.JsonUtils;
import com.example.utils.StringTools;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("VDForMssqlETLStrategy")
public class VDForMssqlETLStrategy extends TrafficApiETLStrategy {

	@Override
	public ExtractResult extract(ResourceInfo resourceInfo) throws ResourceException {
		try {
			// Step 1: Check resource available or not
			Date updateTime = new Date();
			String resource = resourceInfo.getResource();
			log.info("Resource: `{}`", resource);

			// Step 2: Extract the array part of resource
			String resourceContent = JsonUtils.toJsonString(resource);
			String resourceArrayPart = resourceContent.substring(resourceContent.indexOf('['),
					resourceContent.lastIndexOf(']') + 1);
			String similarDateTime = StringTools.findFirstMatchSequence(resourceContent, DateUtils.DATE_SIMILAR_REGEX);
			Date srcUpdateTime = ClassUtils.isValid(similarDateTime) ? DateUtils.parseStrToDate(similarDateTime)
					: updateTime;
			log.info("Resource fetch spent {}ms", System.currentTimeMillis() - updateTime.getTime());

			return ETLHelper.buildExtractResult(resourceInfo, resourceArrayPart, srcUpdateTime, updateTime);
		} catch (ResourceNotUpdateException | ResourceFormatErrorException e) {
			throw e;
		} catch (Exception e) {
			throw new ResourceException(e);
		}
	}

	@Override
	public TransformResult transform(ResourceInfo resourceInfo, ExtractResult extractResult) throws ResourceFormatErrorException {
		// Step 1: Prepare transform information
		long startTime = System.currentTimeMillis();
		String targetTable = resourceInfo.getTargetTable();
		String detectionlinkTable = String.format("%s_detectionlink", targetTable);
		Date srcUpdateTime = extractResult.getSrcUpdateTime();
		Date updateTime = extractResult.getUpdateTime();
		Map<String, EncapsulationFile> importFileMap = new HashMap<>();
		// Get resources and deserialize to model array 
		List<File> resources = extractResult.getResources();

		// Step 2: Transfer result and map to related model
		List<VDForKHH> vdList = resources.stream()
			.map(FileOperationUtils::extractContent)
			.map(content -> JsonUtils.toBeanList(content, VDForKHH.class))
			.flatMap(List::stream)
			.map(vd -> {
//				vd.setSrcUpdateTime(srcUpdateTime);
				vd.setUpdateTime(updateTime);
//				vd.setInfoTime(srcUpdateTime);
//				vd.setInfoDate(vd.getInfoTime());
				return vd;
			})
			.collect(Collectors.toList());

		// Step 3: Transfer other result and map to related model
		AtomicInteger linkCounter = new AtomicInteger(1); 
		List<VDDetectionlinkForKHH> detectionLinkList = vdList.stream().map(vd -> {
			List<VDDetectionlinkForKHH> detectionlinks = vd.getDetectionlinks();
			ETLHelper.copyProperties(linkCounter, vd, detectionlinks);
			return detectionlinks; 
		})
		.flatMap(List::stream)
		.collect(Collectors.toList());
		
		try {
			// Step 3: Map model list to encapsulation file
			EncapsulationFile masterFile = ETLHelper.buildEncapsulationFile(extractResult, targetTable, vdList);
			EncapsulationFile detailFile = ETLHelper.buildEncapsulationFile(extractResult, detectionlinkTable, detectionLinkList);
			importFileMap.put(targetTable, masterFile);
			importFileMap.put(detectionlinkTable, detailFile);
			log.info("Extract result transform spent {}ms", System.currentTimeMillis() - startTime);
			
			return TransformResult.builder()
					.importFileMap(importFileMap)
					.build();
		} catch (Exception e) {
			throw new ResourceFormatErrorException(e);
		}
	}

}
