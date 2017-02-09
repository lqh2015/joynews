package com.hzpd.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;

import com.color.tools.mytools.LogUtils;
import com.hzpd.hflt.R;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;

/**
 * uuid生成 sp2dp dp2sp destiny 网络状态 ip地址 mac地址
 */
public class MyCommonUtil {

	public static String getMyUUID(Context context) {

		final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

		final String tmDevice, tmSerial, androidId;

		tmDevice = "" + tm.getDeviceId();
		tmSerial = "" + tm.getSimSerialNumber();
		androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(),
				android.provider.Settings.Secure.ANDROID_ID);
		UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
		String uniqueId = deviceUuid.toString();

		return uniqueId;

	}

	/**
	 * @return 网络是否连接可用
	 */
	public static boolean isNetworkConnected(Context context) {
		ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkinfo = connManager.getActiveNetworkInfo();
		if (networkinfo != null) {
			return networkinfo.isAvailable();
		}
		return false;
	}

	public String getLocalMacAddress(Context context) {
		WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		return info.getMacAddress();
	}

	public static String getIP() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {
			LogUtils.i("WifiPreference IpAddress:" + ex.toString());
		}
		return null;
	}

	public static float dp2px(Resources resources, float dp) {
		final float scale = resources.getDisplayMetrics().density;
		return dp * scale + 0.5f;
	}

	public static float sp2px(Resources resources, float sp) {
		final float scale = resources.getDisplayMetrics().scaledDensity;
		return sp * scale;
	}

	public static float px2dip(Resources resources, int px) {
		final float scale = resources.getDisplayMetrics().scaledDensity;
		return px / scale + 0.5f;
	}

	public static int getDensityRatio(Context ctx) {
		int CURRENT_DENSITY = ctx.getResources().getDisplayMetrics().densityDpi;
		int DENSITY_RATIO = CURRENT_DENSITY / 160;
		return DENSITY_RATIO;
	}

	public static DisplayMetrics getDisplayMetric(Resources resources) {
		DisplayMetrics metrics = resources.getDisplayMetrics();
		return metrics;
	}

	public static String getSimId(Context context) {
		TelephonyManager telMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return telMgr.getDeviceId();
	}

	private static final int NETWORK_CLASS_WIFI = -101;
	private static final int NETWORK_CLASS_UNAVAILABLE = -1;
	/** Unknown network class. */
	private static final int NETWORK_CLASS_UNKNOWN = 0;
	/** Class of broadly defined "2G" networks. */
	private static final int NETWORK_CLASS_2_G = 1;
	/** Class of broadly defined "3G" networks. */
	private static final int NETWORK_CLASS_3_G = 2;
	/** Class of broadly defined "4G" networks. */
	private static final int NETWORK_CLASS_4_G = 3;

	private static final int NETWORK_TYPE_UNAVAILABLE = -1;
	// private static final int NETWORK_TYPE_MOBILE = -100;
	private static final int NETWORK_TYPE_WIFI = -101;

	// 适配低版本手机
	/** Network type is unknown */
	public static final int NETWORK_TYPE_UNKNOWN = 0;
	/** Current network is GPRS */
	public static final int NETWORK_TYPE_GPRS = 1;
	/** Current network is EDGE */
	public static final int NETWORK_TYPE_EDGE = 2;
	/** Current network is UMTS */
	public static final int NETWORK_TYPE_UMTS = 3;
	/** Current network is CDMA: Either IS95A or IS95B */
	public static final int NETWORK_TYPE_CDMA = 4;
	/** Current network is EVDO revision 0 */
	public static final int NETWORK_TYPE_EVDO_0 = 5;
	/** Current network is EVDO revision A */
	public static final int NETWORK_TYPE_EVDO_A = 6;
	/** Current network is 1xRTT */
	public static final int NETWORK_TYPE_1xRTT = 7;
	/** Current network is HSDPA */
	public static final int NETWORK_TYPE_HSDPA = 8;
	/** Current network is HSUPA */
	public static final int NETWORK_TYPE_HSUPA = 9;
	/** Current network is HSPA */
	public static final int NETWORK_TYPE_HSPA = 10;
	/** Current network is iDen */
	public static final int NETWORK_TYPE_IDEN = 11;
	/** Current network is EVDO revision B */
	public static final int NETWORK_TYPE_EVDO_B = 12;
	/** Current network is LTE */
	public static final int NETWORK_TYPE_LTE = 13;
	/** Current network is eHRPD */
	public static final int NETWORK_TYPE_EHRPD = 14;
	/** Current network is HSPA+ */
	public static final int NETWORK_TYPE_HSPAP = 15;

	public static String getCurrentNetworkType(Context context) {
		int networkClass = getNetworkClass(context);
		String type = "未知";
		switch (networkClass) {
			case NETWORK_CLASS_UNAVAILABLE:
				type = "无";
				break;
			case NETWORK_CLASS_WIFI:
				type = "Wi-Fi";
				break;
			case NETWORK_CLASS_2_G:
				type = "2G";
				break;
			case NETWORK_CLASS_3_G:
				type = "3G";
				break;
			case NETWORK_CLASS_4_G:
				type = "4G";
				break;
			case NETWORK_CLASS_UNKNOWN:
				type = "未知";
				break;
		}
		return type;
	}

	private static int getNetworkClassByType(int networkType) {
		switch (networkType) {
			case NETWORK_TYPE_UNAVAILABLE:
				return NETWORK_CLASS_UNAVAILABLE;
			case NETWORK_TYPE_WIFI:
				return NETWORK_CLASS_WIFI;
			case NETWORK_TYPE_GPRS:
			case NETWORK_TYPE_EDGE:
			case NETWORK_TYPE_CDMA:
			case NETWORK_TYPE_1xRTT:
			case NETWORK_TYPE_IDEN:
				return NETWORK_CLASS_2_G;
			case NETWORK_TYPE_UMTS:
			case NETWORK_TYPE_EVDO_0:
			case NETWORK_TYPE_EVDO_A:
			case NETWORK_TYPE_HSDPA:
			case NETWORK_TYPE_HSUPA:
			case NETWORK_TYPE_HSPA:
			case NETWORK_TYPE_EVDO_B:
			case NETWORK_TYPE_EHRPD:
			case NETWORK_TYPE_HSPAP:
				return NETWORK_CLASS_3_G;
			case NETWORK_TYPE_LTE:
				return NETWORK_CLASS_4_G;
			default:
				return NETWORK_CLASS_UNKNOWN;
		}
	}

	private static int getNetworkClass(Context context) {
		int networkType = NETWORK_TYPE_UNKNOWN;
		try {
			final NetworkInfo network = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE))
					.getActiveNetworkInfo();
			if (network != null && network.isAvailable() && network.isConnected()) {
				int type = network.getType();
				if (type == ConnectivityManager.TYPE_WIFI) {
					networkType = NETWORK_TYPE_WIFI;
				} else if (type == ConnectivityManager.TYPE_MOBILE) {
					TelephonyManager telephonyManager = (TelephonyManager) context
							.getSystemService(Context.TELEPHONY_SERVICE);
					networkType = telephonyManager.getNetworkType();
				}
			} else {
				networkType = NETWORK_TYPE_UNAVAILABLE;
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return getNetworkClassByType(networkType);

	}

	public static void startApp(Activity activity, String packageName) {
		if (!isAvilible(activity, packageName)) {
			LogUtils.i("start download");
			TUtils.ToastLeftAndRight(activity, null, R.drawable.notice_failure,activity.getString(R.string.toast_app_not_installed));
		} else {
			LogUtils.i("start bookcat");
			ComponentName componetName = new ComponentName(packageName, packageName);
			Intent intent = new Intent();
			intent.setComponent(componetName);
			activity.startActivity(intent);
		}
	}

	private static boolean isAvilible(Context context, String packageName) {
		final PackageManager packageManager = context.getPackageManager();
		List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
		if (pinfo != null) {
			for (int i = 0; i < pinfo.size(); i++) {
				if (packageName.equalsIgnoreCase(pinfo.get(i).packageName)) {
					return true;
				}
			}
		}
		return false;
	}

}