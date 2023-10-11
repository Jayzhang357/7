package com.zhd.commonhelper;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

import android.R.integer;
import android.util.Log;

import com.zhd.gps.manage.models.GpsEnum;

public class GpsHelper {
	public static double toDouble(String str) {
		if (str == null || str.length() == 0) {
			return 0.0;
		}
		try {
			if (str.startsWith("0")) {
				str = str.substring(1);
			}
			return Double.parseDouble(str);
		} catch (Exception e) {
			// TODO: handle exception
			return 0.0;
		}
	}

	public static double todegree(String str) {
		if (str == null || str.length() == 0) {
			return 0.0;
		}
		try {
			String data[] = str.split("\\.");
			String degreeStr = "";
			String minuteStr = "";
			if (data[0].length() == 4) {
				degreeStr = str.substring(0, 2);
				minuteStr = str.substring(2);
			} else if (data[0].length() == 5) {
				degreeStr = str.substring(0, 3);
				minuteStr = str.substring(3);
			}
			int degree = Integer.parseInt(degreeStr);
			double minute = Double.parseDouble(minuteStr);
			return degree + minute / 60;
		}

		catch (Exception e) {
			return 0.0;
		}
	}

	public static double toLdegree(String str) {
		if (str == null || str.length() == 0) {
			return 0.0;
		}
		try {
			String degreeStr = str.substring(0, 2);
			if (degreeStr.startsWith("0")) {
				degreeStr = str.substring(1);
			}
			int degree = Integer.parseInt(degreeStr);

			String miniteStr = str.substring(3);
			if (miniteStr.startsWith("0")) {
				miniteStr = miniteStr.substring(1);
			}

			double minite = Double.parseDouble(miniteStr);
			return degree + minite / 60;
		} catch (Exception e) {
			// TODO: handle exception
			return 0.0;
		}
	}

	public static double toBdegree(String str) {

		if (str == null || str.length() == 0) {
			return 0.0;
		}
		try {
			String degreeStr = str.substring(0, 3);
			if (degreeStr.startsWith("0")) {
				degreeStr = str.substring(1);
			}
			if (degreeStr.startsWith("0")) {
				degreeStr = str.substring(1);
			}

			int degree = Integer.parseInt(degreeStr);

			String miniteStr = str.substring(4);
			if (miniteStr.startsWith("0")) {
				miniteStr = miniteStr.substring(1);
			}
			double minite = Double.parseDouble(miniteStr);
			return degree + minite / 60;
		} catch (Exception e) {
			// TODO: handle exception
			return 0.0;
		}
	}

	/**
	 * 字符串转换为Int
	 *
	 * @param str
	 * @return
	 */
	public static int toInt(String str) {
		if (str == null || str.length() == 0) {
			return 0;
		}
		try {
			while (str.startsWith("0")) {
				str = str.substring(1);
			}
			return Integer.parseInt(str);
		} catch (Exception e) {
			// TODO: handle exception
			return 0;
		}

	}

	/**
	 * 字符串转换为Float
	 *
	 * @param str
	 * @return
	 */
	public static float toFloat(String str) {
		if (str == null || str.length() == 0) {
			return 0.0f;
		}
		try {
			while (str.startsWith("0")) {
				str = str.substring(1);
			}

			return Float.parseFloat(str);
		} catch (Exception e) {
			// TODO: handle exception
			return 0.0f;
		}
	}

	public static int GetStandardPrn(int prn) {
		if (prn >= 38 && prn <= 61) // 65 to 96
		{
			return prn + 27;
		} else if (prn >= 120 && prn <= 138) // 33 to 64
		{
			// 不减87
			// return prn -= 87;
		}
		return prn;
	}

	public static int GetGpsTypeForUB280(int prn) {
		int prnType = 0;

		if (prn >= 1 && prn <= 32)
			prnType = GpsEnum.GpsType.GPS.getValue();
		else if (prn >= 38 && prn <= 62) // 65 to 96
			prnType = GpsEnum.GpsType.GLONASS.getValue();
		else if (prn >= 161 && prn <= 197) // 33 to 64
			prnType = GpsEnum.GpsType.BD.getValue();

		return prnType;
	}

	public static int GetGpsTypeForNovatel(int prn) {
		int prnType = 0;

		if (prn >= 1 && prn <= 32)
			prnType = GpsEnum.GpsType.GPS.getValue();
		else if (prn >= 38 && prn <= 61) // 65 to 96
			prnType = GpsEnum.GpsType.GLONASS.getValue();
		else if (prn >= 120 && prn <= 138) // 33 to 64
			prnType = GpsEnum.GpsType.BD.getValue();

		return prnType;
	}

	// char转byte

	public static byte[] getBytes(char[] chars) {
		// Charset cs = Charset.forName ("UTF-8");
		// CharBuffer cb = CharBuffer.allocate (chars.length);
		// cb.put (chars);
		// cb.flip ();
		// ByteBuffer bb = cs.encode (cb);
		//
		// return bb.array();
		String str = new String(chars);
		byte[] bs = str.getBytes();// 转换搜索过来的byte数组 不过介于你初学 建议你用第2中方式
		/***** 第2中方式 ******/
		return bs;

	}

	// byte转char

	public static char[] getChars(byte[] bytes) {
		Charset cs = Charset.forName("UTF-8");
		ByteBuffer bb = ByteBuffer.allocate(bytes.length);
		bb.put(bytes);
		bb.flip();
		CharBuffer cb = cs.decode(bb);

		return cb.array();
	}

	public static int[] getNcellnum(byte[] message, int first, int tpye) {
		int Nsat = 0;
		int Nsig = 0;
		int capability = 0;
		if (tpye == 2) {
			for (int i = first; i < first + 8; i++) {
				if ((message[i] & 0x01) == 1)
					Nsat++;
				if ((message[i] & 0x03) >> 1 == 1)
					Nsat++;
				if ((message[i] & 0x07) >> 2 == 1)
					Nsat++;
				if ((message[i] & 0x0F) >> 3 == 1)
					Nsat++;
				if ((message[i] & 0x10) >> 4 == 1)
					Nsat++;
				if ((message[i] & 0x30) >> 5 == 1)
					Nsat++;
				if ((message[i] & 0x70) >> 6 == 1)
					Nsat++;
				if ((message[i] & 0xFF) >> 7 == 1)
					Nsat++;
			}
			for (int i = first + 8; i < first + 8 + 4; i++) {
				if ((message[i] & 0x01) == 1)
					Nsig++;
				if ((message[i] & 0x03) >> 1 == 1)
					Nsig++;
				if ((message[i] & 0x07) >> 2 == 1)
					Nsig++;
				if ((message[i] & 0x0F) >> 3 == 1)
					Nsig++;
				if ((message[i] & 0x10) >> 4 == 1)
					Nsig++;
				if ((message[i] & 0x30) >> 5 == 1)
					Nsig++;
				if ((message[i] & 0x70) >> 6 == 1)
					Nsig++;
				if ((message[i] & 0xFF) >> 7 == 1)
					Nsig++;
			}
		} else {
			for (int i = first; i < first + 5; i++) {
				if ((message[i] & 0x01) == 1)
					Nsat++;
				if ((message[i] & 0x03) >> 1 == 1)
					Nsat++;
				if ((message[i] & 0x07) >> 2 == 1)
					Nsat++;
				if ((message[i] & 0x0F) >> 3 == 1)
					Nsat++;
				if ((message[i] & 0x10) >> 4 == 1)
					Nsat++;
				if ((message[i] & 0x30) >> 5 == 1)
					Nsat++;
				if ((message[i] & 0x70) >> 6 == 1)
					Nsat++;
				if ((message[i] & 0xFF) >> 7 == 1)
					Nsat++;
			}
			for (int i = first + 5; i < first + 5 + 3; i++) {
				if ((message[i] & 0x01) == 1)
					Nsig++;
				if ((message[i] & 0x03) >> 1 == 1)
					Nsig++;
				if ((message[i] & 0x07) >> 2 == 1)
					Nsig++;
				if ((message[i] & 0x0F) >> 3 == 1)
					Nsig++;
				if ((message[i] & 0x10) >> 4 == 1)
					Nsig++;
				if ((message[i] & 0x30) >> 5 == 1)
					Nsig++;
				if ((message[i] & 0x70) >> 6 == 1)
					Nsig++;
				if ((message[i] & 0xFF) >> 7 == 1)
					Nsig++;
			}
		}
		int[] abc = new int[2];
		abc[0] = Nsat;
		abc[1] = Nsat * Nsig;
		return abc;
	}

	// 返回1
	public static int getOnenum(int[] message, int first, int lenght) {
		int Nsat = 0;
		for (int i = first; i < first + lenght; i++) {
			if (message[i] == 1)
				Nsat++;
		}
		return Nsat;
	}

	public static int getOnenum_b(byte[] message, int first, int lenght) {
		int Nsat = 0;
		int left_s = lenght / 8;
		int left_l = lenght % 8;
		for (int i = first; i < first + left_s; i++) {
			if ((message[i] & 0x01) == 1)
				Nsat++;
			if ((message[i] & 0x03) >> 1 == 1)
				Nsat++;
			if ((message[i] & 0x07) >> 2 == 1)
				Nsat++;
			if ((message[i] & 0x0F) >> 3 == 1)
				Nsat++;
			if ((message[i] & 0x10) >> 4 == 1)
				Nsat++;
			if ((message[i] & 0x30) >> 5 == 1)
				Nsat++;
			if ((message[i] & 0x70) >> 6 == 1)
				Nsat++;
			if ((message[i] & 0xFF) >> 7 == 1)
				Nsat++;
		}

		if ((message[first + left_s] & 0xFF) >> 7 == 1 && left_l > 0)
			Nsat++;
		if ((message[first + left_s] & 0x70) >> 6 == 1 && left_l > 1)
			Nsat++;
		if ((message[first + left_s] & 0x30) >> 5 == 1 && left_l > 2)
			Nsat++;
		if ((message[first + left_s] & 0x10) >> 4 == 1 && left_l > 3)
			Nsat++;
		if ((message[first + left_s] & 0x0F) >> 3 == 1 && left_l > 4)
			Nsat++;
		if ((message[first + left_s] & 0x07) >> 2 == 1 && left_l > 5)
			Nsat++;
		if ((message[first + left_s] & 0x03) >> 1 == 1 && left_l > 6)
			Nsat++;

		return Nsat;
	}

	// 返回1
	public static int[] getGpsType(byte[] message, int first) {
		int Nsat = 0;
		int[] GPSTemp = new int[8];
		if ((message[first] & 0x01) == 1) {
			Nsat++;
			GPSTemp[7] = 1;
		}
		if ((message[first] & 0x03) >> 1 == 1) {
			Nsat++;
			GPSTemp[6] = 1;
		}
		if ((message[first] & 0x07) >> 2 == 1) {
			Nsat++;
			GPSTemp[5] = 1;
		}
		if ((message[first] & 0x0F) >> 3 == 1) {
			Nsat++;
			GPSTemp[4] = 1;
		}
		if ((message[first] & 0x10) >> 4 == 1) {
			Nsat++;
			GPSTemp[3] = 1;
		}
		if ((message[first] & 0x30) >> 5 == 1) {
			Nsat++;
			GPSTemp[2] = 1;
		}
		if ((message[first] & 0x70) >> 6 == 1) {
			Nsat++;
			GPSTemp[1] = 1;
		}
		if ((message[first] & 0xFF) >> 7 == 1) {
			Nsat++;
			GPSTemp[0] = 1;
		}

		return GPSTemp;
	}

	public static int[] returnBYTE(byte[] message, int first) {
		int[] abc = new int[(message.length - first) * 8];
		for (int i = first; i < message.length; i++) {
			abc[(i - first) * 8 + 7] = (message[i] & 0x01);
			abc[(i - first) * 8 + 6] = (message[i] & 0x03) >> 1;
			abc[(i - first) * 8 + 5] = (message[i] & 0x07) >> 2;
			abc[(i - first) * 8 + 4] = (message[i] & 0x0F) >> 3;
			abc[(i - first) * 8 + 3] = (message[i] & 0x10) >> 4;
			abc[(i - first) * 8 + 2] = (message[i] & 0x30) >> 5;
			abc[(i - first) * 8 + 1] = (message[i] & 0x70) >> 6;
			abc[(i - first) * 8] = (message[i] & 0xFF) >> 7;

		}
		return abc;
	}

	// 返回1
	public static int[] reOneNsat(int[] message, int first, int lenght, int type) {
		int Nsat = 0;
		for (int i = first; i < first + lenght; i++) {
			if (message[i] == 1)
				Nsat++;
		}
		int id[] = new int[Nsat];
		int j = 0;
		for (int i = first; i < first + lenght; i++) {
			if (message[i] == 1) {
				switch (type) {
					case 0:
						id[j] = i + 1 - first;
						break;
					case 1:
						id[j] = i + 1 - first + 87;
						break;
					case 2:
						id[j] = i + 1 - first + 64;
						break;
					case 5:
						id[j] = i + 1 - first + 100;
						break;
				}

				j++;
			}
		}
		return id;
	}

	public static int reL2(int[] message, int first, int lenght, int type) {
		int l2 = 0;
		int j = 0;
		for (int i = first; i < first + lenght; i++) {

			if (message[i] == 1) {
				j++;
				switch (type) {
					case 0: {
						if ((i - first + 1) > 7 && (i - first + 1) < 18) {
							l2 = j;
							break;
						}
					}
					case 1: {
						break;
					}
					case 2: {
						if ((i - first + 1) > 7 && (i - first + 1) < 10) {
							l2 = j;
							break;
						}
					}
					case 5: {
						if ((i - first + 13) > 7 && (i - first + 1) < 16) {
							l2 = j;
							break;
						}
					}
				}

				if(l2!=0)
				{
					break;
				}
			}

		}
		return l2;
	}

	public static int getL2(int[] message, int first, int L2_s, int Resolution) {
		int L2 = 0;
		int abc = 1;
		if (Resolution == 0) {
			first += (L2_s - 1) * 6;
			String abcd="";
			for (int i = first  ; i < first+6; i++) {
				abcd+=message[i]+";";
				L2 += message[i] * abc;
				abc = abc * 2;
			}
			abcd+=";";
		} else {
			first += (L2_s - 1) * 10;

			for (int i = first + 8 - 1; i > first; i--) {

				L2 += message[i] * abc;
				abc = abc * 2;
			}
		}
		return L2;
	}

	public static int[] reL2_s(int[] message, int first, int lenght, int type,
							   int l2, int Nsat, int Nsig) {
		int[] L2_s = new int[Nsat];
		int count_0 = 0;
		int j = 0;String abc="";
		for(int i=first-1;i>first-9;i--)
		{
			abc+=";"+message[i];
		}
		Log.v("测试9995", abc);
		for (int i = first; i < first + lenght; i++) {
			if ((i - first) != 0 && (i - first) % Nsig == 0) {
				L2_s[j] =count_0 ;
				j++;
				//		count_0 = 0;
			}

			if (message[i] ==1) {
				count_0++;
			}

		}
		L2_s[j] =count_0;
		return L2_s;
	}
}
