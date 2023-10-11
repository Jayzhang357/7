package com.zhd.gps.manage.models;

public class ZCBYEntity {

	double mDeviation=0;//偏差
	double mDirectiondifference=0;//方向差
	double mTargetangle=0;//目标角
	double mAngleofmeasurement=0;//测量角
	double mCalculationangle=0;//计算角
	double mHeading=0;//航向
	double mVTG=0;//vtg
	double mRadioPower=0;//场强
	double mSpeed=0;//速度
	int mHeadingState=0;//状态位
	int mGPSState=0;//状态位
	int  mSatellitenumber=0;//卫星数
	double mDifferentialage=0;//差分龄期
	double mX=0;//x坐标
	double mY=0;//Y坐标
	double mB=0;//B坐标
	double mL=0;//L坐标
	double mTime=0;//时间
	int mWorkon=0;//启停
	double mVoltage=0;//电压
	double mElectric=0;//电流
	//double mRadioIntensity=0;//方向差
	double mLevel=0;//翻滚
	int mErrorType_s=0;//启停
	boolean mRV=false;
	boolean[] mErrorType = new boolean[9];
	public boolean getmRV() {
		return mRV;
	}

	public void setmRV(boolean RV) {
		this.mRV = RV;
	}
	public int getmErrorType_s() {
		return mErrorType_s;
	}

	public void setmErrorType_s(int ErrorType_s) {
		this.mErrorType_s = ErrorType_s;
	}
	public boolean[] getErrorType() {
		return mErrorType;
	}

	public void setErrorType(boolean[] ErrorType) {
		this.mErrorType = ErrorType;
	}
	public double getRadioPower() {
		return mRadioPower;
	}

	public void setRadioPower(double RadioPower) {
		this.mRadioPower = RadioPower;
	}
	public double getLevel() {
		return mLevel;
	}

	public void setLevel(double mLevel) {
		this.mLevel = mLevel;
	}
	/*	public double getRadioIntensity() {
            return mRadioIntensity;
        }

        public void setRadioIntensity(double mRadioIntensity) {
            this.mRadioIntensity = mRadioIntensity;
        }*/
	public int getGPSState() {
		return mGPSState;
	}

	public void setGPSState(int mGPSState) {
		this.mGPSState = mGPSState;
	}
	public double getElectric() {
		return mElectric;
	}

	public void setElectric(double mElectric) {
		this.mElectric = mElectric;
	}
	public double getVoltage() {
		return mVoltage;
	}

	public void setVoltage(double mVoltage) {
		this.mVoltage = mVoltage;
	}
	public int getWorkon() {
		return mWorkon;
	}

	public void setWorkon(int mWorkon) {
		this.mWorkon = mWorkon;
	}
	public double getTime() {
		return mTime;
	}

	public void setTime(double mTime) {
		this.mTime = mTime;
	}
	public double getB() {
		return mB;
	}

	public void setB(double mB) {
		this.mB= mB;
	}
	public double getL() {
		return mL;
	}

	public void setL(double mL) {
		this.mL = mL;
	}
	public double getY() {
		return mY;
	}

	public void setY(double mY) {
		this.mY = mY;
	}
	public double getX() {
		return mX;
	}

	public void setX(double mX) {
		this.mX = mX;
	}
	public double getDifferentialage() {
		return mDifferentialage;
	}

	public void setDifferentialage(double mDifferentialage) {
		this.mDifferentialage = mDifferentialage;
	}
	public int getSatellitenumber() {
		return mSatellitenumber;
	}

	public void setSatellitenumber(int mSatellitenumber) {
		this.mSatellitenumber = mSatellitenumber;
	}
	public int getHeadingState() {
		return mHeadingState;
	}

	public void setHeadingState(int mHeadingState) {
		this.mHeadingState = mHeadingState;
	}
	public double getSpeed() {
		return mSpeed;
	}

	public void setSpeed(double mSpeed) {
		this.mSpeed = mSpeed;
	}
	public double getVTG() {
		return mVTG;
	}

	public void setVTG(double mVTG) {
		this.mVTG = mVTG;
	}
	public double getHeading() {
		return mHeading;
	}

	public void setHeading(double mHeading) {
		this.mHeading = mHeading;
	}
	public double getCalculationangle() {
		return mCalculationangle;
	}

	public void setCalculationangle(double mCalculationangle) {
		this.mCalculationangle = mCalculationangle;
	}
	public double getAngleofmeasurement() {
		return mAngleofmeasurement;
	}

	public void setAngleofmeasurement(double mAngleofmeasurement) {
		this.mAngleofmeasurement = mAngleofmeasurement;
	}
	public double getTargetangle() {
		return mTargetangle;
	}

	public void setTargetangle(double mTargetangle) {
		this.mTargetangle = mTargetangle;
	}
	public double getDeviation() {
		return mDeviation;
	}

	public void setDeviation(double mDeviation) {
		this.mDeviation = mDeviation;
	}
	public double getDirectiondifference() {
		return mDirectiondifference;
	}

	public void setDirectiondifference(double mDirectiondifference) {
		this.mDirectiondifference = mDirectiondifference;
	}

}
