package com.hzpd.adapter.editcolumn;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hzpd.hflt.R;
import com.hzpd.modle.NewsChannelBean;

import java.util.Collections;
import java.util.List;

public class DragAdapter extends BaseAdapter {
	private List<NewsChannelBean> titleData;
	private LayoutInflater mInflater;
	public static final int hiddenNum = 0;
	private int mHidePosition = -1;

	public DragAdapter(Context context, List<NewsChannelBean> titleData) {
		this.titleData = titleData;
		mInflater = LayoutInflater.from(context);
	}

	public void setList(List<NewsChannelBean> titleData) {
		this.titleData = titleData;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		if (titleData.size() < hiddenNum) {
			return 0;
		}
		return titleData.size() - hiddenNum;
	}

	@Override
	public Object getItem(int position) {
		return titleData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	boolean animFlag = false;

	public void setAnim(boolean animFlag) {
		this.animFlag = animFlag;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		int p = position + hiddenNum;
		convertView = mInflater.inflate(R.layout.channel_item, parent, false);
		TextView txtAge = (TextView) convertView.findViewById(R.id.text_item);

		NewsChannelBean stb = titleData.get(p);
		txtAge.setText(stb.getCnname());

		if (p == titleData.size() - 1 && animFlag) {
			convertView.setVisibility(View.INVISIBLE);
		} else {
			convertView.setVisibility(View.VISIBLE);
		}

		return convertView;
	}


	public void reorderItems(int oldPosition, int newPosition) {
		oldPosition += hiddenNum;
		newPosition += hiddenNum;
		NewsChannelBean temp = titleData.get(oldPosition);
		if (oldPosition < newPosition) {
			for (int i = oldPosition; i < newPosition; i++) {
				Collections.swap(titleData, i, i + 1);
			}
		} else if (oldPosition > newPosition) {
			for (int i = oldPosition; i > newPosition; i--) {
				Collections.swap(titleData, i, i - 1);
			}
		}
		titleData.set(newPosition, temp);
	}

	public void setHideItem(int hidePosition) {
		this.mHidePosition = hidePosition;
		notifyDataSetChanged();
	}

}
