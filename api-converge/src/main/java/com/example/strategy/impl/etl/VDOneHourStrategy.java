package com.example.strategy.impl.etl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import com.example.model.enums.DbSourceEnum;
import com.example.utils.SqlUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("VDOneHourStrategy")
public class VDOneHourStrategy extends GeneralApiETLStrategy{
	
	@Value("${vdOneHour.insertVDOneHour}")
	private String insertSQL;

	@Autowired
	@Qualifier("jdbcTemplateMap")
	private Map<DbSourceEnum, NamedParameterJdbcTemplate> jdbcTemplateMap;
	
	@Override
	public void successCallback() {
		log.info("DbSource: {} / Procedure: {}", DbSourceEnum.MY_SQL, insertSQL);
		JdbcTemplate jdbcTemplate = jdbcTemplateMap.get(DbSourceEnum.MY_SQL).getJdbcTemplate();
		jdbcTemplate.execute(SqlUtils.clean(insertSQL));

	}

}
