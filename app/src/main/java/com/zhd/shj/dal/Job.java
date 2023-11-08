package com.zhd.shj.dal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.zhd.shj.entity.CommonEnum;
import com.zhd.shj.entity.JobInfo;
import com.zhd.shj.entity.JobRecordInfo;

import java.util.ArrayList;
import java.util.List;

public class Job extends BaseDAL {
    private String mCurrentRecordTableName = ""; // 当前关联的轨迹表
    private JobRecord mCurrentJobRecord = null;
    private Context mContext = null;


    public Job(Context context) {
        super(context, DatabaseHelper.GetConnectionString(), "HF_Job");

        mContext = context;

        mKeyIsIdentity = true;


    }

    public Job(Context context, String dataBasePath) {
        super(context, dataBasePath, "HF_Job");

        mContext = context;

        mKeyIsIdentity = true;


    }

    @SuppressLint("Range")
    protected JobInfo dataReaderToEntity(Cursor cursor) {
        JobInfo jobInfo = new JobInfo();
        jobInfo.ID = cursor.getInt(cursor.getColumnIndex("ID"));
        jobInfo.AbType = cursor.getInt(cursor.getColumnIndex("AbType"));
        jobInfo.APointB = (cursor.getDouble(cursor.getColumnIndex("APointB")));
        jobInfo.APointL = (cursor.getDouble(cursor.getColumnIndex("APointL")));
        jobInfo.APointH = (cursor.getDouble(cursor.getColumnIndex("APointH")));
        jobInfo.BPointB = (cursor.getDouble(cursor.getColumnIndex("BPointB")));
        jobInfo.BPointL = (cursor.getDouble(cursor.getColumnIndex("BPointL")));
        jobInfo.BPointH = (cursor.getDouble(cursor.getColumnIndex("BPointH")));
        jobInfo.CPointB = (cursor.getDouble(cursor.getColumnIndex("CPointB")));
        jobInfo.CPointL = (cursor.getDouble(cursor.getColumnIndex("CPointL")));
        jobInfo.CPointH = (cursor.getDouble(cursor.getColumnIndex("CPointH")));
        jobInfo.setH = (cursor.getDouble(cursor.getColumnIndex("setH")));
        jobInfo.setHsmall = (cursor.getDouble(cursor.getColumnIndex("setHsmall")));
        jobInfo.Width = (cursor.getDouble(cursor.getColumnIndex("Width")));
        jobInfo.Sensitivity = (cursor.getDouble(cursor.getColumnIndex("Sensitivity")));
        jobInfo.RecordTableName = (cursor.getString(cursor
                .getColumnIndex("RecordTableName")));
        jobInfo.JobName = (cursor.getString(cursor.getColumnIndex("JobName")));
        jobInfo.CoverageArea = cursor.getFloat(cursor
                .getColumnIndex("CoverageArea"));
        jobInfo.IsSelected = (cursor
                .getInt(cursor.getColumnIndex("IsSelected")));


        jobInfo.CreateTime = (cursor.getLong(cursor
                .getColumnIndex("CreateTime")));


        return jobInfo;
    }

    /**
     * 字符串的ID为32位UUID值
     */
    // protected Object setKeyValue(Object obj) {
    //
    // JobInfo info = (JobInfo) obj;
    // if (info.ID == null || info.ID.length() <= 0) {
    // info.ID = (UUID.randomUUID().toString());
    // }
    // return info.ID;
    // }
    protected Object setKeyValue(Object obj) {

        if (((JobInfo) obj).ID != -1)
            return ((JobInfo) obj).ID;

        SQLiteDatabase db = this.getReadableDatabase();
        int maxID = DatabaseHelper.getMaxKey(db, this.mTableName, "ID");

        return maxID;
    }

    /**
     * 返回之前选中的数据
     *
     * @param
     * @return
     */
    public JobInfo getSelectedJob() {
        String condition = "IsSelected = ?";
        String[] args = {Integer
                .toString(CommonEnum.DbBoolean.True.getValue())};

        List<Object> objList = selectByCondition(condition, args, "CreateTime");
        if (objList != null && objList.size() > 0) {
            return (JobInfo) objList.get(0);
        } else
            return null;
    }

    /**
     * 返回之前选中的数据
     *
     * @param
     * @return
     */
    protected List<Object> getSelectedJobList() {
        String condition = "IsSelected = ?";
        String[] args = {Integer
                .toString(CommonEnum.DbBoolean.True.getValue())};

        List<Object> objList = selectByCondition(condition, args, "CreateTime");
        if (objList != null && objList.size() > 0) {
            return objList;
        } else
            return null;
    }

    /**
     * 是否存在相同的名称
     *
     * @param name
     * @return
     */
    public boolean existName(String name) {
        String condition = "JobName = ?";
        String[] args = {name};

        return selectCountByCondition(condition, args) > 0 ? true : false;
    }


    //重新选中作业
    public boolean updateSelected() {
        List<Object> selectJobList = getSelectedJobList();

        // 取出最大值
        SQLiteDatabase db = getReadableDatabase();
        // 重新实例化为可写入的数据库对象 用于事务处理
        db = getWritableDatabase();
        db.beginTransaction();
        boolean isUpdateSuccess;
        try {
            // 取出之前的任务 并把选中取消
            if (selectJobList != null) {
                for (Object object : selectJobList) {
                    JobInfo selectedJobInfo = (JobInfo) object;
                    selectedJobInfo.IsSelected = CommonEnum.DbBoolean.False
                            .getValue();

                    isUpdateSuccess = updateSync(selectedJobInfo, db);
                    if (isUpdateSuccess == false)
                        return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            db.endTransaction();
            db.close();

        }
        return true;
    }

    public boolean insertJob(JobInfo jobInfo,
                             boolean isNeedSelected) {
        String tableName = "";
        int maxID = -1;

        // 取出之前的任务
        List<Object> selectJobList = getSelectedJobList();

        // 取出最大值
        SQLiteDatabase db = getReadableDatabase();
        maxID = DatabaseHelper.getMaxKey(db, this.mTableName, "ID");
        maxID += 1;

        // 重新实例化为可写入的数据库对象 用于事务处理
        db = getWritableDatabase();
        db.beginTransaction();

        int rowid = -1;
        boolean isUpdateSuccess = true;
        try {
            // 取出之前的任务 并把选中取消
            if (isNeedSelected) {
                if (selectJobList != null) {
                    for (Object object : selectJobList) {
                        JobInfo selectedJobInfo = (JobInfo) object;
                        selectedJobInfo.IsSelected = CommonEnum.DbBoolean.False
                                .getValue();
                        isUpdateSuccess = updateSync(selectedJobInfo, db);
                        if (isUpdateSuccess == false)
                            return false;
                    }
                }
            }

            // 插入新作业实体
            // 创建记录表名 通过10位随机数字字母组合成表名 目的在于进行导入操作的时候不会出现表名重复的问题
            tableName = "HF_Record_" + DatabaseHelper.getCharAndNumr();
            jobInfo.RecordTableName = tableName;
            long row = Long.parseLong(insertSync(jobInfo, db).toString());
            rowid = (int) row;
            if (row != -1) {
                // 构建表
                String sql = "CREATE TABLE [" + tableName + "] ("
                        + "[ID] INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "

                        + " [SegmentIndex] INTEGER NOT NULL, "
                        + " [B] DOUBLE NOT NULL, "
                        + " [L] DOUBLE NOT NULL, "
                         + " [H] DOUBLE NOT NULL);";


                // 执行sql
                db.execSQL(sql);


                rowid = (int) row;
                if (rowid != -1) {
                    db.setTransactionSuccessful();
                } else {
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();

            return false;

        } finally {
            db.endTransaction();

            db.close();
        }

        return true;
    }

    public boolean modifyJob(JobInfo jobInfo, String oldJobName) {

        // 取出最大值
        SQLiteDatabase db = getReadableDatabase();
        // 重新实例化为可写入的数据库对象 用于事务处理
        db = getWritableDatabase();
        db.beginTransaction();

        boolean isUpdateSuccess = true;
        try {
            // 取出之前的任务 并把选中取消
            if (jobInfo != null) {
                isUpdateSuccess = updateSync(jobInfo, db);
                if (isUpdateSuccess == false)
                    return false;
            }

            String condition = "JobName = ?";
            String[] args = {oldJobName};

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            db.endTransaction();
            db.close();
        }

        return true;
    }


    public boolean updateAndRefreshTableName(Object obj) {
        boolean res = super.update(obj);

        refreshRecordName();

        return res;
    }

    ;

    /**
     * 更新当前选中的作业表名
     */
    public void refreshRecordName() {
        JobInfo jobInfo = this.getSelectedJob();
        if (jobInfo != null) {
            mCurrentRecordTableName = jobInfo.RecordTableName;

            // 表名改变了，需要重新实例化
            mCurrentJobRecord = new JobRecord(mContext, mCurrentRecordTableName);

            Log.i("refreshTable", "yes");
        }
    }

    /**
     * 插入一条轨迹记录
     *
     * @param info
     */
    public void insertJobRecord(JobRecordInfo info) {
        if (mCurrentJobRecord == null || mCurrentRecordTableName.equals("")) {
            refreshRecordName();
        }

        mCurrentJobRecord.insert(info);
    }

    /**
     * 返回线段的最大值
     *
     * @return
     */
    public int getMaxRecordSegmentIndex() {
        int maxID = -1;

        if (mCurrentRecordTableName.equals("")) {
            refreshRecordName();
        }
        if (!mCurrentRecordTableName.equals("")) {

            SQLiteDatabase db = this.getReadableDatabase();
            maxID = DatabaseHelper.getMaxKey(db, mCurrentRecordTableName,
                    "SegmentIndex");

            Log.i("segmentindex",
                    "CurrentRecordTableName:" + mCurrentRecordTableName
                            + ",segmentindex:" + Integer.toString(maxID));
        }

        return maxID;
    }
    /**
     * 返回线段的最大值
     *
     * @return
     */
    public int getMaxRecordSegmentIndex_1() {
        int maxID = -1;

        if (mCurrentRecordTableName.equals("")) {
            refreshRecordName();
        }
        if (!mCurrentRecordTableName.equals("")) {

            SQLiteDatabase db = this.getReadableDatabase();
            maxID = DatabaseHelper.getMaxKey(db, mCurrentRecordTableName,
                    "SegmentIndex");

            Log.i("segmentindex",
                    "CurrentRecordTableName:" + mCurrentRecordTableName
                            + ",segmentindex:" + Integer.toString(maxID));
        }

        return maxID+1;
    }

    /**
     * 返回坐标记录列表
     *
     * @return
     */
    public ArrayList<JobRecordInfo> getRecordList(double boxXmin,
                                                  double boxYmin, double boxXmax, double boxYmax) {
        ArrayList<JobRecordInfo> recordList = new ArrayList<JobRecordInfo>();

        if (mCurrentJobRecord == null || mCurrentRecordTableName.equals("")) {
            refreshRecordName();
        }

        recordList = mCurrentJobRecord.getCoordList();

        return recordList;
    }

    /**
     * 返回坐标记录列表
     *
     * @return
     */
    public ArrayList<JobRecordInfo> getRecordList_1(double boxXmin,
                                                  double boxYmin, double boxXmax, double boxYmax) {
        ArrayList<JobRecordInfo> recordList = new ArrayList<JobRecordInfo>();

        if (mCurrentJobRecord == null || mCurrentRecordTableName.equals("")) {
            refreshRecordName();
        }

        recordList = mCurrentJobRecord.getCoordList(boxXmin, boxYmin, boxXmax,
                boxYmax);

        return recordList;
    }



    /**
     * 返回最小格网的索引值
     *
     * @param
     * @return
     */
    public int getSmallGridIndex(int h, int w) {
        String condition = "SmallGridIndexX = ? and SmallGridIndexY = ?";
        String[] args = {Integer.toString(w), Integer.toString(h)};

        if (mCurrentJobRecord == null) {
            if (mCurrentRecordTableName.equals("")) {
                refreshRecordName();
            }
        }

        return mCurrentJobRecord.selectCountByCondition(condition, args);
    }

    public boolean deleteJobCoverage(JobInfo jobInfo) {
        boolean result = true;
        JobRecord jobRecord = new JobRecord(mContext, jobInfo.RecordTableName);
        jobRecord.deleteAll();
        return result;
    }
}
