package com.example.component;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import com.example.factory.YamlPropertySourceFactory;

import lombok.Data;

@Component
@PropertySource(value = "classpath:datasource.sql.yml", factory = YamlPropertySourceFactory.class)
@ConfigurationProperties(prefix = "impala.sql")
public @Data class ImpalaSqlStore {

	private String checkMissingInfoDate;

	private String general;

	private String hinetCvpRtData;
	
}
