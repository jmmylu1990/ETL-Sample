package com.example.strategy.impl;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.ReflectionUtils;

import com.example.component.ImpalaSqlStore;
import com.example.dao.mysql.ImpalaImportConfigRepository;
import com.example.exception.ImportException;
import com.example.model.entity.mysql.ImpalaImportConfig;
import com.example.model.enums.DbSourceEnum;
import com.example.strategy.FileImportStrategy;
import com.example.utils.HDFSUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@Qualifier("impalaFileImportRepository")
public class ImpalaFileImportStrategy implements FileImportStrategy {
	
	@Autowired
	private ImpalaSqlStore impalaSqlStore;
	@Autowired
	@Qualifier("jdbcTemplateMap")
	private Map<DbSourceEnum, NamedParameterJdbcTemplate> jdbcTemplateMap;
	
	@Autowired
	private ImpalaImportConfigRepository impalaImportConfigRepository;
	
	@Override
	public long loadData(File file, String schemaName, String tableName, boolean truncateFirst) throws ImportException {
		try {
			String targetTable = String.format("%s.%s", schemaName, tableName);
			ImpalaImportConfig impalaImportConfig = impalaImportConfigRepository.findByEnable(true).parallelStream()
				.filter(config -> config.getTargetTable().equals(targetTable))
				.findFirst()
				.orElseThrow(() -> new ImportException("No match impala import config found."));
			
			String hdfsPath = impalaImportConfig.getHdfsPath();
			String sqlKey = impalaImportConfig.getSqlKey();
			log.info("[{}] HDFS stage path remove result: {}", hdfsPath, HDFSUtils.remove(hdfsPath));
			HDFSUtils.mkdirs(hdfsPath); // Create destination path to prevent it not exit
			HDFSUtils.upload(file, hdfsPath, file.getName()); // Upload to destination path for external table that referenced
			JdbcTemplate jdbcTemplate = jdbcTemplateMap.get(DbSourceEnum.IMPALA).getJdbcTemplate();
			// If truncateFirst is assigned `true`, then truncate table before import
			if (truncateFirst) truncate(jdbcTemplate, targetTable);
			Field field = ReflectionUtils.findField(ImpalaSqlStore.class, sqlKey, String.class);
			Objects.requireNonNull(field); // filed object must be nonNull
			ReflectionUtils.makeAccessible(field);
			String[] sqls = field.get(impalaSqlStore).toString()
					.replace(":targetTable", targetTable)
					.split(";");
			
			return Stream.of(sqls)
					.map(String::trim)
					.mapToLong(sql -> {
						if (sql.startsWith("SELECT")) {
							return jdbcTemplate.queryForObject(sql, Long.class);
						}
						jdbcTemplate.execute(sql);
						return 0L;
					})
					.max()
					.orElse(-1L);
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
