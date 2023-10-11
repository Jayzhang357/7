package com.zhd.gps.manage.models;



public class ReadSenEntity {
	private float mLine= 0;//轴距
	private float mTurn=0;//天线高度
	private int mType=0;//天线高度
	private float mBack=0;//天线高度
	public int getType() {
		return mType;
	}
	public void setType(int Type) {
		this.mType = Type;
	}
	public float getBack() {
		return mBack;
	}
	public void setBack(float Back) {
		this.mBack = Back;
	}
	public float getTurn() {
		return mTurn;
	}
	public void setTurn(float Turn) {
		this.mTurn = Turn;
	}
	public float getLine() {
		return mLine;
	}
	public void setLine(float Line) {
		this.mLine = Line;
	}
}
