package com.zhd.gps.manage.models;

import java.util.HashMap;
import java.util.Map;


public class SATVISEntity {
	private int mSeenSatNum = 0;

	private HashMap<Integer, SatelliteEntity> mMap = null;

	public int getSeenSatNum() {
		return mSeenSatNum;
	}

	public void setSeenSatNum(int mSeenSatNum) {
		this.mSeenSatNum = mSeenSatNum;
	}

	public HashMap<Integer,SatelliteEntity> getSATVISMap() {
		return mMap;
	}

	public void setSATVISMap(HashMap<Integer, SatelliteEntity> map) {
		this.mMap = map;
	}
}
