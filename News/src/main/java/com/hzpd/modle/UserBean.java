package com.hzpd.modle;

import java.io.Serializable;

public class UserBean implements Serializable {
	private static final long serialVersionUID = 1L;

	private String uid;//": "10",
	private String mobile;//": "13340116539",
	private String avatar_path;//": "http://www.99cms_ts.com/mages/noavatar_small.gif",
	private String username;//": "niming10",
	private String nickname;//": "匿名用户10",
	private String sex;//": "3",
	private String ucenterid;//": "701815"
	private String token;


	public String getUid() {
		return uid;
	}

	public String getMobile() {
		return mobile;
	}

	public String getAvatar_path() {
		return avatar_path;
	}

	public String getUsername() {
		return username;
	}

	public String getNickname() {
		return nickname;
	}

	public String getSex() {
		return sex;
	}

	public String getUcenterid() {
		return ucenterid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public void setAvatar_path(String avatar_path) {
		this.avatar_path = avatar_path;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public void setUcenterid(String ucenterid) {
		this.ucenterid = ucenterid;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
}
