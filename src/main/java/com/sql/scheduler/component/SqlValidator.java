package com.sql.scheduler.component;

import org.apache.calcite.sql.parser.SqlParseException;
import org.apache.calcite.sql.parser.SqlParser;

public class SqlValidator {
	/**
	 * 입력한 관계형 SQL평문 유효성 체크
	 * @param sql
	 * @return
	 */
	public static boolean isSQL(String sql) {
		SqlParser parser = SqlParser.create(sql, SqlParser.configBuilder().build());
		try {
			parser.parseStmt();
			return true;
		} catch (SqlParseException e) {
			return false;
		}
	}
}
