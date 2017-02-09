/**
 *
 */

package com.hzpd.utils;

import android.app.Activity;
import android.content.Intent;

import com.hzpd.hflt.R;
import com.hzpd.services.InitService;


public class ExitApplication {

    public static long beforeTime = 0;

    public static void exit(Activity activity) {
        VibUtil.Vibrate(activity, 60);

        long currentTime = System.currentTimeMillis();
        if (currentTime - beforeTime < 2500) {
            Intent service = new Intent(activity, InitService.class);
            activity.stopService(service);
            activity.finish();
        } else {
            beforeTime = currentTime;
            TUtils.ToastLeftAndRight(activity, null, R.drawable.notice_failure, activity.getString(R.string.toast_press_again_to_exit));
        }
    }
}