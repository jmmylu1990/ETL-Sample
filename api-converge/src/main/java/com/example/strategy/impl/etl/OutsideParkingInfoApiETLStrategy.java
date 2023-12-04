package com.example.strategy.impl.etl;

import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import com.example.exception.ResourceException;
import com.example.exception.ResourceFormatErrorException;
import com.example.exception.ResourceNotUpdateException;
import com.example.model.dto.etl.ExtractResult;
import com.example.model.dto.etl.ResourceInfo;
import com.example.model.dto.source.parking.OutsideParkingInfo;
import com.example.utils.ClassUtils;
import com.example.utils.DateUtils;
import com.example.utils.ETLHelper;
import com.example.utils.JsonUtils;
import com.example.utils.StringTools;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("OutsideParkingInfoApiETLStrategy")
public class OutsideParkingInfoApiETLStrategy extends GeneralApiETLStrategy {

	@Override
	public ExtractResult extract(ResourceInfo resourceInfo) throws ResourceException {
		try {
			// Step 1: Check resource available or not
			Date updateTime = new Date();
			String resource = resourceInfo.getResource();
			String resourcePage = resource.substring(0, resource.lastIndexOf("download"));
			Document document = Jsoup.parse(JsonUtils.toJsonString(resourcePage));
			Elements elements = document.select("table > tbody > tr > td");
			log.info("Resource: `{}`", resource);

			List<OutsideParkingInfo> outsideParkingInfoList = new ArrayList<>();

			try (InputStream in = new URL(resource).openStream()) {
				List<String> lines = IOUtils.readLines(in, StandardCharsets.UTF_8);
				// Skip header
				outsideParkingInfoList = lines.stream().skip(1).map(lineCentent -> {
					OutsideParkingInfo outsideParkingInfo = new OutsideParkingInfo();
					String[] sgments = lineCentent.split(",");
					outsideParkingInfo.setArea(sgments[0]);
					outsideParkingInfo.setType(sgments[1]);
					outsideParkingInfo.setParkName(sgments[2]);
					outsideParkingInfo.setAddress(sgments[3]);
					outsideParkingInfo.setLon(Double.parseDouble(sgments[4]));
					outsideParkingInfo.setLat(Double.parseDouble(sgments[5]));
					outsideParkingInfo.setTotalLargeCar(!sgments[6].equals("") ? Integer.parseInt(sgments[6]) : null);
					outsideParkingInfo.setTotalSmallCar(!sgments[7].equals("") ? Integer.parseInt(sgments[7]) : null);
					outsideParkingInfo.setTotalMotor(!sgments[8].equals("") ? Integer.parseInt(sgments[8]) : null);
					outsideParkingInfo.setTotalBike(!sgments[9].equals("") ? Integer.parseInt(sgments[9]) : null);
					outsideParkingInfo.setPayex(sgments[10]);
					outsideParkingInfo.setRemark(sgments.length == 12 ? sgments[11] : null);
					
					return outsideParkingInfo;
				}).collect(Collectors.toList());
			};
			
			// Step 2: Extract the array part of resource
			String resourceContent = JsonUtils.getMapper().writeValueAsString(outsideParkingInfoList);
			String resourceArrayPart = resourceContent.substring(resourceContent.indexOf('['), resourceContent.lastIndexOf(']') + 1);
			String similarDateTime = StringTools.findFirstMatchSequence(elements.text(), DateUtils.DATE_SIMILAR_REGEX);
			Date srcUpdateTime = ClassUtils.isValid(similarDateTime) ? DateUtils.parseStrToDate(similarDateTime) : updateTime;
			log.info("Resource fetch spent {}ms", System.currentTimeMillis() - updateTime.getTime());

			return ETLHelper.buildExtractResult(resourceInfo, resourceArrayPart, srcUpdateTime, updateTime);
		} catch (ResourceNotUpdateException | ResourceFormatErrorException e) {
			throw e;
		} catch (Exception e) {
			throw new ResourceException(e);
		}
	}
}
