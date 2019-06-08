package com.sql.scheduler.code;

public enum DBMS {
	POSTGRESQL("PostgreSQL", "org.postgresql.Driver"),
	MSSQL("MS SQL Server", "net.sf.log4jdbc.sql.jdbcapi.DriverSpy"),
	ORACLE("Oracle DB", "net.sf.log4jdbc.sql.jdbcapi.DriverSpy"),
	MYSQL("MySQL/MariaDB", "com.mysql.jdbc.Driver");

	private String dbms;
	private String driverClassName;

	DBMS(String dbms, String driverClassName) {
		this.dbms = dbms;
		this.driverClassName = driverClassName;
	}

	public String getDbms() {
		return dbms;
	}

	public String getDriverClassName() {
		return driverClassName;
	}
}
