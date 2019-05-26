package com.sql.scheduler.repository;

import com.sql.scheduler.entity.JobGroup;
import org.springframework.data.repository.CrudRepository;

public interface JobGroupRepository extends CrudRepository<JobGroup, Integer> {
}
