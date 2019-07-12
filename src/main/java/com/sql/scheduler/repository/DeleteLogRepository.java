package com.sql.scheduler.repository;

import com.sql.scheduler.entity.DeleteLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeleteLogRepository extends JpaRepository<DeleteLog, Integer> {
	List<DeleteLog> findByOrderByDtDesc();
}
