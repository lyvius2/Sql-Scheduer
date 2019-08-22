package com.sql.scheduler.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * delete_log(삭제 로그) 테이블
 */
@Entity
@Table(indexes = {@Index(name = "index_delete_log", columnList = "seq", unique = true)})
@Data
public class DeleteLog {
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	private int seq;

	@Column(name = "delete_target")
	private String deleteTarget;

	@Column(name = "target_count")
	private int targetCount;

	@Column(name = "username")
	private String username;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "dt")
	private Date dt = new Date();
}
