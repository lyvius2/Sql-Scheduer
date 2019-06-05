package com.sql.scheduler.repository;

import com.sql.scheduler.entity.JobAgree;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface JobAgreeRepository extends CrudRepository<JobAgree, Integer> {
	@Query(value = "SELECT MAX(register_seq) FROM JobAgree WHERE job_seq = ?1", nativeQuery = true)
	int getMaxRegisterSeqByJobSeq(int jobSeq);
}
