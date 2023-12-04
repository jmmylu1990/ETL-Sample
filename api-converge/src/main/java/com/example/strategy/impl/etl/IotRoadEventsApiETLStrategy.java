package com.example.strategy.impl.etl;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.jsoup.select.Collector;
import org.springframework.stereotype.Component;
import com.example.exception.ResourceException;
import com.example.exception.ResourceFormatErrorException;
import com.example.exception.ResourceNotUpdateException;
import com.example.model.dto.etl.ExtractResult;
import com.example.model.dto.etl.ResourceInfo;
import com.example.model.dto.source.iot.RoadEvents;
import com.example.utils.ClassUtils;
import com.example.utils.DateUtils;
import com.example.utils.ETLHelper;
import com.example.utils.JsonUtils;
import com.example.utils.StringTools;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("IotRoadEventsApiETLStrategy")
public class IotRoadEventsApiETLStrategy extends GeneralApiETLStrategy {

	@Override
	public ExtractResult extract(ResourceInfo resourceInfo) throws ResourceException {
		try {
			// Step 1: Check resource available or not
			Date updateTime = new Date();
			String resource = resourceInfo.getResource();
			log.info("Resource: `{}`", resource);

			// Step 2: Extract the array part of resource
			String resourceContent = JsonUtils.toJsonString(resource);
			List<RoadEvents> roadEventsList = JsonUtils.toBeanList(resourceContent, RoadEvents.class);
			List<RoadEvents> roadEventsListSorted = roadEventsList.stream()
					.sorted(Comparator.comparing(RoadEvents::getVersionUpdateTime)).collect(Collectors.toList());
			Date NewestVersionUpdateTime = roadEventsListSorted.get(roadEventsListSorted.size() - 1)
					.getVersionUpdateTime();
			Date srcUpdateTime = NewestVersionUpdateTime;
			log.info("Resource fetch spent {}ms", System.currentTimeMillis() - updateTime.getTime());

			return ETLHelper.buildExtractResult(resourceInfo, resourceContent, srcUpdateTime, updateTime);
		} catch (ResourceNotUpdateException | ResourceFormatErrorException e) {
			throw e;
		} catch (Exception e) {
			throw new ResourceException(e);
		}
	}

}
