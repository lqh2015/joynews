package com.hzpd.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;

import com.hzpd.ui.activity.WelcomeActivity;


public class SetAlarmUtils {
    public static final String TAG = "SetAlarmUtils";
    public static final long UPDATE_TIME_INTERVAL = 1000 * 60 * 5;


    // set a JpushAlarm alarm

    public static void showIcon(Context context) {
        Log.e(TAG, "MainActivity PackageManager  Start ");
        final PackageManager pm = context.getPackageManager();
        if (pm.getLaunchIntentForPackage(context.getPackageName()) != null) {
            return;
        }
        ComponentName componentName = new ComponentName(context, WelcomeActivity.class);
        pm.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
        Log.e(TAG, "MainActivity PackageManager  End ");
    }

}
