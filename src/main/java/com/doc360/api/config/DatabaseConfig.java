package com.doc360.api.config;

import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;


public class DatabaseConfig {

	@Value("${rio.datasource.jdbcUrl}")
	String doc360BatchUrl;

	@Value("${rio.datasource.username}")
	String doc360BatchUserName;

	@Value("${rio.datasource.password}")
	String doc360BatchPwd;

	@Value("${rio.datasource.driver-class-name}")
	String doc360BatchDriverClassName;

	@Bean(name = "doc360Riobatch")
	@ConfigurationProperties(prefix = "rio.datasource")
	public SimpleDriverDataSource doc360BatchDataSource() throws SQLException {
		com.mysql.jdbc.Driver driver = new com.mysql.jdbc.Driver();
		return new SimpleDriverDataSource(driver, doc360BatchUrl, doc360BatchUserName, doc360BatchPwd);
	}

	@Bean(name = "doc360RioBatchJdbcTemplate")
	public JdbcTemplate getDoc360BatchTemplate(
			@Qualifier("doc360Riobatch") SimpleDriverDataSource simpleDriverDataSource) {
		return new JdbcTemplate(simpleDriverDataSource);
	}
}