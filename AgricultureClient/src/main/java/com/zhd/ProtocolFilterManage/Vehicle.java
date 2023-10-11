package com.zhd.ProtocolFilterManage;

import java.io.IOException;

public class Vehicle {
	private int mWheelBase;// 车辆轴距
	private int mAntennaForeOrAftValue;// 天线左右偏移
	private int mAntennalLateralOffset;// 天线前后偏移
	private int mAntennaHeight;// 天线高度
	private int mEcuLateralOffset;// ECU前后偏移
	private int mEcuForeOrAftValue;//  ECU左右偏移
	private int mEcuHeight;// ECU天线高度
	private int mGeo;// 姿态
	private int mAtti;// 安装方向
	public byte[] ToByte() {
		Exchange ex = new Exchange();
		ex.InttoBytes(mWheelBase);
		ex.InttoBytes(mAntennalLateralOffset);
		ex.InttoBytes(mAntennaForeOrAftValue);
		ex.InttoBytes(mAntennaHeight);
		ex.InttoBytes(mEcuLateralOffset);
		ex.InttoBytes(mEcuForeOrAftValue);
		ex.InttoBytes(mEcuHeight);
		ex.InttoBytes(mGeo);
		ex.InttoBytes(mAtti);

		/*
		 * for(int i=0;i<(int)ab_count;i++) {
		 * ex.AddIntAsBytes(ab_list[i].getLongitude());
		 * ex.AddIntAsBytes(ab_list[i].getLatitude()); }
		 */
		return ex.GetAllBytes();
	}

	public void WheelBase_set(int value) {

		mWheelBase = value;
	}

	public int WheelBase_get() {

		return mWheelBase;
	}
	public void AntennaForeOrAftValue_set(int value) {

		mAntennaForeOrAftValue = value;
	}

	public int AntennaForeOrAftValue_get() {

		return mAntennaForeOrAftValue;
	}
	public void AntennalLateralOffset_set(int value) {

		mAntennalLateralOffset = value;
	}

	public int AntennalLateralOffset_get() {

		return mAntennalLateralOffset;
	}
	public void AntennaHeight_set(int value) {

		mAntennaHeight = value;
	}

	public int AntennaHeight_get() {

		return mAntennaHeight;
	}
	public void EcuLateralOffset_set(int value) {

		mEcuLateralOffset = value;
	}

	public int EcuLateralOffset_get() {

		return mEcuLateralOffset;
	}
	public void EcuForeOrAftValue_set(int value) {

		mEcuForeOrAftValue = value;
	}

	public int EcuForeOrAftValue_get() {

		return mEcuForeOrAftValue;
	}
	public void EcuHeight_set(int value) {

		mEcuHeight = value;
	}

	public int EcuHeight_get() {

		return mEcuHeight;
	}
	public void Geo_set(int value) {

		mGeo = value;
	}

	public int Geo_get() {

		return mGeo;
	}
	public void Atti_set(int value) {

		mAtti = value;
	}

	public int Atti_get() {

		return mAtti;
	}
}
