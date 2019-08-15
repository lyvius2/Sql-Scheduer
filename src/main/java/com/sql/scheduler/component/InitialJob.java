package com.sql.scheduler.component;

import com.sql.scheduler.entity.TaskLog;
import com.sql.scheduler.repository.TaskLogRepository;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class InitialJob extends QuartzJobBean {
	private int interval;

	@Autowired
	private TaskLogRepository repository;

	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		Date date = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, interval * -1);
		date = cal.getTime();

		List<TaskLog> list = repository.findByTargetDataIsNotNullAndBeginTimeBefore(date);
		for (TaskLog log : list) {
			log.setTargetData(null);
			repository.save(log);
		}
	}

	public int getInterval() {
		return interval;
	}

	public void setInterval(int interval) {
		this.interval = interval;
	}
}
