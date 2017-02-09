package com.hzpd.ui.fragments.welcome;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.hzpd.hflt.R;
import com.hzpd.ui.App;
import com.hzpd.ui.activity.WelcomeActivity;
import com.hzpd.ui.fragments.BaseFragment;
import com.hzpd.utils.Log;
import com.hzpd.utils.SPUtil;
import com.nostra13.universalimageloader.utils.DiskCacheUtils;

import java.io.File;

public class AdFlashFragment extends BaseFragment implements View.OnClickListener, View.OnLongClickListener {

    private ImageView adflash_img_ad;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.adflash_frag_layout, container,
                false);
        adflash_img_ad = (ImageView) view.findViewById(R.id.adflash_img_ad);
        adflash_img_ad.setOnClickListener(this);
        adflash_img_ad.setOnLongClickListener(this);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        isAdd = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("test", "  onResume ");
        adflash_img_ad.postDelayed(runnable, 300);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        isAdd = false;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        isAdd = true;
    }

    boolean isAdd = false;
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Log.e("test", "  AdFlashFragment " + isAdd);
            Bitmap bm = null;
            if (!isAdd) {
                return;
            }
            if (null != App.getInstance().welcomeAdbean) {
                final String url = App.getInstance().welcomeAdbean.getImage();
                if (!TextUtils.isEmpty(url)) {

                    try {
                        File fBitmap = DiskCacheUtils.findInCache(url.replaceAll("&amp;", "&"), mImageLoader.getDiskCache());
                        if (null != fBitmap) {
                            bm = BitmapFactory.decodeFile(fBitmap
                                    .getAbsolutePath());
                        } else {
                            String uri = url;
                            if (SPUtil.isImageUri(uri)) {
                                mImageLoader.loadImage(uri, null);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            if (!isAdd) {
                return;
            }
            try {
                if (null != bm) {
                    adflash_img_ad.setImageBitmap(bm);
                    adflash_img_ad.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (isAdd) {
                                    ((WelcomeActivity) getActivity()).loadMainUI();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, 500);
                } else {
                    try {
                        if (isAdd) {
                            ((WelcomeActivity) getActivity()).loadMainUI();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };


    @Override
    public void onDestroy() {
        try {
            ((ViewGroup) view).removeAllViews();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.adflash_img_ad:
                String url = null;
                if (null != App.getInstance().welcomeAdbean) {
                    url = App.getInstance().welcomeAdbean.getLink();
                }
                if (!TextUtils.isEmpty(url)) {
                    ((WelcomeActivity) getActivity()).jump(url);
                }
                break;
        }
    }

    @Override
    public boolean onLongClick(View view) {
        return true;
    }
}
