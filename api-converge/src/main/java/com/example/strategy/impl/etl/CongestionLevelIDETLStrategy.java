package com.example.strategy.impl.etl;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.exception.ResourceFormatErrorException;
import com.example.model.dto.etl.EncapsulationFile;
import com.example.model.dto.etl.ExtractResult;
import com.example.model.dto.etl.ResourceInfo;
import com.example.model.dto.etl.TransformResult;
import com.example.model.dto.source.traffic.device.CongestionLevel;
import com.example.model.dto.source.traffic.device.CongestionLevelItem;
import com.example.utils.ETLHelper;
import com.example.utils.FileOperationUtils;
import com.example.utils.JsonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("CongestionLevelIDETLStrategy")
public class CongestionLevelIDETLStrategy extends TrafficApiETLStrategy {

	@Override
	public TransformResult transform(ResourceInfo resourceInfo, ExtractResult extractResult) throws ResourceFormatErrorException {
		// Step 1: Prepare transform information
		long startTime = System.currentTimeMillis();
		String targetTable = resourceInfo.getTargetTable();
		String detectionlinkTable = String.format("%s_level", targetTable);
		Date srcUpdateTime = extractResult.getSrcUpdateTime();
		Date updateTime = extractResult.getUpdateTime();
		Map<String, EncapsulationFile> importFileMap = new HashMap<>();
		// Get resources and deserialize to model array 
		List<File> resources = extractResult.getResources();

		// Step 2: Transfer result and map to related model
		List<CongestionLevel> congestionLevelList = resources.stream()
			.map(FileOperationUtils::extractContent)
			.map(content -> JsonUtils.toBeanList(content, CongestionLevel.class))
			.flatMap(List::stream)
			.map(congestionLevel -> {
				congestionLevel.setSrcUpdateTime(srcUpdateTime);
				congestionLevel.setUpdateTime(updateTime);
				congestionLevel.setInfoTime(srcUpdateTime);
				congestionLevel.setInfoDate(congestionLevel.getInfoTime());
				return congestionLevel;
			})
			.collect(Collectors.toList());

		// Step 3: Transfer other result and map to related model
		AtomicInteger linkCounter = new AtomicInteger(1); 
		List<CongestionLevelItem> congestionLevelItemList = congestionLevelList.stream().map(congestionLevel -> {
			List<CongestionLevelItem> congestionLevelItems = congestionLevel.getLevels();
			ETLHelper.copyProperties(linkCounter, congestionLevel, congestionLevelItems);
			return congestionLevelItems; 
		})
		.flatMap(List::stream)
		.collect(Collectors.toList());
		
		try {
			// Step 3: Map model list to encapsulation file
			EncapsulationFile masterFile = ETLHelper.buildEncapsulationFile(extractResult, targetTable, congestionLevelList);
			EncapsulationFile detailFile = ETLHelper.buildEncapsulationFile(extractResult, detectionlinkTable, congestionLevelItemList);
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
