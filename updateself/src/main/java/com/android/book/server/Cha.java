package com.android.book.server;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * 用于管理cha.txt/chg，默认位置是assets/ --有可能未被打入到游戏中。
 * 
 * @author zhengnan
 * @date 2014年10月31日
 */
public class Cha {
	// singleton
	private static Cha ins = null;

	private boolean existCha_png = false;

	public static Cha getInstance(Context ctx) {
		if (ins == null)
			ins = new Cha(ctx);
		if (ins.ctx == null)
			ins.ctx = ctx;
		return ins;
	}

	private Cha(Context ctx) {
		this.ctx = ctx;
		String ptyName = getExistPtyName();

		if (ptyName.equals("")) {
			isExist = false;
			return;
		}
		isExist = true;

		Properties pty = null;
		if (existCha_png) {
			try {
				pty = getPty4bytes(ChaUtil
						.reveal(ctx.getAssets().open(ptyName)));
			} catch (IOException e) {
				throw new RuntimeException();
			}
		} else
			pty = readAssetsProPerty(ptyName, ctx);

		nameValues = pty2pairList(pty);
		byte[] gameIds = { 103, 97, 109, 101, 73, 100 };// "gameId"
		gameId = pty.getProperty(new String(gameIds), "");
		byte[] channelIds = { 99, 104, 97, 110, 110, 101, 108, 73, 100 };// "channelId"

		channelId = pty.getProperty(new String(channelIds), "");
		extDatas = getExtData(pty);
	}

	/**
	 * 通过bytes造一个pty
	 * 
	 * @param bs
	 * @return
	 */
	public static Properties getPty4bytes(byte[] bs) {
		Properties pty = new Properties();
		try {
			InputStream in = new ByteArrayInputStream(bs);
			pty.load(in);
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pty;
	}

	public static Properties readAssetsProPerty(String fileName, Context ctx) {
		Properties pty = new Properties();
		try {
			InputStream in = ctx.getAssets().open(fileName);
			pty.load(in);
			in.close();
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
		return pty;
	}

	public static List<NameValuePair> pty2pairList(Properties pty) {
		List<NameValuePair> datas = new ArrayList<NameValuePair>();
		Iterator its = pty.entrySet().iterator();
		while (its.hasNext()) {
			Map.Entry<String, String> ent = (Map.Entry<String, String>) its
					.next();
			datas.add(new BasicNameValuePair(ent.getKey(), ent.getValue()));
		}
		return datas;
	}

	// field
	private Context ctx = null;
	private boolean isExist = false;
	private String gameId = "";// appid
	private String channelId = "";// 渠道id
	private Map<String, String> extDatas = null;// 由于是外部加入的,可能会有许多额外数据。存在除基本数据外的，额外数据
	private List<NameValuePair> nameValues = null;// 将所有键值以参数对的形式添加到list

	// method
	/**
	 * @return 由于差异性的存在，有的游戏加的是cha.txt-海外，有的是cha.chg-国内，char.pro-网游。需要兼容两种情况！
	 * @throws IOException
	 */
	private String getExistPtyName() {
		byte[] cha_png = { 99, 104, 97, 46, 112, 110, 103 };
		byte[] cha_pro = { 99, 104, 97, 46, 112, 114, 111 };
		byte[] cha_chg = { 99, 104, 97, 46, 99, 104, 103 };
		byte[] cha_txt = { 99, 104, 97, 46, 116, 120, 116 };
		// cha.png 的hashCode 737045975 --对应原来的cha.txt
		String fileName = new String(cha_png).hashCode() + "";
		// if(AssetsManager.isExist(ctx, new String(cha_png).hashCode()+"")){
		// Util_Log.log("存在cha.png : "+fileName);
		// existCha_png = true;
		// return fileName;
		// }
		fileName = getChaPng(ctx);
		if (!fileName.equals("")) {
			existCha_png = true;
			return fileName;
		}

		if (isExist(ctx, new String(cha_chg))) {
			return new String(cha_chg);
			// existFileName = "cha.chg";
		} else if (isExist(ctx, new String(cha_pro))) {
			return new String(cha_pro);
		} else if (isExist(ctx, new String(cha_txt))) {
			return new String(cha_txt);
		}
		return "";
	}

	/**
	 * 将获取结果保存，以保证减少assets.list""的加载时间
	 * 
	 * @param ctx
	 * @return
	 */
	private String getChaPng(Context ctx) {
		SharedPreferences fp = PreferenceManager
				.getDefaultSharedPreferences(ctx);
		byte[] cha_png = { 99, 104, 97, 46, 112, 110, 103 };
		String charpng = new String(cha_png);
		String realChapngName = fp.getString(charpng, "");
		String rt = "";
		if (realChapngName.equals("")) {// 从未检索过,
			try {
				String[] files = ctx.getAssets().list("");
				for (int i = 0; i < files.length; i++) {
					if (files[i].contains(charpng.hashCode() + "")) {
						rt = files[i];
						break;
					}
				}
				// set
				if (rt.equals("")) {
					fp.edit().putString(charpng, "not").commit();
				} else {
					fp.edit().putString(charpng, rt).commit();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (realChapngName.equals("not")) {// 不存在

		} else {// 存在 ，直接去获取
			rt = realChapngName;
			// 若存在，则再次判断下，以防更新时未覆盖
			if (!isExist(ctx, rt)) {
				fp.edit().putString(charpng, "").commit();
				return getChaPng(ctx);
			}
		}
		return rt;
		/* */}

	/**
	 * 返回额外的数据
	 * 
	 * @param pty
	 * @return
	 */
	private Map<String, String> getExtData(Properties pty) {
		Map<String, String> datas = new HashMap<String, String>((Map) pty);
		datas.remove(gameId);
		datas.remove(channelId);
		return datas;
	}

	/** get **/
	public String getGameId() {
		return gameId;
	}

	public String getChannelId() {
		return channelId;
	}

	public Map<String, String> getExtDatas() {
		return extDatas;
	}

	public boolean isExist() {
		return isExist;
	}

	public List<NameValuePair> getNameValues() {
		if (nameValues == null) {
			nameValues = new ArrayList<NameValuePair>();

			// nameValues.add(new BasicNameValuePair("cha.xx", "notExist"));//
		}
		return nameValues;
	}

	public void setNameValues(List<NameValuePair> nameValues) {
		this.nameValues = nameValues;
	}

	private static HashMap<String, Boolean> isExistMap = new HashMap<String, Boolean>();

	public static boolean isExist(Context ctx, String file) {
		if (isExistMap.containsKey(file))
			return isExistMap.get(file);

		boolean isExist = false;
		try {
			ctx.getAssets().open(file);
			isExist = true;
		} catch (FileNotFoundException n) {
			isExist = false;
		} catch (IOException e) {
			//
			e.printStackTrace();
		}
		isExistMap.put(file, isExist);
		return isExist;
	}
}
