package com.example.strategy.impl.etl;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.example.exception.ImportException;
import com.example.exception.ResourceException;
import com.example.exception.ResourceFormatErrorException;
import com.example.exception.ResourceNotUpdateException;
import com.example.model.dto.etl.EncapsulationFile;
import com.example.model.dto.etl.ExtractResult;
import com.example.model.dto.etl.ImportResult;
import com.example.model.dto.etl.ResourceInfo;
import com.example.model.dto.etl.TransformResult;
import com.example.model.dto.source.traffic.device.VDOneLandMin;
import com.example.model.dto.source.traffic.live.VDLive;
import com.example.model.dto.source.traffic.live.VDLiveLane;
import com.example.model.dto.source.traffic.live.VDLiveLinkFlow;
import com.example.model.dto.source.traffic.live.VDLiveVehicle;
import com.example.model.enums.DbSourceEnum;
import com.example.strategy.FileImportStrategy;
import com.example.utils.DateUtils;
import com.example.utils.ETLHelper;
import com.example.utils.FileOperationUtils;
import com.example.utils.HttpUtils;
import com.example.utils.JsonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("VDLiveETLStrategyWithOneLandMin")
public class VDLiveETLStrategyWithOneLandMin extends TrafficApiETLStrategy {

	@Value("${root.path}")
	private String rootPath;

	@Override
	public TransformResult transform(ResourceInfo resourceInfo, ExtractResult extractResult)
			throws ResourceFormatErrorException {
		// Step 1: Prepare transform information
		long startTime = System.currentTimeMillis();
		String targetTable = resourceInfo.getTargetTable();
		String linkflowTable = String.format("%s_linkflow", targetTable);
		String vehicleTable = String.format("%s_vehicle", targetTable);
		String onelandMinTable = String.format("%s_vd_one_land_min", targetTable.substring(0, targetTable.indexOf("_")));
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

			List<VDOneLandMin> vdOneLandMinListForNFB = vdList.stream().map(vdLive -> {

				return vdLive.getLinkFlows().stream().map(linkFlow -> {

					return linkFlow.getLanes().stream().map(lane -> {
						VDOneLandMin vdOneLandMin = new VDOneLandMin();

						vdOneLandMin.setVdID(vdLive.getVdID());
						vdOneLandMin.setStatus(String.valueOf(vdLive.getStatus()));
						vdOneLandMin.setLinkID(linkFlow.getLinkID());
						vdOneLandMin.setVsrID(lane.getLaneID());
						vdOneLandMin.setSpeed(lane.getSpeed());

						lane.getVehicles().forEach(vehicle -> {
							String vehicleType = vehicle.getVehicleType();
							int volume = vehicle.getVolume() == null ? 0 : vehicle.getVolume();
							if ("S".equals(vehicleType)) {
								vdOneLandMin.setSVolume(volume);
							} else if ("M".equals(vehicleType)) {
								vdOneLandMin.setTVolume(volume);
							} else if ("T".equals(vehicleType)) {
								vdOneLandMin.setTVolume(volume);
							} else if (("L".equals(vehicleType))) {
								vdOneLandMin.setLVolume(volume);
							}

						});

						vdOneLandMin.setOcc(lane.getOccupancy());
						vdOneLandMin.setDataCollectTime(vdLive.getDataCollectTime());
						vdOneLandMin.setSrcUpdateTime(srcUpdateTime);
						vdOneLandMin.setUpdateTime(updateTime);
						vdOneLandMin.setInfoTime(srcUpdateTime);
						vdOneLandMin.setInfoDate(srcUpdateTime);
						return vdOneLandMin;

					}).collect(Collectors.toList());

				}).flatMap(List::stream).collect(Collectors.toList());

			}).flatMap(List::stream).collect(Collectors.toList());
         
			JsonNode treeForNFB = JsonUtils.getMapper().valueToTree(vdOneLandMinListForNFB);

			String resourceArrayPartForNFB = JsonUtils.getMapper().writeValueAsString(treeForNFB);

			ResourceInfo resourceInfo2 = ResourceInfo.builder().targetTable(onelandMinTable).rootPath(rootPath).relativePath(resourceInfo.getRelativePath()).build();
			ExtractResult extractResult2 = ETLHelper.buildExtractResult(resourceInfo2, resourceArrayPartForNFB, srcUpdateTime, updateTime);
			EncapsulationFile oneLandMinFile = ETLHelper.buildEncapsulationFile(extractResult2, onelandMinTable, vdOneLandMinListForNFB);
			importFileMap.put(onelandMinTable, oneLandMinFile);
			return TransformResult.builder().importFileMap(importFileMap).build();
		} catch (Exception e) {
			throw new ResourceFormatErrorException(e);
		}
	}
	
	@Override
	public Map<DbSourceEnum, ImportResult> load(TransformResult transformResult, boolean clearFirst,
			DbSourceEnum... dbSourceEnums) throws ImportException {
		long startTime = System.currentTimeMillis();
		Map<DbSourceEnum, ImportResult> resultMap = new EnumMap<>(DbSourceEnum.class);
		Map<String, EncapsulationFile> importFileMap = transformResult.getImportFileMap();
		Stream.of(dbSourceEnums).forEach(dbSource -> importFileMap.forEach((targetTable, encapsulationFile) -> {
			File importFile = encapsulationFile.getFile();
			ImportResult importResult = new ImportResult();
			if (clearFirst && !targetTable.contains("vd_one_land_min"))
				log.warn("Table `{}` will be truncated!", targetTable);
			try {
				switch (dbSource) {
				case IMPALA:
					break;
				case JSON:
					break;
				case XML:
					break;

				// If dbSource is mysql/mssql/oracle then get instance from
				// fileImportStrategyFactory
				default:
					log.info("DbSource: {} / File: {}", dbSource, importFile);
					FileImportStrategy importStrategy = fileImportStrategyFactory.getObject(dbSource);
					if(targetTable.contains("vd_one_land_min")) {
					importResult.setImportCount(importStrategy.loadData(importFile, targetTable, false));
					}else {
						importResult.setImportCount(importStrategy.loadData(importFile, targetTable, clearFirst));
					}
					break;
				}
			} catch (ImportException e) {
				log.error(e.getMessage(), e);
				importResult.setException(e);
			} finally {
				// Merge the import result if it exists
				resultMap.merge(dbSource, importResult, (o, n) -> {
					n.setImportCount(n.getImportCount() + o.getImportCount());
					n.setException(Optional.ofNullable(n.getException()).orElse(o.getException()));
					return n;
				});
			}
		}));
		log.info("Transform result import spent {}ms", System.currentTimeMillis() - startTime);

		return resultMap;
	}

}
