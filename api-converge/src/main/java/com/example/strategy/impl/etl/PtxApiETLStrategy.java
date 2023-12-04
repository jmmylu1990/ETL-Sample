package com.example.strategy.impl.etl;

import com.example.exception.ResourceException;
import com.example.exception.ResourceFormatErrorException;
import com.example.exception.ResourceNotUpdateException;
import com.example.model.dto.etl.ExtractResult;
import com.example.model.dto.etl.ResourceInfo;
import com.example.service.interfaces.TdxService;
import com.example.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

@Slf4j
@Component
public class PtxApiETLStrategy extends GeneralApiETLStrategy  {
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
			String resourceArrayPart = resourceContent.substring(resourceContent.indexOf('['), resourceContent.lastIndexOf(']') + 1);
			String similarDateTime = StringTools.findFirstMatchSequence(resourceContent, DateUtils.DATE_SIMILAR_REGEX);
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
