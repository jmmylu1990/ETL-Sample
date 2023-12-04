package com.example.strategy.impl;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.jdbc.support.rowset.SqlRowSetMetaData;
import org.springframework.stereotype.Repository;

import com.google.common.io.Files;
import com.example.exception.ImportException;
import com.example.model.enums.DbSourceEnum;
import com.example.strategy.FileImportStrategy;
import com.example.utils.ClassUtils;
import com.example.utils.DateUtils;
import com.example.utils.SqlUtils;
import com.example.utils.StringTools;

@Repository
@Qualifier("oracleFileImportRepository")
public class OracleFileImportStrategy implements FileImportStrategy {

	private static final Logger LOGGER = LoggerFactory.getLogger(OracleFileImportStrategy.class);
	
	@Autowired
	@Qualifier("jdbcTemplateMap")
	private Map<DbSourceEnum, NamedParameterJdbcTemplate> jdbcTemplateMap;

	@Override
	public long loadData(File file, String schemaName, String tableName, boolean truncateFirst) throws ImportException {
		try {
			String targetTable = SqlUtils.clean(String.format("%s.%s", schemaName, tableName));
			JdbcTemplate jdbcTemplate = jdbcTemplateMap.get(DbSourceEnum.ORACLE).getJdbcTemplate();
			List<String> lines = Files.readLines(file, StandardCharsets.UTF_8);
			String delimiter = StringTools.findFirstMatchSequence(lines.get(0), "\t|,");
			// If truncateFirst is assigned `true`, then truncate table before import
			if (truncateFirst) truncate(jdbcTemplate, targetTable);
			// Get table metaData
			SqlRowSet rowSet = jdbcTemplate.queryForRowSet(String.format("SELECT * FROM %s WHERE ROWNUM = 1", targetTable));
			SqlRowSetMetaData metaData = rowSet.getMetaData();
			// Prepare insert script and insert values
			final int lineCount = lines.size();
			final int batchSize = lineCount > 3 ? lineCount / 3 : lineCount;
			final String insertSql = this.insertSqlBuilder(targetTable, metaData);
			LOGGER.info("[{}] JDBC insert batch size: `{}`", targetTable, batchSize);
			int[][] effectedCounts = jdbcTemplate.batchUpdate(insertSql, lines, batchSize, (ps, line) -> this.bindValues(metaData, ps, line.split(delimiter)));
			
			// Since Statement executed SUCCESS_NO_INFO is `-2` and EXECUTE_FAILED is `-3`, 
			// so that we need to determine the result is `-2` or not 
			long importCount = Arrays.stream(effectedCounts)
					.mapToLong(counts -> Arrays.stream(counts).reduce(0, (accur, curr) -> accur + (curr == Statement.SUCCESS_NO_INFO ? 1 : 0)))
					.sum();
			LOGGER.info("[{}] JDBC insert data row: `{}`", targetTable, importCount);
			return importCount;
		} catch (IOException e) {
			throw new ImportException(e);
		}
	}
	
	@Override
	public long loadData(File file, String table, boolean truncateFirst) throws ImportException {
		String schemaName = table.substring(0, table.indexOf('.'));
		String tableName = table.substring(table.indexOf('.') + 1);
		return this.loadData(file, schemaName, tableName, truncateFirst);
	}
	
	private String insertSqlBuilder(String table, SqlRowSetMetaData metaData) {
		final String insertSqlTemplate = "INSERT INTO %s (%s) values(%s)";
		List<String> columnList = IntStream.rangeClosed(1, metaData.getColumnCount())
				.mapToObj(metaData::getColumnName)
				.collect(Collectors.toList());
		String insertValuePlaceholder = columnList.stream().map(column -> "?").collect(Collectors.joining(StringTools.COMMA)); 
		return String.format(
				insertSqlTemplate, 
				table, 
				String.join(StringTools.COMMA, columnList),
				insertValuePlaceholder
		);
	}
	
	private void bindValues(SqlRowSetMetaData metaData, PreparedStatement ps, String[] values) throws SQLException {
		AtomicInteger indexCounter = new AtomicInteger(0); 
		while (indexCounter.incrementAndGet() <= metaData.getColumnCount()) {
			int index = indexCounter.get();
			int columnType = metaData.getColumnType(index);
			String value = values[index - 1]; // Since PreparedStatement set placeholder value begin with 1
			if (!ClassUtils.isValid(value)) {
				ps.setObject(index, null);
				continue;
			}
			switch (columnType) {
			case Types.BOOLEAN:
				ps.setBoolean(index, Boolean.parseBoolean(value));
				break;
			case Types.TINYINT:
			case Types.SMALLINT:
			case Types.INTEGER:
				ps.setInt(index, Integer.parseInt(value));
				break;
			case Types.BIGINT:
				ps.setBigDecimal(index, new BigDecimal(value));
				break;
			case Types.FLOAT:
				ps.setFloat(index, Float.parseFloat(value));
				break;
			case Types.DECIMAL:
			case Types.DOUBLE:
				ps.setDouble(index, Double.parseDouble(value));
				break;
			case Types.CHAR:
			case Types.VARCHAR:
			case Types.NVARCHAR:
			case Types.CLOB:
				ps.setString(index, value);
				break;
			case Types.DATE:
				ps.setDate(index, new Date(DateUtils.parseStrToDate(value).getTime()));
				break;
			case Types.TIME:
				ps.setTime(index, new Time(DateUtils.parseStrToDate(value).getTime()));
				break;
			case Types.TIMESTAMP:
				ps.setTimestamp(index, new Timestamp(DateUtils.parseStrToDate(value).getTime()));
				break;
			default:
				ps.setObject(index, value);
				break;
			}
		}
	}
}
