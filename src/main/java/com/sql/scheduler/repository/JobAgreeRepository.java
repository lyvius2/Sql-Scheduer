package com.sql.scheduler.repository;

import com.sql.scheduler.code.AgreeStatus;
import com.sql.scheduler.entity.JobAgree;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface JobAgreeRepository extends CrudRepository<JobAgree, Integer> {
	@Query(value = "SELECT MAX(register_seq) FROM job_agree WHERE job_seq = ?1", nativeQuery = true)
	int getMaxRegisterSeqByJobSeq(int jobSeq);

	long countByJobSeqAndRegisterSeqAndAgreedNot(int jobSeq, int registerJob, AgreeStatus status);
}
