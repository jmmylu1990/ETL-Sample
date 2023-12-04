package com.example.config;

import java.time.Duration;
import java.util.EnumMap;
import java.util.Map;

import org.locationtech.jts.geom.GeometryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.client.RestTemplate;

import com.example.model.enums.DbSourceEnum;

@EnableAsync
@Configuration
public class BaseConfig {

	private static final Logger LOGGER = LoggerFactory.getLogger(BaseConfig.class);
	
	@Bean(name = "geometryFactory")
	public GeometryFactory geometryFactory() {
		return new GeometryFactory();
	}
	
	@Bean(name = "restTemplate")
	public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
		return restTemplateBuilder
			.setConnectTimeout(Duration.ofSeconds(30))
			.setReadTimeout(Duration.ofMinutes(15))
			.build();
	}

	@Bean(name = "hdfsConfig")
	public org.apache.hadoop.conf.Configuration hdfsConfig() {
		org.apache.hadoop.conf.Configuration conf = new org.apache.hadoop.conf.Configuration();
		conf.addResource("hadoop/3.0/core-site.xml");
		conf.addResource("hadoop/3.0/hdfs-site.xml");
		LOGGER.info("fs.defaultFS={}", conf.get("fs.defaultFS"));
		
		return conf;
	}

	@Bean(name = "jdbcTemplateMap")
	public Map<DbSourceEnum, NamedParameterJdbcTemplate> jdbcTemplateMap() {
		return new EnumMap<>(DbSourceEnum.class);
	}
}
