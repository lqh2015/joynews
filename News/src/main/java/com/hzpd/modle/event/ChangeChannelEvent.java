package com.hzpd.modle.event;

/**
 * 改变频道
 */
public class ChangeChannelEvent {
    public ChannelSortedList csl;
    public int position;

    public ChangeChannelEvent(ChannelSortedList list, int position) {
        csl = list;
        this.position = position;
    }
}
