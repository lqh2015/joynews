package com.news.update;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.hzpd.network.Const;
import com.hzpd.network.NetworkEvent;

import de.greenrobot.event.EventBus;

/**
 * listen boot and network
 */
public class NetReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
        try {
            String action = intent.getAction();
            switch (action) {

                case ConnectivityManager.CONNECTIVITY_ACTION:
                    checkNetwork(context);
                    break;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void checkNetwork(Context context) {
        NetworkInfo networkInfo = UpdateUtils.getNetworkInfo(context);
        EventBus.getDefault().post(new NetworkEvent());
        Const.checkNetwork(networkInfo);
        if (!UpdateUtils.isRomVersion(context)) {
            return;
        }
    }
}