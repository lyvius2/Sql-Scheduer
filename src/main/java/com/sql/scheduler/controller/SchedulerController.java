package com.sql.scheduler.controller;

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

	@RequestMapping(value = "/startSchedule")
	@ResponseBody
	public String test() throws SchedulerException {
		schedulerService.startSchedule();
		return "Success!";
	}
}
