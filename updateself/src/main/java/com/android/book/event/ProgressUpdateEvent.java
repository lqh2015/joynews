package com.android.book.event;

/** 本应用更新进度 */
public class ProgressUpdateEvent {
	public int progress;

	public ProgressUpdateEvent(int value) {
		progress = value;
	}
}
