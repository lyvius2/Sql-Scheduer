package com.sql.scheduler.repository;

import com.sql.scheduler.entity.Job;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface JobRepository extends CrudRepository<Job, Integer> {
	long countByGroupSeq(int groupSeq);
	List<Job> findAllByGroupSeqOrderByTaskSeqAsc(int groupSeq);
	List<Job> findAllByRegUsernameAndModUsernameIsNull(String username);
	List<Job> findAllByModUsername(String username);
	void deleteByJobSeq(int jobSeq);
}
