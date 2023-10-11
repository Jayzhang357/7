package com.zhd.gps.manage.models;

import java.util.HashMap;
import java.util.Map;


public class GSVEntity {
	private int mSeenSatNum = 0;

	private HashMap<Integer, SatelliteEntity> mGsvMap = null;

	public int getSeenSatNum() {
		return mSeenSatNum;
	}

	public void setSeenSatNum(int mSeenSatNum) {
		this.mSeenSatNum = mSeenSatNum;
	}

	public HashMap<Integer,SatelliteEntity> getGsvMap() {
		return mGsvMap;
	}

	public void setGsvMap(HashMap<Integer, SatelliteEntity> mGsvMap) {
		this.mGsvMap = mGsvMap;
	}
}
