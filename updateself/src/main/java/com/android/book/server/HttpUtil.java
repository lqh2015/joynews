package com.android.book.server;

import com.android.book.Util_Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtil {
	/**
	 * POST方式发送数据
	 */
	public static String sendPost(String http, String data ) {
		StringBuffer rval = new StringBuffer();
		try {
			URL url = new URL(http);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setConnectTimeout(10000);
			conn.setReadTimeout(15000);
			conn.setDoOutput(true);
			conn.setDoInput(true);
//			conn.setRequestProperty("Content-Encoding", "gzip");
			conn.connect();
			// 传送数据
			if (data != null && !"".equals(data)) {
				OutputStream os = conn.getOutputStream();
				OutputStreamWriter out = new OutputStreamWriter(os);
				BufferedWriter bw = new BufferedWriter(out);
				bw.write(data);
				bw.flush();
				bw.close();
				out.close();
				os.close();
			}

			// 接收数据
			if (conn.getResponseCode() == 200) {
				InputStream is = conn.getInputStream();
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				String line;
				while ((line = br.readLine()) != null) {
					rval.append(line).append(System.getProperty("line.separator"));
				}
				br.close();
				isr.close();
				is.close();
			}
			conn.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}

		Util_Log.e("post data ： " + data);
		return rval.toString().trim();
	}
//	public static String sendPost(String url, String data) {
//		StringBuffer stringBuffer = new StringBuffer();
//		try {
//			URL realUrl = new URL(url);
//			HttpURLConnection connection = (HttpURLConnection) realUrl
//					.openConnection();
//			connection.setRequestMethod("POST");
//			connection.setDoOutput(true);
//			connection.setDoInput(true);
//			connection.connect();
//			// 传送数据
//			if (data != null && !"".equals(data)) {
//				OutputStream os = connection.getOutputStream();
//				BufferedOutputStream bos = new BufferedOutputStream(os);
//				OutputStreamWriter out = new OutputStreamWriter(bos);
//				out.write(data);
//				out.flush();
//				out.close();
//			}
//
//			// 接收数据
//			InputStream is = connection.getInputStream();
//			BufferedInputStream bis = new BufferedInputStream(is);
//			InputStreamReader isr = new InputStreamReader(bis);
//			char[] buffer = new char[1024];
//			while (isr.read(buffer) != -1) {
//				stringBuffer.append(buffer);
//			}
//			is.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//			Log.w("Sprinkle_HttpUtil", "Exception:" + e.getMessage());
//		}
//		return stringBuffer.toString().trim();
//	}
	/**
	 * GET方式发送数据
	 */
	public static String sendGet(String http, String data) {
		Util_Log.i("url:" + http + " , param:" + data);
		StringBuffer rval = new StringBuffer();
		try {
			if (data != null && !"".equals(data)) {
				http = http + "?" + data;
			}
			URL url = new URL(http);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(3000);
			conn.setReadTimeout(15000);
			conn.connect();
			// 接收数据
			InputStream is = conn.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line;
			while ((line = br.readLine()) != null) {
				rval.append(line).append(System.getProperty("line.separator"));
			}
			br.close();
			isr.close();
			is.close();
			conn.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rval.toString().trim();
	}
//	public static String sendGet(String url, String data) {
//		Util_Log.i("url:"+url+" , param:"+data);
//		StringBuffer stringBuffer = new StringBuffer();
//		try {
//			if (data != null && !"".equals(data)) {
//				url = url + "?" + data;
//			}
//			URL realUrl = new URL(url);
//			HttpURLConnection connection = (HttpURLConnection) realUrl
//					.openConnection();
//			connection.setRequestMethod("GET");
//			connection.connect();
//			// 接收数据
//			InputStream is = connection.getInputStream();
//			BufferedInputStream bis = new BufferedInputStream(is);
//			InputStreamReader isr = new InputStreamReader(bis);
//			char[] buffer = new char[1024];
//			while (isr.read(buffer) != -1) {
//				stringBuffer.append(buffer);
//			}
//			is.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//			Log.w("Sprinkle_HttpUtil", "Exception:" + e.getMessage());
//		}
//		return stringBuffer.toString().trim();
//	}

}