package com.doc360.api.config;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.zaxxer.hikari.HikariDataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(entityManagerFactoryRef = "rioEntityManagerFactory", transactionManagerRef = "rioTransactionManager", basePackages = {
		"com.optum.doc360.upload.database.rio" })
public class RioDBConfig {
	
	@Bean
	@ConfigurationProperties(prefix = "rio.datasource")
	public DataSourceProperties rioDataSourceProperties() {
		return new DataSourceProperties();
	}

	/**
	 * Provider the DB data source.
	 * 
	 * @return HikariDataSource
	 */
	@Bean
	@ConfigurationProperties(prefix = "rio.datasource.configuration")
	public HikariDataSource rioDataSource() {
		return this.rioDataSourceProperties().initializeDataSourceBuilder().type(HikariDataSource.class).build();
	}
	
	@Bean(name = "rioEntityManagerFactory")
	public LocalContainerEntityManagerFactoryBean rioEntityManagerFactory(EntityManagerFactoryBuilder builder,
			@Qualifier("rioDataSource") DataSource uiDataSource) {
		return builder.dataSource(uiDataSource).packages("com.optum.doc360.upload.database.rio")
				.persistenceUnit("rio").build();
	}
	@Bean(name = "rioTransactionManager")
	public PlatformTransactionManager rioTransactionManager(
			@Qualifier("rioEntityManagerFactory") EntityManagerFactory rioEntityManagerFactory) {
		return new JpaTransactionManager(rioEntityManagerFactory);
	}
	
}
