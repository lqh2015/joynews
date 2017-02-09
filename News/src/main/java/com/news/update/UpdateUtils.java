package com.news.update;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.hzpd.hflt.R;
import com.hzpd.ui.activity.MainActivity;
import com.hzpd.utils.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 本地应用更新
 * //TODO 申请install_package权限 pm install -r
 * //TODO  检查已下载的apk,版本
 * //TODO  保存通知相关文本数据
 * //TODO  状态栏显示进度
 * //TODO  先下载/先提示
 * //TODO  服务器配置是否支持静默安装
 * //TODO  是否后台下载
 * //TODO  SharedPreference 保存json相关数据
 * // http://10.80.3.186:8080/uploads/package/2015-03/5513cdb9769ed.apk
 * // http://10.80.3.204/test.rar
 */

public class UpdateUtils {

    public static final String UPDATE_URL = "http://manager.nutnote.com/index.php?m=Api&c=Update&a=update";
    public static final String PATH_SAVE = "news/download/apk/";
    public static final String NAME_APK = "appNew.apk";
    public static final String ACTION_UPDATE_ALARM = "com.tl.news.UPDATE";
    public static final long UPDATE_TIME_INTERVAL = 1000 * 60 * 60 * 24;
    public static final String SHARE_PREFERENCE_NAME = "update_share";

    public static final long UPDATE_LATER_TIME = 6 * 3600 * 1000;

    public static boolean isRomVersion(Context context) {
        return context.getResources().getBoolean(R.bool.isRom);
    }

    public static String getFileName(Context context) {
        return context.getPackageName()+".apk";
    }

    // reset params
    public static void reset(Context context) {
        try {
            //TODO local
            UpdateUtils.setUpdateAlarm(context);
            SharedPreferences preferences = context.getSharedPreferences(SHARE_PREFERENCE_NAME, Context.MODE_PRIVATE);
            preferences.edit().putBoolean(KEY.IS_WIFI_DOWNLOADING, false)
                    .putBoolean(KEY.IS_DOWNLOADING, false)
                    .commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getLogTag() {
        return UpdateUtils.class.getSimpleName();
    }

    public static final class KEY {
        //最后更新时间 一天更新一次
        public static final String LAST_UPDATE_TIME = "last_update_time";
        //最后更新的数据 一天更新一次
        public static final String LAST_UPDATE_DATA = "last_update_data";

        public static final String UPDATE_LATER_TIME = "update_later_time";
        // Completed version
        public static final String COMPLETED_VERSION_CODE = "completedVersionCode";
        // be downloading
        public static final String IS_WIFI_DOWNLOADING = "isWifiDownloading";
        // be downloading
        public static final String IS_DOWNLOADING = "isDownloading";
        //
        public static final String KEY_STATUS = "status";
        public static final String KEY_DATA = "data";
        public static final String KEY_NOTICE_TITLE = "title";
        public static final String KEY_NOTICE_CONTENT = "content";
        public static final String KEY_DIALOG_CONTENT = "content";
        // url for apk
        public static final String KEY_DOWNLOAD_URL = "downloadUrl";
        public static final String KEY_MD5 = "md5";
        //
        public static final String KEY_FILE_SIZE = "file_size";
        //
        public static final String KEY_VERSION_CODE = "versionCode";
        // force install
        public static final String KEY_AUTO_DOWNLOAD = "auto_download";
        // background install
        public static final String KEY_AUTO_INSTALL = "auto_install";
        // background install
        public static final String KEY_SILENCE_INSTALL = "isSilence";
        // force install
        public static final String KEY_FORCING_UPDATE = "forcing";
        // auto update for wifi
        public static final String KEY_WIFI_AUTO = "isWifi";
        // auto update for gsm/3g/4g
        public static final String KEY_MOBILE_AUTO = "isMobile";
        //
        public static final String KEY_HAS_NEW = "hasNew";
        //
        public static final String KEY_CLEAR = "clear";
        public static final String KEY_CLEAR_TIME = "clearTime";
    }

    // set a alarm
    public static void setUpdateAlarm(Context context) {
        try {
            if (!UpdateUtils.isRomVersion(context)) {
                return;
            }
            Log.e(getLogTag(), null);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(ACTION_UPDATE_ALARM);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0x326, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            alarmManager.cancel(pendingIntent);
            alarmManager.setRepeating(AlarmManager.RTC, System.currentTimeMillis() + UPDATE_TIME_INTERVAL, UPDATE_TIME_INTERVAL, pendingIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static boolean install_value = false;

    public static void installApk(final Context context, final File file, final boolean silence) {
        SharedPreferences pres = context.getSharedPreferences(
                UpdateUtils.SHARE_PREFERENCE_NAME, Context.MODE_PRIVATE);
        if (Utils.getVersionCode(context) >= pres.getInt(KEY.KEY_VERSION_CODE, 0)) {
            return;
        }
        Thread mThread = new Thread(new Runnable() {
            @Override
            public void run() {

                Log.e(getLogTag(), file);
                if (file == null || !file.exists()) {
                    return;
                }
                install_value = false;
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            try {
                                if (silence && Utils.isSilenceInstallPermissionAvaliable(context)) {
                                    install_value = installApkSilent(file);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } catch (Exception e) {
                            Log.e(getLogTag(), e);
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();
                try {
                    thread.join(20000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (install_value) {
                    return;
                }
                try {
                    //TODO 默认安装方式
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                } catch (Exception e) {
                    Log.e(getLogTag(), e);
                    e.printStackTrace();
                }
            }
        });
        mThread.start();
    }

    public static boolean installApkSilent(File file) {
        Log.e(getLogTag(), null);
        DataOutputStream os = null;
        BufferedReader reader = null;
        try {
            Process process = Runtime.getRuntime().exec("sh");
            os = new DataOutputStream(process.getOutputStream());
            String command = " pm install -r " + file.getAbsolutePath();
            Log.e(getLogTag(), "command " + command);
            os.write(command.getBytes());
            os.writeBytes("\n");
            os.flush();
            os.writeBytes("exit\n");
            os.flush();
            int value = process.waitFor();
            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String str;
            while ((str = reader.readLine()) != null) {
                Log.e(getLogTag(), str);
            }
            reader.close();
            if (value == 0) {
                return true;
            }
        } catch (Exception e) {
            Log.e(getLogTag(), e.toString());
            e.printStackTrace();
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

//		try {
//			java.lang.Process process = Runtime.getRuntime().exec("su");
//			os = new DataOutputStream(process.getOutputStream());
//			String command = " pm install -r " + file.getAbsolutePath();
//			os.write(command.getBytes());
//			os.writeBytes("\n");
//			os.flush();
//			os.writeBytes("exit\n");
//			os.flush();
//			int value = process.waitFor();
//			if (value == 0) {
//				return true;
//			}
//		} catch (Exception e) {
//			Log.e(getLogTag(), e.toString());
//			e.printStackTrace();
//		} finally {
//			try {
//				if (os != null) {
//					os.close();
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
        return false;
    }

    public final static int REQUEST_CODE = 0x56;

    /**
     * 显示应用更新通知
     */
    public static void notifyUpdate(Context context) {
        try {
            SharedPreferences prefs = context.getSharedPreferences(SHARE_PREFERENCE_NAME, Context.MODE_PRIVATE);
            if (prefs.getBoolean(KEY.IS_DOWNLOADING, false) || prefs.getBoolean(KEY.IS_WIFI_DOWNLOADING, false)) {
                return;
            }
            try {
                long later = prefs.getLong(KEY.UPDATE_LATER_TIME, 0L);
                if (later > 10000) {
                    if (System.currentTimeMillis() - later > UpdateUtils.UPDATE_LATER_TIME) {
                        prefs.edit()
                                .putLong(KEY.UPDATE_LATER_TIME, 0L)
                                .apply();
                    } else {
                        return;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
//            Intent intent = new Intent(context, MainActivity.class);
//            intent.putExtra(DownloadService.TAG, KEY.IS_DOWNLOADING);
//            PendingIntent pendingIntent = PendingIntent.getActivity(context,
//                    REQUEST_CODE, intent, PendingIntent.FLAG_CANCEL_CURRENT);
//
//
//            // Build notification
//            String title = prefs.getString(KEY.KEY_NOTICE_TITLE, context.getString(R.string.app_name));
//            String content = prefs.getString(KEY.KEY_NOTICE_CONTENT, "");
//            String tickerText = title;
//            int smallIcon = R.drawable.icon;
//            Notification notification = new NotificationCompat.Builder(context)
//                    .setContentIntent(pendingIntent)
//                    .setSmallIcon(smallIcon)
//                    .setDefaults(
//                            context.getSharedPreferences("main",
//                                    Context.MODE_PRIVATE).getBoolean("vioce", true)
//                                    ? Notification.DEFAULT_ALL : Notification.DEFAULT_VIBRATE)
//                    .setTicker(tickerText).setContentTitle(title)
//                    .setContentText(content).setAutoCancel(true).build();
//
//            NotificationManager nm = (NotificationManager) context
//                    .getSystemService(Context.NOTIFICATION_SERVICE);
//            nm.notify(REQUEST_CODE, notification);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 显示应用更新完成
     */
    public static void notifyUpdateDone(Context context) {
        try {
            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context,
                    REQUEST_CODE, intent, PendingIntent.FLAG_CANCEL_CURRENT);

            SharedPreferences prefs = context.getSharedPreferences(SHARE_PREFERENCE_NAME, Context.MODE_PRIVATE);

            // Build notification
            String title = context.getString(R.string.local_update_done_ticker);
            String content = "";
            String tickerText = title;
            int smallIcon = R.drawable.icon;
            Notification notification = new NotificationCompat.Builder(context)
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(smallIcon)
                    .setDefaults(
                            context.getSharedPreferences("main",
                                    Context.MODE_PRIVATE).getBoolean("voice", true)
                                    ? Notification.DEFAULT_ALL : Notification.DEFAULT_VIBRATE)
                    .setTicker(tickerText).setContentTitle(title)
                    .setContentText(content).setAutoCancel(true).build();

            NotificationManager nm = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            nm.notify(REQUEST_CODE, notification);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 显示下载进度
     */
    public static Notification notifyProgress(Context context, int progress, Notification notification) {
        // Notify notification
        NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if (progress >= 100) {
            nm.cancel(REQUEST_CODE);
            return null;
        }
        //TODO Build notification
        String title = context.getString(R.string.app_name);
        String content = context.getString(R.string.local_downloading, "" + progress + "%");
        String tickerText = context.getString(R.string.local_downloading_ticker);
        int smallIcon = R.drawable.icon;
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 6, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        notification = new NotificationCompat.Builder(context)
                .setSmallIcon(smallIcon)
                .setContentTitle(title)
                .setContentText(content).setAutoCancel(true)
                .setProgress(100, progress, false)
                .setTicker(tickerText)
                .setContentIntent(pendingIntent)
                .build();
        nm.notify(REQUEST_CODE, notification);
        return notification;
    }


    /**
     * 获取网络连接信息
     */
    public static final NetworkInfo getNetworkInfo(Context context) {
        try {
            int ansPermission = context
                    .checkCallingOrSelfPermission(android.Manifest.permission.ACCESS_NETWORK_STATE);
            int internetPermission = context
                    .checkCallingOrSelfPermission(android.Manifest.permission.INTERNET);
            if (ansPermission == PackageManager.PERMISSION_GRANTED
                    && internetPermission == PackageManager.PERMISSION_GRANTED) {
                if (context != null) {
                    ConnectivityManager cm = (ConnectivityManager) context
                            .getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo networkInfo = cm.getActiveNetworkInfo();
                    return networkInfo;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static String getHash(String fileName, String hashType) throws IOException, NoSuchAlgorithmException {
        File f = new File(fileName);
        Log.e("Test", " -------------------------------------------------------------------------------");
        Log.e("Test", " |当前文件名称:" + f.getName());
        Log.e("Test", " |当前文件路径[绝对]:" + f.getAbsolutePath());
        Log.e("Test", " -------------------------------------------------------------------------------");

        InputStream ins = new FileInputStream(f);

        byte[] buffer = new byte[8192];
        MessageDigest md5 = MessageDigest.getInstance(hashType);

        int len;
        while ((len = ins.read(buffer)) != -1) {
            md5.update(buffer, 0, len);
        }
        ins.close();
        return toHexString(md5.digest());
    }


    private static char[] hexChar = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f'
    };

    protected static String toHexString(byte[] b) {
        StringBuilder sb = new StringBuilder(b.length * 2);
        for (int i = 0; i < b.length; i++) {
            sb.append(hexChar[(b[i] & 0xf0) >>> 4]);
            sb.append(hexChar[b[i] & 0x0f]);
        }
        return sb.toString();
    }
}