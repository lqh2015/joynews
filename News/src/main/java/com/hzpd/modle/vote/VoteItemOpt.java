package com.hzpd.modle.vote;


public class VoteItemOpt {
	private String name;//":"选项名称1",
	private String imgurl;//":"http://www.99cms_ts.com/Public/Uploads/vote/1409126456.jpg",
	private String description;//":"选项详情",
	private String status;//":0


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getImgurl() {
		return imgurl;
	}

	public void setImgurl(String imgurl) {
		this.imgurl = imgurl;
	}


}
