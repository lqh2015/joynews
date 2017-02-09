package com.hzpd.adapter;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.hzpd.modle.NewsChannelBean;
import com.hzpd.ui.fragments.BaseFragment;
import com.hzpd.ui.fragments.ChooseFragment;
import com.hzpd.ui.fragments.NewsItemFragment;

import java.util.ArrayList;
import java.util.List;

public class NewsFragmentPagerAdapter extends FragmentPagerAdapter {
    List<NewsChannelBean> saveTitleList = new ArrayList<>();
    int selectedPosition = 0;
    BaseFragment selectedFragment;

    public NewsFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }


    public void sortChannel(List<NewsChannelBean> saveTitleList) {
        if (saveTitleList != null) {
            this.saveTitleList.clear();
            this.saveTitleList.addAll(saveTitleList);
        }
        notifyDataSetChanged();
    }

    @Override
    public BaseFragment getItem(int position) {
        return getChannelFragment(saveTitleList.get(position));
    }

    @Override
    public long getItemId(int position) {
        NewsChannelBean bean = saveTitleList.get(position);
        return ("tid:" + bean.getTid() + "#" + "tagid:" + bean.getId()).hashCode();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return saveTitleList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        NewsChannelBean bean = saveTitleList.get(position);
        String ti = bean.getCnname();
        return ti;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return super.isViewFromObject(view, object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        return super.instantiateItem(container, position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }

    boolean changed = false;

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
        changed = true;
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
        if (changed) {
            changed = false;
            ((BaseFragment) object).loadData();
        }
    }

    BaseFragment getChannelFragment(NewsChannelBean ncb) {
        BaseFragment fragment = null;
        switch (ncb.getType()) {
            case NewsChannelBean.TYPE_RECOMMEND:
                fragment = new ChooseFragment(ncb);
                break;
            default:
                fragment = new NewsItemFragment(ncb);
                break;
        }
        return fragment;
    }
}