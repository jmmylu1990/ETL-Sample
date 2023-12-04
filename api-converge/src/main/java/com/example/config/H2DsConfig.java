package com.example.config;

import com.example.utils.SqlUtils;
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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Map;
import java.util.stream.Stream;

/**
*   數據源相關配置 
*/
@Configuration
@ConditionalOnProperty(prefix = "spring.h2.datasource", name = "enabled", havingValue = "true")
@EnableTransactionManagement
@EnableJpaRepositories(
	entityManagerFactoryRef = "h2EntityManagerFactory",
	transactionManagerRef = "h2TransactionManager",
	basePackages = "com.example.dao.h2" // Repository class path
)
public class H2DsConfig {
	 
    /**
    *   JPA SQL 方言
    */
	@Value("${spring.h2.datasource.hibernate.dialect}")
    private String sqlDialect;
	
	/**
	 *  需要控管的 Entity 所在路徑 
	 */
	@Value("${spring.h2.datasource.hibernate.entity-packages}")
	private String[] entityPackages;

	@Bean(name = "h2DataSource")
	@ConfigurationProperties(prefix = "spring.h2.datasource")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }
    
    @Bean(name = "h2EntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory (
    		EntityManagerFactoryBuilder builder,
    		JpaProperties jpaProperties,
    		@Qualifier("h2DataSource") DataSource dataSource) {
    	
    	Map<String, String> properties = jpaProperties.getProperties();
        properties.put("hibernate.dialect", sqlDialect);
    	
        return builder.dataSource(dataSource)
                .properties(properties)
                .packages(entityPackages) // Entity class path
                .build();
    }

    @Bean(name = "h2TransactionManager")
    public PlatformTransactionManager transactionManager(@Qualifier("h2EntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
    
    @PostConstruct
    private void initDatabase() {
    	JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource());
    	Stream.of(SqlUtils.fromFile("/data.sql").split(";")).forEach(jdbcTemplate::execute);
    }

}
