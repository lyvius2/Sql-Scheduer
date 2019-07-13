package com.sql.scheduler.config;

import com.sql.scheduler.component.Administrator;
import com.sql.scheduler.entity.TaskLog;
import com.sql.scheduler.repository.TaskLogRepository;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper=false)
public class QuartzJob extends QuartzJobBean {
	private String name;

	@Autowired
	private Administrator administrator;

	@Autowired
	private TaskLogRepository repository;

	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		//System.out.println(String.format("Hello %s!", this.name));
		Date date = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, (administrator.getDeleteTargetDataBeforeDay()) * -1);
		date = cal.getTime();

		List<TaskLog> list = repository.findByTargetDataIsNotNullAndBeginTimeBefore(date);
		for (TaskLog log : list) {
			log.setTargetData(null);
			repository.save(log);
		}
	}
}
