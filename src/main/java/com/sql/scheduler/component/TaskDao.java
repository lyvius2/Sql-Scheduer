package com.sql.scheduler.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.thymeleaf.util.StringUtils;

import java.sql.SQLException;
import java.util.HashMap;

@Slf4j
@Component
public class TaskDao {
	@Autowired
	private DataSourceAccess dataAccess;

	public int count(String targetTable, String conditional) {
		NamedParameterJdbcTemplate jdbcTemplate = this.dataAccess.getJdbcTemplate();
		StringBuffer countQuery = new StringBuffer();
		countQuery.append("SELECT count(*)\n");
		countQuery.append("FROM ");
		countQuery.append(targetTable);
		countQuery.append("\n");
		countQuery.append(conditional);
		log.info("타겟팅 : " + targetTable);
		return jdbcTemplate.queryForObject(countQuery.toString(), new HashMap<>(), Integer.class);
	}

	public void execute(String performing, String conditional) {
		NamedParameterJdbcTemplate jdbcTemplate = this.dataAccess.getJdbcTemplate();
		if (!StringUtils.isEmpty(performing) &&
				!StringUtils.isEmpty(conditional) &&
				StringUtils.contains(conditional.toLowerCase(), "where ")) {
			DataSourceTransactionManager transactionManager = this.dataAccess.getTransactionManager();
			TransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
			TransactionStatus transactionStatus = transactionManager.getTransaction(transactionDefinition);

			StringBuffer updateQuery = new StringBuffer();
			updateQuery.append(performing);
			updateQuery.append(conditional);

			try {
				int result = jdbcTemplate.update(updateQuery.toString(), new HashMap<>());
				if (result > 0) {
					log.info("업데이트 성공");
					transactionManager.commit(transactionStatus);
				} else {
					log.info("업데이트 실패");
					transactionManager.rollback(transactionStatus);
				}
			} catch (Exception e) {
				e.getStackTrace();
				log.error(e.toString());
				log.info("업데이트 실패");
				transactionManager.rollback(transactionStatus);
			}
		}
	}
}
