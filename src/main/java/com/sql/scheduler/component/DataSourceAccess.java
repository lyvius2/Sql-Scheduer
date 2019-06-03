package com.sql.scheduler.component;

import com.sql.scheduler.code.DBMS;
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

	public void dataSourceInitialize(String url, String username, String password, DBMS dbms) {
		this.dataSource = new DriverManagerDataSource();
		((DriverManagerDataSource) this.dataSource).setUrl(url);
		((DriverManagerDataSource) this.dataSource).setUsername(username);
		((DriverManagerDataSource) this.dataSource).setPassword(password);
		((DriverManagerDataSource) this.dataSource).setDriverClassName(dbms.getDriverClassName());
		this.transactionManager = new DataSourceTransactionManager(this.dataSource);
		this.jdbcTemplate = new NamedParameterJdbcTemplate(this.dataSource);
	}

	public void connectionClose() {
		try {
			this.jdbcTemplate = null;
			this.transactionManager = null;
			this.dataSource.getConnection().close();
			this.dataSource = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
