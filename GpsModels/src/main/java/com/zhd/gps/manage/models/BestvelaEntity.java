package com.zhd.gps.manage.models;

public class BestvelaEntity {

	private double mSpeedRate = 0.0;
	private double mTDirection = 0.0;
	private double mVert = 0.0;

	public double getSpeedRate() {
		return mSpeedRate;
	}

	public void setSpeedRate(double mSpeedRate) {
		this.mSpeedRate = mSpeedRate;
	}

	public double getTDirection() {
		return mTDirection;
	}

	public void setTDirection(double mTDirection) {
		this.mTDirection = mTDirection;
	}

	// 垂直速度
	public double getVert() {
		return mVert;
	}

	public void setVert(double mVert) {
		this.mVert = mVert;
	}
}
