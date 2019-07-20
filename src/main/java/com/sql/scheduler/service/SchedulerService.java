package com.sql.scheduler.service;

import com.sql.scheduler.component.Administrator;
import com.sql.scheduler.component.CronUtil;
import com.sql.scheduler.component.InitialJob;
import com.sql.scheduler.component.QuartzTriggerBuilder;
import com.sql.scheduler.entity.JobGroup;
import com.sql.scheduler.repository.AdminRepository;
import com.sql.scheduler.repository.JobGroupRepository;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class SchedulerService {
	@Autowired
	private Scheduler scheduler;

	@Autowired
	private QuartzTriggerBuilder triggerBuilder;

	@Autowired
	private JobGroupRepository jobGroupRepository;

	@Autowired
	private Administrator administrator;

	@PostConstruct
	public void start() throws Exception {
		log.info("========================================================");
		log.info("Batch Schedule Initializer");
		log.info("========================================================");
		scheduler.start();

		List<JobGroup> list = jobGroupRepository.findAllByUse("Y");
		for (JobGroup j : list) {
			if (j.getJob().size() > 0) startSchedule(j);
		}

		if (administrator.getDeleteTargetDataBeforeDay() > 0 &&
				CronUtil.cronParser(administrator.getDeleteTargetDataLogCron()) != null) {
			JobDetail initialJob = JobBuilder.newJob(InitialJob.class).withIdentity("cleaningJob").usingJobData("interval", administrator.getDeleteTargetDataBeforeDay()).build();
			Trigger initialTrigger = TriggerBuilder.newTrigger().withSchedule(CronScheduleBuilder.cronSchedule(administrator.getDeleteTargetDataLogCron())).build();
			scheduler.scheduleJob(initialJob, initialTrigger);
		}
	}

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
		if (trigger != null) scheduler.deleteJob(trigger.getJobKey());
	}
}
