package com.zhd.gps.manage.models;

import java.util.HashMap;
import java.util.Map;


public class Trimble40hEntity {
	private int mSeenSatNum = 0;

	private HashMap<Integer, SatelliteEntity> mMap = null;

	public int getSeenSatNum() {
		return mSeenSatNum;
	}

	public void setSeenSatNum(int mSeenSatNum) {
		this.mSeenSatNum = mSeenSatNum;
	}

	public HashMap<Integer,SatelliteEntity> getGsofMap() {
		return mMap;
	}

	public void setGsofMap(HashMap<Integer, SatelliteEntity> map) {
		this.mMap = map;
	}
}
