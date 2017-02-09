package com.hzpd.modle;

public class CommentzqzxBean {

	private String nickname;//": "匿名用户11",
	private String cid;//": "7",
	private String avatar_path;//": "www.99cms_hb.info/Public/avatar/icon_default.png",
	private String dateline;//": "2014-08-15 11:11:01",
	private String uid;//": "11",
	private String praise;//": "35",
	private String content;//": "第一次评论",;
	private String status;//1审核通过 -2审核中


	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getCid() {
		return cid;
	}

	public void setCid(String cid) {
		this.cid = cid;
	}

	public String getAvatar_path() {
		return avatar_path;
	}

	public void setAvatar_path(String avatar_path) {
		this.avatar_path = avatar_path;
	}

	public String getDateline() {
		return dateline;
	}

	public void setDateline(String dateline) {
		this.dateline = dateline;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getPraise() {
		return praise;
	}

	public void setPraise(String praise) {
		this.praise = praise;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public String toString() {
		return "CommentzqzxBean{" +
				"nickname='" + nickname + '\'' +
				", cid='" + cid + '\'' +
				", avatar_path='" + avatar_path + '\'' +
				", dateline='" + dateline + '\'' +
				", uid='" + uid + '\'' +
				", praise='" + praise + '\'' +
				", content='" + content + '\'' +
				", status='" + status + '\'' +
				'}';
	}
}
