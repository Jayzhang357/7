package com.android.manager;

interface IMcuManagerListener {
    boolean onAccState(byte state);
    void onGpioState(int gpio, byte state);
    void onGSensor(int x, int y, int z);
    void onCanReceived(int channel, long id, boolean id_extend, boolean frame_remote, in byte[] data);
}
