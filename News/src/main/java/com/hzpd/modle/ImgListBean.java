package com.hzpd.modle;

import java.io.Serializable;
import java.util.List;

public class ImgListBean implements Serializable {
	private static final long serialVersionUID = 2343307900361358461L;

	private String pid;
	private String title;
	private String count;
	private String siteid;
	private String create_time;
	private String json_url;
	private String copyfrom;//新闻来源
	private String fav;//收藏数目
	private String comcount;//": "0",

	public static long getSerialVersionUID() {
		return serialVersionUID;
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

	public String getComcount() {
		return comcount;
	}

	public void setComcount(String comcount) {
		this.comcount = comcount;
	}

	private List<ImageListSubBean> subphoto;


	public String getPid() {
		return pid;
	}

	public String getTitle() {
		return title;
	}

	public String getCount() {
		return count;
	}

	public String getSiteid() {
		return siteid;
	}

	public String getCreate_time() {
		return create_time;
	}

	public String getJson_url() {
		return json_url;
	}

	public List<ImageListSubBean> getSubphoto() {
		return subphoto;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setCount(String count) {
		this.count = count;
	}

	public void setSiteid(String siteid) {
		this.siteid = siteid;
	}

	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}

	public void setJson_url(String json_url) {
		this.json_url = json_url;
	}

	public void setSubphoto(List<ImageListSubBean> subphoto) {
		this.subphoto = subphoto;
	}

}