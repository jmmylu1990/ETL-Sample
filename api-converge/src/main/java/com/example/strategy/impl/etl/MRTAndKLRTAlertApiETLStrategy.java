package com.example.strategy.impl.etl;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.example.utils.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.JsonNode;
import com.example.exception.ResourceException;
import com.example.exception.ResourceFormatErrorException;
import com.example.exception.ResourceNotUpdateException;
import com.example.model.dto.etl.EncapsulationFile;
import com.example.model.dto.etl.ExtractResult;
import com.example.model.dto.etl.ResourceInfo;
import com.example.model.dto.etl.TransformResult;
import com.example.model.dto.source.alert.AlertKaoMaster;
import com.example.model.dto.source.alert.AlertKaoNetwork;
import com.example.model.dto.source.alert.AlertKaoRoutes;
import com.example.model.dto.source.alert.AlertKaoLines;
import com.example.model.dto.source.alert.AlertKaoStations;
import com.example.model.dto.source.alert.AlertKaoTrains;
import com.example.model.dto.source.alert.AlertKaoLineSections;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("MRTAndKLRTAlertApiETLStrategy")
public class MRTAndKLRTAlertApiETLStrategy extends PtxApiETLStrategy {

	@Override
	public ExtractResult extract(ResourceInfo resourceInfo) throws ResourceException {
		String token = tdxService.getToken();
		Map<String, String> headers = HttpUtils.customHeaderFoTDX(token);
		try {
			// Step 1: Check resource available or not
			Date updateTime = new Date();
			String resource = resourceInfo.getResource();
			log.info("Resource: `{}`", resource);

			JsonNode jsonNode = JsonUtils.getMapper().readTree(JsonUtils.toJsonString(resource, headers));
		
			String resourceContent = JsonUtils.getMapper().writeValueAsString(jsonNode.get("Alerts"));
			System.out.println("jsonNode:"+jsonNode.toString());
			String srcUpdateTimeStr = jsonNode.get("SrcUpdateTime").asText();

			// Step 2: Extract the array part of resource
			String resourceArrayPart = resourceContent.substring(resourceContent.indexOf('['),
					resourceContent.lastIndexOf(']') + 1);
			Date srcUpdateTime = DateUtils.parseStrToDate("yyyy-MM-dd'T'HH:mm:ss+08:00", srcUpdateTimeStr);
		
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
		// Step 1: Serialize to related model
		long startTime = System.currentTimeMillis();
		String targetTable = resourceInfo.getTargetTable();
		String networkTable = String.format("%s_network", targetTable).replace("_master", "");
		String stationsTable = String.format("%s_stations", targetTable).replace("_master", "");
		String linesTable = String.format("%s_lines", targetTable).replace("_master", "");
		String routesTable = String.format("%s_routes", targetTable).replace("_master", "");
		String trainsTable = String.format("%s_trains", targetTable).replace("_master", "");
		String lineSectionsTable = String.format("%s_line_section", targetTable).replace("_master", "");
		Date srcUpdateTime = extractResult.getSrcUpdateTime();
		Date updateTime = extractResult.getUpdateTime();
		Map<String, EncapsulationFile> importFileMap = new HashMap<>();
		// Get resources and deserialize to model array
		List<File> resources = extractResult.getResources();

		// Step 2: Transfer result and map to related model
		List<AlertKaoMaster> alertKaoMasterList = resources.stream().map(FileOperationUtils::extractContent)
				.map(content -> JsonUtils.toBeanList(content, AlertKaoMaster.class)).flatMap(List::stream)
				.map(alertKaoMaster -> {
					alertKaoMaster.setSrcUpdateTime(srcUpdateTime);
					alertKaoMaster.setUpdateTime(updateTime);
					alertKaoMaster.setInfoTime(alertKaoMaster.getSrcUpdateTime());
					alertKaoMaster.setInfoDate(alertKaoMaster.getSrcUpdateTime());
					alertKaoMaster.getScope().getNetwork().setAlertID(alertKaoMaster.getAlertID());
					if (alertKaoMaster.getScope().getStations().size() == 0) {
						AlertKaoStations alertKaoStations = new AlertKaoStations();
						alertKaoStations.setAlertID(alertKaoMaster.getAlertID());
						alertKaoMaster.getScope().getStations().add(alertKaoStations);
					}

					if (alertKaoMaster.getScope().getLines().size() == 0) {
						AlertKaoLines alertKaoLines = new AlertKaoLines();
						alertKaoLines.setAlertID(alertKaoMaster.getAlertID());
						alertKaoMaster.getScope().getLines().add(alertKaoLines);

					}

					if (alertKaoMaster.getScope().getRoutes().size() == 0) {
						AlertKaoRoutes alertKaoRoutes = new AlertKaoRoutes();
						alertKaoRoutes.setAlertID(alertKaoMaster.getAlertID());
						alertKaoMaster.getScope().getRoutes().add(alertKaoRoutes);

					}

					if (alertKaoMaster.getScope().getTrains().size() == 0) {
						AlertKaoTrains alertKaoTrains = new AlertKaoTrains();
						alertKaoTrains.setAlertID(alertKaoMaster.getAlertID());
						alertKaoMaster.getScope().getTrains().add(alertKaoTrains);

					}
					if (alertKaoMaster.getScope().getTrains().size() == 0) {
						AlertKaoTrains alertKaoTrains = new AlertKaoTrains();
						alertKaoTrains.setAlertID(alertKaoMaster.getAlertID());
						alertKaoMaster.getScope().getTrains().add(alertKaoTrains);

					}
					if (alertKaoMaster.getScope().getLineSections().size() == 0) {
						AlertKaoLineSections alertKaoLineSections = new AlertKaoLineSections();
						alertKaoLineSections.setAlertID(alertKaoMaster.getAlertID());
						alertKaoMaster.getScope().getLineSections().add(alertKaoLineSections);

					}

					return alertKaoMaster;
				}).collect(Collectors.toList());
		// Step 3: Transfer other result and map to related model
		//AtomicInteger networkCounter = new AtomicInteger(1);
		AtomicInteger stationsCounter = new AtomicInteger(1);
		AtomicInteger linesCounter = new AtomicInteger(1);
		AtomicInteger routesCounter = new AtomicInteger(1);
		AtomicInteger trainsCounter = new AtomicInteger(1);
		AtomicInteger lineSectionsCounter = new AtomicInteger(1);

		List<AlertKaoStations> alertKaoStationsList = alertKaoMasterList.stream().map(alertKaoMaster -> {

			List<AlertKaoStations> alertKaoStations = alertKaoMaster.getScope().getStations().stream().map(station -> {
				station.setAlertID(alertKaoMaster.getAlertID());
				return station;
			}).collect(Collectors.toList());

			ETLHelper.copyProperties(stationsCounter, alertKaoMaster, alertKaoStations);
			return alertKaoStations;
		}).flatMap(List::stream).collect(Collectors.toList());

		List<AlertKaoNetwork> alertKaoNetworkList = alertKaoMasterList.stream().map(alertKaoMaster -> {
			AlertKaoNetwork alertKaoNetwork = alertKaoMaster.getScope().getNetwork();
			alertKaoNetwork.setAlertID(alertKaoMaster.getAlertID());
			alertKaoNetwork.setSrcUpdateTime(srcUpdateTime);
			alertKaoNetwork.setUpdateTime(updateTime);
			alertKaoNetwork.setInfoTime(srcUpdateTime);
			alertKaoNetwork.setInfoDate(srcUpdateTime);
			return alertKaoNetwork;
		}).collect(Collectors.toList());

		List<AlertKaoLines> alertKaoLinesList = alertKaoMasterList.stream().map(alertKaoMaster -> {

			List<AlertKaoLines> alertKaoLines = alertKaoMaster.getScope().getLines().stream().map(line -> {
				line.setAlertID(alertKaoMaster.getAlertID());
				return line;
			}).collect(Collectors.toList());

			ETLHelper.copyProperties(linesCounter, alertKaoMaster, alertKaoLines);
			return alertKaoLines;
		}).flatMap(List::stream).collect(Collectors.toList());

		List<AlertKaoRoutes> alertKaoRoutesList = alertKaoMasterList.stream().map(alertKaoMaster -> {

			List<AlertKaoRoutes> alertKaoRoutes = alertKaoMaster.getScope().getRoutes().stream().map(route -> {
				route.setAlertID(alertKaoMaster.getAlertID());
				return route;
			}).collect(Collectors.toList());

			ETLHelper.copyProperties(routesCounter, alertKaoMaster, alertKaoRoutes);
			return alertKaoRoutes;
		}).flatMap(List::stream).collect(Collectors.toList());

		List<AlertKaoTrains> alertKaoTrainsList = alertKaoMasterList.stream().map(alertKaoMaster -> {

			List<AlertKaoTrains> alertKaoTrains = alertKaoMaster.getScope().getTrains().stream().map(train -> {
				train.setAlertID(alertKaoMaster.getAlertID());
				return train;
			}).collect(Collectors.toList());

			ETLHelper.copyProperties(trainsCounter, alertKaoMaster, alertKaoTrains);
			return alertKaoTrains;
		}).flatMap(List::stream).collect(Collectors.toList());

		List<AlertKaoLineSections> alertKaoLineSectionsList = alertKaoMasterList.stream().map(alertKaoMaster -> {

			List<AlertKaoLineSections> alertKaoLineSections = alertKaoMaster.getScope().getLineSections().stream().map(train -> {
				train.setAlertID(alertKaoMaster.getAlertID());
				return train;
			}).collect(Collectors.toList());

			ETLHelper.copyProperties(lineSectionsCounter, alertKaoMaster, alertKaoLineSections);
			return alertKaoLineSections;
		}).flatMap(List::stream).collect(Collectors.toList());
		
		try {
			// Step 3: Map model list to encapsulation file
			EncapsulationFile masterFile = ETLHelper.buildEncapsulationFile(extractResult, targetTable,
					alertKaoMasterList);
			EncapsulationFile networkFile = ETLHelper.buildEncapsulationFile(extractResult, networkTable,
					alertKaoNetworkList);
			EncapsulationFile detailStationsFile = ETLHelper.buildEncapsulationFile(extractResult, stationsTable,
					alertKaoStationsList);
			EncapsulationFile detailLinesFile = ETLHelper.buildEncapsulationFile(extractResult, linesTable,
					alertKaoLinesList);
			EncapsulationFile detailRoutesFile = ETLHelper.buildEncapsulationFile(extractResult, routesTable,
					alertKaoRoutesList);
			EncapsulationFile detailTrainFile = ETLHelper.buildEncapsulationFile(extractResult, trainsTable,
					alertKaoTrainsList);
			EncapsulationFile detailLineSectionsFile = ETLHelper.buildEncapsulationFile(extractResult, lineSectionsTable,
					alertKaoLineSectionsList);
			importFileMap.put(targetTable, masterFile);
			importFileMap.put(networkTable, networkFile);
			importFileMap.put(stationsTable, detailStationsFile);
			importFileMap.put(linesTable, detailLinesFile);
			importFileMap.put(routesTable, detailRoutesFile);
			importFileMap.put(trainsTable, detailTrainFile);
			importFileMap.put(lineSectionsTable, detailLineSectionsFile);
			log.info("Extract result transform spent {}ms", System.currentTimeMillis() - startTime);

			return TransformResult.builder().importFileMap(importFileMap).build();
		} catch (Exception e) {
			throw new ResourceFormatErrorException(e);
		}
	}
}
