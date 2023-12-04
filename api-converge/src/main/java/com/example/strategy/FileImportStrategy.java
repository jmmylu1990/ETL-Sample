package com.example.strategy;

import java.io.File;

import org.springframework.jdbc.core.JdbcTemplate;

import com.example.exception.ImportException;
import com.example.utils.SqlUtils;

public interface FileImportStrategy {

	public long loadData(File file, String table, boolean truncateFirst) throws ImportException;
	
	public long loadData(File file, String schemaName, String tableName, boolean truncateFirst) throws ImportException;

	default int truncate(JdbcTemplate jdbcTemplate, String targetTable) {
		return jdbcTemplate.update("TRUNCATE TABLE " + SqlUtils.clean(targetTable));
	}
}
