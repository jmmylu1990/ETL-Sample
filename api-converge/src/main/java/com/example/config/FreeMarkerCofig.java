package com.example.config;

import org.springframework.context.annotation.Bean;

import freemarker.template.Configuration;

@org.springframework.context.annotation.Configuration
public class FreeMarkerCofig {

	@Bean
	public Configuration freeMarkerConfig() {
		return new Configuration(freemarker.template.Configuration.VERSION_2_3_28);
	}
	
}
