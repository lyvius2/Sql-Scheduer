package com.sql.scheduler.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
public class DbConfig {

	@Bean(name = "jobDataSource")
	public DataSource dataSource() {
		DataSource dataSource = new DriverManagerDataSource();
		((DriverManagerDataSource) dataSource).setDriverClassName("net.sf.log4jdbc.sql.jdbcapi.DriverSpy");
		return dataSource;
	}

	@Bean(name = "jobTransactionManager")
	public DataSourceTransactionManager transactionManager() {
		return new DataSourceTransactionManager(dataSource());
	}

	@Bean(name = "jobJdbcTemplete")
	public NamedParameterJdbcTemplate jdbcTemplate() {
		return new NamedParameterJdbcTemplate(dataSource());
	}
}
