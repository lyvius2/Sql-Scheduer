package com.sql.scheduler.config;

import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzConfig {
	@Bean
	public JobDetail jobDetail() {
		return JobBuilder.newJob(QuartzJob.class).withIdentity("quartzJob").usingJobData("name", "world").storeDurably().build();
	}

	@Bean
	public Trigger jobTrigger() {
		SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(2).repeatForever();
		return TriggerBuilder.newTrigger().forJob(jobDetail()).withIdentity("jobTrigger").withSchedule(scheduleBuilder).build();
	}
}
