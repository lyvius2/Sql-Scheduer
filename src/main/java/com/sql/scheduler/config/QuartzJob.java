package com.sql.scheduler.config;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

@Data
@EqualsAndHashCode(callSuper=false)
public class QuartzJob extends QuartzJobBean {

	private String name;

	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		//System.out.println(String.format("Hello %s!", this.name));
	}
}
