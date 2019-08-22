package com.sql.scheduler.component;

import com.sql.scheduler.entity.JobGroup;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.TimeZone;

@Component
public class QuartzTriggerBuilder {
	@Autowired
	private AES256 aes256;

	/**
	 * 스케줄링 작업 생성
	 * @param jobGroup
	 * @return
	 * @throws Exception
	 */
	public JobDetail buildJobDetail(JobGroup jobGroup) throws Exception {
		JobDataMap dataMap = new JobDataMap();
		dataMap.put("jobsInfo", jobGroup);
		dataMap.put("dbPassword", aes256.AESDecoder(jobGroup.getDbPassword()));
		return JobBuilder.newJob(QuartzJob.class).withIdentity(String.format("%sJob", jobGroup.getGroupSeq())).usingJobData(dataMap).storeDurably().build();
	}

	/**
	 * 스케줄링 트리거 생성
	 * @param cron
	 * @param groupSeq
	 * @return
	 */
	public Trigger buildTrigger(String cron, int groupSeq) {
		CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cron)
				.withMisfireHandlingInstructionFireAndProceed()
				.inTimeZone(TimeZone.getDefault());
		return TriggerBuilder.newTrigger().withIdentity(String.format("%sTrigger", groupSeq)).withSchedule(scheduleBuilder).build();
	}
}
