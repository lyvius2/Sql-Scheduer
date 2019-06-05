package com.sql.scheduler.code;

public enum AgreeStatus {
	WAIT("대기"), REJECT("반려"), AGREED("확인/승인");

	private String statusName;

	AgreeStatus(String statusName) {
		this.statusName = statusName;
	}

	public String getStatusName() {
		return statusName;
	}
}
