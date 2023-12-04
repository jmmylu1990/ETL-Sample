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
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.example.model.enums.DbSourceEnum;

@Configuration
@ConditionalOnProperty(prefix = "spring.mysql.datasource", name = "enabled", havingValue = "true")
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "mysqlEntityManagerFactory",
        transactionManagerRef = "mysqlTransactionManager",
        basePackages = "com.example.dao.mysql" // Repository class path
)
public class MySQLDsConfig {
	
	@Value("${spring.mysql.datasource.hibernate.dialect}")
    private String mysqlDsDialect;
    
	@Primary
	@Bean(name = "mysqlDataSource")
	@ConfigurationProperties(prefix = "spring.mysql.datasource")
	public DataSource mysqlDataSource() {
		return DataSourceBuilder.create().build();
	}

	@Primary
    @Bean(name = "mysqlEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean mysqlEntityManagerFactory (
    		JpaProperties jpaProperties,
    		EntityManagerFactoryBuilder builder,
    		@Qualifier("mysqlDataSource") DataSource mysqlDataSource,
    		@Qualifier("jdbcTemplateMap") Map<DbSourceEnum, NamedParameterJdbcTemplate> jdbcTemplateMap) {

    	jdbcTemplateMap.put(DbSourceEnum.MY_SQL, new NamedParameterJdbcTemplate(mysqlDataSource));
    	Map<String, String> properties = jpaProperties.getProperties();
    	properties.put("hibernate.dialect", mysqlDsDialect);
    	
        return builder.dataSource(mysqlDataSource)
                .properties(properties)
                .packages("com.example.model.entity.mysql") // Entity class path
                .build();
    }

	@Primary
    @Bean(name = "mysqlTransactionManager")
    public PlatformTransactionManager mysqlTransactionManager(
    		@Qualifier("mysqlEntityManagerFactory") EntityManagerFactory mysqlEntityManagerFactory) {
        return new JpaTransactionManager(mysqlEntityManagerFactory);
    }

	@Primary
    @Bean(name = "mysqlJdbcTemplate")
  	public NamedParameterJdbcTemplate mysqlJdbcTemplateMap(@Qualifier("mysqlDataSource") DataSource mysqlDataSource) {
  		return new NamedParameterJdbcTemplate(mysqlDataSource);
  	}
    
}
