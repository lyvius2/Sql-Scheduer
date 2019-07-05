package com.sql.scheduler.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sql.scheduler.code.DBMS;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;

@Entity
@Table(indexes = {@Index(name = "index_job_groupSeq", columnList = "group_seq", unique = true)})
@Data
public class JobGroup {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "group_seq")
	private int groupSeq;

	@Column(name = "group_name")
	private String groupName;

	@Enumerated(EnumType.STRING)
	@Column(name = "dbms")
	private DBMS dbms;

	@Column(name = "db_url")
	private String dbUrl;

	@Column(name = "db_username")
	private String dbUsername;

	@JsonIgnore
	@Column(name = "db_password")
	private String dbPassword;

	@Column(name = "cron")
	private String cron;

	@Size(max = 1)
	@Column(name = "mailing")
	private String mailing;

	@Size(max = 1)
	@Column(name = "use")
	private String use = "Y";

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "reg_dt")
	private Date regDt = new Date();

	@Column(name = "reg_username")
	private String regUsername;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "mod_dt")
	private Date modDt;

	@Column(name = "mod_username")
	private String modUsername;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "group_seq")
	private List<Job> job;
}
