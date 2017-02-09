package com.hzpd.ui.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import com.hzpd.hflt.R;
import com.hzpd.ui.widget.ChoiceRateView;
import com.hzpd.utils.SPUtil;

import org.common.lib.analytics.AnalyticBaseDialogFragment;

/**
 * 用户反馈，带标签
 */
public class FeedbackTagFragment extends AnalyticBaseDialogFragment implements View.OnClickListener {

    public static final String TAG = FeedbackTagFragment.class.getSimpleName();
    public static final int COUNT_COLUMN = 50;
    private static boolean sending = false;
    private static boolean success = false;

    public FeedbackTagFragment() {
        success = false;
    }

    public static boolean shown = false;
    private Object httpTag;

    @Override
    public void show(FragmentManager manager, String tag) {
        sending = false;
        shown = true;
        super.show(manager, tag);
    }

    @Override
    public void onResume() {
        super.onResume();
        shown = true;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        shown = false;
        super.onDismiss(dialog);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(false);
        createContentView(dialog);

        return dialog;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void configDialogSize(Dialog dialog) {
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);

        DisplayMetrics screenMetrics = new DisplayMetrics();
        dialogWindow.getWindowManager().getDefaultDisplay()
                .getMetrics(screenMetrics);
        // 竖屏状态下以宽度为基准
        lp.width = (int) (screenMetrics.widthPixels * 6f / 7f);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialogWindow.setAttributes(lp);
    }

    View dialogView;

    private void createContentView(Dialog dialog) {
        try {
            dialogView = LayoutInflater.from(getActivity()).inflate(
                    R.layout.feedback_tag_layout,
                    (ViewGroup) dialog.getWindow().getDecorView(), false);
            configDialogSize(dialog);
            // 设置内容
            dialogView.findViewById(R.id.sure).setOnClickListener(this);
            dialogView.findViewById(R.id.cancel).setOnClickListener(this);
            dialog.setContentView(dialogView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ChoiceRateView rateView;
    private EditText editText;

    @Override
    public void onDestroy() {
//        OkHttpClientManager.cancel(httpTag);
        super.onDestroy();
    }


    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {

                case R.id.cancel:
                    dismiss();
                    break;
                case R.id.sure:
                    Intent intent = SPUtil.getIntent(getActivity());
                    startActivity(intent);
                    dismiss();
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onDestroyView() {
//        OkHttpClientManager.cancel("httpTag");
        super.onDestroyView();
    }


    @Override
    public String getAnalyticPageName() {
        return null;
    }
}
