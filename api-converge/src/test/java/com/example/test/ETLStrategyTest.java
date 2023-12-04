package com.example.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import java.io.File;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import com.example.dao.mysql.ScheduleJobRepository;
import com.example.factory.ETLStrategyFactory;
import com.example.model.dto.etl.EncapsulationFile;
import com.example.model.dto.etl.ExtractResult;
import com.example.model.dto.etl.ImportResult;
import com.example.model.dto.etl.ResourceInfo;
import com.example.model.dto.etl.TransformResult;
import com.example.model.entity.mysql.JobExtraParam;
import com.example.model.entity.mysql.ScheduleJob;
import com.example.model.enums.DbSourceEnum;
import com.example.strategy.ETLStrategy;
import com.example.utils.FileOperationUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class ETLStrategyTest {
	
//	@Value("${root.path}")
//	private String rootPath;
//	private String rootPath = "D:\\ETL-Result\\KHH-CPT";
	private String rootPath = "Z:\\TEST";
	@Autowired
	private ETLStrategyFactory etlStrategyFactory;
	@Autowired
	private ScheduleJobRepository scheduleJobRepository;
	
	@Test
	public void testStrategy() throws Exception {
		String jobId = "6f9a47e604769e9110d465c164227a95"; // Use it to change etl process
		ScheduleJob scheduleJob = scheduleJobRepository.findByJobId(jobId);
		assertNotNull("ScheduleJob must not null", scheduleJob);
		JobExtraParam jobExtraParam = scheduleJob.getJobExtraParam();
		assertNotNull("JobExtraParam must not null", jobExtraParam);
		DbSourceEnum[] dbSources = Stream.of(jobExtraParam.getDbSources().split(",")).map(DbSourceEnum::fromName).toArray(DbSourceEnum[]::new);
		ETLStrategy etlStrategy = etlStrategyFactory.getObject(scheduleJob.getJobStrategy());
		assertNotNull("ETLStrategy must not null", etlStrategy);
		// Prepare the resource info
		String relativePath = jobExtraParam.getRelativePath();
		File outputPath = new File(rootPath, relativePath);
		// Remove output before test every time
		FileOperationUtils.remove(outputPath);
		ResourceInfo resourceInfo = ResourceInfo.builder()
				.resource(jobExtraParam.getResourceUrl())
				.rootPath(rootPath)
				.relativePath(relativePath)
				.modelClass(Class.forName(jobExtraParam.getClassName()))
				.targetTable(jobExtraParam.getLinkTable())
				.build();
		ExtractResult extractResult = etlStrategy.extract(resourceInfo);
		// Transform the resource to new data
		TransformResult transformResult = etlStrategy.transform(resourceInfo, extractResult);
		long srcDataCount = transformResult.getImportFileMap().values().stream().mapToLong(EncapsulationFile::getLineCount).sum();
		Map<DbSourceEnum, ImportResult> importResultMap = etlStrategy.load(transformResult, jobExtraParam.isClearFirst(), dbSources);
		log.debug("{}", importResultMap);
		Collection<ImportResult> importResults = importResultMap.values();
		long totalImportCount = importResults.stream()
				.mapToLong(ImportResult::getImportCount)
				.sum();
		log.info("Source: {} / Load: {}", srcDataCount, totalImportCount);
		etlStrategy.successCallback(); // Execute callback after `load` step
		// The source data count and import data count must be matched
		assertThat(srcDataCount).isPositive();
		assertThat(totalImportCount).isPositive();
		assertThat(srcDataCount).isEqualTo(totalImportCount);
	}
}
