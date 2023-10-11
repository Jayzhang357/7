package com.zhd.shj.dal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


import com.zhd.AppHelper;
import com.zhd.shj.entity.JobRecordInfo;

import java.util.ArrayList;

public class JobRecord extends BaseDAL {
    public JobRecord(Context context, String tableName) {
        super(context, DatabaseHelper.GetConnectionString(), tableName);

        mKeyIsIdentity = true;
    }

    @SuppressLint("Range")
    protected JobRecordInfo dataReaderToEntity(Cursor cursor) {
        JobRecordInfo taskRecordInfo = new JobRecordInfo();
        taskRecordInfo.ID = cursor.getInt(cursor.getColumnIndex("ID"));

        taskRecordInfo.SegmentIndex = cursor.getInt(cursor
                .getColumnIndex("SegmentIndex"));

        taskRecordInfo.B = cursor.getFloat(cursor.getColumnIndex("B"));
        taskRecordInfo.L = cursor.getFloat(cursor.getColumnIndex("L"));
        taskRecordInfo.H = cursor.getFloat(cursor.getColumnIndex("H"));

        return taskRecordInfo;
    }

    protected Object setKeyValue(Object obj) {

        if (((JobRecordInfo) obj).ID != -1)
            return ((JobRecordInfo) obj).ID;

        SQLiteDatabase db = this.getReadableDatabase();
        int maxID = 0;

        maxID = DatabaseHelper.getMaxKey(db, this.mTableName, "ID");

        return maxID;
    }
    /**
     * 返回指定索引区域内的坐标集合
     *
     * @param
     * @param
     * @return
     */
    @SuppressLint("Range")
    public ArrayList<JobRecordInfo> getCoordList() {


        ArrayList<JobRecordInfo> segmentCoordList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        String condition = "SELECT SegmentIndex,B,L,ID,H FROM "
                + this.mTableName
                + " ORDER BY  ID DESC";
        Log.e("作业名称1", AppHelper.JOB_INFO .JobName+condition);
        String[] args = {};
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(condition, args);


            if (cursor != null && cursor.moveToLast()) {
                while (!cursor.isFirst()) {
                    try {
                        JobRecordInfo abcd = new JobRecordInfo();

                        abcd.B = cursor.getDouble(cursor
                                .getColumnIndex("B"));

                        abcd.L = cursor.getDouble(cursor
                                .getColumnIndex("L"));
                        abcd.H = cursor.getDouble(cursor
                                .getColumnIndex("H"));

                        abcd.ID = cursor.getInt(cursor
                                .getColumnIndex("ID"));
                        Log.e("ID",   abcd.ID+";" +cursor.isAfterLast());
                        abcd.SegmentIndex = cursor.getInt(cursor
                                .getColumnIndex("SegmentIndex"));
                        segmentCoordList.add(abcd);

                    } catch (Exception e) {

                    }
                    cursor.moveToPrevious();

                }
                JobRecordInfo abcd = new JobRecordInfo();

                abcd.B = cursor.getDouble(cursor
                        .getColumnIndex("B"));

                abcd.L = cursor.getDouble(cursor
                        .getColumnIndex("L"));
                abcd.H = cursor.getDouble(cursor
                        .getColumnIndex("H"));

                abcd.ID = cursor.getInt(cursor
                        .getColumnIndex("ID"));
                Log.e("ID",   abcd.ID+";" +cursor.isAfterLast());
                abcd.SegmentIndex = cursor.getInt(cursor
                        .getColumnIndex("SegmentIndex"));
                segmentCoordList.add(abcd);
            }
        } catch (Exception e) {
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

        } finally {
            if (cursor != null) {
                cursor.close();// 2
            }
            db.close();
        }


        return segmentCoordList;
    }
    /**
     * 返回指定索引区域内的坐标集合
     *
     * @param
     * @param
     * @return
     */
    @SuppressLint("Range")
    public ArrayList<JobRecordInfo> getCoordList(double boxXmin, double boxYmin,
                                                 double boxXmax, double boxYmax) {


        ArrayList<JobRecordInfo> segmentCoordList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        String condition = "SELECT SegmentIndex,B,L,ID FROM "
                + this.mTableName
                + " WHERE B  >= ? AND L >= ? AND B  <= ? AND L <= ? ORDER BY  ID DESC  LIMIT 0,200";

        String[] args = {Double.toString(boxXmin), Double.toString(boxYmin),

                Double.toString(boxXmax), Double.toString(boxYmax)};
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(condition, args);


            if (cursor != null && cursor.moveToLast()) {
                while (!cursor.isFirst()) {
                    try {
                        JobRecordInfo abcd = new JobRecordInfo();

                        abcd.B = cursor.getDouble(cursor
                                .getColumnIndex("B"));

                        abcd.L = cursor.getDouble(cursor
                                .getColumnIndex("L"));


                        abcd.ID = cursor.getInt(cursor
                                .getColumnIndex("ID"));
                        Log.e("ID",   abcd.ID+";" +cursor.isAfterLast());
                        abcd.SegmentIndex = cursor.getInt(cursor
                                .getColumnIndex("SegmentIndex"));
                        segmentCoordList.add(abcd);

                    } catch (Exception e) {

                    }
                    cursor.moveToPrevious();

                }
                JobRecordInfo abcd = new JobRecordInfo();

                abcd.B = cursor.getDouble(cursor
                        .getColumnIndex("B"));

                abcd.L = cursor.getDouble(cursor
                        .getColumnIndex("L"));


                abcd.ID = cursor.getInt(cursor
                        .getColumnIndex("ID"));
                Log.e("ID",   abcd.ID+";" +cursor.isAfterLast());
                abcd.SegmentIndex = cursor.getInt(cursor
                        .getColumnIndex("SegmentIndex"));
                segmentCoordList.add(abcd);
            }
        } catch (Exception e) {
            int i=0;
            i++;
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

        } finally {
            if (cursor != null) {
                cursor.close();// 2
            }
            db.close();
        }


        return segmentCoordList;
    }
}
