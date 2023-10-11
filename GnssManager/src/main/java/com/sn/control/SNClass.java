package com.sn.control;

import android.app.Activity;

public class SNClass extends Activity {
	public static native int SNRd(byte[] snbuf);

	static {
		System.loadLibrary("RdSN");
	}
	public static String getDeviceNo() {
		String verifyMessage = "";
		String deviceNo = "";

		try {
		byte[] info = new byte[31];
		int isReadSucessNum = SNClass.SNRd(info);
		if (isReadSucessNum == -1)
			return verifyMessage;

			verifyMessage = new String(info, "UTF-8");

			String[] verifyMessageArr = verifyMessage.split(",");
			if (verifyMessageArr.length == 4) {
				deviceNo = verifyMessageArr[2];
				
				char[] arr = deviceNo.toCharArray();
				char[] deviceArr = new char[8];
				for (int i = 0; i < 4; i++) {
					deviceArr[2 * i] = arr[3 * i + 1];
					deviceArr[2 * i + 1] = arr[3 * i + 2];
				}

				deviceNo = String.valueOf(deviceArr);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return deviceNo;
	}

}