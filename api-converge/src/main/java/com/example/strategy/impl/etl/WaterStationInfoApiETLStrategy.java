package com.example.strategy.impl.etl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.example.exception.ResourceException;
import com.example.exception.ResourceFormatErrorException;
import com.example.exception.ResourceNotUpdateException;
import com.example.model.dto.etl.EncapsulationFile;
import com.example.model.dto.etl.ExtractResult;
import com.example.model.dto.etl.ResourceInfo;
import com.example.model.dto.etl.TransformResult;
import com.example.model.dto.source.iot.WaterStationInfoDetail;
import com.example.model.dto.source.iot.WaterStationInfoMaster;
import com.example.utils.ETLHelper;
import com.example.utils.FileOperationUtils;
import com.example.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("WaterStationInfoApiETLStrategy")
public class WaterStationInfoApiETLStrategy extends GeneralApiETLStrategy {

    @Value("${waterStationInfo.authorization}")
    private String key;
    @Value("${waterStationInfo.valueOfAuthorization}")
    private String value;

    @Override
    public ExtractResult extract(ResourceInfo resourceInfo) throws ResourceException {
        try {
            // Step 1: Check resource available or not
            Date updateTime = new Date();
            String resource = resourceInfo.getResource();
            log.info("Resource: `{}`", resource);

            // Step 2: Extract the array part of resource
            Map<String, String> paramMap = new HashMap<>();
            paramMap.put(key, value);

            String resourceContent = JsonUtils.toJsonString(resource, paramMap);
//			try {
//			      FileWriter myWriter = new FileWriter("C:\\Users\\2006007\\OneDrive - example\\桌面\\aaa.txt");
//			      myWriter.write(resourceContent);
//			      myWriter.close();
//			    } 
//			    catch (IOException e) {
//			     
//			      e.printStackTrace();
//			    }
            List<WaterStationInfoMaster> waterStationInfoMasterList = JsonUtils.toBeanList(resourceContent,
                    WaterStationInfoMaster.class);

            List<Date> measTimestamps = waterStationInfoMasterList.stream().map(s -> {
                Date measTimestamp = s.getWaterStationInfoDetails().get(0).getMeasTimestamp();

                return measTimestamp;
            }).sorted().collect(Collectors.toList());

            String resourceArrayPart = resourceContent.substring(resourceContent.indexOf('['),
                    resourceContent.lastIndexOf(']') + 1);

            Date srcUpdateTime = measTimestamps.get(measTimestamps.size() - 1);
            log.info("Resource fetch spent {}ms", System.currentTimeMillis() - updateTime.getTime());

            return ETLHelper.buildExtractResult(resourceInfo, resourceArrayPart, srcUpdateTime, updateTime);
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
        String targetTable = resourceInfo.getTargetTable();
        String waterStationInfoDetailTable = String.format("%s_detail", targetTable.replace("_master", ""));
        Date srcUpdateTime = extractResult.getSrcUpdateTime();
        Date updateTime = extractResult.getUpdateTime();
        Map<String, EncapsulationFile> importFileMap = new HashMap<>();
        // Get resources and deserialize to model array
        List<File> resources = extractResult.getResources();

        // Step 2: Transfer result and map to related model
        List<WaterStationInfoMaster> waterStationInfoMasterList = resources.stream()
                .map(FileOperationUtils::extractContent)
                .map(content -> JsonUtils.toBeanList(content, WaterStationInfoMaster.class)).flatMap(List::stream)
                .map(waterStationInfoMaster -> {
                    waterStationInfoMaster.setSrcUpdateTime(srcUpdateTime);
                    waterStationInfoMaster.setUpdateTime(updateTime);
                    List<Date> measTimestamps = waterStationInfoMaster.getWaterStationInfoDetails().stream().map(WaterStationInfoDetail::getMeasTimestamp).sorted().collect(Collectors.toList());
                    waterStationInfoMaster.setInfoTime(measTimestamps.get(measTimestamps.size() - 1));
                    waterStationInfoMaster.setInfoDate(waterStationInfoMaster.getInfoTime());
                    return waterStationInfoMaster;
                }).collect(Collectors.toList());

        // Step 3: Transfer other result and map to related model
        AtomicInteger linkCounter = new AtomicInteger(1);
        List<WaterStationInfoDetail> waterStationInfoDetailList = waterStationInfoMasterList.stream()
                .map(waterStationInfoMaster -> {
                    List<WaterStationInfoDetail> waterStationInfoDetails = waterStationInfoMaster
                            .getWaterStationInfoDetails().stream().map(detail -> {

                                detail.setSrcUpdateTime(srcUpdateTime);
                                detail.setUpdateTime(updateTime);
                                detail.setInfoTime(detail.getMeasTimestamp());
                                detail.setInfoDate(detail.getInfoTime());

                                return detail;
                            }).collect(Collectors.toList());

                    //因為淹水detail的要採用measTimsstamp，使用ETLHelper.copyProperties會將master的相同屬性的值複製到detail的相同屬性
                    // 例如srcUpdateTime、updataTime、infoDate、infoTime，故不採用
                    // ETLHelper.copyProperties(linkCounter, waterStationInfoMaster, waterStationInfoDetails);
                    return waterStationInfoDetails;
                }).flatMap(List::stream).collect(Collectors.toList());

        try {
            // Step 3: Map model list to encapsulation file
            EncapsulationFile masterFile = ETLHelper.buildEncapsulationFile(extractResult, targetTable,
                    waterStationInfoMasterList);
            EncapsulationFile detailFile = ETLHelper.buildEncapsulationFile(extractResult, waterStationInfoDetailTable,
                    waterStationInfoDetailList);
            importFileMap.put(targetTable, masterFile);
            importFileMap.put(waterStationInfoDetailTable, detailFile);
            log.info("Extract result transform spent {}ms", System.currentTimeMillis() - startTime);

            return TransformResult.builder().importFileMap(importFileMap).build();
        } catch (Exception e) {
            throw new ResourceFormatErrorException(e);
        }
    }

}
