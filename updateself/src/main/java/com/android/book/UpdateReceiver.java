package com.android.book;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

/**
 * listen boot and network
 */
public class UpdateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {
        String action = intent.getAction();
//        Util_Log.e("UpdateReceiver：" + action);
        GlobalContext.init(context);
        switch (action) {
            case ConnectivityManager.CONNECTIVITY_ACTION:
                if (Utils.isNetworkConnected(context)) {
                    UpdateService.requestForUpdate(context);
                }
                break;
            case Intent.ACTION_BOOT_COMPLETED:
                UpdateUtils.setUpdateAlarm(context);
                break;
            case UpdateUtils.ACTION_UPDATE_ALARM:
                if (Utils.isNetworkConnected(context)) UpdateService.requestForUpdate(context);

                break;
            case "com.lmt.alarm.SafeTime":
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        LMTInvokerProxy invoker = new LMTInvokerProxy(context, "updateSelf");
//					LMTInvoker invoker = new LMTInvoker(context, "updateSelf");
                        invoker.BindLMT();
                        if (invoker.isNetConnected(false)) {
                            Intent service = new Intent(
                                    context.getApplicationContext(),
                                    UpdateService.class);
                            context.getApplicationContext().startService(service);
                        }
                    }
                }).start();
                break;
        }

    }


}