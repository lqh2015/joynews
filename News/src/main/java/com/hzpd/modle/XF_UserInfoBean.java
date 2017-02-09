package com.hzpd.modle;

public class XF_UserInfoBean {

	private String uid;// ": "1",
	private String username;// ": "张三",
	private String nickname;// ": "188****4188",
	private String avatar_path;// ": "http://www.99cms_jhxt_new.com/Public/Tipoffs/201504/24/1429870009_61094.jpg",
	private String sex;// ": "3",
	private String regtime;// ": "2015/04/13 16:13",
	private String exp;// ": "500",
	private String level;// ": "V4",
	private String alias;// ": 等级头衔
	private String lastexp;// ": "1500"

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getAvatar_path() {
		return avatar_path;
	}

	public void setAvatar_path(String avatar_path) {
		this.avatar_path = avatar_path;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getRegtime() {
		return regtime;
	}

	public void setRegtime(String regtime) {
		this.regtime = regtime;
	}

	public String getExp() {
		return exp;
	}

	public void setExp(String exp) {
		this.exp = exp;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getLastexp() {
		return lastexp;
	}

	public void setLastexp(String lastexp) {
		this.lastexp = lastexp;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	@Override
	public String toString() {
		return "XF_UserInfoBean{" +
				"uid='" + uid + '\'' +
				", username='" + username + '\'' +
				", nickname='" + nickname + '\'' +
				", avatar_path='" + avatar_path + '\'' +
				", sex='" + sex + '\'' +
				", regtime='" + regtime + '\'' +
				", exp='" + exp + '\'' +
				", level='" + level + '\'' +
				", alias='" + alias + '\'' +
				", lastexp='" + lastexp + '\'' +
				'}';
	}
}
