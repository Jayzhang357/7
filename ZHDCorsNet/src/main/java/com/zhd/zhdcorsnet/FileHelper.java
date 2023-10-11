package com.zhd.zhdcorsnet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.Buffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.R.string;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

public class FileHelper {

	private static DateFormat formatter = new SimpleDateFormat(
			"yyyy-MM-dd-HH-mm-ss");

	public static void write3gStatus(String msg, String fileName) {
		try {
			// long timestamp = System.currentTimeMillis();
			// String time = formatter.format(new Date());
			if (Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {

				String logPath = "";
				logPath =  Environment.getExternalStorageDirectory()+"/call3g";

				File dir = new File(logPath);
				if (!dir.exists()) {
					dir.mkdirs();
				}
				// 覆盖的方式
				FileOutputStream fos = new FileOutputStream(logPath + "/"
						+ fileName, false);
				fos.write(msg.getBytes());
				fos.close();
			}
		} catch (Exception e) {
			Log.e("serialport", "an error occured while writing file...", e);
		}
	}



	public static String read3gStatus(String fileName) {
		File file = new File( Environment.getExternalStorageDirectory()+"/call3g", fileName);
		byte[] buffer = new byte[500];
		FileInputStream fis;
		String result = "";

		try {
			fis = new FileInputStream(file);
			int length = fis.available();

			if (length == 0)
				return result;

			fis.read(buffer);
			fis.close();

			result = new String(buffer, "UTF-8");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}

		return result;

	}

	public static Boolean checkIP(String IP) {
		if (IP == null || IP == "")
			return false;

		try {
			String[] fields = IP.split("\\.");
			if (fields.length != 4)
				return false;
			for (String str : fields) {
				int field = Integer.valueOf(str);
				if (field > 255 || field < 0)
					return false;
			}
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

	}
}
