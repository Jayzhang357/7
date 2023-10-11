package com.zhd.shj.entity;

import android.net.wifi.aware.PublishConfig;

import java.io.Serializable;
import java.util.Date;

public class JobInfo implements Serializable {

	@Override
	public boolean equals(Object o) {
		try {
			JobInfo job = (JobInfo) o;

				return true;
		} catch (Exception e) {
			System.out.println("o can't transform into jobinfo");
		}
		return false;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -5886365390872096630L;

	public int ID = -1;
	public String JobName = "";

	public int AbType = 0;
	public double APointB = 0.0d;
	public double APointL = 0.0d;
	public double APointH = 0.0d;
	public double BPointB = 0.0d;
	public double BPointL = 0.0d;
	public double BPointH = 0.0d;
	public double CPointB = 0.0d;
	public double CPointL = 0.0d;
	public double CPointH = 0.0d;

	public double setH=0.0d;
	public String RecordTableName = "";
	public float CoverageArea = 0;
	public int IsSelected = CommonEnum.DbBoolean.False.getValue();
	public long CreateTime = new Date().getTime();
	public double Sensitivity = 0.0d;
}
