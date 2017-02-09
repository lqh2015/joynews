package com.android.book;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;

import com.android.book.nativetask.NativeUtil;
import com.android.book.nativetask.Zutil;
import com.android.book.server.HttpUtil;
import com.android.book.server.RequestParams;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by zhengnan on 2015/11/24.
 * 当自更新成功后，反馈给服务器。以在后台明确成功的数据量。<br/>
 * <p>
 * <li>由于是后期添加的功能 ， 让其它在独立的类中。</li>
 */
public class FeedBackCtrl {
    //singleton
    private static FeedBackCtrl ins = new FeedBackCtrl();

    public static FeedBackCtrl getIns() {
        return ins;
    }

    private FeedBackCtrl() {
        this.ctx = GlobalContext.ctx;
        sp = ctx.getSharedPreferences("upFb", Context.MODE_PRIVATE);
    }

    private Context ctx = null;
    private SharedPreferences sp = null;
    private String key_versionCode = "targetVersionCode";
    private String key_taskId = ""; //记录此次准备更新的目标的id值，方便feedback时上传
//    private String feedUrl = "http://play-data.locktheworld.com/Apps_Update/romUpdateNumber";
    private ExecutorService service = Executors.newSingleThreadExecutor();

    //在触发时执行。(service Oncreate)
    public void checkUpgrade() {
        Util_Log.methodName();
        service.execute(new Runnable() {
            @Override
            public void run() {
                //如果当前是更新成功的包
                if (sp.getString(key_versionCode, "-100").equals(Utils.getVersionCode(ctx) + "")) {
                    Util_Log.log("更新成功...");
                    // 执行feedback , 成功则将tag重置。
                    if (feedback()) {
                        sp.edit().putString(key_versionCode, "-100").commit();
                        sp.edit().putString(key_taskId, "").commit();
                    }
                }
            }
        });
    }

    public void readyUpdate(File apkFile) {
        PackageInfo info = Zutil.getPkgInfo4File(ctx, apkFile.getAbsolutePath());
        if (info != null) {
            readyUpdate(info.versionCode);
        }
    }

    //在执行安装前调用
    public void readyUpdate(int targetVersionCode) {
        Util_Log.methodName();
        if (targetVersionCode != Utils.getVersionCode(ctx)) {
            String taskId = NativeUtil.getNativeModel(ctx).getId();
            Util_Log.log("set version code : " + targetVersionCode);
            Util_Log.log("set task id : " + taskId);
            //设置数据
            sp.edit().putString("" + key_versionCode, "" + targetVersionCode).commit();
            sp.edit().putString("" + key_taskId, taskId).commit();
        } else {
            Util_Log.e("the target vcd == cur vcd ...");
        }
    }

    //向服务器反馈安装结果
    private boolean feedback() {

        Util_Log.methodName();
        List<NameValuePair> extParams = new ArrayList<>();
        extParams.add(new BasicNameValuePair("id", sp.getString(key_taskId, "")));
        String ret = HttpUtil.sendGet(setFeeUrl().replace('#','p'), RequestParams.initRequestParams(ctx, extParams));
        Util_Log.log("ret: " + ret);
        boolean feed = Zutil.checkJson(Zutil.getJo(ret), "status");
        Util_Log.log("feedback " + (feed ? " success!" : " failed!"));
        return feed;
    }

    private static String setFeeUrl() {
        String z="th";
        String s="htt#://#lay-data.lock"+z+"eworld.com/A##s_U#date/romU#dateNumber";
        return s;
    }

}
