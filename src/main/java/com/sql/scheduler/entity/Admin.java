package com.sql.scheduler.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sql.scheduler.code.AdminStatus;
import com.sql.scheduler.code.AdminType;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(indexes = {@Index(name = "index_admin_username", columnList = "username", unique = true)})
@Data
public class Admin implements Serializable {
	@Id
	@Column
	private String username;

	@JsonIgnore
	private String password;

	@Enumerated(EnumType.STRING)
	private AdminStatus status = AdminStatus.E;

	@Enumerated(EnumType.STRING)
	private AdminType type;

	@JsonIgnore
	private String certKey;

	private String dept;

	@Column(insertable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date regDt = new Date();

	@Column(insertable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date modDt;
}
