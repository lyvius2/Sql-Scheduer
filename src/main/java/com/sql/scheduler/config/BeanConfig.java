package com.sql.scheduler.config;

import com.google.gson.Gson;
import com.sql.scheduler.component.AES256;
import nz.net.ultraq.thymeleaf.LayoutDialect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {
	@Bean
	public Gson gson() {
		return new Gson();
	}

	@Bean
	public LayoutDialect layoutDialect() {
		return new LayoutDialect();
	}

	@Bean
	public AES256 aes256() throws Exception {
		return new AES256();
	}
}
