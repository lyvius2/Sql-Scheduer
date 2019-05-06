package com.sql.scheduler.service;

import com.sql.scheduler.entity.Admin;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;

public class LoginAdminDetails extends User {
	private final Admin admin;

	public LoginAdminDetails(Admin admin) {
		super(admin.getUsername(), admin.getPassword(), AuthorityUtils.createAuthorityList("ROLE_USER"));
		this.admin = admin;
	}
}
