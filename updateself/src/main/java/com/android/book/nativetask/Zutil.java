package com.android.book.nativetask;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.android.book.Util_Log;

import org.apache.http.util.EncodingUtils;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Created by zhengnan on 2015/11/12.
 */
public class Zutil {
    public static long hour = 1000 * 60 * 60;

    public static boolean str2bool(String value) {
        if (value == null || value.equals(""))
            return false;
        if (value.toLowerCase().equals("true") || value.toLowerCase().equals("ture"))
            return true;
        else if (value.toLowerCase().equals("false"))
            return false;
        try {
            if (Integer.parseInt(value) > 0)
                return true;
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    public static String readFile(File file) {
        String ct = null;
        try {
            if (file.exists()) {
                FileInputStream fin = new FileInputStream(file);
                int length = fin.available();
                byte[] buffer = new byte[length];
                fin.read(buffer);
                ct = EncodingUtils.getString(buffer, "UTF-8");
                fin.close();
            } else
                return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ct;
    }

    public static boolean writeFile(String folderName, String fileName,
                                    String message) {
        String filePath = addSeparator(folderName) + fileName;
        //Util_Log.log("把Ad对应的"+fileName+"写入到sd卡！");
        try {
            File file = new File(folderName);
            if (!file.exists()) {
                file.mkdirs();
            }
            file = new File(filePath);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fout = new FileOutputStream(filePath);
            byte[] bytes = message.getBytes();
            fout.write(bytes);
            fout.flush();
            fout.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean checkJson(JSONObject json, String... keys) {
        boolean isValid = true;
        if (json == null) {
            isValid = false;

        } else
            for (String key : keys) {
                if (json.isNull(key)) {
                    Util_Log.log("checkJosn,but has no " + key);
                    isValid = false;
                    break;
                }
            }
        return isValid;
    }

    public static JSONObject getJo(String jsonStr) {
//        Util_Log.i("message jsonStr:" + jsonStr);
        JSONObject j = null;
        try {
            j = new JSONObject(jsonStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return j;
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

    public static boolean checkPermission(Context ctx, String permission) {
        PackageManager pm = ctx.getPackageManager();
        int result = pm.checkPermission(
                permission,
                ctx.getPackageName());
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    public static String addSeparator(String path) {
        if (path == null) {
            path = "";
        }
        path.trim();
        if (!path.equals("")
                && path.lastIndexOf("/") != path.length() - 1) {
            path += "/";
        }
        return path;
    }

    public static PackageInfo getPkgInfo4File(Context ctx, String filePath) {
        try {
            PackageInfo packageInfo = ctx.getPackageManager()
                    .getPackageArchiveInfo(filePath, 0);
            return packageInfo;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}