package com.hzpd.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.hzpd.hflt.R;
import com.hzpd.utils.DisplayOptionFactory;
import com.hzpd.utils.SPUtil;
import com.hzpd.utils.ViewHolder;

public class VoteDetailMultiPicAdapter extends ListBaseAdapter<String> {

	public VoteDetailMultiPicAdapter(Activity context) {
		super(context);
	}

	@Override
	public View getMyView(int position, View convertView, ViewGroup parent) {
		if (null == convertView) {
			convertView = inflater.inflate(R.layout.vote_detail_pic_item, parent, false);
		}
		ImageView vote_detail_imge = ViewHolder.get(convertView, R.id.vote_detail_imge);
		String imgUrl = list.get(position);
		SPUtil.displayImage(imgUrl
				, vote_detail_imge, DisplayOptionFactory.Small.options);

		return convertView;
	}

}
