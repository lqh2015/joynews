package com.color.tools.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class ListViewInScrollView extends ListView {

	public ListViewInScrollView(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public ListViewInScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ListViewInScrollView(Context context) {
		super(context);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
		        MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}
	
	
	
	
}
