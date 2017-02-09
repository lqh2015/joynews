package com.hzpd.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;

import com.hzpd.modle.CacheBean;
import com.hzpd.ui.App;
import com.hzpd.ui.interfaces.I_Result;
import com.hzpd.utils.db.NewsListDbTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 本应用数据清除管理器
 */
public class DataCleanManager {

    /**
     * 清除本应用内部缓存(/data/data/com.xxx.xxx/cache)
     *
     * @param context
     */
    public static void cleanInternalCache(Context context) {
        deleteDir(context.getCacheDir());
    }

    /**
     * 清除本应用所有数据库(/data/data/com.xxx.xxx/databases)
     *
     * @param context
     */
    public static void cleanDatabases(Context context) {
        deleteDir(new File("/data/data/"
                + context.getPackageName() + "/databases"));
    }

    /**
     * * 清除本应用SharedPreference(/data/data/com.xxx.xxx/shared_prefs)
     *
     * @param context
     */
    public static void cleanSharedPreference(Context context) {
        deleteDir(new File("/data/data/"
                + context.getPackageName() + "/shared_prefs"));
    }

    /**
     * 按名字清除本应用数据库 * * @param context * @param dbName
     */
    public static void cleanDatabaseByName(Context context, String dbName) {
        context.deleteDatabase(dbName);
    }

    /**
     * 清除/data/data/com.xxx.xxx/files下的内容 * * @param context
     */
    public static void cleanFiles(Context context) {
        deleteDir(context.getFilesDir());
    }

    /**
     * * 清除外部cache下的内容(/mnt/sdcard/android/data/com.xxx.xxx/cache) * * @param
     * context
     */
    public static void cleanExternalCache(Context context) {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            deleteDir(context.getExternalCacheDir());
        }
    }

    /**
     * 清除自定义路径下的文件，使用需小心，请不要误删。而且只支持目录下的文件删除 * * @param filePath
     */
    public static void cleanCustomCache(String filePath) {
        deleteDir(new File(filePath));
    }

    /**
     * 清除本应用所有的数据 * * @param context * @param filepath
     */
    public static void cleanApplicationData(Context context, String... filepath) {
        cleanInternalCache(context);
        cleanExternalCache(context);
        cleanDatabases(context);
//		cleanSharedPreference(context);
        cleanFiles(context);

        if (filepath != null) {
            for (String fp : filepath) {
                cleanCustomCache(fp);
            }
        }
    }


    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            //递归删除目录中的子目录下
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
    }


    public void deleteDb(List<CacheBean> list, Context context, I_Result i_Result) {
        DeleteDb deleteDb = new DeleteDb(list, context, i_Result);
        deleteDb.executeOnExecutor(App.executorService);
    }


    class DeleteDb extends AsyncTask<String, String, Boolean> {
        private List<CacheBean> list;
        private Context context;
        private I_Result i_Result;

        public DeleteDb(List<CacheBean> list, Context context, I_Result i_Result) {
            super();
            this.list = list;
            this.context = context;
            this.i_Result = i_Result;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            Log.e("test", "---------");
            if (null == list || list.size() < 1) {
                return true;
            }
            try {
                List<String> newsIds = new ArrayList<String>();
                List<String> albumIds = new ArrayList<String>();
                List<String> videoIds = new ArrayList<String>();
                for (CacheBean bean : list) {
                    if ("news".equals(bean.getModule())) {
                        newsIds.add(bean.getId());
                    }
                    if ("album".equals(bean.getModule())) {
                        albumIds.add(bean.getId());
                    }
                    if ("video".equals(bean.getModule())) {
                        videoIds.add(bean.getId());
                    }
                }
                //删除新闻
                if (null != newsIds && newsIds.size() > 0) {
                    NewsListDbTask newsListDbTask = new NewsListDbTask(context);
                    newsListDbTask.asyncDeleteList(newsIds);
                }
                //删除图集
                if (null != albumIds && albumIds.size() > 0) {
                    DBHelper.getInstance().getAlbumDBUitls().deleteAll();
                }
                //删除视频
                if (null != videoIds && videoIds.size() > 0) {
                    DBHelper.getInstance().getVideoDBUitls().deleteAll();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (null != i_Result) {
                i_Result.setResult(result);
            }
            Log.e("test", "---------");
        }

    }
}