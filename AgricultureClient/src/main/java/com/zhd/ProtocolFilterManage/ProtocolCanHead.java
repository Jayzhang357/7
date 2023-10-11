package com.zhd.ProtocolFilterManage;

import java.io.IOException;

/// <summary>
/// 表示消息头，支持序列化和反序列化方法
/// </summary>
public class ProtocolCanHead {
	// 消息ID
	public short MessageID;

	// 电话号码
	public byte[] PhoneNum = new byte[16];
	public short can_cartype;
	public short can_sign;

	// / <summary>
	// / 序列化成字节数组
	// / </summary>
	// / <returns></returns>
	public byte[] ToByteCode() {
		Exchange ex = new Exchange();
		ex.AddByte((byte)MessageID);
	
		ex.AddBytes(PhoneNum, false);
		ex.AddByte((byte)can_cartype);
		ex.AddByte((byte)can_sign);
		return ex.GetAllBytes();
	}

	
}