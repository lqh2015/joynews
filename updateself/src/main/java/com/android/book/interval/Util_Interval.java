package com.android.book.interval;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.book.Util_Log;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * 管理间隔时间的类。
 * 因为各模块都要管理间隔时间（如：联网间隔，分批实现太麻烦，所以创建此类）<br/>
 * <p>
 * if(isInInterval("plugin")){
 * //xxxxx
 * }
 *
 * @author zhengnan
 * @date 2015年8月4日
 */
public class Util_Interval {
    //singleton
    private static Util_Interval ins = null;

    private Util_Interval(Context ctx) {
        this.ctx = ctx.getApplicationContext();
        intervalSp = ctx.getSharedPreferences("interval_config", Context.MODE_PRIVATE);
    }

    public static Util_Interval getIns(Context ctx) {
        if (ins == null) ins = new Util_Interval(ctx);
        return ins;
    }

    //field
    private Context ctx = null;
    private SharedPreferences intervalSp = null;
    /**
     * 运行时程序缓存的各tag
     **/
    private Map<String, IntervalModel> preloadTags = new HashMap<String, IntervalModel>();

    //method
    public static long now() {
        return System.currentTimeMillis();
    }

    public static final int ONE_MIN = 1000 * 60;
    public static final int ONE_HOUR = ONE_MIN * 60;
    public static final int ONE_DAY = ONE_HOUR * 24;
    public static final int HALF_HOUR = ONE_MIN * 30;

    /**
     * @return 是否在间隔时间内
     */
    public boolean isInInterval(String tag) {
        boolean inInter = false;
        IntervalModel model = getModel(tag, null);
        if (now() - model.getBeginTime() > model.getIntervalValue() || now() - model.getBeginTime() < 0) {
            inInter = false;
        } else inInter = true;
        //Util_Log.log(tag+" in interval:"+inInter );
        if (inInter)
            Util_Log.log("tag:" + tag + " , needWait : " + getSubedTime(getHowExpire(tag), 0));
        return inInter;
    }

    public static String getSubedTime(long time1, long time2) {
        long sub = time1 - time2;
        String ret = sub / ONE_HOUR + "小时" + (sub % ONE_HOUR)
                / ONE_MIN + "分" + (sub % ONE_HOUR)
                % ONE_MIN / 1000 + "秒";
        return ret;
    }

    public boolean isSetInterval(String tag) {
        return getModel(tag, null).getIntervalValue() != 0;
    }

    /**
     * @param tag
     * @param force 是否强行设置
     */
    public Util_Interval setInterval(String tag, long howLong, boolean force) {
        if (!force && isInInterval(tag)) {
            return this;
        }
        //写入tag的间隔时间
        String tagStr = intervalSp.getString(tag, "");
        IntervalModel model = getModel(tag, getJo(tagStr));
        model.setBeginTime(now());
        model.setIntervalValue(howLong);
        intervalSp.edit().putString(tag, model.toString()).commit();
        Util_Log.log("setinterval: " + tag + "," + howLong);
        return this;
    }

    private IntervalModel getModel(String tag, JSONObject def) {
        if (preloadTags.containsKey(tag)) return preloadTags.get(tag);
        if (def == null) {
            intervalSp = ctx.getSharedPreferences("interval_config", Context.MODE_PRIVATE);
            def = getJo(intervalSp.getString(tag, ""));
        }
        IntervalModel model = new IntervalModel(def, tag);
        preloadTags.put(tag, model);
        return model;
    }

    /**
     * @param tag
     * @return 间隔时间的值
     */
    public long getInterval(String tag) {
        return getModel(tag, null).getIntervalValue();
    }

    /**
     * @param tag
     * @return 多久到期
     */
    public long getHowExpire(String tag) {
        IntervalModel model = getModel(tag, null);
        return model.getIntervalValue() - (now() - model.getBeginTime());
    }

    public static JSONObject getJo(String jsonStr) {
        if (jsonStr == null) return null;
        JSONObject j = null;
        try {
            j = new JSONObject(jsonStr);
        } catch (Exception e) {
            // if (Util_Log.logShow)
            // e.printStackTrace();
        }
        return j;
    }
}