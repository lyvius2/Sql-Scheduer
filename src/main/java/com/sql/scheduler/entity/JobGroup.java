package com.sql.scheduler.entity;

import com.sql.scheduler.code.DBMS;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(indexes = {@Index(name = "index_job_group_seq", columnList = "group_seq", unique = true)})
@Data
public class JobGroup {
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	private int group_seq;

	private String group_name;

	@Enumerated(EnumType.STRING)
	private DBMS dbms;

	@NotNull
	private String db_url;

	private String db_username;
	private String db_password;

	@NotNull
	private String cron;

	@Temporal(TemporalType.TIMESTAMP)
	private Date reg_dt;

	private String reg_username;

	@Temporal(TemporalType.TIMESTAMP)
	private Date mod_dt;

	private String mod_username;
}
