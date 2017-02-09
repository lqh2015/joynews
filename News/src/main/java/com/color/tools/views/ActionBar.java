package com.color.tools.views;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.color.tools.mytools.Res;

public class ActionBar extends RelativeLayout {
	private ImageView leftImg;
	private ImageView titleImg;
	private TextView  titleTv;
	private ImageView rightImg;
	private TextView  rightTv;
	
	private ActionBarListener leftListener;
	private ActionBarListener rightListener;
	private ActionBarListener titleListener;
	
	public ActionBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}

	public ActionBar(Context context) {
		super(context);
		initView(context);
	}

	public ActionBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	private void initView(Context context) {
		LayoutInflater.from(context).inflate(
				Res.getInstance(getContext()).layout("color_action_bar"), this);
		this.leftImg = ((ImageView) findViewById(Res
				.getInstance(getContext()).id("color_ab_leftImg")));
		this.titleImg = ((ImageView) findViewById(Res
				.getInstance(getContext()).id("color_ab_titleImg")));
		this.titleTv = ((TextView) findViewById(Res
				.getInstance(getContext()).id("color_ab_titleTv")));
		this.rightImg = ((ImageView) findViewById(Res
				.getInstance(getContext()).id("color_ab_RightImg")));
		this.rightTv = ((TextView) findViewById(Res
				.getInstance(getContext()).id("color_ab_rightTv")));
		
		this.leftImg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(null!=leftListener){
					leftListener.onclick();
				}
			}
		});
		this.titleImg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(null!=titleListener){
					titleListener.onclick();
				}
			}
		});
		this.titleTv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(null!=titleListener){
					titleListener.onclick();
				}
			}
		});
		this.rightImg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(null!=rightListener){
					rightListener.onclick();
				}
			}
		});
		this.rightTv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(null!=rightListener){
					rightListener.onclick();
				}
			}
		});
	}
	
	
	
	public void setLeftImgRes(int resId){
		if(-1==resId){
			leftImg.setVisibility(View.GONE);
		}else{
			leftImg.setVisibility(View.VISIBLE);
			leftImg.setImageResource(resId);
		}
	}
	
	public void setTitleString(String content){
		setTitleString(content, Color.parseColor("#ffffff"));
	}
	
	public void setTitleString(String content,int textColor){
		if(null==content){
			titleImg.setVisibility(View.GONE);
			titleTv.setVisibility(View.GONE);
		}else{
			titleImg.setVisibility(View.GONE);
			titleTv.setVisibility(View.VISIBLE);
			titleTv.setText(content);
			titleTv.setTextColor(textColor);
		}
	}
	
	
	public void setTitleImgRes(int resId){
		if(-1==resId){
			titleImg.setVisibility(View.GONE);
			titleTv.setVisibility(View.GONE);
		}else{
			titleImg.setVisibility(View.VISIBLE);
			titleTv.setVisibility(View.GONE);
			titleImg.setImageResource(resId);
		}
	}
	
	public void setRightString(String content){
		setRightString(content, Color.parseColor("#444444"));
	}
	
	public void setRightString(String content,int textColor){
		if(null==content){
			rightImg.setVisibility(View.GONE);
			rightTv.setVisibility(View.GONE);
		}else{
			rightImg.setVisibility(View.GONE);
			rightTv.setVisibility(View.VISIBLE);
			rightTv.setText(content);
			rightTv.setTextColor(textColor);
		}
	}
	
	public void setRightImgRes(int resId){
		if(-1==resId){
			rightImg.setVisibility(View.GONE);
			rightTv.setVisibility(View.GONE);
		}else{
			rightImg.setVisibility(View.VISIBLE);
			rightTv.setVisibility(View.GONE);
			rightImg.setImageResource(resId);
		}
	}
	
	public View getLeftView(){
		if(View.VISIBLE==leftImg.getVisibility()){
			return leftImg;
		}
		return null;
	}
	
	public View getTitleView(){
		if(View.VISIBLE==titleImg.getVisibility()){
			return titleImg;
		}else if(View.VISIBLE==titleTv.getVisibility()){
			return titleTv;
		}
		return null;
	}
	
	public View getRightView(){
		if(View.VISIBLE==rightImg.getVisibility()){
			return rightImg;
		}else if(View.VISIBLE==rightTv.getVisibility()){
			return rightTv;
		}
		return null;
	}
	
	public void setLeftListener(ActionBarListener mleftlistener){
		this.leftListener=mleftlistener;
	}
	public void setRightListener(ActionBarListener mrightlistener){
		this.rightListener=mrightlistener;
	}
	public void setTitleListener(ActionBarListener mtitlelistener){
		this.titleListener=mtitlelistener;
	}
	
}


