package com.hzpd.modle;

public class CacheBean {

	private String cacheid;//: "9",
	private String update_time;//: "1429262795",
	private String module;//: "news",
	private String tid;//: "0",
	private String download;//: null,
	private String act;//: "del",
	private String option;//: "list",
	private String id;//: "3618"


	public String getCacheid() {
		return cacheid;
	}

	public String getUpdate_time() {
		return update_time;
	}

	public String getModule() {
		return module;
	}

	public String getTid() {
		return tid;
	}

	public String getDownload() {
		return download;
	}

	public String getAct() {
		return act;
	}

	public String getOption() {
		return option;
	}

	public String getId() {
		return id;
	}

	public void setCacheid(String cacheid) {
		this.cacheid = cacheid;
	}

	public void setUpdate_time(String update_time) {
		this.update_time = update_time;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public void setTid(String tid) {
		this.tid = tid;
	}

	public void setDownload(String download) {
		this.download = download;
	}

	public void setAct(String act) {
		this.act = act;
	}

	public void setOption(String option) {
		this.option = option;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "CacheBean{" +
				"cacheid='" + cacheid + '\'' +
				", update_time='" + update_time + '\'' +
				", module='" + module + '\'' +
				", tid='" + tid + '\'' +
				", download='" + download + '\'' +
				", act='" + act + '\'' +
				", option='" + option + '\'' +
				", id='" + id + '\'' +
				'}';
	}
}
