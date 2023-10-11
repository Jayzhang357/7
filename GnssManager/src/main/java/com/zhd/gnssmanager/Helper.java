package com.zhd.gnssmanager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.test.control.ProClass;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

public class Helper {

	/*
	 * 文件是否存在
	 */
	public static boolean fileIsExist(String filePath) {
		File f = new File(filePath);
		if (!f.exists()) {
			return false;
		}
		return true;
	}

	/**
	 * 删除文件
	 *
	 * @param filePath
	 * @return
	 */
	public static boolean deleteFile(String filePath) {
		File f = new File(filePath);
		if (f.exists()) {
			return f.delete();
		}
		return false;
	}

	/*
	 * 发送消息
	 */

	/*
	 * 获取当前时间的字符串表示（年月日时分秒）
	 */
	public static String getDateTimeNow() {
		SimpleDateFormat format1 = new SimpleDateFormat("yyyyMMddhhmmss");
		Date currentTime = new Date();
		String timeNow = format1.format(currentTime);
		return timeNow;
	}

	/*
	 * 获取当前时间的字符串表示（年—月—日）
	 */
	public static String getDateNow() {
		SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
		Date currentTime = new Date();
		String timeNow = format1.format(currentTime);
		return timeNow;
	}

	/*
	 * 获取当前时间的long表示
	 */
	public static long getLongDateNow() {
		Date currentTime = new Date();
		return currentTime.getTime();
	}

	/*
	 * 获取SD卡路径
	 */
	public static String getSDPath() {
		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
		if (sdCardExist) {
			sdDir = Environment.getExternalStorageDirectory();// 获取根目录
			return sdDir.toString();
		}
		return null;
	}

	/*
	 * SD卡是否存在
	 */
	public static boolean isExistSDCard() {
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
		return sdCardExist;
	}

	/*
	 * 检测网络状态：别忘记在mainfest.xml中添加android.permission.ACCESS_NETWORK_STATE权限
	 */
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			return false;
		} else {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * 创建ID
	 *
	 * @return
	 */
	public static String createOID() {
		UUID uuid = UUID.randomUUID();
		return uuid.toString();
	}

	/**
	 * 创建文件夹
	 *
	 * @param folderPath
	 *            文件夹路径
	 * @return
	 */
	public static boolean createFolder(String folderPath) {
		File file = new File(folderPath);
		if (!file.exists()) {
			try {
				// 按照指定的路径创建文件夹
				file.mkdirs();
			} catch (Exception e) {
				// TODO: handle exception
				return false;
			}
		}
		return true;
	}

	/**
	 * 字符串是否为空
	 *
	 * @param str
	 * @return
	 */
	public static boolean stringIsNullOrEmpty(String str) {
		if (str == null || str.length() <= 0)
			return true;
		return false;
	}

	/**
	 * 获取文件名后缀(包括".")
	 *
	 * @param filename
	 * @param defExt
	 * @return
	 */
	public static String getExtension(String filename, String defExt) {
		if ((filename != null) && (filename.length() > 0)) {
			int i = filename.lastIndexOf('.');

			if ((i > -1) && (i < (filename.length() - 1))) {
				return filename.substring(i);
			}
		}
		return defExt;
	}

	/**
	 * 提供对用户名的密码加密
	 *
	 * @param password
	 *            用户名密码的明文
	 * @return 返回加密后的字符串
	 */
	public static String encryptPassword(String password) {
		MessageDigest md = null;

		try {
			md = MessageDigest.getInstance("SHA-1");
			md.update(password.getBytes("UTF-8"));
		} catch (Exception e) {
			return null;
		}

		byte[] result = md.digest();
		StringBuffer sb = new StringBuffer();
		for (byte b : result) {
			int i = b & 0xff;
			if (i < 0xf) {
				sb.append(0);
			}
			sb.append(Integer.toHexString(i));
		}
		String encryptPassword = (sb.toString().toUpperCase());
		return encryptPassword;
	}

	/**
	 * 获取文件的字节流
	 *
	 * @param filePath
	 * @return
	 */
	public static byte[] getImageBytes(String filePath) {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(filePath);
		} catch (FileNotFoundException e) {
			try {
				fis.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			// TODO Auto-generated catch block
			return null;
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int count = 0;
		try {
			while ((count = fis.read(buffer)) >= 0)
				baos.write(buffer, 0, count);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return null;
		}
		try {
			fis.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		byte [] abc=baos.toByteArray();
		try {
			baos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return abc;
	}

	public static boolean validString(String name) {

		return ((name != null && !name.trim().equals("")));

	}

	/**
	 * 将字符数组转换成十六进制数组
	 *
	 * @param src
	 * @param size
	 * @return
	 */
	public static String bytesToHexString(byte[] src, int size) {
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || size <= 0) {
			return null;
		}
		for (int i = 0; i < size; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
			stringBuilder.append(" ");
		}
		return stringBuilder.toString();
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

	/**
	 * 打开已知名称的程序
	 *
	 * @param ApkName
	 * @param activity
	 * @return
	 * @return Intent
	 * @time 2014年8月29日 上午9:15:07
	 */
	public static Intent startAnotherApp(String ApkName, Activity activity) {
		PackageManager pm = activity.getPackageManager();
		List<ApplicationInfo> applicationInfos = pm.getInstalledApplications(0);
		String packageName = null;
		for (ApplicationInfo applicationInfo : applicationInfos) {
			if (pm.getApplicationLabel(applicationInfo).toString()
					.equals(ApkName)) {
				packageName = applicationInfo.packageName;
			}
		}
		PackageInfo packageInfo = null;
		try {
			packageInfo = activity.getPackageManager().getPackageInfo(
					packageName, 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
		resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		resolveIntent.setPackage(packageInfo.packageName);
		List<ResolveInfo> resolveInfoList = activity.getPackageManager()
				.queryIntentActivities(resolveIntent, 0);
		ResolveInfo resolveInfo = resolveInfoList.iterator().next();
		Intent intent = new Intent(Intent.ACTION_MAIN);
		if (resolveInfo != null) {
			String activityPackageName = resolveInfo.activityInfo.packageName;
			String className = resolveInfo.activityInfo.name;

			intent.addCategory(Intent.CATEGORY_LAUNCHER);
			ComponentName componentName = new ComponentName(
					activityPackageName, className);

			intent.setComponent(componentName);
		}
		return intent;
	}

	// public static void DialogBuilder(String tiltle,String message) {
	// AleartDialogHelper name = new AleartDialogHelper(arguments);
	// }

	public static int getMotherType(Context context) {
		final SharedPreferences sharedPreferences = context
				.getSharedPreferences("gnss", Activity.MODE_PRIVATE);
		String motherBoradType = sharedPreferences.getString("motherboard", "");
		int motherType = -1;
		if (motherBoradType != null && !motherBoradType.equals(""))
			motherType = Integer.parseInt(motherBoradType);

		return motherType;
	}

	@SuppressLint("SdCardPath")
	@SuppressWarnings("resource")
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

	public static int getSIMIntensity() {
		int ret = -1;
		ret = ProClass.protest(1);
		if (ret == 0) {
			return ProClass.protest(2);

		}
		return ret;
	}
}
