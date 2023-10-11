package com.zhd.ProtocolFilterManage;

import java.util.ArrayList;

/// <summary>
/// 消息的基类
/// </summary>
public class ProtocolMessage_Can {
	public ProtocolCanHead mHead;
	public byte[] mProtocolContent;

	// / <summary>
	// / 构造一个消息
	// / </summary>
	// / <param name="hd"></param>
	// / <param name="protocolContent"></param>
	public ProtocolMessage_Can(ProtocolCanHead ph, byte[] protocolContent) {
		mHead = ph;
		mProtocolContent = protocolContent;
	}

	// / <summary>
	// / 序列化消息头，并将消息头和消息体组合起来
	// / </summary>
	// / <returns></returns>
	public ArrayList<Byte> ToByte() {
		ArrayList<Byte> al = new ArrayList<Byte>();
		byte[] tmp = mHead.ToByteCode();
		for (int i = 0; i < tmp.length; i++)
			al.add(tmp[i]);
		al.add((byte) 2);
		int sum_c = 0;
		if (mProtocolContent != null)

			sum_c =  mProtocolContent.length + 2;
		else
			sum_c = 2;
		byte[] abc = shortToByte((short) sum_c);
		al.add(abc[0]);
		al.add(abc[1]);
		if (mProtocolContent != null) {
			for (int i = 0; i < mProtocolContent.length; i++)
				al.add(mProtocolContent[i]);
		}
		return al;
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

}
