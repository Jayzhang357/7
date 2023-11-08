package com.zhd.shj.dal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BaseDAL extends SQLiteOpenHelper {

	private final static int DATABASE_VERSION = 1;
	protected String mTableName = "";
	protected String mKeyName = "ID";// 主键名
	protected boolean mKeyIsIdentity = false; // 主键是否自增长
	protected Context mContext = null;

	public BaseDAL(Context context, String dbPath, String tableName) {
		super(context, dbPath, null, DATABASE_VERSION);
		this.mTableName = tableName;
		this.mContext = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
	}

	/**
	 * 读取全部数据对象
	 *
	 * @param
	 * @return数据对象集合
	 */
	private String saveCrashInfo2File(String ex) {

		try {

			String fileName = "crash" + ".log";
			if (Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {
				String path =  Environment.getExternalStorageDirectory()+"/HiFarmLog/";
				File dir = new File(path);
				if (!dir.exists()) {
					dir.mkdirs();
				}
				BufferedWriter bw = new BufferedWriter(new FileWriter(path
						+ fileName, true));
				SimpleDateFormat df = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");// 设置日期格式

				bw.write("当前时间：" + df.format(new Date()) + ":::::"
						+ ex.toString());
				bw.close();

			}
			return fileName;
		} catch (Exception e) {
			saveCrashInfo2File("发生了错误" + e);
		}
		return null;
	}

	/**
	 * 读取全部数据对象
	 *
	 * @param
	 * @return数据对象集合
	 */
	public List<Object> selectAll(String orderField, String orderType) {
		List<Object> objList = new ArrayList<Object>();
		SQLiteDatabase db = this.getReadableDatabase();
		// String selection ="FieldName='ii'";
		Log.v("数据库","1");
		Cursor cursor = null;

		try {
			cursor = db.query(mTableName, null, null, null, null, null,
					orderField + " " + orderType);
			if (cursor.getColumnCount() == 18) {
				db.execSQL("ALTER  TABLE   HF_Job  ADD COLUMN  setHsmall DOUBLE ");

				cursor = db.query(mTableName, null, null,
						null, null, null, orderField + " " + orderType);
			}
			if (cursor.getColumnCount() == 19) {
				db.execSQL("ALTER  TABLE   HF_Job  ADD COLUMN  Width DOUBLE ");

				cursor = db.query(mTableName, null, null,
						null, null, null, orderField + " " + orderType);
			}
			if (cursor != null && cursor.moveToFirst()) {
				while (!cursor.isAfterLast()) {
					objList.add(this.dataReaderToEntity(cursor));

					cursor.moveToNext();
				}
			}
		} catch (Exception e) {
			saveCrashInfo2File("发生错误1" + e);
		} finally {
			if (cursor != null) {
				Log.v("数据库关闭","1");
				cursor.close();// 2
			}

			db.close();
		}

		return objList;
	}

	/**
	 * 读取全部数据对象
	 *
	 * @param
	 * @return数据对象集合
	 */
	public List<Object> selectAll(String orderField) {
		return selectAll(orderField, " asc");
	}

	/**
	 *
	 * @param keyValue
	 * @return
	 */
	public Object selectByKey(Object keyValue) {
		Object obj = null;
		String selection = mKeyName + " = ?";
		String[] selectionArgs = { String.valueOf(keyValue) };
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = null;
		Log.v("数据库","2");
		try {
			cursor = db.query(mTableName, null, selection, selectionArgs,
					null, null, mKeyName + " asc");

			if (cursor != null && cursor.moveToFirst()) {
				while (!cursor.isAfterLast()) {
					obj = (this.dataReaderToEntity(cursor));
					break;
				}
			}
		} catch (Exception e) {
			saveCrashInfo2File("发生错误2" + e);
		} finally {
			if (cursor != null) {
				Log.v("数据库关闭","2");
				cursor.close();// 2
			}
			db.close();
		}

		return obj;
	}

	/**
	 * 通过条件返回Cursor
	 *
	 * @param columns
	 * @param selection
	 * @param selectionArgs
	 * @param orderField
	 * @return
	 */
	public Cursor selectReturnCursor(String[] columns, String selection,
									 String[] selectionArgs, String orderField) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = null;
		Log.v("数据库","3");
		try {
			cursor = db.query(mTableName, columns, selection,
					selectionArgs, null, null, orderField + " asc");
		} catch (Exception e) {
			saveCrashInfo2File("发生错误3" + e);
		} finally {
			if (cursor != null) {
				Log.v("数据库关闭","3");
				cursor.close();// 2
			}
			db.close();
		}

		return cursor;
	}

	/**
	 * 读取全部滿足條件数据对象
	 *
	 * @param selection
	 * @param selectionArgs
	 * @param orderField
	 *            根据该字段排序
	 * @return数据对象集合
	 */
	@SuppressWarnings("null")
	public List<Object> selectByCondition(String selection,
										  String[] selectionArgs, String orderField) {
		List<Object> objList = new ArrayList<Object>();
		SQLiteDatabase db = this.getReadableDatabase();
		Log.v("数据库","4");
		Cursor cursor = null;

		try {
			cursor = db.query(mTableName, null, selection, selectionArgs,
					null, null, orderField);

			Log.v("数据库","4"+cursor.getColumnCount());
			if (cursor.getColumnCount() == 18) {
				db.execSQL("ALTER  TABLE   HF_Job  ADD COLUMN  setHsmall DOUBLE ");

				cursor = db.query(mTableName, null, selection,
						selectionArgs, null, null, orderField);
			}
			if (cursor.getColumnCount() == 19) {
				db.execSQL("ALTER  TABLE   HF_Job  ADD COLUMN  Width DOUBLE ");

				cursor = db.query(mTableName, null, selection, selectionArgs,
						null, null, orderField);
			}
			if (cursor.moveToFirst()) {
				while (!cursor.isAfterLast()) {
					objList.add(this.dataReaderToEntity(cursor));

					cursor.moveToNext();
				}
			}
		} catch (Exception e) {
			saveCrashInfo2File("发生错误4" + e);
		} finally {
			if (cursor != null) {
				Log.v("数据库关闭","4");
				cursor.close();// 2
			}
			db.close();
		}

		return objList;

	}

	public List<Object> selecteByConditionSync(String selection,
											   String[] selectionArgs, String orderField, SQLiteDatabase db) {
		List<Object> objList = new ArrayList<Object>();

		Cursor cursor = null;
		Log.v("数据库","5");
		try {
			cursor = db.query(mTableName, null, selection, selectionArgs,
					null, null, orderField + " asc");

			if (cursor != null && cursor.moveToFirst()) {
				while (!cursor.isAfterLast()) {
					objList.add(this.dataReaderToEntity(cursor));

					cursor.moveToNext();
				}
			}
		} catch (Exception e) {
			saveCrashInfo2File("发生错误5" + e);
		} finally {
			if (cursor != null) {
				Log.v("数据库关闭","5");
				cursor.close();// 2
			}
			db.close();
		}

		return objList;
	}

	/**
	 * 通过Sql语句实现查询
	 *
	 * @param sql
	 * @param selectionArgs
	 * @return
	 */
	public List<Object> queryBySql(String sql, String[] selectionArgs) {
		List<Object> objList = new ArrayList<Object>();
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = null;
		Log.v("数据库","6");
		try {

			cursor = db.rawQuery(sql, selectionArgs);

			if (cursor != null && cursor.moveToFirst()) {
				while (!cursor.isAfterLast()) {
					objList.add(this.dataReaderToEntity(cursor));

					cursor.moveToNext();
				}
			}
		} catch (Exception e) {
			saveCrashInfo2File("发生错误6" + e);
		} finally {
			if (cursor != null) {
				Log.v("数据库关闭","6");
				cursor.close();// 2
			}
			db.close();
		}

		return objList;
	}

	/**
	 * 返回符合条件的数目
	 *
	 * @param selection
	 * @param selectionArgs
	 * @return
	 */
	public int selectCountByCondition(String selection, String[] selectionArgs) {
		String[] columns = { "Count(*)" };
		SQLiteDatabase db = this.getReadableDatabase();
		int count = 0;
		Cursor cursor = null;
		Log.v("数据库","7");
		try {
			cursor = db.query(mTableName, columns, selection,
					selectionArgs, null, null, null);

			if (cursor != null && cursor.moveToFirst())
				count = cursor.getInt(0);
			//	TestCursor abc=new TestCursor(cursor);

		} catch (Exception e) {
			Log.v("数据库关闭","泄漏了");
				/*	new Thread() {
			            @Override
			            public void run() {
			                Intent intent = new Intent(mContext, MainActivity.class);
			                PendingIntent restartIntent = PendingIntent.getActivity(mContext, 0, intent, Intent.FLAG_ACTIVITY_NEW_TASK);
			                AlarmManager mgr = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
			                mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, restartIntent);
			                android.os.Process.killProcess(android.os.Process.myPid());
			            }
			        }.start();*/
			saveCrashInfo2File("发生错误7" + e);
		} finally {
			if (cursor != null) {

				Log.v("数据库关闭","7");
				cursor.close();// 2
			}
			db.close();
		}

		return count;
	}

	/**
	 * 读取滿足條件的指定数量数据对象
	 *
	 * @param selection
	 * @param selectionArgs
	 * @param orderField
	 * @param limit
	 * @return
	 */
	public List<Object> selectByCondition(String selection,
										  String[] selectionArgs, String orderField, String limit) {
		List<Object> objList = new ArrayList<Object>();
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = null;
		Log.v("数据库","8");
		try {
			cursor = db.query(mTableName, null, selection, selectionArgs,
					null, null, orderField + " asc", limit);

			if (cursor != null && cursor.moveToFirst()) {
				while (!cursor.isAfterLast()) {
					objList.add(this.dataReaderToEntity(cursor));

					cursor.moveToNext();
				}
			}
		} catch (Exception e) {
			saveCrashInfo2File("发生错误8" + e);
		} finally {
			if (cursor != null) {
				Log.v("数据库关闭","8");
				cursor.close();// 2
			}
			db.close();
		}

		return objList;
	}

	/**
	 * 更新记录
	 *
	 * @param
	 * @return更新是否成功
	 */
	public boolean update(Object obj) {
		SQLiteDatabase db = this.getWritableDatabase();
		String where = mKeyName + " = ?";
		String[] whereValue = null;
		ContentValues cv = new ContentValues();
		Log.v("数据库","9");
		Class<? extends Object> temp = obj.getClass();
		Field field = null;
		Object fieldValue = null;
		Field[] fields = temp.getFields();
		for (int i = 0; i < fields.length; i++) {
			String fieldName = fields[i].getName();
			try {
				field = temp.getField(fieldName);
				fieldValue = field.get(obj);
				if (fieldName.equalsIgnoreCase(mKeyName)) {
					whereValue = new String[] { String.valueOf(fieldValue) };
					continue;
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				saveCrashInfo2File("发生错误9" + e);
				e.printStackTrace();
			}
			cv.put(fieldName, String.valueOf(fieldValue));
		/*	if(fieldName!="CoverageArea")
			cv.put(fieldName, String.valueOf(fieldValue));
			else if(Float.parseFloat( String.valueOf(fieldValue))>0)
				cv.put(fieldName, String.valueOf(fieldValue));*/

		}

		try {

			int success = db.update(mTableName, cv, where, whereValue);

			if (success > 0)
				return true;

		} catch (Exception e) {
			saveCrashInfo2File("发生错误9" + e);
		} finally {
			db.close();
		}
		return false;
	}

	/**
	 * 事务同步更新记录
	 *
	 * @param
	 * @return更新是否成功
	 */
	public boolean updateSync(Object obj, SQLiteDatabase db) {
		String where = mKeyName + " = ?";
		String[] whereValue = null;
		ContentValues cv = new ContentValues();
		Log.v("数据库","10");
		Class<? extends Object> temp = obj.getClass();
		Field field = null;
		Object fieldValue = null;
		Field[] fields = temp.getFields();
		for (int i = 0; i < fields.length; i++) {
			String fieldName = fields[i].getName();
			try {
				field = temp.getField(fieldName);
				fieldValue = field.get(obj);
				if (fieldName.equalsIgnoreCase(mKeyName)) {
					whereValue = new String[] { String.valueOf(fieldValue) };
					continue;
				}
			} catch (Exception e) {
				saveCrashInfo2File("发生错误10" + e);
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if(fieldName!="CoverageArea")
				cv.put(fieldName, String.valueOf(fieldValue));
			else if(Float.parseFloat( String.valueOf(fieldValue))>0) {
				saveCrashInfo2File("插入田块数据" + fieldValue);
				cv.put(fieldName, String.valueOf(fieldValue));
			}
			else
			{
				saveCrashInfo2File("插入田块数据123" + fieldValue);
			}
		}

		int success = db.update(mTableName, cv, where, whereValue);
		if (success > 0)
			return true;

		return false;
	}

	/**
	 * 更新記錄
	 *
	 * @param keyValue
	 * @param fieldName
	 * @param fieldVlaue
	 * @return
	 */
	public boolean update(Object keyValue, String fieldName, String fieldVlaue) {
		SQLiteDatabase db = this.getWritableDatabase();
		String where = mKeyName + " = ?";
		String[] whereValue = { String.valueOf(keyValue) };
		ContentValues cv = new ContentValues();

		cv.put(fieldName, fieldVlaue);
		Log.v("数据库","11");
		try {

			if (db.update(mTableName, cv, where, whereValue) > 0)
				return true;

		} catch (Exception e) {
			saveCrashInfo2File("发生错误11" + e);
		} finally {
			db.close();
		}
		return false;
	}

	/**
	 * 插入记录
	 *
	 * @param
	 * @return插入的记录ID
	 */
	public Object insert(Object obj) {
		SQLiteDatabase db = null;
		ContentValues cv = new ContentValues();
		Log.v("数据库","12");
		Class<? extends Object> temp = obj.getClass();
		Field field = null;
		Object fieldValue = null;
		Object keyValue = null;
		Field[] fields = temp.getFields();
		for (int i = 0; i < fields.length; i++) {
			String fieldName = fields[i].getName();
			try {
				field = temp.getField(fieldName);
				fieldValue = field.get(obj);
			} catch (Exception e1) {
				return "";
			}

			if (fieldName.equalsIgnoreCase(mKeyName)) {
				if (!mKeyIsIdentity) // 如果不是自增長，則需要設置ID值，自增長不需要設置ID值
				{
					keyValue = setKeyValue(obj);
					cv.put(fieldName, String.valueOf(keyValue));
				}
			} else {
				String tempValue = String.valueOf(fieldValue);
				cv.put(fieldName, tempValue);
			}
		}

		try {
			db = this.getWritableDatabase();

			long row = db.insert(mTableName, null, cv);
			if (row == -1 || mKeyIsIdentity)
				return row;
			else
				return keyValue;

		} catch (Exception e) {
			saveCrashInfo2File("发生错误12" + e);
			return keyValue;
		} finally {
			db.close();
		}
	}

	/**
	 * 同步插入记录
	 *
	 * @param
	 * @return插入的记录ID
	 */
	public Object insertSync(Object obj, SQLiteDatabase db) {
		ContentValues cv = new ContentValues();
		Log.v("数据库","13");
		Class<? extends Object> temp = obj.getClass();
		Field field = null;
		Object fieldValue = null;
		Object keyValue = null;
		Field[] fields = temp.getFields();
		for (int i = 0; i < fields.length; i++) {
			String fieldName = fields[i].getName();
			try {
				field = temp.getField(fieldName);
				fieldValue = field.get(obj);
			} catch (Exception e1) {
				return "";
			}

			if (fieldName.equalsIgnoreCase(mKeyName)) {
				if (!mKeyIsIdentity) // 如果不是自增長，則需要設置ID值，自增長不需要設置ID值
				{
					keyValue = setKeyValue(obj);
					cv.put(fieldName, String.valueOf(keyValue));
				}
			} else {
				String tempValue = String.valueOf(fieldValue);
				cv.put(fieldName, tempValue);
			}
		}

		long row = db.insert(mTableName, null, cv);
		if (row == -1 || mKeyIsIdentity)
			return row;
		else
			return keyValue;


	}

	public boolean deleteByKeyValue(Object keyValue) {
		SQLiteDatabase db = this.getWritableDatabase();
		String where = mKeyName + " = ?";
		String[] whereValue = { String.valueOf(keyValue) };
		Log.v("数据库","14");
		try {

			db.delete(mTableName, where, whereValue);

		} catch (Exception e) {
			e.printStackTrace();
			saveCrashInfo2File("发生错误13" + e);
			return false;

		} finally {
			db.close();
		}

		return true;
	}

	public void deleteByCondition(String selection, String[] selectionArgs) {
		Log.v("数据库","15");
		SQLiteDatabase db = this.getWritableDatabase();
		try {

			db.delete(mTableName, selection + " = ?", selectionArgs);

		} catch (Exception e) {
			saveCrashInfo2File("发生错误14" + e);
		} finally {
			db.close();
		}
	}


	public void DropTabel(String mTableName) {
		Log.v("数据库","15");
		SQLiteDatabase db = this.getWritableDatabase();
		try {

			String sql = " DROP TABLE " + mTableName;
			db.execSQL(sql);

		} catch (Exception e) {
			saveCrashInfo2File("发生错误14" + e);
		} finally {
			db.close();
		}
	}

	public void execBySql(String sql) {
		SQLiteDatabase db = this.getWritableDatabase();
		try {
			Log.v("数据库","16");
			db.execSQL(sql);

		} catch (Exception e) {
			saveCrashInfo2File("发生错误15" + e);
		} finally {
			db.close();
		}
	}

	public void deleteAll() {
		SQLiteDatabase db = this.getWritableDatabase();
		try {
			Log.v("数据库","17");
			db.delete(mTableName, null, null);

		} catch (Exception e) {
			saveCrashInfo2File("发生错误16" + e);
		} finally {
			db.close();
		}
	}

	public boolean isExist(String selection, String[] selectionArgs) {
		String[] columns = { mKeyName };
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = null;
		Log.v("数据库","18");
		boolean isExist = false;
		try {
			cursor = db.query(mTableName, columns, selection,
					selectionArgs, null, null, null);

			if (cursor != null)
				isExist = cursor.moveToFirst();

		} catch (Exception e) {
			saveCrashInfo2File("发生错误17" + e);
		} finally {
			if (cursor != null) {
				Log.v("数据库关闭","18");
				cursor.close();// 2
			}
			db.close();
		}

		return isExist;

	}

	public boolean isExist(Object keyValue) {
		String selection = mKeyName + " = ?";
		String[] selectionArgs = { String.valueOf(keyValue) };
		Log.v("数据库","19");
		String[] columns = { mKeyName };
		SQLiteDatabase db = this.getReadableDatabase();
		boolean isExist = false;
		Cursor cursor = null;

		try {
			cursor = db.query(mTableName, columns, selection,
					selectionArgs, null, null, null);

			if (cursor != null)
				isExist = cursor.moveToFirst();
		} catch (Exception e) {
			saveCrashInfo2File("发生错误18" + e);
		} finally {
			if (cursor != null) {
				Log.v("数据库关闭","19");
				cursor.close();// 2
			}
			db.close();
		}
		return isExist;

	}

	/**
	 * 把数据记录读到转换成具体对象
	 *
	 * @param cursor
	 * @return
	 */
	protected Object dataReaderToEntity(Cursor cursor) {
		return null;
	}

	/**
	 * 设置主键值
	 *
	 * @param obj
	 * @return
	 */
	protected Object setKeyValue(Object obj) {
		return null;
	}
}
