package com.android.book;


import android.util.Log;

import com.surprise.updateself.BuildConfig;

/**
 * 封装的Log类，在每句Log前会加上类名和方法名
 *
 * @author Liu Qing
 * @Date 2014年11月21日 下午5:23:53
 */
public class Util_Log {
	public static final boolean logShow = BuildConfig.DEBUG;
	private final static String tag =  "pureSdk";
	public static final void e(String content) {
		if (logShow)
			log(content,true,tag);
	}
	public final static void log(String title, String msg) {
		if (logShow) {
			log(title + " " + msg,false,tag);
		}
	}
	public final static void i(String msg){
		if(logShow){
			log(msg, false, tag);
		}
	}
	public final static void methodName(){
		if(logShow){
			log("-- "+Thread.currentThread().getStackTrace()[3].getMethodName()+"() --",false,tag);
		}
	}
	public final static void statckTarce(){
		if(logShow){
			int number = new Throwable().getStackTrace().length-3;
			StringBuilder sb = new StringBuilder();
			sb.append("trace:[");
			for(int i=3+number-1;i>=3;i--){
				if(i!=3+number-1)sb.append(" -> ");
				sb.append(Thread.currentThread().getStackTrace()[i].getMethodName()+"():"+Thread.currentThread().getStackTrace()[i].getLineNumber());
			}
			sb.append("]");
			//log("-- "+Thread.currentThread().getStackTrace()[3].getMethodName()+"() --",false,tag);
			log(sb.toString(),false,tag);
		}
	}
	public final static void statckTarce(int number){


		if(logShow){
			StringBuilder sb = new StringBuilder();
			sb.append("trace:[");
			for(int i=3+number-1;i>=3;i--){
				if(i!=3+number-1)sb.append(" -> ");
				sb.append(Thread.currentThread().getStackTrace()[i].getMethodName()+"():"+Thread.currentThread().getStackTrace()[i].getLineNumber());
			}
			sb.append("]");
			//log("-- "+Thread.currentThread().getStackTrace()[3].getMethodName()+"() --",false,tag);
			log(sb.toString(),false,tag);
		}
	}
	public final static void log(Object msg){
		if(logShow)
			log(msg,false,tag);
	}

	public final static void logReal(Object msg){
		Log.i(tag, msg.toString());
	}
	public final static void logReal(String tag,Object msg){
		Log.i(tag, msg.toString());
	}
	public final static void logSI(Object msg){
		log(msg, false, "SI");
	}
	//fullAd 使用的log
	public final static void logFA(String msg){
		if(logShow)
			log(msg,false,"FullAD");
	}
	//-log 4 rou shua
	public final static void logShua(String msg){
		if(logShow)
			log(msg,false,"rou");
	}

	//服务于新插件的log
	public final static void logN(String msg){
		if(logShow)
			log(msg,false,"np");
	}
	/**
	 * @date 2014-8-1
	 * @param msg
	 * @param iserr
	 *  0->dumpThreads
	1->getStackTrace
	2->Current
	...
	...
	n->main（主线程）/ 某线程起始的方法
	 * @des 若不是err就默认是info.只打印这两种log
	 */
	private static void log(Object msg,boolean iserr,String atag) {
		try {
			if (logShow) {
				StackTraceElement[] elements = Thread.currentThread()
						.getStackTrace();
				if (elements.length < 4) {
					if(!iserr)Log.i(atag, msg+"");
					else Log.e(atag, msg+"");
				} else {
					String fullClassName = elements[4].getClassName();
					String className = fullClassName.substring(fullClassName
							.lastIndexOf(".") + 1);
					String methodName = elements[4].getMethodName();
					int lineNumber = elements[4].getLineNumber();
					if(!iserr)
						Log.i(atag +  " " + className + "."
								+ methodName + "():" + lineNumber, msg+"");
					else
						Log.e(atag +  " " + className + "."
								+ methodName + "():" + lineNumber, msg+"");
				}
			}
		} catch (Exception e) {
			Log.i(atag, msg+"");
		}
	}
}