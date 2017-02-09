package com.hzpd.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

public class MainPagerAdapter extends FragmentStatePagerAdapter {
	private ArrayList<Fragment> fragments;

	public MainPagerAdapter(FragmentManager fm) {
		super(fm);
		fragments = new ArrayList<Fragment>();
	}

	@Override
	public Fragment getItem(int position) {
		return fragments.get(position);
	}

	@Override
	public int getCount() {
		if (null != fragments) {
			return fragments.size();
		}
		return 0;
	}

	public void add(Fragment fragment) {
		fragments.add(fragment);
	}
}
