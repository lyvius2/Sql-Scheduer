package com.sql.scheduler.controller;

import com.google.gson.Gson;
import com.sql.scheduler.entity.Admin;
import com.sql.scheduler.service.AdminService;
import com.sql.scheduler.service.LoginAdminDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Date;
import java.util.HashMap;
import java.util.Optional;

@Slf4j
@Controller
public class LoginController {
	@Autowired
	private Gson gson;

	@Autowired
	private AdminService service;

	@RequestMapping(value = "/loginForm")
	public String loginForm(@RequestParam(value = "error", required = false) Optional<String> error, HttpServletRequest request, Model model) {
		if (error.isPresent()) {
			String errorMsg = ((Exception)request.getSession().getAttribute("SPRING_SECURITY_LAST_EXCEPTION")).getMessage();
			model.addAttribute("errorMsg", errorMsg);
		}
		return "login";
	}

	@RequestMapping(value = "/loginForm", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
	@ResponseBody
	public String loginForm(@ModelAttribute @Valid Admin admin, HttpServletRequest request, Errors errors) {
		String confirmPassword = request.getParameter("confirmPassword");
		service.checkAdminRegisterValue(admin, confirmPassword, errors);
		HashMap<String, Object> hashMap = new HashMap<>();
		if (errors.hasErrors()) {
			FieldError error = errors.getFieldError();
			hashMap.put("success", false);
			hashMap.put("message", error.getDefaultMessage());
		} else {
			Admin resultAdmin = service.save(admin, true);
			if (resultAdmin != null) {
				hashMap.put("success", true);
				hashMap.put("message", "등록되었습니다.\n수퍼 관리자의 확인 후 로그인이 가능합니다.");
			}
		}
		return gson.toJson(hashMap);
	}

	@RequestMapping(value = "/password", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
	@ResponseBody
	public String password(@AuthenticationPrincipal LoginAdminDetails admin,
	                       @RequestParam("password") String password, @RequestParam("passwordConfirm") String passwordConfirm) {
		HashMap<String, Object> hashMap = new HashMap<>();
		if (!password.equals(passwordConfirm)) {
			hashMap.put("success", false);
			hashMap.put("message", "입력한 패스워드 두 개가 서로 다릅니다.");
		} else if (!service.checkPasswordPatterns(password)) {
			hashMap.put("success", false);
			hashMap.put("message", AdminService.PASSWORD_INVALID_MESSAGE);
		} else {
			Admin origin = service.findOne(admin.getUsername());
			origin.setPassword(password);
			origin.setModDt(new Date());
			service.save(origin, true);
			hashMap.put("success", true);
			hashMap.put("message", "비밀번호가 변경되었습니다.\n로그아웃했다가 다시 로그인하십시오.");
		}
		return gson.toJson(hashMap);
	}
}
