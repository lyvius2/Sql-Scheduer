package com.sql.scheduler.component;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * application.properties 에서 설정한 Property Model
 */
@Component
@ConfigurationProperties(prefix = "system.super.admin")
@Data
public class Administrator {
	private String mail;
	private String deleteTargetDataLogCron;
	private int deleteTargetDataBeforeDay;
	private int dataBackupMaxRows;
}
