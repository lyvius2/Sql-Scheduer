package com.sql.scheduler.controller;

import com.sql.scheduler.entity.Mail;
import com.sql.scheduler.service.MailService;
import com.sql.scheduler.service.SchedulerService;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class SchedulerController {
	@Autowired
	private SchedulerService schedulerService;

	@Autowired
	private MailService mailService;

	@RequestMapping(value = "/startSchedule")
	@ResponseBody
	public String test() throws SchedulerException {
		schedulerService.startSchedule();
		return "Success!";
	}

	@RequestMapping(value = "/sendTestEmail")
	@ResponseBody
	public String testMail() {
		Mail mail = new Mail();
		mail.setFrom("lyvius2@naver.com");
		String[] to = {"lyvius2@naver.com"};
		mail.setTo(to);
		mail.setSubject("title");
		mail.setText("content");
		mailService.sendMail(mail);
		return "Success!";
	}
}
