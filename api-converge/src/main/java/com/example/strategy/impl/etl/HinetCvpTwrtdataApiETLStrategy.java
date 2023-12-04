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
import com.fasterxml.jackson.databind.JsonNode;
import com.example.utils.ETLHelper;
import com.example.utils.FileOperationUtils;
import com.example.exception.ResourceException;
import com.example.exception.ResourceFormatErrorException;
import com.example.exception.ResourceNotUpdateException;
import com.example.model.ProgramConstant;
import com.example.model.dto.etl.EncapsulationFile;
import com.example.model.dto.etl.ExtractResult;
import com.example.model.dto.etl.ResourceInfo;
import com.example.model.dto.etl.TransformResult;
import com.example.model.dto.source.hinet.HinetCvpTwrtdata;
import com.example.utils.ClassUtils;
import com.example.utils.DateUtils;
import com.example.utils.JsonUtils;
import com.example.utils.StringTools;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("HinetCvpTwrtdataApiETLStrategy")
public class HinetCvpTwrtdataApiETLStrategy extends GeneralApiETLStrategy {

	@Value("${TESTHinetLogin.url}")
	private String url;
	@Value("${TESTHinetLogin.id}")
	private String id;
	@Value("${TESTHinetLogin.pass}")
	private String pass;

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

			// Step 2: Extract the array part of resource
			String jsonContentOfToken = JsonUtils.postJsonString(url, paramMapforToken);

			// Get Json tree to extract infomation we need
			JsonNode tree = JsonUtils.getMapper().readTree(jsonContentOfToken);
			JsonNode tokenElem = tree.get("access_token");
			String token = tokenElem.asText();
			Map<String, String> paramMap = new HashMap<>();
			paramMap.put("token", token);
			paramMap.put("split", "1");
			paramMap.put("api_id", "32");

			String resourceContent = JsonUtils.postJsonString(resource, paramMap);
			JsonNode jsonNode = JsonUtils.getMapper().readTree(resourceContent);
			String dataContent = jsonNode.get("data").get(0).toString();
			List<HinetCvpTwrtdata> hinetCvpTwrtdataList = JsonUtils.toBeanList(dataContent, HinetCvpTwrtdata.class);

			List<HinetCvpTwrtdata> itemList = hinetCvpTwrtdataList.stream().map(item -> {
				item.setApiID(jsonNode.get("api_id").asText());
				item.setStatus(jsonNode.get("status").asText());
				item.setMsg(jsonNode.get("msg").asText());
				item.setDataTime(DateUtils.parseStrToDate("yyyyMMddHHmmss", jsonNode.get("time").asText()));
				return item;
			}).collect(Collectors.toList());

			String jsonContent = JsonUtils.getMapper().writeValueAsString(itemList);
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
