package com.example.strategy.impl.etl;

import java.util.Date;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.example.exception.ResourceException;
import com.example.exception.ResourceFormatErrorException;
import com.example.exception.ResourceNotUpdateException;
import com.example.model.dto.etl.ExtractResult;
import com.example.model.dto.etl.ResourceInfo;
import com.example.utils.ClassUtils;
import com.example.utils.DateUtils;
import com.example.utils.ETLHelper;
import com.example.utils.JsonUtils;
import com.example.utils.StringTools;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class MongoOutputETLStrategy extends GeneralApiETLStrategy {

	private static final String RECORD_PROPERTY = "records";

	@Override
	public ExtractResult extract(ResourceInfo resourceInfo) throws ResourceException {
		try {
			// Step 1: Check resource available or not
			Date updateTime = new Date();
			String resource = resourceInfo.getResource();
			log.info("Resource: `{}`", resource);
			String resourceContent = JsonUtils.toJsonString(resource);
			// Step 2: Extract the array part of resource
			JsonNode tree = JsonUtils.getMapper().readTree(resourceContent);
			JsonNode recordsNode = tree.get(RECORD_PROPERTY);
			// Get the srcUpdateTime
			String similarDateTime = StringTools.findFirstMatchSequence(resourceContent, DateUtils.DATE_SIMILAR_REGEX);
			Date srcUpdateTime = ClassUtils.isValid(similarDateTime) ? DateUtils.parseStrToDate(similarDateTime)
					: updateTime;
			String recordsContent = recordsNode.toString();
			String resourceArrayPart = recordsContent.substring(recordsContent.indexOf('['),
					recordsContent.lastIndexOf(']') + 1);
			log.info("Resource fetch spent {}ms", System.currentTimeMillis() - updateTime.getTime());

			return ETLHelper.buildExtractResult(resourceInfo, resourceArrayPart, srcUpdateTime, updateTime);
		} catch (ResourceNotUpdateException | ResourceFormatErrorException e) {
			throw e;
		} catch (Exception e) {
			throw new ResourceException(e);
		}
	}

}
