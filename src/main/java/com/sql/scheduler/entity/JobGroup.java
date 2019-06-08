package com.sql.scheduler.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sql.scheduler.code.DBMS;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;

@Entity
@Table(indexes = {@Index(name = "index_job_groupSeq", columnList = "groupSeq", unique = true)})
@Data
public class JobGroup {
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	private int groupSeq;

	private String groupName;

	@Enumerated(EnumType.STRING)
	private DBMS dbms;

	private String dbUrl;
	private String dbUsername;

	@JsonIgnore
	private String dbPassword;

	private String cron;

	@Size(max = 1)
	private String mailing;

	@Size(max = 1)
	private String use = "Y";

	@Temporal(TemporalType.TIMESTAMP)
	private Date regDt = new Date();

	private String regUsername;

	@Temporal(TemporalType.TIMESTAMP)
	private Date modDt;

	private String modUsername;

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "groupSeq")
	private List<Job> job;
}
