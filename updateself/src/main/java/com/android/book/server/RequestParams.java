package com.android.book.server;

import android.content.Context;

import com.android.book.Const;
import com.android.book.Utils;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class RequestParams {
	/**
	 * @description 初始化手机参数data
	 */
	public static String initRequestParams(Context context, List<NameValuePair> extData) {
		List<NameValuePair> params = new ArrayList<>();
		params.add(new BasicNameValuePair(Const.RequestParam.PACKAGE_NAME_SELF,""+context.getPackageName()));
		params.add(new BasicNameValuePair(Const.RequestParam.LAUGUAGE,""+Utils.getLanguage(context)));
		params.add(new BasicNameValuePair(Const.RequestParam.COUNTRY,Utils.getCountry(context)));
		params.add(new BasicNameValuePair(Const.RequestParam.MANUFACTURE,""+Utils.getManufacturer()));
		params.add(new BasicNameValuePair(Const.RequestParam.MODEL,""+Utils.getPhoneModel()));
		params.add(new BasicNameValuePair(Const.RequestParam.SIM_OPERATOR,""+Utils.getSimOperator(context)));
		params.add(new BasicNameValuePair(Const.RequestParam.IMEI,""+Utils.getIMEI(context)));
		params.add(new BasicNameValuePair(Const.RequestParam.IMSI, "" + Utils.getIMSI(context)));
		params.add(new BasicNameValuePair(Const.RequestParam.ANDROID_ID, "" + Utils.getAndroidId(context)));
		params.add(new BasicNameValuePair(Const.RequestParam.OS_VERSION, "" + Utils.getOsVersion()));
		params.add(new BasicNameValuePair(Const.RequestParam.SCREEN_RESOLUTION, "" + Utils.getScreenResolution(context)));
		params.add(new BasicNameValuePair(Const.RequestParam.UUID, "" + Utils.getDeviceUUID(context)));
		params.add(new BasicNameValuePair(Const.RequestParam.VERSION_CODE, "" + Utils.getVersionCode(context)));
		params.add(new BasicNameValuePair(Const.RequestParam.IS_ROM, "" + Utils.isSystemApp(context)));
		params.add(new BasicNameValuePair(Const.RequestParam.IS_SYSTEM_APP, "" +  Utils.isSystemApp(context)));
		params.add(new BasicNameValuePair(Const.RequestParam.CAN_SILENT, "" + Utils.isSilenceInstallPermissionAvaliable(context)));

		params.add(new BasicNameValuePair(Const.RequestParam.HAS_GOOGLE_MARKET, "" +  (Utils.isAppInstall(context, Const.GOOGLE_PLAY_PACKAGE_NAME) ? "1"
				: "0")));

//		params.add("channelId", ""+ WelcomeActivity.channelId);
//		params.add("gameId", ""+WelcomeActivity.gameId);

		params.addAll(Cha.getInstance(context).getNameValues());
		if(extData!=null)
			params.addAll(extData);
		//z+
		params.add(new BasicNameValuePair("curVersion", "" + Const.version));

//		Log.e("test", builder.toString());
		return URLEncodedUtils.format(params,"UTF-8");
	}

	/**
	 * @description channerId
	 */
	private static String getChannelId(Context context) {
		return Cha.getInstance(context).getChannelId();
	}

	/**
	 * @description gameId
	 */
	private static String getGameId(Context context) {
		return Cha.getInstance(context).getGameId();
	}

}