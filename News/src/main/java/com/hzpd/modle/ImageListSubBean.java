package com.hzpd.modle;

import java.io.Serializable;

public class ImageListSubBean implements Serializable {
	private static final long serialVersionUID = 8558294341863265593L;

	private String subdesc;//": "张春贤：紧密结合新疆实际 全面推进依法治疆",
	private String subphoto;//": "http://www.99cms.com//Public/Attachment/2014/10/29/20141029020723_191.jpg"

	public ImageListSubBean() {
	}

	public ImageListSubBean(String subdesc, String subphoto) {
		this.subdesc = subdesc;
		this.subphoto = subphoto;
	}

	public String getSubdesc() {
		return subdesc;
	}

	public String getSubphoto() {
		return subphoto;
	}

	public void setSubdesc(String subdesc) {
		this.subdesc = subdesc;
	}

	public void setSubphoto(String subphoto) {
		this.subphoto = subphoto;
	}

}
