package com.sql.scheduler.code;

/**
 * 작업 승인 상태
 */
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
