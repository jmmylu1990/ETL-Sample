package com.example.test;

import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.dao.mysql.ScheduleJobRepository;
import com.example.model.entity.mysql.ScheduleJob;
import com.example.utils.DateUtils;
import com.example.utils.ETLHelper;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TaskTest {

	@Autowired
	private ScheduleJobRepository scheduleJobRepository;
	
	@Test
	public void testCompress() {
		String basePath = "D:\\ETL-Result";
		String jobId = "32721FFE-FA4C-4F10-84BF-1C45CED8FBFF"; // Use it to change etl process
		ScheduleJob scheduleJob = scheduleJobRepository.findByJobId(jobId);
		Date processDate = DateUtils.addDays(new Date(), -8);
		boolean compressResult = ETLHelper.compressETLResult(scheduleJob, basePath, processDate, true);
		
		assertTrue(compressResult);
	}
}
