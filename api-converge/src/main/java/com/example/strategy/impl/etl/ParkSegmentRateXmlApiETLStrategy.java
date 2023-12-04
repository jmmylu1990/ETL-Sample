package com.example.strategy.impl.etl;

import java.util.Date;
import org.springframework.stereotype.Component;
import com.example.exception.ResourceException;
import com.example.exception.ResourceFormatErrorException;
import com.example.exception.ResourceNotUpdateException;
import com.example.model.dto.etl.ExtractResult;
import com.example.model.dto.etl.ResourceInfo;
import com.example.model.dto.source.parking.SegmentRate;
import com.example.utils.ETLHelper;
import com.example.utils.JsonUtils;
import com.example.utils.XmlUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("ParkSegmentRateXmlApiETLStrategy")
public class ParkSegmentRateXmlApiETLStrategy extends GeneralApiETLStrategy {

	@Override
	public ExtractResult extract(ResourceInfo resourceInfo) throws ResourceException {
		try {
			// Step 1: Check resource available or not
			Date updateTime = new Date();
			String resource = resourceInfo.getResource();
			log.info("Resource: `{}`", resource);

			// Step 2: Extract the array part of resource
		    SegmentRate segmentRate = XmlUtils.toBean(JsonUtils.toJsonString(resource), SegmentRate.class);
			String resourceArrayPart = JsonUtils.getMapper().writeValueAsString(segmentRate.getParkingRates());
			Date srcUpdateTime = segmentRate.getUpdateTime();
			
			log.info("Resource fetch spent {}ms", System.currentTimeMillis() - updateTime.getTime());

			return ETLHelper.buildExtractResult(resourceInfo, resourceArrayPart, srcUpdateTime, updateTime);
		} catch (ResourceNotUpdateException | ResourceFormatErrorException e) {
			throw e;
		} catch (Exception e) {
			throw new ResourceException(e);
		}
	}
	
}
