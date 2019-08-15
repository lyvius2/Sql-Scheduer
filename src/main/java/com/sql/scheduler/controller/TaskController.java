package com.sql.scheduler.controller;

import com.google.gson.Gson;
import com.sql.scheduler.code.DBMS;
import com.sql.scheduler.code.DayCode;
import com.sql.scheduler.code.MonthCode;
import com.sql.scheduler.component.AES256;
import com.sql.scheduler.component.CronUtil;
import com.sql.scheduler.component.EmailSender;
import com.sql.scheduler.entity.Admin;
import com.sql.scheduler.entity.Job;
import com.sql.scheduler.entity.JobGroup;
import com.sql.scheduler.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.validation.Valid;
import java.util.*;

@Slf4j
@Controller
public class TaskController {
	@Autowired
	private Gson gson;

	@Autowired
	private AES256 aes256;

	@Autowired
	private EmailSender sender;

	@Autowired
	private TemplateEngine templateEngine;

	@Autowired
	private AdminService adminService;

	@Autowired
	private GroupService groupService;

	@Autowired
	private TaskService taskService;

	@Autowired
	private SchedulerService schedulerService;

	@Autowired
	private LogService logService;

	@RequestMapping(value = "/")
	public String index(Model model) {
		model.addAttribute("groupList", groupService.findAllByUse("Y"));
		model.addAttribute("dbmsList", DBMS.values());
		model.addAttribute("dayCodes", DayCode.values());
		model.addAttribute("monthCodes", MonthCode.values());
		return "index";
	}

	@RequestMapping(value = "/group", method = RequestMethod.GET)
	public String group() {
		return "redirect:/";
	}

	@PreAuthorize("hasRole('ROLE_DEVELOPER') or hasRole('ROLE_SUPER_ADMIN')")
	@RequestMapping(value = "/group", method = RequestMethod.POST)
	public String group(@ModelAttribute @Valid JobGroup jobGroup, @AuthenticationPrincipal LoginAdminDetails admin, Errors errors) throws Exception {
		boolean isNewJobGroup = true;
		JobGroup result = null;
		if (jobGroup.getGroupSeq() > 0) {
			JobGroup origin = groupService.findOne(jobGroup.getGroupSeq());
			groupService.bindNewerToOriginObjectData(origin, jobGroup);
			origin.setModUsername(admin.getUsername());
			result = groupService.save(origin);
			isNewJobGroup = false;
		} else {
			jobGroup.setRegUsername(admin.getUsername());
			result = groupService.save(jobGroup);
		}
		if (!isNewJobGroup) schedulerService.startSchedule(result);
		return "redirect:/group";
	}

	@RequestMapping(value = "/group/{seq}", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public String group(@PathVariable Integer seq) throws Exception {
		HashMap<String, Object> map = gson.fromJson(gson.toJson(groupService.findOne(seq)), HashMap.class);
		map.put("dbPassword", aes256.AESDecoder(map.get("dbPassword").toString()));
		return gson.toJson(map);
	}

	@PreAuthorize("hasRole('ROLE_DEVELOPER') or hasRole('ROLE_SUPER_ADMIN')")
	@RequestMapping(value = "/group/{seq}", method = RequestMethod.DELETE, produces = "application/json; charset=utf-8")
	@ResponseBody
	public String removeGroup(@PathVariable Integer seq) throws Exception {
		HashMap<String, Object> map = new HashMap<>();
		JobGroup jobGroup = groupService.findOne(seq);
		jobGroup.setUse("N");
		jobGroup = groupService.save(jobGroup);
		if (jobGroup.getUse().equals("N")) {
			map.put("success", true);
			schedulerService.stopSchedule(jobGroup);
		} else map.put("success", false);
		return gson.toJson(map);
	}

	@RequestMapping(value = {"/task/{seq}", "/task"}, method = RequestMethod.GET)
	public String task(@PathVariable Optional<Integer> seq, @RequestParam("group") int groupSeq, Model model) throws Exception {
		JobGroup jobGroup = groupService.findOne(groupSeq);
		if (jobGroup != null) model.addAttribute("group", jobGroup);
		else return "redirect:../";

		if (seq.isPresent()) model.addAttribute("job", taskService.findOne(seq.get()));
		else model.addAttribute("job", new Job());
		model.addAttribute("taskSequence", taskService.findAllByGroupSeq(groupSeq));
		return "task";
	}

	@PreAuthorize("hasRole('ROLE_DEVELOPER') or hasRole('ROLE_SUPER_ADMIN')")
	@RequestMapping(value = "/task", method = RequestMethod.POST)
	public String task(@ModelAttribute Job job, @AuthenticationPrincipal LoginAdminDetails admin) throws Exception {
		if (job.getTaskSeq() == 0) {
			job.setTaskSeq(taskService.countByGroupSeq(job.getGroupSeq()) + 1);
			job.setRegUsername(admin.getUsername());
		} else {
			job.setModUsername(admin.getUsername());
			job.setModDt(new Date());
		}
		HashMap<String, Object> result = taskService.save(job);
		Job resultJob = (Job)result.get("resultJob");
		if ((boolean)result.get("isConfirmMailingCase")) {
			int registerSeq = (int)result.get("registerSeq");
			List<Admin> checkers = adminService.findCheckers(admin.getUsername());
			JobGroup jobGroup = groupService.findOne(resultJob.getGroupSeq());
			checkers.stream().forEach(c -> {
				taskService.save(resultJob.getJobSeq(), registerSeq, c.getUsername());
				SimpleMailMessage message = new SimpleMailMessage();
				message.setFrom(admin.getAdmin().getEmail());
				message.setTo(c.getEmail());

				Context ctx = new Context(Locale.KOREA);
				ctx.setVariable("name", admin.getAdmin().getName() + "(" + admin.getUsername() + ")");
				ctx.setVariable("performing", resultJob.getPerforming());
				ctx.setVariable("conditional", resultJob.getConditional());
				ctx.setVariable("dbUrl", jobGroup.getDbUrl());
				message.setText(templateEngine.process("mail/confirm-query", ctx));
				message.setSubject("[BATCH] 스케줄러 작업에 대한 SQL쿼리 확인 요청");
				sender.sendMail(message);
			});
		}
		schedulerService.startSchedule(groupService.findOne(resultJob.getGroupSeq()));
		return String.format("redirect:/task/%s?group=%s", resultJob.getJobSeq(), resultJob.getGroupSeq());
	}

	@PreAuthorize("hasRole('ROLE_DEVELOPER') or hasRole('ROLE_SUPER_ADMIN')")
	@RequestMapping(value = "/task/{seq}", method = RequestMethod.DELETE)
	@ResponseBody
	public String task(@PathVariable Optional<Integer> seq) {
		if (seq.isPresent()) taskService.deleteByJobSeq(seq.get());
		return "SUCCESS";
	}

	@RequestMapping(value = "/chkCron", produces = "application/json; charset=utf-8")
	@ResponseBody
	public String cron(@RequestParam("cron") String cronStr) {
		String parseResult = CronUtil.cronParser(cronStr);
		HashMap<String, Object> resultMap = new HashMap<>();
		resultMap.put("input", cronStr);
		resultMap.put("validate", parseResult != null ? true : false);
		if (parseResult != null) resultMap.put("describe", parseResult);
		return gson.toJson(resultMap);
	}

	@RequestMapping(value = "/taskHistory", produces = "application/json; charset=utf-8")
	@ResponseBody
	public String taskHistory(@RequestParam("seq") int jobSeq) {
		return gson.toJson(logService.findTopByJobSeq(jobSeq));
	}
}
