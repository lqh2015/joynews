package com.hzpd.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.VideoView;

import com.alibaba.fastjson.JSONObject;
import com.color.tools.mytools.LogUtils;
import com.hzpd.hflt.R;
import com.hzpd.hflt.wxapi.FacebookSharedUtil;
import com.hzpd.modle.CommentsCountBean;
import com.hzpd.modle.NewsBean;
import com.hzpd.modle.ReplayBean;
import com.hzpd.modle.VideoDetailBean;
import com.hzpd.modle.VideoItemBean;
import com.hzpd.modle.db.NewsItemBeanForCollection;
import com.hzpd.modle.db.NewsItemBeanForCollectionDao;
import com.hzpd.modle.db.NewsJumpBean;
import com.hzpd.modle.db.NewsJumpBeanDao;
import com.hzpd.ui.App;
import com.hzpd.url.InterfaceJsonfile;
import com.hzpd.url.OkHttpClientManager;
import com.hzpd.utils.AAnim;
import com.hzpd.utils.FjsonUtil;
import com.hzpd.utils.GetFileSizeUtil;
import com.hzpd.utils.Log;
import com.hzpd.utils.MyCommonUtil;
import com.hzpd.utils.RequestParamsUtils;
import com.hzpd.utils.SPUtil;
import com.hzpd.utils.TUtils;
import com.squareup.okhttp.Request;

import java.io.File;
import java.util.HashMap;
import java.util.Map;


public class VideoPlayerActivity extends MBaseActivity implements MediaPlayer.OnPreparedListener, OnClickListener {

    private static final String BASEURL = InterfaceJsonfile.PATH_ROOT + "/Public/videoview/id/";
    @Override
    protected int getOnResumeApi() {
        return 138;
    }

    @Override
    protected int getOnStopApi() {
        return 139;
    }

    private VideoView mVideoView;
    private View mVolumeBrightnessLayout;
    private ImageView mOperationBg;
    private ImageView mOperationPercent;
    private AudioManager mAudioManager;
    private int mMaxVolume;// 最大声音
    private int mVolume = -1;// 当前声音
    private float mBrightness = -1f;// 当前亮度
    private GestureDetector mGestureDetector;
    private View mLoadingView;
    private TextView video_loading_tv;
    private TextView video_loading_tv2;
    private LinearLayout videodetails_title_comment;// 评论
    private TextView videodetails_title_num;// 评论数量
    private LinearLayout videodetails_title_pl;// 收藏 评论 分享


    /**
     * 是否需要自动恢复播放，用于自动暂停，自动恢复播放
     */
    private boolean needResume = true;
    ;// 自动播放

    private VideoItemBean vib;
    private VideoDetailBean vdib;
    private String mPath;
    private String mTitle;
    private String videoPath;
    private View videoview_back;
    private String from;// newsitem newsdetail videofragment collection
    private boolean isCollected;

    private Object tag;

    @Override
    public void onCreate(Bundle bundle) {
        try {
            super.onCreate(bundle);
            setContentView(R.layout.video_view);
            initView();
            tag = OkHttpClientManager.getTag();
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            // -------初始化播放器--------------
            // ~~~ 绑定事件

            // ~~~ 绑定数据
            mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            mMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

            // 设置显示名称
            mVideoView.setMediaController(new MediaController(this));
            mVideoView.requestFocus();
            mVideoView.setOnPreparedListener(this);
            mVideoView.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (isPlaying()) {
                        stopPlayer();
                    } else {
                        startPlayer();
                    }
                    return false;
                }
            });


            if (true) {
                mVideoView.setVideoURI(Uri.parse("http://7xkbzx.com1.z0.glb.clouddn.com/SampleVideo_720x480_10mb.mp4"));
                return;
            }
            // ~~~ 获取播放地址和标题
            Intent intent = getIntent();
            String action = intent.getAction();

            String vid = null;
            if (null != action && Intent.ACTION_VIEW.equals(action)) {
                Uri uri = intent.getData();
                if (uri != null) {
                    vid = uri.getPath();
                    vid = vid.replace(File.separator, "");
                    from = "browser";
                }
            } else {
                from = intent.getStringExtra("from");
            }
            NewsBean nb = (NewsBean) intent.getSerializableExtra("newbean");
            if ("newsitem".equals(from)) {
                vib = new VideoItemBean();
                vib.setMainpic(nb.getImgs()[0]);
                vib.setTitle(nb.getTitle());
                vib.setTime(nb.getUpdate_time());
                getVideo_ni(nb.getJson_url());
            } else if ("browser".equals(from)) {
                videoPath = App.getInstance().getJsonFileCacheRootDir() + File.separator + "video";
                getVideoItemBean(vid);
            }
            // ---------------
            videodetails_title_num.setText(String.valueOf(nb.getComcount()));
            isCollection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        mVideoView = (VideoView) findViewById(R.id.surface_view);
        mVolumeBrightnessLayout = findViewById(R.id.operation_volume_brightness);
        mOperationBg = (ImageView) findViewById(R.id.operation_bg);
        mOperationPercent = (ImageView) findViewById(R.id.operation_percent);
        mLoadingView = findViewById(R.id.video_loading);
        video_loading_tv = (TextView) findViewById(R.id.video_loading_tv);
        video_loading_tv2 = (TextView) findViewById(R.id.video_loading_tv2);
        videodetails_title_comment = (LinearLayout) findViewById(R.id.videodetails_title_comment);
        videodetails_title_comment.setOnClickListener(this);
        videodetails_title_num = (TextView) findViewById(R.id.videodetails_title_num);
        videodetails_title_pl = (LinearLayout) findViewById(R.id.videodetails_title_pl);
        videodetails_title_pl.setOnClickListener(this);
        videoview_back = findViewById(R.id.videoview_back);
        videoview_back.setOnClickListener(this);
    }

    // 来自浏览器
    private void getVideoItemBean(String vid) {
        Map<String, String> params = RequestParamsUtils.getMaps();
        params.put("vid", vid);
        SPUtil.addParams(params);
        OkHttpClientManager.postAsyn(tag, InterfaceJsonfile.videoItem, new OkHttpClientManager.ResultCallback() {
            @Override
            public void onSuccess(Object response) {
                try {
                    String json = response.toString();
                    JSONObject obj = FjsonUtil.parseObject(response.toString());
                    if (null != obj) {
                        if (200 == obj.getIntValue("code")) {
                            vib = FjsonUtil.parseObject(obj.getString("data"), VideoItemBean.class);
                            getData();
                        } else {
                            TUtils.toast(getString(R.string.toast_cannot_connect_network));
                        }
                    } else {
                        TUtils.toast(getString(R.string.toast_cannot_connect_network));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Request request, Exception e) {
                TUtils.toast(getString(R.string.toast_server_no_response));
            }
        }, params);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.videodetails_title_pl:
                popUpwindow();
                break;
            case R.id.videodetails_title_comment:
                // 跳转到评论页
                if (!MyCommonUtil.isNetworkConnected(this)) {
                    TUtils.toast(getString(R.string.toast_check_network));
                    return;
                }
                if (null == vib) {
                    return;
                }
                break;
            case R.id.videoview_back:
                finish();
                break;

        }
    }

    // 加入收藏
    private void addCollection() {
        if (null == spu.getUser()) {
            NewsItemBeanForCollection nibfc = new NewsItemBeanForCollection(vib);
            try {
                NewsItemBeanForCollection mnbean = dbHelper.getCollectionDBUitls()
                        .queryBuilder().where(NewsItemBeanForCollectionDao.Properties.Nid.eq(vib.getVid()))
                        .build().unique();
                if (mnbean == null) {
                    dbHelper.getCollectionDBUitls().insert(nibfc);
                    TUtils.toast(getString(R.string.toast_collect_success));
                    isCollected = true;

                } else {
                    dbHelper.getCollectionDBUitls()
                            .queryBuilder().where(NewsItemBeanForCollectionDao.Properties.Nid.eq(vib.getVid()))
                            .buildDelete().executeDeleteWithoutDetachingEntities();
                    TUtils.toast(getString(R.string.toast_collect_cancelled));
                    isCollected = false;
                }
            } catch (Exception e) {
                e.printStackTrace();
                TUtils.toast(getString(R.string.toast_collect_failed));
            }
            return;
        }
        Map<String, String> params = RequestParamsUtils.getMapWithU();
        params.put("type", "3");
        params.put("typeid", vib.getVid());
        params.put("siteid", InterfaceJsonfile.SITEID);
        params.put("data", vib.getJson_url());
        SPUtil.addParams(params);
        OkHttpClientManager.postAsyn(tag, InterfaceJsonfile.ADDCOLLECTION// InterfaceApi.addcollection
                , new OkHttpClientManager.ResultCallback() {
            @Override
            public void onSuccess(Object response) {
                JSONObject obj = null;
                try {
                    obj = JSONObject.parseObject(response.toString());
                } catch (Exception e) {
                    TUtils.toast(getString(R.string.toast_collect_failed));
                    return;
                }

                if (200 == obj.getIntValue("code")) {
                    JSONObject object = obj.getJSONObject("data");
                    // 1:收藏操作成功 2:取消收藏操作成功
                    if ("1".equals(object.getString("status"))) {
                        TUtils.toast(getString(R.string.toast_collect_success));
                        isCollected = true;
                    } else {
                        TUtils.toast(getString(R.string.toast_collect_cancelled));
                        isCollected = false;
                    }
                } else {
                    TUtils.toast(obj.getString("msg"));
                }
            }

            @Override
            public void onFailure(Request request, Exception e) {
                TUtils.toast(getString(R.string.toast_cannot_connect_to_server));
            }
        }, params);
    }

    // 是否收藏
    private void isCollection() {
        if (null == vib) {
            return;
        }
        if (null != spu.getUser()) {
            Map<String, String> params = new HashMap<>();
            params.put("uid", spu.getUser().getUid());
            params.put("typeid", vib.getVid());
            params.put("type", "3");
            SPUtil.addParams(params);
            OkHttpClientManager.postAsyn(tag, InterfaceJsonfile.ISCELLECTION, new OkHttpClientManager.ResultCallback() {
                @Override
                public void onSuccess(Object response) {
                    try {
                        JSONObject obj = null;
                        try {
                            obj = JSONObject.parseObject(response.toString());
                        } catch (Exception e) {
                            return;
                        }

                        if (200 == obj.getIntValue("code")) {
                            JSONObject object = obj.getJSONObject("data");
                            if ("1".equals(object.getString("status"))) {
                                // "已收藏"
                                isCollected = true;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Request request, Exception e) {
                    LogUtils.i("isCollection failed");
                }
            }, params);
        } else {
            try {
                NewsItemBeanForCollection nbfc = dbHelper.getCollectionDBUitls()
                        .queryBuilder().where(NewsItemBeanForCollectionDao.Properties.Nid.eq(vib.getVid()))
                        .where(NewsItemBeanForCollectionDao.Properties.Type.eq("3"))
                        .build().unique();
                if (null != nbfc) {
                    isCollected = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPause() {
        if (mVideoView != null) {
            mVideoView.pause();
        }
        super.onPause();
    }

    private void popUpwindow() {
        final PopupWindow pinlunpop = new PopupWindow(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View popRoot = inflater.inflate(R.layout.newsdetail_popupwindow_layout, null);
        pinlunpop.setWindowLayoutMode(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        pinlunpop.setContentView(popRoot);
        ColorDrawable dw = new ColorDrawable(Color.TRANSPARENT);
        pinlunpop.setBackgroundDrawable(dw);
        pinlunpop.setOutsideTouchable(true);
        TextView pop_fenxiang_img = (TextView) popRoot.findViewById(R.id.pop_fenxiang_img);
        TextView pop_shoucang_img = (TextView) popRoot.findViewById(R.id.pop_shoucang_img);
        TextView pop_ziti_img = (TextView) popRoot.findViewById(R.id.pop_ziti_img);
        ImageView pop_xiazai_iv = (ImageView) popRoot.findViewById(R.id.pop_xiazai_iv);

//        if (isCollected) {
//            pop_shoucang_img.setImageResource(R.drawable.details_collect_already_select);
//        } else {
//            pop_shoucang_img.setImageResource(R.drawable.details_collect_select);
//        }
        // 写评论
//        pop_ziti_img.setImageResource(R.drawable.bt_comment_unselected);
        if (null != ccBean) {
            if (!"0".equals(ccBean.getComflag())) {
                // pop_ziti_img.setVisibility(View.GONE);
            }
        }

        pop_fenxiang_img.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                pinlunpop.dismiss();
                if (null == vib) {
                    return;
                }
                FacebookSharedUtil.showShares(vib.getTitle(), BASEURL + vib.getVid(), vib.getMainpic(),
                        VideoPlayerActivity.this);
            }
        });
        pop_shoucang_img.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                pinlunpop.dismiss();
                addCollection();
            }
        });
        // 评论
        pop_ziti_img.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                pinlunpop.dismiss();
                if (null == vib) {
                    return;
                }

                // if(!"0".equals(ccBean.getComflag())){
                ReplayBean bean = new ReplayBean(vib.getVid(), vib.getTitle(), "Video", vib.getJson_url(),
                        vib.getMainpic(), vib.getComcount());
                Intent intent = new Intent(VideoPlayerActivity.this, ZQ_ReplyActivity.class);
                intent.putExtra("replay", bean);
                startActivity(intent);
                AAnim.bottom2top(VideoPlayerActivity.this);
                // }

            }
        });

        pinlunpop.showAsDropDown(videodetails_title_pl, -60, -15);

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            LogUtils.i("landscape"); // 横屏
        } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            LogUtils.i("portrait"); // 竖屏
        }

        if (mVideoView != null) {
            mVideoView.resume();
            mVideoView.start();
        }
    }

    @Override
    protected void onDestroy() {
        if (mVideoView != null) {
            mVideoView.stopPlayback();
        }
        if (isTaskRoot() && "browser".equals(from)) {
            Intent in = new Intent();
            in.setClass(this, WelcomeActivity.class);
            startActivity(in);
        }
        OkHttpClientManager.cancel(tag);
        super.onDestroy();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mGestureDetector!=null&&mGestureDetector.onTouchEvent(event)) {
            return true;
        }

        // 处理手势结束
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP: {
                endGesture();
            }
            break;
        }
        return super.onTouchEvent(event);
    }

    // 手势结束
    private void endGesture() {
        mVolume = -1;
        mBrightness = -1f;
        // 隐藏
        mDismissHandler.removeMessages(0);
        mDismissHandler.sendEmptyMessageDelayed(0, 500);
    }

    // 定时隐藏
    private Handler mDismissHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mVolumeBrightnessLayout.setVisibility(View.GONE);
        }
    };

    // 滑动改变声音大小
    private void onVolumeSlide(float percent) {
        if (mVolume == -1) {
            mVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            if (mVolume < 0) {
                mVolume = 0;
            }

            // 显示
            mOperationBg.setImageResource(R.drawable.video_volumn_bg);
            mVolumeBrightnessLayout.setVisibility(View.VISIBLE);
        }

        int index = (int) (percent * mMaxVolume) + mVolume;
        if (index > mMaxVolume) {
            index = mMaxVolume;
        } else if (index < 0) {
            index = 0;
        }

        // 变更声音
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0);

        // 变更进度条
        ViewGroup.LayoutParams lp = mOperationPercent.getLayoutParams();
        lp.width = findViewById(R.id.operation_full).getLayoutParams().width * index / mMaxVolume;
        mOperationPercent.setLayoutParams(lp);
    }

    // 滑动改变亮度
    private void onBrightnessSlide(float percent) {
        if (mBrightness < 0) {
            mBrightness = getWindow().getAttributes().screenBrightness;
            if (mBrightness <= 0.00f)
                mBrightness = 0.50f;
            if (mBrightness < 0.01f)
                mBrightness = 0.01f;

            // 显示
            mOperationBg.setImageResource(R.drawable.video_brightness_bg);
            mVolumeBrightnessLayout.setVisibility(View.VISIBLE);
        }
        WindowManager.LayoutParams lpa = getWindow().getAttributes();
        lpa.screenBrightness = mBrightness + percent;
        if (lpa.screenBrightness > 1.0f) {
            lpa.screenBrightness = 1.0f;
        } else if (lpa.screenBrightness < 0.01f) {
            lpa.screenBrightness = 0.01f;
        }
        getWindow().setAttributes(lpa);

        ViewGroup.LayoutParams lp = mOperationPercent.getLayoutParams();
        lp.width = (int) (findViewById(R.id.operation_full).getLayoutParams().width * lpa.screenBrightness);
        mOperationPercent.setLayoutParams(lp);
    }


    private void stopPlayer() {
        if (mVideoView != null)
            mVideoView.pause();
    }

    private void startPlayer() {
        if (mVideoView != null)
            mVideoView.start();
    }

    private boolean isPlaying() {
        return mVideoView != null && mVideoView.isPlaying();
    }

    private void getData() {
        LogUtils.i("getData");
        final File pageFile = App
                .getFile(videoPath + File.separator + "videodetail" + File.separator + "detail" + vib.getVid());
        LogUtils.i("pageFile-->" + pageFile.getAbsolutePath());

        if (GetFileSizeUtil.getInstance().getFileSizes(pageFile) > 10) {
            String data = App.getFileContext(pageFile);
            LogUtils.i("video detail data-->" + data);
            JSONObject obj = null;
            try {
                obj = JSONObject.parseObject(data);
            } catch (Exception e) {
                pageFile.delete();
                e.printStackTrace();
                TUtils.toast(getString(R.string.toast_cache_failed));
                return;
            }
            vdib = FjsonUtil.parseObject(obj.getString("data"), VideoDetailBean.class);

            mTitle = vdib.getTitle();
            mPath = vdib.getVideourl();

            Log.i("test", "video path:" + mPath + "  mTitle-->" + mTitle);
            if (mPath.startsWith("http:")) {
                mVideoView.setVideoURI(Uri.parse(mPath));
            } else {
                mVideoView.setVideoPath(mPath);
            }

            return;
        }

        OkHttpClientManager.getAsyn(tag, vib.getJson_url(), new OkHttpClientManager.ResultCallback() {
            @Override
            public void onSuccess(Object response) {
                String data = response.toString();
                if (null == data || "".equals(data)) {
                    return;
                }
                SPUtil.saveFile(pageFile, data);
                LogUtils.i("http video detail data-->" + data);
                JSONObject obj = null;
                try {
                    obj = JSONObject.parseObject(data);
                } catch (Exception e) {
                    e.printStackTrace();
                    TUtils.toast(getString(R.string.toast_cache_failed));
                    return;
                }

                vdib = JSONObject.parseObject(obj.getString("data"), VideoDetailBean.class);
                mTitle = vdib.getTitle();
                mPath = vdib.getVideourl();
                LogUtils.i("video path:" + mPath);
                if (mPath.startsWith("http:")) {
                    mVideoView.setVideoURI(Uri.parse(mPath));
                } else {
                    mVideoView.setVideoPath(mPath);
                }

            }

            @Override
            public void onFailure(Request request, Exception e) {
                LogUtils.i("getvideo detail failed-");
            }
        });
    }

    //
    private void getVideo_ni(final String jsonurl) {

        NewsJumpBean videobbean = null;
        videobbean = dbHelper.getNewsJumpBeanDao()
                .queryBuilder().where(NewsJumpBeanDao.Properties.Url.eq(jsonurl))
                .build().unique();
        if (null != videobbean) {
            vdib = JSONObject.parseObject(videobbean.getContent(), VideoDetailBean.class);
            mTitle = vdib.getTitle();
            mPath = vdib.getVideourl();

            if (mPath.startsWith("http:")) {
                mVideoView.setVideoURI(Uri.parse(mPath));
            } else {
                mVideoView.setVideoPath(mPath);
            }
        } else {
            final File pathFile = App.getFile(App.getInstance().getJsonFileCacheRootDir() + File.separator + "temp" + File.separator + "album");
            OkHttpClientManager.getAsyn(tag, jsonurl,
                    new OkHttpClientManager.ResultCallback() {
                        @Override
                        public void onSuccess(Object response) {
                            try {
                                String data = response.toString();
                                if (!TextUtils.isEmpty(data)) {
                                    SPUtil.saveFile(pathFile, data);
                                }
                                JSONObject obj = null;
                                try {
                                    obj = JSONObject.parseObject(data);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    TUtils.toast(getString(R.string.toast_cache_failed));
                                    return;
                                }
                                vdib = JSONObject.parseObject(obj.getString("data"), VideoDetailBean.class);

                                NewsJumpBean videobbean = new NewsJumpBean(jsonurl, obj.getString("data"));
                                dbHelper.getNewsJumpBeanDao().insert(videobbean);
                                mTitle = vdib.getTitle();
                                mPath = vdib.getVideourl();

                                if (mPath.toLowerCase().startsWith("http")) {
                                    mVideoView.setVideoURI(Uri.parse(mPath));
                                } else {
                                    mVideoView.setVideoPath(mPath);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onFailure(Request request, Exception e) {

                        }
                    });
        }

    }

    private CommentsCountBean ccBean;// 评论数量，是否可评论 //TODO

    @Override
    public void finish() {
        if (isTaskRoot() && "browser".equals(from)) {
            Intent in = new Intent();
            in.setClass(this, WelcomeActivity.class);
            startActivity(in);
        }
        super.finish();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.e("test", "" + mVideoView);
        mLoadingView.setVisibility(View.GONE);
    }


}