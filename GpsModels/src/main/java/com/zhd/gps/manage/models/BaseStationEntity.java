package com.zhd.gps.manage.models;

public class BaseStationEntity {

	private double mLatitude = 0.0;
	private double mLongitude = 0.0;
	private double mHeight = 0.0;
	private String mBaseName = "";
	private int mBaseId = 0;
	private boolean mIsValid = false;

	public int getBaseId() {
		return mBaseId;
	}

	public void setBaseId(int baseId) {
		this.mBaseId = baseId;
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

	public String getBaseName() {
		return mBaseName;
	}

	public void setBaseName(String baseName) {
		this.mBaseName = baseName;
	}

	public double getHeight() {
		return mHeight;
	}

	public void setHeight(double mHeight) {
		this.mHeight = mHeight;
	}
}
