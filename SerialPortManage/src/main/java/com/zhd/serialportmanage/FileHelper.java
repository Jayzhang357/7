package com.zhd.serialportmanage;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.os.Environment;

public class FileHelper {

	private static File sdCardDir = Environment.getExternalStorageDirectory();
	private static Boolean isFirst = true;
	public static long LASR_GGA_MOMENT=System.currentTimeMillis();

	public static void writeByte(byte[] buffer, String name, boolean isClear)
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

			e.printStackTrace();
		}

	}

	public static void writeByteBin(byte[] buffer, String name, boolean isClear)
			throws IOException {

		File saveFile = new File(sdCardDir, name);
		DataOutputStream outStream;

		if (!saveFile.exists()) {
			saveFile.createNewFile();

		} else {

		}

		try {
			outStream = new DataOutputStream(new FileOutputStream(saveFile,
					!isClear));

			if (isClear) {
				byte[] b = { 32 };
				outStream.write(b);
			} else
				outStream.write(buffer);
//				for (byte b : buffer) {
//					System.out.print(b & 0xFF);
//					outStream.write(b);
//				}
			outStream.flush();
			outStream.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Convert char to byte
	 *
	 * @param c
	 *            char
	 * @return byte
	 */
	private static byte charToByte(char c) {
		return (byte) "0123456789ABCDEF".indexOf(c);
	}

	// 有符号
	public static byte[] bytesToHexString(String hexString) {
		if (hexString == null || hexString.equals("")) {
			return null;
		}
		hexString = hexString.toUpperCase();
		String[] strs = hexString.split(" ");
		int length = strs.length;
		byte[] d = new byte[length];
		for (int i = 0; i < length; i++) {
			char[] hexChars = strs[i].toCharArray();
			d[i] = (byte) (charToByte(hexChars[0]) << 4 | charToByte(hexChars[1]));
		}
		return d;
	}

	public static byte[] readByte(String name) throws IOException {
		File file = new File(sdCardDir, name);
		byte[] buffer = new byte[5000];
		FileInputStream fis;
		try {
			fis = new FileInputStream(file);
			int length = fis.available();

			fis.read(buffer);
			fis.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return buffer;

	}
}
