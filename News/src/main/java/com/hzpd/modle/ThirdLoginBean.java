package com.hzpd.modle;

public class ThirdLoginBean {

	private String userid;
	private String gender;
	private String nickname;
	private String photo;
	private String third;

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public String getThird() {
		return third;
	}

	public void setThird(String third) {
		this.third = third;
	}

	public ThirdLoginBean(String userid, String gender, String nickname,
	                      String photo, String third) {
		this.userid = userid;

		if ("m".equals(gender)) {
			this.gender = "1";
		} else if ("f".equals(gender)) {
			this.gender = "2";
		} else {
			this.gender = "3";
		}
		this.nickname = nickname;
		this.photo = photo;
		this.third = third;
	}

	public ThirdLoginBean(){

	}

	@Override
	public String toString() {
		return "ThirdLoginBean{" +
				"userid='" + userid + '\'' +
				", gender='" + gender + '\'' +
				", nickname='" + nickname + '\'' +
				", photo='" + photo + '\'' +
				", third='" + third + '\'' +
				'}';
	}
}