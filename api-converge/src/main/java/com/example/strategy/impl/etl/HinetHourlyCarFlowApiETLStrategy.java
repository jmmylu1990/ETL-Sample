package com.example.strategy.impl.etl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.JsonNode;
import com.example.utils.ETLHelper;
import com.example.exception.ResourceException;
import com.example.exception.ResourceFormatErrorException;
import com.example.exception.ResourceNotUpdateException;
import com.example.model.dto.etl.ExtractResult;
import com.example.model.dto.etl.ResourceInfo;
import com.example.model.dto.source.iot.device.CamevtParkingRemaining;
import com.example.model.dto.source.iot.device.HinetHourlyCarFlow;
import com.example.utils.ClassUtils;
import com.example.utils.DateUtils;
import com.example.utils.JsonUtils;
import com.example.utils.StringTools;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("HinetHourlyCarFlowApiETLStrategy")
public class HinetHourlyCarFlowApiETLStrategy extends GeneralApiETLStrategy {

	@Override
	public ExtractResult extract(ResourceInfo resourceInfo) throws ResourceException {
		try {
			// Step 1: Check resource available or not
			Date updateTime = new Date();
			String resources = resourceInfo.getResource();
			List<String> resourceList = Arrays.asList(resources.split(","));

			List<HinetHourlyCarFlow> hinetHourlyCarFlows = new ArrayList<HinetHourlyCarFlow>();

			for (String resource : resourceList) {

				String partOfResource = resource.substring(resource.indexOf("device/") + 7, resource.length());
				String deviceName = partOfResource.substring(0, partOfResource.indexOf("/"));
				log.info("Resource: `{}`", resource);
				// Set request body to authenticate the resource
				Map<String, String> paramMap = new HashMap<>();
				paramMap.put("CK", "PKQDRTH4HIRRWP5AZH");

				// Step 2: Extract the array part of resource
				String jsonContent = JsonUtils.toJsonString(resource, paramMap);

				// Get Json tree to extract infomation we need
				JsonNode tree = JsonUtils.getMapper().readTree(jsonContent);
				JsonNode dataElem = tree.get("value");
				HinetHourlyCarFlow hinetHourlyCarFlow = new HinetHourlyCarFlow();
				hinetHourlyCarFlow.setId(tree.get("id").asText());
				hinetHourlyCarFlow.setDeviceID(tree.get("deviceId").asText());
				hinetHourlyCarFlow
						.setDataTime(DateUtils.parseStrToDate("yyyy-MM-dd'T'HH:mm:ss'Z'", tree.get("time").asText()));
				hinetHourlyCarFlow.setMotoStraightCounts(dataElem.get(0).asInt());
				hinetHourlyCarFlow.setMotoLeftCounts(dataElem.get(1).asInt());
				hinetHourlyCarFlow.setMotoRightCounts(dataElem.get(2).asInt());
				hinetHourlyCarFlow.setCarStraightCounts(dataElem.get(3).asInt());
				hinetHourlyCarFlow.setCarLeftCounts(dataElem.get(4).asInt());
				hinetHourlyCarFlow.setCarRightCounts(dataElem.get(5).asInt());
				hinetHourlyCarFlow.setTruckStraightCounts(dataElem.get(6).asInt());
				hinetHourlyCarFlow.setTruckLeftCounts(dataElem.get(7).asInt());
				hinetHourlyCarFlow.setTruckRightCounts(dataElem.get(8).asInt());
				hinetHourlyCarFlow.setTotalCounts(dataElem.get(9).asInt());

				hinetHourlyCarFlows.add(hinetHourlyCarFlow);

			}

			JsonNode tree2 = JsonUtils.getMapper().valueToTree(hinetHourlyCarFlows);
			
			String resourceArrayPart = tree2.toString();
			String similarDateTime = StringTools.findFirstMatchSequence(resourceArrayPart,
					DateUtils.DATE_SIMILAR_REGEX);
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
