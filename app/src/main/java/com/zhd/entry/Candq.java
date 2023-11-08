package com.zhd.entry;

import com.zhd.AppHelper;

public class Candq {
    public int ts=0;
    public int xq=0;
    public int qq=0;
    public int hy=0;
    public int zk=0;
    public int yk=0;
    public int xt=0;
    public int ts_speed=0;
    public int xq_speed=0;
    public int qq_speed=0;
    public int hy_speed=0;
    public int zk_speed=0;
    public int yk_speed=0;

    public String setCommand()
    {
        String a0 =  AppHelper.get8(ts+xq*2+qq*4+hy*8+zk*16+yk*32+xt*64
        );
        String a1 =  AppHelper.get8(ts_speed);
        String a2 =  AppHelper.get8(xq_speed);
        String a3 =  AppHelper.get8(qq_speed);
        String a4 =  AppHelper.get8(hy_speed);
        String a5 =  AppHelper.get8(zk_speed);
        String a6 =  AppHelper.get8(yk_speed);
        return  a0+a1+a2+a3+a4+a5+a6;
    }


}
