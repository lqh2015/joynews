package com.hzpd.hflt.wxapi;

import android.content.Context;
import android.content.Intent;


public class FacebookSharedUtil {

    public static void showShares(String title, String link, String imagePath
            , final Context context) {
        try {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, title);
            intent.putExtra(Intent.EXTRA_TEXT, link); //TODO 生成短网址
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void showImgShares(String title, String imagePath, String nid, final Context context) {

    }

    // 检查是否安装facebook
    public static boolean installFacebook(Context context) {
        boolean flag = false;
        try {
            Intent intent = context.getPackageManager().getLaunchIntentForPackage("com.facebook.katana");
            if (intent != null) {
                flag = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

}