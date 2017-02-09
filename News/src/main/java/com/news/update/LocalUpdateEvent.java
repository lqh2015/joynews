package com.news.update;

/**
 * 本地更新通知
 */
public class LocalUpdateEvent extends Event {
    //已下载完成
    public boolean done = false;

    public LocalUpdateEvent(boolean bool) {
        done = bool;
    }
}
