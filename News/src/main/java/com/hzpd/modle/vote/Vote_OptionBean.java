package com.hzpd.modle.vote;


import java.util.List;

public class Vote_OptionBean {
	private String name;// ":"选项名称1",
	private List<String> imgurls;// ": [
	// "http://www.99cms_hb.info/Public/Tipoffs/201412/19/s_1418967461_59440.jpeg",
	// "http://www.99cms_hb.info/Public/Tipoffs/201412/19/s_1418967461_57542.jpeg",
	// "http://www.99cms_hb.info/Public/Tipoffs/201412/19/s_1418967462_81751.jpeg"
	// ],
	private String description;// ":"选项简介",
	private String status;// ":"0"

	public String getName() {
		return name;
	}

	public List<String> getImgurls() {
		return imgurls;
	}

	public String getDescription() {
		return description;
	}

	public String getStatus() {
		return status;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setImgurls(List<String> imgurls) {
		this.imgurls = imgurls;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}

