package com.sql.scheduler.controller;

import com.google.gson.Gson;
import com.sql.scheduler.code.DBMS;
import com.sql.scheduler.entity.Job;
import com.sql.scheduler.entity.JobGroup;
import com.sql.scheduler.service.GroupService;
import com.sql.scheduler.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class TaskController {
	@Autowired
	private Gson gson;

	@Autowired
	private GroupService groupService;

	@Autowired
	private TaskService taskService;

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
	public String group(@ModelAttribute JobGroup jobGroup) {
		groupService.save(jobGroup);
		return "redirect:/group";
	}

	@RequestMapping(value = {"/task/{seq}", "/task"}, method = RequestMethod.GET)
	public String task(@PathVariable Optional<Integer> seq, @RequestParam("group") int groupSeq, Model model) {
		Optional<JobGroup> group = groupService.findOne(groupSeq);
		if (group.isPresent()) model.addAttribute("group", group.get());

		if (seq.isPresent()) model.addAttribute("job", taskService.findOne(seq.get()));
		else model.addAttribute("job", new Job());

		model.addAttribute("taskSequence", taskService.findAllByGroupSeq(groupSeq));
		return "task";
	}

	@RequestMapping(value = "/task", method = RequestMethod.POST)
	public String task(@ModelAttribute Job job) {
		job.setGroupSeq(1);
		if (job.getTaskSeq() == 0) job.setTaskSeq(taskService.countByGroupSeq(1) + 1);
		Job result = taskService.save(job);
		return String.format("redirect:/task/%s", result.getJobSeq());
	}
}
