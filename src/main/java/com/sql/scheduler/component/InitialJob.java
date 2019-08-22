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

/**
 * 애플리케이션 구동 시 기존 저장되어 있던 작업 리스케줄링
 */
public class InitialJob extends QuartzJobBean {
	private int interval;

	@Autowired
	private TaskLogRepository repository;

	/**
	 * 설정한 날짜 이전의 데이터 백업본 삭제
	 * @param context
	 * @throws JobExecutionException
	 */
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
