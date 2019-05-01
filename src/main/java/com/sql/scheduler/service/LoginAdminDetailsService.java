package com.sql.scheduler.service;

import com.sql.scheduler.entity.Admin;
import com.sql.scheduler.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class LoginAdminDetailsService implements UserDetailsService {
	@Autowired
	private AdminRepository adminRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Admin admin = adminRepository.getOne(username);
		if (admin == null) {
			throw new UsernameNotFoundException("Admin User is not found.");
		} else if (admin != null && !admin.getStatus().equals("Y")) {
			throw new UsernameNotFoundException("This account did not complete the procedure.");
		}
		return new LoginAdminDetails(admin);
	}
}
