package com.sql.scheduler.component;

import org.apache.calcite.sql.parser.SqlParseException;
import org.apache.calcite.sql.parser.SqlParser;

public class SqlValidator {
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
