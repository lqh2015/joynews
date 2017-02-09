package com.hzpd.ui.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.hzpd.utils.ACache;
import com.hzpd.utils.DBHelper;
import com.hzpd.utils.SPUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.common.lib.analytics.AnalyticCallback;
import org.common.lib.analytics.FragmentLifecycleAction;

import java.lang.reflect.Field;

public class BaseFragment extends Fragment implements AnalyticCallback {

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisible != isVisibleToUser) {
            isVisible = isVisibleToUser;
        }
    }




    public boolean isVisible = false;
    public int mPosition = -2; //ViewPager Position

    private FragmentLifecycleAction action = new FragmentLifecycleAction(this);

    protected ImageLoader mImageLoader;
    protected SPUtil spu;//
    protected ACache aCache;
    protected FragmentManager fm;
    protected DBHelper dbHelper;
    protected Activity activity;
    protected String title;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        action.onCreate(this);
        fm = getChildFragmentManager();
        spu = SPUtil.getInstance();
        mImageLoader = ImageLoader.getInstance();
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
        dbHelper = DBHelper.getInstance();
    }

    public void onResume() {
        super.onResume();
        action.onResume(this);
    }

    public void onPause() {
        super.onPause();
        action.onPause(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public String getLogTag() {
        return getClass().getSimpleName();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    @Override
    public void onStart() {
        super.onStart();
        action.onStart(this);
    }


    @Override
    public void onStop() {
        super.onStop();
        action.onStop(this);
    }

    //LOAD DATA
    public void loadData() {

    }

    @Override
    public String getAnalyticPageName() {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
