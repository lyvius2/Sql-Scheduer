package com.sql.scheduler.config;

import com.sql.scheduler.component.Administrator;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.TimeZone;

@Configuration
public class QuartzConfig {
	@Autowired
	Administrator administrator;

	@Bean
	public JobDetail jobDetail() {
		return JobBuilder.newJob(QuartzJob.class).withIdentity("quartzJob").usingJobData("name", "world").storeDurably().build();
	}

	@Bean
	public Trigger jobTrigger() {
		CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(administrator.getDeleteTargetDataLogCron())
				.withMisfireHandlingInstructionFireAndProceed()
				.inTimeZone(TimeZone.getDefault());
		return TriggerBuilder.newTrigger().forJob(jobDetail()).withIdentity("quartzTrigger").withSchedule(scheduleBuilder).build();
	}
}
