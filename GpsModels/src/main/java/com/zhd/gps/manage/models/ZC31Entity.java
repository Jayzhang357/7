package com.zhd.gps.manage.models;

import java.util.Date;




public class ZC31Entity {

	String mbaseversion="";//基站固件版本
	String mbaseFirmwareversion="";//基站主板版本
	String mbaseRegist="";//设备号
	String mbaseRegistDate="";//注册时间
	public String getbaseRegistDate() {
		return mbaseRegistDate;
	}

	public void setbaseRegistDate(String baseRegistDate) {
		this.mbaseRegistDate = baseRegistDate;
	}
	public String getbaseRegist() {
		return mbaseRegist;
	}

	public void setbaseRegist(String baseRegist) {
		this.mbaseRegist = baseRegist;
	}
	public String getbaseversion() {
		return mbaseversion;
	}

	public void setbaseversion(String baseversion) {
		this.mbaseversion = baseversion;
	}
	public String getbaseFirmwareversion() {
		return mbaseFirmwareversion;
	}

	public void setbaseFirmwareversion(String baseFirmwareversion) {
		this.mbaseFirmwareversion = baseFirmwareversion;
	}
}
