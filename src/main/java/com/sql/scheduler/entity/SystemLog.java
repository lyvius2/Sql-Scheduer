package com.sql.scheduler.entity;

import com.sql.scheduler.code.ResultStatus;
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

	private ResultStatus status;
	private String method;
	private String username;
	private String ip;
	private String requestPath;
	private String params;
	private String comment;
	private String message;
	private String traceLog;
	private Date beginTime;
	private Date endTime;
	private long proceedTime;
}
