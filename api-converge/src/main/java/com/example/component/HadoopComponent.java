package com.example.component;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.exception.ImportException;
import com.example.model.enums.DbSourceEnum;
import com.example.utils.ClassUtils;
import com.example.utils.DateUtils;
import com.example.utils.FileOperationUtils;
import com.example.utils.StringTools;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class HadoopComponent {
	
	@Value("${root.path}")
	private String rootPath;
	@Value("${root.backup.path}")
	private String rootBackupPath;
	@Value("${hdfs.path}")
	private String hdfsPath;
	
	@Autowired
	@Qualifier("hdfsConfig")
	private Configuration conf;
	@Autowired
	private ImpalaSqlStore impalaSqlStore;
	
	@Autowired
	@Qualifier("jdbcTemplateMap")
	private Map<DbSourceEnum, NamedParameterJdbcTemplate> jdbcTemplateMap;
	
	@Transactional(rollbackFor = Exception.class)
	public boolean putFilesToHDFSAndImport(List<File> fileList, String sqls) throws ImportException {
		log.info("-- Establishing HDfS Connection --");
		boolean isDone = false;
		try (FileSystem fs = FileSystem.newInstance(conf)) {
			for (File file : fileList) {
				String localFilePath = file.getParent();
				String fileName = file.getName();
				String tableName = fileName.substring(0, fileName.lastIndexOf('_'));
				String infoDate = DateUtils.formatRebuild(DateUtils.DASHED_DATE_FORMAT, StringTools.findFirstMatchSequence(fileName, "\\d{8}"));
				Path localFile = new Path(localFilePath, fileName);
				File localBackupFile = new File(file.getAbsolutePath().replace(rootPath, rootBackupPath));
				if (!localBackupFile.getParentFile().exists()) {
					localBackupFile.getParentFile().mkdirs();
				} else if (localBackupFile.exists()) {
					FileOperationUtils.remove(localBackupFile);
				}
				Path hdfsFilePath = new Path(localFilePath.replace(rootPath, hdfsPath));
				if (!fs.exists(hdfsFilePath)) fs.mkdirs(hdfsFilePath);
				// Move the file to external file storage
				// Copy file from local disk to HDFS
				log.info("-- HDFS I/O begin --");
				long hdfsStartTime = System.currentTimeMillis();
				log.info("From: [{}] --> To: [{}]", localFile, hdfsFilePath);
				fs.copyFromLocalFile(localFile, hdfsFilePath);
				String hdfsIOCostTime = DateUtils.formatUsageTime(System.currentTimeMillis() - hdfsStartTime);
				log.info("-- HDFS I/O finished cost time: {} --", hdfsIOCostTime);
				Thread.sleep(3000L); // Wait 3 seconds to avoid Disk IO exception
				// Execute sql statements
				log.info("-- Import sql begin --");
				long sqlStartTime = System.currentTimeMillis();
				log.info("Table: {} / HDFS path: {} / InfoDate: {}", tableName, hdfsFilePath, infoDate);
				sqls = String.format(sqls, tableName, hdfsFilePath, infoDate);
				this.executeImpalaSqls(sqls.split(";"));
				String sqlExecCostTimeDesc = DateUtils.formatUsageTime(System.currentTimeMillis() - sqlStartTime);
				log.info("-- Import sql finished cost time: {} --", sqlExecCostTimeDesc);
				FileUtils.moveFile(file, localBackupFile);
				isDone = localBackupFile.exists();
				log.info("[{}] Local file backup result: {}", fileName, isDone);
			}
		} catch (Exception e) {
			throw new ImportException(e);
		}

		return isDone;
	}
	
	public String getSqlBySourceType(String sourcType) {
		try {
			Method method = ImpalaSqlStore.class.getMethod(String.format("get%s", StringTools.upperFirstCase(sourcType)));
			
			return method.invoke(impalaSqlStore).toString();
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			log.error(e.getMessage(), e);
		}
		
		return null;
	}

	public void executeImpalaSqls(String... sqls) {
		JdbcTemplate jdbcTemplate = jdbcTemplateMap.get(DbSourceEnum.IMPALA).getJdbcTemplate();
		Arrays.stream(sqls).map(String::trim)
			.filter(ClassUtils::isValid)
			.forEachOrdered(jdbcTemplate::execute);
	}

}
