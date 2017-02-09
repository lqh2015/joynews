package com.hzpd.modle.event;

/**
 * Created by taoshuang on 2015/10/10.
 */
public class RestartEvent {
    public boolean isChangeCountry = false;

    public RestartEvent() {

    }

    public RestartEvent(boolean isChangeCountry) {
        this.isChangeCountry = isChangeCountry;
    }


}