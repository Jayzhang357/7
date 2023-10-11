package com.zhd.shj.business;

import android.content.Context;

import com.zhd.shj.entity.JobInfo;
import com.zhd.shj.entity.JobRecordInfo;

import java.util.ArrayList;


public class Job extends BaseBusiness {

	private com.zhd.shj.dal.Job mJob = null;
	private Context mContext = null;

	public Job(Context context) {
		super(new com.zhd.shj.dal.Job(context));
		mJob = (com.zhd.shj.dal.Job) super.getBaseDAL();
		mContext = context;
	}

	public Job(Context context, String dataBasePath) {
		super(new com.zhd.shj.dal.Job(context, dataBasePath));
		mJob = (com.zhd.shj.dal.Job) super.getBaseDAL();
	}

	/**
	 * 返回之前选中的数据
	 *
	 * @return
	 */
	public JobInfo getSelectedJob() {
		return mJob.getSelectedJob();
	}

	/**
	 * 是否存在相同的名称
	 *
	 * @param name
	 * @return
	 */
	public boolean existName(String name) {
		return mJob.existName(name);
	}

	public String getJobRecordTableName(int id) {
		String tableName = "HF_table_" + Integer.toString(id);

		return tableName;
	}

	/**
	 * 插入事务
	 *
	 * @param jobInfo
	 * @return 记录名称
	 */
	public boolean insertJob(JobInfo jobInfo,
							 boolean isNeedSelected) {
		return mJob.insertJob(jobInfo, isNeedSelected);

	}

	/**
	 * 插入一条轨迹记录
	 *
	 * @param info
	 */
	public void insertJobRecord(JobRecordInfo info) {
		mJob.insertJobRecord(info);
	}


	/**
	 * 返回线段的最大值
	 *
	 * @return
	 */
	public int getMaxRecordSegmentIndex() {

		return mJob.getMaxRecordSegmentIndex();
	}
	public boolean updateSelected() {

		return mJob.updateSelected();
	}

	/**
	 * 返回坐标记录列表
	 *
	 * @return
	 */
	public ArrayList<JobRecordInfo> getRecordList(int boxXmin,
												  int boxYmin, int boxXmax, int boxYmax) {
		return mJob.getRecordList(boxXmin, boxYmin, boxXmax, boxYmax);
	}


	/**
	 * 返回坐标记录列表
	 *
	 * @return
	 */
	public ArrayList<JobRecordInfo> getRecordList_1(int boxXmin,
												  int boxYmin, int boxXmax, int boxYmax) {
		return mJob.getRecordList_1(boxXmin, boxYmin, boxXmax, boxYmax);
	}
	/**
	 * 更新并且同时更新记录表名称
	 *
	 * @param obj
	 * @return
	 */
	public boolean updateAndRefreshTableName(Object obj) {
		return mJob.updateAndRefreshTableName(obj);
	};



	/**
	 * 返回最小格网的索引值
	 *
	 * @param
	 * @return
	 */
	public int getSmallGridIndex(int h, int w) {
		return mJob.getSmallGridIndex(h, w);
	}



	/**
	 * 删除作业轨迹
	 *
	 * @param jobInfo
	 * @return
	 */
	public boolean deleteJobCoverage(JobInfo jobInfo) {
		return mJob.deleteJobCoverage(jobInfo);
	}

}
