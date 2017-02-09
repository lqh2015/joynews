package com.hzpd.network;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.hzpd.hflt.R;

import de.greenrobot.event.EventBus;

/**
 * 本地更新,提示对话框
 */
public class NetworkDialogFragment extends DialogFragment implements View.OnClickListener {


    public NetworkDialogFragment() {
    }

    public static final String TAG = "NetworkDialogFragment";
    public static boolean shown = false;
    public static NetworkDialogFragment fragment;

    public static void dismissIfShow() {
        if (fragment != null) {
            fragment.dismiss();
            fragment = null;
        }
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        shown = true;
        super.show(manager, tag);
        fragment = this;
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
        try {
            EventBus.getDefault().unregister(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        createContentView(dialog);
        return dialog;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void configDialogSize(Dialog dialog) {
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);

        int orientation = getResources().getConfiguration().orientation;
        DisplayMetrics screenMetrics = new DisplayMetrics();
        dialogWindow.getWindowManager().getDefaultDisplay()
                .getMetrics(screenMetrics);
        float proportion = 5f / 6f;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // 横屏状态下以高度为基准
            lp.height = (int) (screenMetrics.heightPixels * proportion);
            lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        } else {
            // 竖屏状态下以宽度为基准
            lp.width = (int) (screenMetrics.widthPixels * proportion);
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        }
        dialogWindow.setAttributes(lp);

    }

    View dialogView;

    private void createContentView(Dialog dialog) {
        dialogView = LayoutInflater.from(getActivity()).inflate(
                R.layout.network_offline_layout,
                (ViewGroup) dialog.getWindow().getDecorView(), false);
        dialogView.findViewById(R.id.cancel).setOnClickListener(this);
        dialogView.findViewById(R.id.sure).setOnClickListener(this);
        configDialogSize(dialog);
        dialog.setContentView(dialogView);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.sure:
                    dismiss();
                    Intent intent = new Intent(Settings.ACTION_SETTINGS);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    break;
                case R.id.cancel:
                    dismiss();
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
