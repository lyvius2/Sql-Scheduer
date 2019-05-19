package com.sql.scheduler.service;

import com.sql.scheduler.component.DataSourceAccess;
import com.sql.scheduler.component.TestDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExecuteJobService {
	@Autowired
	private DataSourceAccess dataAccess;

	@Autowired
	private TestDao dao;

	public int executeJob(String url, String username, String password) {
		this.dataAccess.dataSourceInitialize(url, username, password);
		return dao.count();
	}
}
