package com.sql.scheduler.entity;

import com.sql.scheduler.code.AgreeStatus;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(indexes = {@Index(name = "index_jobAgree_jobSeq", columnList = "jobSeq", unique = false)})
@Data
public class JobAgree {
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	private int jobAgreeSeq;

	@Column(name = "job_seq")
	private int jobSeq;

	@Column(name = "register_seq")
	private int registerSeq;

	private String username;

	@Enumerated(EnumType.STRING)
	private AgreeStatus agreed = AgreeStatus.WAIT;

	@Column(insertable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date regDt = new Date();

	@Column(insertable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date modDt;
}
