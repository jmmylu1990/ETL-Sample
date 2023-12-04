package com.example.strategy.impl;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.jdbc.support.rowset.SqlRowSetMetaData;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import com.example.exception.ImportException;
import com.example.model.enums.DbSourceEnum;
import com.example.strategy.FileImportStrategy;
import com.example.utils.CMDUtils;
import com.example.utils.DateUtils;
import com.example.utils.FileOperationUtils;
import com.example.utils.SqlUtils;
import com.example.utils.StringTools;

import freemarker.template.Configuration;
import freemarker.template.TemplateException;

//@Repository
//@Qualifier("oracleFileImportRepository")
public class OldOracleFileImportStrategy implements FileImportStrategy {

	private static final Logger LOGGER = LoggerFactory.getLogger(OldOracleFileImportStrategy.class);
	
	@Value("${oracle.import.statement}")
	private String oracleImportStmt;
	
	@Autowired
	@Qualifier("jdbcTemplateMap")
	private Map<DbSourceEnum, NamedParameterJdbcTemplate> jdbcTemplateMap;
	@Autowired
	private Configuration freeMarkerConfig;

	@Override
	public long loadData(File file, String schemaName, String tableName, boolean truncateFirst) throws ImportException {
		freeMarkerConfig.setClassForTemplateLoading(this.getClass(), "/templates/sql/oracle");
		try {
			String targetTable = SqlUtils.clean(String.format("%s.%s", schemaName, tableName));
			JdbcTemplate jdbcTemplate = jdbcTemplateMap.get(DbSourceEnum.ORACLE).getJdbcTemplate();
			String content = FileOperationUtils.extractContent(file, StandardCharsets.UTF_8.name());
			String delimiter = StringTools.findFirstMatchSequence(content, "\t|,");
			// If truncateFirst is assigned `true`, then truncate table before import
			if (truncateFirst) truncate(jdbcTemplate, targetTable);
			SqlRowSet rowSet = jdbcTemplate.queryForRowSet(String.format("SELECT * FROM %s WHERE ROWNUM = 1", targetTable));
			Map<String, Object> params = new HashMap<>();
			params.put("filePath", file.getCanonicalPath());
			params.put("table", targetTable);
			params.put("delimiter", delimiter);
			params.put("columnSpec", this.getColumnSpec(rowSet.getMetaData()));
			String templateContent = FreeMarkerTemplateUtils.processTemplateIntoString(freeMarkerConfig.getTemplate("FileImportCtl.sftl"), params);
			String ctlPath = "./oracle-ctl/";
			String fileName = String.format("%s_%s.ctl", tableName, DateUtils.now(DateUtils.SIMPLE_DATETIME_FORMAT));
			File ctlFile = new File(ctlPath, fileName);
			File logFile = new File(ctlPath, fileName.replaceAll("(\\.\\w+)+$",  ".log"));
			File badFile = new File(ctlPath, fileName.replaceAll("(\\.\\w+)+$",  ".bad"));
			FileOperationUtils.generateTextFile(ctlFile, templateContent);
			String execStmt = String.format(oracleImportStmt, ctlFile.getCanonicalPath(), logFile.getCanonicalPath(), badFile.getCanonicalPath());
			LOGGER.info("SQL*Loader execute script: {}", execStmt);
			CMDUtils.exec(execStmt); // Use CMDUtils to execute sqlldr statement
			Long loadCount = jdbcTemplate.queryForObject(String.format("SELECT COUNT(*) FROM %s", targetTable), Long.class);
			// If import successfuly, there will not generate the bad file so that the log file and ctl file can be removed
			if (!badFile.exists() && loadCount > 0) FileOperationUtils.removeMultiple(ctlFile, logFile);
			
			return loadCount;
		} catch (IOException | TemplateException e) {
			throw new ImportException(e);
		}
	}
	
	@Override
	public long loadData(File file, String table, boolean truncateFirst) throws ImportException {
		String schemaName = table.substring(0, table.indexOf('.'));
		String tableName = table.substring(table.indexOf('.') + 1);
		return this.loadData(file, schemaName, tableName, truncateFirst);
	}
	
	private String getColumnSpec(SqlRowSetMetaData metaData) {
		return IntStream.rangeClosed(1, metaData.getColumnCount()).mapToObj(i -> {
			String columnName = metaData.getColumnName(i);
			int columnType = metaData.getColumnType(i);
			int length = metaData.getColumnDisplaySize(i);
			switch (columnType) {
			case Types.CHAR:
			case Types.VARCHAR:
				return String.format("%s CHAR(%d) NULLIF(%1$s='null')", columnName, length);
			case Types.TIMESTAMP:
				return String.format("%s TIMESTAMP 'YYYY-MM-DD HH24:MI:SS' NULLIF(%1$s='null')", columnName);
			case Types.DATE:
				return String.format("%s DATE 'YYYY-MM-DD' NULLIF(%1$s='null')", columnName);
			case Types.TIME:
				return String.format("%s TIME 'HH24:MI:SS' NULLIF(%1$s='null')", columnName);
			case Types.CLOB:
				return String.format("%s CHAR(10000) NULLIF(%1$s='null')", columnName);
			default:
				return String.format("%s NULLIF(%1$s='null')", columnName);
			}
		}).collect(Collectors.joining(StringTools.COMMA + StringTools.LF + StringTools.TAB));
	}
}
