package com.sql.scheduler.entity;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

@Data
@RequiredArgsConstructor
public class Mail implements Serializable {
	private String from;
	private String[] to;
	private String subject;
	private String text;
}
