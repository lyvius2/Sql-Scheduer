package com.sql.scheduler.code;

/**
 * 사용자 타입
 */
public enum AdminType {
	ADMIN("일반 관리자"), DEVELOPER("개발자"), SUPER_ADMIN("수퍼 관리자");

	private String typeName;

	AdminType(String typeName) {
		this.typeName = typeName;
	}

	public String getTypeName() {
		return typeName;
	}
}
