package com.zhd.ProtocolFilterManage;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class Getip_31 {

	public int cout_num;
	public String IME_ID;
	public byte [] Token;
	public byte[] ToByte() {
		Exchange ex = new Exchange();
		ex.AddByte((byte) 0xAA);
		ex.AddByte((byte) 0x55);
		ex.AddIntAsBytes(cout_num);
	
		ex.AddByte((byte) 0x00);
		ex.AddByte((byte) 0xC1);
		
		ex.AddByte((byte) 0x01);

		ex.AddBytes(ex.StringtoBytes(IME_ID), false);

		ex.AddByte((byte) 0x23);
		ex.AddBytes(Token, false);
		ex.AddByte((byte) 0x00);
		ex.AddByte((byte) 0x00);
		byte[] abc = ex.GetAllBytes();
		ex = new Exchange();
		ex.AddBytes(abc, false);
		ex.AddBytes(getCRC16(abc), false);
		ex.AddByte((byte) 0x40);
		ex.AddByte((byte) 0x40);
		ex.AddByte((byte) 0x24);
		ex.AddByte((byte) 0x24);
		return ex.GetAllBytes();
	}

	public static byte[] getCRC16(byte[] bytes) {
		// CRC寄存器全为1
		int CRC = 0x0000ffff;
		// 多项式校验值
		int POLYNOMIAL = 0x0000a001;
		int i, j;
		for (i = 0; i < bytes.length; i++) {
			CRC ^= ((int) bytes[i] & 0x000000ff);
			for (j = 0; j < 8; j++) {
				if ((CRC & 0x00000001) != 0) {
					CRC >>= 1;
					CRC ^= POLYNOMIAL;
				} else {
					CRC >>= 1;
				}
			}
		}
		// 结果转换为16进制
		String result = Integer.toHexString(CRC).toUpperCase();
		if (result.length() != 4) {
			StringBuffer sb = new StringBuffer("0000");
			result = sb.replace(4 - result.length(), 4, result).toString();
		}
		String a1 = result.substring(2, 4);
		int aa = Integer.parseInt(a1, 16);
		String a2 = result.substring(0, 2);
		int bb = Integer.parseInt(a2, 16);
		byte[] qq = new byte[] { (byte) aa, (byte) bb

		};
		// 高位在前地位在后
		// return result.substring(2, 4) + " " + result.substring(0, 2);
		// 交换高低位，低位在前高位在后
		return qq;
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
		result[0] = (byte) ((i >> 24) & 0xFF);
		result[1] = (byte) ((i >> 16) & 0xFF);
		result[2] = (byte) ((i >> 8) & 0xFF);
		result[3] = (byte) (i & 0xFF);
		return result;
	}

}
