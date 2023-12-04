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
// spring.datasource.url=jdbc:oracle:thin:tts/taipei@192.168.130.52:1521:orcl
@Configuration
//@ConditionalOnProperty(prefix = "spring.oracle.datasource", name = "enabled", havingValue = "true")
@ConditionalOnProperty(prefix = "spring.oracle.datasource", name = "enabled", havingValue = "true")
@EnableTransactionManagement
@EnableJpaRepositories(
		entityManagerFactoryRef = "oracleEntityManagerFactory",
		transactionManagerRef = "oracleTransactionManager",
		basePackages = "com.example.dao.oracle" // Repository class path
)
public class OracleDsConfig {
	
	@Value("${spring.oracle.datasource.hibernate.dialect}")
    private String oracleDsDialect;
    
	@Bean(name = "oracleDataSource")
	@ConfigurationProperties(prefix = "spring.oracle.datasource")
    public DataSource oracleDataSource() {
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }
    
    @Bean(name = "oracleEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory (
    		EntityManagerFactoryBuilder builder,
    		JpaProperties jpaProperties,
    		 @Qualifier("oracleDataSource") DataSource oracleDataSource,
    		 @Qualifier("jdbcTemplateMap") Map<DbSourceEnum, NamedParameterJdbcTemplate> jdbcTemplateMap) {
    	
    	jdbcTemplateMap.put(DbSourceEnum.ORACLE, new NamedParameterJdbcTemplate(oracleDataSource));
    	Map<String, String> properties = jpaProperties.getProperties();
        properties.put("hibernate.dialect", oracleDsDialect);
       
        
        return builder.dataSource(oracleDataSource)
                .properties(properties)
                .packages("com.example.model.entity.oracle") // Entity class path
                .build();
    }

    @Bean(name = "oracleTransactionManager")
    public PlatformTransactionManager transactionManager(
    		@Qualifier("oracleEntityManagerFactory") EntityManagerFactory oracleEntityManagerFactory) {
        return new JpaTransactionManager(oracleEntityManagerFactory);
    }

}
