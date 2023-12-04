package com.example.strategy.impl.etl;

import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import com.example.exception.ResourceException;
import com.example.model.dto.etl.ExtractResult;
import com.example.model.dto.etl.ResourceInfo;
import com.example.model.enums.DbSourceEnum;
import com.example.strategy.SQLCallerStrategy;
import com.example.utils.SqlUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("VDOneHourSQLCallerStrategyForNFB")
public class VDOneHourSQLCallerStrategyForNFB implements SQLCallerStrategy {

	@Value("${vdOneHourForNFB.insertVDOneHour}")
	private String insertSQL;

	@Autowired
	@Qualifier("jdbcTemplateMap")
	private Map<DbSourceEnum, NamedParameterJdbcTemplate> jdbcTemplateMap;

	@Override
	public ExtractResult executeSP(ResourceInfo resourceInfo) throws ResourceException {
		// TODO Auto-generated method stub
		Date updateTime = new Date();
		String resource = resourceInfo.getResource();
		log.info("Resource: `{}`", resource);

		log.info("DbSource: {} / Procedure: {}", DbSourceEnum.MY_SQL, insertSQL);
		JdbcTemplate jdbcTemplate = jdbcTemplateMap.get(DbSourceEnum.MY_SQL).getJdbcTemplate();
		jdbcTemplate.execute(SqlUtils.clean(insertSQL));
		log.info("ExecuteSQL fetch spent {}ms", System.currentTimeMillis() - updateTime.getTime());
		return ExtractResult.builder().updateTime(updateTime).build();
	}

}
