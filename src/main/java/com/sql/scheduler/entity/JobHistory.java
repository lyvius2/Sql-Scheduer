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

	private int groupSeq;
	private int jobSeq;
	private String username;

	@Temporal(TemporalType.TIMESTAMP)
	private Date dt = new Date();

	private String comment;
}
