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
	private TaskService service;

	@Autowired
	private AdminService adminService;

	@Autowired
	private TaskDao taskDao;

	@Autowired
	private AES256 aes256;

	/**
	 * SQL 스케줄링 작업
	 * @param context
	 * @throws JobExecutionException
	 */
	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		LocalDateTime beginDate = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		log.info("===============================================================");
		log.info(jobsInfo.getGroupName() + " 작업 시작 - " + beginDate.format(formatter));
		log.info("===============================================================");
		List<TaskLog> taskLogs = new ArrayList<>();
		// 작업그룹에 속한 SQL 작업이 존재한다면 (Null이 아니라면)
		if (this.jobsInfo.getJob() != null) {
			// DB접속 초기화 (접속 객체 생성)
			DataSourceAccess dataAccess = new DataSourceAccess();
			dataAccess.dataSourceInitialize(this.jobsInfo.getDbUrl(), this.jobsInfo.getDbUsername(), dbPassword, this.jobsInfo.getDbms());
			for (Job job : this.jobsInfo.getJob()) {
				// 미승인 SQL 작업은 수행하지 않음
				if (service.countByUnagreedCase(job.getJobSeq()) == 0) {
					// taskDao 객체의 executeTask 메서드에서 SQL 작업을 실행하고 결과를 Log 데이터로 반환
					TaskLog taskResultLog = taskDao.executeTask(job, new TaskLog(), this.jobsInfo.getDbms(), dataAccess);
					taskLogs.add(taskResultLog);
				}
			}
			// 작업 완료 후 DB접속 닫기
			dataAccess.connectionClose();
		} else {
			log.info("작업이 없습니다.");
		}
		LocalDateTime endDate = LocalDateTime.now();
		log.info("===============================================================");
		log.info(jobsInfo.getGroupName() + " 작업 완료 - " + endDate.format(formatter) + " (소요시간: " + (ChronoUnit.MILLIS.between(beginDate, endDate)) + " ms)");
		log.info("===============================================================");

		// 메일링 설정 'Y'로 되어 있다면 스케줄 작업 결과를 등록된 사용자들에게 메일로 발송
		if (jobsInfo.getMailing().equals("Y")) {
			SimpleMailMessage message = new SimpleMailMessage();
			message.setFrom(administrator.getMail());
			message.setTo(adminService.findAll().stream().map(d -> d.getEmail()).toArray(String[]::new));

			if (message.getTo().length > 0) {
				Context ctx = new Context(Locale.KOREA);
				ctx.setVariable("groupName", this.jobsInfo.getGroupName());
				ctx.setVariable("tasks", taskLogs);
				message.setText(templateEngine.process("mail/task-result", ctx));
				message.setSubject("[BATCH] " + this.jobsInfo.getGroupName() + " 작업 결과입니다.");
				sender.sendMail(message);
			}
		}
	}
}
