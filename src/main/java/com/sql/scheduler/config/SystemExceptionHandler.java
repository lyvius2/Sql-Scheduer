package com.sql.scheduler.config;

import com.sql.scheduler.code.ResultStatus;
import com.sql.scheduler.component.LoggingUtil;
import com.sql.scheduler.entity.SystemLog;
import com.sql.scheduler.repository.SystemLogRepository;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class SystemExceptionHandler {
	@Autowired
	private LoggingUtil loggingUtil;

	@Autowired
	private SystemLogRepository systemLogRepository;

	@ExceptionHandler(Exception.class)
	public void handleException(Exception e, HttpServletRequest request) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		SystemLog systemErrorLog = loggingUtil.createLogging(request, ResultStatus.ERROR, auth != null ? auth.getName():null);
		systemErrorLog.setMessage(e.toString());
		systemErrorLog.setTraceLog(ExceptionUtils.getStackTrace(e));
		systemLogRepository.save(systemErrorLog);
	}
}
