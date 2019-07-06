package com.sql.scheduler.config;

import com.sql.scheduler.code.ResultStatus;
import com.sql.scheduler.component.LoggingUtil;
import com.sql.scheduler.entity.SystemLog;
import com.sql.scheduler.repository.SystemLogRepository;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Slf4j
@Aspect
@Component
public class SystemLoggingAspect {
	@Autowired
	private HttpServletRequest request;

	@Autowired
	private LoggingUtil loggingUtil;

	@Autowired
	private SystemLogRepository systemLogRepository;

	@Around("execution(* com.sql.scheduler.controller.*.*(..))")
	public Object onAroundHandlerForLogging(ProceedingJoinPoint joinPoint) throws Throwable {
		Date beginTime = new Date();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		Object returnObj = joinPoint.proceed();

		Date endTime = new Date();
		SystemLog systemLog = loggingUtil.createLogging(request, ResultStatus.SUCCESS, auth != null ? auth.getName():null);
		systemLog.setComment(loggingUtil.createActionLogComment(request, joinPoint.getTarget().getClass().getSimpleName(), joinPoint.getSignature().getName()));
		systemLog.setBeginTime(beginTime);
		systemLog.setEndTime(endTime);
		systemLog.setProceedTime(endTime.getTime() - beginTime.getTime());
		systemLogRepository.save(systemLog);
		return returnObj;
	}
}
