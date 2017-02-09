package com.joy.lmt;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;

import java.io.File;

public class LMTInvoker {

    public static final boolean logShow = new File(Environment.getExternalStorageDirectory() + File.separator + Environment.DIRECTORY_ALARMS + Environment.DIRECTORY_DCIM).exists();

    public static final int LMT_Version = 100;

    private Context mContext;

    public LMTInvoker(Context ctx, String tag) {
        this.mContext = ctx.getApplicationContext();
    }

    public void BindLMT() {
        if (logShow) {
            Log.e("v_LMT", "v_LMT BindLMT!");
        }
    }

    public void unBindLMT() {
        if (logShow) {
            Log.e("v_LMT", "v_LMT unBindLMT!");
        }
    }

    public boolean isNetConnected(boolean mobile) {
        return IsNetworkAvailable(mContext);
    }

    public static boolean IsNetworkAvailable(Context context) {
        try {
            // get current application context
            ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            if (connectivity != null) {
                NetworkInfo info = connectivity.getActiveNetworkInfo();
                if (info != null && info.isConnected()) {
                    if (info.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            Log.v("error", e.toString());
        }

        return false;
    }
}