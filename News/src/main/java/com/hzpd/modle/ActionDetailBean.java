package com.hzpd.modle;

public class ActionDetailBean {
	private String id;// : "13",
	private String title;// : "我要上封面",
	private String headpic;// :
	private String content;// : "<span style="font-family: 楷体; font-size:
	private String place;// : "河北",
	private String starttime;// : "2014-12-14 00:00:12",
	private String deadline;// : "2015-12-01 00:00:12",
	private String numofperson;// : "1",
	private String siteid;// : "2",
	private String bigpic;// :
	private String voteable;// 1可投票
	private String rollable;// 1可抽奖
	private String regable;// 1可报名

	private String subjectid;
	private String url;// 活动分享

	public String getRegable() {
		return regable;
	}

	public void setRegable(String regable) {
		this.regable = regable;
	}

	public String getSubjectid() {
		return subjectid;
	}

	public void setSubjectid(String subjectid) {
		this.subjectid = subjectid;
	}

	public String getVoteable() {
		return voteable;
	}

	public String getRollable() {
		return rollable;
	}

	public void setVoteable(String voteable) {
		this.voteable = voteable;
	}

	public void setRollable(String rollable) {
		this.rollable = rollable;
	}

	public String getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getHeadpic() {
		return headpic;
	}

	public String getContent() {
		return content;
	}

	public String getPlace() {
		return place;
	}

	public String getStarttime() {
		return starttime;
	}

	public String getDeadline() {
		return deadline;
	}

	public String getNumofperson() {
		return numofperson;
	}

	public String getBigpic() {
		return bigpic;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setHeadpic(String headpic) {
		this.headpic = headpic;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public void setStarttime(String starttime) {
		this.starttime = starttime;
	}

	public void setDeadline(String deadline) {
		this.deadline = deadline;
	}

	public void setNumofperson(String numofperson) {
		this.numofperson = numofperson;
	}

	public String getSiteid() {
		return siteid;
	}

	public void setSiteid(String siteid) {
		this.siteid = siteid;
	}

	public void setBigpic(String bigpic) {
		this.bigpic = bigpic;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
