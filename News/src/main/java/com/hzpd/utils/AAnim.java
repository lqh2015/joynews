package com.hzpd.utils;

import android.app.Activity;

import com.hzpd.hflt.R;

public class AAnim {

	public static void ActivityStartAnimation(Activity activity) {
		activity.overridePendingTransition(android.R.anim.slide_in_left,
				android.R.anim.fade_out);
	}

	public static void ActivityFinish(Activity activity) {
		activity.overridePendingTransition(android.R.anim.fade_in,
				android.R.anim.slide_out_right);
	}

	public static void startScreen(Activity activity) {
		activity.overridePendingTransition(android.R.anim.slide_in_left,
				android.R.anim.fade_out);
	}

	public static void bottom2top(Activity activity) {
		activity.overridePendingTransition(R.anim.bottom2top_in,
				android.R.anim.fade_out);
	}
}
