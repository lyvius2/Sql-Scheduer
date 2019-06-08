package com.sql.scheduler.code;

public enum MonthCode {
	JAN("January", 1), FEB("February", 2), MAR("March", 3), APR("April", 4), MAY("May", 5), JUN("June", 6),
	JUL("July", 7), AUG("August", 8), SEP("September", 9), OCT("October", 10), NOV("November", 11), DEC("December", 12);

	private String fullname;

	private int seq;

	MonthCode(String fullname, int seq) {
		this.fullname = fullname;
		this.seq = seq;
	}

	public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	public int getSeq() {
		return seq;
	}

	public void setSeq(int seq) {
		this.seq = seq;
	}
}
