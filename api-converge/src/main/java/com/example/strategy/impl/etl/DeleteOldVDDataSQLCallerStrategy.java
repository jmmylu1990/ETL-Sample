package com.example.strategy.impl.etl;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import com.example.exception.ResourceException;
import com.example.exception.ResourceFormatErrorException;
import com.example.exception.ResourceNotUpdateException;
import com.example.factory.FileImportStrategyFactory;
import com.example.model.dto.etl.ExtractResult;
import com.example.model.dto.etl.ResourceInfo;
import com.example.model.dto.source.other.OilPrice;
import com.example.model.enums.DbSourceEnum;
import com.example.strategy.SQLCallerStrategy;
import com.example.utils.DateUtils;
import com.example.utils.ETLHelper;
import com.example.utils.JsonUtils;
import com.example.utils.SqlUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("DeleteOldVDDataSQLCallerStrategy")
public class DeleteOldVDDataSQLCallerStrategy implements SQLCallerStrategy {

	@Value("${deleteOldVDData.deleteVD}")
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
