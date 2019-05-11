package com.sql.scheduler.service;

import com.sql.scheduler.config.Test;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
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
	private Test test;

	public void startSchedule() throws SchedulerException {
		JobDetail jobDetail = test.buildJobDetail();
		Trigger trigger = test.buildTrigger();
		Set<Trigger> triggers = new HashSet<>();
		triggers.add(trigger);
		scheduler.scheduleJob(jobDetail, triggers, false);
	}
}
