package com.example.strategy.impl.etl;

import com.fasterxml.jackson.databind.JsonNode;
import com.example.dao.h2.AlertDatastoreRepository;
import com.example.dao.mysql.VAlertDatastoreRepository;
import com.example.exception.ResourceException;
import com.example.exception.ResourceFormatErrorException;
import com.example.exception.ResourceNotUpdateException;
import com.example.model.dto.etl.EncapsulationFile;
import com.example.model.dto.etl.ExtractResult;
import com.example.model.dto.etl.ResourceInfo;
import com.example.model.dto.etl.TransformResult;
import com.example.model.dto.source.alert.AlertDumpDatastore;
import com.example.model.dto.source.alert.AlertDumpDatastoreInfoIsObject;
import com.example.model.dto.source.alert.AlertDumpDatastoreInfoList;
import com.example.model.dto.source.alert.Parameter;
import com.example.model.entity.mysql.VAlertDatastore;
import com.example.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Component("AlertDumpDatastoreApiETLStrategy")
public class AlertDumpDatastoreApiETLStrategy extends GeneralApiETLStrategy {

    @Autowired
    private VAlertDatastoreRepository vAlertDatastoreRepository;

    @Value("${alert.api.alertDumpDatastore.capId}")
    private String alertDumpDatastoreUrl;
    @Value("${alert.datastore.url}")
    private String datastoreURL;
    @Autowired
    private AlertDatastoreRepository alertDatastoreRepository;

    @Override
    public ExtractResult extract(ResourceInfo resourceInfo) throws ResourceException {
        try {
            // Step 1: Check resource available or not

            String resource = resourceInfo.getResource();
            log.info("Resource: `{}`", resource);

            List<VAlertDatastore> vAlertDatastoreList = vAlertDatastoreRepository.findAll().stream().map(s -> {
                s.setInfoTime(s.getExpires());
                s.setInfoDate(s.getExpires());
                return s;
            }).collect(Collectors.toList());

           // Date updateTime = vAlertDatastoreList.get(0).getUpdateTime();
            Date updateTime = new Date();
            List<AlertDumpDatastoreInfoList> list = new ArrayList<>();
            List<AlertDumpDatastore> alertDumpDatastoreList = new ArrayList<AlertDumpDatastore>();
            vAlertDatastoreList.forEach(vAlertDatastore -> {
                try {
                    long startTime = System.currentTimeMillis();
                    String url = "https://alerts.ncdr.nat.gov.tw/api/dump/datastore?apikey=qwDe6kY%2BbejlUCjpJWBvieSicfngdwreE1F13rWTH4IUC1zAwdz3lEHhyYMxbMJ0&capid=" + vAlertDatastore.getCapId() + "&format=json";
                    String resourceContenta = JsonUtils.toJsonString(url);
                    String jsonContent = resourceContenta.replace("/\\*.+?\\*/", "");

                    String resourceContent = null;

                    if (jsonContent.contains("alert")&&resourceContenta!=null) {

                        JsonNode jsonNode = JsonUtils.getMapper().readTree(jsonContent).get("alert");

                        if (jsonNode.get("info").isArray()) {

                            AlertDumpDatastoreInfoList alertDumpDatastoreInfoList = JsonUtils.toBean(jsonNode.toString(),AlertDumpDatastoreInfoList.class);
                            alertDumpDatastoreInfoList.getInfos().forEach(info->{

                                info.getArea().forEach(area->{
                                    AlertDumpDatastore alertDumpDatastore = new AlertDumpDatastore();
                                    alertDumpDatastore.setIdentifier(alertDumpDatastoreInfoList.getIdentifier());
                                    alertDumpDatastore.setSender(alertDumpDatastoreInfoList.getSender());
                                    alertDumpDatastore.setSent(alertDumpDatastoreInfoList.getSent());
                                    alertDumpDatastore.setStatus(alertDumpDatastoreInfoList.getStatus());
                                    alertDumpDatastore.setMsgType(alertDumpDatastoreInfoList.getMsgType());
                                    alertDumpDatastore.setSource(alertDumpDatastoreInfoList.getSource());
                                    alertDumpDatastore.setScope(alertDumpDatastoreInfoList.getScope());
                                    alertDumpDatastore.setReferences(alertDumpDatastoreInfoList.getReferences());
                                    alertDumpDatastore.setLanguage(info.getLanguage());
                                    alertDumpDatastore.setCategory(info.getCategory());
                                    alertDumpDatastore.setEvent(info.getEvent());
                                    alertDumpDatastore.setResponseType(info.getResponseType());
                                    alertDumpDatastore.setUrgency(info.getUrgency());
                                    alertDumpDatastore.setSeverity(info.getSeverity());
                                    alertDumpDatastore.setCertainty(info.getCertainty());
                                    alertDumpDatastore.setEventName(info.getEventCode().getValueName());
                                    alertDumpDatastore.setEventValue(info.getEventCode().getValue());
                                    alertDumpDatastore.setEffective(info.getEffective());
                                    alertDumpDatastore.setOnset(info.getOnset());
                                    alertDumpDatastore.setExpires(info.getExpires());
                                    alertDumpDatastore.setSenderName(info.getSenderName());
                                    alertDumpDatastore.setHeadline(info.getHeadline());
                                    alertDumpDatastore.setDescription(info.getDescription());
                                    alertDumpDatastore.setInstruction(info.getInstruction());
                                    alertDumpDatastore.setWeb(info.getWeb());
                                    String parameterName = "";
                                   for(Parameter parameter : info.getParameters()){
                                      parameterName =  parameterName + parameter.getName()+";";
                                   }
                                    alertDumpDatastore.setParameterName(parameterName);
                                   String parameterValue = "";
                                    for(Parameter parameter : info.getParameters()){
                                        parameterValue =  parameterValue + parameter.getValue()+";";
                                    }
                                    alertDumpDatastore.setParameterValue(parameterValue);
                                    alertDumpDatastore.setAreaDesc(area.getAreaDesc());
                                    if(area.getGeocode()!=null) {
                                        alertDumpDatastore.setGeocodeName(area.getGeocode().getValueName());
                                        alertDumpDatastore.setGeocodeValue(area.getGeocode().getValue());
                                    }
                                    alertDumpDatastore.setSrcUpdateTime(updateTime);
                                    alertDumpDatastore.setUpdateTime(updateTime);
                                    alertDumpDatastore.setInfoTime(updateTime);
                                    alertDumpDatastore.setInfoDate(updateTime);
                                    alertDumpDatastoreList.add(alertDumpDatastore);
                                });
                            });

                        }else if(jsonNode.get("info").isObject()){
                            AlertDumpDatastoreInfoIsObject alertDumpDatastoreInfoIsObject = JsonUtils.toBean(jsonNode.toString(),AlertDumpDatastoreInfoIsObject.class);
                            alertDumpDatastoreInfoIsObject.getInfo().getArea().forEach(area->{
                                AlertDumpDatastore alertDumpDatastore = new AlertDumpDatastore();
                                alertDumpDatastore.setIdentifier(alertDumpDatastoreInfoIsObject.getIdentifier());
                                alertDumpDatastore.setSender(alertDumpDatastoreInfoIsObject.getSender());
                                alertDumpDatastore.setSent(alertDumpDatastoreInfoIsObject.getSent());
                                alertDumpDatastore.setStatus(alertDumpDatastoreInfoIsObject.getStatus());
                                alertDumpDatastore.setMsgType(alertDumpDatastoreInfoIsObject.getMsgType());
                                alertDumpDatastore.setSource(alertDumpDatastoreInfoIsObject.getSource());
                                alertDumpDatastore.setScope(alertDumpDatastoreInfoIsObject.getScope());
                                alertDumpDatastore.setReferences(alertDumpDatastoreInfoIsObject.getReferences());
                                alertDumpDatastore.setLanguage(alertDumpDatastoreInfoIsObject.getInfo().getLanguage());
                                alertDumpDatastore.setCategory(alertDumpDatastoreInfoIsObject.getInfo().getCategory());
                                alertDumpDatastore.setEvent(alertDumpDatastoreInfoIsObject.getInfo().getEvent());
                                alertDumpDatastore.setResponseType(alertDumpDatastoreInfoIsObject.getInfo().getResponseType());
                                alertDumpDatastore.setUrgency(alertDumpDatastoreInfoIsObject.getInfo().getUrgency());
                                alertDumpDatastore.setSeverity(alertDumpDatastoreInfoIsObject.getInfo().getSeverity());
                                alertDumpDatastore.setCertainty(alertDumpDatastoreInfoIsObject.getInfo().getCertainty());
                                alertDumpDatastore.setEventName(alertDumpDatastoreInfoIsObject.getInfo().getEventCode().getValueName());
                                alertDumpDatastore.setEventValue(alertDumpDatastoreInfoIsObject.getInfo().getEventCode().getValue());
                                alertDumpDatastore.setEffective(alertDumpDatastoreInfoIsObject.getInfo().getEffective());
                                alertDumpDatastore.setOnset(alertDumpDatastoreInfoIsObject.getInfo().getOnset());
                                alertDumpDatastore.setExpires(alertDumpDatastoreInfoIsObject.getInfo().getExpires());
                                alertDumpDatastore.setSenderName(alertDumpDatastoreInfoIsObject.getInfo().getSenderName());
                                alertDumpDatastore.setHeadline(alertDumpDatastoreInfoIsObject.getInfo().getHeadline());
                                alertDumpDatastore.setDescription(alertDumpDatastoreInfoIsObject.getInfo().getDescription());
                                alertDumpDatastore.setInstruction(alertDumpDatastoreInfoIsObject.getInfo().getInstruction());
                                alertDumpDatastore.setWeb(alertDumpDatastoreInfoIsObject.getInfo().getWeb());
                                String parameterName = null;
                                if(alertDumpDatastoreInfoIsObject.getInfo().getParameters()!=null) {
                                    for (Parameter parameter : alertDumpDatastoreInfoIsObject.getInfo().getParameters()) {
                                        parameterName = parameterName + parameter.getName() + ";";
                                    }
                                    alertDumpDatastore.setParameterName(parameterName);
                                    String parameterValue = "";
                                    for(Parameter parameter : alertDumpDatastoreInfoIsObject.getInfo().getParameters()){
                                        parameterValue =  parameterValue + parameter.getValue()+";";
                                    }
                                    alertDumpDatastore.setParameterValue(parameterValue);
                                }
                                alertDumpDatastore.setAreaDesc(area.getAreaDesc());
                                if(area.getGeocode()!=null){
                                    alertDumpDatastore.setGeocodeName(area.getGeocode().getValueName());
                                    alertDumpDatastore.setGeocodeValue(area.getGeocode().getValue());
                                }
                                alertDumpDatastore.setSrcUpdateTime(updateTime);
                                alertDumpDatastore.setUpdateTime(updateTime);
                                alertDumpDatastore.setInfoTime(updateTime);
                                alertDumpDatastore.setInfoDate(updateTime);
                                alertDumpDatastoreList.add(alertDumpDatastore);
                            });
                        };
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            // Step 2: Extract the array part of resource

            String vAlertDatastoreListContent = JsonUtils.getMapper().writeValueAsString(alertDumpDatastoreList);
            JsonNode jsonNodea = JsonUtils.getMapper().readTree(vAlertDatastoreListContent);
            String recordsContent = jsonNodea.toString();

            String resourceArrayPart = recordsContent.substring(recordsContent.indexOf('['), recordsContent.lastIndexOf(']') + 1);
//            String similarDateTime = StringTools.findFirstMatchSequence(recordsContent, DateUtils.DATE_SIMILAR_REGEX);
            Date srcUpdateTime =  updateTime;
            log.info("Resource fetch spent {}ms", System.currentTimeMillis() - updateTime.getTime());

            return ETLHelper.buildExtractResult(resourceInfo, resourceArrayPart, srcUpdateTime, updateTime);
        } catch (ResourceNotUpdateException | ResourceFormatErrorException e) {
            throw e;
        } catch (Exception e) {
            throw new ResourceException(e);
        }
    }

    @Override
    public TransformResult transform(ResourceInfo resourceInfo, ExtractResult extractResult) throws ResourceFormatErrorException {
        // Step 1: Prepare transform information
        long startTime = System.currentTimeMillis();
        //主表格
        String targetTable = resourceInfo.getTargetTable();
//
        Map<String, EncapsulationFile> importFileMap = new HashMap<>();
        // Get resources and deserialize to model array
        List<File> resources = extractResult.getResources();
        // Step 2: Transfer result and map to related model

        List<AlertDumpDatastore> alertDumpDatastoreList = resources.stream()
                .map(FileOperationUtils::extractContent)
                .map(content -> JsonUtils.toBeanList(content, AlertDumpDatastore.class))
                .flatMap(List::stream).collect(Collectors.toList());

        // Step 3: Transfer other result and map to related model
        AtomicInteger messageCounter = new AtomicInteger(1);


        try {
            // Step 3: Map model list to encapsulation file
            EncapsulationFile masterFile = ETLHelper.buildEncapsulationFile(extractResult, targetTable, alertDumpDatastoreList);
            importFileMap.put(targetTable, masterFile);

            log.info("Extract result transform spent {}ms", System.currentTimeMillis() - startTime);

            return TransformResult.builder()
                    .importFileMap(importFileMap)
                    .build();
        } catch (Exception e) {
            throw new ResourceFormatErrorException(e);
        }
    }

}
