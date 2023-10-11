package com.zhd.shj.business;

import android.database.Cursor;


import com.zhd.shj.dal.BaseDAL;

import java.util.List;

public class BaseBusiness {

	private BaseDAL mBaseDal;

	public BaseBusiness(BaseDAL baseDal) {
		this.mBaseDal = baseDal;
	}

	public BaseDAL getBaseDAL() {
		return mBaseDal;
	}

	/**
	 * 读取全部数据对象
	 *
	 * @param orderField
	 * @return
	 */
	public List<Object> selectAll(String orderField) {
		return mBaseDal.selectAll(orderField);
	}

	/**
	 * 读取全部数据对象
	 *
	 * @param orderField
	 * @param orderType
	 * @return
	 */
	public List<Object> selectAll(String orderField, String orderType) {
		return mBaseDal.selectAll(orderField, orderType);
	}

	/**
	 * 读取全部数据对象
	 *
	 * @param selection
	 * @param selectionArgs
	 * @param orderField
	 * @return
	 */
	public List<Object> selectByCondition(String selection,
										  String[] selectionArgs, String orderField) {
		return mBaseDal.selectByCondition(selection, selectionArgs, orderField);
	}

	/**
	 * 通过Sql语句实现查询
	 *
	 * @param sql
	 * @param selectionArgs
	 * @return
	 */
	public List<Object> queryBySql(String sql, String[] selectionArgs) {
		return mBaseDal.queryBySql(sql, selectionArgs);
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
		return mBaseDal.selectReturnCursor(columns, selection, selectionArgs,
				orderField);
	}

	/**
	 * 返回符合条件的数目
	 *
	 * @param selection
	 * @param selectionArgs
	 * @return
	 */
	public int selectCountByCondition(String selection, String[] selectionArgs) {
		return mBaseDal.selectCountByCondition(selection, selectionArgs);
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
		return mBaseDal.selectByCondition(selection, selectionArgs, orderField,
				limit);
	}

	/**
	 * 通過主鍵讀取對象
	 *
	 * @param keyValue
	 * @return
	 */
	public Object selectByKey(Object keyValue) {
		return mBaseDal.selectByKey(keyValue);
	}

	// / <summary>
	// / 更新记录
	// / </summary>
	// / <param name="obj">对象</param>
	// / <returns>受操作影响的行数</returns>
	public boolean update(Object obj) {
		return mBaseDal.update(obj);
	}

/*	public boolean update(String keyValue, String fieldName, String fieldVlaue) {
		return mBaseDal.update(keyValue, fieldName, fieldVlaue);
	}*/

	// / <summary>
	// / 插入记录
	// / </summary>
	// / <param name="obj">待插入对象</param>
	// / <returns>插入的记录ID</returns>
	public Object insert(Object obj) {
		return mBaseDal.insert(obj);
	}

	public void deleteByKeyValue(Object keyValue) {
		mBaseDal.deleteByKeyValue(keyValue);
	}

	public void deleteByCondition(String selection, String[] selectionArgs) {
		mBaseDal.deleteByCondition(selection, selectionArgs);
	}
	public void DropTabel(String mTableName) {
		mBaseDal.DropTabel(mTableName);
	}
	public void deleteAll() {
		mBaseDal.deleteAll();
	}

	public boolean isExist(String selection, String[] selectionArgs) {
		return mBaseDal.isExist(selection, selectionArgs);
	}

	public boolean isExist(Object keyValue) {
		return mBaseDal.isExist(keyValue);
	}

	public void execBySql(String sql) {
		mBaseDal.execBySql(sql);
	}
}
