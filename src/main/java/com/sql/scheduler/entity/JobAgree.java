package com.sql.scheduler.entity;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Date;

@Entity
@Table(indexes = {@Index(name = "index_jobAgree_jobSeq", columnList = "jobSeq", unique = false)})
@Data
public class JobAgree {
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	private int jobAgreeSeq;

	private int jobSeq;

	private String username;

	@Size(max = 1)
	private String agreed = "N";

	@Column(insertable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date regDt = new Date();

	@Column(insertable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date agreedDt;
}
