package com.hzpd.modle.vote;

import java.io.Serializable;


public class VoteBaseInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	private String subjectid;//、、":"59",
	private String subject;//":"投票主题的名称",
	private String addtime;//":"2014-08-27",
	private String type;//":"0",单选 多选
	private String imgurl;//":"http://www.99cms_ts.com/Public/Attachment/2014/06/29/s_20140629122609_941.jpg",
	private String description;//":"投票规则描述或投票详情",
	private String substat;//":0
	private String lottery;

	public String getSubjectid() {
		return subjectid;
	}

	public void setSubjectid(String subjectid) {
		this.subjectid = subjectid;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getAddtime() {
		return addtime;
	}

	public void setAddtime(String addtime) {
		this.addtime = addtime;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getImgurl() {
		return imgurl;
	}

	public void setImgurl(String imgurl) {
		this.imgurl = imgurl;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getSubstat() {
		return substat;
	}

	public void setSubstat(String substat) {
		this.substat = substat;
	}

	public String getLottery() {
		return lottery;
	}

	public void setLottery(String lottery) {
		this.lottery = lottery;
	}


}
