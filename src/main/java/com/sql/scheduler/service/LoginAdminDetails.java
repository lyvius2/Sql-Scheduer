package com.sql.scheduler.service;

import com.sql.scheduler.entity.Admin;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;

public class LoginAdminDetails extends User {
	private final Admin admin;

	public LoginAdminDetails(Admin admin, List<GrantedAuthority> authorities) {
		super(admin.getUsername(), admin.getPassword(), authorities);
		this.admin = admin;
	}

	public Admin getAdmin() {
		return admin;
	}
}
