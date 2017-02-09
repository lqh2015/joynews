package com.color.tools.mytools;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.hzpd.hflt.R;

public class TUtil {
	public static void toast(Context context, int textRes) {
		if(null==context){
			return;
		}
		CharSequence text = context.getResources().getText(textRes);
		makeText(context, text).show();
	}

	public static void toast(Context context, CharSequence sequence) {
		if(null==context){
			return;
		}
		makeText(context, sequence).show();
	}

	public static Toast makeText(Context context, CharSequence text) {
		
		if(null==context){
			return null;
		}
		
		Toast result = new Toast(context);

		LayoutInflater inflate = LayoutInflater.from(context);
		View v = inflate.inflate(
				Res.getInstance(context).layout("color_toast"), null);
		result.setView(v);
		TextView tv = (TextView) v.findViewById(R.id.message);
		tv.setText(text);
		result.setGravity(17, 0, 0);
		result.setDuration(Toast.LENGTH_SHORT);

		return result;
	}
}
