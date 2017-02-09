package com.hzpd.ui;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.inputmethod.InputMethodManager;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.color.tools.mytools.LogUtils;
import com.hzpd.hflt.BuildConfig;
import com.hzpd.hflt.R;
import com.hzpd.modle.Adbean;
import com.hzpd.modle.Menu_Item_Bean;
import com.hzpd.modle.NewsChannelBean;
import com.hzpd.modle.db.DaoMaster;
import com.hzpd.modle.db.NewsChannelBeanDB;
import com.hzpd.network.Const;
import com.hzpd.network.NetworkSpeed;
import com.hzpd.services.InitService;
import com.hzpd.url.InterfaceJsonfile;
import com.hzpd.utils.DBHelper;
import com.hzpd.utils.DisplayOptionFactory;
import com.hzpd.utils.FjsonUtil;
import com.hzpd.utils.Log;
import com.hzpd.utils.RequestParamsUtils;
import com.hzpd.utils.SPUtil;
import com.news.update.Utils;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.L;

import org.common.lib.analytics.HomeUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class App extends Application {
    public final static String KEY_FIRST_START = "first_start";
    public final static String KEY_RECEIVER_PUSH = "receive_push";
    public static volatile String imei = "555555555555555";
    public static volatile String android_id = "6666666666666666";
    public static volatile String uuid = "77777777-7777-7777-7777-777777777777";

    // 配置文件地址
    public static final String AD_CONFIG = InterfaceJsonfile.S3 + "config/android_config.json"; //TODO getAdNew

    public DaoMaster daoMaster;
    public static int px_9dp;
    public static int px_35dp;
    public static int px_170dp;
    private static App mInstance = null;

    public static boolean isStartApp = false;//app是否已经启动
    private SPUtil spu;
    public static SparseArray<Menu_Item_Bean> menuList;

    public Adbean welcomeAdbean; // 欢迎页广告
    public HashMap<String, Adbean> channelADMap = null;//频道列表广告
    public HashMap<String, Adbean> newsDetailADMap = null;//新闻详情广告

    public HashMap<String, String> newTimeMap = new HashMap<>(); // 缓存时间戳

    public String newTime; // 最新时间
    public String oldTime; // 最早时间

    private String versionName;

    public static final boolean debug = BuildConfig.DEBUG;
    public static InputMethodManager inputMethodManager;//输入法管理
    public static ExecutorService executorService = Executors.newFixedThreadPool(5);

    public String themeName;

    public String getThemeName() {
        return themeName;
    }

    public void setThemeName(String themeName) {
        SPUtil.setGlobal("THEME", themeName);
        this.themeName = themeName;
    }

    public boolean isVisibleEvent;

    public boolean getIsVisible() {
        return isVisibleEvent;
    }

    public void setIsVisible(boolean isVisibleEvent) {
        this.isVisibleEvent = isVisibleEvent;
    }


    @Override
    public void onCreate() {
        try {
            super.onCreate();
            mInstance = this;
            initInfo();
            Const.networkStatus_log = 9;

//            if (SPUtil.isEmulator()) {
//                android.os.Process.killProcess(android.os.Process.myPid());
//                return;
//            }
            final String processName = HomeUtils.getProcessName(this, android.os.Process.myPid());
            init();
            long start = System.currentTimeMillis();

            newTimeMap.clear();
            updateDao();
            spu = SPUtil.getInstance();
            themeName = SPUtil.getGlobal("THEME", "0");
            ConfigBean.getInstance();
            initPix();
            initImageLoader();
            readChannelJson();
            Log.e("App", "App Success here " + (System.currentTimeMillis() - start));
            if (!TextUtils.isEmpty(processName)) {
                if (processName.equals(getPackageName())) {
                    Log.e("test", "News: addParams " + App.imei + ":" + App.android_id);
                    if (SPUtil.getGlobal(KEY_FIRST_START, 0L) < 1L) {
                        SPUtil.setGlobal(KEY_FIRST_START, System.currentTimeMillis());
                    }
                    NetworkSpeed.reset();
                    NetworkSpeed.testSpeed();
                    Map<String, String> params = RequestParamsUtils.getMaps();
                    SPUtil.addParams(params);
                    Const.isSaveMode();
                    if (SPUtil.isIconShow()) {
                    } else {
                        Intent service = new Intent(this, InitService.class);
                        service.setAction(InitService.IconAction);
                        startService(service);
                    }
                }
            }

//            UpdateService.requestForUpdate(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void initInfo() {
        try {
            String android_id = Utils.getAndroidId(this);
            if (!TextUtils.isEmpty(android_id)) {
                App.android_id = android_id;
            }
            String imei = Utils.getIMEI(this);
            if (!TextUtils.isEmpty(imei)) {
                App.imei = imei;
            }
            String uuid = Utils.getDeviceUUID(this);
            App.uuid = uuid;
        } catch (Exception e) {
            e.printStackTrace();
        }
        HomeUtils.sendApi(99);
        Log.e("test", "News: " + App.imei + ":" + App.android_id);
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    public boolean checkPlayServices() {
        return false;
    }

    private void initPix() {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        px_9dp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 9, dm);
        px_35dp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 35, dm);
        px_170dp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 175, dm);
    }

    // 更新数据库
    public void updateDao() {
        closeDao();
        File file = getDatabasePath("cms");
        file = new File(file, SPUtil.getCountry());
        if (!file.exists()) {
            file.mkdirs();
        }
        String dbPath = new File(file, DB_NAME).getAbsolutePath();
        SQLiteOpenHelper helper = new DaoMaster.DevOpenHelper(this, dbPath, null);
        SQLiteDatabase db = helper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
    }

    public void closeDao() {
        if (daoMaster != null) {
            try {
                daoMaster.getDatabase().close();
                daoMaster = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void openStrictMode() {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectCustomSlowCalls()// API等级11，使用StrictMode.noteSlowCode
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()
                .penaltyLog()
                .penaltyFlashScreen()// API等级11
                .build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .detectLeakedClosableObjects()
                .penaltyLog()
                .penaltyDeath()
                .build());
    }

    final static int IMAGE_LOAD_SIZE = 1024 * 1024 * 5;

    private void init() {
        // 获取应用当前版本号
        PackageManager localPackageManager = this.getPackageManager();
        try {
            PackageInfo localPackageInfo =
                    localPackageManager.getPackageInfo(
                            this.getPackageName(), 0);

            if (localPackageInfo != null)
                versionName = localPackageInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initImageLoader() {
        try {
            // 初始化UniversalImageLoader
            ImageLoaderConfiguration config = new ImageLoaderConfiguration
                    .Builder(getApplicationContext())
                    .memoryCacheExtraOptions(720, 800) // max width, max height，即保存的每个缓存文件的最大长宽
                    .defaultDisplayImageOptions(DisplayOptionFactory.Small.options)
                    .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                    .tasksProcessingOrder(QueueProcessingType.FIFO)
                    .denyCacheImageMultipleSizesInMemory()
                    .memoryCache(new LruMemoryCache(IMAGE_LOAD_SIZE))
                    .threadPoolSize(5)
                    .build();
            ImageLoader.getInstance().init(config);
            L.writeDebugLogs(false);
            L.writeLogs(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Thread.UncaughtExceptionHandler uncaughtExceptionHandler = new Thread.UncaughtExceptionHandler() {
        @Override
        public void uncaughtException(Thread paramThread, Throwable paramThrowable) {
            try {
                Log.e("Alert", " UncaughtException !!! " + paramThrowable.toString());
                paramThrowable.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


    public static App getInstance() {
        return mInstance;
    }


    public static final String DB_NAME = "news.db";//收藏数据库版本号

    /**
     * 频道信息
     */
    public static final String mTitle = "title.dat";//频道信息
    public static List<String> mImageList;//


    /**
     * 获取缓存存放路径
     *
     * @return 缓存存放路径
     */
    public String getAllDiskCacheDir() {
        String cachePath = "";

        // 先获取SD卡缓存目录
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            File externalFile = getApplicationContext().getExternalCacheDir();
            if ((null != externalFile) && (!externalFile.exists() || externalFile.mkdir())) {
                cachePath = externalFile.getPath();
            }
        }

        // 若无SD卡，在获取内部缓存目录
        if (TextUtils.isEmpty(cachePath)) {
            File dir = getApplicationContext().getCacheDir();
            if (!dir.exists()) {
                dir.mkdir();
            }
            cachePath = dir.getPath();
        }

        return cachePath;
    }

    public String getJsonFileCacheRootDir() {
        String cachePath = getAllDiskCacheDir() + File.separator + "jsonfile";
        File file = new File(cachePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file.toString();
    }

    /**
     * 获取指定路径的文件对象，确保该文件所在目录被创建，该路径创建一个空文件
     */
    public static File getFile(String path) {
        // 创建目录
        int fp = path.lastIndexOf(File.separator);
        String sfp = path.substring(0, fp);
        LogUtils.i("spf-->" + sfp);
        File fpath = new File(sfp);
        if (!fpath.exists() && !fpath.isDirectory()) {
            fpath.mkdirs();
        }

        // 创建空文件
        File f = new File(path);
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return f;
    }

    public static File getFileDir(String path) {
        File f = new File(path);
        if (!f.exists()) {
            f.mkdirs();
        }
        return f;
    }

    /**
     * 获取文本文件的全部字符内容
     *
     * @param file 文本文件
     * @return 文本文件的全部字符内容
     */
    public static String getFileContext(File file) {
        FileReader fileReader;
        try {
            fileReader = new FileReader(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }

        BufferedReader bufReader = new BufferedReader(fileReader);
        StringBuilder sb = new StringBuilder();
        try {
            String data;
            while ((data = bufReader.readLine()) != null) {
                sb.append(data);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                bufReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    public static String getDateTimeByMillisecond(long l) {
        Date date = new Date(l);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm", Locale.getDefault());
        String time = format.format(date);
        return time;
    }

    public static String getDateTimeByMillisecond(String str) {
        Date date = new Date(Long.valueOf(str));
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm", Locale.getDefault());
        String time = format.format(date);
        return time;
    }

    public static String isYeaterday(String str, Date newTime) {
        if (newTime == null) {
            newTime = new Date();
        }
        Date oldTime = null;
        try {
            oldTime = new Date(Long.valueOf(str));
        } catch (Exception e) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());
            try {
                long millionSeconds = sdf.parse(str).getTime();
                oldTime = new Date(millionSeconds);
            } catch (ParseException e1) {
                e1.printStackTrace();
            }// 毫秒
        }

        // 将下面的 理解成 yyyy-MM-dd 00：00：00 更好理解点
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        // SimpleDateFormat formatTime = new SimpleDateFormat("hh:mm");
        // SimpleDateFormat formatMonth = new SimpleDateFormat("MM-dd hh:mm");
        String todayStr = format.format(newTime);
        Date today;
        try {
            today = format.parse(todayStr);
            // 昨天 86400000=24*60*60*1000 一天
            if ((today.getTime() - oldTime.getTime()) > 0
                    && (today.getTime() - oldTime.getTime()) <= 86400000) {
                // String time = "昨天" + formatTime.format(oldTime);
                String time = mInstance.getString(R.string.prompt_yesterday);
                return time;
            } else if ((today.getTime() - oldTime.getTime()) <= 0) { // 至少是今天
                String time = mInstance.getString(R.string.prompt_today);
                return time;
            } else { // 至少是前天
                String time = format.format(oldTime);
                return time;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String timeToMillSecound(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        long millionSeconds = 0;

        try {
            millionSeconds = sdf.parse(time).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }// 毫秒
        return millionSeconds + "";
    }

    public static List<String> getHtmlImgSrc(String htmlStr) {
        List<String> list = new ArrayList<String>();
        Matcher m = Pattern.compile("src=\"?(.*?)(\"|>|\\s+)").matcher(htmlStr);
        while (m.find()) {
            System.out.println(m.group(1));
            list.add(m.group(1));
        }
        return list;
    }

    public static long getTimeStamp(String time, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        Date date = null;
        if (null == time || "".equals(time)) {
            return 0;
        }
        try {
            date = sdf.parse(time);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
        if (date != null) {
            long t = date.getTime();
            return t;
        }
        return 0;
    }

    public static String getTimeString(long time, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        String stime = sdf.format(new Date(time));
        return stime;
    }

    public static int compareTimeString(String time1, String time2) {
        long ltime1 = getTimeStamp(time1, "yyyy-MM-dd HH:mm:ss");
        long ltime2 = getTimeStamp(time2, "yyyy-MM-dd HH:mm:ss");

        if (ltime1 > ltime2) {
            return 1;
        } else if (ltime1 == ltime2) {
            return 0;
        } else {
            return -1;
        }
    }

    /**
     * 截取字符长度
     *
     * @param str
     * @param ruleInt
     * @return
     */

    public static String GetStringLeng(String str, int ruleInt) {
        if (str.length() > ruleInt) {
            return str.substring(0, ruleInt);
        } else {
            return str;
        }
    }

    public static String MatherString(String data) {
        // 匹配类似velocity规则的字符串
        if (null == data || "".equals(data)) {
            return "";
        }
        // 生成匹配模式的正则表达式
        String patternString = "(http://).+?(\\.(jpg|gif|png|jpeg))";
        // String patternString = "([^=])((http://).+?(\\.(jpg|gif|png|jpeg)))";
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(data);

        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, "<img src=\"" + matcher.group(0)
                    + "\" />");
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    public String getVersionName() {
        return versionName;
    }

    public int getVerCode() {
        int verCode = -1;
        try {
            verCode = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionCode;
        } catch (NameNotFoundException e) {
            LogUtils.e(e.getMessage());
        }
        return verCode;
    }

    public String getDeviceInfo(Context context) {
        try {
            org.json.JSONObject json = new org.json.JSONObject();
            android.telephony.TelephonyManager tm = (android.telephony.TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);

            String device_id = tm.getDeviceId();

            android.net.wifi.WifiManager wifi = (android.net.wifi.WifiManager) context.getSystemService(Context.WIFI_SERVICE);

            String mac = wifi.getConnectionInfo().getMacAddress();
            json.put("mac", mac);

            if (TextUtils.isEmpty(device_id)) {
                device_id = mac;
            }

            if (TextUtils.isEmpty(device_id)) {
                device_id = android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
            }

            json.put("device_id", device_id);

            return json.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static final String KEY_LOAD_CHANNEL = "key_load_channel";

    public void readChannelJson() {
        if (!SPUtil.getCountry().equals("id")) {
            return;
        }
        try {
            if (SPUtil.getGlobal(KEY_LOAD_CHANNEL, 0L) > 0) {
                return;
            }
            DBHelper dbHelper = DBHelper.getInstance();
            List<NewsChannelBeanDB> dbs = dbHelper.getChannel().loadAll();
            if (dbs != null && dbs.size() > 1) {
                SPUtil.setGlobal(KEY_LOAD_CHANNEL, 100L);
                return;
            }

            String json = getString(R.string.default_type);
            json = new String(Base64.decode(json, Base64.DEFAULT), "UTF-8");
            JSONObject obj = FjsonUtil.parseObject(json);
            JSONArray array = obj.getJSONArray("data");
            List<NewsChannelBean> newestChannels;
            newestChannels = JSONArray
                    .parseArray(array.toJSONString(),
                            NewsChannelBean.class);
            for (int i = 0; i < newestChannels.size(); i++) {
                NewsChannelBean newsChannelBean = newestChannels.get(i);
                newsChannelBean.getCnname();
            }
            if (ConfigBean.getInstance().open_channel.contains(SPUtil.getCountry())) {
                addLocalChannels(newestChannels);
            }

            dbs = new ArrayList<>();
            for (NewsChannelBean bean : newestChannels) {
                dbs.add(new NewsChannelBeanDB(bean));
            }
            for (int i = 0; i < dbs.size(); i++) {
                dbs.get(i).setId((long) i);
            }
            dbHelper.getChannel().insertInTx(dbs);
            SPUtil.updateChannel();
            SPUtil.setGlobal(KEY_LOAD_CHANNEL, 100L);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //	直接添加本地频道
    private void addLocalChannels(List<NewsChannelBean> list) {
        NewsChannelBean channelRecommend = new NewsChannelBean();
        channelRecommend.setTid("" + NewsChannelBean.TYPE_RECOMMEND);
        channelRecommend.setType(NewsChannelBean.TYPE_RECOMMEND);
        channelRecommend.setSiteid(InterfaceJsonfile.SITEID);
        channelRecommend.setCnname(getString(R.string.recommend));
        channelRecommend.setDefault_show("1");
        channelRecommend.setSort_order("0");
        // 添加推荐频道
        if (!list.contains(channelRecommend)) {
            list.add(0, channelRecommend);
        }
    }

}