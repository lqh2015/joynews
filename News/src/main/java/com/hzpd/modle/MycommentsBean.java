package com.hzpd.modle;

import java.util.List;

public class MycommentsBean {

	private String nid;//": "133",
	private String tid;
	private String title;//": "石家庄一大学生保安揪出偷包贼 派出所送来表扬信",
	private String type;//": 新闻1  图片2  直播3    （视频4 暂无 ）  引用7
	private String update_time;
	private String url;//": "http://58.68.134.165:8081/cms_json/zqzx/News/Content/197001/01/133",

	private NewsBean bean;
	private MyCommentBean content;
	private List<MycommentsitemBean> coms;//":

	@Override
	public String toString() {
		return "MycommentsBean{" +
				"nid='" + nid + '\'' +
				", tid='" + tid + '\'' +
				", title='" + title + '\'' +
				", type='" + type + '\'' +
				", update_time='" + update_time + '\'' +
				", url='" + url + '\'' +
				", content=" + content +
				", coms=" + coms +
				", smallimgurl='" + smallimgurl + '\'' +
				", copyfrom='" + copyfrom + '\'' +
				", fav='" + fav + '\'' +
				'}';
	}

	public MyCommentBean getContent() {
		return content;
	}

	public void setContent(MyCommentBean content) {
		this.content = content;
	}



	private String smallimgurl;
	private String copyfrom;//新闻来源
	private String fav;


	public String getFav() {
		return fav;
	}

	public void setFav(String fav) {
		this.fav = fav;
	}

	public String getCopyfrom() {
		return copyfrom;
	}

	public void setCopyfrom(String copyfrom) {
		this.copyfrom = copyfrom;
	}



	public String getNid() {
		return nid;
	}

	public void setNid(String nid) {
		this.nid = nid;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSmallimgurl() {
		return smallimgurl;
	}

	public void setSmallimgurl(String smallimgurl) {
		this.smallimgurl = smallimgurl;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public List<MycommentsitemBean> getComs() {
		return coms;
	}

	public void setComs(List<MycommentsitemBean> coms) {
		this.coms = coms;
	}

	public String getTid() {
		return tid;
	}

	public void setTid(String tid) {
		this.tid = tid;
	}

	public String getUpdate_time() {
		return update_time;
	}

	public void setUpdate_time(String update_time) {
		this.update_time = update_time;
	}

}