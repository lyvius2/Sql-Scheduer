package com.sql.scheduler.code;

public enum AdminType {
	ADMIN("일반 관리자"), DEVELOPER("개발자");

	private String typeName;

	AdminType(String typeName) {
		this.typeName = typeName;
	}

	public String getTypeName() {
		return typeName;
	}
}
