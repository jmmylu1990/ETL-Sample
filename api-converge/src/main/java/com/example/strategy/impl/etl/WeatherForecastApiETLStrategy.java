package com.example.strategy.impl.etl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.stream.StreamSupport;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
@Component("WeatherForecastApiETLStrategy")
public class WeatherForecastApiETLStrategy extends GeneralApiETLStrategy {

	@Override
	public ExtractResult extract(ResourceInfo resourceInfo) throws ResourceException {
		try {
			// Step 1: Check resource available or not
			ZoneId zoneId = ZoneId.systemDefault();
			LocalDateTime localDateTime = LocalDateTime.now();
			ZonedDateTime zdt = localDateTime.atZone(zoneId);
			Date updateTime = Date.from(zdt.toInstant());
			int startHour = localDateTime.getHour();
			String timeFrom = null;
			String timeTo = null;
			if (startHour == 5) {
				timeFrom = localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'06:00:00"));
				timeTo = localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'18:00:00"));
			} else if (startHour == 17) {
				LocalDateTime after1DayTime = localDateTime.plusDays(1);
				timeFrom = localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'18:00:00"));
				timeTo = after1DayTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'06:00:00"));
			} else {
				LocalDateTime after12HoursTime = localDateTime.plusHours(12);
				timeFrom = localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:00:00"));
				timeTo = after12HoursTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:00:00"));
			}
			
			String resourceWithTime = resourceInfo.getResource() + "&timeFrom=" + timeFrom + "&timeTo=" + timeTo;
			log.info("Resource: `{}`", resourceWithTime);

			JsonNode jsonNode = JsonUtils.getMapper().readTree(JsonUtils.toJsonString(resourceWithTime));
			JsonNode secondElem = jsonNode.get("records").get("locations").get(0);
			JsonNode locationElem = secondElem.get("location");
			JsonNode locationsNameElem = secondElem.get("locationsName");
			StreamSupport.stream(locationElem.spliterator(), true).map(ObjectNode.class::cast)
					.forEach(item -> item.set("cityName", locationsNameElem));

			// Step 2: Extract the array part of resource
			String resourceContent = locationElem.toString();
			String similarDateTime = StringTools.findFirstMatchSequence(resourceContent, DateUtils.DATE_SIMILAR_REGEX);
			Date srcUpdateTime = ClassUtils.isValid(similarDateTime) ? DateUtils.parseStrToDate(similarDateTime)
					: updateTime;
			log.info("Resource fetch spent {}ms", System.currentTimeMillis() - updateTime.getTime());

			return ETLHelper.buildExtractResult(resourceInfo, resourceContent, srcUpdateTime, updateTime);
		} catch (ResourceNotUpdateException | ResourceFormatErrorException e) {
			throw e;
		} catch (Exception e) {
			throw new ResourceException(e);
		}
	}
}
