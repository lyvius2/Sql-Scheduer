package com.sql.scheduler.service;

import com.sql.scheduler.component.AES256;
import com.sql.scheduler.entity.JobGroup;
import com.sql.scheduler.repository.JobGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
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

	public List<JobGroup> findAllByUse(String use) {
		return repository.findAllByUse(use);
	}

	public JobGroup findOne(int groupSeq) {
		Optional<JobGroup> optionalJobGroup = repository.findById(groupSeq);
		if (optionalJobGroup.isPresent()) {
			JobGroup jobGroup = optionalJobGroup.get();
			return jobGroup;
		}
		else return null;
	}

	public JobGroup save(JobGroup jobGroup) throws Exception {
		jobGroup.setDbPassword(aes256.AESEncoder(jobGroup.getDbPassword()));
		return repository.save(jobGroup);
	}

	public void bindNewerToOriginObjectData(JobGroup origin, JobGroup newer) {
		origin.setGroupName(newer.getGroupName());
		origin.setDbUrl(newer.getDbUrl());
		origin.setDbUsername(newer.getDbUsername());
		origin.setDbPassword(newer.getDbPassword());
		origin.setCron(newer.getCron());
		origin.setDbms(newer.getDbms());
		origin.setMailing(newer.getMailing());
		origin.setModDt(new Date());
	}
}
