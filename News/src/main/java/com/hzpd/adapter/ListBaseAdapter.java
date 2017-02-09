package com.hzpd.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.hzpd.utils.DBHelper;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public abstract class ListBaseAdapter<T> extends BaseAdapter {
	protected Activity context;
	protected LayoutInflater inflater;
	protected List<T> list = null;
	protected ImageLoader mImageLoader;
	protected DBHelper dbHelper;
	protected List<T> appendoldlist = null;

	public ListBaseAdapter(Activity context) {
		this.context = context;
		this.inflater = LayoutInflater.from(context);
		list = new ArrayList<T>();
		dbHelper = DBHelper.getInstance();
	}


	public List<T> getAdapterData() {
		return list;
	}

	// 封装加数据的方法--1条 多条
	public void appendData(T t, boolean isClearOld) {
		if (t == null) {
			return;
		}
		if (isClearOld) {
			list.clear();
		}
		list.add(t);
	}

	public void appendData(List<T> data, boolean isClearOld) {
		if (data == null) {
			return;
		}
		if (isClearOld) {
			list.clear();
		}
		appendoldlist = data;
		list.addAll(data);
	}

	// 封装加数据的方法--1条 多条 在顶部添加
	public void appendDataTop(T t, boolean isClearOld) {
		if (t == null)
			return;
		if (isClearOld)
			list.clear();
		list.add(0, t);
	}

	public void appendDataTop(List<T> data, boolean isClearOld) {
		if (data == null) {
			return;
		}
		if (isClearOld) {
			list.clear();
		}
		appendoldlist = data;
		list.addAll(0, data);
	}

	public void clear() {
		list.clear();
	}

	public void removeOld() {
		if (null != appendoldlist && appendoldlist.size() > 0) {
			list.removeAll(appendoldlist);
		}
	}

	@Override
	public int getCount() {
		if (null == list) {
			return 0;
		}
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		if (list == null) {
			return null;
		}
		if (position > list.size() - 1) {
			return null;
		}
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return getMyView(position, convertView, parent);
	}

	public abstract View getMyView(int position, View convertView,
	                               ViewGroup parent);

}
