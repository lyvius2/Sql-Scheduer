package com.sql.scheduler.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class TaskLoggingAspect {
	@Pointcut("execution(* com.sql.scheduler.component.TaskDao.*(..))")
	public void onJointCut() {}

	@Before("onJointCut()")
	public void printBeforeLogging(JoinPoint joinPoint) {
		log.info("----------------------->> before");
	}

	@After("onJointCut()")
	public void printAfterLogging(JoinPoint joinPoint) {
		log.info("----------------------->> after");
	}
}
