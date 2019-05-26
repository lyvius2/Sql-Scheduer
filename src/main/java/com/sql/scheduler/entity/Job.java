package com.sql.scheduler.entity;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Date;

@Entity
@Table(indexes = {@Index(name = "index_jobSeq", columnList = "jobSeq, groupSeq", unique = true)})
@Data
public class Job {
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	private int jobSeq;

	private int groupSeq;
	private String targetTable;
	private String jobDescription;
	private String performing;
	private String conditional;

	@Size(max = 1)
	private String testMode;

	@Size(max = 1)
	private String use = "Y";

	@Column(name = "task_seq")
	private int taskSeq;

	@Temporal(TemporalType.TIMESTAMP)
	private Date regDt = new Date();

	private String regUsername;

	@Temporal(TemporalType.TIMESTAMP)
	private Date modDt;

	private String modUsername;
}
