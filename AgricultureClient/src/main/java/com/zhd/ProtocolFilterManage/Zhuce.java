package com.zhd.ProtocolFilterManage;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class Zhuce {



	public byte[] ToByte() {
		Exchange ex = new Exchange();
		byte [] abc=new byte[]{0x00, 0x02C , 0x001 , 0x02C , 0x053 , 0x04D , 0x041 , 0x052
				, 0x054 , 0x073 , 0x06D , 0x061 , 0x072 , 0x074 , 0x031 , 0x000 , 0x000 , 0x000
				, 0x000 , 0x000 , 0x000 , 0x000 , 0x000 , 0x000 , 0x000 , 0x000 , 0x000 , 0x000
				, 0x000 , 0x031 , 0x032 , 0x033 , 0x034 , 0x035 , 0x000 , 0x000 , 0x001 , 0x041 , 0x042 , 0x041
				, 0x058 , 0x058 , 0x058 , 0x058 , 0x058};
		ex.AddBytes(abc, false);

		return ex.GetAllBytes();
	}

	public static byte[] getCRC16(byte[] bytes) {
		// CRC�Ĵ���ȫΪ1
		int CRC = 0x0000ffff;
		// ����ʽУ��ֵ
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
		// ���ת��Ϊ16����
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
		// ��λ��ǰ��λ�ں�
		// return result.substring(2, 4) + " " + result.substring(0, 2);
		// �����ߵ�λ����λ��ǰ��λ�ں�
		return qq;
	}

	public static byte[] shortToByte(short number) {
		int temp = number;
		byte[] b = new byte[2];
		for (int i = 0; i < b.length; i++) {
			b[i] = new Integer(temp & 0xff).byteValue();//

			temp = temp >> 8; // ������8λ
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
