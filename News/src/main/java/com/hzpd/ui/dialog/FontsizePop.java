package com.hzpd.ui.dialog;

import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;

public class FontsizePop extends PopupWindow implements View.OnClickListener {

    private TextView nd_tv_big;
    private TextView nd_tv_mid;
    private TextView nd_tv_small;

    private Handler handler;
    private View view;

    public FontsizePop(View view, Handler h) {
        super(view, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        this.setBackgroundDrawable(new BitmapDrawable());
//        nd_tv_big = (TextView) view.findViewById(R.id.nd_tv_big);
//        nd_tv_big.setOnClickListener(this);
//        nd_tv_mid = (TextView) view.findViewById(R.id.nd_tv_mid);
//        nd_tv_mid.setOnClickListener(this);
//        nd_tv_small = (TextView) view.findViewById(R.id.nd_tv_small);
//        nd_tv_small.setOnClickListener(this);
        this.handler = h;
        setOutsideTouchable(true);

    }


    @Override
    public void onClick(View v) {
//        this.dismiss();
//        switch (v.getId()) {
//            case R.id.nd_tv_big: {
//                handler.sendEmptyMessage(CODE.font_big);
//            }
//            break;
//            case R.id.nd_tv_mid: {
//                handler.sendEmptyMessage(CODE.font_mid);
//            }
//            break;
//            case R.id.nd_tv_small: {
//                handler.sendEmptyMessage(CODE.font_small);
//            }
//            break;
//
//        }

    }
}
