package com.sql.scheduler.entity;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"job_seq", "group_seq"}))
@Data
public class Job {
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	private int job_seq;

	@NotNull
	private int group_seq;

	private String job_name;
	private String performing;

	@NotNull
	private String conditional;

	private int orderby;

	@Temporal(TemporalType.TIMESTAMP)
	private Date reg_dt;

	private String reg_username;

	@Temporal(TemporalType.TIMESTAMP)
	private Date mod_dt;

	private String mod_username;
}
