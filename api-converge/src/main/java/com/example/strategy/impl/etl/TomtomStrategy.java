package com.example.strategy.impl.etl;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.hadoop.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.JsonNode;
import com.example.exception.ResourceException;
import com.example.exception.ResourceFormatErrorException;
import com.example.exception.ResourceNotUpdateException;
import com.example.model.dto.etl.ExtractResult;
import com.example.model.dto.etl.ResourceInfo;
import com.example.model.dto.source.tomtom.DynamicInfo;
import com.example.model.dto.source.tomtom.Summary;
import com.example.model.dto.source.tomtom.TomtomRoad;
import com.example.model.enums.DbSourceEnum;
import com.example.utils.DateUtils;
import com.example.utils.ETLHelper;
import com.example.utils.FileOperationUtils;
import com.example.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("TomtomStrategy")
public class TomtomStrategy extends GeneralApiETLStrategy {
	
	@Value("${TESTTomtomRoad.selectAllsql}")
	private String selectSql;

	@Value("${TEST.api.key}")
	private String key;
	@Autowired
	@Qualifier("jdbcTemplateMap")
	private Map<DbSourceEnum, NamedParameterJdbcTemplate> jdbcTemplateMap;

	@Override
	public ExtractResult extract(ResourceInfo resourceInfo) throws ResourceException {
		try {
			// Step 1: Check resource available or not
			Date updateTime = new Date();
			Date srcUpdateTime = updateTime;
			JdbcTemplate jdbcTemplate = jdbcTemplateMap.get(DbSourceEnum.MY_SQL).getJdbcTemplate();
			List<TomtomRoad> list = jdbcTemplate.query(selectSql,
					new BeanPropertyRowMapper<TomtomRoad>(TomtomRoad.class)); 
			
			 
			List<DynamicInfo> dynamicInfoList = list.stream().map(s -> {
				
				String pointSeqVariable = Stream.of(
					s.getStartPointStr(),
					s.getMiddlePointStr(),
					s.getEndPointStr()
				)
				.filter(Objects::nonNull)
				.collect(Collectors.joining(":"));
				 
				
				
				DynamicInfo dynamicInfo = new DynamicInfo();
				String url = String.format(resourceInfo.getResource(), pointSeqVariable,key);
				log.info("Resource: `{}`", url);
				String jsonContent = null;
				try {
					jsonContent = JsonUtils.toJsonString(url);
					JsonNode jsonNode = JsonUtils.getMapper().readTree(jsonContent);
					JsonNode firstNode = jsonNode.get("routes").get(0).get("summary");
					Summary summary = JsonUtils.getMapper().readValue(firstNode.toString(), Summary.class);   
					dynamicInfo.setSectionID(s.getId());
					dynamicInfo.setTravelTime(summary.getTravelTimeInSeconds());
					dynamicInfo.setTravelSpeed((summary.getLengthInMeters()/1000.0)/(summary.getTravelTimeInSeconds()/3600.0));

				} catch (Exception e) {
					log.error("Error content: {}", jsonContent);
				}
				return dynamicInfo;

			}).collect(Collectors.toList());

			JsonNode jsonNode = JsonUtils.getMapper().valueToTree(dynamicInfoList);
			String recordsContent = jsonNode.toString();
			log.info("Resource fetch spent {}ms", System.currentTimeMillis() - updateTime.getTime());

			return ETLHelper.buildExtractResult(resourceInfo, recordsContent, srcUpdateTime, updateTime);
		} catch (ResourceNotUpdateException | ResourceFormatErrorException e) {
			throw e;
		} catch (Exception e) {
			throw new ResourceException(e);
		}
	}
	
}
