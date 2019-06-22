package com.sql.scheduler.service;

import com.sql.scheduler.code.AdminStatus;
import com.sql.scheduler.code.AdminType;
import com.sql.scheduler.component.Administrator;
import com.sql.scheduler.entity.Admin;
import com.sql.scheduler.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LoginAdminDetailsService implements UserDetailsService {
	@Autowired
	private Administrator administrator;

	@Autowired
	private AdminRepository adminRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Admin admin = adminRepository.findByUsername(username);
		if (admin == null) {
			throw new UsernameNotFoundException("Admin User is not found.");
		} else if (admin != null && !admin.getStatus().equals(AdminStatus.Y)) {
			throw new UsernameNotFoundException("This account did not complete the procedure.");
		}
		List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(String.format("ROLE_%s", AdminType.ADMIN.name()));
		if (admin.getType().equals(AdminType.DEVELOPER)) authorities.add(new SimpleGrantedAuthority(String.format("ROLE_%s", AdminType.DEVELOPER.name())));
		if (admin.getEmail().equals(administrator.getMail())) authorities.add(new SimpleGrantedAuthority(String.format("ROLE_%s", AdminType.SUPER_ADMIN.name())));
		return new LoginAdminDetails(admin, authorities);
	}
}
