package com.example.listener;

import java.util.Date;
import java.util.Objects;

import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.listeners.JobListenerSupport;

import com.example.exception.ResourceException;
import com.example.model.JobDataMapConstant;
import com.example.model.ProgramConstant;
import com.example.model.entity.base.AbstractScheduleJob;
import com.example.utils.DateUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RetryableJobListener extends JobListenerSupport {

	private static final String FALLING_JOBS_GROUP = "FailingJobsGroup";
	
    @Override
    public String getName() {
        return ProgramConstant.JOB_LISTENER_NAME;
    }

    @Override
    public void jobToBeExecuted(JobExecutionContext context) {
    }

    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
        Scheduler scheduler = context.getScheduler();
        JobDataMap jobDataMap = context.getMergedJobDataMap();
        AbstractScheduleJob scheduleJob = (AbstractScheduleJob) jobDataMap.get(JobDataMapConstant.SCHEDULE_JOB);
        String jobName = scheduleJob.getJobName();
        int refireCount = jobDataMap.containsKey(JobDataMapConstant.FIRE_INDEX) ? 
        		jobDataMap.getInt(JobDataMapConstant.FIRE_INDEX) : 0;
        long refireInterval = scheduleJob.getRefireInterval();
        long refireMaxCount = scheduleJob.getRefireMaxCount();
        try {
			if (Objects.nonNull(jobException) && jobException.getCause() instanceof ResourceException && refireCount < refireMaxCount) {
	        	jobDataMap.put(JobDataMapConstant.FIRE_INDEX, refireCount + 1);
	        	// The schedule will be refired later
	        	long refireIntervalMs = refireInterval * 1000L;
				Date nextFireTimeWhenFailure = new Date(System.currentTimeMillis() + refireIntervalMs);
	        	String afterTimeDesc = DateUtils.formatUsageTime(refireIntervalMs);
				log.info("排程【{}】第{}次執行介接失敗! 將於{}({}後)重新執行", jobName, refireCount + 1, nextFireTimeWhenFailure, afterTimeDesc);
	        	JobKey jobKey = JobKey.jobKey(String.format("%s[%d]", jobName, refireCount), FALLING_JOBS_GROUP);
	        	// Remove the temp job if it existed
	        	if (scheduler.checkExists(jobKey)) scheduler.deleteJob(jobKey);
	        	// Create the new one
	        	JobDetail job = context.getJobDetail()
	    				.getJobBuilder()
	    				// to track the number of retries
	    				.withIdentity(jobKey)
	    				.usingJobData(jobDataMap)
	    				.build();
				Trigger trigger = TriggerBuilder.newTrigger()
	    				.forJob(job)
	    				.withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionNowWithExistingCount())
	    				// trying to reduce back pressure, you can use another algorithm
	    				.startAt(nextFireTimeWhenFailure)
	    				.build();
				scheduler.scheduleJob(job, trigger);
			}
        } catch (SchedulerException e) {
			log.error(e.getMessage(), e);
		}
    }
	
}