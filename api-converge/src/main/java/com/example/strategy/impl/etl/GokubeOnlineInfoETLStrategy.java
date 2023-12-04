package com.example.strategy.impl.etl;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.exception.ResourceException;
import com.example.exception.ResourceFormatErrorException;
import com.example.exception.ResourceNotUpdateException;
import com.example.model.dto.etl.ExtractResult;
import com.example.model.dto.etl.ResourceInfo;
import com.example.model.dto.source.other.GokubeOnlineLineAndOffLineInfo;
import com.example.model.dto.source.other.Scooter;
import com.example.utils.ETLHelper;
import com.example.utils.HttpUtils;
import com.example.utils.JsonUtils;
import com.example.utils.jackson.StringTrimmerModule;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component("gokubeOnlineInfoETLStrategy")
public class GokubeOnlineInfoETLStrategy extends GeneralApiETLStrategy {

    @Value("${gokube.onlineAndoffLineinfo.authorization}")
    private  String key;
    @Value("${gokube.onlineAndoffLineinfo.valueOfAuthorization}")
    private  String value;

    private static ObjectMapper objectMapper;

    @Override
    public ExtractResult extract(ResourceInfo resourceInfo) throws ResourceException {
        try {
            // Step 1: Check resource available or not
            Date updateTime = new Date();
            String resource = resourceInfo.getResource();
            log.info("Resource: `{}`", resource);

            // Step 2: Extract the array part of resource
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("distance", 6);
            paramMap.put("lat", 22.639730035981824);
            paramMap.put("lng", 120.30256153934542);
            paramMap.put("status", "online");

            String resourceContent = postJsonString(resource, key,value,paramMap);

            Scooter scooter = JsonUtils.toBean(resourceContent, Scooter.class);

            List<GokubeOnlineLineAndOffLineInfo> gokubeOnlineAndOffLineInfoList = scooter.getData().stream().map(s->{

                GokubeOnlineLineAndOffLineInfo gokubeOnlineAndOffLineInfo = new GokubeOnlineLineAndOffLineInfo();

                gokubeOnlineAndOffLineInfo.setCode(scooter.getCode());
                gokubeOnlineAndOffLineInfo.setPlate(s.getPlate());
                gokubeOnlineAndOffLineInfo.setLat(s.getLatitude());
                gokubeOnlineAndOffLineInfo.setLon(s.getLongitude());
                gokubeOnlineAndOffLineInfo.setPower(s.getPower());
                gokubeOnlineAndOffLineInfo.setStatus(s.getStatus());
                gokubeOnlineAndOffLineInfo.setSrcUpdateTime(scooter.getUpdatetime());
                //System.out.println(scooter.getUpdatetime());
                gokubeOnlineAndOffLineInfo.setUpdateTime(updateTime);
                gokubeOnlineAndOffLineInfo.setInfoDate(scooter.getUpdatetime());
                gokubeOnlineAndOffLineInfo.setInfoTime(scooter.getUpdatetime());

                return gokubeOnlineAndOffLineInfo;
            }).collect(Collectors.toList());
          String gokubeOnlineAndOffLineInfoListContent =   JsonUtils.getMapper().writeValueAsString(gokubeOnlineAndOffLineInfoList);

            String resourceArrayPart = gokubeOnlineAndOffLineInfoListContent.substring(gokubeOnlineAndOffLineInfoListContent.indexOf('['),
                    gokubeOnlineAndOffLineInfoListContent.lastIndexOf(']') + 1);

            log.info("Resource fetch spent {}ms", System.currentTimeMillis() - updateTime.getTime());

            return ETLHelper.buildExtractResult(resourceInfo, resourceArrayPart, scooter.getUpdatetime(), updateTime);
        } catch (ResourceNotUpdateException | ResourceFormatErrorException e) {
            throw e;
        } catch (Exception e) {
            throw new ResourceException(e);
        }
    }

    public static String postJsonString(String url ,String key ,String value,Object body) throws Exception {

        HttpPost httpPost = (HttpPost) HttpUtils.toHttpRequest(url, HttpMethod.POST.name());
        httpPost.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        httpPost.setHeader(key, value);
        httpPost.setEntity(new StringEntity(getMapper().writeValueAsString(body), StandardCharsets.UTF_8));
        try (CloseableHttpClient httpClient = (CloseableHttpClient) HttpUtils.createHttpClient();
             CloseableHttpResponse response = httpClient.execute(httpPost);
             InputStream content = response.getEntity().getContent();) {
            return EntityUtils.toString(response.getEntity());
        }
    }


    /**
     * 初始化Json序列化/反序列化轉化器
     *
     * @return Json物件轉換器, ObjectMapper型別
     */
    private static ObjectMapper initialMapper() {
        objectMapper = new ObjectMapper();
        // Set the field name case insensitive
        // Set the key or value can use single quote or no quote
        // Set the array value can only single value
        objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
                .configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .enable(MapperFeature.USE_WRAPPER_NAME_AS_PROPERTY_NAME)
                .enable(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)
                .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
                .enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
                .registerModule(new StringTrimmerModule());

        return objectMapper;
    }

    public static ObjectMapper getMapper() {
        return (objectMapper == null) ? initialMapper() : objectMapper;
    }
}
