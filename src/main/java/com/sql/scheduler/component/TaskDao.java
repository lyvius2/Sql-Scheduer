package com.sql.scheduler.component;

import com.google.gson.Gson;
import com.sql.scheduler.code.ResultStatus;
import com.sql.scheduler.entity.Job;
import com.sql.scheduler.entity.TaskLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.thymeleaf.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class TaskDao {
	@Autowired
	private DataSourceAccess dataAccess;

	@Autowired
	private Gson gson;

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

	public TaskLog executeTask(Job job, TaskLog taskLog) {
		NamedParameterJdbcTemplate jdbcTemplate = this.dataAccess.getJdbcTemplate();
		StringBuffer targetDataQuery = new StringBuffer();
		targetDataQuery.append("SELECT *\n");
		targetDataQuery.append("FROM ");
		targetDataQuery.append(job.getTargetTable() + "\n");
		targetDataQuery.append(job.getConditional());
		List<Map<String, Object>> targetList = jdbcTemplate.queryForList(targetDataQuery.toString(), new HashMap<>());
		taskLog.setTargetData(gson.toJson(targetList));
		taskLog.setTargetCount(targetList.size());

		String chkUpdateQuery = validateUpdateQuery(job, targetList.size());
		switch(chkUpdateQuery) {
			case "test":
				taskLog.setResultStatus(ResultStatus.NOTACT);
				taskLog.setErrorMsg("이 작업은 테스트 모드로 업데이트 쿼리가 실행되지 않습니다.");
				break;
			case "not.exist.target":
				taskLog.setResultStatus(ResultStatus.NOTACT);
				taskLog.setErrorMsg("대상 데이터가 없어 업데이트 쿼리를 실행하지 않습니다.");
				break;
			case "invalid.sql":
				taskLog.setResultStatus(ResultStatus.FAILURE);
				taskLog.setErrorMsg("업데이트 쿼리가 아닌 것으로 식별되어 쿼리가 실행되지 못했습니다.");
				break;
			default:
				DataSourceTransactionManager transactionManager = this.dataAccess.getTransactionManager();
				TransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
				TransactionStatus transactionStatus = transactionManager.getTransaction(transactionDefinition);

				StringBuffer updateQuery = new StringBuffer();
				updateQuery.append(job.getPerforming());
				updateQuery.append(job.getConditional());
				taskLog.setExecutedSql(updateQuery.toString());

				try {
					int result = jdbcTemplate.update(updateQuery.toString(), new HashMap<>());
					if (result > 0) {
						transactionManager.commit(transactionStatus);
						taskLog.setTargetCount(result);
						taskLog.setResultStatus(ResultStatus.SUCCESS);
					} else {
						transactionManager.rollback(transactionStatus);
						taskLog.setResultStatus(ResultStatus.FAILURE);
						taskLog.setErrorMsg("업데이트 쿼리 실행이 실패하여 Rollback 되었습니다.");
					}
				} catch (Exception e) {
					e.getStackTrace();
					transactionManager.rollback(transactionStatus);
					taskLog.setResultStatus(ResultStatus.ERROR);
					taskLog.setErrorMsg(e.toString());
				}
		}
		return taskLog;
	}

	private String validateUpdateQuery(Job job, int targetCount) {
		if (job.getTestMode().equals("Y")) return "test";
		if (targetCount == 0) return "not.exist.target";
		if (!StringUtils.isEmpty(job.getPerforming()) &&
				!StringUtils.isEmpty(job.getConditional()) &&
				StringUtils.contains(job.getConditional().toLowerCase(), "where ")) return "execute";
		else return "invalid.sql";
	}
}
