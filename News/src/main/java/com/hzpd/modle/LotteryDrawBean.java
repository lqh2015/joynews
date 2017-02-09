package com.hzpd.modle;

public class LotteryDrawBean {

	private String status;//" : 1,
	private String level;//" : "一等奖",
	private String prize;//" : "iphone",
	private String number;//": "123456",
	private String act_lastvotes;//": 4,
	private String tod_lastvotes;//": 2,
	private String lastdraw;//": 1,
	private String info;//": "本活动游客每日能抽奖2次，登录用户每日能抽奖2次"


	public String getStatus() {
		return status;
	}

	public String getLevel() {
		return level;
	}

	public String getNumber() {
		return number;
	}

	public String getAct_lastvotes() {
		return act_lastvotes;
	}

	public String getTod_lastvotes() {
		return tod_lastvotes;
	}

	public String getLastdraw() {
		return lastdraw;
	}

	public String getInfo() {
		return info;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public void setAct_lastvotes(String act_lastvotes) {
		this.act_lastvotes = act_lastvotes;
	}

	public void setTod_lastvotes(String tod_lastvotes) {
		this.tod_lastvotes = tod_lastvotes;
	}

	public void setLastdraw(String lastdraw) {
		this.lastdraw = lastdraw;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getPrize() {
		return prize;
	}

	public void setPrize(String prize) {
		this.prize = prize;
	}

}
