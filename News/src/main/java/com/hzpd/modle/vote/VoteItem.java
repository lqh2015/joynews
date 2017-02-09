package com.hzpd.modle.vote;

import java.io.Serializable;


public class VoteItem implements Serializable {
	private static final long serialVersionUID = 1L;

	private String optionid;//":"329",
	private String subjectid;//":"59",
	private String sort_order;//":"0",
	private String type;//":"3",
	private String rate;//":"15.63%"
	private String votenum;
	private VoteItemOpt option;//":"

	public String getOptionid() {
		return optionid;
	}

	public void setOptionid(String optionid) {
		this.optionid = optionid;
	}

	public String getSubjectid() {
		return subjectid;
	}

	public void setSubjectid(String subjectid) {
		this.subjectid = subjectid;
	}

	public String getSort_order() {
		return sort_order;
	}

	public void setSort_order(String sort_order) {
		this.sort_order = sort_order;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getRate() {
		return rate;
	}

	public void setRate(String rate) {
		this.rate = rate;
	}

	public VoteItemOpt getOption() {
		return option;
	}

	public void setOption(VoteItemOpt option) {
		this.option = option;
	}

	public String getVotenum() {
		return votenum;
	}

	public void setVotenum(String votenum) {
		this.votenum = votenum;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof VoteItem) {
			if (((VoteItem) o).getOptionid().equals(optionid)) {
				return true;
			}
		}

		return false;
	}


}
