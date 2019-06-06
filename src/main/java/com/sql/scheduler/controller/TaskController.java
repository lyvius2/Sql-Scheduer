package com.sql.scheduler.controller;

import com.cronutils.descriptor.CronDescriptor;
import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.parser.CronParser;
import com.google.gson.Gson;
import com.sql.scheduler.code.DBMS;
import com.sql.scheduler.entity.Job;
import com.sql.scheduler.entity.JobGroup;
import com.sql.scheduler.service.GroupService;
import com.sql.scheduler.service.SchedulerService;
import com.sql.scheduler.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Locale;
import java.util.Optional;

@Slf4j
@Controller
public class TaskController {
	@Autowired
	private Gson gson;

	@Autowired
	private GroupService groupService;

	@Autowired
	private TaskService taskService;

	@Autowired
	private SchedulerService schedulerService;

	@RequestMapping(value = "/")
	public String index(Model model) {
		model.addAttribute("groupList", groupService.findAll());
		model.addAttribute("dbmsList", DBMS.values());
		return "index";
	}

	@RequestMapping(value = "/group", method = RequestMethod.GET)
	public String group() {
		return "redirect:../";
	}

	@RequestMapping(value = "/group", method = RequestMethod.POST)
	public String group(@ModelAttribute @Valid JobGroup jobGroup, Errors errors) throws Exception {
		groupService.save(jobGroup);
		return "redirect:/group";
	}

	@RequestMapping(value = {"/task/{seq}", "/task"}, method = RequestMethod.GET)
	public String task(@PathVariable Optional<Integer> seq, @RequestParam("group") int groupSeq, Model model) {
		JobGroup jobGroup = groupService.findOne(groupSeq);
		if (jobGroup != null) model.addAttribute("group", jobGroup);
		else return "redirect:../";

		if (seq.isPresent()) model.addAttribute("job", taskService.findOne(seq.get()));
		else model.addAttribute("job", new Job());
		model.addAttribute("taskSequence", taskService.findAllByGroupSeq(groupSeq));
		return "task";
	}

	@RequestMapping(value = "/task", method = RequestMethod.POST)
	public String task(@ModelAttribute Job job) throws Exception {
		if (job.getTaskSeq() == 0) job.setTaskSeq(taskService.countByGroupSeq(job.getGroupSeq()) + 1);
		Job result = taskService.save(job);
		schedulerService.startSchedule(groupService.findOne(result.getGroupSeq()));
		return String.format("redirect:/task/%s?group=%s", result.getJobSeq(), result.getGroupSeq());
	}

	@RequestMapping(value = "/chkCron", produces = "application/json; charset=utf-8")
	@ResponseBody
	public String cron(@RequestParam("cron") String cronStr) {
		HashMap<String, Object> resultMap = new HashMap<>();
		resultMap.put("input", cronStr);
		CronDefinition cronDefinition = CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ);
		CronParser cronParser = new CronParser(cronDefinition);
		try {
			Cron cron = cronParser.parse(cronStr).validate();
			CronDescriptor descriptor = CronDescriptor.instance(Locale.KOREA);
			resultMap.put("describe", descriptor.describe(cron));
			resultMap.put("validate", true);
		} catch(Exception e) {
			e.getStackTrace();
			resultMap.put("validate", false);
		}
		return gson.toJson(resultMap);
	}
}
