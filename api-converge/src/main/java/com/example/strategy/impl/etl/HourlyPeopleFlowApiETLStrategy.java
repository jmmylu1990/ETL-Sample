package com.example.strategy.impl.etl;


import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.JsonNode;
import com.example.utils.ETLHelper;
import com.example.exception.ResourceException;
import com.example.exception.ResourceFormatErrorException;
import com.example.exception.ResourceNotUpdateException;
import com.example.model.dto.etl.ExtractResult;
import com.example.model.dto.etl.ResourceInfo;
import com.example.model.dto.source.iot.device.HinetHourlyPeopleFlow;
import com.example.utils.ClassUtils;
import com.example.utils.DateUtils;
import com.example.utils.JsonUtils;
import com.example.utils.StringTools;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("HourlyPeopleFlowApiETLStrategy")
public class HourlyPeopleFlowApiETLStrategy extends GeneralApiETLStrategy {
		
	@Override
	public ExtractResult extract(ResourceInfo resourceInfo) throws ResourceException {
		try {
			// Step 1: Check resource available or not
			Date updateTime = new Date();
			String resource = resourceInfo.getResource();
			String partOfResource = resource.substring(resource.indexOf("device/")+7, resource.length());
			String deviceName = partOfResource.substring(0,partOfResource.indexOf("/"));
			
			log.info("Resource: `{}`", resource);
			// Set request body to authenticate the resource
			Map<String, String> paramMap = new HashMap<>();
			paramMap.put("CK", "PKQDRTH4HIRRWP5AZH");
			
			// Step 2: Extract the array part of resource
			String jsonContent = JsonUtils.toJsonString(resource, paramMap);
			
			// Get Json tree to extract infomation we need
			JsonNode tree = JsonUtils.getMapper().readTree(jsonContent);
			JsonNode dataElem = tree.get("value");
			HinetHourlyPeopleFlow hinetHourlyPeopleFlow = new HinetHourlyPeopleFlow();
			hinetHourlyPeopleFlow.setId(tree.get("id").asText());
			hinetHourlyPeopleFlow.setDeviceID(tree.get("deviceId").asText());
			hinetHourlyPeopleFlow.setDataTime(DateUtils.parseStrToDate("yyyy-MM-dd'T'HH:mm:ss'Z'",tree.get("time").asText()));
			hinetHourlyPeopleFlow.setSouthPeopleFlowCounts(dataElem.get(0).asInt());
			hinetHourlyPeopleFlow.setSouthDataCounts(dataElem.get(1).asInt());
			hinetHourlyPeopleFlow.setNorthPeopleFlow_counts(dataElem.get(2).asInt());
			hinetHourlyPeopleFlow.setNorthDataCounts(dataElem.get(3).asInt());
			JsonNode tree2 = JsonUtils.getMapper().valueToTree(hinetHourlyPeopleFlow);
			String resourceArrayPart = tree2.toString();
			String similarDateTime = StringTools.findFirstMatchSequence(resourceArrayPart, DateUtils.DATE_SIMILAR_REGEX);
			Date srcUpdateTime = ClassUtils.isValid(similarDateTime) ? DateUtils.parseStrToDate(similarDateTime) : updateTime;
			log.info("Resource fetch spent {}ms", System.currentTimeMillis() - updateTime.getTime());

			return ETLHelper.buildExtractResult(resourceInfo, resourceArrayPart, srcUpdateTime, updateTime);
		} catch (ResourceNotUpdateException | ResourceFormatErrorException e) {
			throw e;
		} catch (Exception e) {
			throw new ResourceException(e);
		}
	}
	
	
}
