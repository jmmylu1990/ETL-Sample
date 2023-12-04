package com.example.strategy.impl.etl;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.JsonNode;
import com.example.exception.ResourceException;
import com.example.exception.ResourceFormatErrorException;
import com.example.exception.ResourceNotUpdateException;
import com.example.model.dto.etl.ExtractResult;
import com.example.model.dto.etl.ResourceInfo;
import com.example.utils.DateUtils;
import com.example.utils.ETLHelper;
import com.example.utils.JsonUtils;
import com.example.utils.XmlUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("ParkingXmlApiETLStrategy")
public class ParkingXmlApiETLStrategy extends GeneralApiETLStrategy {

	@Override
	public ExtractResult extract(ResourceInfo resourceInfo) throws ResourceException {
		try {
			// Step 1: Check resource available or not
			Date updateTime = new Date();
			String resource = resourceInfo.getResource();
			log.info("Resource: `{}`", resource);

			// Step 2: Extract the array part of resource
			JsonNode tree = XmlUtils.getMapper().readTree(JsonUtils.toJsonString(resource));
			
			Iterator<String> keys = tree.fieldNames();
			
			List<String> keyList = new ArrayList<String>();
			while(keys.hasNext()) {
				String key = keys.next();
				keyList.add(key);
			}
			String lastfieldName = keyList.get(keyList.size()-1);
			String resourceContent = tree.get(lastfieldName).toString();
			String resourceArrayPart = resourceContent.substring(resourceContent.indexOf('['),
					resourceContent.lastIndexOf(']') + 1);
			Date srcUpdateTime = DateUtils.parseStrToDate("yyyy-MM-dd'T'HH:mm:ss", tree.get("UpdateTime").asText());
			log.info("Resource fetch spent {}ms", System.currentTimeMillis() - updateTime.getTime());

			return ETLHelper.buildExtractResult(resourceInfo, resourceArrayPart, srcUpdateTime, updateTime);
		} catch (ResourceNotUpdateException | ResourceFormatErrorException e) {
			throw e;
		} catch (Exception e) {
			throw new ResourceException(e);
		}
	}
}
