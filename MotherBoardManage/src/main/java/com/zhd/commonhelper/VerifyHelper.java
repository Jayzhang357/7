package com.zhd.commonhelper;

import java.io.UnsupportedEncodingException;

public class VerifyHelper {

	private static final long CRC32_POLYNOMIAL = 0xEDB88320 & 0xFFFFFFFFL;

	private static long CRC32Value(long l) {
		long j;
		long ulCRC;
		ulCRC = l;
		for (j = 8; j > 0; j--) {
			if ((ulCRC & 1) != 0) {
				ulCRC = (ulCRC >> 1) ^ CRC32_POLYNOMIAL;
			} else {
				ulCRC >>= 1;
			}
		}
		return ulCRC;
	}

	public static long CalculateBlockCRC32(byte[] buf, long len) {
		long ulTemp1;
		long ulTemp2;
		long ulCRC = 0;
		for (int i = 0; i < len; i++) {
			ulTemp1 = (ulCRC >> 8) & 0x00FFFFFF;
			ulTemp2 = CRC32Value((ulCRC ^ buf[i]) & 0xFF);
			ulCRC = ulTemp1 ^ ulTemp2;
		}
		return ulCRC;
	}



	/**
	 * NMEA8位异或校验
	 * @param entireNmeaMessage
	 * @return
	 */
	public static boolean X8ORVerify(byte[] entireNmeaMessage) {
		if(entireNmeaMessage.length < 7)
			return false;

		byte checkSumByteA = entireNmeaMessage[entireNmeaMessage.length - 4];
		byte checkSumByteB = entireNmeaMessage[entireNmeaMessage.length - 3];
		byte[] fourBithextochar = new byte[] { '0', '1', '2', '3', '4', '5',
				'6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
		byte xor = entireNmeaMessage[1];
		for (int i = 2; i < entireNmeaMessage.length - 5; i++) {
			xor ^= entireNmeaMessage[i];
		}
		int xorA = xor >> 4;
		int xorB = xor & 0x0F;
		if (xorA < 0 || xorB < 0) {
			return false;
		}

		if (fourBithextochar[xorA] != checkSumByteA
				|| fourBithextochar[xorB] != checkSumByteB)
			return false;

		return true;
	}

	public static boolean modulo256verify(byte[] entireMessage)
	{
		int modResult = 0;
		int sum  =0;
		if((sum-((sum>>8)<<8))==modResult)
			return true;
		else
			return false;




	}
}
