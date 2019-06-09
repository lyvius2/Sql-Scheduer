package com.sql.scheduler.service;

import com.sql.scheduler.code.AdminType;
import com.sql.scheduler.entity.Admin;
import com.sql.scheduler.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import javax.transaction.Transactional;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Transactional
public class AdminService {
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private AdminRepository adminRepository;

	public Admin save(Admin admin) {
		admin.setPassword(passwordEncoder.encode(admin.getPassword()));
		return adminRepository.save(admin);
	}

	public Admin findOne(String username) {
		return adminRepository.findByUsername(username);
	}

	public List<Admin> findCheckers(String administrator) {
		Admin register = adminRepository.findByUsername(administrator);
		String senderEmail = register.getEmail();
		List<Admin> checkers = adminRepository.findAllByType(AdminType.DEVELOPER).stream().filter(a -> !a.getEmail().equals(senderEmail)).collect(Collectors.toList());
		return checkers;
	}

	public void checkAdminRegisterValue(Admin admin, String confirmPassword, Errors errors) {
		int count = (int)adminRepository.countByUsername(admin.getUsername());
		if (count > 0) errors.rejectValue("username", null, "이미 등록된 ID가 있습니다.");
		boolean validationPw = Pattern.matches("^(?=.*\\d)(?=.*[~`!@#$%\\^&*()-])(?=.*[a-zA-Z]).{8,20}$", admin.getPassword());
		if (!validationPw) errors.rejectValue("password", null, "비밀번호는 8자리 이상 20자리 미만으로 지정하십시오.\n영문과 숫자 그리고 특수 문자를 포함하여야 합니다.");
		if (!admin.getPassword().equals(confirmPassword)) errors.rejectValue("password", null, "입력한 패스워드 값 두 개가 서로 일치하지 않습니다.");
	}
}
