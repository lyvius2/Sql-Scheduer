package com.sql.scheduler.service;

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
	private JobGroupRepository repository;

	public List<JobGroup> findAll() {
		return (ArrayList<JobGroup>)repository.findAll();
	}

	public Optional<JobGroup> findOne(int groupSeq) {
		return repository.findById(groupSeq);
	}
}
