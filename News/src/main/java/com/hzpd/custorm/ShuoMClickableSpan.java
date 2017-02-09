package com.hzpd.custorm;

import android.content.Context;
import android.content.Intent;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import com.hzpd.hflt.R;
import com.hzpd.ui.activity.ZQ_FeedBackActivity;
import com.hzpd.utils.AvoidOnClickFastUtils;
import com.hzpd.utils.Log;

public class ShuoMClickableSpan extends ClickableSpan {

    private String feedbackFrom;
    String string;
    Context context;

    public ShuoMClickableSpan(String str, Context context) {
        super();
        this.string = str;
        this.context = context;
    }

    public ShuoMClickableSpan(String feedbackFrom, String str, Context context) {
        super();
        this.feedbackFrom = feedbackFrom;
        this.string = str;
        this.context = context;
    }

    @Override
    public void updateDrawState(TextPaint ds) {
//		ds.setColor(Color.BLUE);
        ds.setColor(context.getResources().getColor(R.color.details_contact_color));
    }

    @Override
    public void onClick(View widget) {
        if (AvoidOnClickFastUtils.isFastDoubleClick(widget)) {
            return;
        }
        Intent intent = new Intent();
        intent.setClass(context, ZQ_FeedBackActivity.class);
        intent.putExtra("FEEDBACK_FROM","NewsDetailActivity "+feedbackFrom);
        context.startActivity(intent);
    }

}
