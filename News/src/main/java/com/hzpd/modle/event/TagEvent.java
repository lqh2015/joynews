package com.hzpd.modle.event;

import com.hzpd.modle.TagBean;

/**
 * Created by taoshuang on 2015/12/16.
 */
public class TagEvent {
   public TagBean bean;
    public TagEvent(TagBean bean){
        this.bean=bean;
    }
}
