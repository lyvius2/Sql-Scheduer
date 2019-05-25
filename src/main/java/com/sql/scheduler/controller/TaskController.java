package com.sql.scheduler.controller;

import com.google.gson.Gson;
import com.sql.scheduler.entity.Job;
import com.sql.scheduler.service.ExecuteJobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TaskController {

	@Autowired
	private Gson gson;

	@Autowired
	private ExecuteJobService service;

	@RequestMapping(value = "/")
	public String index() {
		return "index";
	}

	@RequestMapping(value = "/task", method = RequestMethod.GET)
	public String task(Model model) {
		return "task";
	}

	@RequestMapping(value = "/task", method = RequestMethod.POST)
	@ResponseBody
	public String task(@ModelAttribute Job job) {
		return gson.toJson(job);
	}
}
