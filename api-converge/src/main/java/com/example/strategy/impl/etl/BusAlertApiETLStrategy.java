package com.example.strategy.impl.etl;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.example.exception.ResourceException;
import com.example.exception.ResourceNotUpdateException;
import com.example.utils.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.example.exception.ResourceFormatErrorException;
import com.example.model.dto.etl.EncapsulationFile;
import com.example.model.dto.etl.ExtractResult;
import com.example.model.dto.etl.ResourceInfo;
import com.example.model.dto.etl.TransformResult;
import com.example.model.dto.source.ptx.bus.BusAlertKaoMaster;
import com.example.model.dto.source.ptx.bus.BusAlertKaoRoute;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("BusAlertApiETLStrategy")
public class BusAlertApiETLStrategy extends PtxApiETLStrategy {

	@Override
	public ExtractResult extract(ResourceInfo resourceInfo) throws ResourceException {
		String token = tdxService.getToken();
		Map<String, String> headers = HttpUtils.customHeaderFoTDX(token);
		try {
			// Step 1: Check resource available or not
			Date updateTime = new Date();
			String resource = resourceInfo.getResource();
			log.info("Resource: `{}`", resource);
			String resourceContent = JsonUtils.toJsonString(resource, headers).replaceAll("\\\\n","");

			// Step 2: Extract the array part of resource
			String resourceArrayPart = resourceContent.substring(resourceContent.indexOf('['), resourceContent.lastIndexOf(']') + 1);
			String similarDateTime = StringTools.findFirstMatchSequence(resourceContent, DateUtils.DATE_SIMILAR_REGEX);
			Date srcUpdateTime = ClassUtils.isValid(similarDateTime) ? DateUtils.parseStrToDate(similarDateTime) : updateTime;
			log.info("Resource fetch spent {}ms", System.currentTimeMillis() - updateTime.getTime());

			return ETLHelper.buildExtractResult(resourceInfo, resourceArrayPart, srcUpdateTime, updateTime);
		} catch(ResourceNotUpdateException | ResourceFormatErrorException e) {
			throw e;
		} catch (Exception e) {
			throw new ResourceException(e);
		}
	}

	@Override
	public TransformResult transform(ResourceInfo resourceInfo, ExtractResult extractResult)
			throws ResourceFormatErrorException {
		// Step 1: Serialize to related model
		long startTime = System.currentTimeMillis();
		String targetTable = resourceInfo.getTargetTable();
		String routeTable = String.format("%s_route", targetTable).replace("_master", "");
		Date srcUpdateTime = extractResult.getSrcUpdateTime();
		Date updateTime = extractResult.getUpdateTime();
		Map<String, EncapsulationFile> importFileMap = new HashMap<>();
		// Get resources and deserialize to model array
		List<File> resources = extractResult.getResources();

		// Step 2: Transfer result and map to related model
		List<BusAlertKaoMaster> busAlertKaoMasterList = resources.stream().map(FileOperationUtils::extractContent)
				.map(content -> JsonUtils.toBeanList(content, BusAlertKaoMaster.class)).flatMap(List::stream)
				.map(busAlertKaoMaster -> {
					busAlertKaoMaster.setSrcUpdateTime(srcUpdateTime);
					busAlertKaoMaster.setUpdateTime(updateTime);
					busAlertKaoMaster.setInfoTime(busAlertKaoMaster.getSrcUpdateTime());
					busAlertKaoMaster.setInfoDate(busAlertKaoMaster.getSrcUpdateTime());

					return busAlertKaoMaster;
				}).collect(Collectors.toList());

		// Step 3: Transfer other result and map to related model
		AtomicInteger routeCounter = new AtomicInteger(1);
		List<BusAlertKaoRoute> busAlertKaoRouteList = busAlertKaoMasterList.stream().map(busAlertKaoMaster -> {
			List<BusAlertKaoRoute> busAlertKaoRoutes = busAlertKaoMaster.getScope().getBusAlertKaoRoutes().stream()
					.map(busAlertKaoRoute -> {
						busAlertKaoRoute.setAlertID(busAlertKaoMaster.getAlertID());

						return busAlertKaoRoute;
					}).collect(Collectors.toList());
			ETLHelper.copyProperties(routeCounter, busAlertKaoMaster, busAlertKaoRoutes);
			return busAlertKaoRoutes;
		}).flatMap(List::stream).collect(Collectors.toList());

		try {
			// Step 3: Map model list to encapsulation file
			EncapsulationFile masterFile = ETLHelper.buildEncapsulationFile(extractResult, targetTable,
					busAlertKaoMasterList);
			EncapsulationFile detailFile = ETLHelper.buildEncapsulationFile(extractResult, routeTable,
					busAlertKaoRouteList);
			importFileMap.put(targetTable, masterFile);
			importFileMap.put(routeTable, detailFile);
			log.info("Extract result transform spent {}ms", System.currentTimeMillis() - startTime);

			return TransformResult.builder().importFileMap(importFileMap).build();
		} catch (Exception e) {
			throw new ResourceFormatErrorException(e);
		}
	}
}
