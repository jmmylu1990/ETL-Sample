package com.example.strategy.impl;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.exception.ImportException;
import com.example.model.ProgramConstant;
import com.example.model.enums.DbSourceEnum;
import com.example.strategy.FileImportStrategy;
import com.example.utils.FileOperationUtils;
import com.example.utils.StringTools;

@Repository
@Qualifier("mySqlFileImportRepository")
public class MysqlFileImportStrategy implements FileImportStrategy {
	
	@Value("${mysql.import.statement}")
	private String stmt;
	
	@Autowired
	@Qualifier("jdbcTemplateMap")
	private Map<DbSourceEnum, NamedParameterJdbcTemplate> jdbcTemplateMap;
	
	@Override
	public long loadData(File file, String schemaName, String tableName, boolean truncateFirst) throws ImportException {
		try {
			// Generate the file to correspond table name and set `null` to `\N` before import
			File importFile = file;
			String fileName = importFile.getName();
			String content = FileOperationUtils.extractContent(importFile, StandardCharsets.UTF_8.name()).replaceAll("(?<=,|\t)null(?=,|\t)", "\\\\N");
			String delimiter = StringTools.findFirstMatchSequence(content, "\t|,");
			JdbcTemplate jdbcTemplate = jdbcTemplateMap.get(DbSourceEnum.MY_SQL).getJdbcTemplate();
			if (!fileName.endsWith(ProgramConstant.MYSQL_IMPORT_FILE_SUFFIX)) {
				String importFilePath = file.getAbsoluteFile() + ProgramConstant.MYSQL_IMPORT_FILE_SUFFIX;
				importFile = FileOperationUtils.generateTextFile(new File(importFilePath), content, true);
			}
			// If truncateFirst is assigned `true`, then truncate table before import
			if (truncateFirst) truncate(jdbcTemplate, String.format("%s.%s", schemaName, tableName));
			// LOAD DATA LOCAL INFILE '%s' INTO TABLE %s.%s CHARACTER SET utf8 FIELDS TERMINATED BY '%s'  LINES TERMINATED BY '\n';
			int affectedCount = jdbcTemplate.update(String.format(stmt, importFile.getAbsolutePath().replace(File.separator, StringTools.SLASH), schemaName, tableName, delimiter));

			return affectedCount > 0 && FileOperationUtils.remove(importFile) ? affectedCount : -1;
		} catch (Exception e) {
			throw new ImportException(e);
		}
	}

	@Override
	public long loadData(File file, String table, boolean truncateFirst) throws ImportException {

		String schemaName = table.substring(0, table.indexOf('.'));
		String tableName = table.substring(table.indexOf('.') + 1);
		return this.loadData(file, schemaName, tableName, truncateFirst);
	}

}
