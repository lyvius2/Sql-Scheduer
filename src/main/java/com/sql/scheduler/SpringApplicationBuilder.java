package com.sql.scheduler;

import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

public class SpringApplicationBuilder extends SpringBootServletInitializer {
	@Override
	protected org.springframework.boot.builder.SpringApplicationBuilder configure(org.springframework.boot.builder.SpringApplicationBuilder builder) {
		return builder.sources(SchedulerApplication.class);
	}
}
