package com.color.tools.mytools;

import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class FjsonUtil {
	
	public static JSONObject parseObject(String text){
		JSONObject obj = null;
		try {
			obj=JSONObject.parseObject(text);
		} catch (Exception e) {
			LogUtils.e(text);
			e.printStackTrace();
		}
		return obj;
	}
	
	public static JSONArray parseArray(String text){
		JSONArray array = null;
		try {
			array=JSONArray.parseArray(text);
		} catch (Exception e) {
			LogUtils.e(text);
			e.printStackTrace();
		}
		return array;
	}
	
	public static <T> T parseObject(String text,Class<T> clazz){
		T t=null;
		try {
			t=JSONObject.parseObject(text, clazz);
		} catch (Exception e) {
			LogUtils.e(text);
			e.printStackTrace();
		}
		return t;
	}
	
	public static <T> List<T> parseArray(String text,Class<T> clazz){
		List<T> list=null;
		try {
			list=JSONObject.parseArray(text, clazz);
		} catch (Exception e) {
			LogUtils.e(text);
			e.printStackTrace();
		}
		return list;
	}

	public static String toJsonString(Object object){
		String json=null;
		try {
			json=JSONObject.toJSONString(object);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return json;
	}
}
