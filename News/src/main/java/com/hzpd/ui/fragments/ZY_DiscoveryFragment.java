package com.hzpd.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hzpd.adapter.MainPagerAdapter;
import com.hzpd.hflt.R;
import com.hzpd.ui.App;
import com.hzpd.ui.activity.SearchActivity;
import com.hzpd.utils.AAnim;
import com.hzpd.utils.AnalyticUtils;
import com.hzpd.utils.AvoidOnClickFastUtils;

public class ZY_DiscoveryFragment extends BaseFragment {

    @Override
    public String getAnalyticPageName() {
        return AnalyticUtils.SCREEN.leftMenu;
    }

    private View main_top_search;

    private BaseFragment[] fragments;
    private ViewPager viewPager;
    private MainPagerAdapter adapter;
    /**
     * ViewPager的当前选中页
     */
    private int currentIndex = 0;
    private View[] tv_menu;
    private View coverTop;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = null;
        try {
            view = inflater.inflate(R.layout.zy_discoveryfragment, container, false);
            coverTop = view.findViewById(R.id.cover_top);
            if (App.getInstance().getThemeName().equals("0")) {
                coverTop.setVisibility(View.GONE);
            } else {
                coverTop.setVisibility(View.VISIBLE);
            }
            main_top_search = view.findViewById(R.id.main_top_search);
            main_top_search.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (AvoidOnClickFastUtils.isFastDoubleClick(v))
                        return;
                    Intent mIntent = new Intent();
                    mIntent.setClass(getActivity(), SearchActivity.class);
                    startActivity(mIntent);
                    AAnim.ActivityStartAnimation(getActivity());
                }
            });
            initLayout(view);
        } catch (Exception e) {

        }


        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    private void initLayout(View layout) {
        // TODO Auto-generated method stub
        viewPager = (ViewPager) layout.findViewById(R.id.rank_pager);
        adapter = new MainPagerAdapter(getActivity()
                .getSupportFragmentManager());
        fragments = new BaseFragment[2];
        fragments[0] = new ZY_DiscoveryItemFragment();
        fragments[1] = new ZY_ClassifyItemFragment();
        adapter.add(fragments[0]);
        adapter.add(fragments[1]);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(currentIndex);
        viewPager.setOnPageChangeListener(new MyOnPageChangeListener());
        viewPager.setOffscreenPageLimit(adapter.getCount());
        InitTextView(layout);
        viewPager.setCurrentItem(0);
        tv_menu[0].setSelected(true);
    }

    private void InitTextView(View layout) {
        tv_menu = new View[2];
        tv_menu[0] = layout.findViewById(R.id.tv_week);
        tv_menu[1] = layout.findViewById(R.id.tv_month);
        for (int i = 0; i < tv_menu.length; i++) {
            tv_menu[i].setOnClickListener(new MyOnClickListener(i));
        }

    }

    private class MyOnClickListener implements View.OnClickListener {
        private int index = 0;

        public MyOnClickListener(int i) {
            index = i;
        }

        public void onClick(View v) {
            viewPager.setCurrentItem(index);
        }

    }

    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        public void onPageScrollStateChanged(int arg0) {

        }

        public void onPageScrolled(int position, float offset, int offsetPixels) {
        }

        public void onPageSelected(int position) {
            resetTextView();
            tv_menu[position].setSelected(true);
            currentIndex = position;
        }
    }

    private void resetTextView() {
        for (View textView : tv_menu) {
            textView.setSelected(false);
        }
    }

}