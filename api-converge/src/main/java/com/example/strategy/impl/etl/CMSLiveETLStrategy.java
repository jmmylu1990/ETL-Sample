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
import com.example.model.dto.source.traffic.live.CMSLive;
import com.example.model.dto.source.traffic.live.CMSLiveMessage;
import com.example.utils.ETLHelper;
import com.example.utils.FileOperationUtils;
import com.example.utils.JsonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("CMSLiveETLStrategy")
public class CMSLiveETLStrategy extends TrafficApiETLStrategy {

	@Override
	public TransformResult transform(ResourceInfo resourceInfo, ExtractResult extractResult) throws ResourceFormatErrorException {
		// Step 1: Prepare transform information
		long startTime = System.currentTimeMillis();
		//主表格
		String targetTable = resourceInfo.getTargetTable();
		//副表格
		String messageTable = String.format("%s_message", targetTable);
		Date srcUpdateTime = extractResult.getSrcUpdateTime();
		Date updateTime = extractResult.getUpdateTime();
		Map<String, EncapsulationFile> importFileMap = new HashMap<>();
		// Get resources and deserialize to model array 
		List<File> resources = extractResult.getResources();
		// Step 2: Transfer result and map to related model
		List<CMSLive> cmsLiveList = resources.stream()
			.map(FileOperationUtils::extractContent)
			.map(content -> JsonUtils.toBeanList(content, CMSLive.class))
			.flatMap(List::stream)
			.map(cmsLive -> {
				cmsLive.setSrcUpdateTime(srcUpdateTime);
				cmsLive.setUpdateTime(updateTime);
				cmsLive.setInfoTime(cmsLive.getDataCollectTime());
				cmsLive.setInfoDate(cmsLive.getInfoTime());
				
				return cmsLive;
			})
			.collect(Collectors.toList());

		// Step 3: Transfer other result and map to related model
		AtomicInteger messageCounter = new AtomicInteger(1); 
		List<CMSLiveMessage> liveMessageList = cmsLiveList.stream().map(cmsLive -> {
			List<CMSLiveMessage> messages = cmsLive.getMessages();
			ETLHelper.copyProperties(messageCounter, cmsLive, messages);
			return messages; 
		})
		.flatMap(List::stream)
		.collect(Collectors.toList());
		
		try {
			// Step 3: Map model list to encapsulation file
			EncapsulationFile masterFile = ETLHelper.buildEncapsulationFile(extractResult, targetTable, cmsLiveList);
			EncapsulationFile detailFile = ETLHelper.buildEncapsulationFile(extractResult, messageTable, liveMessageList);
			importFileMap.put(targetTable, masterFile);
			importFileMap.put(messageTable, detailFile);
			log.info("Extract result transform spent {}ms", System.currentTimeMillis() - startTime);
			
			return TransformResult.builder()
					.importFileMap(importFileMap)
					.build();
		} catch (Exception e) {
			throw new ResourceFormatErrorException(e);
		}
	}
	
}
