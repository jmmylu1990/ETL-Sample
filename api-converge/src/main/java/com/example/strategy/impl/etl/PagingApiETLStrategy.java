package com.example.strategy.impl.etl;

import java.util.Date;

import org.springframework.stereotype.Component;

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
public class PagingApiETLStrategy extends GeneralApiETLStrategy{
	
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

}
