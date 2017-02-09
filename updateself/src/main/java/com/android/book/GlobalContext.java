package com.android.book;

import android.content.Context;

/**
 * Created by zhengnan on 2015/11/24.
 */
public class GlobalContext {
    public static Context ctx = null;
    public static void init(Context ctx){
            if(ctx!=null)GlobalContext.ctx = ctx.getApplicationContext();
    }
}
