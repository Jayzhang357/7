package com.zhd.gps.manage.models;

public class GGAEntity {

	private String mUtcTime = "";
	private double mLatitude = 0.0;
	private double mLongitude = 0.0;
	private String mNthSthHemesphere = "N";
	private String mWstEstHemesphere = "E";
	private int mGpsStatus = 0;
	private int mCaculateSatNum = 0;
	private float mHdop = 0.0f;
	private double mHeight = 0.0;
	private String mDiffTime = "";
	private String mBaseStationNum = "";
	private float mUndulation = 0.0f;

	public String getUtcTime() {
		return mUtcTime;
	}

	public void setUtcTime(String mUtcTime) {
		this.mUtcTime = mUtcTime;
	}

	public double getLatitude() {
		return mLatitude;
	}

	public void setLatitude(double mLatitude) {
		this.mLatitude = mLatitude;
	}

	public double getLongitude() {
		return mLongitude;
	}

	public void setLongitude(double mLongitude) {
		this.mLongitude = mLongitude;
	}

	public String getNthSthHemesphere() {
		return mNthSthHemesphere;
	}

	public void setNthSthHemesphere(String mNthSthHemesphere) {
		this.mNthSthHemesphere = mNthSthHemesphere;
	}

	public String getWstEstHemesphere() {
		return mWstEstHemesphere;
	}

	public void setWstEstHemesphere(String mWstEstHemesphere) {
		this.mWstEstHemesphere = mWstEstHemesphere;
	}

	public int getGpsStatus() {
		return mGpsStatus;
	}

	public void setGpsStatus(int mGpsStatus) {
		this.mGpsStatus = mGpsStatus;
	}

	public int getCaculateSatNum() {
		return mCaculateSatNum;
	}

	public void setCaculateSatNum(int mCaculateSatNum) {
		this.mCaculateSatNum = mCaculateSatNum;
	}

	public float getHdop() {
		return mHdop;
	}

	public void setHdop(float mHdop) {
		this.mHdop = mHdop;
	}

	public double getHeight() {
		return mHeight;
	}

	public void setHeight(double mHeight) {
		this.mHeight = mHeight;
	}

	public String getDiffTime() {
		return mDiffTime;
	}

	public void setDiffTime(String mDiffTime) {
		this.mDiffTime = mDiffTime;
	}

	public String getBaseStationNum() {
		return mBaseStationNum;
	}

	public void setBaseStationNum(String mBaseStationNum) {
		this.mBaseStationNum = mBaseStationNum;
	}

	public float getUndulation() {
		return mUndulation;
	}

	public void setUndulation(float mUndulation) {
		this.mUndulation = mUndulation;
	}
	// public static GgaModel getInstance() {
	//
	// if (null == GGAMODEL) {
	// GGAMODEL = new GgaModel();
	// }
	//
	// return GGAMODEL;
	//
	// }
}
