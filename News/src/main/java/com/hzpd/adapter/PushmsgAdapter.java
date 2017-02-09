package com.hzpd.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hzpd.hflt.R;
import com.hzpd.modle.PushmsgBean;
import com.hzpd.utils.DisplayOptionFactory;
import com.hzpd.utils.SPUtil;

public class PushmsgAdapter extends ListBaseAdapter<PushmsgBean> {

	public PushmsgAdapter(Activity activity) {
		super(activity);
	}

	@Override
	public View getMyView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;

		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.lehuo_list_item_layout, null);
			holder.lehuo_img_id = (ImageView) convertView.findViewById(R.id.lehuo_img_id);
			holder.lehuo_content_txt = (TextView) convertView.findViewById(R.id.lehuo_content_txt);
			holder.lehuo_sj_txt = (TextView) convertView.findViewById(R.id.lehuo_sj_txt);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		PushmsgBean pb = list.get(position);
		SPUtil.displayImage(pb.getSmallimgurl(), holder.lehuo_img_id
				, DisplayOptionFactory.Small.options);
		holder.lehuo_content_txt.setText(pb.getTitle());
		holder.lehuo_sj_txt.setText(pb.getTime());
		return convertView;
	}

	private static class ViewHolder {
		ImageView lehuo_img_id;
		TextView lehuo_content_txt;
		TextView lehuo_sj_txt;
	}


}