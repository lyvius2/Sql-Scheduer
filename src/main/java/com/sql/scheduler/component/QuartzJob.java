package com.sql.scheduler.component;

import com.sql.scheduler.entity.Job;
import com.sql.scheduler.entity.JobGroup;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

@Slf4j
@Data
@Component
public class QuartzJob extends QuartzJobBean {
	private JobGroup jobsInfo;
	private String dbPassword;

	@Autowired
	private DataSourceAccess dataAccess;

	@Autowired
	private AES256 aes256;

	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		dataAccess.dataSourceInitialize(this.jobsInfo.getDbUrl(), this.jobsInfo.getDbUsername(), dbPassword, this.jobsInfo.getDbms());
		for (Job job : this.jobsInfo.getJob()) {
			log.info("Job Trigger Test : " + job.getTargetTable());
		}
		dataAccess.connectionClose();
	}
}
