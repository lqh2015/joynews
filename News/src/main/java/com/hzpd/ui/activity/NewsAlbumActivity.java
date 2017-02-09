package com.hzpd.ui.activity;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.color.tools.mytools.LogUtils;
import com.hzpd.custorm.DonutProgress;
import com.hzpd.custorm.ImageViewPager;
import com.hzpd.custorm.RecyclingPagerAdapter;
import com.hzpd.hflt.BuildConfig;
import com.hzpd.hflt.R;
import com.hzpd.hflt.wxapi.FacebookSharedUtil;
import com.hzpd.modle.CommentsCountBean;
import com.hzpd.modle.ImageListSubBean;
import com.hzpd.modle.ImgListBean;
import com.hzpd.modle.NewsBean;
import com.hzpd.modle.NewsDetailBean;
import com.hzpd.modle.NewsDetailImgList;
import com.hzpd.modle.ReplayBean;
import com.hzpd.modle.db.Jsonbean;
import com.hzpd.modle.db.JsonbeanDao;
import com.hzpd.modle.db.NewsItemBeanForCollection;
import com.hzpd.modle.db.NewsItemBeanForCollectionDao;
import com.hzpd.modle.db.NewsJumpBean;
import com.hzpd.modle.db.NewsJumpBeanDao;
import com.hzpd.ui.App;
import com.hzpd.url.InterfaceJsonfile;
import com.hzpd.url.OkHttpClientManager;
import com.hzpd.utils.AAnim;
import com.hzpd.utils.AvoidOnClickFastUtils;
import com.hzpd.utils.Constant;
import com.hzpd.utils.DisplayOptionFactory;
import com.hzpd.utils.EventUtils;
import com.hzpd.utils.FjsonUtil;
import com.hzpd.utils.Log;
import com.hzpd.utils.MyCommonUtil;
import com.hzpd.utils.RequestParamsUtils;
import com.hzpd.utils.SPUtil;
import com.hzpd.utils.TUtils;
import com.squareup.okhttp.Request;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import uk.co.senab.photoview.IPhotoView;
import uk.co.senab.photoview.PhotoView;

/**
 * 图集展示*
 */
public class NewsAlbumActivity extends MBaseActivity implements OnClickListener {


    @Override
    public String getAnalyticPageName() {
        return "图集展示页";
    }

    @Override
    protected int getOnResumeApi() {
        return 110;
    }

    @Override
    protected int getOnStopApi() {
        return 111;
    }


    private ImageViewPager mViewPager;
    private TextView mTextViewTitle;
    private TextView mTextViewContent;
    private TextView mTextViewNumber;
    private View mLayoutBack;
    private LinearLayout imgdetails_title_pl;
    private LinearLayout imgdetails_title_comment;
    private TextView imgdetails_title_num;
    private DonutProgress donutProgress;
    private NewsAlbumAdapter simpleAdapter;
    private String from;//news album newsitem
    private int currentPosition = 0;
    private RelativeLayout album_rl_head;//头部
    private RelativeLayout album_rl_bottom;
    private ImageView pop_xiazai_iv1;
    private boolean animationFlag = false;//是否隐藏

    private NewsDetailBean ndb;
    private ImgListBean imgListBean;
    private Object tag;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_detail_img_main_layout);
        initViews();
        tag = OkHttpClientManager.getTag();
        pop_xiazai_iv1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AvoidOnClickFastUtils.isFastDoubleClick(v);
                Log.e("test", "News: ALBUM");
                ImageListSubBean bean = simpleAdapter.getCurrentBean(currentPosition);
                final String sdPath = getSDPath();
                if (null == sdPath) {
                    TUtils.ToastLeftAndRight(activity, null, R.drawable.notice_failure, getString(R.string.toast_no_sdcard));
                    return;
                }
                String imgUrl = bean.getSubphoto();
                Log.e("test", "imgUrl: " + imgUrl);
                if ((null == imgUrl || "".equals(imgUrl))
                        && !imgUrl.endsWith(".jpg") && !imgUrl.endsWith(".JPG")
                        && !imgUrl.endsWith(".png") && !imgUrl.endsWith(".PNG")
                        && !imgUrl.endsWith(".gif") && !imgUrl.endsWith(".GIF")) {
                    TUtils.toast(getString(R.string.toast_image_format_is_wrong));
                    return;
                }
                // 首先保存图片
                final File appDir = new File(Environment.getExternalStorageDirectory(), "BERKIN");
                if (!appDir.exists()) {
                    appDir.mkdirs();
                }
                final String fileName = "BERKIN_" + SPUtil.getImageName(imgUrl);
                final File file = new File(appDir, fileName);
                Log.i("News", "图片路径file：" + file.getAbsolutePath());
                TUtils.ToastLeftAndRight(activity, null, R.drawable.notice_succeed, getString(R.string.toast_start_download));
                OkHttpClientManager.download(imgUrl, file, false, new OkHttpClientManager.ResultCallback() {
                    @Override
                    public void onSuccess(Object response) {
                        try {
                            Log.i("onSuccess", "图片下载  ===  onSuccess " + file.exists());
                            TUtils.toast(R.string.toast_downloaded_at + file.getAbsolutePath());
                            // 其次把文件插入到系统图库
                            try {
                                MediaStore.Images.Media.insertImage(getContentResolver(),
                                        file.getAbsolutePath(), fileName, null);
                                // 最后通知图库更新
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + Environment.getExternalStorageDirectory() + "/BERKIN")));
                            pop_xiazai_iv1.setFocusable(false);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Request request, Exception e) {
                        TUtils.ToastLeftAndRight(activity, null, R.drawable.notice_failure, getString(R.string.toast_download_failed));
                    }

                    @Override
                    public void onLoading(int total, int current) {
                    }
                });
            }
        });

        init();
    }


    private void initViews() {
        mViewPager = (ImageViewPager) findViewById(R.id.img_detail_test_pager);
        mTextViewTitle = (TextView) findViewById(R.id.img_detail_title_id);
        mTextViewContent = (TextView) findViewById(R.id.img_detail_content_id);
        mTextViewNumber = (TextView) findViewById(R.id.img_detial_number_id);
        mLayoutBack = findViewById(R.id.main_title_personal);
        imgdetails_title_pl = (LinearLayout) findViewById(R.id.imgdetails_title_pl);
        imgdetails_title_comment = (LinearLayout) findViewById(R.id.imgdetails_title_comment);
        imgdetails_title_num = (TextView) findViewById(R.id.imgdetails_title_num);
        donutProgress = (DonutProgress) findViewById(R.id.album_donutProgress);
        album_rl_head = (RelativeLayout) findViewById(R.id.album_rl_head);
        album_rl_bottom = (RelativeLayout) findViewById(R.id.album_rl_bottom);
        pop_xiazai_iv1 = (ImageView) findViewById(R.id.pop_xiazai_iv1);

    }

    private void init() {

        String pid = null;
        Intent intent = getIntent();
        String action = intent.getAction();
        if (null != action && Intent.ACTION_VIEW.equals(action)) {
            Uri uri = intent.getData();
            if (uri != null) {
                pid = uri.getPath();
                pid = pid.replace(File.separator, "");
                from = "browser";
                getAlbum_browser(pid);
            }
        } else {
            from = intent.getStringExtra("from");
        }

        initViewPage();

        if ("news".equals(from)) {
            currentPosition = intent.getIntExtra("position", 0);
            ndb = (NewsDetailBean) intent.getSerializableExtra("ndb");

            List<ImageListSubBean> mlist = new ArrayList<ImageListSubBean>();
            if (null != ndb.getPic()) {
                for (NewsDetailImgList im : ndb.getPic()) {
                    ImageListSubBean ilsb = new ImageListSubBean(im.getSubdesc(), im.getSubphoto());
                    mlist.add(ilsb);
                }
                simpleAdapter.setList(mlist);
            }
            mTextViewTitle.setText(ndb.getTitle());
            if (simpleAdapter.getCount() > currentPosition) {
                mViewPager.setCurrentItem(currentPosition);
            }
        } else if ("album".equals(from)) {
            currentPosition = 0;
            imgListBean = (ImgListBean) intent.getSerializableExtra("imgbean");
            if (null != imgListBean) {
                simpleAdapter.setList(imgListBean.getSubphoto());
                setVisible();
            }

        } else if ("collection".equals(from)) {
            getCollectionPhotoList(intent.getStringExtra("json_url")
                    , intent.getStringExtra("pid"));
        } else if ("newsitem".equals(from)) {
            //获取图集
            NewsBean nb = (NewsBean) intent.getSerializableExtra("newbean");
            getAlbum_ni(nb.getJson_url());

        } else if ("browser".equals(from)) {
            getAlbum_browser(pid);
        } else {
            if (BuildConfig.DEBUG) {
                TUtils.toast("from nothing!");
            }
            return;
        }
        mLayoutBack.setOnClickListener(this);
        imgdetails_title_pl.setOnClickListener(this);
        imgdetails_title_comment.setOnClickListener(this);

    }

    private void setVisible() {
        if (simpleAdapter.getListSize() > 0) {
            imgdetails_title_pl.setVisibility(View.VISIBLE);
//			imgdetails_title_comment.setVisibility(View.VISIBLE);
//            getCommentsCounts();//TODO 评论数量
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initViewPage() {
        simpleAdapter = new NewsAlbumAdapter();

        mViewPager.setPageMargin(10);

        mViewPager.setAdapter(simpleAdapter);
        mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrollStateChanged(int arg0) {

            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageSelected(int arg0) {
                mTextViewNumber.setText((arg0 + 1) + "/" + simpleAdapter.getCount());
                mTextViewContent.setText(simpleAdapter.getDes(arg0));
                currentPosition = arg0;
            }
        });
        mViewPager.setCurrentItem(currentPosition);

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    class NewsAlbumAdapter extends RecyclingPagerAdapter {
        private List<ImageListSubBean> list;
        private LayoutInflater inflater;

        public NewsAlbumAdapter() {
            list = new ArrayList<ImageListSubBean>();
            inflater = LayoutInflater.from(NewsAlbumActivity.this);
        }

        public ImageListSubBean getCurrentBean(int position) {
            return list.get(position % list.size());
        }

        public void setList(List<ImageListSubBean> mList) {
            if (mList.size() > 0) {
                clearList();
                addList(mList);
            }
        }

        public int getListSize() {
            return list.size();
        }

        public void addList(List<ImageListSubBean> mList) {
            list.addAll(mList);
            mTextViewNumber.setText("1/" + list.size());
            if ("news".equals(from)) {
                mTextViewTitle.setText(ndb.getTitle());
            } else {
                mTextViewTitle.setText(imgListBean.getTitle());
            }

            if (list.size() > 0) {
                mTextViewContent.setText(list.get(0).getSubdesc());
            }
            notifyDataSetChanged();
        }

        public void clearList() {
            list.clear();
        }

        @Override
        public int getCount() {
            return list.size();
        }

        public String getDes(int positon) {
            return list.get(positon).getSubdesc();
        }


        @Override
        public View getView(int position, View convertView, ViewGroup container) {

            final PhotoView photoView = new PhotoView(activity);
            LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT
                    , LayoutParams.MATCH_PARENT);
            photoView.setLayoutParams(params);

            Log.e("test", "getView: " + list.get(position).getSubphoto());
            SPUtil.displayImage(list.get(position).getSubphoto()
                    , photoView, DisplayOptionFactory.Big.options);

            photoView.setOnDoubleTapListener(new OnDoubleTapListener() {
                @Override
                public boolean onSingleTapConfirmed(MotionEvent e) {
                    LogUtils.i("photoView click");
                    if (animationFlag) {
                        ObjectAnimator
                                .ofFloat(album_rl_head, "translationY"
                                        , -album_rl_head.getHeight(), 0f)
                                .setDuration(350)
                                .start();

                        ObjectAnimator
                                .ofFloat(album_rl_bottom, "translationY"
                                        , album_rl_bottom.getHeight(), 0f)
                                .setDuration(350)
                                .start();

                        animationFlag = false;
                    } else {
                        ObjectAnimator
                                .ofFloat(album_rl_head, "translationY"
                                        , 0f, -album_rl_head.getHeight())
                                .setDuration(350)
                                .start();

                        ObjectAnimator
                                .ofFloat(album_rl_bottom, "translationY"
                                        , 0f, album_rl_bottom.getHeight())
                                .setDuration(350)
                                .start();

                        animationFlag = true;
                    }
                    return true;
                }

                @Override
                public boolean onDoubleTapEvent(MotionEvent e) {
                    return false;
                }

                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    float scale = photoView.getScale();
                    scale = Math.round(scale * 100) / 100f;
                    LogUtils.i("photoView double " + scale);

                    if (scale <= IPhotoView.DEFAULT_MIN_SCALE) {
                        photoView.setScale(IPhotoView.DEFAULT_MID_SCALE, true);
                    } else if (scale <= IPhotoView.DEFAULT_MID_SCALE) {
                        photoView.setScale(IPhotoView.DEFAULT_MAX_SCALE, true);
                    } else if (scale <= IPhotoView.DEFAULT_MAX_SCALE) {
                        photoView.setScale(IPhotoView.DEFAULT_MIN_SCALE, true);
                    } else {
                        return true;
                    }

                    return false;
                }
            });

            return photoView;
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_title_personal: {
                finish();
            }
            break;
            case R.id.imgdetails_title_comment: {
                //跳转到评论
                //跳转到评论页
                if (null == imgListBean) {
                    return;
                }
                if (!MyCommonUtil.isNetworkConnected(this)) {
                    TUtils.ToastLeftAndRight(activity, null, R.drawable.notice_failure, getString(R.string.toast_check_network));
                    return;
                }

            }
            break;
            case R.id.imgdetails_title_pl: {
                //分享 收藏  写评论
                popUpwindow();
            }
            break;
        }
    }

    private void popUpwindow() {
        final PopupWindow pinlunpop = new PopupWindow(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View popRoot = inflater.inflate(R.layout.newsdetail_popupwindow_layout, null);
        pinlunpop.setWindowLayoutMode(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        pinlunpop.setContentView(popRoot);
        ColorDrawable dw = new ColorDrawable(Color.TRANSPARENT);
        pinlunpop.setBackgroundDrawable(dw);
        pinlunpop.setOutsideTouchable(true);
        TextView pop_fenxiang_img = (TextView) popRoot.findViewById(R.id.pop_fenxiang_img);
        TextView pop_shoucang_img = (TextView) popRoot.findViewById(R.id.pop_shoucang_img);
        TextView pop_ziti_img = (TextView) popRoot.findViewById(R.id.pop_ziti_img);
        ImageView pop_xiazai_iv = (ImageView) popRoot.findViewById(R.id.pop_xiazai_iv);


        //写评论
//        pop_ziti_img.setImageResource(R.drawable.bt_comment_unselected);
        if (null != ccBean) {
            if (!"0".equals(ccBean.getComflag())) {
//				pop_ziti_img.setVisibility(View.GONE);
            }
        }
        pop_xiazai_iv.setVisibility(View.VISIBLE);

//		分享
        pop_fenxiang_img.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                pinlunpop.dismiss();
                String[] array = new String[imgListBean.getSubphoto().size()];
                for (int i = 0; i < imgListBean.getSubphoto().size(); i++) {
                    ImageListSubBean b = imgListBean.getSubphoto().get(i);
                    array[i] = b.getSubphoto();
                }
                String imgPath = null;
                if (imgListBean.getSubphoto().size() > 0) {
                    imgPath = imgListBean.getSubphoto().get(0).getSubphoto();
                }
                //http://58.68.134.165:8081/index/Album/view/id/pid
                FacebookSharedUtil.showImgShares(imgListBean.getTitle(), imgPath, imgListBean.getPid(), NewsAlbumActivity.this);
            }
        });
        pop_shoucang_img.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                pinlunpop.dismiss();
                addCollection();
            }
        });
        //
        pop_ziti_img.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                pinlunpop.dismiss();
                if (null == imgListBean) {
                    return;
                }

//				if(!"0".equals(ccBean.getComflag())){
                String smallimg = imgListBean.getSubphoto().get(0).getSubphoto();
                ReplayBean bean = new ReplayBean(imgListBean.getPid()
                        , imgListBean.getTitle(), "Album", imgListBean.getJson_url()
                        , smallimg, imgListBean.getComcount());

//				TUtils.toast(imgListBean.getPid() + "" + imgListBean.getTitle() + "" + "Album" + "" + imgListBean.getJson_url() + "" + smallimg);
                Intent intent = new Intent(NewsAlbumActivity.this, ZQ_ReplyActivity.class);
                intent.putExtra("replay", bean);
                startActivity(intent);
                AAnim.bottom2top(NewsAlbumActivity.this);
//				}else{
//					TUtils.toast("不可评论");
//				}

            }
        });

        //照片下载
        pop_xiazai_iv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                pinlunpop.dismiss();
                ImageListSubBean bean = simpleAdapter.getCurrentBean(currentPosition);
                String sdPath = getSDPath();
                if (null == sdPath) {
                    TUtils.ToastLeftAndRight(activity, null, R.drawable.notice_failure, getString(R.string.toast_no_sdcard));
                    return;
                }


                String imgUrl = bean.getSubphoto();
                if ((null == imgUrl || "".equals(imgUrl))
                        && !imgUrl.endsWith(".jpg") && !imgUrl.endsWith(".JPG")
                        && !imgUrl.endsWith(".png") && !imgUrl.endsWith(".PNG")
                        && !imgUrl.endsWith(".gif") && !imgUrl.endsWith(".GIF")) {
                    TUtils.toast(getString(R.string.toast_image_format_is_wrong));
                    return;
                }
                final File img = App.getFile(sdPath + File.separator + "download_joymeng" + File.separator + System.currentTimeMillis() + ".jpg");

                OkHttpClientManager.getAsyn(tag,
                        imgUrl
                        , new OkHttpClientManager.ResultCallback() {
                            @SuppressLint("StringFormatInvalid")
                            @Override
                            public void onSuccess(Object response) {
                                try {
                                    SPUtil.saveFile(img, response.toString());
                                    if (isResume) {
                                        TUtils.toast("" + getString(R.string.toast_downloaded_at, img.getAbsolutePath()));
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(Request request, Exception e) {
                                TUtils.ToastLeftAndRight(activity, null, R.drawable.notice_failure, getString(R.string.toast_download_failed));
                            }
//                            @Override
//                            public void onStart() {
//                                super.onStart();
//                            }
//
//                            @Override
//                            public void onLoading(long total, long current,
//                                                  boolean isUploading) {
//                                super.onLoading(total, current, isUploading);
//                                LogUtils.i("current:" + current + " total:" + total + "   " + (current * 100 / total));
//                            }
                        });
            }
        });

        pinlunpop.showAsDropDown(imgdetails_title_pl, -60, -15);

    }

    //添加收藏------------------
    private void addCollection() {
        if (null == spu.getUser()) {
            String data = JSONObject.toJSONString(imgListBean);
            LogUtils.i("data-->" + data);
            Jsonbean tcb = new Jsonbean(imgListBean.getPid(), data);
            NewsItemBeanForCollection nibfc = new NewsItemBeanForCollection(imgListBean);

            try {
                NewsItemBeanForCollection nitb = dbHelper.getCollectionDBUitls().queryBuilder()
                        .where(NewsItemBeanForCollectionDao.Properties.Nid.eq(imgListBean.getPid()))
                        .build().unique();
                if (null == nitb) {
                    dbHelper.getCollectionDBUitls().insert(nibfc);
                    dbHelper.getJsonbeanDao().insert(tcb);

                    TUtils.toast(getString(R.string.toast_collect_success));
                } else {
                    dbHelper.getCollectionDBUitls().queryBuilder()
                            .where(NewsItemBeanForCollectionDao.Properties.Nid.eq(imgListBean.getPid()))
                            .buildDelete().executeDeleteWithoutDetachingEntities();

                    dbHelper.getJsonbeanDao().queryBuilder()
                            .where(JsonbeanDao.Properties.Fid.eq(imgListBean.getPid()))
                            .buildDelete().executeDeleteWithoutDetachingEntities();

                    TUtils.ToastLeftAndRight(activity, null, R.drawable.notice_succeed, getString(R.string.toast_collect_cancelled));
                }

            } catch (Exception e1) {
                TUtils.ToastLeftAndRight(activity, null, R.drawable.notice_failure, getString(R.string.toast_collect_failed));
            }
            return;
        }
        LogUtils.i("Type-->" + "  Fid-->" + imgListBean.getPid());
        Map<String, String> params = RequestParamsUtils.getMaps();
        params.put("type", "2");
        params.put("typeid", imgListBean.getPid());
        params.put("siteid", InterfaceJsonfile.SITEID);
        params.put("data", imgListBean.getJson_url());
        SPUtil.addParams(params);
        OkHttpClientManager.postAsyn(tag
                , InterfaceJsonfile.ADDCOLLECTION//InterfaceApi.addcollection
                , new OkHttpClientManager.ResultCallback() {
                    @Override
                    public void onSuccess(Object response) {
                        try {
                            JSONObject obj = FjsonUtil.parseObject(response.toString());
                            if (null == obj) {
                                return;
                            }
                            if (200 == obj.getIntValue("code")) {
                                JSONObject object = obj.getJSONObject("data");
                                //1:收藏操作成功 2:取消收藏操作成功
                                if ("1".equals(object.getString("status"))) {
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Request request, Exception e) {
                        TUtils.ToastLeftAndRight(activity, null, R.drawable.notice_failure, getString(R.string.toast_cannot_connect_to_server));
                    }

                }, params
        );

    }

    //获取收藏
    private void getCollectionPhotoList(String json_url, final String albumitemid) {

        LogUtils.i("json_url-->" + json_url + "  albumitemid-->" + albumitemid);
        if (TextUtils.isEmpty(json_url)) {
            imgdetails_title_pl.setVisibility(View.GONE);
            imgdetails_title_comment.setVisibility(View.GONE);
        }
        final String filePath = App.getInstance().getJsonFileCacheRootDir() + File.separator + "temp" + File.separator + "collection" + File.separator + albumitemid;
        final File file = App.getFile(filePath);
//		
        Jsonbean tcl = null;
        try {
            tcl = dbHelper.getJsonbeanDao().queryBuilder()
                    .where(JsonbeanDao.Properties.Fid.eq(albumitemid)).unique();
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        if (null != tcl) {
            LogUtils.i("data-->" + tcl.getData());

            imgListBean = FjsonUtil.parseObject(tcl.getData(), ImgListBean.class);
            if (null == imgListBean) {
                LogUtils.i("imgListBeann not null");
                return;
            }
            currentPosition = 0;
            simpleAdapter.setList(imgListBean.getSubphoto());
            mTextViewTitle.setText(imgListBean.getTitle());
            setVisible();
            return;
        }

        OkHttpClientManager.getAsyn(tag,
                json_url
                , new OkHttpClientManager.ResultCallback() {
                    @Override
                    public void onSuccess(Object response) {
                        try {
                            String data = response.toString();
                            if (!TextUtils.isEmpty(data)) {
                                SPUtil.saveFile(file, data);
                            }
                            JSONObject obj = FjsonUtil.parseObject(data);
                            if (null == obj) {
                                SPUtil.deleteFiles(filePath);
                                return;
                            }
                            imgListBean = FjsonUtil.parseObject(obj.getString("data"), ImgListBean.class);
                            if (null == imgListBean) {
                                SPUtil.deleteFiles(filePath);
                                return;
                            }
                            currentPosition = 0;
                            simpleAdapter.setList(imgListBean.getSubphoto());
                            mTextViewTitle.setText(imgListBean.getTitle());

                            setVisible();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Request request, Exception e) {
                        TUtils.ToastLeftAndRight(activity, null, R.drawable.notice_failure, getString(R.string.toast_server_no_response));
                    }
                });

    }

    //获取单个图集
    private void getAlbum_ni(final String json_url) {
        LogUtils.i("json_url-->" + json_url);
        NewsJumpBean albumdbbean = null;
        try {
            albumdbbean = dbHelper.getNewsJumpBeanDao().queryBuilder()
                    .where(NewsJumpBeanDao.Properties.Url.eq(json_url)).unique();
        } catch (Exception e) {
            e.printStackTrace();
            albumdbbean = null;
        }
        if (null != albumdbbean) {
            LogUtils.i("albumdbbeanid-->" + albumdbbean.getId()
                    + "\nurl-->" + albumdbbean.getUrl()
                    + "data-->" + albumdbbean.getContent());
            imgListBean = JSONObject.parseObject(albumdbbean.getContent(), ImgListBean.class);
            currentPosition = 0;
            LogUtils.i("-->" + imgListBean.getSubphoto().size());
            if (null == imgListBean) {
                LogUtils.i("imgListBean null");
            }
            simpleAdapter.setList(imgListBean.getSubphoto());
            setVisible();
        } else {
            final File file = App.getFile(App.getInstance().getJsonFileCacheRootDir() + File.separator + "temp" + File.separator + "album");

            OkHttpClientManager.getAsyn(tag,
                    json_url
                    , new OkHttpClientManager.ResultCallback() {
                        @Override
                        public void onSuccess(Object response) {
                            try {
                                String content = response.toString();
                                if (!TextUtils.isEmpty(content)) {
                                    SPUtil.saveFile(file, content);
                                }
                                Log.e("test", "NewsAlbumActivity: " + content);
                                JSONObject obj = JSONObject.parseObject(content);

                                imgListBean = JSONObject.parseObject(
                                        obj.getJSONObject("data").toJSONString()
                                        , ImgListBean.class);

                                NewsJumpBean albumdbbean = new NewsJumpBean(
                                        json_url
                                        , obj.getJSONObject("data").toJSONString());
                                try {
                                    dbHelper.getNewsJumpBeanDao().insert(albumdbbean);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                currentPosition = 0;
                                simpleAdapter.setList(imgListBean.getSubphoto());
                                setVisible();
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

    //获取单个图集来自浏览器跳转
    private void getAlbum_browser(final String pid) {

    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    public void finish() {
        if (isTaskRoot() && SPUtil.isIconShow()) {
            Intent in = new Intent();
            in.setClass(this, WelcomeActivity.class);
            startActivity(in);
        }
        super.finish();
    }

    public String getSDPath() {
        String path = null;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED);
        if (sdCardExist) {
            File sdDir = Environment.getExternalStorageDirectory();
            path = sdDir.getAbsolutePath();
        }
        return path;
    }

    private void alpha() {
        AlphaAnimation alpha = new AlphaAnimation(1f, 0f);
        alpha.setDuration(150);
        alpha.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                donutProgress.setVisibility(View.GONE);
            }
        });
        donutProgress.startAnimation(alpha);
    }

    private CommentsCountBean ccBean;//评论数量，是否可评论

    private void getCommentsCounts() {
        if (null == imgListBean) {
            return;
        }

        EventUtils.sendReadAtical(activity);
        Map<String, String> params = RequestParamsUtils.getMaps();
        params.put("type", Constant.TYPE.AlbumA.toString());
        params.put("nids", imgListBean.getPid());
        SPUtil.addParams(params);
        OkHttpClientManager.postAsyn(tag, InterfaceJsonfile.commentsConts

                , new OkHttpClientManager.ResultCallback() {
            @Override
            public void onSuccess(Object response) {
                try {
                    JSONObject obj = FjsonUtil
                            .parseObject(response.toString());
                    if (null == obj) {
                        return;
                    }
                    if (200 == obj.getIntValue("code")) {
                        List<CommentsCountBean> li = JSONObject.parseArray(obj.getString("data")
                                , CommentsCountBean.class);
                        if (null == li) {
                            return;
                        }
                        for (CommentsCountBean cc : li) {
                            if (imgListBean.getPid().equals(cc.getNid())) {
                                ccBean = cc;
                                String snum = cc.getC_num();
                                if (TextUtils.isDigitsOnly(snum)) {
                                    int count = Integer.parseInt(snum);
                                    //										TUtils.toast(count+"");
                                    if (count > 0) {
                                        imgdetails_title_comment.setVisibility(View.VISIBLE);
                                        imgdetails_title_num.setText(count + "");//设置评论数量
                                    }
                                }
                            }
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Request request, Exception e) {

            }
        }, params);
    }

}