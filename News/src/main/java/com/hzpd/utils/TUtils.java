package com.hzpd.utils;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hzpd.hflt.R;
import com.hzpd.ui.App;

public class TUtils {

    public static Handler baseHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0: {
                    Bundle bundle = msg.getData();
                    String text = bundle.getString("text");
                    int duration = bundle.getInt("duration");
                    makeText(App.getInstance(), text, duration).show();
                }
                break;
                default: {

                }
                break;
            }
        }
    };

    public static void toast(String str) {
        try {
            Message msg = baseHandler.obtainMessage(0);
            Bundle bundle = new Bundle();
            bundle.putString("text", str);
            bundle.putInt("duration", Toast.LENGTH_SHORT);
            msg.setData(bundle);
            baseHandler.sendMessage(msg);
        } catch (Exception e) {
            Log.e("test", "News: toast " + str);
            e.printStackTrace();
        }
    }

    public static void toast(String str, int duration) {
        Message msg = baseHandler.obtainMessage(0);
        Bundle bundle = new Bundle();
        bundle.putString("text", str);
        bundle.putInt("duration", duration);
        msg.setData(bundle);
        baseHandler.sendMessage(msg);
    }

    public static Toast makeText(Context context, CharSequence text,
                                 int duration) {

        if (null == context) {
            return null;
        }

        Toast result = new Toast(context);

        View v = LayoutInflater.from(context).inflate(R.layout.color_toast, null);
        result.setView(v);
        TextView tv = (TextView) v.findViewById(R.id.message);
        tv.setText(text);
        result.setDuration(duration);

        return result;
    }

    public static void TosatExit() {

    }

    public static void ToastLeftAndRight(Context context, ViewGroup root, int drawableId, String tvString) {
        View layout = LayoutInflater.from(context).inflate(R.layout.toast_left_and_right, root);
        TextView text = (TextView) layout.findViewById(R.id.text);
        ImageView mImageView = (ImageView) layout.findViewById(R.id.iv);
        mImageView.setBackgroundResource(drawableId);
        text.setText(tvString);
        text.setTextColor(context.getResources().getColor(R.color.white));
        Toast toast = new Toast(context);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    public static void ToastUpAndDown(Context context, ViewGroup root, int drawableId, String tvString) {
        View layout = LayoutInflater.from(context).inflate(R.layout.toast_up_and_down, root);
        TextView text = (TextView) layout.findViewById(R.id.text);
        ImageView mImageView = (ImageView) layout.findViewById(R.id.iv);
        mImageView.setBackgroundResource(drawableId);
        text.setText(tvString);
        text.setTextColor(context.getResources().getColor(R.color.white));
        Toast toast = new Toast(context);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }


}
