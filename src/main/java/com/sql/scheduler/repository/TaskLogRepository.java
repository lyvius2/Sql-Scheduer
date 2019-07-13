package com.sql.scheduler.repository;

import com.sql.scheduler.entity.TaskLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.List;

public interface TaskLogRepository extends MongoRepository<TaskLog, String> {
	TaskLog findTopByJobSeq(int jobSeq);
	Page<TaskLog> findAllByOrderByBeginTimeDesc(Pageable pageable);
	List<TaskLog> findByTargetDataIsNotNullAndBeginTimeBefore(Date date);
}
