package com.sql.scheduler.model;

import com.sql.scheduler.entity.JobAgree;
import lombok.Data;

import java.util.HashMap;

@Data
public class Reject {
	private JobAgree jobAgree;
	private HashMap job;

	public Reject(JobAgree jobAgree, HashMap job) {
		this.jobAgree = jobAgree;
		this.job = job;
	}
}
