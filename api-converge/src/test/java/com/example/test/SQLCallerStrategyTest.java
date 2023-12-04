package com.example.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import java.util.stream.Stream;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.dao.mysql.ScheduleJobRepository;
import com.example.factory.SQLCallerStrategyFactory;
import com.example.model.dto.etl.ExtractResult;
import com.example.model.dto.etl.ResourceInfo;
import com.example.model.entity.mysql.JobExtraParam;
import com.example.model.entity.mysql.ScheduleJob;
import com.example.model.enums.DbSourceEnum;
import com.example.strategy.SQLCallerStrategy;

@SpringBootTest
@RunWith(SpringRunner.class)
public class SQLCallerStrategyTest {

	@Autowired
	private SQLCallerStrategyFactory sqlCallerStrategyFactory;
	@Autowired
	private ScheduleJobRepository scheduleJobRepository;
     // mssql測試用

	@Test
	public void testStrategy() throws Exception {
		
		String jobId = "dbfcf3752be2a37e4702a14f6dd42093"; 
		ScheduleJob scheduleJob = scheduleJobRepository.findByJobId(jobId);
		assertNotNull("ScheduleJob must not null", scheduleJob);
	
		JobExtraParam jobExtraParam = scheduleJob.getJobExtraParam();
		assertNotNull("JobExtraParam must not null", jobExtraParam);
		DbSourceEnum[] dbSources = Stream.of(jobExtraParam.getDbSources().split(",")).map(DbSourceEnum::fromName).toArray(DbSourceEnum[]::new);
		SQLCallerStrategy sqlCallerStrategy = sqlCallerStrategyFactory.getObject(scheduleJob.getJobStrategy());
	
		assertNotNull("ETLStrategy must not null", sqlCallerStrategy);
		// Prepare the resource info
		ResourceInfo resourceInfo = ResourceInfo.builder().resource(jobExtraParam.getResourceUrl())
				.targetTable(jobExtraParam.getLinkTable()).build();
		ExtractResult extractResult = sqlCallerStrategy.executeSP(resourceInfo);


	}
}
