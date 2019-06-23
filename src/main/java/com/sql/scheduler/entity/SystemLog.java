package com.sql.scheduler.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document(collection = "systemLog")
public class SystemLog {
	@Id
	@Indexed
	private String _id;

	private String username;
	private String comment;
	private Date createTime;
}
