package com.sql.scheduler.service;

import com.sql.scheduler.component.QuartzTriggerBuilder;
import com.sql.scheduler.entity.JobGroup;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
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
		Trigger trigger = triggerBuilder.buildTrigger(group.getCron(), group.getGroupSeq());
		Set<Trigger> triggers = new HashSet<>();
		triggers.add(trigger);
		scheduler.scheduleJob(jobDetail, triggers, true);
	}

	public void stopSchedule(JobGroup group) throws SchedulerException {
		Set<TriggerKey> triggerKeys = scheduler.getTriggerKeys(GroupMatcher.triggerGroupEquals(Scheduler.DEFAULT_GROUP));
		Trigger trigger = null;
		for (TriggerKey key : triggerKeys) {
			if (key.getName().equals(String.format("%sTrigger", group.getGroupSeq()))) {
				trigger = scheduler.getTrigger(key);
				break;
			}
		}
		scheduler.deleteJob(trigger.getJobKey());
	}
}
