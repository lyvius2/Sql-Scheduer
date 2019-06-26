package com.sql.scheduler.code;

public enum ResultStatus {
	SUCCESS("성공"), FAILURE("실패"), ERROR("에러"), NOTPERFORMANCE("미수행");

	private String statusName;

	ResultStatus(String statusName) {
		this.statusName = statusName;
	}

	public String getStatusName() {
		return statusName;
	}
}
