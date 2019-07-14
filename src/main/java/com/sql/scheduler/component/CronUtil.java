package com.sql.scheduler.component;

import com.cronutils.descriptor.CronDescriptor;
import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.parser.CronParser;

import java.util.Locale;

public class CronUtil {
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
