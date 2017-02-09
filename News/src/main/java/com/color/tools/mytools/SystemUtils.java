package com.color.tools.mytools;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Process;
import android.provider.Settings.Secure;
import android.telephony.NeighboringCellInfo;
import android.telephony.TelephonyManager;
import android.view.Display;
import android.view.WindowManager;

/**
 * 获取相关系统信息
 * 
 * @author gdpancheng@gmail.com 2013-10-12 下午2:26:34
 */
public class SystemUtils {
	private volatile static SystemUtils instance;
	
	private String mIMEI;
	private String mSIM;
	private String mMobileVersion;
	private String mNetwrokIso;
	private String mNetType;
	private String mDeviceID;
	private List<NeighboringCellInfo> mCellinfos;
	private Context context;

	public static final String systemWidth = "width";
	public static final String systemHeight = "height";
	public  static String UA = Build.MODEL;
	
	private  HashMap<String, Integer> map;

	private SystemUtils(Context context) {
		this.context=context;
		mTm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		mIMEI = mTm.getDeviceId();
		mMobileVersion = mTm.getDeviceSoftwareVersion();
		mCellinfos = mTm.getNeighboringCellInfo();
		mNetwrokIso = mTm.getNetworkCountryIso();
		mSIM = mTm.getSimSerialNumber();
		mDeviceID = getDeviceId(context);
		try {
			ConnectivityManager cm = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo info = cm.getActiveNetworkInfo();
			if (info != null) {
				mNetType = info.getTypeName();
			}
		} catch (Exception ex) {
			
		}
	}

	public static SystemUtils getInstance(Context context) {
		if (instance == null) {
			synchronized (SystemUtils.class) {
				if (instance == null) {
					instance = new SystemUtils(context);
				}
			}
		}
		return instance;
	}

	/**
	 * 获取应用程序名称
	 * 
	 * @author gdpancheng@gmail.com 2013-10-12 下午2:30:56
	 * @return
	 * @return String
	 */
	public String getAppName() {
		return getAppName(null);
	}

	/**
	 * 获取应用程序名称
	 * 
	 * @author gdpancheng@gmail.com 2013-10-12 下午2:30:43
	 * @param packageName
	 * @return
	 * @return String
	 */
	public String getAppName(String packageName) {
		String applicationName;

		if (packageName == null) {
			packageName = context.getPackageName();
		}

		try {
			PackageManager packageManager = context.getPackageManager();
			PackageInfo packageInfo = packageManager.getPackageInfo(
					packageName, 0);
			applicationName = context
					.getString(packageInfo.applicationInfo.labelRes);
		} catch (Exception e) {
			applicationName = "";
			e.printStackTrace();
		}

		return applicationName;
	}

	/**
	 * 获取版本名称
	 * 
	 * @author gdpancheng@gmail.com 2013-10-12 下午2:30:37
	 * @return
	 * @return String
	 */
	public  String getAppVersionNumber() {
		return getAppVersionNumber(null);
	}

	/**
	 * 获取版本名称
	 * 
	 * @author gdpancheng@gmail.com 2013-10-12 下午2:30:27
	 * @param packageName
	 * @return
	 * @return String
	 */
	public String getAppVersionNumber(String packageName) {
		String versionName;

		if (packageName == null) {
			packageName = context.getPackageName();
		}

		try {
			PackageManager packageManager = context.getPackageManager();
			PackageInfo packageInfo = packageManager.getPackageInfo(
					packageName, 0);
			versionName = packageInfo.versionName;
		} catch (Exception e) {
			versionName = "";
			e.printStackTrace();
		}

		return versionName;
	}

	/**
	 * 获取应用程序的版本号
	 * 
	 * @author gdpancheng@gmail.com 2013-10-12 下午2:30:12
	 * @return
	 * @return String
	 */
	public  String getAppVersionCode() {
		return getAppVersionCode(null);
	}

	/**
	 * 获取指定应用程序的版本号
	 * 
	 * @author gdpancheng@gmail.com 2013-10-12 下午2:29:51
	 * @param packageName
	 * @return
	 * @return String
	 */
	public String getAppVersionCode(String packageName) {
		String versionCode;

		if (packageName == null) {
			packageName = context.getPackageName();
		}

		try {
			PackageManager packageManager = context.getPackageManager();
			PackageInfo packageInfo = packageManager.getPackageInfo(
					packageName, 0);
			versionCode = Integer.toString(packageInfo.versionCode);
		} catch (Exception e) {
			versionCode = "";
			e.printStackTrace();
		}

		return versionCode;
	}

	/**
	 * 获取SDK版本
	 * 
	 * @author gdpancheng@gmail.com 2013-10-12 下午2:29:13
	 * @return
	 * @return int
	 */
	public static int getSdkVersion() {
		try {
			return Build.VERSION.class.getField("SDK_INT").getInt(null);
		} catch (Exception e) {
			e.printStackTrace();
			return 3;
		}
	}

	/*
	 * 判断是否是该签名打包
	 */
	public boolean isRelease(String signatureString) {
		final String releaseSignatureString = signatureString;
		if (releaseSignatureString == null
				|| releaseSignatureString.length() == 0) {
			throw new RuntimeException(
					"Release signature string is null or missing.");
		}

		final Signature releaseSignature = new Signature(releaseSignatureString);
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(),
					PackageManager.GET_SIGNATURES);
			for (Signature sig : pi.signatures) {
				if (sig.equals(releaseSignature)) {
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return true;
		}
		return false;
	}

	/**
	 * 判断是否是模拟器
	 * 
	 * @author gdpancheng@gmail.com 2013-10-12 下午2:28:40
	 * @return
	 * @return boolean
	 */
	public static boolean isEmulator() {
		return Build.MODEL.equals("sdk") || Build.MODEL.equals("google_sdk");
	}

	/**
	 * @author 潘城 gdpancheng@gmail.com 2012-5-7 下午1:21:48
	 * @Title: getMobileInfo
	 * @Description: 获取手机的硬件信息
	 * @param @return 设定文件
	 * @return String 返回类型
	 */
	public static String getMobileInfo() {
		StringBuffer sb = new StringBuffer();
		/**
		 * 通过反射获取系统的硬件信息 获取私有的信息
		 */
		try {
			Field[] fields = Build.class.getDeclaredFields();
			for (Field field : fields) {
				field.setAccessible(true);
				String name = field.getName();
				String value = field.get(null).toString();
				sb.append(name + "=" + value);
				sb.append("\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	
	/**
	 * @Title: getDisplayMetrics
	 * @Description: 获取屏幕的分辨率
	 * @param @param cx
	 * @param @return 设定文件
	 * @return HashMap<String,Integer> 返回类型
	 */
	public HashMap<String, Integer> getDisplayMetrics() {
		if (map == null) {
			map = new HashMap<String, Integer>();
			Display display = ((WindowManager) context
					.getSystemService(Context.WINDOW_SERVICE))
					.getDefaultDisplay();
			int screenWidth = display.getWidth();
			int screenHeight = display.getHeight();
			map.put(systemWidth, screenWidth);
			map.put(systemHeight, screenHeight);
		}
		return map;
	}

	

	public static int dp2px(Context context, float dipValue) {
		float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5F);
	}

	public static int px2dp(Context context, float pxValue) {
		float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5F);
	}
	
	public static float sp2px(Context context, float sp) {
		final float scale = context.getResources().getDisplayMetrics().scaledDensity;
		return sp * scale;
	}
	
	public static int getDensityRatio(Context ctx) {
		int CURRENT_DENSITY = ctx.getResources().getDisplayMetrics().densityDpi;
		int DENSITY_RATIO = CURRENT_DENSITY / 160;
		return DENSITY_RATIO;
	}
	
	
	
	/**
	 * 获取通知栏高度
	 * 
	 * @author gdpancheng@gmail.com 2012-2-12 下午07:37:13
	 * @Title: getBarHeight
	 * @param @param context
	 * @param @return 设定文件
	 * @return int 返回类型
	 */
	public static int getBarHeight(Context context) {
		Class<?> c = null;
		Object obj = null;
		Field field = null;
		int x = 0, sbar = 0;
		try {
			c = Class.forName("com.android.internal.R$dimen");
			obj = c.newInstance();
			field = c.getField("status_bar_height");
			x = Integer.parseInt(field.get(obj).toString());
			sbar = context.getResources().getDimensionPixelSize(x);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return sbar;
	}

	public static boolean hasFroyo() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
	}

	public static boolean hasGingerbread() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
	}

	public static boolean hasGingerbreadMR1() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD_MR1;
	}

	public static boolean hasHoneycomb() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
	}

	public static boolean hasHoneycombMR1() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1;
	}

	public static boolean hasICS() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
	}

	public static boolean hasJellyBean() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
	}

	public static boolean hasJellyBeanMR1() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1;
	}

	public static boolean isTablet(Context context) {
		return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
	}

	public static boolean isHoneycombTablet(Context context) {
		return hasHoneycomb() && isTablet(context);
	}

	public static boolean isGingerbread() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD
				&& Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1;
	}

	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		if (activeNetwork == null || !activeNetwork.isConnected()) {
			return false;
		}
		return true;
	}

	public static final int DEFAULT_THREAD_POOL_SIZE = getDefaultThreadPoolSize();

	/**
	 * get recommend default thread pool size
	 * 
	 * @return if 2 * availableProcessors + 1 less than 8, return it, else
	 *         return 8;
	 * @see {@link #getDefaultThreadPoolSize(int)} max is 8
	 */
	public static int getDefaultThreadPoolSize() {
		return getDefaultThreadPoolSize(8);
	}

	/**
	 * get recommend default thread pool size
	 * 
	 * @param max
	 * @return if 2 * availableProcessors + 1 less than max, return it, else
	 *         return max;
	 */
	public static int getDefaultThreadPoolSize(int max) {
		int availableProcessors = 2 * Runtime.getRuntime()
				.availableProcessors() + 1;
		return availableProcessors > max ? max : availableProcessors;
	}


	static TelephonyManager mTm = null;

	/**
	 * 在获取系统信息前初始化
	 * 
	 * @author gdpancheng@gmail.com 2013-10-22 下午1:14:12
	 * @return void
	 */


	/**
	 * 获得android设备-唯一标识，android2.2 之前无法稳定运行.
	 * */
	public static String getDeviceId(Context mCm) {
		String PREFS_FILE = "device_id.xml";
	    String PREFS_DEVICE_ID = "device_id";
	    UUID uuid = null;
        if (uuid == null) {
            synchronized (SystemUtils.class) {
                if (uuid == null) {
                    final SharedPreferences prefs = mCm
                            .getSharedPreferences(PREFS_FILE, 0);
                    final String id = prefs.getString(PREFS_DEVICE_ID, null);
                    if (id != null) {
                        // Use the ids previously computed and stored in the
                        // prefs file
                        uuid = UUID.fromString(id);
                    } else {
                        final String androidId = Secure.getString(
                        		mCm.getContentResolver(), Secure.ANDROID_ID);
                        // Use the Android ID unless it's broken, in which case
                        // fallback on deviceId,
                        // unless it's not available, then fallback on a random
                        // number which we store to a prefs file
                        try {
                            if (!"9774d56d682e549c".equals(androidId)) {
                                uuid = UUID.nameUUIDFromBytes(androidId
                                        .getBytes("utf8"));
                            } else {
                                final String deviceId = ((TelephonyManager) 
                                		mCm.getSystemService(
                                            Context.TELEPHONY_SERVICE))
                                            .getDeviceId();
                                uuid = deviceId != null ? UUID
                                        .nameUUIDFromBytes(deviceId
                                                .getBytes("utf8")) : UUID
                                        .randomUUID();
                            }
                        } catch (UnsupportedEncodingException e) {
                            throw new RuntimeException(e);
                        }
                        // Write the value out to the prefs file
                        prefs.edit().putString(PREFS_DEVICE_ID, uuid.toString())
                                .commit();
                    }
                }
            }
        }
		return uuid.toString();
	}

	public String getImei() {
		return mIMEI;
	}

	public String getSIM() {
		return mSIM;
	}

	public static String getUA() {
		return UA;
	}

	/**
	 * 获取设备信息 以字符串的形式
	 * 
	 * @author gdpancheng@gmail.com 2013-10-22 下午1:14:30
	 * @return String
	 */
	public String getDeviceInfo() {
		StringBuffer info = new StringBuffer();
		info.append("IMEI:").append(getImei());
		info.append("\n");
		info.append("SIM:").append(getSIM());
		info.append("\n");
		info.append("UA:").append(getUA());
		info.append("\n");
		info.append("MobileVersion:").append(mMobileVersion);

		info.append("\n");
		info.append("SDK: ").append(Build.VERSION.SDK);
		info.append("\n");
		info.append(getCallState());
		info.append("\n");
		info.append("SIM_STATE: ").append(getSimState());
		info.append("\n");
		info.append("SIM: ").append(getSIM());
		info.append("\n");
		info.append(getSimOpertorName());
		info.append("\n");
		info.append(getPhoneType());
		info.append("\n");
		info.append(getPhoneSettings(context));
		info.append("\n");
		return info.toString();
	}

	/**
	 * 检查sim的状态
	 * 
	 * @author gdpancheng@gmail.com 2013-10-22 下午1:15:25
	 * @return String
	 */
	public static String getSimState() {
		switch (mTm.getSimState()) {
		case TelephonyManager.SIM_STATE_UNKNOWN:
			return "未知SIM状态_"
					+ TelephonyManager.SIM_STATE_UNKNOWN;
		case TelephonyManager.SIM_STATE_ABSENT:
			return "没插SIM卡_"
					+ TelephonyManager.SIM_STATE_ABSENT;
		case TelephonyManager.SIM_STATE_PIN_REQUIRED:
			return "锁定SIM状态_需要用户的PIN码解锁_"
					+ TelephonyManager.SIM_STATE_PIN_REQUIRED;
		case TelephonyManager.SIM_STATE_PUK_REQUIRED:
			return "锁定SIM状态_需要用户的PUK码解锁_"
					+ TelephonyManager.SIM_STATE_PUK_REQUIRED;
		case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
			return "锁定SIM状态_需要网络的PIN码解锁_"
					+ TelephonyManager.SIM_STATE_NETWORK_LOCKED;
		case TelephonyManager.SIM_STATE_READY:
			return "就绪SIM状态_"
					+ TelephonyManager.SIM_STATE_READY;
		default:
			return "未知SIM状态_"
					+ TelephonyManager.SIM_STATE_UNKNOWN;

		}
	}

	/**
	 * 获取手机信号状态
	 * 
	 * @author gdpancheng@gmail.com 2013-10-22 下午1:15:37
	 * @return String
	 */
	public static String getPhoneType() {
		switch (mTm.getPhoneType()) {
		case TelephonyManager.PHONE_TYPE_NONE:
			return "PhoneType: 无信号_"
					+ TelephonyManager.PHONE_TYPE_NONE;
		case TelephonyManager.PHONE_TYPE_GSM:
			return "PhoneType: GSM信号_"
					+ TelephonyManager.PHONE_TYPE_GSM;
		case TelephonyManager.PHONE_TYPE_CDMA:
			return "PhoneType: CDMA信号_"
					+ TelephonyManager.PHONE_TYPE_CDMA;
		default:
			return "PhoneType: 无信号_"
					+ TelephonyManager.PHONE_TYPE_NONE;
		}
	}

	/**
	 * 服务商名称：例如：中国移动、联通 　　 SIM卡的状态必须是 SIM_STATE_READY(使用getSimState()判断). 　　
	 */
	public static String getSimOpertorName() {
		if (mTm.getSimState() == TelephonyManager.SIM_STATE_READY) {
			StringBuffer sb = new StringBuffer();
			sb.append("SimOperatorName: ").append(mTm.getSimOperatorName());
			sb.append("\n");
			sb.append("SimOperator: ").append(mTm.getSimOperator());
			sb.append("\n");
			sb.append("Phone:").append(mTm.getLine1Number());
			return sb.toString();
		} else {
			StringBuffer sb = new StringBuffer();
			sb.append("SimOperatorName: ").append("未知");
			sb.append("\n");
			sb.append("SimOperator: ").append("未知");
			return sb.toString();
		}
	}

	/**
	 * 获取手机设置状态 比如蓝牙开启与否
	 * 
	 * @author gdpancheng@gmail.com 2013-10-22 下午1:16:02
	 * @return String
	 */
	public static String getPhoneSettings(Context context) {
		StringBuffer buf = new StringBuffer();
		String str = Secure.getString(context.getContentResolver(),
				Secure.BLUETOOTH_ON);
		buf.append("蓝牙:");
		if (str.equals("0")) {
			buf.append("禁用");
		} else {
			buf.append("开启");
		}
		//
		str = Secure.getString(context.getContentResolver(),
				Secure.BLUETOOTH_ON);
		buf.append("WIFI:");
		buf.append(str);

		str = Secure.getString(context.getContentResolver(),
				Secure.INSTALL_NON_MARKET_APPS);
		buf.append("APP位置来源:");
		buf.append(str);

		return buf.toString();
	}

	/**
	 * 获取电话状态
	 * 
	 * @author gdpancheng@gmail.com 2013-10-22 下午1:16:37
	 * @return String
	 */
	public static String getCallState() {
		switch (mTm.getCallState()) {
		case TelephonyManager.CALL_STATE_IDLE:
			return "电话状态[CallState]: 无活动";
		case TelephonyManager.CALL_STATE_OFFHOOK:
			return "电话状态[CallState]: 无活动";
		case TelephonyManager.CALL_STATE_RINGING:
			return "电话状态[CallState]: 无活动";
		default:
			return "电话状态[CallState]: 未知";
		}
	}

	public String getNetwrokIso() {
		return mNetwrokIso;
	}

	/**
	 * @return the mDeviceID
	 */
	public String getmDeviceID() {
		return mDeviceID;
	}

	/**
	 * @return the mNetType
	 */
	public String getNetType() {
		return mNetType;
	}
	
	public static String getCurProcessName(Context context) {
		int pid = Process.myPid();

		ActivityManager mActivityManager = (ActivityManager) context
				.getSystemService("activity");

		for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager
				.getRunningAppProcesses()) {
			if (appProcess.pid == pid) {
				return appProcess.processName;
			}
		}
		return null;
	}

}
