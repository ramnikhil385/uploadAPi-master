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
@EnableJpaRepositories(entityManagerFactoryRef = "preEntityManagerFactory", transactionManagerRef = "transactionManager",basePackages = {
		"com.optum.doc360.upload.database.pre_processor" })
public class PreProcessorDBConfig {

	@Bean
	@ConfigurationProperties(prefix = "preprocessor.datasource")
	public DataSourceProperties preDataSourceProperties() {
		return new DataSourceProperties();
	}

	/**
	 * Provider the DB data source.
	 * 
	 * @return HikariDataSource
	 */
	@Bean
	@ConfigurationProperties(prefix = "preprocessor.datasource.configuration")
	public HikariDataSource preDataSource() {
		return this.preDataSourceProperties().initializeDataSourceBuilder().type(HikariDataSource.class).build();
	}

	@Bean(name = "preEntityManagerFactory")
	public LocalContainerEntityManagerFactoryBean preEntityManagerFactory(EntityManagerFactoryBuilder builder,
			@Qualifier("preDataSource") DataSource uiDataSource) {
		return builder.dataSource(uiDataSource).packages("com.optum.doc360.upload.database.pre_processor")
				.persistenceUnit("preprocessor").build();
	}
	@Bean(name = "transactionManager")
	public PlatformTransactionManager rioTransactionManager(
			@Qualifier("preEntityManagerFactory") EntityManagerFactory preEntityManagerFactory) {
		return new JpaTransactionManager(preEntityManagerFactory);
	}
}
