package com.doc360.api.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.zaxxer.hikari.HikariDataSource;

/**
 * Define the UI DB configuration details.
 * 
 *
 */
@Configuration
//@EnableTransactionManagement
@EnableJpaRepositories( entityManagerFactoryRef = "uiEntityManagerFactory",basePackages = {
		"com.optum.doc360.upload.database.ui" })
public class UIDBConfig {

	/**
	 * Provide the UI data source configuration.
	 * 
	 * @return DataSourceProperties
	 */
	
	
	@Bean
	@Primary
	@ConfigurationProperties(prefix = "ui.datasource")
	public DataSourceProperties uiDataSourceProperties() {
		return new DataSourceProperties();
	}

	/**
	 * Provider the DB data source.
	 * 
	 * @return HikariDataSource
	 */
	@Bean
	@Primary
	@ConfigurationProperties(prefix = "ui.datasource.configuration")
	public HikariDataSource uiDataSource() {
		return this.uiDataSourceProperties().initializeDataSourceBuilder().type(HikariDataSource.class).build();
	}
	
	@Bean(name = "uiEntityManagerFactory")
	@Primary
	public LocalContainerEntityManagerFactoryBean uientityManagerFactory(EntityManagerFactoryBuilder builder,
			@Qualifier("uiDataSource") DataSource uiDataSource) {
		return builder.dataSource(uiDataSource).packages("com.optum.doc360.upload.database.ui")
				.persistenceUnit("ui").build();
	}

}
