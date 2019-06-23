package com.sql.scheduler.repository;

import com.sql.scheduler.entity.SystemLog;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SystemLogRepository extends MongoRepository<SystemLog, String> {
}
