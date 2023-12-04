package com.example.job;

import java.util.Date;
import java.util.List;

import org.quartz.CronExpression;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.component.AbstractJobExecutor;
import com.example.component.ApiRewriteComponent;
import com.example.dao.mysql.ScheduleJobRepository;
import com.example.dao.mysql.ScheduleLogRepository;
import com.example.exception.ResourceException;
import com.example.exception.ResourceFormatErrorException;
import com.example.exception.ResourceNotUpdateException;
import com.example.factory.SQLCallerStrategyFactory;
import com.example.model.JobDataMapConstant;
import com.example.model.dto.NotifyArgument;
import com.example.model.dto.NotifyArgument.NotifyArgumentBuilder;
import com.example.model.dto.etl.ResourceInfo;
import com.example.model.entity.base.AbstractScheduleJob;
import com.example.model.entity.mysql.JobExtraParam;
import com.example.model.entity.mysql.JobNotifyUser;
import com.example.model.entity.mysql.ScheduleJob;
import com.example.model.entity.mysql.ScheduleLog;
import com.example.model.enums.ApiFormatEnum;
import com.example.model.enums.ETLResultEnum;
import com.example.model.enums.JobResultEnum;
import com.example.service.interfaces.NotifyService;
import com.example.strategy.SQLCallerStrategy;
import com.example.utils.DateUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@DisallowConcurrentExecution
public class SQLJobExecutor extends AbstractJobExecutor {
	@Autowired
	private SQLCallerStrategyFactory sqlCallerStrategyFactory;
	
	@Autowired
	private ScheduleJobRepository scheduleJobRepository;
	@Autowired
	private ScheduleLogRepository scheduleLogRepository;
	
	@Autowired
	private NotifyService notifyService;
	@Autowired
	private ApiRewriteComponent apiRewriteComponent;
	@Override
	public void executiveCore(JobExecutionContext context) throws JobExecutionException {
		JobDataMap jobDataMap = context.getMergedJobDataMap();
		AbstractScheduleJob abstractScheduleJob = (AbstractScheduleJob) jobDataMap.get(JobDataMapConstant.SCHEDULE_JOB);
		ScheduleJob scheduleJob = scheduleJobRepository.findByJobId(abstractScheduleJob.getJobId());
		scheduleJob.setLastFireTime(new Date());
		scheduleJobRepository.save(scheduleJob); // Update last fire time at first
		jobDataMap.put(JobDataMapConstant.SCHEDULE_JOB, scheduleJob);
        Date executeTime = (Date) jobDataMap.getOrDefault(JobDataMapConstant.EXECUTION_TIME, new Date());
        Date infoDate = DateUtils.parseStrToDate(DateUtils.today());
        int refireCount = (Integer) jobDataMap.getOrDefault(JobDataMapConstant.FIRE_INDEX, 0);
		JobExtraParam jobExtraParam = scheduleJob.getJobExtraParam();
		scheduleJob.setLastResult(JobResultEnum.FAIL); // Set job result fail at first
		List<JobNotifyUser> jobNotifyUsers = scheduleJob.getJobNotifyUsers();
		String jobStrategy = scheduleJob.getJobStrategy();
		String jobName = scheduleJob.getJobName();

		// Rewrite resource
		String resourceNormalized = apiRewriteComponent.normalize(jobExtraParam.getResourceUrl());
		String resource = apiRewriteComponent.rewrite(resourceNormalized, ApiFormatEnum.TEXT);
		int refireMaxCount = scheduleJob.getRefireMaxCount();
		int errorAccumulation = scheduleJob.getErrorAccumulation();
		
		// Instantiate the schedule log
		ScheduleLog scheduleLog = new ScheduleLog();
		scheduleLog.setJobId(scheduleJob.getJobId());
		scheduleLog.setRefireFlag(refireCount);
		scheduleJob.setLastFireTime(executeTime);
		NotifyArgumentBuilder notifyArgBuilder = NotifyArgument.builder()
				.jobName(jobName)
				.resourceUrl(resource)
				.processIndex(refireCount);
		try {
			// Get the valid next execute fire time and set next schedule execute time
			scheduleJob.setNextFireTime(new CronExpression(scheduleJob.getCronExpression()).getNextValidTimeAfter(executeTime));
			SQLCallerStrategy sqlCallerStrategy = sqlCallerStrategyFactory.getObject(jobStrategy);
			// Prepare the resource info
			ResourceInfo resourceInfo = ResourceInfo.builder()
					.resource(resource)
					.build();
			// Extract the resource
			scheduleLog.setProcessTime(new Date());
			sqlCallerStrategy.executeSP(resourceInfo);
			// Calculate the source data count
			long srcDataCount = 0;
			scheduleLog.setSrcDataCount(srcDataCount);
			// Import the data with file that is transformed
			scheduleLog.setImportTime(new Date());
			// Set schedule job result
			errorAccumulation = 0;
			scheduleJob.setLastResult(JobResultEnum.SUCCESS);
			// Set schedule log result 
			scheduleLog.setImportDataCount(srcDataCount);
			scheduleLog.setResult(ETLResultEnum.SUCCESS);
			notifyService.successNotify(
				notifyArgBuilder
					.notifyMails(this.fetchVaildNotifyMails(jobNotifyUsers, ETLResultEnum.SUCCESS))
					.build()
			);
		} catch (ResourceFormatErrorException e) {
			log.error(e.getMessage(), e);
			scheduleLog.setResult(ETLResultEnum.RESOURCE_FORMAT_ERROR);
			scheduleLog.setCallbackMsg(e.getMessage());
			notifyService.failedNotify(
				notifyArgBuilder
					.apistatus(ETLResultEnum.RESOURCE_FORMAT_ERROR)
					.exception(e)
					.errorAccumulation(++errorAccumulation)
					.notifyMails(this.fetchVaildNotifyMails(jobNotifyUsers, ETLResultEnum.RESOURCE_FORMAT_ERROR))
					.build()
			);
		} catch (ResourceException e) {
			log.warn(e.getMessage(), e);
			boolean srcNotUpdate = ResourceNotUpdateException.class.isAssignableFrom(e.getClass());
			scheduleLog.setResult(srcNotUpdate ? ETLResultEnum.RESOURCE_NOT_UPDATE : ETLResultEnum.RESOURCE_UNAVAILABLE);
			scheduleLog.setCallbackMsg(e.getMessage());
			// Check the refire count reach the limit or not
			// If reach the limit, then send the mail
			if (refireCount >= refireMaxCount) {
				// Set last result for ScheduleJob
				errorAccumulation += refireMaxCount;
				log.info("排程【{}】錯誤累積次數: {}", jobName, errorAccumulation);
				notifyArgBuilder.exception(e).errorAccumulation(errorAccumulation);
				if (srcNotUpdate) {
					scheduleJob.setLastResult(JobResultEnum.SUCCESS);
					notifyService.noUpdateNotify(
						notifyArgBuilder
							.apistatus(ETLResultEnum.RESOURCE_NOT_UPDATE)
							.notifyMails(this.fetchVaildNotifyMails(jobNotifyUsers, ETLResultEnum.RESOURCE_NOT_UPDATE))
							.build()
					);
				} else {
					scheduleJob.setLastResult(JobResultEnum.FAIL);
					notifyService.failedNotify(
						notifyArgBuilder
							.apistatus(ETLResultEnum.RESOURCE_UNAVAILABLE)
							.notifyMails(this.fetchVaildNotifyMails(jobNotifyUsers, ETLResultEnum.RESOURCE_UNAVAILABLE))
							.build()
					);
				}
			} else {
				Date nextRetryTime = DateUtils.addSeconds(new Date(), scheduleJob.getRefireInterval());
				scheduleJob.setNextFireTime(nextRetryTime);
				scheduleJob.setLastResult(JobResultEnum.PENDING);
				throw new JobExecutionException(e);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			scheduleLog.setCallbackMsg(e.getMessage());
			notifyService.failedNotify(
				notifyArgBuilder
					.apistatus(ETLResultEnum.UNKNOWN_ERROR)
					.exception(e)
					.errorAccumulation(++errorAccumulation)
					.notifyMails(this.fetchVaildNotifyMails(jobNotifyUsers, ETLResultEnum.RESOURCE_FORMAT_ERROR))
					.build()
			);
		} finally {
			// Must stored the schedule job result in last
			// Set last schedule complete time
			Date completeTime = new Date();
			scheduleJob.setLastCompleteTime(completeTime);
			// Set error accumulation
			scheduleJob.setErrorAccumulation(errorAccumulation);
			// Set complete time and infoDate in schedule log
			scheduleLog.setCompleteTime(completeTime);
			scheduleLog.setInfoDate(infoDate);
			scheduleJobRepository.save(scheduleJob);
			// Save the job log
			scheduleLogRepository.save(scheduleLog);
		}
	
	}
	
}
