package com.sql.scheduler.entity;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Date;

@Entity
@Table(indexes = {@Index(name = "index_jobSeq", columnList = "job_seq, group_seq", unique = true)})
@Data
public class Job {
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	@Column(name = "job_seq")
	private int jobSeq;

	@Column(name = "group_seq")
	private int groupSeq;

	@Column(name = "target_table")
	private String targetTable;

	@Column(name = "job_description")
	private String jobDescription;

	@Column(name = "performing")
	private String performing;

	@Column(name = "conditional")
	private String conditional;

	@Size(max = 1)
	@Column(name = "test_mode")
	private String testMode;

	@Size(max = 1)
	@Column(name = "use")
	private String use = "Y";

	@Column(name = "task_seq")
	private int taskSeq;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "reg_dt")
	private Date regDt = new Date();

	@Column(name = "reg_username")
	private String regUsername;

	@Column(insertable = false, name = "mod_dt")
	@Temporal(TemporalType.TIMESTAMP)
	private Date modDt;

	@Column(name = "mod_username")
	private String modUsername;
}
