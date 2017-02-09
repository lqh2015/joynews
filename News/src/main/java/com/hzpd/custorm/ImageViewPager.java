package com.hzpd.custorm;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * @author lixinyuan
 *         解决photoview ava.lang.IllegalArgumentException: pointerIndex out of range
 */

public class ImageViewPager extends ViewPager {


	public ImageViewPager(Context context) {
		super(context);
	}

	public ImageViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent arg0) {
		try {
			return super.onInterceptTouchEvent(arg0);
		} catch (IllegalArgumentException e) {

		} catch (ArrayIndexOutOfBoundsException e) {

		}
		return false;
	}

}