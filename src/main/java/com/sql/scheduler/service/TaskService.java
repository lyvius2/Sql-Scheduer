package com.sql.scheduler.service;

import com.google.gson.Gson;
import com.sql.scheduler.code.AgreeStatus;
import com.sql.scheduler.entity.Job;
import com.sql.scheduler.entity.JobAgree;
import com.sql.scheduler.repository.JobAgreeRepository;
import com.sql.scheduler.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
public class TaskService {
	@Autowired
	private Gson gson;

	@Autowired
	private JobRepository jobRepository;

	@Autowired
	private JobAgreeRepository agreeRepository;

	public Job findOne(int seq) {
		Optional<Job> optionalJob = jobRepository.findById(seq);
		if (optionalJob.isPresent()) return optionalJob.get();
		return null;
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

	public JobAgree findById(int jobAgreeSeq) {
		Optional<JobAgree> jobAgree = agreeRepository.findById(jobAgreeSeq);
		if (jobAgree != null) return jobAgree.get();
		return null;
	}

	public JobAgree save(int jobSeq, int registerSeq, String checkerUsername) {
		JobAgree jobAgree = new JobAgree();
		jobAgree.setJobSeq(jobSeq);
		jobAgree.setRegisterSeq(registerSeq);
		jobAgree.setUsername(checkerUsername);
		return agreeRepository.save(jobAgree);
	}

	public JobAgree save(JobAgree jobAgree) {
		return agreeRepository.save(jobAgree);
	}

	public int countByUnagreedCase(int jobSeq) {
		int registerSeq = agreeRepository.getMaxRegisterSeqByJobSeq(jobSeq);
		return (int)agreeRepository.countByJobSeqAndRegisterSeqAndAgreedNot(jobSeq, registerSeq, AgreeStatus.AGREED);
	}

	public List<JobAgree> findByUsernameAndAgreedNotOrderByRegDtDesc(String username) {
		List<JobAgree> jobAgrees = agreeRepository.findByUsernameAndAgreedNotOrderByRegDtDesc(username, AgreeStatus.AGREED);
		return jobAgrees.stream().filter(j -> j.getRegisterSeq() == agreeRepository.getMaxRegisterSeqByJobSeq(j.getJobSeq())).collect(Collectors.toList());
	}

	public List<HashMap> findByMyQueryAgreedStatus(String username) {
		List<Job> jobs = Stream.of(jobRepository.findAllByRegUsernameAndModUsernameIsNull(username),
				jobRepository.findAllByModUsername(username)).flatMap(job -> job.stream()).collect(Collectors.toList());
		List<HashMap> mapList = new ArrayList<>();
		if (jobs.size() > 0) {
			for (Job j : jobs) {
				int maxRegisterSeq = agreeRepository.getMaxRegisterSeqByJobSeq(j.getJobSeq());
				List<JobAgree> jobAgrees = agreeRepository.findByJobSeqAndRegisterSeqOrderByRegDtDesc(j.getJobSeq(), maxRegisterSeq);
				jobAgrees.stream().forEach(jobAgree -> {
					HashMap map = gson.fromJson(gson.toJson(jobAgree), HashMap.class);
					map.put("executeDt", jobAgree.getModDt() != null ? jobAgree.getModDt() : jobAgree.getRegDt());
					map.put("targetTable", j.getTargetTable());
					map.put("jobDescription", j.getJobDescription());
					map.put("query", j.getPerforming() + "\n" + j.getConditional());
					mapList.add(map);
				});
			}
		}
		return mapList;
	}
}
