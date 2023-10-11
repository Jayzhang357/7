package com.zhd.datamanagetemplate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Environment;
import android.util.Log;

public class FileHelper {

	private static File sdCardDir = Environment.getExternalStorageDirectory();
	private static Boolean isFirst = true;

	public static void writeByte(byte[] buffer, String name, Boolean isClear)
			throws IOException {

		File saveFile = new File(sdCardDir, name);
		FileOutputStream outStream;

		if (!saveFile.exists()) {
			saveFile.createNewFile();
		} else {

		}

		try {
			outStream = new FileOutputStream(saveFile, !isClear);
			if (isClear) {
				byte[] b = { 32 };
				outStream.write(b);
			} else
				outStream.write(buffer);

			outStream.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	private static DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
	public static void record(String str){
		try {
			String fileName = "serialport-crash.txt";
			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				
				String logPath = "";
					logPath = Environment.getExternalStorageDirectory()+"/HiFarm/SerialportError";
				
				File dir = new File(logPath);
				if (!dir.exists()) {
					dir.mkdirs();
				}
				FileOutputStream fos = new FileOutputStream(logPath +"/"+ fileName);
				fos.write(str.getBytes());
				fos.close();
			}
		} catch (Exception e) {
			Log.e("serialport", "an error occured while writing file...", e);
		}
	}

}
