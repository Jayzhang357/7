package com.zhd.ProtocolFilterManage;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class Track_3g {
	public int malarm_sign;// 报警标记

	public int mLatitude; // 纬度
	public int mLongitude; // 经度
	public short mhight;// 高程
	public short mspeed;// 速度
	public short mdirection;// 方向
	public short mLenght;

	public String mTime = "";

	public byte[] ToByte() {
		Exchange ex = new Exchange();
	/*	ex.InttoBytes(malarm_sign);

		ex.state_toBytes(mstate, msorn, meorw);*/
		ex.InttoBytes(malarm_sign);
		ex.AddBytes(new byte[]{0x00 ,0x3C ,0x00 ,0x13},false);
		ex.InttoBytes(mLatitude);
		ex.InttoBytes(mLongitude);
		ex.ShorttoBytes(mhight);
		ex.ShorttoBytes(mspeed);
		ex.ShorttoBytes(mdirection);

		try {
			ex.AddBytes(BCDDecode.str2Bcd(mTime), false);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ex.ShorttoBytes(mLenght);

		return ex.GetAllBytes();
	}




}
