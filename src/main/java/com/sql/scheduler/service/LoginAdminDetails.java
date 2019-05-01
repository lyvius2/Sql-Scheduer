package com.sql.scheduler.service;

import com.sql.scheduler.entity.Admin;
import lombok.Data;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;

@Data
public class LoginAdminDetails extends User {
	private final Admin admin;

	public LoginAdminDetails(Admin admin) {
		super(admin.getUsername(), admin.getPassword(), AuthorityUtils.createAuthorityList("ROLE_ADMIN"));
		this.admin = admin;
	}
}
