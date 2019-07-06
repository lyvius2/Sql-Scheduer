package com.sql.scheduler.entity;

import com.sql.scheduler.code.ResultStatus;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document(collection = "taskLog")
public class TaskLog {
	@Id
	private String _id;

	@Indexed
	private int groupSeq;

	@Indexed
	private int jobSeq;

	private String targetTable;
	private String jobDescription;
	private Date beginTime;
	private Date endTime;
	private long proceedTime;
	private String executedSql;
	private String targetData;
	private int targetCount;
	private ResultStatus resultStatus;
	private String errorMsg;
}
