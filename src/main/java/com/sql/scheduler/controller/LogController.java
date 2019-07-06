package com.sql.scheduler.controller;

import com.google.gson.Gson;
import com.sql.scheduler.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LogController {
	@Autowired
	private Gson gson;

	@Autowired
	private LogService logService;

	@RequestMapping(value = "/log", method = RequestMethod.GET)
	public String log() {
		return "log";
	}

	@RequestMapping(value = "/taskLog", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public String taskLogList(@RequestParam(value = "currPageNo", required = true) int currPageNo) {
		return gson.toJson(logService.findAll(currPageNo));
	}
}
