package com.hzpd.modle;

public class PushmsgBean {
	private String articleid;//": "190",
	private String atype;//": "1",
	private String time;//": "2014-10-21",
	private String smallimgurl;//": "http://www.99cms_hb.info/Public/Attachment/2014/09/27/s_20140927183315_931.jpg",
	private String title;//": "武汉：大学生外租房里的梦想",
	private String comcount;//": "0",
	private String sid;//": "0",
	private String type;//": "2"

	public PushmsgBean() {
	}

	public PushmsgBean(String articleid, String atype, String time,
	                   String smallimgurl, String title, String sid,
	                   String type) {
		this.articleid = articleid;
		this.atype = atype;
		this.time = time;
		this.smallimgurl = smallimgurl;
		this.title = title;
		this.sid = sid;
		this.type = type;
	}

	public String getArticleid() {
		return articleid;
	}

	public String getAtype() {
		return atype;
	}

	public String getTime() {
		return time;
	}

	public String getSmallimgurl() {
		return smallimgurl;
	}

	public String getTitle() {
		return title;
	}

	public String getSid() {
		return sid;
	}

	public String getType() {
		return type;
	}

	public void setArticleid(String articleid) {
		this.articleid = articleid;
	}

	public void setAtype(String atype) {
		this.atype = atype;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public void setSmallimgurl(String smallimgurl) {
		this.smallimgurl = smallimgurl;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getComcount() {
		return comcount;
	}

	public void setComcount(String comcount) {
		this.comcount = comcount;
	}


}
