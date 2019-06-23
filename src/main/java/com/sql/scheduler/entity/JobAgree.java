package com.sql.scheduler.entity;

import com.sql.scheduler.code.AgreeStatus;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(indexes = {@Index(name = "index_jobAgree_jobSeq", columnList = "job_seq", unique = false)})
@Data
public class JobAgree {
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	@Column(name = "job_agree_seq")
	private int jobAgreeSeq;

	@Column(name = "job_seq")
	private int jobSeq;

	@Column(name = "register_seq")
	private int registerSeq;

	@Column(name = "username")
	private String username;

	@Enumerated(EnumType.STRING)
	@Column(name = "agreed")
	private AgreeStatus agreed = AgreeStatus.WAIT;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "reg_dt")
	private Date regDt = new Date();

	@Column(insertable = false, name = "mod_dt")
	@Temporal(TemporalType.TIMESTAMP)
	private Date modDt;
}
