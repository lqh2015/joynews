package com.hzpd.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ProgressBar;

public class MyProgressBar extends ProgressBar {
	private int mBgColor = 0xffE0F0EF;
	private int mProgressColor = 0xff00B5B6;
	private float mCurProgress = 0f;
	private float mMaxProgress = 100f;

	public MyProgressBar(Context context) {
		super(context);
		init();
	}

	public MyProgressBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public MyProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	private Paint mPaintBg;
	private Paint mPaintProgress;

	private void init() {
		setLayerType(LAYER_TYPE_SOFTWARE, null);
		setWillNotDraw(false);
		mPaintBg = new Paint();
		mPaintBg.setAntiAlias(true);
		mPaintBg.setStyle(Paint.Style.FILL);
		mPaintBg.setColor(mBgColor);

		mPaintProgress = new Paint();
		mPaintProgress.setAntiAlias(true);
		mPaintProgress.setStyle(Paint.Style.FILL);
		mPaintProgress.setColor(mProgressColor);
	}

	public void setProgress(int progress) {
		mCurProgress = progress;
		requestLayout();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// Draw background
		canvas.drawRect(0, 0, getWidth(), getHeight(), mPaintBg);
		float right = getWidth() * (mCurProgress / mMaxProgress) + 3;
		canvas.drawRect(0, 0, right, getHeight(), mPaintProgress);

	}
}
