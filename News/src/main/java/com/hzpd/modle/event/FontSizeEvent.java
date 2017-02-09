package com.hzpd.modle.event;

public class FontSizeEvent {
	private int fontSize;

	public FontSizeEvent() {
	}

	public FontSizeEvent(int fontSize) {
		super();
		this.fontSize = fontSize;
	}

	private String mMsg;

	public FontSizeEvent(String mMsg) {
		this.mMsg=mMsg;
	}

	public String getMsg(){
		return mMsg;
	}

	public int getFontSize() {
		return fontSize;
	}

	public void setFontSize(int fontSize) {
		this.fontSize = fontSize;
	}


}