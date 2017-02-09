package com.hzpd.modle.vote;


public class VoteEntity {
	private String term;
	private Double percent;
	private int optionid;
	public static int mWidth = 300;

	public VoteEntity() {
		super();
	}

	public VoteEntity(String term, Double percent, int optionid) {
		super();
		this.term = term;
		this.percent = percent;
		this.optionid = optionid;
	}

	public int getOptionid() {
		return optionid;
	}

	public void setOptionid(int optionid) {
		this.optionid = optionid;
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public Double getPercent() {
		return percent;
	}

	public void setPercent(Double percent) {
		this.percent = percent;
	}

	public static int getmWidth() {
		return mWidth;
	}

	public static void setmWidth(int mWidth) {
		VoteEntity.mWidth = mWidth;
	}

}
