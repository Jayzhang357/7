package com.zhd.gps.manage.models;



public class ReadcarEntity {
	private float WheelBase= 0;//轴距
	private float Height=0;//天线高度
	private float ForeOrAftValue= 0;//天线左右位移
	private float LateralOffset= 0;//天线左右位移
	private int Carstye= 0;//车辆类型
	private int Direction= 0;//安装方向
	public int getDirection() {
		return Direction;
	}
	public void setDirection(int Direction) {
		this.Direction = Direction;
	}
	public int getCarstye() {
		return Carstye;
	}
	public void setCarstye(int Carstye) {
		this.Carstye = Carstye;
	}
	public float getLateralOffset() {
		return LateralOffset;
	}
	public void setLateralOffset(float LateralOffset) {
		this.LateralOffset = LateralOffset;
	}
	public float getHeight() {
		return Height;
	}
	public void setHeight(float Height) {
		this.Height = Height;
	}
	public float getWheelBase() {
		return WheelBase;
	}
	public void setWheelBase(float WheelBase) {
		this.WheelBase = WheelBase;
	}
	public float getForeOrAftValue() {
		return ForeOrAftValue;
	}
	public void setForeOrAftValue(float ForeOrAftValue) {
		this.ForeOrAftValue = ForeOrAftValue;
	}
}
