package com.hzpd.custorm.smoothimg;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView.ScaleType;

import com.color.tools.mytools.LogUtils;
import com.hzpd.ui.activity.MBaseActivity;
import com.hzpd.utils.DisplayOptionFactory;
import com.hzpd.utils.SPUtil;

import java.io.File;

public class SpaceImageDetailActivity extends MBaseActivity {

	private int mLocationX;
	private int mLocationY;
	private int mWidth;
	private int mHeight;
	private SmoothImageView imageView = null;
	private String imageUrl;
	private String type;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		type = getIntent().getStringExtra("type");
		imageUrl = getIntent().getStringExtra("images");
		mLocationX = getIntent().getIntExtra("locationX", 0);
		mLocationY = getIntent().getIntExtra("locationY", 0);
		mWidth = getIntent().getIntExtra("width", 0);
		mHeight = getIntent().getIntExtra("height", 0);

		LogUtils.i("" + imageUrl + "  mx-->" + mLocationX + " my-->"
				+ mLocationY + " mw-->" + mWidth + " mh-->" + mHeight);

		imageView = new SmoothImageView(this);
		imageView.setOriginalInfo(mWidth, mHeight, mLocationX, mLocationY);
		imageView.transformIn();
		imageView.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
		imageView.setScaleType(ScaleType.FIT_CENTER);
		setContentView(imageView);

		final ScaleAnimation scaleAnimation = new ScaleAnimation(0.5f, 1.0f, 0.5f,
				1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		scaleAnimation.setDuration(300);
		scaleAnimation.setInterpolator(new AccelerateInterpolator());

		String displayString;
		if ("uri".equals(type)) {
			Uri fileUri = Uri.fromFile(new File(imageUrl));
			displayString = fileUri.toString();
		} else {
			displayString = imageUrl;
		}

		SPUtil.displayImage(displayString
				, imageView
				, DisplayOptionFactory.Small.options
				);

		imageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});

	}

	@Override
	public void onBackPressed() {
		imageView.setOnTransformListener(new SmoothImageView.TransformListener() {
			@Override
			public void onTransformComplete(int mode) {
				if (mode == 2) {
					finish();
				}
			}
		});
		imageView.transformOut();
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (isFinishing()) {
			overridePendingTransition(0, 0);
		}
	}

}