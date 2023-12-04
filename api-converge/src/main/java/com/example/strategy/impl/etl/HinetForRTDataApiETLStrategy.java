package com.example.strategy.impl.etl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.JsonNode;
import com.example.utils.ETLHelper;
import com.example.exception.ResourceException;
import com.example.exception.ResourceFormatErrorException;
import com.example.exception.ResourceNotUpdateException;
import com.example.model.dto.etl.ExtractResult;
import com.example.model.dto.etl.ResourceInfo;
import com.example.model.dto.source.hinet.HinetCvpRtData;
import com.example.utils.ClassUtils;
import com.example.utils.DateUtils;
import com.example.utils.JsonUtils;
import com.example.utils.StringTools;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("HinetForRTDataApiETLStrategy")
public class HinetForRTDataApiETLStrategy extends GeneralApiETLStrategy {

	@Value("${TESTHinetLogin.url}")
	private String url;
	@Value("${TESTHinetLogin.id}")
	private String id;
	@Value("${TESTHinetLogin.pass}")
	private String pass;
	@Value("${TESTHinetLogin.id2}")
	private String id2;
	@Value("${TESTHinetLogin.pass2}")
	private String pass2;

	@Override
	public ExtractResult extract(ResourceInfo resourceInfo) throws ResourceException {
		try {
			// Step 1: Check resource available or not
			Date updateTime = new Date();
			String resource = resourceInfo.getResource();

			log.info("Resource: `{}`", resource);
			// Set request body to authenticate the resource
			Map<String, String> paramMapforToken = new HashMap<>();
			paramMapforToken.put("id", id);
			paramMapforToken.put("pass", pass);

			Map<String, String> paramMapforToken2 = new HashMap<>();
			paramMapforToken2.put("id", id2);
			paramMapforToken2.put("pass", pass2);

			// Step 2: Extract the array part of resource
			String jsonContentOfToken = JsonUtils.postJsonString(url, paramMapforToken);
			String jsonContentOfToken2 = JsonUtils.postJsonString(url, paramMapforToken2);
			// Get Json tree to extract infomation we need
			JsonNode tree = JsonUtils.getMapper().readTree(jsonContentOfToken);
			JsonNode secondTree = JsonUtils.getMapper().readTree(jsonContentOfToken2);
			JsonNode tokenElem = tree.get("access_token");
			JsonNode secondTokenElem = secondTree.get("access_token");
			String token = tokenElem.asText();
			String token2 = secondTokenElem.asText();
			Map<String, String> paramMap = new HashMap<>();
			paramMap.put("token", token);
			paramMap.put("api_id", "33");
			Map<String, String> paramMap2 = new HashMap<>();
			paramMap2.put("token", token2);
			paramMap2.put("api_id", "33");
			String resourceContent = JsonUtils.postJsonString(resource, paramMap);
			String resourceContent2 = JsonUtils.postJsonString(resource, paramMap2);
			JsonNode jsonNode = JsonUtils.getMapper().readTree(resourceContent);
			JsonNode jsonNode2 = JsonUtils.getMapper().readTree(resourceContent2);
			String gridsContent = jsonNode.get("data").get(0).get("grids").toString();
			String gridsContent2 = jsonNode2.get("data").get(0).get("grids").toString();
			String gridsContent3 = jsonNode2.get("data").get(1).get("grids").toString();
			List<HinetCvpRtData> hinetCvpRtDataList = JsonUtils.toBeanList(gridsContent, HinetCvpRtData.class);
			List<HinetCvpRtData> hinetCvpRtDataList2 = JsonUtils.toBeanList(gridsContent2, HinetCvpRtData.class);
			List<HinetCvpRtData> hinetCvpRtDataList3 = JsonUtils.toBeanList(gridsContent3, HinetCvpRtData.class);
			List<HinetCvpRtData> itemList = hinetCvpRtDataList.stream().map(item -> {
				item.setApiID(jsonNode.get("api_id").asText());
				item.setStatus(jsonNode.get("status").asText());
				item.setMsg(jsonNode.get("msg").asText());
				item.setName(jsonNode.get("data").get(0).get("name").asText());
				item.setDataTime(DateUtils.parseStrToDate("yyyyMMddHHmmss", jsonNode.get("time").asText()));
				return item;
			}).collect(Collectors.toList());

			List<HinetCvpRtData> itemList2 = hinetCvpRtDataList2.stream().map(item -> {
				item.setApiID(jsonNode2.get("api_id").asText());
				item.setStatus(jsonNode2.get("status").asText());
				item.setMsg(jsonNode2.get("msg").asText());
				item.setName(jsonNode2.get("data").get(0).get("name").asText());
				item.setDataTime(DateUtils.parseStrToDate("yyyyMMddHHmmss", jsonNode2.get("time").asText()));
				return item;
			}).collect(Collectors.toList());

			List<HinetCvpRtData> itemList3 = hinetCvpRtDataList3.stream().map(item -> {
				item.setApiID(jsonNode2.get("api_id").asText());
				item.setStatus(jsonNode2.get("status").asText());
				item.setMsg(jsonNode2.get("msg").asText());
				item.setName(jsonNode2.get("data").get(1).get("name").asText());
				item.setDataTime(DateUtils.parseStrToDate("yyyyMMddHHmmss", jsonNode2.get("time").asText()));
				return item;
			}).collect(Collectors.toList());

			List<HinetCvpRtData> listByStream = Stream.of(itemList, itemList2, itemList3).flatMap(Collection::stream)
					.collect(Collectors.toList());

			List<HinetCvpRtData> newList = listByStream.stream()
					.collect(Collectors.collectingAndThen(
							Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(HinetCvpRtData::getGid))),
							ArrayList::new));

			String jsonContent = JsonUtils.getMapper().writeValueAsString(newList);
			// String jsonContent = JsonUtils.getMapper().writeValueAsString(listByStream);
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

}
