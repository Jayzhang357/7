package com.zhd.gps.manage.models;

public class SatelliteEntity {
	private int mSatNo = 0;
	private int mSatPrn = 0;
	private float mElevation = 0.0f;
	private float mAzimath = 0.0f;
	private int mSnrL1 = 0;
	private int mSnrL2 = 0;
	private GpsEnum.GpsType mGpsType=null;
	
	public int getSatNo() {
		return mSatNo;
	}
	public void setSatNo(int mSatNo) {
		this.mSatNo = mSatNo;
	}
	public int getSatPrn() {
		return mSatPrn;
	}
	public void setSatPrn(int mSatPrn) {
		this.mSatPrn = mSatPrn;
	}

	public float getElevation() {
		return mElevation;
	}

	public void setElevation(float mElevation) {
		this.mElevation = mElevation;
	}

	public float getAzimath() {
		return mAzimath;
	}

	public void setAzimath(float mAzimath) {
		this.mAzimath = mAzimath;
	}

	public int getSnrL1() {
		return mSnrL1;
	}

	public void setSnrL1(int mSnrL1) {
		this.mSnrL1 = mSnrL1;
	}

	public int getSnrL2() {
		return mSnrL2;
	}

	public void setSnrL2(int mSnrL2) {
		this.mSnrL2 = mSnrL2;
	}
	public GpsEnum.GpsType getGpsType() {
		return mGpsType;
	}
	public void setGpsType(GpsEnum.GpsType mGpsType) {
		this.mGpsType = mGpsType;
	}
}
