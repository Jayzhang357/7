package com.zhd.ProtocolFilterManage;

import java.io.IOException;

public class Job_3g {


	private String mJobname;//是否作业
	private String mFieldname;//是否作业

	private int mImplementWidth;
	private int mImplementFrtBkOffset;
	private int mImplementLtRtOffset;
	private int mAB_displament;

	private int mLatitudeA; // A纬度
	private int mLongitudeA; // A经度
	private int mLatitudeB; // A纬度
	private int mLongitudeB; // A经度
	private String mAPP_VERSION;//是否作业

	private String mVerison;//是否作业

	private String mInsVerison;//是否作业

	private String mMduVerison;//是否作业

	private String mTB2Verison;//是否作业

	public byte[] ToByte() {
		Exchange ex = new Exchange();

		ex.AddStringAsBytes_1(mJobname);
		ex.AddStringAsBytes_1(mFieldname);
		ex.InttoBytes(mImplementWidth);
		ex.InttoBytes(mImplementFrtBkOffset);
		ex.InttoBytes(mImplementLtRtOffset);
		ex.InttoBytes(mAB_displament);
		ex.InttoBytes(mLatitudeA);
		ex.InttoBytes(mLongitudeA);
		ex.InttoBytes(mLatitudeB);
		ex.InttoBytes(mLongitudeB);
		ex.AddStringAsBytes_1(mAPP_VERSION);
		ex.AddStringAsBytes_1(mVerison);
		ex.AddStringAsBytes_1(mInsVerison);
		ex.AddStringAsBytes_1(mMduVerison);
		ex.AddStringAsBytes_1(mTB2Verison);
		/*
		 * for(int i=0;i<(int)ab_count;i++) {
		 * ex.AddIntAsBytes(ab_list[i].getLongitude());
		 * ex.AddIntAsBytes(ab_list[i].getLatitude()); }
		 */
		return ex.GetAllBytes();
	}
	public void LatitudeA_set(int value) {

		mLatitudeA = value;
	}

	public int LatitudeA_get() {

		return mLatitudeA;
	}
	public void LongitudeA_set(int value) {

		mLongitudeA = value;
	}

	public int LongitudeA_get() {

		return mLongitudeA;
	}
	public void LatitudeB_set(int value) {

		mLatitudeB = value;
	}

	public int LatitudeB_get() {

		return mLatitudeB;
	}
	public void LongitudeB_set(int value) {

		mLongitudeB = value;
	}

	public int LongitudeB_get() {

		return mLongitudeB;
	}




	public void ImplementWidth_set(int value) {

		mImplementWidth = value;
	}

	public int ImplementWidth_get() {

		return mImplementWidth;
	}
	public void ImplementFrtBkOffset_set(int value) {

		mImplementFrtBkOffset = value;
	}

	public int ImplementFrtBkOffset_get() {

		return mImplementFrtBkOffset;
	}
	public void ImplementLtRtOffset_set(int value) {

		mImplementLtRtOffset = value;
	}

	public int ImplementLtRtOffset_get() {

		return mImplementLtRtOffset;
	}
	public void AB_displament_set(int value) {

		mAB_displament = value;
	}

	public int AB_displament_get() {

		return mAB_displament;
	}
	public void Fieldname_set(String value) {

		mFieldname = value;
	}

	public String Fieldname_get() {

		return mFieldname;
	}
	public void Jobname_set(String value) {

		mJobname = value;
	}

	public String Jobname_get() {

		return mJobname;
	}
	public void APP_VERSION_set(String value) {

		mAPP_VERSION = value;
	}

	public String APP_VERSION_get() {

		return mAPP_VERSION;
	}
	public void Verison_set(String value) {

		mVerison = value;
	}

	public String Verison_get() {

		return mVerison;
	}
	public void InsVerison_set(String value) {

		mInsVerison = value;
	}

	public String InsVerison_get() {

		return mInsVerison;
	}	public void MduVerison_set(String value) {

		mMduVerison = value;
	}

	public String MduVerison_get() {

		return mMduVerison;
	}
	public void TB2Verison_set(String value) {

		mTB2Verison = value;
	}

	public String TB2Verison_get() {

		return mTB2Verison;
	}
}
