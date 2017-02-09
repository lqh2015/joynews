package com.android.book.interval;

import org.json.JSONObject;

public class IntervalModel {
    public IntervalModel(JSONObject json,String tag){
	try {
	    setIntervalValue(Long.parseLong( getJsonParameter(json, "intervalValue", "0")));
	    setBeginTime(Long.parseLong( getJsonParameter(json, "beginTime", "0")));
	   setTag(tag);
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }
    
    /** 间隔时间 **/
    private long intervalValue = 0;
    /** 开始计时时间 **/
    private long beginTime = 0;
    /** 对应标签 **/
    private String tag = "";
    
    
    @Override
    public String toString() {
	try {
	    	JSONObject json = new JSONObject();
		json.put("intervalValue", getIntervalValue());
		json.put("beginTime", getBeginTime());
 		json.put("tag", getTag());
		return json.toString();
	} catch (Exception e) {
	}
	return "";
    }
    
    public long getIntervalValue() {
        return intervalValue;
    }
    public void setIntervalValue(long intervalValue) {
        this.intervalValue = intervalValue;
    }
    public long getBeginTime() {
        return beginTime;
    }
    public void setBeginTime(long beginTime) {
        this.beginTime = beginTime;
    }
    public String getTag() {
        return tag;
    }
    public void setTag(String tag) {
        this.tag = tag;
    }

    public static String getJsonParameter(JSONObject jsonAd, String parameter,
                                          String initValue) {
        if (jsonAd == null)
            return initValue;
        String returnValue = "-1";
        try {
            if (jsonAd.has(parameter)) {
                if (!jsonAd.getString(parameter).equals("")) {
                    returnValue = jsonAd.getString(parameter);
                } else {
                    returnValue = initValue;
                }
            } else {
                returnValue = initValue;
            }
        } catch (Exception ex1) {
            returnValue = initValue;
        }
        return returnValue;
    }
}
