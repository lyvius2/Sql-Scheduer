package com.sql.scheduler.config;

import com.sql.scheduler.code.ResultStatus;
import com.sql.scheduler.component.LoggingUtil;
import com.sql.scheduler.entity.DeleteLog;
import com.sql.scheduler.entity.SystemLog;
import com.sql.scheduler.repository.DeleteLogRepository;
import com.sql.scheduler.repository.SystemLogRepository;
import com.sql.scheduler.service.LoginAdminDetails;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
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

	@Autowired
	private DeleteLogRepository deleteLogRepository;

	@Around("execution(* com.sql.scheduler.controller.AdminController.*(..)) || execution(* com.sql.scheduler.controller.LogController.log(..)) || " +
			"execution(* com.sql.scheduler.controller.LoginController.*(..)) || execution(* com.sql.scheduler.controller.TaskController.*(..))")
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

	@AfterReturning(pointcut = "execution(* com.sql.scheduler.controller.LogController.removeLog(..))", returning = "count")
	public void onAfterReturningHandler(JoinPoint joinPoint, Object count) {
		Object[] referenceObj = joinPoint.getArgs();
		String flag = (String)referenceObj[0];
		LoginAdminDetails admin = (LoginAdminDetails)referenceObj[1];
		DeleteLog log = new DeleteLog();
		log.setDeleteTarget(flag.equals("task") ? "스케줄 로그 삭제":"접속 기록 삭제");
		log.setTargetCount((int)count);
		log.setUsername(admin.getUsername());
		deleteLogRepository.save(log);
	}
}
