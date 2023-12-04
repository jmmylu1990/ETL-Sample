package com.example.config;

import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.example.model.enums.DbSourceEnum;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
@ConditionalOnProperty(prefix = "spring.impala.datasource", name = "enabled", havingValue = "true")
@EnableTransactionManagement
@EnableJpaRepositories(
		entityManagerFactoryRef = "impalaEntityManagerFactory",
		transactionManagerRef = "impalaTransactionManager",
		basePackages = "com.example.dao.impala"
)
public class ImpalaDsConfig {

	@Value("${spring.impala.datasource.hibernate.dialect}")
    private String impalaDsDialect;

	@Bean(name = "impalaDataSource")
	@ConfigurationProperties(prefix = "spring.impala.datasource")
	public DataSource dataSource() {
		return DataSourceBuilder.create().type(HikariDataSource.class).build();
	}

	@Bean(name = "impalaEntityManagerFactory")
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(
			EntityManagerFactoryBuilder builder,
			JpaProperties jpaProperties,
			@Qualifier("impalaDataSource") DataSource impalaDataSource,
			@Qualifier("jdbcTemplateMap") Map<DbSourceEnum, NamedParameterJdbcTemplate> jdbcTemplateMap) {

		jdbcTemplateMap.put(DbSourceEnum.IMPALA, new NamedParameterJdbcTemplate(impalaDataSource));
		Map<String, String> properties = jpaProperties.getProperties();
    	properties.put("hibernate.dialect", impalaDsDialect);

		return builder.dataSource(impalaDataSource)
				.packages("com.example.model.impala")
				.properties(properties)
				.build();
	}

	@Bean(name = "impalaTransactionManager")
	public PlatformTransactionManager transactionManager(
			@Qualifier("impalaEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
		return new JpaTransactionManager(entityManagerFactory);
	}

	@Bean(name = "impalaJdbcTemplate")
	public NamedParameterJdbcTemplate impalaJdbcTemplateMap(@Qualifier("impalaDataSource") DataSource impalaDataSource) {
		return new NamedParameterJdbcTemplate(impalaDataSource);
	}
}
