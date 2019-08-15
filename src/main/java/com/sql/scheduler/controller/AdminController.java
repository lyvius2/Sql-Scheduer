package com.sql.scheduler.controller;

import com.google.gson.Gson;
import com.sql.scheduler.code.AdminStatus;
import com.sql.scheduler.code.AdminType;
import com.sql.scheduler.code.AgreeStatus;
import com.sql.scheduler.component.Administrator;
import com.sql.scheduler.entity.Admin;
import com.sql.scheduler.entity.Job;
import com.sql.scheduler.entity.JobAgree;
import com.sql.scheduler.entity.JobGroup;
import com.sql.scheduler.model.Reject;
import com.sql.scheduler.service.AdminService;
import com.sql.scheduler.service.GroupService;
import com.sql.scheduler.service.LoginAdminDetails;
import com.sql.scheduler.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Controller
public class AdminController {
	@Autowired
	private Gson gson;

	@Autowired
	private Administrator administrator;

	@Autowired
	private AdminService adminService;

	@Autowired
	private GroupService groupService;

	@Autowired
	private TaskService taskService;

	@RequestMapping(value = "/admin", method = RequestMethod.GET)
	public String admin(Model model, @AuthenticationPrincipal LoginAdminDetails admin) {
		List<Admin> admins = adminService.findAll();
		model.addAttribute("waiters", admins.stream().filter(a -> a.getStatus().equals(AdminStatus.E)).collect(toList()));
		model.addAttribute("users", admins.stream().filter(a -> a.getStatus().equals(AdminStatus.Y)).collect(toList()));
		model.addAttribute("leavings", admins.stream().filter(a -> a.getStatus().equals(AdminStatus.N)).collect(toList()));

		List<Reject> rejects = new ArrayList<>();
		taskService.findByUsernameAndAgreedNotOrderByRegDtDesc(admin.getUsername()).stream()
				.forEach(r -> {
					Job j = taskService.findOne(r.getJobSeq());
					if (j != null) {
						JobGroup jg = groupService.findOne(j.getGroupSeq());
						HashMap map = gson.fromJson(gson.toJson(j), HashMap.class);
						map.put("dbUrl", jg.getDbUrl());
						Reject reject = new Reject(r, map);
						rejects.add(reject);
					}
				});
		model.addAttribute("rejects", rejects);
		model.addAttribute("agreedStatus", taskService.findByMyQueryAgreedStatus(admin.getUsername()));
		model.addAttribute("administrator", administrator.getMail());
		model.addAttribute("newLineChar", '\n');
		return "admin";
	}

	@PreAuthorize("hasRole('ROLE_SUPER_ADMIN')")
	@RequestMapping(value = "/admin", method = RequestMethod.POST)
	public String chgPwd(Model model, HttpServletRequest request, @AuthenticationPrincipal LoginAdminDetails admin) {
		boolean valid = true;
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		if (username == null || password == null) {
			model.addAttribute("message", "접근이 잘못되었습니다.");
			valid = false;
			}
		if (admin.getAuthorities().contains(new SimpleGrantedAuthority(String.format("ROLE_%s", AdminType.SUPER_ADMIN.name())))) model.addAttribute("message", "비밀번호가 초기화되었습니다.");
		else {
			model.addAttribute("message", "권한이 없습니다.");
			valid = false;
		}

		if (valid) {
			Admin admin1 = adminService.findOne(username);
			admin1.setPassword(password);
			adminService.save(admin1, true);
		}
		return "layout/alert";
	}

	@PreAuthorize("hasRole('ROLE_SUPER_ADMIN')")
	@RequestMapping(value = "/admin/{toBeStatus}/{username}", method = RequestMethod.POST)
	public String admin(@PathVariable("toBeStatus") AdminStatus toBeStatus,
	                    @PathVariable("username") String username) {
		Admin admin = adminService.findOne(username);
		admin.setStatus(toBeStatus);
		admin.setModDt(new Date());
		adminService.save(admin, false);
		return "redirect:/admin";
	}

	@PreAuthorize("hasRole('ROLE_SUPER_ADMIN')")
	@RequestMapping(value = "/admin/developer/{username}", method = RequestMethod.POST)
	@ResponseBody
	public String chgDeveloperStatus(@PathVariable("username") String username) {
		Admin admin = adminService.findOne(username);
		if (admin.getType().equals(AdminType.ADMIN)) admin.setType(AdminType.DEVELOPER);
		else admin.setType(AdminType.ADMIN);
		admin.setModDt(new Date());
		adminService.save(admin, false);
		return "SUCCESS";
	}

	@PreAuthorize("hasRole('ROLE_DEVELOPER') or hasRole('ROLE_SUPER_ADMIN')")
	@RequestMapping(value = "/agree/{toBeStatus}/{jobAgreeSeq}",
			method = RequestMethod.POST, produces = "application/json; charset=utf-8")
	@ResponseBody
	public String decision(@PathVariable("toBeStatus") AgreeStatus toBeStatus,
	                       @PathVariable("jobAgreeSeq") Integer jobAgreeSeq,
	                       @AuthenticationPrincipal LoginAdminDetails admin) {
		HashMap<String, Object> map = new HashMap<>();
		JobAgree jobAgree = taskService.findById(jobAgreeSeq);
		if (jobAgree.getUsername().equals(admin.getUsername()) && !AgreeStatus.WAIT.equals(toBeStatus)) {
			jobAgree.setAgreed(toBeStatus);
			jobAgree.setModDt(new Date());
			taskService.save(jobAgree);
			map.put("success", true);
			map.put("msg", toBeStatus.equals(AgreeStatus.AGREED) ? "승인되었습니다." : "반려되었습니다.\n이 작업은 재승인 때까지 진행되지 않습니다.");
		} else {
			map.put("success", false);
			map.put("msg", "잘못된 접근입니다.");
		}
		return gson.toJson(map);
	}
}
