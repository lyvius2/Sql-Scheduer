package com.sql.scheduler.controller;

import com.sql.scheduler.code.AdminStatus;
import com.sql.scheduler.entity.Admin;
import com.sql.scheduler.model.Reject;
import com.sql.scheduler.service.AdminService;
import com.sql.scheduler.service.LoginAdminDetails;
import com.sql.scheduler.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;

@Controller
public class AdminController {
	@Autowired
	private AdminService adminService;

	@Autowired
	private TaskService taskService;

	@RequestMapping(value = "/admin", method = RequestMethod.GET)
	public String admin(Model model, @AuthenticationPrincipal LoginAdminDetails admin) {
		List<Admin> admins = adminService.findAll();
		model.addAttribute("waiters", admins.stream().filter(a -> a.getStatus().equals(AdminStatus.E)).collect(toList()));
		model.addAttribute("users", admins.stream().filter(a -> a.getStatus().equals(AdminStatus.Y)).collect(toList()));
		model.addAttribute("leaving", admins.stream().filter(a -> a.getStatus().equals(AdminStatus.N)).collect(toList()));

		List<Reject> rejects = new ArrayList<>();
		taskService.findByUsernameAndAgreedNotOrderByRegDtDesc(admin.getUsername()).stream().forEach(r -> {
			Reject reject = new Reject(r, taskService.findOne(r.getJobSeq()));
			rejects.add(reject);
		});
		model.addAttribute("rejects", rejects);
		return "admin";
	}
}
