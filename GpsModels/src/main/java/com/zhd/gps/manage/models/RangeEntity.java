package com.zhd.gps.manage.models;

import java.util.HashMap;
import java.util.Map;


public class RangeEntity {
	private int mPrn = 0;
	private int mSvType = 0;
	private float mL1 = 0;
	private float mL2 = 0;
	
	public int getPrn() {
		return mPrn;
	}

	public void setPrn(int prn) {
		this.mPrn = prn;
	}

	public int getSvType() {
		return mSvType;
	}

	public void setSvType(int svType) {
		this.mSvType = svType;
	}

	public float getL1() {
		return mL1;
	}

	public void setL1(float l1) {
		this.mL1 = l1;
	}

	public float getL2() {
		return mL2;
	}

	public void setL2(float l2) {
		this.mL2 = l2;
	}
}
