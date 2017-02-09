package com.hzpd.modle.event;

/**
 * 刷新当前新闻列表页
 */
public class RefreshEvent {
    public boolean force = false;

    public RefreshEvent() {

    }

    public RefreshEvent(boolean force) {
        this.force = force;
    }

}
