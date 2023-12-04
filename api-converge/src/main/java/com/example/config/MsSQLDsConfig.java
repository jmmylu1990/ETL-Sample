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
@ConditionalOnProperty(prefix = "spring.mssql.datasource", name = "enabled", havingValue = "true")
@EnableTransactionManagement
@EnableJpaRepositories(
		entityManagerFactoryRef = "mssqlEntityManagerFactory",
		transactionManagerRef = "mssqlTransactionManager",
		basePackages = "com.example.dao.mssql" // Repository class path
)
public class MsSQLDsConfig {
	
	@Value("${spring.mssql.datasource.hibernate.dialect}")
    private String mssqlDsDialect;
    
	@Bean(name = "mssqlDataSource")
	@ConfigurationProperties(prefix = "spring.mssql.datasource")
    public DataSource mssqlDataSource() {
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }
    
    @Bean(name = "mssqlEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean mssqlEntityManagerFactory(
    		EntityManagerFactoryBuilder builder,
    		JpaProperties jpaProperties,
    		@Qualifier("mssqlDataSource") DataSource mssqlDataSource,
    		@Qualifier("jdbcTemplateMap") Map<DbSourceEnum, NamedParameterJdbcTemplate> jdbcTemplateMap) {
    	
    	jdbcTemplateMap.put(DbSourceEnum.MS_SQL, new NamedParameterJdbcTemplate(mssqlDataSource));
    	Map<String, String> properties = jpaProperties.getProperties();
    	properties.put("hibernate.dialect", mssqlDsDialect);
    	
        return builder.dataSource(mssqlDataSource)
                .properties(properties)
                .packages("com.example.model.entity.mssql") // Entity class path
                .build();
    }

    @Bean(name = "mssqlTransactionManager")
    PlatformTransactionManager mssqlTransactionManager(
    		@Qualifier("mssqlEntityManagerFactory") EntityManagerFactory mssqlEntityManagerFactory) {
        return new JpaTransactionManager(mssqlEntityManagerFactory);
    }

}
