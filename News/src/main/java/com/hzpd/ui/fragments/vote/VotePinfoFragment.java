package com.hzpd.ui.fragments.vote;

import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.hzpd.adapter.VoteDetailMultiPicAdapter;
import com.hzpd.hflt.R;
import com.hzpd.modle.vote.Vote_detailMultiPicBean;
import com.hzpd.ui.fragments.BaseFragment;
import com.hzpd.url.OkHttpClientManager;
import com.hzpd.utils.MyCommonUtil;

public class VotePinfoFragment extends BaseFragment {

	private TextView vote_detail_tv_right;
	private TextView vote_detail_tv_bottom;
	private ListView vote_detail_lv;
	private Paint paint = new Paint();
	private Vote_detailMultiPicBean bean;
	private VoteDetailMultiPicAdapter adapter;
	private String androidId;    //
	private String newsid;        //
	private String optid;
	private String subjectid;
	private String isRadio;
	private String actionname;
	private Object tag;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.vote_detail_multipic_layout, container, false);
		vote_detail_tv_right= (TextView) view.findViewById(R.id.vote_detail_tv_right);
		vote_detail_tv_bottom= (TextView) view.findViewById(R.id.vote_detail_tv_bottom);
		vote_detail_lv= (ListView) view.findViewById(R.id.vote_detail_lv);
		tag= OkHttpClientManager.getTag();
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		Bundle args = getArguments();
		if (null == args) {
			return;
		}
		newsid = args.getString("newsid");
		optid = args.getString("optionid");
		subjectid = args.getString("subjectid");
		isRadio = args.getString("isRadio");
		actionname = args.getString("actionname");
		androidId = MyCommonUtil.getMyUUID(activity);
		adapter = new VoteDetailMultiPicAdapter(activity);
		vote_detail_lv.setAdapter(adapter);
		getInfoFromServer();

	}


	private void getInfoFromServer() {}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		OkHttpClientManager.cancel(tag);
	}
}