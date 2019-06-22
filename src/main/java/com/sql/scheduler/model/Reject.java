package com.sql.scheduler.model;

import com.sql.scheduler.entity.Job;
import com.sql.scheduler.entity.JobAgree;
import lombok.Data;

@Data
public class Reject {
	private JobAgree jobAgree;
	private Job job;

	public Reject(JobAgree jobAgree, Job job) {
		this.jobAgree = jobAgree;
		this.job = job;
	}
}
