package com.sql.scheduler.service;

import com.sql.scheduler.component.QuartzTriggerBuilder;
import com.sql.scheduler.entity.JobGroup;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class SchedulerService {
	@Autowired
	private Scheduler scheduler;

	@Autowired
	private QuartzTriggerBuilder triggerBuilder;

	public void startSchedule(JobGroup group) throws Exception {
		JobDetail jobDetail = triggerBuilder.buildJobDetail(group);
		Trigger trigger = triggerBuilder.buildTrigger(group.getCron(), group.getGroupName());
		Set<Trigger> triggers = new HashSet<>();
		triggers.add(trigger);
		scheduler.scheduleJob(jobDetail, triggers, true);
	}
}
