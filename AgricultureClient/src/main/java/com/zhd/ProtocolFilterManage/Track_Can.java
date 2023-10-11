package com.zhd.ProtocolFilterManage;

import java.io.IOException;

public class Track_Can {

	public int Latitude; // 纬度
	public int Longitude; // 经度

	public short speed;// 速度
	public short direction;// 方向
	public short hight;// 高程
	public String Time;
	public short CaculateSatNum;
	public short CaculateSeeNum;
	public short PDOP;
	public short GPSsate;

	public byte[] ToByte() {
		Exchange ex = new Exchange();

		ex.AddByte((byte) 0x00);
		ex.AddByte((byte) 0x01);
		ex.AddBytes(shortToByte((short) 25), false);
		ex.AddBytes(intToByteArray(Latitude), false);
		ex.AddBytes(intToByteArray(Longitude), false);
	
		ex.AddBytes(shortToByte(speed), false);
		ex.AddBytes(shortToByte( direction), false);
		ex.AddBytes(shortToByte( hight), false);

		byte[] qq = new byte[6];
		qq[0] = (byte) Integer.parseInt(Time.substring(0, 2));
		qq[1] = (byte) Integer.parseInt(Time.substring(2, 4));
		qq[2] = (byte) Integer.parseInt(Time.substring(4, 6));
		qq[3] = (byte) Integer.parseInt(Time.substring(6, 8));
		qq[4] = (byte) Integer.parseInt(Time.substring(8, 10));
		qq[5] = (byte) Integer.parseInt(Time.substring(10, 12));
		ex.AddBytes(qq, false);
		ex.AddByte((byte) CaculateSatNum);
		ex.AddByte((byte) CaculateSeeNum);
		ex.AddBytes(shortToByte(PDOP), false);

		ex.AddByte((byte) GPSsate);
		return ex.GetAllBytes();
	}

	public static byte[] shortToByte(short number) {
		int temp = number;
		byte[] b = new byte[2];
		for (int i = 0; i < b.length; i++) {
			b[i] = new Integer(temp & 0xff).byteValue();//

			temp = temp >> 8; // 向右移8位
		}
		byte get = b[0];

		return b;
	}

	public static byte[] intToByteArray(int i) {
		byte[] result = new byte[4];
		result[3] = (byte) ((i >> 24) & 0xFF);
		result[2] = (byte) ((i >> 16) & 0xFF);
		result[1] = (byte) ((i >> 8) & 0xFF);
		result[0] = (byte) (i & 0xFF);
		return result;
	}

}
