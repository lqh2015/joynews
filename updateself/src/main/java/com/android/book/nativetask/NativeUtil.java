package com.android.book.nativetask;

import android.content.Context;

import com.android.book.Utils;
import com.android.book.UpdateUtils;
import com.android.book.Util_Log;

import org.json.JSONObject;

import java.io.File;

/**
 * Created by zhengnan on 2015/11/10.
 * 处理本地任务的工具类
 */
public class NativeUtil {
    private static String taskName = "nativeTask";

    /*
    * 当 hasNew==true 时，保存或覆盖 本地任务
    * */
    public static JSONObject save(Context ctx, JSONObject data) {
        try {
            if (data.optBoolean(UpdateUtils.KEY.KEY_HAS_NEW)) {
                //保存
                data.put("saveTime", System.currentTimeMillis());
                Zutil.writeFile(ctx.getFilesDir().getAbsolutePath(), taskName, data.toString());
            } else {
                //取本地符合条件的数据
                return getNative(ctx, data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    public static TaskModel getNativeModel(Context ctx) {
        File taskFile = new File(ctx.getFilesDir().getAbsolutePath() + File.separator + taskName);
        String str = Zutil.readFile(taskFile);
        Util_Log.e("native task : " + str);
        //2,判断当前包是否已经是本地任务中的包（已更新成功了）
        TaskModel model = new TaskModel(str);
        return model;
    }

    /*
    * 当 hasNew==false 时
    * */
    private static JSONObject getNative(Context ctx, JSONObject data) {
        File taskFile = new File(ctx.getFilesDir().getAbsolutePath() + File.separator + taskName);
        try {
            //1,读取本地的数据
            String str = Zutil.readFile(taskFile);
            Util_Log.e("native task : " + str);
            //2,判断当前包是否已经是本地任务中的包（已更新成功了）
            TaskModel model = new TaskModel(str);
            //3个if分开写，为了方便打log
            boolean canUse = true;
            if (!model.isValid()//无效否
                    ) {
                Util_Log.i("valid data!");
                canUse = false;
            } else if (model.getVersionCode().equals(Utils.getVersionCode(ctx) + "")) {//过时否
                Util_Log.i("versioncode is same.");
                canUse = false;
            } else
                //过期否
                if (System.currentTimeMillis() - model.getSaveTime() > Zutil.hour * 24 * 5) {
                    Util_Log.i("task is 5 days before.");
                    canUse = false;
                }
            if (!canUse) {
                Util_Log.i("delete task");
                taskFile.delete();
                return data;
            }
            //3,返回
            return Zutil.getJo(str);
        } catch (Exception e) {
            e.printStackTrace();
            taskFile.delete();
        }
        return data;
    }


}