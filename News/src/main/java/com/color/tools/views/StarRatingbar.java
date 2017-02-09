package com.color.tools.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.hzpd.hflt.R;

public class StarRatingbar extends LinearLayout {
	private int starNum ;
	private int totalNum ;
	
	public StarRatingbar(Context context) {
		super(context);
	}
	public StarRatingbar(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray typeArray = context.obtainStyledAttributes(attrs, R.styleable.StarRatingbar);
		starNum = typeArray.getInt(R.styleable.StarRatingbar_starNum, 5);
		totalNum = typeArray.getInt(R.styleable.StarRatingbar_totalNum, 5);
		typeArray.recycle();
		
		addStar();
	}
	private void addStar(){
		for(int i=0;i<totalNum;i++){
			ImageView imgv=new ImageView(getContext());
			if(i<starNum){
				imgv.setBackgroundResource(R.drawable.star_selected);
			}else{
				imgv.setBackgroundResource(R.drawable.star_unselected);
			}
			addView(imgv);
		}
	}
	
	public void setStar(int num){
		this.starNum=num;
		removeAllViews();
		addStar();
	}

	
}
