package com.hzpd.modle;

import java.util.List;

public class XF_UserCommentsBean {
	private XF_UserCommNewsBean content;
	private List<XF_CommentBean> coms;

	@Override
	public String toString() {
		return "XF_UserCommentsBean{" +
				"content=" + content +
				", coms=" + coms +
				'}';
	}

	public XF_UserCommNewsBean getContent() {
		return content;
	}

	public void setContent(XF_UserCommNewsBean content) {
		this.content = content;
	}

	public List<XF_CommentBean> getComs() {
		return coms;
	}

	public void setComs(List<XF_CommentBean> coms) {
		this.coms = coms;
	}
}
