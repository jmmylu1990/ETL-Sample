package com.example.strategy.impl.etl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.example.exception.ResourceException;
import com.example.exception.ResourceFormatErrorException;
import com.example.exception.ResourceNotUpdateException;
import com.example.model.dto.etl.ExtractResult;
import com.example.model.dto.etl.ResourceInfo;
import com.example.utils.DateUtils;
import com.example.utils.ETLHelper;
import com.example.utils.JsonUtils;
import com.example.utils.XmlUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Date;
import java.util.Objects;
import java.util.stream.StreamSupport;

@Slf4j
@Component("CECIETagPairApiETLStrategy")
public class CECIETagPairApiETLStrategy extends GeneralApiETLStrategy {
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
            JsonNode tree = XmlUtils.getMapper().readTree(resourceContent.replaceAll("\n",""));

            JsonNode arrayNode = StreamSupport.stream(tree.get("ETagPairs").spliterator(), false)
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
}
