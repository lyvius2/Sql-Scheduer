package com.sql.scheduler.config;

import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class DataSourceBean {

	private DataSource dataSource;

	public DataSource init(String driverClassName, String url, String username, String password) {
		this.dataSource = new DriverManagerDataSource();
		((DriverManagerDataSource) this.dataSource).setDriverClassName(driverClassName);
		((DriverManagerDataSource) this.dataSource).setUrl(url);
		((DriverManagerDataSource) this.dataSource).setUsername(username);
		((DriverManagerDataSource) this.dataSource).setPassword(password);
		return this.dataSource;
	}

	public DataSourceTransactionManager setTran() {
		if (this.dataSource != null) return new DataSourceTransactionManager(this.dataSource);
		return null;
	}
}
