package com.sql.scheduler.repository;

import com.sql.scheduler.code.ResultStatus;
import com.sql.scheduler.entity.SystemLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface SystemLogRepository extends MongoRepository<SystemLog, String>, PagingAndSortingRepository<SystemLog, String> {
	long countByStatus(ResultStatus status);
	Page<SystemLog> findByStatusOrderBy_idDesc(ResultStatus status, Pageable pageable);
}
