package com.hzpd.ui.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * 释放图片内存
 */
public class FixedImageView extends ImageView {

    public FixedImageView(Context context) {
        super(context);
    }

    public FixedImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    BitmapDrawable bitmapDrawable;

    @Override
    public void setImageBitmap(Bitmap bm) {
        if (!bm.isRecycled()) {
            super.setImageBitmap(bm);
            bitmapDrawable = (BitmapDrawable) getDrawable();
        }
    }

}
