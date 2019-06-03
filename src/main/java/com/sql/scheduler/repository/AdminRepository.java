package com.sql.scheduler.repository;

import com.sql.scheduler.code.AdminType;
import com.sql.scheduler.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdminRepository extends JpaRepository<Admin, String> {
	List<Admin> findAllByType(AdminType adminType);
	Admin findByUsername(String username);
}
