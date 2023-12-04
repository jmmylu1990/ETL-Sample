package com.example.job;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

import com.example.component.AbstractJobExecutor;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ShellJobExecutor extends AbstractJobExecutor {

	@Override
	public void executiveCore(JobExecutionContext context) throws JobExecutionException {
		// TODO: Custom implemention
		log.info("This is a Shell executor");
	}
	
}
