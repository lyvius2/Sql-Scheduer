package com.sql.scheduler.component;

import com.sql.scheduler.entity.Job;
import com.sql.scheduler.entity.JobGroup;
import com.sql.scheduler.entity.TaskLog;
import com.sql.scheduler.service.AdminService;
import com.sql.scheduler.service.TaskService;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Slf4j
@Data
@EqualsAndHashCode(callSuper=false)
@Component
public class QuartzJob extends QuartzJobBean {
	private JobGroup jobsInfo;
	private String dbPassword;

	@Autowired
	private Administrator administrator;

	@Autowired
	private TemplateEngine templateEngine;

	@Autowired
	private EmailSender sender;

	@Autowired
	private DataSourceAccess dataAccess;

	@Autowired
	private TaskService service;

	@Autowired
	private AdminService adminService;

	@Autowired
	private TaskDao taskDao;

	@Autowired
	private AES256 aes256;

	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		LocalDateTime beginDate = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		log.info("===============================================================");
		log.info(jobsInfo.getGroupName() + " 작업 시작 - " + beginDate.format(formatter));
		log.info("===============================================================");
		List<TaskLog> taskLogs = new ArrayList<>();
		if (this.jobsInfo.getJob() != null) {
			dataAccess.dataSourceInitialize(this.jobsInfo.getDbUrl(), this.jobsInfo.getDbUsername(), dbPassword, this.jobsInfo.getDbms());
			for (Job job : this.jobsInfo.getJob()) {
				if (service.countByUnagreedCase(job.getJobSeq()) == 0) {
					TaskLog taskResultLog = taskDao.executeTask(job, new TaskLog());
					taskLogs.add(taskResultLog);
				}
			}
			dataAccess.connectionClose();
		} else {
			log.info("작업이 없습니다.");
		}
		LocalDateTime endDate = LocalDateTime.now();
		log.info("===============================================================");
		log.info(jobsInfo.getGroupName() + " 작업 완료 - " + endDate.format(formatter) + " (소요시간: " + (ChronoUnit.MILLIS.between(beginDate, endDate)) + " ms)");
		log.info("===============================================================");

		if (jobsInfo.getMailing().equals("Y")) {
			SimpleMailMessage message = new SimpleMailMessage();
			message.setFrom(administrator.getMail());
			message.setTo(adminService.findDevelopers().stream().map(d -> d.getEmail()).toArray(String[]::new));

			Context ctx = new Context(Locale.KOREA);
			ctx.setVariable("groupName", jobsInfo.getGroupName());
			ctx.setVariable("tasks", taskLogs);
			message.setText(templateEngine.process("mail/task-result", ctx));
			message.setSubject("[BATCH] " + jobsInfo.getGroupName() + " 작업 결과입니다.");
			sender.sendMail(message);
		}
	}
}
