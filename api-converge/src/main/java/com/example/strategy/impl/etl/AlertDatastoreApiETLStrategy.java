package com.example.strategy.impl.etl;

import com.fasterxml.jackson.databind.JsonNode;
import com.example.exception.ResourceException;
import com.example.exception.ResourceFormatErrorException;
import com.example.exception.ResourceNotUpdateException;
import com.example.factory.FileImportStrategyFactory;
import com.example.model.dto.etl.ExtractResult;
import com.example.model.dto.etl.ResourceInfo;
import com.example.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component("AlertDatastoreApiETLStrategy")
public class AlertDatastoreApiETLStrategy extends GeneralApiETLStrategy {


	@Override
	public ExtractResult extract(ResourceInfo resourceInfo) throws ResourceException {
		try {
			// Step 1: Check resource available or not
			Date updateTime = new Date();
			String resource = resourceInfo.getResource();
			log.info("Resource: `{}`", resource);
			
			// Step 2: Extract the array part of resource
			JsonNode jsonNode = JsonUtils.getMapper().readTree(JsonUtils.toJsonString(resource));
			String recordsContent = jsonNode.toString().replace("\\n","");
			String resourceArrayPart = recordsContent.substring(recordsContent.indexOf('['), recordsContent.lastIndexOf(']') + 1);
		    Date srcUpdateTime = updateTime;
			log.info("Resource fetch spent {}ms", System.currentTimeMillis() - updateTime.getTime());
			
			return ETLHelper.buildExtractResult(resourceInfo, resourceArrayPart, srcUpdateTime, updateTime);
		} catch(ResourceNotUpdateException | ResourceFormatErrorException e) {
			throw e;
		} catch (Exception e) {
			throw new ResourceException(e);
		}
	}

	

}
