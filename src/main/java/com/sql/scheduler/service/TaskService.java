package com.sql.scheduler.service;

import com.sql.scheduler.entity.Job;
import com.sql.scheduler.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TaskService {
	@Autowired
	private JobRepository repository;

	public Job findOne(int seq) {
		return repository.findById(seq).get();
	}

	public Job save(Job job) {
		return repository.save(job);
	}

	public int countByGroupSeq(int groupSeq) {
		return (int)repository.countByGroupSeq(groupSeq);
	}

	public List<Job> findAllByGroupSeq(int groupSeq) {
		return repository.findAllByGroupSeqOrderByTaskSeqAsc(groupSeq);
	}
}
