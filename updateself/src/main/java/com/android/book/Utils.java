package com.android.book;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.book.nativetask.UUIDRetriever;

public class Utils {
	private static Toast sToast = null;

	/**
	 * 显示一个长时间的Toast提示
	 * 
	 * @param context
	 *            上下文
	 * @param text
	 *            要显示的提示文字
	 * 
	 * @author Liu Qing
	 * @Date 2014年11月25日 下午2:54:41
	 */
	public static void showText(Context context, String text) {
		if (sToast != null) {
			sToast.cancel();
		}
		sToast = Toast.makeText(context, text, Toast.LENGTH_LONG);
		sToast.show();
	}


	/**
	 * 获取设备唯一识别码，用以区别不同的设备
	 * 
	 * @param context
	 *            上下文
	 * 
	 * @return 设备唯一识别码
	 * 
	 * @author Liu Qing
	 * @Date 2014年11月25日 下午2:54:11
	 */
	public static final String getDeviceUUID(Context context) {
		return UUIDRetriever.get(context);
//		final TelephonyManager tm = (TelephonyManager) context
//				.getSystemService(Context.TELEPHONY_SERVICE);
//		final String tmDevice, tmSerial, androidId;
//		tmDevice = "" + tm.getDeviceId();
//		tmSerial = "" + tm.getSimSerialNumber();
//		androidId = ""
//				+ android.provider.Settings.Secure.getString(
//						context.getContentResolver(),
//						android.provider.Settings.Secure.ANDROID_ID);
//		UUID deviceUuid = new UUID(androidId.hashCode(),
//				((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
//		String deviceId = deviceUuid.toString();
//		return deviceId;
	}

	/**
	 * 获取SIM卡运营商
	 * 
	 * @param context
	 *            上下文
	 * 
	 * @return SIM卡运营商
	 * 
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
	 * @param context
	 *            上下文
	 * 
	 * @return 手机IMSI
	 * 
	 * @author Liu Qing
	 * @Date 2014年11月29日 下午12:00:40
	 */
	public static final String getIMSI(Context context) {
		try {
			final TelephonyManager tm = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			String ret = tm.getSubscriberId();
			if(ret==null)ret="";
			return ret;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 获取手机IMEI
	 * 
	 * @param context
	 *            上下文
	 * 
	 * @return 手机IMEI
	 * 
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
	 * @param context
	 *            上下文
	 * 
	 * @return 应用版本号
	 * 
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
	 * @param context
	 *            上下文
	 * 
	 * @return 应用版本名称
	 * 
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
			PackageInfo packageInfo = context.getPackageManager()
					.getPackageInfo(context.getPackageName(), 0);
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
	 * @param context
	 *            上下文
	 * @param packageName
	 *            应用包名
	 * 
	 * @return
	 * 
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
	 * 
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
	 * @param context
	 *            上下文
	 * 
	 * @return
	 * 
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
	 * @param context
	 *            上下文链接
	 * 
	 * @return 返回网络连接类型，如WIFI
	 * 
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
	 * @param context
	 *            上下文
	 * 
	 * @return 返回屏幕分辨率，如 480*854
	 * 
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
	 * @param context
	 *            上下文
	 * 
	 * @return 返回语言代码，如zh、en
	 * 
	 * @author Liu Qing
	 * @Date 2014年11月26日 上午10:37:24
	 */
	public static String getLanguage(Context context) {
		String result = Locale.getDefault().getLanguage();
//		if (context != null) {
//			Configuration config = context.getResources().getConfiguration();
//			if (config != null) {
//				return testLanguage(config.locale.getLanguage());
//			}
//		}
		return result;
	}

	/**
	 * 转化语言代码
	 */
	private static String testLanguage(String language) {
		if (language.startsWith("zh") || language.startsWith("cn")) {
			return "zh-chs";
		}
		return language;
	}

	/**
	 * 获取国家代码
	 * 
	 * @param context
	 *            上下文
	 * 
	 * @return 返回国家代码，如CN
	 * 
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
	 * 
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
	 * 
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
	 * 
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
				context.startActivity(intent);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 分享图片
	 * 
	 * @param context
	 *            上下文环境
	 * @param uri
	 *            图片地址
	 */
	public static void shareImage(Context context, Uri uri) {
		if (uri != null) {
			Intent shareIntent = new Intent();
			shareIntent.setAction(Intent.ACTION_SEND);
			shareIntent.setType("image/*");
			shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
			shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

			try {
				context.startActivity(Intent
						.createChooser(shareIntent, "Share"));
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
		}
	}

	/**
	 * 分享图片
	 * 
	 * @param context
	 *            上下文环境
	 * @param uri
	 *            图片地址
	 */
	public static void shareTextAndImage(Context context, String subject,
			String text, Uri uri) {
		if (uri != null) {
			Intent shareIntent = new Intent();
			shareIntent.setAction(Intent.ACTION_SEND);
			shareIntent.setType("image/*");
			shareIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
			shareIntent.putExtra(Intent.EXTRA_TEXT, text);
			shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
			shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

			try {
				context.startActivity(Intent
						.createChooser(shareIntent, subject));
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
		}
	}

	/**
	 * 用第三方程序打开指定链接
	 * 
	 * @param context
	 *            上下文
	 * @param link
	 *            要打开的链接。</br>如果是GooglePlay的链接，并且已经安装GooglePlay，就用GooglePlay打开
	 * 
	 * @author Liu Qing
	 * @Date 2014年11月26日 上午10:39:08
	 */
	public static boolean openLink(Context context, String link) {
		boolean result = false;
		if (!TextUtils.isEmpty(link)) {
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_VIEW);
			intent.setData(Uri.parse(link));
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

			// 判断是否是GooglePlay的链接
			if (link.startsWith(Const.GOOGLE_PLAY_PREFFIX_HTTP)
					|| link.startsWith(Const.GOOGLE_PLAY_PREFFIX_HTTPS)
					|| link.startsWith(Const.GOOGLE_PLAY_PREFFIX_MARKET)) {
				// 如果安装了GooglePlay
				if (isAppInstall(context, Const.GOOGLE_PLAY_PACKAGE_NAME)) {
					// 就用GooglePlay打开链接
					intent.setPackage(Const.GOOGLE_PLAY_PACKAGE_NAME);
				}
			}

			try {
				context.startActivity(intent);
				result = true;
			} catch (Exception e) {
				// 做一个异常捕获，防止没有第三方程序可以打开这个链接
				e.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * 根据指定的应用商店名称,打开指定app的详情页面
	 */
	public static void openStore(Context context, String link,
			String storePackageName) {
		if (!TextUtils.isEmpty(link)) {
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_VIEW);
			intent.setData(Uri.parse(link));
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			// 判断是否是GooglePlay的链接
			if (link.startsWith(Const.GOOGLE_PLAY_PREFFIX_HTTP)
					|| link.startsWith(Const.GOOGLE_PLAY_PREFFIX_HTTPS)
					|| link.startsWith(Const.GOOGLE_PLAY_PREFFIX_MARKET)) {
				// 如果安装了GooglePlay
				if (isAppInstall(context, Const.GOOGLE_PLAY_PACKAGE_NAME)) {
					// 就用GooglePlay打开链接
					intent.setPackage(Const.GOOGLE_PLAY_PACKAGE_NAME);
				} else if (!TextUtils.isEmpty(storePackageName)) {
					// 如果安装了应用商店
					if (isAppInstall(context, storePackageName)) {
						intent.setPackage(storePackageName);
					}
				}
			}
			try {
				context.startActivity(intent);
			} catch (Exception e) {
				// 做一个异常捕获，防止没有第三方程序可以打开这个链接
				e.printStackTrace();
			}
		}
	}

	/**
	 * 打开指定包名的应用
	 * 
	 * @param context
	 *            上下文
	 * @param packageName
	 *            要打开的应用包名
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
	 * @param context
	 *            上下文
	 * @param action
	 *            要打开的动作
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
	 * @param downloadCount
	 *            下载量
	 * 
	 * @return 下载量的短字符串
	 * 
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
		// if (downloadCount > 1000000000L) {
		// result = downloadCount / 1000000000 + "G";
		// } else if (downloadCount > 1000000L) {
		// result = downloadCount / 1000000 + "M";
		// } else if (downloadCount > 1000) {
		// result = downloadCount / 1000 + "K";
		// } else {
		// result = downloadCount + "";
		// }
		return sb.toString();
	}

	/**
	 * 格式化指定的秒数为X天Y小时Z分钟S秒的字符串形式
	 * 
	 * @param context
	 *            上下文
	 * @param millis
	 *            要转化的秒数
	 * 
	 * @return
	 * 
	 * @author Liu Qing
	 * @Date 2014年12月8日 下午2:31:32
	 */
//	public static String formatTimeBucket(Context context, long millis) {
//		String result = "";
//		long days = millis / (1000 * 60 * 60 * 24);
//		long hours = (millis % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
//		long minutes = (millis % (1000 * 60 * 60)) / (1000 * 60);
//		long seconds = (millis % (1000 * 60)) / 1000;
//		if (days > 0) {
//			result += context.getString(R.string.time_countdown_d, days);
//		}
//		if (hours > 0) {
//			result += context.getString(R.string.time_countdown_h, hours);
//		}
//		if (minutes > 0) {
//			result += context.getString(R.string.time_countdown_m, minutes);
//		} else if (seconds > 0) {
//			minutes = 1;
//			result += context.getString(R.string.time_countdown_m, minutes);
//		}
//		return result;
//	}

//	public static String calRemainTimeString(Context context, long fullSeconds,
//			long startTimeMillis) {
//		long expirationTime = startTimeMillis + fullSeconds * 1000;
//		long remainLeft = expirationTime - System.currentTimeMillis();
//		String result = Utils.formatTimeBucket(context, remainLeft);
//		return result;
//	}

	public static String formatDate(long millis) {
		Date d = new Date(millis);
		// SimpleDateFormat formatter = new
		// SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat formatter = new SimpleDateFormat("MM-dd HH:mm");
		String result = formatter.format(d);
		return result;
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

	/**
	 * 检查当前是否在免打扰时段
	 */
//	public static boolean checkTime() {
//		// TODO
//		try {
//			SharedPreferences prefs = Const.ROOT_CONTEXT.getSharedPreferences(
//					Const.PrefName.MAIN, Context.MODE_PRIVATE);
//			if (!prefs.getBoolean(Const.PrefKey.Setting.NONE_NOTICE, true)) {
//				return false;
//			}
//			int hourOfDay = 0;
//			int minute = 0;
//			hourOfDay = prefs.getInt(Const.PrefKey.Setting.NONE_START_HOURS,
//					Const.DEFAULT_START);
//			minute = prefs.getInt(Const.PrefKey.Setting.NONE_START_MINUTES, 0);
//			Calendar calendar1 = Calendar.getInstance();
//			calendar1.set(Calendar.HOUR_OF_DAY, hourOfDay);
//			calendar1.set(Calendar.MINUTE, minute);
//
//			hourOfDay = prefs.getInt(Const.PrefKey.Setting.NONE_END_HOURS,
//					Const.DEFAULT_END);
//			minute = prefs.getInt(Const.PrefKey.Setting.NONE_END_MINUTES, 0);
//			Calendar calendar2 = Calendar.getInstance();
//			calendar2.set(Calendar.HOUR_OF_DAY, hourOfDay);
//			calendar2.set(Calendar.MINUTE, minute);
//
//			Calendar now = Calendar.getInstance();
//			if (calendar2.before(calendar1)) {
//				return now.after(calendar1) || now.before(calendar2);
//			} else {
//				return now.after(calendar1) && now.before(calendar2);
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return false;
//	}

	final static long day = 24 * 3600 * 1000;
	final static long hour = 3600 * 1000;
	final static long minutes = 60 * 1000;

//	public static String formatDate2Day(Context context, long time) {
//		String str = context.getResources().getString(R.string.news_time_day);
//		try {
//			if (time > 100) {
//				long now = System.currentTimeMillis();
//				long diff = now - time;
//				diff = diff > 0 ? diff : minutes;
//				if (diff > day * 2) {
//					Date date = new Date(time);
//					SimpleDateFormat formatter = new SimpleDateFormat(
//							"yyyy-MM-dd ");
//					str = formatter.format(date);
//				} else if (diff > day) {
//					str = context.getResources().getString(
//							R.string.news_time_yesterday);
//				} else if (diff > hour) {
//					str = context.getResources().getString(
//							R.string.news_time_hour, ("" + diff / hour));
//				} else {
//					str = context.getResources().getString(
//							R.string.news_time_minute, "" + diff / minutes);
//				}
//			}
//		} catch (Resources.NotFoundException e) {
//			e.printStackTrace();
//		}
//		return str;
//	}

	/**
	 * 判断email格式是否正确
	 */
	public static boolean isEmail(String email) {
		String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,"
				+ "3})(\\]?)$";
		Pattern p = Pattern.compile(str);
		Matcher m = p.matcher(email);
		return m.matches();
	}

	public static float dpToPx(Resources resources, float dp) {
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				resources.getDisplayMetrics());
	}

	public static boolean isYouTubeLink(String link) {
		boolean result = false;
		if (!TextUtils.isEmpty(link)) {
			if (link.startsWith(Const.YOUTUBE_PREFFIX_NORMAL)
					|| link.startsWith(Const.YOUTUBE_PREFFIX_SHORT)
					|| link.startsWith(Const.YOUTUBE_PREFFIX_EMBED)) {
				result = true;
			}
		}
		return result;
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

 
	// public static int calculateMemoryCacheSize(Context context) {
	// ActivityManager am = (ActivityManager) context
	// .getSystemService(ACTIVITY_SERVICE);
	// boolean largeHeap = (context.getApplicationInfo().flags &
	// FLAG_LARGE_HEAP) != 0;
	// int memoryClass = am.getMemoryClass();
	// if (largeHeap && SDK_INT >= HONEYCOMB) {
	// memoryClass = am.getLargeMemoryClass();
	// }
	// // Target ~15% of the available heap.
	// return 1024 * 1024 * memoryClass / 7;
	// }

	public static boolean isSilenceInstallPermissionAvaliable(Context context) {
		PackageManager pm = context.getPackageManager();
		int result = pm.checkPermission(
				android.Manifest.permission.INSTALL_PACKAGES,
				context.getPackageName());
		if (result == PackageManager.PERMISSION_GRANTED) {
			return true;
		} else {
			return false;
		}
	}
}