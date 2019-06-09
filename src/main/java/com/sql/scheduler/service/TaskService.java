package com.sql.scheduler.service;

import com.sql.scheduler.code.AgreeStatus;
import com.sql.scheduler.entity.Job;
import com.sql.scheduler.entity.JobAgree;
import com.sql.scheduler.repository.JobAgreeRepository;
import com.sql.scheduler.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;

@Service
@Transactional
public class TaskService {
	@Autowired
	private JobRepository jobRepository;

	@Autowired
	private JobAgreeRepository agreeRepository;

	public Job findOne(int seq) {
		return jobRepository.findById(seq).get();
	}

	public HashMap<String, Object> save(Job job) {
		boolean isConfirmMailingCase = false;
		int registerSeq = 1;
		if (job.getJobSeq() != 0) {
			Job prevJob = jobRepository.findById(job.getJobSeq()).get();
			if (!job.getTargetTable().equals(prevJob.getTargetTable())
					|| !job.getPerforming().equals(prevJob.getPerforming())
					|| !job.getConditional().equals(prevJob.getConditional())) {
				isConfirmMailingCase = true;
				registerSeq = (agreeRepository.getMaxRegisterSeqByJobSeq(job.getJobSeq()) + 1);
			}
		} else {
			isConfirmMailingCase = true;
		}

		Job resultJob = jobRepository.save(job);
		HashMap<String, Object> result = new HashMap<>();
		result.put("resultJob", resultJob);
		result.put("isConfirmMailingCase", isConfirmMailingCase);
		result.put("registerSeq", registerSeq);
		return result;
	}

	public int countByGroupSeq(int groupSeq) {
		return (int)jobRepository.countByGroupSeq(groupSeq);
	}

	public List<Job> findAllByGroupSeq(int groupSeq) {
		return jobRepository.findAllByGroupSeqOrderByTaskSeqAsc(groupSeq);
	}

	public void deleteByJobSeq(int jobSeq) {
		jobRepository.deleteByJobSeq(jobSeq);
	}

	public JobAgree save(int jobSeq, int registerSeq, String checkerUsername) {
		JobAgree jobAgree = new JobAgree();
		jobAgree.setJobSeq(jobSeq);
		jobAgree.setRegisterSeq(registerSeq);
		jobAgree.setUsername(checkerUsername);
		return agreeRepository.save(jobAgree);
	}

	public int countByUnagreedCase(int jobSeq) {
		int registerSeq = agreeRepository.getMaxRegisterSeqByJobSeq(jobSeq);
		return (int)agreeRepository.countByJobSeqAndRegisterSeqAndAgreedNot(jobSeq, registerSeq, AgreeStatus.AGREED);
	}
}
