package com.sql.scheduler.controller;

import com.google.gson.Gson;
import com.sql.scheduler.code.ResultStatus;
import com.sql.scheduler.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Optional;

@Controller
public class LogController {
	@Autowired
	private Gson gson;

	@Autowired
	private LogService logService;

	@RequestMapping(value = "/log", method = RequestMethod.GET)
	public String log(Model model) {
		model.addAttribute("status", Arrays.asList(ResultStatus.values()));
		return "log";
	}

	@RequestMapping(value = "/taskLog", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public String taskLogList(@RequestParam(value = "currPageNo", required = true) int currPageNo) {
		return gson.toJson(logService.findAllTaskLog(currPageNo));
	}

	@RequestMapping(value = {"/systemLog/{resultStatus}", "/systemLog"}, method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public String systemLogList(@PathVariable Optional<ResultStatus> resultStatus, @RequestParam(value = "currPageNo", required = true) int currPageNo) {
		if (resultStatus.isPresent()) return gson.toJson(logService.findByStatusSystemLog(resultStatus.get(), currPageNo));
		else return gson.toJson(logService.findAllSystemLog(currPageNo));
	}
}
