package com.example.strategy.impl;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;

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
@Qualifier("msSqlFileImportRepository")
public class MssqlFileImportStrategy implements FileImportStrategy {
	
	@Value("${root.path}")
	private String rootPath;
	@Value("${mssql.path:#{null}}")
	private String mssqlPath;
	@Value("${mssql.import.statement}")
	private String stmt;
	
	@Autowired
	@Qualifier("jdbcTemplateMap")
	private Map<DbSourceEnum, NamedParameterJdbcTemplate> jdbcTemplateMap;
	
	@Override
	public long loadData(File file, String schemaName, String tableName, boolean truncateFirst) throws ImportException {
		try {
			File importFile = file;
			String fileName = importFile.getName();
			String content = FileOperationUtils.extractContent(importFile, StandardCharsets.UTF_8.name()).replace("null", "");
			String delimiter = StringTools.findFirstMatchSequence(content, "\t|,");
			String importFileLocation = importFile.getParent();
			String importFileLocationForMssql = Objects.isNull(mssqlPath) ? importFileLocation : importFileLocation.replace(File.separator, StringTools.SLASH).replace(rootPath, mssqlPath);
			JdbcTemplate jdbcTemplate = jdbcTemplateMap.get(DbSourceEnum.MS_SQL).getJdbcTemplate();
			if (!fileName.endsWith(ProgramConstant.MSSQL_IMPORT_FILE_SUFFIX)) {
				String importFilePath = file.getAbsoluteFile() + ProgramConstant.MSSQL_IMPORT_FILE_SUFFIX;
				importFile = FileOperationUtils.generateTextFile(importFilePath, content, true);
				fileName = importFile.getName();
			}
			// If truncateFirst is assigned `true`, then truncate table before import
			if (truncateFirst) truncate(jdbcTemplate, String.format("%s.%s", schemaName, tableName));
			// BULK INSERT %s.%s FROM '%s' WITH (CODEPAGE='65001', FIELDTERMINATOR = '%s', ROWTERMINATOR = '0x0a', MAXERRORS = 0, KEEPNULLS);
			int affectedCount = jdbcTemplate.update(String.format(stmt, schemaName, tableName, new File(importFileLocationForMssql, fileName), delimiter));
			
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
