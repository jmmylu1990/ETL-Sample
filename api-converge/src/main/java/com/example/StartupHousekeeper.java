package com.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.example.model.entity.base.AbstractScheduleJob;
import com.example.service.QuartzScheduleService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Profile({ "test", "prod" })
public class StartupHousekeeper {
	
	@Autowired
	private QuartzScheduleService<AbstractScheduleJob> quartzScheduleService;

	@EventListener(ContextRefreshedEvent.class)
	public void contextRefreshedEvent() {
		quartzScheduleService.listAllJob().parallelStream()
			.forEach(quartzScheduleService::dispatcher);
		
		log.info("Quartz job automatic initialization completely!");
	}
}