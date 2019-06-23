package com.sql.scheduler.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(indexes = {@Index(name = "index_job_history_comment", columnList = "comment", unique = false)})
@Data
public class JobHistory {
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	private int seq;

	@Column(name = "group_seq")
	private int groupSeq;

	@Column(name = "job_seq")
	private int jobSeq;

	@Column(name = "username")
	private String username;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "dt")
	private Date dt = new Date();

	@Column(name = "comment")
	private String comment;
}
