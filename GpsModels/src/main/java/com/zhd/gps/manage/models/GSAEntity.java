package com.zhd.gps.manage.models;

public class GSAEntity {
	private float mHDOP = 0.0f;
	private float mVDOP = 0.0f;
	private float mPDOP = 0.0f;


	public float getHDOP() {
		return mHDOP;
	}

	public void setHDOP(float mHDOP) {
		this.mHDOP =  mHDOP;
	}

	public float getVDOP() {
		return mVDOP;
	}

	public void setVDOP(float mVDOP) {
		this.mVDOP = mVDOP;
	}

	public float getPDOP() {
		return mPDOP;
	}

	public void setPDOP(float mPDOP) {
		this.mPDOP = mPDOP;
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
