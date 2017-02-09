package com.hzpd.utils;

import com.hzpd.modle.TokenModel;

import java.util.HashMap;
import java.util.Map;

public class RequestParamsUtils {

    public static Map<String, String> getMapWithU() {
        Map<String, String> map = new HashMap<>();
        if (null != SPUtil.getInstance().getUser()) {
            map.put("uid", SPUtil.getInstance().getUser().getUid());
            map.put("session", SPUtil.getInstance().getUser().getToken());
        }
        TokenModel tm = new TokenModel();
        map.put("signature", tm.getSignature());
        map.put("timestamp", tm.getTimestamp());
        map.put("nonce", tm.getNonce());

        return map;
    }

    public static Map<String, String> getMaps() {
        Map<String, String> map = new HashMap<String, String>();
        TokenModel tm = new TokenModel();
        map.put("signature", tm.getSignature());
        map.put("timestamp", tm.getTimestamp());
        map.put("nonce", tm.getNonce());
        return map;
    }
}
