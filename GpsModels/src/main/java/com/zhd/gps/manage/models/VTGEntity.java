package com.zhd.gps.manage.models;

public class VTGEntity {

	private double mSpeedRate = 0.0;
	private double mTDirection = 0.0;
	private double mMDirection = 0.0;
	private VTGMode mvtgmode=null;


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

	public double getMDirection() {
		return mMDirection;
	}

	public void setMDirection(double mMDirection) {
		this.mMDirection = mMDirection;
	}
	public VTGMode getvtgMode(){
		return mvtgmode;
	}
	public void setvtgMode(char vtgmodeMark){

	}
	private class VTGMode{
		//自主定位
		public final static char SelfLocalization='C';
		//差分
		public final static char Difference='D';
		//估算
		public final static char Estimation='E';
		//数据无效
		public final static char Invalid='N';
	}
}
