package com.hzpd.modle;

import java.util.Arrays;

public class XF_UserCommNewsBean {



	private String[] imgs;
	private String nid;//": "133",
	private String tid;
	private String title;//": "石家庄一大学生保安揪出偷包贼 派出所送来表扬信",
	private String type;//": 新闻1  图片2  直播3    （视频4 暂无 ）  引用7
	private String update_time;
	private String url;//": "http://58.68.134.165:8081/cms_json/zqzx/News/Content/197001/01/133",

	private String smallimgurl;
	private String copyfrom;//新闻来源
	private String fav;
	private String comcount;//": "0 ",评论数


	public String getComcount() {
		return comcount;
	}

	public void setComcount(String comcount) {
		this.comcount = comcount;
	}

	public String[] getImgs() {
		return imgs;
	}

	public void setImgs(String[] imgs) {
		this.imgs = imgs;
	}

	@Override
	public String toString() {
		return "MyCommentBean{" +
				"imgs=" + Arrays.toString(imgs) +
				", nid='" + nid + '\'' +
				", tid='" + tid + '\'' +
				", title='" + title + '\'' +
				", type='" + type + '\'' +
				", update_time='" + update_time + '\'' +
				", url='" + url + '\'' +
				", smallimgurl='" + smallimgurl + '\'' +
				", copyfrom='" + copyfrom + '\'' +
				", fav='" + fav + '\'' +
				'}';
	}

	public String getNid() {
		return nid;
	}

	public void setNid(String nid) {
		this.nid = nid;
	}

	public String getTid() {
		return tid;
	}

	public void setTid(String tid) {
		this.tid = tid;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUpdate_time() {
		return update_time;
	}

	public void setUpdate_time(String update_time) {
		this.update_time = update_time;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getSmallimgurl() {
		return smallimgurl;
	}

	public void setSmallimgurl(String smallimgurl) {
		this.smallimgurl = smallimgurl;
	}

	public String getCopyfrom() {
		return copyfrom;
	}

	public void setCopyfrom(String copyfrom) {
		this.copyfrom = copyfrom;
	}

	public String getFav() {
		return fav;
	}

	public void setFav(String fav) {
		this.fav = fav;
	}
}