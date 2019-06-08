package com.sql.scheduler.repository;

import com.sql.scheduler.entity.JobGroup;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface JobGroupRepository extends CrudRepository<JobGroup, Integer> {
	List<JobGroup> findAllByUse(String use);
}
