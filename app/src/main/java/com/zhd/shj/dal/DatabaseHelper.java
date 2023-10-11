package com.zhd.shj.dal;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.zhd.AppHelper;


import java.io.File;
import java.util.Hashtable;
import java.util.Random;

public class DatabaseHelper {

	private static String m_ConnectionString = null;

	public static String GetConnectionString() {
		if (m_ConnectionString == null) {
			m_ConnectionString = ReadConnectionString();
		}

		return m_ConnectionString;
	}

	/**
	 * 获取连接数据库字符串
	 *
	 * @return
	 */
	private static String ReadConnectionString() {
		String path = AppHelper.getDBPath();
		File f = new File(path);
		if (!f.exists())
			return "";
		else {
			String connectStr = path;
			return connectStr;
		}
	}

	private static Hashtable<String, Integer> m_ScopeKeys = new Hashtable<String, Integer>();

	public static int getMaxKey(SQLiteDatabase db, String tableName,
								String keyName) {
		return getMaxKeyValue(db, tableName, keyName);
	}

	private static int getMaxKeyValue(SQLiteDatabase db, String tableName,
									  String keyName) {
		int maxKey = 0;
		// if (m_ScopeKeys.containsKey(tableName)) {
		// maxKey = m_ScopeKeys.get(tableName);
		// maxKey++;
		// m_ScopeKeys.put(tableName, maxKey);
		// } else {
		String sql = "SELECT MAX(" + keyName + ") AS maxID FROM " + tableName;
		Cursor cursor=null;
		try {
			cursor = db.rawQuery(sql, null);
			if (cursor != null && cursor.moveToFirst()) {
				while (!cursor.isAfterLast()) {
					maxKey = cursor.getInt(0);
					break;
				}
			}
		} catch (Exception e) {

		} finally {
			if (cursor != null) {
				cursor.close();// 2
			}
			db.close();
		}
		// maxKey++;
		// m_ScopeKeys.put(tableName, maxKey);
		// }

		return maxKey;
	}

	/**
	 * java生成随机数字和字母组合
	 *
	 * @param
	 *
	 * @return
	 */
	public static String getCharAndNumr() {
		String val = "";
		Random random = new Random();
		for (int i = 0; i < 7; i++) {
			// 输出字母还是数字
			String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";
			// 字符串
			if ("char".equalsIgnoreCase(charOrNum)) {
				// 取得大写字母还是小写字母
				val += (char) (97 + random.nextInt(25));
			} else if ("num".equalsIgnoreCase(charOrNum)) { // 数字
				val += String.valueOf(random.nextInt(10));
			}
		}
		return val;
	}
}
