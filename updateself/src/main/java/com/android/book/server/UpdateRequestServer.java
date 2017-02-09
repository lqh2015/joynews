package com.android.book.server;

import android.content.Context;

import com.android.book.UpdateUtils;

public class UpdateRequestServer extends BaseRequestServer implements
		RequestServerInterface {

	public Context mContext;

	public UpdateRequestServer(Context context) {
		mContext = context;
	}

	@Override
	public String sendPostToServer() {
		return HttpUtil.sendPost(getRequestUrl(), getRequestData());
	}

	@Override
	public String sendGetToServer() {
		return HttpUtil.sendGet(getRequestUrl(), getRequestData());
	}

	@Override
	protected String getRequestUrl() {
		return UpdateUtils.UPDATE_URL;
	}

	@Override
	protected String getRequestData() {
		return RequestParams.initRequestParams(mContext, null);
	}

}
