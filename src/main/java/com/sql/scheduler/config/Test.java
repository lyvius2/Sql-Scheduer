package com.sql.scheduler.config;

import org.quartz.*;
import org.springframework.stereotype.Component;

import java.util.TimeZone;

@Component
public class Test {

	public JobDetail buildJobDetail() {
		JobDataMap dataMap = new JobDataMap();
		dataMap.put("name", "Power up");
		return JobBuilder.newJob(QuartzJob.class).withIdentity("testJob").usingJobData(dataMap).storeDurably().build();
	}

	public Trigger buildTrigger() {
		CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule("*/4 * * * * ?")
				.withMisfireHandlingInstructionFireAndProceed()
				.inTimeZone(TimeZone.getDefault());
		return TriggerBuilder.newTrigger().withIdentity("testTrigger").withSchedule(scheduleBuilder).build();
	}

}
