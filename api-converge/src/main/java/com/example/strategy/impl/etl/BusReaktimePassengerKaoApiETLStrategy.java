package com.example.strategy.impl.etl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.example.exception.ResourceException;
import com.example.exception.ResourceFormatErrorException;
import com.example.exception.ResourceNotUpdateException;
import com.example.model.dto.etl.ExtractResult;
import com.example.model.dto.etl.ResourceInfo;
import com.example.model.dto.source.iot.device.CamevtParkingRemaining;
import com.example.model.dto.source.other.bus.Bus;
import com.example.model.dto.source.other.bus.BusReaktimePassengerKao;
import com.example.model.dto.source.other.bus.Route;
import com.example.model.dto.source.other.bus.Routes;
import com.example.utils.ClassUtils;
import com.example.utils.DateUtils;
import com.example.utils.ETLHelper;
import com.example.utils.HttpUtils;
import com.example.utils.JsonUtils;
import com.example.utils.StringTools;
import com.example.utils.XmlUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("BusReaktimePassengerKaoApiETLStrategy")
public class BusReaktimePassengerKaoApiETLStrategy extends GeneralApiETLStrategy {
	@Override
	public ExtractResult extract(ResourceInfo resourceInfo) throws ResourceException {
		try {
			// Step 1: Check resource available or not
			Date updateTime = new Date();
			String resource = resourceInfo.getResource();
			log.info("Resource: `{}`", resource);
			JsonNode jsonNode = XmlUtils.getMapper().readTree(JsonUtils.toJsonString(resource));
			BusReaktimePassengerKao busReaktimePassengerKao = JsonUtils.toBean(jsonNode.toString(),
					BusReaktimePassengerKao.class);

			List<Route> routeList = busReaktimePassengerKao.getRoutes().getRoute();

			List<Bus> busList = routeList.stream().filter(route -> route.getBuses() != null)
					.collect(Collectors.toList()).stream().map(route -> {
						String routeID = route.getId();

						return route.getBuses().getBus().stream().filter(Objects::nonNull).map(bus -> {

							bus.setRouteID(routeID);
							bus.setUpdateTime(updateTime);
							bus.setSrcUpdateTime(updateTime);

							return bus;
						}).collect(Collectors.toList());
					}).flatMap(List::stream).collect(Collectors.toList());

			JsonNode tree = JsonUtils.getMapper().valueToTree(busList);

			// Step 2: Extract the array part of resource

			String resourceContent = JsonUtils.getMapper().writeValueAsString(tree);
			System.out.println(resourceContent);
			String resourceArrayPart = resourceContent.substring(resourceContent.toString().indexOf('['),
					resourceContent.lastIndexOf(']') + 1);

//			String similarDateTime = StringTools.findFirstMatchSequence(resourceContent, DateUtils.DATE_SIMILAR_REGEX);
//			Date srcUpdateTime = ClassUtils.isValid(similarDateTime) ? DateUtils.parseStrToDate(similarDateTime)
//					: updateTime;
			Date srcUpdateTime = updateTime;
			log.info("Resource fetch spent {}ms", System.currentTimeMillis() - updateTime.getTime());
			return ETLHelper.buildExtractResult(resourceInfo, resourceArrayPart, srcUpdateTime, updateTime);
		} catch (ResourceNotUpdateException | ResourceFormatErrorException e) {
			throw e;
		} catch (Exception e) {
			throw new ResourceException(e);
		}
	}

}
