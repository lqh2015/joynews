package com.color.tools.mytools;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Service;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.Uri.Builder;
import android.os.Environment;
import android.os.Process;
import android.os.StatFs;
import android.os.Vibrator;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.URLSpan;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
/**
 * 
 * @author lixinyuan
 * 
 *
 */
@SuppressLint({ "SimpleDateFormat" })
public class Util {
	private static final String TAG = "Util";
	private static final char SPLIT = ',';
	private static final SimpleDateFormat timeFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm");

	public static Location getLat(Context context, LocationListener listener) {
		LocationManager locMan = (LocationManager) context
				.getSystemService("location");
		Location location = locMan.getLastKnownLocation("gps");
		if (location == null) {
			location = locMan.getLastKnownLocation("network");
		}
		return location;
	}

	public static String getUrlWithAuth(String url, Context context) {
		if ((url == null) || (url.length() == 0)) {
			return null;
		}

		URI uri = URI.create(url);

		if ((uri == null) || (uri.getHost() == null)) {
			return null;
		}

		return url;
	}

	public static String bytesToHexString(byte[] bytes) {
		if ((bytes == null) || (bytes.length <= 0)) {
			return null;
		}

		StringBuilder stringBuilder = new StringBuilder();

		for (int i = 0; i < bytes.length; i++) {
			int intValue = bytes[i] & 0xFF;
			String hexValue = Integer.toHexString(intValue);

			if (hexValue.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hexValue);
		}

		return stringBuilder.toString().toUpperCase();
	}

	public static void shake(long milliseconds, Context context) {
		Vibrator vVi = (Vibrator) context.getSystemService("vibrator");
		vVi.vibrate(milliseconds);
		vVi.vibrate(new long[] { 200L, 200L, 1000L }, -1);
	}

	/**
	 * 设置手机立刻震动
	 * */
	public static void Vibrate(Context context, long milliseconds) {
		Vibrator vib = (Vibrator) context
				.getSystemService(Service.VIBRATOR_SERVICE);
		vib.vibrate(milliseconds);
	}
	
	public static int calculateLength(String arg) {
		if ((arg == null) || ("".equals(arg))) {
			return 0;
		}
		int count = 0;

		for (int i = 0; i < arg.length(); i++) {
			if (isChinese(arg.charAt(i)))
				count += 2;
			else {
				count++;
			}
		}

		return count;
	}

	public static String getTwentyCharNickName(String arg) {
		if ((!TextUtils.isEmpty(arg)) && (calculateLength(arg) > 20)) {
			int length = 0;
			for (int i = 0; i < arg.length(); i++) {
				if (isChinese(arg.charAt(i)))
					length += 2;
				else {
					length++;
				}
				if (length == 20)
					return arg.substring(0, i + 1);
				if (length >= 21) {
					return arg.substring(0, i);
				}
			}
		}
		return arg;
	}

	public static boolean isChinese(char c) {
		Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
		if ((ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS)
				|| (ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS)
				|| (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A)
				|| (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B)
				|| (ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION)
				|| (ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS)
				|| (ub == Character.UnicodeBlock.GENERAL_PUNCTUATION)) {
			return true;
		}
		return false;
	}

	public static boolean isChinese(String arg) {
		Pattern pattern = Pattern.compile("[\\u4e00-\\u9fa5]");
		Matcher matcher = pattern.matcher(arg);

		return matcher.find();
	}

	
	public static byte[] desEncrypt(byte[] datasource, String key) {
		try {
			SecureRandom random = new SecureRandom();
			DESKeySpec desKey = new DESKeySpec(key.getBytes());

			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			SecretKey securekey = keyFactory.generateSecret(desKey);

			Cipher cipher = Cipher.getInstance("DES");

			cipher.init(1, securekey, random);

			return Base64.encode(cipher.doFinal(datasource), 0);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}

	public static byte[] desDecrypt(byte[] datasource, String key) {
		try {
			byte[] base64Data = Base64.decode(datasource, 0);

			SecureRandom random = new SecureRandom();

			DESKeySpec desKey = new DESKeySpec(key.getBytes());

			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");

			SecretKey securekey = keyFactory.generateSecret(desKey);

			Cipher cipher = Cipher.getInstance("DES");

			cipher.init(2, securekey, random);

			return cipher.doFinal(base64Data);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}

	public static <T> String listToString(List<T> list, char split) {
		StringBuilder buffer = new StringBuilder();
		int size = list.size();
		for (int i = 0; i < size; i++) {
			buffer.append(list.get(i));
			buffer.append(split);
		}
		buffer.deleteCharAt(buffer.length() - 1);
		return buffer.toString();
	}

	public static <T> String listToString(List<T> list) {
		return listToString(list, ',');
	}

	public static int strLength(String value) {
		if (TextUtils.isEmpty(value)) {
			return 0;
		}
		int valueLength = 0;
		String chinese = "[Α-￥]";

		for (int i = 0; i < value.length(); i++) {
			String temp = value.substring(i, i + 1);

			if (temp.matches(chinese)) {
				valueLength += 2;
			} else {
				valueLength++;
			}
		}
		return valueLength;
	}

	public static String getCrrentDateTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(new Date());
	}

	public static String getImageKeyFileName(String imageKey) {
		String fileName = null;

		if ((!TextUtils.isEmpty(imageKey)) && (imageKey.lastIndexOf("/") != -1)) {
			fileName = imageKey.substring(imageKey.lastIndexOf("/") + 1);
		}
		return fileName;
	}

	public static void saveDataToDir(File file, byte[] data) throws IOException {
		FileOutputStream stream = new FileOutputStream(file);

		stream.write(data);
		stream.flush();
		stream.close();
	}

	public static byte[] getByteFromUri(Context context, Uri uri) {
		InputStream input = null;
		try {
			input = context.getContentResolver().openInputStream(uri);

			int count = 0;
			while (count == 0) {
				count = input.available();
			}

			byte[] bytes = new byte[count];
			input.read(bytes);

			return bytes;
		} catch (Exception e) {
			return null;
		} finally {
			if (input != null)
				try {
					input.close();
				} catch (IOException e) {
				}
		}
	}

	
	public static Date strTODate(String dateStr) {
		if ((dateStr == null) || (dateStr.length() == 0))
			return null;
		Date date = null;
		try {
			date = timeFormat.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	public static String dateToStr(Date date) {
		if (date == null) {
			return null;
		}

		return timeFormat.format(date);
	}

	
	
	
}
