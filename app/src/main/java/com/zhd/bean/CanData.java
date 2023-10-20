package com.zhd.bean;

public class CanData {
    public int channel;
    public long id;
    public boolean id_extend;
    public boolean frame_remote;
    public byte[] data;

    public CanData(int channel, long id, boolean id_extend, boolean frame_remote, byte[] data) {
        this.channel = channel;
        this.id = id;
        this.id_extend = id_extend;
        this.frame_remote = frame_remote;
        this.data = data;

    }
}
