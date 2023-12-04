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

import com.example.service.interfaces.TdxService;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.example.model.dto.source.traffic.live.VDLive;
import com.example.model.dto.source.traffic.live.VDLiveLane;
import com.example.model.dto.source.traffic.live.VDLiveLinkFlow;
import com.example.model.dto.source.traffic.live.VDLiveVehicle;
import com.example.utils.DateUtils;
import com.example.utils.ETLHelper;
import com.example.utils.FileOperationUtils;
import com.example.utils.HttpUtils;
import com.example.utils.JsonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("VDLiveETLStrategy")
public class VDLiveETLStrategy extends TrafficApiETLStrategy {

	@Autowired
	TdxService tdxService;
	@Override
	public ExtractResult extract(ResourceInfo resourceInfo) throws ResourceException {
		String token = tdxService.getToken();
		Map<String, String> headers = HttpUtils.customHeaderFoTDX(token);
		try {
			// Step 1: Check resource available or not
			Date updateTime = new Date();
			String resource = resourceInfo.getResource();
			log.info("Resource: `{}`", resource);
			String resourceContent = JsonUtils.toJsonString(resource, headers);
			// Step 2: Extract the array part of resource
			JsonNode tree = JsonUtils.getMapper().readTree(resourceContent);
			// Get json array part
			JsonNode arrayNode = StreamSupport.stream(tree.spliterator(), false)
					.filter(JsonNode::isArray)
					.findAny()
					.orElseThrow(() -> new ResourceFormatErrorException("The array content not existed!"));
			// Get authorityCode and set in every item of array if the property exists
			JsonNode updateIntervalNode = tree.get(UPDATE_INTERVAL_PROPERTY);
			JsonNode srcUpdateTimeNode = tree.get(SRCUPDATE_TIME_PROPERTY);
			JsonNode authorityCodeNode = tree.get(AUTHORITY_CODE_PROPERTY);
			Date srcUpdateTime = Objects.nonNull(srcUpdateTimeNode) ? DateUtils.parseStrToDate(srcUpdateTimeNode.asText()) : updateTime;
			StreamSupport.stream(arrayNode.spliterator(), true)
					.map(ObjectNode.class::cast)
					.forEach(node -> {
						node.set(UPDATE_INTERVAL_PROPERTY, updateIntervalNode);
						node.set(SRCUPDATE_TIME_PROPERTY, srcUpdateTimeNode);
						node.set(AUTHORITY_CODE_PROPERTY, authorityCodeNode);
					});

			String resourceArrayPart = arrayNode.toString();
			log.info("Resource fetch spent {}ms", System.currentTimeMillis() - updateTime.getTime());

			return ETLHelper.buildExtractResult(resourceInfo, resourceArrayPart, srcUpdateTime, updateTime);
		} catch (ResourceNotUpdateException | ResourceFormatErrorException e) {
			throw e;
		} catch (Exception e) {
			throw new ResourceException(e);
		}
	}

	@Override
	public TransformResult transform(ResourceInfo resourceInfo, ExtractResult extractResult)
			throws ResourceFormatErrorException {
		// Step 1: Prepare transform information
		long startTime = System.currentTimeMillis();
		String targetTable = resourceInfo.getTargetTable();
		String linkflowTable = String.format("%s_linkflow", targetTable);
		String vehicleTable = String.format("%s_vehicle", targetTable);
		Date srcUpdateTime = extractResult.getSrcUpdateTime();
		Date updateTime = extractResult.getUpdateTime();
		Map<String, EncapsulationFile> importFileMap = new HashMap<>();
		// Get resources and deserialize to model array
		List<File> resources = extractResult.getResources();

		// Step 2: Transfer result and map to related model
		List<VDLive> vdList = resources.stream().map(FileOperationUtils::extractContent)
				.map(content -> JsonUtils.toBeanList(content, VDLive.class)).flatMap(List::stream).map(vdLive -> {
					vdLive.setSrcUpdateTime(srcUpdateTime);
					vdLive.setUpdateTime(updateTime);
					vdLive.setInfoTime(vdLive.getDataCollectTime());
					vdLive.setInfoDate(vdLive.getInfoTime());

					return vdLive;
				}).collect(Collectors.toList());

		// Step 3: Transfer other result and map to related model
		AtomicInteger linkCounter = new AtomicInteger(1);
		AtomicInteger laneCounter = new AtomicInteger(1);
		AtomicInteger vehicleCounter = new AtomicInteger(1);
		List<VDLiveVehicle> allVehicles = new ArrayList<>();
		// VDLiveLane process
		List<VDLiveLane> laneList = vdList.stream().map(vdLive -> {
			List<VDLiveLinkFlow> linkFlows = vdLive.getLinkFlows();
			ETLHelper.copyProperties(linkCounter, vdLive, linkFlows);
			return linkFlows.stream().map(linkFlow -> {
				List<VDLiveLane> lanes = linkFlow.getLanes();
				ETLHelper.copyProperties(laneCounter, linkFlow, lanes);

				return lanes;
			}).flatMap(List::stream).collect(Collectors.toList());
		}).flatMap(List::stream).collect(Collectors.toList());

		// VDLiveVehicle process
		laneList.stream().forEach(lane -> {
			List<VDLiveVehicle> vehicles = lane.getVehicles();

			allVehicles.addAll(vehicles);
			ETLHelper.copyProperties(vehicleCounter, lane, vehicles);

		});

		try {
			// Step 3: Map model list to encapsulation file
			EncapsulationFile masterFile = ETLHelper.buildEncapsulationFile(extractResult, targetTable, vdList);
			EncapsulationFile detailFile = ETLHelper.buildEncapsulationFile(extractResult, linkflowTable, laneList);
			EncapsulationFile detail2File = ETLHelper.buildEncapsulationFile(extractResult, vehicleTable, allVehicles);
			importFileMap.put(targetTable, masterFile);
			importFileMap.put(linkflowTable, detailFile);
			importFileMap.put(vehicleTable, detail2File);
			log.info("Extract result transform spent {}ms", System.currentTimeMillis() - startTime);

			return TransformResult.builder().importFileMap(importFileMap).build();
		} catch (Exception e) {
			throw new ResourceFormatErrorException(e);
		}
	}

}
