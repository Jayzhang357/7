package com.android.manager;
import com.android.manager.IMcuManagerListener;

interface IMcuManager
{
    boolean addMcuManagerListener(IMcuManagerListener listener);
    boolean removeMcuManagerListener(IMcuManagerListener listener);
    String getMcuVersion();
    boolean startMcuUpdate(String filename);
    byte getAccState();
    byte getBattery();
    String getVoltage();
    byte getCanBaud(byte channel);
    boolean setCanBaud(byte channel, byte baud);
    boolean sendCanData(byte channel, long id, boolean id_extend, boolean frame_remote, in byte[] data);
    boolean setSleepTime(long sec);
    long getSleepTime();
    boolean setPower(byte cmd);
    boolean setCanFilter(byte channel, long id, long mask);
    boolean cancelCanFilter(byte channel);
    byte getGpioState(byte gpio);
    boolean startMcuUpdaeByError(String filename);
}
