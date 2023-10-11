package com.zhd.shj.business;


import com.zhd.shj.dal.BaseDAL;

public class JobRecord extends BaseBusiness {

	public JobRecord(BaseDAL baseDal) {
		super(baseDal);
		// TODO Auto-generated constructor stub
	}

	private com.zhd.shj.dal.JobRecord mTaskRecordDal = null;

//	public JobRecord(Context context) {
//		super(new com.zhd.hifarm2.dal.JobRecord(context));
//		mTaskRecordDal = (com.zhd.hifarm2.dal.JobRecord) super.getBaseDAL();
//	}

}
