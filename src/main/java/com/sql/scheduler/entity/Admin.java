package com.sql.scheduler.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table
@Data
public class Admin implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column
	private String username;

	@Column
	private String password;

	@Column(columnDefinition = "E")
	private String status;

	@Column
	private String cert_key;

	@Column
	private String dept;

	@Column(insertable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date reg_dt;
}
