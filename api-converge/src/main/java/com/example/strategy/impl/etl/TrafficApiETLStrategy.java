package com.example.strategy.impl.etl;


import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.stream.StreamSupport;

import com.example.service.interfaces.TdxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.example.exception.ResourceException;
import com.example.exception.ResourceFormatErrorException;
import com.example.exception.ResourceNotUpdateException;
import com.example.model.dto.etl.ExtractResult;
import com.example.model.dto.etl.ResourceInfo;
import com.example.utils.DateUtils;
import com.example.utils.ETLHelper;
import com.example.utils.HttpUtils;
import com.example.utils.JsonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TrafficApiETLStrategy extends GeneralApiETLStrategy  {

	protected static final String UPDATE_INTERVAL_PROPERTY = "UpdateInterval";
	protected static final String AUTHORITY_CODE_PROPERTY = "AuthorityCode";
	protected static final String SRCUPDATE_TIME_PROPERTY = "SrcUpdateTime";
	protected static final String UPDATE_TIME_PROPERTY = "UpdateTime";
	
	@Autowired
	TdxService tdxService;
	@Override
	public ExtractResult extract(ResourceInfo resourceInfo) throws ResourceException {
		String token = tdxService.getToken();
		Map<String, String> headers = HttpUtils.customHeaderFoTDX(token);
		try {
			// Step 1: Check resource available or not
			Date updateTime = new Date();
			String resource = resourceInfo.getResource();
			log.info("Resource: `{}`", resource);
			String resourceContent = JsonUtils.toJsonString(resource, headers);
			// Step 2: Extract the array part of resource
			JsonNode tree = JsonUtils.getMapper().readTree(resourceContent);
			// Get json array part
			JsonNode arrayNode = StreamSupport.stream(tree.spliterator(), false)
				.filter(JsonNode::isArray)
				.findAny()
				.orElseThrow(() -> new ResourceFormatErrorException("The array content not existed!"));
			// Get authorityCode and set in every item of array if the property exists
			JsonNode updateIntervalNode = tree.get(UPDATE_INTERVAL_PROPERTY);
			JsonNode srcUpdateTimeNode = tree.get(SRCUPDATE_TIME_PROPERTY);
			JsonNode authorityCodeNode = tree.get(AUTHORITY_CODE_PROPERTY);
			Date srcUpdateTime = Objects.nonNull(srcUpdateTimeNode) ? DateUtils.parseStrToDate(srcUpdateTimeNode.asText()) : updateTime;
			StreamSupport.stream(arrayNode.spliterator(), true)
				.map(ObjectNode.class::cast)
				.forEach(node -> {
					node.set(UPDATE_INTERVAL_PROPERTY, updateIntervalNode);
					node.set(SRCUPDATE_TIME_PROPERTY, srcUpdateTimeNode);
					node.set(AUTHORITY_CODE_PROPERTY, authorityCodeNode);
				});
			
			String resourceArrayPart = arrayNode.toString();
			log.info("Resource fetch spent {}ms", System.currentTimeMillis() - updateTime.getTime());
			
			return ETLHelper.buildExtractResult(resourceInfo, resourceArrayPart, srcUpdateTime, updateTime);
		} catch (ResourceNotUpdateException | ResourceFormatErrorException e) {
			throw e;
		} catch (Exception e) {
			throw new ResourceException(e);
		}
	}
	
}
