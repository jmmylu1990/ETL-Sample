package com.example.strategy.impl.etl;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import com.example.utils.ETLHelper;
import com.example.utils.FileOperationUtils;
import com.example.utils.HttpUtils;
import com.example.exception.ResourceException;
import com.example.exception.ResourceFormatErrorException;
import com.example.exception.ResourceNotUpdateException;
import com.example.model.dto.etl.EncapsulationFile;
import com.example.model.dto.etl.ExtractResult;
import com.example.model.dto.etl.ResourceInfo;
import com.example.model.dto.etl.TransformResult;
import com.example.model.dto.source.parking.CarParkTBKCAvailability;
import com.example.model.dto.source.parking.CarParkTBKCStatic;
import com.example.model.enums.DbSourceEnum;
import com.example.utils.ClassUtils;
import com.example.utils.DateUtils;
import com.example.utils.JsonUtils;
import com.example.utils.StringTools;
import lombok.extern.slf4j.Slf4j;
import com.example.utils.SqlUtils;

@Slf4j
@Component("CarParkTBKCStaticApiETLStrategy")
public class CarParkTBKCStaticApiETLStrategy extends GeneralApiETLStrategy {

	@Value("${TEST.carParkTBCKStatic.apiKey}")
	private String apiKey;
	@Value("${carParkTBKCStatic.insertCarParkTBKCHourAvailability}")
	private String insertSQL;

	@Autowired
	@Qualifier("jdbcTemplateMap")
	private Map<DbSourceEnum, NamedParameterJdbcTemplate> jdbcTemplateMap;

	@Override
	public ExtractResult extract(ResourceInfo resourceInfo) throws ResourceException {
		try {
			// Step 1: Check resource available or not
			Date updateTime = new Date();
			String resource = resourceInfo.getResource();

			log.info("Resource: `{}`", resource);
			// Set request body to authenticate the resource
			String body = apiKey;

			// Step 2: Extract the array part of resource
			String resourceContent = postJsonStringContent(resource, body);
			// Get Json tree to extract infomation we need

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

	@Override
	public TransformResult transform(ResourceInfo resourceInfo, ExtractResult extractResult)
			throws ResourceFormatErrorException {
		// Step 1: Prepare transform information
		long startTime = System.currentTimeMillis();
		String staticTable = resourceInfo.getTargetTable();
		String availabilityTable = String.format("%s_availability", staticTable).replace("static_", "");

		Date srcUpdateTime = extractResult.getSrcUpdateTime();
		Date updateTime = extractResult.getUpdateTime();
		Map<String, EncapsulationFile> importFileMap = new HashMap<>();
		// Get resources and deserialize to model array
		List<File> resources = extractResult.getResources();

		// Step 2: Transfer result and map to related model
		List<CarParkTBKCStatic> carParkTBKCStaticList = resources.stream().map(FileOperationUtils::extractContent)
				.map(content -> JsonUtils.toBeanList(content, CarParkTBKCStatic.class)).flatMap(List::stream)
				.map(carParkTBKCStatic -> {

					carParkTBKCStatic.setSrcUpdateTime(srcUpdateTime);
					carParkTBKCStatic.setUpdateTime(updateTime);
					carParkTBKCStatic.setInfoTime(srcUpdateTime);
					carParkTBKCStatic.setInfoDate(srcUpdateTime);

					return carParkTBKCStatic;
				}).collect(Collectors.toList());

		List<CarParkTBKCAvailability> carParkTBKCAvailabilityList = resources.stream()
				.map(FileOperationUtils::extractContent)
				.map(content -> JsonUtils.toBeanList(content, CarParkTBKCAvailability.class)).flatMap(List::stream)
				.map(carParkTBKCAvailability -> {
					
					carParkTBKCAvailability.setSrcUpdateTime(srcUpdateTime);
					carParkTBKCAvailability.setUpdateTime(updateTime);
					carParkTBKCAvailability.setInfoTime(srcUpdateTime);
					carParkTBKCAvailability.setInfoDate(srcUpdateTime);

					return carParkTBKCAvailability;
				}).collect(Collectors.toList());

		try {
			// Step 3: Map model list to encapsulation file
			EncapsulationFile masterFile = ETLHelper.buildEncapsulationFile(extractResult, staticTable,
					carParkTBKCStaticList);
			EncapsulationFile detailFile = ETLHelper.buildEncapsulationFile(extractResult, availabilityTable,
					carParkTBKCAvailabilityList);
			importFileMap.put(staticTable, masterFile);
			importFileMap.put(availabilityTable, detailFile);
			log.info("Extract result transform spent {}ms", System.currentTimeMillis() - startTime);

			return TransformResult.builder().importFileMap(importFileMap).build();
		} catch (Exception e) {
			throw new ResourceFormatErrorException(e);
		}
	}

	@Override
	public void successCallback() {
		log.info("DbSource: {} / Procedure: {}", DbSourceEnum.MY_SQL, insertSQL);
		JdbcTemplate jdbcTemplate = jdbcTemplateMap.get(DbSourceEnum.MY_SQL).getJdbcTemplate();
		jdbcTemplate.execute(SqlUtils.clean(insertSQL));

	}

	public static String postJsonStringContent(String url, String body) throws Exception {
		HttpPost httpPost = (HttpPost) HttpUtils.toHttpRequest(url, HttpMethod.POST.name());
		httpPost.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		httpPost.setEntity(new StringEntity(body, StandardCharsets.UTF_8));
		try (CloseableHttpClient httpClient = (CloseableHttpClient) HttpUtils.createHttpClient();
				CloseableHttpResponse response = httpClient.execute(httpPost);
				InputStream content = response.getEntity().getContent();) {
			return EntityUtils.toString(response.getEntity());
		}
	}

}
