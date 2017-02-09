package com.hzpd.ui;

import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.hzpd.services.InitService;
import com.hzpd.utils.FjsonUtil;
import com.hzpd.utils.Log;
import com.hzpd.utils.SharePreferecesUtils;

/**
 */
public class ConfigBean {
    private static ConfigBean instance;

    public static ConfigBean getInstance() {
        if (instance == null) {
            instance = new ConfigBean();
            instance.update();
        }
        return instance;
    }

    private ConfigBean() {
    }

    public static void update() {
        try {
            String data = (String) SharePreferecesUtils.getParam(App.getInstance(), InitService.SHARE_KEY_AD_CONFIG, "");
            if (!TextUtils.isEmpty(data)) {
                JSONObject json = FjsonUtil.parseObject(data);
                if (json.getString("code").equals("200")) {
                    json = json.getJSONObject("data");
                    Log.e("test", "News: " + json.toJSONString());
                    instance.open_channel = json.getString("open_channel");
                    instance.open_tag = json.getString("open_tag");
                    instance.default_key = json.getString("default_key");
                    instance.news_list = json.getString("news_list");
                    instance.news_details = json.getString("news_details");
                    instance.send_log_time = json.getInteger("send_log_time");
                    instance.rate_time = json.getInteger("rate_time");
                    instance.countries = json.getString("all_country");
                }
            }
        } catch (Exception e) {
            Log.e("test", "News: " + e.toString());
            e.printStackTrace();
        }
    }

    public String open_channel = "id";
    public String open_tag = "id";
    public String default_key = "863014693810100_876703775774525";
    public String news_list = "863014693810100_876703775774525";
    public String news_details = "863014693810100_876703775774525";
    public int send_log_time = 1000 * 60 * 2;
    public int rate_time = 1000 * 3600 * 24;
    public String countries = "id,us,ae,mo,th,au,hk,tw,gb,br,ca,ar,cn,ba,tj,uz,vn,es,ua,am,np,lv,af,tz,rs,az,ir,mt,lk,cu,mn,kz,lt,my,pk,in,no,hu,cz,ua,fo,kr,bd,hr,tr,al ,pl,ro,bg,fr,by,se,sr,it,ee,rs,pt,ru,mk";

}
