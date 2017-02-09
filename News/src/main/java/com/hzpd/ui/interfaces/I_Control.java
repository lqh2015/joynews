package com.hzpd.ui.interfaces;

import com.alibaba.fastjson.JSONObject;

public interface I_Control {

	public void getDbList();

	public void getServerList(String ids);

	public void setData(JSONObject obj);
}
