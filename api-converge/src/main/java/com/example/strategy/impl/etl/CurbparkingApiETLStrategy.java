package com.example.strategy.impl.etl;

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
import com.example.utils.*;
import lombok.extern.slf4j.Slf4j;
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

import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component("CurbparkingApiETLStrategy")
public class CurbparkingApiETLStrategy extends GeneralApiETLStrategy {

    @Value("${TEST.curbparkingGetLotByRegion.body}")
    private String getLotByRegionBody;

    @Value("${TEST.curbparkingGetParkingRate.body}")
    private String getParkingRateBody;


    @Autowired
    @Qualifier("jdbcTemplateMap")
    private Map<DbSourceEnum, NamedParameterJdbcTemplate> jdbcTemplateMap;

    @Override
    public ExtractResult extract(ResourceInfo resourceInfo) throws ResourceException {

        try {
            // Step 1: Check resource available or not
            Date updateTime = new Date();
            String resource = resourceInfo.getResource();

            String body = null;
            if (resourceInfo.getTargetTable().contains("curbparking_get_lot_by_region")) {
                body = getLotByRegionBody;

            } else if (resourceInfo.getTargetTable().contains("curbparking_get_parking_rate")) {
                body = getParkingRateBody;
            }

            log.info("Resource: `{}`", resource);
            // Set request body to authenticate the resource

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
