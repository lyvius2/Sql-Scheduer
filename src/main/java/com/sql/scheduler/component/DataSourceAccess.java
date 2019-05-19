package com.sql.scheduler.component;

import lombok.Data;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Data
@Component
public class DataSourceAccess {

	private DataSource dataSource;
	private DataSourceTransactionManager transactionManager;
	private NamedParameterJdbcTemplate jdbcTemplate;

	public void dataSourceInitialize(String url, String username, String password) {
		this.dataSource = new DriverManagerDataSource();
		((DriverManagerDataSource) this.dataSource).setUrl(url);
		((DriverManagerDataSource) this.dataSource).setUsername(username);
		((DriverManagerDataSource) this.dataSource).setPassword(password);
		//((DriverManagerDataSource) this.dataSource).setDriverClassName("net.sf.log4jdbc.sql.jdbcapi.DriverSpy");
		((DriverManagerDataSource) this.dataSource).setDriverClassName("org.postgresql.Driver");
		this.transactionManager = new DataSourceTransactionManager(this.dataSource);
		this.jdbcTemplate = new NamedParameterJdbcTemplate(this.dataSource);
	}

	public void connectionClose() {
		this.jdbcTemplate = null;
		this.transactionManager = null;
		this.dataSource = null;
	}
}
