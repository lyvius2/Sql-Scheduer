package com.sql.scheduler.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class TestDao {
	@Autowired
	private DataSourceAccess dataAccess;

	public int count() {
		NamedParameterJdbcTemplate jdbcTemplate = this.dataAccess.getJdbcTemplate();
		HashMap<String, Object> paramMap = new HashMap<>();
		paramMap.put("up_cd", "NAT");
		return jdbcTemplate.queryForObject("SELECT count(*) FROM code WHERE up_cd = :up_cd", paramMap, Integer.class);
	}
}
