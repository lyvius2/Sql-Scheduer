package com.sql.scheduler.code;

public enum AdminStatus {
	E("대기"), Y("사용"), N("미사용");

	private String statusName;

	AdminStatus(String statusName) {
		this.statusName = statusName;
	}

	public String getStatusName() {
		return statusName;
	}
}
