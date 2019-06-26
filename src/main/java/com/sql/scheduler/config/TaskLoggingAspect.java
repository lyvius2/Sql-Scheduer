package com.sql.scheduler.config;

import com.sql.scheduler.code.ResultStatus;
import com.sql.scheduler.entity.Job;
import com.sql.scheduler.entity.TaskLog;
import com.sql.scheduler.repository.TaskLogRepository;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Aspect
@Component
public class TaskLoggingAspect {
	@Autowired
	private TaskLogRepository taskLogRepository;

	@Pointcut("execution(* com.sql.scheduler.component.TaskDao.executeTask(..))")
	public void onJointCut() {}

	@Before("onJointCut()")
	public void onBeforeHandler(JoinPoint joinPoint) {
		Object[] referenceObj = joinPoint.getArgs();
		Job job = (Job)referenceObj[0];
		((TaskLog)referenceObj[1]).setBeginTime(new Date());
		((TaskLog)referenceObj[1]).setGroupSeq(job.getGroupSeq());
		((TaskLog)referenceObj[1]).setJobSeq(job.getJobSeq());
		((TaskLog)referenceObj[1]).setJobDescription(job.getJobDescription());
		((TaskLog)referenceObj[1]).setTargetTable(job.getTargetTable());
	}

	@AfterReturning(pointcut = "onJointCut()", returning = "taskLog")
	public void onAfterReturningHandler(JoinPoint joinPoint, Object taskLog) {
		((TaskLog)taskLog).setEndTime(new Date());
		taskLogRepository.save((TaskLog)taskLog);
	}

	@AfterThrowing(pointcut = "onJointCut()", throwing = "exceptionObj")
	public void onAfterThrowingHandler(JoinPoint joinPoint, Exception exceptionObj) {
		Object[] referenceObj = joinPoint.getArgs();
		TaskLog taskLog = (TaskLog)referenceObj[1];
		taskLog.setResultStatus(ResultStatus.ERROR);
		taskLog.setErrorMsg(exceptionObj.toString());
		taskLog.setEndTime(new Date());
		taskLogRepository.save(taskLog);
	}
}
