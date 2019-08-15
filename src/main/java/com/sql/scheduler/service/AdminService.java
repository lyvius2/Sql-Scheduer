package com.sql.scheduler.service;

import com.sql.scheduler.code.AdminStatus;
import com.sql.scheduler.code.AdminType;
import com.sql.scheduler.component.Administrator;
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
	public static String DUPLE_MESSAGE = "이미 등록된 %s가(이) 있습니다.";
	public static String PASSWORD_PATTERNS = "^(?=.*\\d)(?=.*[~`!@#$%\\^&*()-])(?=.*[a-zA-Z]).{8,20}$";
	public static String PASSWORD_INVALID_MESSAGE = "비밀번호는 8자리 이상 20자리 미만으로 지정하십시오.\n영문과 숫자 그리고 특수 문자를 포함하여야 합니다.";

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private Administrator administrator;

	@Autowired
	private AdminRepository adminRepository;

	public Admin save(Admin admin, boolean pwdEncode) {
		if (pwdEncode) admin.setPassword(passwordEncoder.encode(admin.getPassword()));
		if (administrator.getMail().equals(admin.getEmail()) && adminRepository.findAll().size() == 0) admin.setStatus(AdminStatus.Y);
		return adminRepository.save(admin);
	}

	public Admin findOne(String username) {
		return adminRepository.findByUsername(username);
	}

	public List<Admin> findAll() {
		return adminRepository.findAll();
	}

	public List<Admin> findDevelopers() {
		return adminRepository.findAllByType(AdminType.DEVELOPER);
	}

	public List<Admin> findCheckers(String administrator) {
		Admin register = adminRepository.findByUsername(administrator);
		String senderEmail = register.getEmail();
		List<Admin> checkers = adminRepository.findAllByType(AdminType.DEVELOPER)
				.stream()
				.filter(a -> ((!a.getEmail().equals(senderEmail)) && a.getStatus().equals(AdminStatus.Y)))
				.collect(Collectors.toList());
		return checkers;
	}

	public boolean checkPasswordPatterns(String password) {
		return Pattern.matches(PASSWORD_PATTERNS, password);
	}

	public void checkAdminRegisterValue(Admin admin, String confirmPassword, Errors errors) {
		int count = (int)adminRepository.countByUsername(admin.getUsername());
		if (count > 0) errors.rejectValue("username", null, String.format(DUPLE_MESSAGE, "ID"));
		count = (int)adminRepository.countByEmail(admin.getEmail());
		if (count > 0) errors.rejectValue("email", null, String.format(DUPLE_MESSAGE, "E-Mail"));
		boolean validationPw = this.checkPasswordPatterns(admin.getPassword());
		if (!validationPw) errors.rejectValue("password", null, PASSWORD_INVALID_MESSAGE);
		if (!admin.getPassword().equals(confirmPassword)) errors.rejectValue("password", null, "입력한 패스워드 값 두 개가 서로 일치하지 않습니다.");
	}
}
