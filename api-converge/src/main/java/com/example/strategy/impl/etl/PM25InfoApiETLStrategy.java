package com.example.strategy.impl.etl;

import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.example.exception.ResourceException;
import com.example.exception.ResourceFormatErrorException;
import com.example.exception.ResourceNotUpdateException;
import com.example.factory.FileImportStrategyFactory;
import com.example.model.dto.etl.ExtractResult;
import com.example.model.dto.etl.ResourceInfo;
import com.example.utils.ClassUtils;
import com.example.utils.DateUtils;
import com.example.utils.ETLHelper;
import com.example.utils.JsonUtils;
import com.example.utils.StringTools;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("PM25InfoApiETLStrategy")
public class PM25InfoApiETLStrategy extends GeneralApiETLStrategy {
	
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
			JsonNode jsonNode = JsonUtils.getMapper().readTree(JsonUtils.toJsonString(resource));
			JsonNode recordsNode = jsonNode.get("records");
			String recordsContent = recordsNode.toString();
			String resourceArrayPart = recordsContent.substring(recordsContent.indexOf('['), recordsContent.lastIndexOf(']') + 1);
			String similarDateTime = StringTools.findFirstMatchSequence(recordsContent, DateUtils.DATE_SIMILAR_REGEX);
			Date srcUpdateTime = ClassUtils.isValid(similarDateTime) ? DateUtils.parseStrToDate(similarDateTime) : updateTime;
			log.info("Resource fetch spent {}ms", System.currentTimeMillis() - updateTime.getTime());
			
			return ETLHelper.buildExtractResult(resourceInfo, resourceArrayPart, srcUpdateTime, updateTime);
		} catch(ResourceNotUpdateException | ResourceFormatErrorException e) {
			throw e;
		} catch (Exception e) {
			throw new ResourceException(e);
		}
	}

	

}
