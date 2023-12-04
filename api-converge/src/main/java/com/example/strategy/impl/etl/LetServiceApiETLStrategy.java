package com.example.strategy.impl.etl;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.model.dto.source.other.LetService;
import com.example.utils.*;
import com.example.utils.jackson.StringTrimmerModule;
import org.apache.http.HttpHeaders;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import com.example.exception.ResourceException;
import com.example.exception.ResourceFormatErrorException;
import com.example.exception.ResourceNotUpdateException;
import com.example.model.dto.etl.ExtractResult;
import com.example.model.dto.etl.ResourceInfo;
import com.example.model.dto.source.letservice.QueryParams;

import lombok.extern.slf4j.Slf4j;

import static com.example.utils.JsonUtils.postJsonString;

@Slf4j
@Component("LetServiceApiETLStrategy")
public class LetServiceApiETLStrategy extends GeneralApiETLStrategy {

    @Value("${TEST.letService.sessionKey.url}")
    private String sessionKeyUrl;
    private ObjectMapper objectMapper;
    @Value("${TEST.letService.sessionKey.userid}")
    private String userID;
    @Value("${TEST.letService.sessionKey.userpwd}")
    private String userPwd;
    @Value("${TEST.letService.sessionKey.deviceos}")
    private String deviceOS;
    @Value("${TEST.letService.sessionKey.langid}")
    private String langID;
    @Value("${TEST.letService.sessionKey.clientip}")
    private String ClientIP;





    @Override
    public ExtractResult extract(ResourceInfo resourceInfo) throws ResourceException {
        try {
            // Step 1: Check resource available or not
            Date updateTime = new Date();
            Date yesterDayTime = DateUtils.addDays(updateTime, -1);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String sDateStr = sdf.format(yesterDayTime);
            String eDateStr = sdf.format(updateTime);
            //輸入x-www-form-urlencoded參數取得SessionKey
            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("userID", userID);
            paramMap.put("userPwd", userPwd);
            paramMap.put("deviceOS", deviceOS);
            paramMap.put("langID", langID);
            paramMap.put("ClientIP", ClientIP);
            String jsonContentOfToken = postJsonString(sessionKeyUrl, paramMap);
            JsonNode jsonNode = JsonUtils.getMapper().readTree(jsonContentOfToken);
            String sessionKey = jsonNode.get("SessionKey").asText();
            log.info("SessionKeyURL: `{}` ", sessionKeyUrl);
            log.info("SessionKey: `{}` ", sessionKey);
            Map<String, String> headerMap = new HashMap<>();
            headerMap.put("SessionKey", sessionKey);

            //取得各設備的偵測地點編號
            List<Integer> techlawsidNos = null;
            if (resourceInfo.getResource().contains("GetRedLightWithPaging")) {
                techlawsidNos = Arrays.asList(2, 3, 4, 5, 6, 7, 25);
            } else if (resourceInfo.getResource().contains("GetInverseHookTurnWithPaging")) {
                techlawsidNos = Arrays.asList(1);
            } else if (resourceInfo.getResource().contains("GetPedestrianPriorityWithPaging")) {
                techlawsidNos = Arrays.asList(10, 11, 12, 13, 14);
            } else if (resourceInfo.getResource().contains("GetRoadSurfaceMarkingWithPaging")) {
                techlawsidNos = Arrays.asList(8, 9);
            }

            String url = null;
            String sDate = String.format("SDate=%s", sDateStr);
            String eDate = String.format("EDate=%s", eDateStr);
            String pageSizeStr = String.format("pageSize=%s", 100);
            int page = 1;
            int totalList = 0;
            List<List<LetService>> letServiceLists = new ArrayList<>();

            for (Integer techlawsidNo : techlawsidNos) {
                String techlawsidStr = String.format("techlawsid=%s", techlawsidNo);

                while (true) {
                    String pageStr = String.format("page=%s", page);
                    url = String.format("%s?%s&%s&%s&%s&%s&%s&%s&%s", resourceInfo.getResource(), "sortSet=ETime+desc", pageStr, pageSizeStr
                            , techlawsidStr, "plateNumber=", sDate, eDate, "beenverified=0").replaceAll(" ", "%20");
                    log.info("URL: `{}` ", url);
                    String jsonContent = JsonUtils.toJsonString(
                            url,
                            headerMap
                    );
                    //不確定該資料會有幾頁，取得的頁數資料為null時，跳出迴圈
                    List<LetService> letServiceList = JsonUtils.toBeanList(jsonContent, LetService.class);
                    if (jsonContent.length()==2) {
                        log.info("jsonContent: `{}` ,換下一個偵測地點編號", jsonContent);
                         page = 1;
                        break;
                    }
                    letServiceLists.add(letServiceList);
                    log.info("techlawSID: `{}` ,目前有: `{}` 頁資料", techlawsidNo,page);
                    page++;
                    totalList++;

                }


            }
            List<LetService> list = letServiceLists.stream().flatMap(List::stream).collect(Collectors.toList());

            log.info("總共有 `{}` 頁資料", totalList);

            String resourceContent = JsonUtils.getMapper().writeValueAsString(list);

            // Step 2: Extract the array part of resource
            String resourceArrayPart = resourceContent.substring(resourceContent.indexOf('['),
                    resourceContent.lastIndexOf(']') + 1);
            String similarDateTime = StringTools.findFirstMatchSequence(resourceContent, DateUtils.DATE_SIMILAR_REGEX);
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

    public String postJsonString(String url, Map<String, String> paramMap) throws Exception {
        HttpPost httpPost = (HttpPost) HttpUtils.toHttpRequest(url, HttpMethod.POST.name());
        httpPost.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
        List<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
        if (paramMap != null) {
            for (Map.Entry<String, String> entry : paramMap.entrySet()) {
                nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));

            }
        }
        httpPost.setEntity(new UrlEncodedFormEntity(nvps, StandardCharsets.UTF_8));
        try (CloseableHttpClient httpClient = (CloseableHttpClient) HttpUtils.createHttpClient();
             CloseableHttpResponse response = httpClient.execute(httpPost);
             InputStream content = response.getEntity().getContent();) {
            return EntityUtils.toString(response.getEntity());
        }
    }

    public ObjectMapper getMapper() {
        return (objectMapper == null) ? initialMapper() : objectMapper;
    }

    private ObjectMapper initialMapper() {
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


}
