package com.example.strategy.impl.etl;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.exception.ImportException;
import com.example.exception.ResourceException;
import com.example.exception.ResourceFormatErrorException;
import com.example.exception.ResourceNotUpdateException;
import com.example.factory.FileImportStrategyFactory;
import com.example.model.ProgramConstant;
import com.example.model.dto.etl.EncapsulationFile;
import com.example.model.dto.etl.ExtractResult;
import com.example.model.dto.etl.ImportResult;
import com.example.model.dto.etl.ResourceInfo;
import com.example.model.dto.etl.TransformResult;
import com.example.model.enums.DbSourceEnum;
import com.example.strategy.ETLStrategy;
import com.example.strategy.FileImportStrategy;
import com.example.utils.ClassUtils;
import com.example.utils.DateUtils;
import com.example.utils.ETLHelper;
import com.example.utils.FileOperationUtils;
import com.example.utils.JsonUtils;
import com.example.utils.StringTools;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class GeneralApiETLStrategy implements ETLStrategy {

	@Autowired
	protected FileImportStrategyFactory fileImportStrategyFactory;

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
	public TransformResult transform(ResourceInfo resourceInfo, ExtractResult extractResult)
			throws ResourceFormatErrorException {
		// Step 1: Serialize to related model
		long startTime = System.currentTimeMillis();
		Map<String, EncapsulationFile> importFileMap = new HashMap<>();
		Class<?> modelClass = resourceInfo.getModelClass();
		List<File> resources = extractResult.getResources();
		List<Serializable> resultList = resources.stream().map(resource -> {
			String content = FileOperationUtils.extractContent(resource);
			List<?> beanList = JsonUtils.toBeanList(content, modelClass);
			// Step 2: Transfer result and map to related model
			try {
				Field srcUpdateTimeField = modelClass.getDeclaredField(ProgramConstant.SRCUPDATETIME_FIELD_NAME);
				Field updateTimeField = modelClass.getDeclaredField(ProgramConstant.UPDATETIME_FIELD_NAME);
				srcUpdateTimeField.setAccessible(true);
				updateTimeField.setAccessible(true);
				return beanList.stream().map(bean -> {
					try {
						// Set srcUpdateTime
						srcUpdateTimeField.set(bean, extractResult.getSrcUpdateTime());
						// Set updateTime
						updateTimeField.set(bean, extractResult.getUpdateTime());
						// Self set value with annotation @AssignFrom
						ClassUtils.selfAssign(bean);
					} catch (ReflectiveOperationException e) {
						log.error(e.getMessage(), e);
					}

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

	@Override
	public Map<DbSourceEnum, ImportResult> load(TransformResult transformResult, boolean clearFirst,
			DbSourceEnum... dbSourceEnums) throws ImportException {
		long startTime = System.currentTimeMillis();
		Map<DbSourceEnum, ImportResult> resultMap = new EnumMap<>(DbSourceEnum.class);
		Map<String, EncapsulationFile> importFileMap = transformResult.getImportFileMap();
		Stream.of(dbSourceEnums).forEach(dbSource -> importFileMap.forEach((targetTable, encapsulationFile) -> {
			File importFile = encapsulationFile.getFile();
			ImportResult importResult = new ImportResult();
			if (clearFirst)
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
					importResult.setImportCount(importStrategy.loadData(importFile, targetTable, clearFirst));
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

	@Override
	public void successCallback() {
		
		// Default is empty, only subclass can implement
	}

}
