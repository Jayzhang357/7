package com.zhd.democors;

import java.io.UnsupportedEncodingException;

import com.sn.control.*;

public class DeviceInfo {

	private static DeviceInfo mDeviceInfo = null;
	private SNClass mSnClass = null;

	public DeviceInfo() {

	}

	public static DeviceInfo getInstanse() {
		if (mDeviceInfo == null)
			mDeviceInfo = new DeviceInfo();

		return mDeviceInfo;
	}

	/**
	 * 获取机身号
	 *
	 * @return
	 */
	public String getDeviceNo() {
		String verifyMessage = "";
		String deviceNo = "";

		try {
			byte[] info = new byte[31];
			int isReadSucessNum = mSnClass.SNRd(info);
			if (isReadSucessNum == -1)
				return "";

			if (info != null)
				verifyMessage = new String(info, "UTF-8");

			char[] timeArr = new char[8];
			String[] verifyMessageArr = verifyMessage.split(",");
			if (verifyMessageArr.length == 4) {
				String registerTime = verifyMessageArr[2];
				char[] registerTimeArr = registerTime.toCharArray();

				for (int i = 0; i < 4; i++) {
					timeArr[2 * i] = registerTimeArr[3 * i + 1];
					timeArr[2 * i + 1] = registerTimeArr[3 * i + 2];
				}
			}

			deviceNo = String.valueOf(timeArr);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}

		return deviceNo;

	}
}
