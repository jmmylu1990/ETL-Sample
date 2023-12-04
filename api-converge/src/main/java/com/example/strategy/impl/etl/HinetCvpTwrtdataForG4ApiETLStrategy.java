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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.JsonNode;
import com.example.utils.ETLHelper;
import com.example.utils.FileOperationUtils;
import com.example.exception.ImportException;
import com.example.exception.ResourceException;
import com.example.exception.ResourceFormatErrorException;
import com.example.exception.ResourceNotUpdateException;
import com.example.model.ProgramConstant;
import com.example.model.dto.etl.EncapsulationFile;
import com.example.model.dto.etl.ExtractResult;
import com.example.model.dto.etl.ImportResult;
import com.example.model.dto.etl.ResourceInfo;
import com.example.model.dto.etl.TransformResult;
import com.example.model.dto.source.hinet.HinetCvpRtData;
import com.example.model.enums.DbSourceEnum;
import com.example.strategy.FileImportStrategy;
import com.example.utils.ClassUtils;
import com.example.utils.DateUtils;
import com.example.utils.JsonUtils;
import com.example.utils.StringTools;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("HinetCvpTwrtdataForG4ApiETLStrategy")
public class HinetCvpTwrtdataForG4ApiETLStrategy extends GeneralApiETLStrategy {

	@Value("${TESTHinetLogin.url}")
	private String url;

	@Override
	public ExtractResult extract(ResourceInfo resourceInfo) throws ResourceException {
		try {
			// Step 1: Check resource available or not
			Date updateTime = new Date();
			String resource = resourceInfo.getResource();
			String[] items = resource.split(",");
			String split = items[1].substring(items[1].indexOf("=") + 1, items[1].length());
			String apiID = items[2].substring(items[2].indexOf("=") + 1, items[2].length());
			String id = items[3].substring(items[3].indexOf("=") + 1, items[3].length());
			String pass = items[4].substring(items[4].indexOf("=") + 1, items[4].length());

			log.info("Resource: `{}`", items[0]);
			// Set request body to authenticate the resource
			Map<String, String> paramMapforToken = new HashMap<>();
			paramMapforToken.put("id", id);
			paramMapforToken.put("pass", pass);

			String jsonContentOfToken = JsonUtils.postJsonString(url, paramMapforToken);
			log.info("id: `{}`", id);
			log.info("pass: `{}`", pass);
			log.info("jsonContentOfToken: `{}`", jsonContentOfToken);

			JsonNode tree = JsonUtils.getMapper().readTree(jsonContentOfToken);
			JsonNode tokenElem = tree.get("access_token");
			String response = null;
			List<HinetCvpRtData> hinetCvpRtDataList = new ArrayList<HinetCvpRtData>();
			List<HinetCvpRtData> itemList = new ArrayList<HinetCvpRtData>();
			List<HinetCvpRtData> hinetCvpTwrtdataList = null;
			List<List<HinetCvpRtData>> totalList = new ArrayList<List<HinetCvpRtData>>();
			if (tokenElem != null) {
				String token = tokenElem.asText();
				Map<String, String> paramMap = new HashMap<>();
				paramMap.put("token", token);
				paramMap.put("split", split);
				paramMap.put("api_id", apiID);

				log.info("token: `{}`", paramMap.get("token"));
				log.info("split: `{}`", paramMap.get("split"));
				log.info("api_id: `{}`", paramMap.get("api_id"));
				String resourceContent = JsonUtils.postJsonString(items[0], paramMap);
				JsonNode jsonNode = JsonUtils.getMapper().readTree(resourceContent);

				for (int i = 0; i < jsonNode.get("data").size(); i++) {
					JsonNode gridsNode = jsonNode.get("data").get(i).get("grids");
					for (JsonNode gird : gridsNode) {
						HinetCvpRtData hinetCvpRtData = new HinetCvpRtData();
						hinetCvpRtData.setApiID(apiID);
						hinetCvpRtData.setStatus(jsonNode.get("status").asText());
						hinetCvpRtData.setMsg(jsonNode.get("msg").asText());
						hinetCvpRtData.setName(jsonNode.get("data").get(i).get("name").asText());
						hinetCvpRtData.setGid(gird.get("gid").asText());
						hinetCvpRtData.setPopulation(gird.get("population").intValue());
						hinetCvpRtData.setDataTime(DateUtils.parseStrToDate(jsonNode.get("time").asText()));
						hinetCvpRtDataList.add(hinetCvpRtData);
					}
					response = String.format("status=%s,api_id=%s,msg=%s,data數量=%s ", jsonNode.get("status"),
							jsonNode.get("api_id"), jsonNode.get("msg"),
							jsonNode.get("data").get(i).get("grids").size());
					log.info("response: `{}`", response);
					String dataContent = JsonUtils.getMapper().writeValueAsString(hinetCvpRtDataList);
					hinetCvpTwrtdataList = JsonUtils.toBeanList(dataContent, HinetCvpRtData.class);
					itemList = hinetCvpTwrtdataList.stream().map(item -> {
						item.setApiID(jsonNode.get("api_id").asText());
						item.setStatus(jsonNode.get("status").asText());
						item.setMsg(jsonNode.get("msg").asText());
						item.setDataTime(DateUtils.parseStrToDate("yyyyMMddHHmmss", jsonNode.get("time").asText()));
						return item;
					}).collect(Collectors.toList());
					totalList.add(itemList);
				}
			}
			List<HinetCvpRtData> totalListflat = totalList.stream().flatMap(List::stream).collect(Collectors.toList());
			String jsonContent = JsonUtils.getMapper().writeValueAsString(totalListflat);
			String similarDateTime = StringTools.findFirstMatchSequence(jsonContent, DateUtils.DATE_SIMILAR_REGEX);
			Date srcUpdateTime = ClassUtils.isValid(similarDateTime) ? DateUtils.parseStrToDate(similarDateTime)
					: updateTime;
			log.info("Resource fetch spent {}ms", System.currentTimeMillis() - updateTime.getTime());

			return ETLHelper.buildExtractResult(resourceInfo, jsonContent, srcUpdateTime, updateTime);
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
		String historytable = String.format("%s_history", targetTable);
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
			importFileMap.put(historytable, encapsulationFile);
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
			if (!targetTable.contains("history"))
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
					if (targetTable.contains("history")) {
                
						importStrategy.loadData(importFile, targetTable, false);
					} else {
						 
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
					n.setImportCount((n.getImportCount() + o.getImportCount()));
					n.setException(Optional.ofNullable(n.getException()).orElse(o.getException()));
					return n;
				});
			}
		}));
		log.info("Transform result import spent {}ms", System.currentTimeMillis() - startTime);

		return resultMap;
	}
}
