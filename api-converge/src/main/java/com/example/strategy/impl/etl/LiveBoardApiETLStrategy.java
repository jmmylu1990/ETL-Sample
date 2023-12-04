package com.example.strategy.impl.etl;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.example.exception.ResourceException;
import com.example.exception.ResourceFormatErrorException;
import com.example.exception.ResourceNotUpdateException;
import com.example.model.ProgramConstant;
import com.example.model.dto.etl.EncapsulationFile;
import com.example.model.dto.etl.ExtractResult;
import com.example.model.dto.etl.ResourceInfo;
import com.example.model.dto.etl.TransformResult;
import com.example.model.dto.source.mrt.MrtLiveBoard;
import com.example.utils.ClassUtils;
import com.example.utils.DateUtils;
import com.example.utils.ETLHelper;
import com.example.utils.FileOperationUtils;
import com.example.utils.HttpUtils;
import com.example.utils.JsonUtils;
import com.example.utils.StringTools;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("LiveBoardApiETLStrategy")
public class LiveBoardApiETLStrategy extends PtxApiETLStrategy {

	@Override
	public TransformResult transform(ResourceInfo resourceInfo, ExtractResult extractResult)
			throws ResourceFormatErrorException {
		// Step 1: Serialize to related model
		long startTime = System.currentTimeMillis();
		Map<String, EncapsulationFile> importFileMap = new HashMap<>();
		List<File> resources = extractResult.getResources();
		List<Serializable> resultList = resources.stream().map(resource -> {
			String content = FileOperationUtils.extractContent(resource);
			List<MrtLiveBoard> beanList = JsonUtils.toBeanList(content, MrtLiveBoard.class);
			// Step 2: Transfer result and map to related model
			try {
				
				return beanList.stream().map(bean -> {

					// Set InfoTume and InfoDate
					bean.setSrcUpdateTime(bean.getUpdateTime());
					bean.setUpdateTime(new Date());
					bean.setInfoTime(bean.getSrcUpdateTime());
					bean.setInfoDate(bean.getSrcUpdateTime());
					// Self set value with annotation @AssignFrom
					ClassUtils.selfAssign(bean);

					return (Serializable) bean;
				}).distinct().collect(Collectors.toList());
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}

			return new ArrayList<Serializable>();
		}).flatMap(List::stream).distinct().collect(Collectors.toList());

		try {
			// Step 3: Map model list to encapsulation file
			EncapsulationFile encapsulationFile = ETLHelper.buildEncapsulationFile(extractResult,
					resourceInfo.getTargetTable(), resultList);
			importFileMap.put(resourceInfo.getTargetTable(), encapsulationFile);
			log.info("Extract result transform spent {}ms", System.currentTimeMillis() - startTime);

			return TransformResult.builder().importFileMap(importFileMap).build();
		} catch (Exception e) {
			throw new ResourceFormatErrorException(e);
		}
	}

}