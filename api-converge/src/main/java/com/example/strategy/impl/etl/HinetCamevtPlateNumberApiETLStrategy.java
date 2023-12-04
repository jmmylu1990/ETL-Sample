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
import com.example.model.dto.source.iot.device.HinetCamevtPlateNumber;
import com.example.model.dto.source.iot.device.HinetHourlyCarFlow;
import com.example.utils.ClassUtils;
import com.example.utils.DateUtils;
import com.example.utils.JsonUtils;
import com.example.utils.StringTools;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("HinetCamevtPlateNumberApiETLStrategy")
public class HinetCamevtPlateNumberApiETLStrategy extends GeneralApiETLStrategy {

	@Override
	public ExtractResult extract(ResourceInfo resourceInfo) throws ResourceException {
		try {
			// Step 1: Check resource available or not
			Date updateTime = new Date();
			String resources = resourceInfo.getResource();
			List<String> resourceList = Arrays.asList(resources.split(","));

			List<HinetCamevtPlateNumber> hinetCamevtPlateNumbers = new ArrayList<HinetCamevtPlateNumber>();

			for (String resource : resourceList) {
			
				log.info("Resource: `{}`", resource);
				// Set request body to authenticate the resource
				Map<String, String> paramMap = new HashMap<>();
				paramMap.put("CK", "PKQDRTH4HIRRWP5AZH");

				// Step 2: Extract the array part of resource
				String jsonContent = JsonUtils.toJsonString(resource, paramMap);

				// Get Json tree to extract infomation we need
				JsonNode tree = JsonUtils.getMapper().readTree(jsonContent);
				JsonNode dataElem = tree.get("value");
				HinetCamevtPlateNumber hinetCamevtPlateNumber = new HinetCamevtPlateNumber();
				hinetCamevtPlateNumber.setId(tree.get("id").asText());
				hinetCamevtPlateNumber.setDeviceID(tree.get("deviceId").asText());
				
				hinetCamevtPlateNumber
						.setDataTime(DateUtils.parseStrToDate("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", tree.get("time").asText()));
				hinetCamevtPlateNumber.setSnapshotURL(dataElem.get(0).asText());
				hinetCamevtPlateNumber.setVersion(dataElem.get(1).asInt());
				hinetCamevtPlateNumber.setCameraID(dataElem.get(2).asText());
				hinetCamevtPlateNumber.setCameraName(dataElem.get(3).asText());
				hinetCamevtPlateNumber.setEventTime(new Date(Long.parseLong(dataElem.get(4).asText())));
				hinetCamevtPlateNumber.setPlateText(dataElem.get(5).asText());
				hinetCamevtPlateNumber.setVehicleType(dataElem.get(6).asText());
				hinetCamevtPlateNumber.setVehicleColor(dataElem.get(7).asText());

				hinetCamevtPlateNumbers.add(hinetCamevtPlateNumber);

			}

			JsonNode tree2 = JsonUtils.getMapper().valueToTree(hinetCamevtPlateNumbers);
		
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
