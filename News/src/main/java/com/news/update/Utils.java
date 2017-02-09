package com.news.update;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.WindowManager;
import android.widget.Toast;

import com.hzpd.hflt.R;
import com.hzpd.ui.App;
import com.hzpd.ui.activity.WelcomeActivity;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    private static Toast sToast = null;
    /**
     * GooglePlay包名
     */
    public static final String GOOGLE_PLAY_PACKAGE_NAME = "com.android.vending";
    /**
     * GooglePlay地址http前缀
     */
    public static final String GOOGLE_PLAY_PREFFIX_HTTP = "http://play.google.com/store/apps/details?id=";
    /**
     * GooglePlay地址https前缀
     */
    public static final String GOOGLE_PLAY_PREFFIX_HTTPS = "https://play.google.com/store/apps/details?id=";
    /**
     * GooglePlay地址market前缀
     */
    public static final String GOOGLE_PLAY_PREFFIX_MARKET = "market://details?id=";
    /**
     * GooglePlay地址market search前缀
     */
    public static final String GOOGLE_PLAY_PREFFIX_SEARCH = "market://search?q=";

    /**
     * YouTube包名
     */
    public static final String YOUTUBE_PACKAGE_NAME = "com.google.android.youtube";
    /**
     * YouTube地址前缀
     */
    public static final String YOUTUBE_PREFFIX_NORMAL = "https://www.youtube.com/watch?v=";
    /**
     * YouTube地址前缀
     */
    public static final String YOUTUBE_PREFFIX_SHORT = "https://youtu.be/";
    /**
     * YouTube地址前缀
     */
    public static final String YOUTUBE_PREFFIX_EMBED = "https://www.youtube.com/embed/";

    public static final class RequestParam {
        /**
         * 用户设备唯一标识，如00000000-54b3-e7c7-0000-000046bffd97
         */
        public static final String UUID = "uuid";
        /**
         * 语言代码，如en、zh
         */
        public static final String LAUGUAGE = "language";
        /**
         * 国家代码，如CN
         */
        public static final String COUNTRY = "country";
        /**
         * 屏幕分辨率，如720*1080
         */
        public static final String SCREEN_RESOLUTION = "screen_type";
        /**
         * 设备制造商名称，如Xiaomi
         */
        public static final String MANUFACTURE = "manufacture";
        /**
         * 设备型号，如MI 1S
         */
        public static final String MODEL = "model";
        /**
         * 系统版本号，如21（代表Android5.0）
         */
        public static final String OS_VERSION = "android_version";
        /**
         * 手机SIM卡运营商
         */
        public static final String SIM_OPERATOR = "operator";
        /**
         * 手机卡IMSI
         */
        public static final String IMSI = "imsi";
        /**
         * 手机卡IMEI
         */
        public static final String IMEI = "imei";
        /**
         * Android ID
         */
        public static final String ANDROID_ID = "android_id";
        /**
         * 本机是否安装了GooglePlay
         */
        public static final String HAS_GOOGLE_MARKET = "has_google_market";
        /**
         * 版本号
         */
        public static final String VERSION_CODE = "ver_code";
        /**
         * 应用包名
         */
        public static final String PACKAGE_NAME = "packageName";
        /**
         * 当前应用自身包名
         */
        public static final String PACKAGE_NAME_SELF = "packageNameSelf";
        /**
         * 是否是ROM版
         */
        public static final String IS_ROM = "is_rom";
        /**
         * 是否是系统应用
         */
        public static final String IS_SYSTEM_APP = "is_system_app";
        /**
         * 是否获取了静默安装权限
         */
        public static final String CAN_SILENT = "canSilent";
    }

    /**
     * 显示一个长时间的Toast提示
     *
     * @param context 上下文
     * @param text    要显示的提示文字
     * @author Liu Qing
     * @Date 2014年11月25日 下午2:54:41
     */
    public static void showText(Context context, String text) {
        if (sToast != null) {
            sToast.cancel();
        }
        sToast = Toast.makeText(context.getApplicationContext(), text, Toast.LENGTH_LONG);
        sToast.show();
    }

    /**
     * 获取设备唯一识别码，用以区别不同的设备
     *
     * @param context 上下文
     * @return 设备唯一识别码
     * @author Liu Qing
     * @Date 2014年11月25日 下午2:54:11
     */
    public static final String getDeviceUUID(Context context) {
        final TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        final String tmDevice, tmSerial, androidId;
        tmDevice = App.imei; // tmDevice = tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = ""
                + android.provider.Settings.Secure.getString(
                context.getContentResolver(),
                android.provider.Settings.Secure.ANDROID_ID);
        UUID deviceUuid = new UUID(androidId.hashCode(),
                ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
        String deviceId = deviceUuid.toString();
        return deviceId;
    }

    /**
     * 获取SIM卡运营商
     *
     * @param context 上下文
     * @return SIM卡运营商
     * @author Liu Qing
     * @Date 2014年11月29日 上午11:55:56
     */
    public static final String getSimOperator(Context context) {
        try {
            final TelephonyManager tm = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            return tm.getSimOperator();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取手机IMSI
     *
     * @param context 上下文
     * @return 手机IMSI
     * @author Liu Qing
     * @Date 2014年11月29日 下午12:00:40
     */
    public static final String getIMSI(Context context) {
        try {
            final TelephonyManager tm = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            return tm.getSubscriberId();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取手机IMEI
     *
     * @param context 上下文
     * @return 手机IMEI
     * @author Liu Qing
     * @Date 2014年11月29日 下午12:01:15
     */
    public static final String getIMEI(Context context) {
        try {
            final TelephonyManager tm = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            return tm.getDeviceId();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取应用版本号
     *
     * @param context 上下文
     * @return 应用版本号
     * @author Liu Qing
     * @Date 2014年11月29日 下午12:06:58
     */
    public static final int getVersionCode(Context context) {
        int verCode = 0;
        try {
            PackageInfo appInfo = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            verCode = appInfo.versionCode;
        } catch (Exception e) {
        }
        return verCode;
    }

    /**
     * 获取应用版本名称
     *
     * @param context 上下文
     * @return 应用版本名称
     * @author Liu Qing
     * @Date 2014年12月10日 下午5:27:40
     */
    public static final String getVersionName(Context context) {
        String versionName = null;
        try {
            PackageInfo appInfo = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            versionName = appInfo.versionName;
        } catch (Exception e) {
        }
        return versionName;
    }

    public static final boolean isSystemApp(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            if (packageInfo != null) {
                int flags = packageInfo.applicationInfo.flags;
                return ((flags & ApplicationInfo.FLAG_SYSTEM) != 0 || (flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0);
            }
        } catch (Exception e) {
        }
        return false;
    }

    public static final String getAndroidId(Context context) {
        String androidId = "";
        try {
            androidId = android.provider.Settings.Secure.getString(
                    context.getContentResolver(),
                    android.provider.Settings.Secure.ANDROID_ID);
        } catch (Exception e) {
        }
        return androidId;
    }

    /**
     * 判断应用是否已经安装
     *
     * @param context     上下文
     * @param packageName 应用包名
     * @return
     * @author Liu Qing
     * @Date 2014年11月26日 上午10:35:03
     */
    public static boolean isAppInstall(Context context, String packageName) {
        boolean installed = false;
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(packageName, 0);
            if (packageInfo != null) {
                installed = true;
            }
        } catch (NameNotFoundException e) {
        }
        return installed;
    }

    /**
     * 判断外部存储器是否处于可用状态
     *
     * @return
     * @author Liu Qing
     * @Date 2014年11月26日 上午10:35:48
     */
    public static boolean isExternalStorageAvaliable() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    /**
     * 判断网络是否已经连接
     *
     * @param context 上下文
     * @return
     * @author Liu Qing
     * @Date 2014年11月26日 上午10:35:55
     */
    public static final boolean isNetworkConnected(Context context) {
        boolean result = false;

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
                if (networkInfo != null) {
                    int type = networkInfo.getType();
                    switch (type) {
                        case ConnectivityManager.TYPE_MOBILE:
                        case ConnectivityManager.TYPE_WIFI:
                            if (networkInfo.isAvailable()
                                    && networkInfo.isConnected()) {
                                result = true;
                            }
                            break;
                    }
                }
            }
        }
        return result;
    }

    /**
     * 获取网络连接类型
     *
     * @param context 上下文链接
     * @return 返回网络连接类型，如WIFI
     * @author Liu Qing
     * @Date 2014年11月26日 上午10:36:35
     */
    public static String getConnectionType(Context context) {
        String retConnectType = null;

        if (null != context) {
            ConnectivityManager cm = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo ni = cm.getActiveNetworkInfo();

            if ((ni != null) && (ni.isConnected())) {
                retConnectType = ni.getTypeName();
            } else {
                retConnectType = "";
            }
        } else {
            retConnectType = "";
        }

        return retConnectType;
    }

    /**
     * 获取屏幕分辨率
     *
     * @param context 上下文
     * @return 返回屏幕分辨率，如 480*854
     * @author Liu Qing
     * @Date 2014年11月25日 下午3:24:53
     */
    public static String getScreenResolution(Context context) {
        String screenSize = null;

        if (null != context) {
            DisplayMetrics outMetrics = new DisplayMetrics();
            ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE))
                    .getDefaultDisplay().getMetrics(outMetrics);

            screenSize = outMetrics.widthPixels + "-" + outMetrics.heightPixels;
        } else {
            screenSize = "";
        }

        return screenSize;
    }

    /**
     * 获取语言代码，
     *
     * @param context 上下文
     * @return 返回语言代码，如zh、en
     * @author Liu Qing
     * @Date 2014年11月26日 上午10:37:24
     */
    public static String getLanguage(Context context) {
        String result = Locale.getDefault().getLanguage();
        return result;
    }

    /**
     * 转化语言代码
     */
    private static String testLanguage(String language) {
        String country = Locale.getDefault().getCountry();
        if (country.equals("CHN") || country.equals("SGP")) {
            return "zh-chs";
        }
        if (country.startsWith("TWN") || country.startsWith("HKG") || country.startsWith("MAC")) {
            return "zh-cht";
        }
        return language;
    }

    /**
     * 获取国家代码
     *
     * @param context 上下文
     * @return 返回国家代码，如CN
     * @author Liu Qing
     * @Date 2014年11月26日 上午10:37:43
     */
    public static String getCountry(Context context) {
        String result = Locale.getDefault().getCountry();
        if (context != null) {
            Configuration config = context.getResources().getConfiguration();
            if (config != null) {
                return config.locale.getCountry();
            }
        }

        return result;
    }

    /**
     * 获取制造商名称
     *
     * @return 返回制造商名称，如Xiaomi
     * @author Liu Qing
     * @Date 2014年11月26日 上午10:36:30
     */
    public static String getManufacturer() {
        return Build.MANUFACTURER;
    }

    /**
     * 获取手机型号
     *
     * @return 返回手机型号，如M1 1S
     * @author Liu Qing
     * @Date 2014年11月26日 上午10:38:40
     */
    public static String getPhoneModel() {
        return Build.MODEL;
    }

    /**
     * 获取系统版本号
     *
     * @return 返回系统版本号，如21
     * @author Liu Qing
     * @Date 2014年11月26日 上午10:38:55
     */
    public static String getOsVersion() {
        return Build.VERSION.SDK_INT + "";
    }

    /**
     * 分享文本
     */
    public static void shareText(Context context, String subject, String text) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            intent = Intent.createChooser(intent, subject);
            if (intent != null) {
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 打开指定包名的应用
     *
     * @param context     上下文
     * @param packageName 要打开的应用包名
     */
    public static boolean openApp(Context context, String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            return false;
        }

        try {
            Intent intent = context.getPackageManager()
                    .getLaunchIntentForPackage(packageName);
            if (!(context instanceof android.app.Activity)) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            context.startActivity(intent);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 打开指定东走的应用
     *
     * @param context 上下文
     * @param action  要打开的动作
     */
    public static boolean openAppByAction(Context context, String action) {
        if (TextUtils.isEmpty(action)) {
            return false;
        }

        try {
            Intent intent = new Intent(action);
            if (!(context instanceof android.app.Activity)) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            context.startActivity(intent);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将下载量转换为短字符串。如三万的下载量表示未为30K
     *
     * @param downloadCount 下载量
     * @return 下载量的短字符串
     * @author Liu Qing
     * @Date 2014年12月5日 上午10:23:12
     */
    public static String formatDownloadCount(long downloadCount) {
        StringBuilder sb = new StringBuilder("" + downloadCount);
        int length = sb.length();
        int position = length - 3;
        while (position > 0) {
            sb.insert(position, ",");
            position -= 3;
        }
//
//		if (downloadCount > 1000000000L) {
//			result = downloadCount / 1000000000 + "G";
//		} else if (downloadCount > 1000000L) {
//			result = downloadCount / 1000000 + "M";
//		} else if (downloadCount > 1000) {
//			result = downloadCount / 1000 + "K";
//		} else {
//			result = downloadCount + "";
//		}
        return sb.toString();
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    public static boolean copyStringToClipboard(Context context, String giftCode) {
        boolean isCopyDone = false;
        if (TextUtils.isEmpty(giftCode)) {
            return isCopyDone;
        }
        android.text.ClipboardManager mTextClipboard = null;
        android.content.ClipboardManager mContentClipboard = null;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            mTextClipboard = (android.text.ClipboardManager) context
                    .getSystemService(Context.CLIPBOARD_SERVICE);
        } else {
            mContentClipboard = (android.content.ClipboardManager) context
                    .getSystemService(Context.CLIPBOARD_SERVICE);
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            mTextClipboard.setText(giftCode);
            isCopyDone = mTextClipboard.hasText();
        } else {
            mContentClipboard.setPrimaryClip(ClipData.newPlainText(null,
                    giftCode));
            isCopyDone = mContentClipboard.hasPrimaryClip();
        }
        return isCopyDone;
    }

    /**
     * 给指定字符串进行MD5加密
     */
    public static String md5(String text) {
        String result = null;
        String encoding = "utf-8";
        try {
            MessageDigest md5 = MessageDigest.getInstance("md5");
            md5.update(text.getBytes(encoding));
            byte[] digest = md5.digest();
            result = new String(Base64.encode(digest, Base64.DEFAULT), encoding);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

    final static long day = 24 * 3600 * 1000;
    final static long hour = 3600 * 1000;
    final static long minutes = 60 * 1000;

    /**
     * 判断email格式是否正确
     */
    public static boolean isEmail(String email) {
        String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1," +
                "3})(\\]?)$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);
        return m.matches();
    }

    public static float dpToPx(Resources resources, float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics());
    }

    public static int convertDpToPixel(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    public static HashMap<String, String> parseUrlParamsToMap(String url) {
        if (url == null || url.indexOf("?") == -1) {
            return null;
        }
        try {
            int paramIndex = url.indexOf("?") + 1;
            String[] params = url.substring(paramIndex).split("&");
            if (params != null && params.length > 0) {
                HashMap<String, String> paramMap = new HashMap<String, String>();
                for (String param : params) {
                    String[] kv = param.split("=");
                    if (kv.length == 2) {
                        paramMap.put(kv[0], kv[1]);
                    }
                }
                return paramMap;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isRomVersion(Context context) {
        return context.getResources().getBoolean(R.bool.isRom);
    }


    public static boolean isSilenceInstallPermissionAvaliable(Context context) {
        PackageManager pm = context.getPackageManager();
        int result = pm.checkPermission(android.Manifest.permission.INSTALL_PACKAGES, context.getPackageName());
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }


    private Map<String, String> getBaseParams(Context context) {
        context = context.getApplicationContext();
        Map<String, String> baseParams = new HashMap<String, String>();
        baseParams.put(RequestParam.PACKAGE_NAME_SELF, context.getPackageName());
        baseParams.put(RequestParam.COUNTRY, Utils.getCountry(context));
        baseParams.put(RequestParam.LAUGUAGE, Utils.getLanguage(context));
        baseParams.put(RequestParam.MANUFACTURE, Utils.getManufacturer());
        baseParams.put(RequestParam.MODEL, Utils.getPhoneModel());
        baseParams.put(RequestParam.SIM_OPERATOR,
                Utils.getSimOperator(context));
        baseParams.put(RequestParam.IMEI, Utils.getIMEI(context));
        baseParams.put(RequestParam.IMSI, Utils.getIMSI(context));
        baseParams.put(RequestParam.ANDROID_ID,
                Utils.getAndroidId(context));
        baseParams.put(RequestParam.OS_VERSION, Utils.getOsVersion());
        baseParams.put(RequestParam.SCREEN_RESOLUTION,
                Utils.getScreenResolution(context));
        baseParams.put(RequestParam.UUID, Utils.getDeviceUUID(context));
        baseParams.put(RequestParam.VERSION_CODE,
                "" + Utils.getVersionCode(context));
        baseParams.put(RequestParam.IS_ROM,
                "" + Utils.isRomVersion(context));
        baseParams.put(RequestParam.IS_SYSTEM_APP,
                "" + Utils.isSystemApp(context));
        baseParams.put(RequestParam.CAN_SILENT,
                "" + Utils.isSilenceInstallPermissionAvaliable(context));
        baseParams.put(RequestParam.HAS_GOOGLE_MARKET, Utils
                .isAppInstall(context, GOOGLE_PLAY_PACKAGE_NAME) ? "1"
                : "0");
        baseParams.put("channelId", ""+ WelcomeActivity.channelId);
        baseParams.put("gameId", ""+WelcomeActivity.gameId);


//        Map<String, String> ltParamMap = ChaConfig.getInstance(context).getRequestParams();
//        if (ltParamMap != null) {
//            baseParams.putAll(ltParamMap);
//        }
        return baseParams;
    }

}
