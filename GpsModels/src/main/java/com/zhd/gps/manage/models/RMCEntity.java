package com.zhd.gps.manage.models;

import java.util.Date;


public class RMCEntity {

	private Date mUtcDate=  new Date();
	private float mSpeedRate=0.0f;
	private float mHeading=0.0f;

	public float getSpeedRate() {
		return mSpeedRate;
	}
	public void setSpeedRate(float mSpeedRate) {
		this.mSpeedRate = mSpeedRate;
	}
	public float getHeading() {
		return mHeading;
	}
	public void setHeading(float mHeading) {
		this.mHeading = mHeading;
	}
	public Date getUtcDate() {
		return mUtcDate;
	}
	public void setUtcDate(Date mUtcDate) {
		this.mUtcDate = mUtcDate;
	}
}
