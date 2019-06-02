package com.sql.scheduler.service;

import com.sql.scheduler.component.AES256;
import com.sql.scheduler.entity.JobGroup;
import com.sql.scheduler.repository.JobGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class GroupService {
	@Autowired
	private AES256 aes256;

	@Autowired
	private JobGroupRepository repository;

	public List<JobGroup> findAll() {
		return (ArrayList<JobGroup>)repository.findAll();
	}

	public Optional<JobGroup> findOne(int groupSeq) {
		return repository.findById(groupSeq);
	}

	public JobGroup save(JobGroup jobGroup) throws Exception {
		jobGroup.setDbPassword(aes256.AESEncoder(jobGroup.getDbPassword()));
		return repository.save(jobGroup);
	}
}
