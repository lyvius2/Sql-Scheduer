package com.sql.scheduler.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sql.scheduler.code.AdminStatus;
import com.sql.scheduler.code.AdminType;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(indexes = {@Index(name = "index_admin_username", columnList = "username", unique = true)})
@Data
public class Admin implements Serializable {
	@Size(min = 4, max = 50, message = "ID는 4글자 이상 50글자 미만으로 지정하십시오.")
	@Id
	@Column(name = "username")
	private String username;

	@NotNull
	//@Size(min = 8, max = 20, message = "비밀번호는 8자리 이상 20자리 미만으로 지정하십시오.")
	//@Pattern(regexp = "^(?=.*\\d)(?=.*[~`!@#$%\\^&*()-])(?=.*[a-zA-Z]).{8,20}$", message = "영문과 숫자 그리고 특수 문자를 포함하여야 합니다.")
	@JsonIgnore
	@Column(name = "password")
	private String password;

	@NotNull(message = "필수 입력 값입니다.")
	@Column(name = "name")
	private String name;

	@Email(message = "E-Mail 양식이 아닙니다.")
	@Column(name = "email")
	private String email;

	@Enumerated(EnumType.STRING)
	@Column(name = "status")
	private AdminStatus status = AdminStatus.E;

	@Enumerated(EnumType.STRING)
	@Column(name = "type")
	private AdminType type = AdminType.ADMIN;

	@Column(name = "dept")
	private String dept;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "reg_dt")
	private Date regDt = new Date();

	@Column(insertable = false, name = "mod_dt")
	@Temporal(TemporalType.TIMESTAMP)
	private Date modDt;

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}
}
