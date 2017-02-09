package com.hzpd.modle;

import java.io.Serializable;

public class NewsDetailImgList implements Serializable {
	private static final long serialVersionUID = 2546343653886781035L;

	private String aid;
	private String subphoto;
	private String id;
	private String subdesc;


	public String getSubdesc() {
		return subdesc;
	}

	public void setSubdesc(String subdesc) {
		this.subdesc = subdesc;
	}

	public String getAid() {
		return aid;
	}

	public String getSubphoto() {
		return subphoto;
	}

	public String getId() {
		return id;
	}

	public void setAid(String aid) {
		this.aid = aid;
	}

	public void setSubphoto(String subphoto) {
		this.subphoto = subphoto;
	}

	public void setId(String id) {
		this.id = id;
	}

}
