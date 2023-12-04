package com.example.strategy.impl.etl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.example.exception.ResourceException;
import com.example.exception.ResourceFormatErrorException;
import com.example.exception.ResourceNotUpdateException;
import com.example.model.dto.etl.EncapsulationFile;
import com.example.model.dto.etl.ExtractResult;
import com.example.model.dto.etl.ResourceInfo;
import com.example.model.dto.etl.TransformResult;
import com.example.model.dto.source.traffic.device.VD;
import com.example.model.dto.source.traffic.device.VDDetectionlink;
import com.example.model.dto.source.traffic.etag.ETagPairLive;
import com.example.model.dto.source.traffic.etag.Flow;
import com.example.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Component("CECIETagPairLiveApiETLStrategy")
public class CECIETagPairLiveApiETLStrategy extends GeneralApiETLStrategy {
    protected static final String AUTHORITY_CODE_PROPERTY = "AuthorityCode";
    protected static final String UPDATE_TIME_PROPERTY = "UpdateTime";

    @Override
    public ExtractResult extract(ResourceInfo resourceInfo) throws ResourceException {
        try {
            // Step 1: Check resource available or not
            Date updateTime = new Date();
            String resource = resourceInfo.getResource();
            log.info("Resource: `{}`", resource);
            String resourceContent = JsonUtils.toJsonString(resource);

            // Step 2: Extract the array part of resource
            JsonNode tree = XmlUtils.getMapper().readTree(resourceContent.replaceAll("\n", ""));

            JsonNode arrayNode = StreamSupport.stream(tree.get("ETagPairLives").spliterator(), false)
                    .filter(JsonNode::isArray)
                    .findAny()
                    .orElseThrow(() -> new ResourceFormatErrorException("The array content not existed!"));

            JsonNode updateTimeNode = tree.get(UPDATE_TIME_PROPERTY);
            JsonNode authorityCodeNode = tree.get(AUTHORITY_CODE_PROPERTY);
            StreamSupport.stream(arrayNode.spliterator(), true)
                    .map(ObjectNode.class::cast)
                    .forEach(node -> {
                        node.set(UPDATE_TIME_PROPERTY, updateTimeNode);
                        node.set(AUTHORITY_CODE_PROPERTY, authorityCodeNode);
                        JsonNode flows = node.get("Flows").get("Flow");
                        node.remove("Flows");
                        node.set("Flows", flows);
                    });
            //JsonNode tree = XmlUtils.getMapper().readTree(JsonUtils.toJsonString(resource));
            String content = arrayNode.toString();
            String resourceArrayPart = content.substring(arrayNode.toString().indexOf('['), arrayNode.toString().lastIndexOf(']') + 1);

            Date srcUpdateTime = Objects.nonNull(updateTimeNode) ? DateUtils.parseStrToDate(updateTimeNode.asText()) : updateTime;
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
        String targetTable = resourceInfo.getTargetTable();
        String flowTable = String.format("%s_flow", targetTable);
        Date srcUpdateTime = extractResult.getSrcUpdateTime();
        Date updateTime = extractResult.getUpdateTime();
        Map<String, EncapsulationFile> importFileMap = new HashMap<>();
        // Get resources and deserialize to model array
        List<File> resources = extractResult.getResources();

        // Step 2: Transfer result and map to related model
        List<ETagPairLive> eTagPairLiveList = resources.stream()
                .map(FileOperationUtils::extractContent)
                .map(content -> JsonUtils.toBeanList(content, ETagPairLive.class))
                .flatMap(List::stream)
                .map(eTagPairLive -> {
                    eTagPairLive.setSrcUpdateTime(srcUpdateTime);
                    eTagPairLive.setUpdateTime(updateTime);
                    eTagPairLive.setInfoTime(srcUpdateTime);
                    eTagPairLive.setInfoDate(eTagPairLive.getInfoTime());
                    return eTagPairLive;
                })
                .collect(Collectors.toList());

        // Step 3: Transfer other result and map to related model
        AtomicInteger linkCounter = new AtomicInteger(1);
        List<Flow> flowLinkList = eTagPairLiveList.stream().map(eTagPairLive -> {
                    List<Flow> flows = eTagPairLive.getFlows();
                    ETLHelper.copyProperties(linkCounter, eTagPairLive, flows);
                    return flows;
                })
                .flatMap(List::stream)
                .collect(Collectors.toList());

        try {
            // Step 3: Map model list to encapsulation file
            EncapsulationFile masterFile = ETLHelper.buildEncapsulationFile(extractResult, targetTable, eTagPairLiveList);
            EncapsulationFile detailFile = ETLHelper.buildEncapsulationFile(extractResult, flowTable, flowLinkList);
            importFileMap.put(targetTable, masterFile);
            importFileMap.put(flowTable, detailFile);
            log.info("Extract result transform spent {}ms", System.currentTimeMillis() - startTime);

            return TransformResult.builder()
                    .importFileMap(importFileMap)
                    .build();
        } catch (Exception e) {
            throw new ResourceFormatErrorException(e);
        }
    }
}
