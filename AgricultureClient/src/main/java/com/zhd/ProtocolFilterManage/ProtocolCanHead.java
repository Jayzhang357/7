package com.zhd.ProtocolFilterManage;

import java.io.IOException;

/// <summary>
/// ��ʾ��Ϣͷ��֧�����л��ͷ����л�����
/// </summary>
public class ProtocolCanHead {
	// ��ϢID
	public short MessageID;

	// �绰����
	public byte[] PhoneNum = new byte[16];
	public short can_cartype;
	public short can_sign;

	// / <summary>
	// / ���л����ֽ�����
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