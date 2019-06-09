package com.sql.scheduler.controller;

import com.cronutils.descriptor.CronDescriptor;
import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.parser.CronParser;
import com.google.gson.Gson;
import com.sql.scheduler.code.DBMS;
import com.sql.scheduler.code.DayCode;
import com.sql.scheduler.code.MonthCode;
import com.sql.scheduler.component.AES256;
import com.sql.scheduler.component.EmailSender;
import com.sql.scheduler.entity.Admin;
import com.sql.scheduler.entity.Job;
import com.sql.scheduler.entity.JobGroup;
import com.sql.scheduler.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

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
	private AdminService adminService;

	@Autowired
	private GroupService groupService;

	@Autowired
	private TaskService taskService;

	@Autowired
	private SchedulerService schedulerService;

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
		return "redirect:../";
	}

	@RequestMapping(value = "/group", method = RequestMethod.POST)
	public String group(@ModelAttribute @Valid JobGroup jobGroup, @AuthenticationPrincipal LoginAdminDetails admin, Errors errors) throws Exception {
		boolean isNewJobGroup = true;
		if (jobGroup.getGroupSeq() > 0) {
			isNewJobGroup = false;
			jobGroup.setModUsername(admin.getUsername());
			jobGroup.setModDt(new Date());
		} else {
			jobGroup.setRegUsername(admin.getUsername());
		}
		JobGroup result = groupService.save(jobGroup);
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
			checkers.stream().forEach(c -> {
				taskService.save(resultJob.getJobSeq(), registerSeq, c.getUsername());
				SimpleMailMessage message = new SimpleMailMessage();
				message.setFrom(admin.getAdmin().getEmail());
				message.setTo(c.getEmail());
				message.setSubject("스케줄러 작업에 대한 쿼리 확인 메일입니다.");
				message.setText("테스트 메일입니다.");
				sender.sendMail(message);
			});
		}
		schedulerService.startSchedule(groupService.findOne(resultJob.getGroupSeq()));
		return String.format("redirect:/task/%s?group=%s", resultJob.getJobSeq(), resultJob.getGroupSeq());
	}

	@RequestMapping(value = "/task/{seq}", method = RequestMethod.DELETE)
	@ResponseBody
	public String task(@PathVariable Optional<Integer> seq) {
		if (seq.isPresent()) taskService.deleteByJobSeq(seq.get());
		return "SUCCESS";
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
