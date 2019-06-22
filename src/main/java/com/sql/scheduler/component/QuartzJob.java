package com.sql.scheduler.component;

import com.sql.scheduler.entity.Job;
import com.sql.scheduler.entity.JobGroup;
import com.sql.scheduler.service.TaskService;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@Slf4j
@Data
@EqualsAndHashCode(callSuper=false)
@Component
public class QuartzJob extends QuartzJobBean {
	private JobGroup jobsInfo;
	private String dbPassword;

	@Autowired
	private DataSourceAccess dataAccess;

	@Autowired
	private TaskService service;

	@Autowired
	private TaskDao taskDao;

	@Autowired
	private AES256 aes256;

	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		LocalDateTime beginDate = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		log.info("===============================================================");
		log.info(jobsInfo.getGroupName() + " 작업 시작 - " + beginDate.format(formatter));
		log.info("===============================================================");
		if (this.jobsInfo.getJob() != null) {
			dataAccess.dataSourceInitialize(this.jobsInfo.getDbUrl(), this.jobsInfo.getDbUsername(), dbPassword, this.jobsInfo.getDbms());
			for (Job job : this.jobsInfo.getJob()) {
				if (service.countByUnagreedCase(job.getJobSeq()) == 0) {
					int targetCount = taskDao.count(job.getTargetTable(), job.getConditional());
					log.info("작업 대상 ROWS : " + targetCount);
					if (job.getTestMode().equals("Y")) log.info("* 이 작업은 테스트 모드로 업데이트 쿼리가 수행되지 않습니다. *");
					if (targetCount > 0 && job.getTestMode().equals("N")) {
						log.info("데이터 업데이트를 수행합니다...");
						taskDao.execute(job.getPerforming(), job.getConditional());
					}
				}
			}
			dataAccess.connectionClose();
		} else {
			log.info("작업이 없습니다.");
		}
		LocalDateTime endDate = LocalDateTime.now();
		log.info("===============================================================");
		log.info(jobsInfo.getGroupName() + " 작업 완료 - " + endDate.format(formatter) + " (소요시간: " + (ChronoUnit.MILLIS.between(beginDate, endDate)) + " ms)");
		log.info("===============================================================");
	}
}
