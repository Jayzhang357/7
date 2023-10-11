package com.zhd.BussinessMessage;

import java.io.UnsupportedEncodingException;


import android.util.Log;

import com.zhd.ProtocolFilterManage.*;

public class AgLandInfos extends GeneralBMessage {

    Zhuce zhuce;
    Jianquan jianquan;
    short num = 0;
    Track_3g track_3g;
    String SN_num = "";


    public AgLandInfos(Track_3g pt, String sn_num, short num_t) {
        num = num_t;
        track_3g = pt;
        SN_num = sn_num;
    }
    public AgLandInfos(Jianquan pt, String sn_num, short num_t) {
        num = num_t;
        jianquan = pt;
        SN_num = sn_num;
    }
    public AgLandInfos(Zhuce pt, String sn_num, short num_t) {
        num = num_t;
        zhuce = pt;
        SN_num = sn_num;
    }
    public ProtocolMessage[] ToProtocolMessage_Track_3g() {
        ProtocolHead ph = new ProtocolHead();
        ph.MessageID = 0x0200;
        ph.MessageSerialNum=num;
        try {
            ph.PhoneNum = BCDDecode.str2Bcd(SN_num);
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        ph.lenght=(short)track_3g.ToByte().length;
        ProtocolMessage[] pm = new ProtocolMessage[1];

        pm[0] = new ProtocolMessage(ph, track_3g.ToByte());

        return pm;
    }

    public ProtocolMessage[] ToProtocolMessage_Jianquan() {
        ProtocolHead ph = new ProtocolHead();
        ph.MessageID = 0x0102;
        ph.MessageSerialNum=num;
        try {
            ph.PhoneNum = BCDDecode.str2Bcd(SN_num);
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        ph.lenght=(short)jianquan.ToByte().length;
        ProtocolMessage[] pm = new ProtocolMessage[1];

        pm[0] = new ProtocolMessage(ph, jianquan.ToByte());

        return pm;
    }

    public ProtocolMessage[] ToProtocolMessage_Zhuce() {
        ProtocolHead ph = new ProtocolHead();
        ph.MessageID = 0x0100;
        ph.MessageSerialNum=num;
        try {
            ph.PhoneNum = BCDDecode.str2Bcd(SN_num);
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        ph.lenght=(short)zhuce.ToByte().length;
        ProtocolMessage[] pm = new ProtocolMessage[1];

        pm[0] = new ProtocolMessage(ph, zhuce.ToByte());
        //  Log.e("数据长度", track.ToByte().length+"");
        return pm;
    }



}

