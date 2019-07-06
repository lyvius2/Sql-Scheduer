package com.sql.scheduler.repository;

import com.sql.scheduler.entity.TaskLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TaskLogRepository extends MongoRepository<TaskLog, String> {
	TaskLog findTopByJobSeq(int jobSeq);
	Page<TaskLog> findAllByOrderByBeginTimeDesc(Pageable pageable);
}
