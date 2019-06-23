package com.sql.scheduler.repository;

import com.sql.scheduler.entity.TaskLog;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TaskLogRepository extends MongoRepository<TaskLog, String> {
}
