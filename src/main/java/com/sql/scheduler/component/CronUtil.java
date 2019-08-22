package com.sql.scheduler.component;

import com.cronutils.descriptor.CronDescriptor;
import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.parser.CronParser;

import java.util.Locale;

public class CronUtil {
	/**
	 * Cron 데이터 Parser
	 * @comment Cron 형식의 값이 유효한지 체크하여 유효하면 Parsing 값을, 유효하지 않으면 Null로 반환
	 * @param str
	 * @return
	 */
	public static String cronParser(String str) {
		CronDefinition cronDefinition = CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ);
		CronParser cronParser = new CronParser(cronDefinition);
		try {
			Cron cron = cronParser.parse(str).validate();
			CronDescriptor descriptor = CronDescriptor.instance(Locale.KOREA);
			return descriptor.describe(cron);
		} catch(Exception e) {
			e.getStackTrace();
			return null;
		}
	}
}
