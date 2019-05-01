package com.sql.scheduler.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Admin implements Serializable {
	@Id
	@Column
	private String username;

	@JsonIgnore
	@Column
	private String password;

	@Column(columnDefinition = "E")
	private String status;

	@JsonIgnore
	@Column
	private String cert_key;

	@Column
	private String dept;

	@Column(insertable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date reg_dt;
}
