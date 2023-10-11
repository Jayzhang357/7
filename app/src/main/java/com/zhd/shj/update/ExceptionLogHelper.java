package com.zhd.shj.update;

import android.database.sqlite.SQLiteException;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;

public class ExceptionLogHelper {

	private static String sdPath = "";
	private static Boolean sdCardExist = false;
	private static String logPath = "";

	static {
		sdCardExist = Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
		if (sdCardExist) {
			File sdDir = Environment.getExternalStorageDirectory();// 获取根目录
			sdPath = sdDir.toString();
			logPath = sdPath + "//AutoUpdateLog";
			File file = new File(logPath);
			if (!file.exists()) {
				file.mkdirs();
			}
		}
	}

	/**
	 * 发布异常信息
	 *
	 * @param userName
	 * @param userIP
	 * @param exception
	 */
	public static void PublishException(Exception exception) {
		StringBuilder strInfo = new StringBuilder();
		strInfo.append("错误列表:\r\n");

		Exception currException = exception;

		strInfo.append(currException.getClass().getName());
		strInfo.append("\r\n信息: ");

		if (currException instanceof SQLiteException) {
			SQLiteException sqliteex = (SQLiteException) currException;

			strInfo.append("Message: ").append(sqliteex.getMessage())
					.append("\r\n");
			strInfo.append("ErrorCode: ").append(sqliteex.getCause())
					.append("\r\n");
			strInfo.append("StackTrace: ").append(sqliteex.getStackTrace())
					.append("\r\n");
		} else {
			strInfo.append(currException.getMessage()
					+ currException.getStackTrace());
		}
		strInfo.append("\r\n");

		if (sdCardExist)// 同时保存到本地（暂时调试使用）
		{
			try {
				java.util.Date currentTime = new java.util.Date();
				SimpleDateFormat format1 = new SimpleDateFormat(
						"yyyy-MM-dd HH-mm-ss");
				strInfo.insert(0, "\r\n---" + format1.format(currentTime)
						+ "---\r\n");

				format1 = new SimpleDateFormat("yyyy-MM-dd");
				String timeNow = format1.format(currentTime);
				String logFile = timeNow + ".txt";
				logFile = logPath + "//" + logFile;

				FileOutputStream fos = new FileOutputStream(logFile, true);
				fos.write(strInfo.toString().getBytes());
				fos.flush();
				fos.close();
			} catch (Exception e) {
			}
		}
	}
}
