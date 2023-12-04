package com.example.job;

import java.util.Date;
import java.util.List;

import org.quartz.CronExpression;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.example.component.AbstractJobExecutor;
import com.example.component.ApiRewriteComponent;
import com.example.dao.mysql.ScheduleJobRepository;
import com.example.model.JobDataMapConstant;
import com.example.model.dto.NotifyArgument;
import com.example.model.entity.base.AbstractScheduleJob;
import com.example.model.entity.mysql.JobNotifyUser;
import com.example.model.entity.mysql.ScheduleJob;
import com.example.model.enums.ETLResultEnum;
import com.example.model.enums.JobResultEnum;
import com.example.service.interfaces.NotifyService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ApiTriggerJobExecutor extends AbstractJobExecutor {

	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private ScheduleJobRepository scheduleJobRepository;
	
	@Autowired
	private NotifyService notifyService;
	@Autowired
	private ApiRewriteComponent apiRewriteComponent;
	
	@Override
	public void executiveCore(JobExecutionContext context) throws JobExecutionException {
		log.info("This is a API-Trigger executor");
		Date executeTime = new Date();
		JobDataMap jobDataMap = context.getMergedJobDataMap();
		AbstractScheduleJob abstractScheduleJob = (AbstractScheduleJob) jobDataMap.get(JobDataMapConstant.SCHEDULE_JOB);
		ScheduleJob scheduleJob = scheduleJobRepository.findByJobId(abstractScheduleJob.getJobId());
		List<JobNotifyUser> jobNotifyUsers = scheduleJob.getJobNotifyUsers();
		int errorAccumulation = scheduleJob.getErrorAccumulation();
		String apiUrl = apiRewriteComponent.normalize(scheduleJob.getJobStrategy());
		
		try {
			scheduleJob.setLastFireTime(executeTime);
			scheduleJob.setLastResult(JobResultEnum.EXECUTING);
			scheduleJob.setNextFireTime(new CronExpression(scheduleJob.getCronExpression()).getNextValidTimeAfter(executeTime));
			scheduleJobRepository.save(scheduleJob); // Update scheduleJob state first
			
			// Fetch api and log response
			String response = restTemplate.getForObject(apiUrl, String.class);
			log.info("`{}` response: {}", apiUrl, response);
			scheduleJob.setErrorAccumulation(0);
			scheduleJob.setLastResult(JobResultEnum.SUCCESS);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			// Set error accumulation
			scheduleJob.setLastResult(JobResultEnum.FAIL);
			scheduleJob.setErrorAccumulation(errorAccumulation + 1);
			
			// Notify if error
			notifyService.failedNotify(
				NotifyArgument.builder()
					.jobName(scheduleJob.getJobName())
					.resourceUrl(apiUrl)
					.processIndex(0)
					.apistatus(ETLResultEnum.UNKNOWN_ERROR)
					.exception(e)
					.errorAccumulation(++errorAccumulation)
					.notifyMails(this.fetchVaildNotifyMails(jobNotifyUsers, ETLResultEnum.RESOURCE_FORMAT_ERROR))
					.build()
			);
		} finally {
			Date completeTime = new Date();
			scheduleJob.setLastCompleteTime(completeTime);
			scheduleJobRepository.save(scheduleJob);
		}
	}

}
