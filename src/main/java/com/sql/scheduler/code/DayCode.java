package com.sql.scheduler.code;

public enum DayCode {
	Sun("Sunday", "일요일", 1), Mon("Monday", "월요일", 2), Tue("Tuesday", "화요일", 3), Wed("Wednesday", "수요일", 4),
	Thu("Thursday", "목요일", 5), Fri("Friday", "금요일", 6), Sat("Saturday", "토요일", 7);

	private String fullname;

	private String fullname_kor;

	private int seq;

	DayCode(String fullname, String fullname_kor, int seq) {
		this.fullname = fullname;
		this.fullname_kor = fullname_kor;
		this.seq = seq;
	}

	public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	public String getFullname_kor() {
		return fullname_kor;
	}

	public void setFullname_kor(String fullname_kor) {
		this.fullname_kor = fullname_kor;
	}

	public int getSeq() {
		return seq;
	}

	public void setSeq(int seq) {
		this.seq = seq;
	}
}
