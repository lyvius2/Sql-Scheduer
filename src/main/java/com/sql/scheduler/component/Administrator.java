package com.sql.scheduler.component;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "system.super.admin")
@Data
public class Administrator {
	private String mail;
	private String deleteTargetDataLogCron;
	private int deleteTargetDataBeforeDay;
}
