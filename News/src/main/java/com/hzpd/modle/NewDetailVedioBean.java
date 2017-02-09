package com.hzpd.modle;

import java.io.Serializable;


public class NewDetailVedioBean implements Serializable {

	private String title;
	private String mainpic;
	private String videourl;
	private String copyfrom;
	private String fav;

	public String getFav() {
		return fav;
	}

	public void setFav(String fav) {
		this.fav = fav;
	}

	@Override
	public String toString() {
		return "NewDetailVedioBean{" +
				"title='" + title + '\'' +
				", mainpic='" + mainpic + '\'' +
				", videourl='" + videourl + '\'' +
				", copyfrom='" + copyfrom + '\'' +
				'}';
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getMainpic() {
		return mainpic;
	}

	public void setMainpic(String mainpic) {
		this.mainpic = mainpic;
	}

	public String getVideourl() {
		return videourl;
	}

	public void setVideourl(String videourl) {
		this.videourl = videourl;
	}

	public String getCopyfrom() {
		return copyfrom;
	}

	public void setCopyfrom(String copyfrom) {
		this.copyfrom = copyfrom;
	}


}
