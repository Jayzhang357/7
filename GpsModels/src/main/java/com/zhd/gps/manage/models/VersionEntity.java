package com.zhd.gps.manage.models;

import java.util.Date;


public class VersionEntity {

	private String mVersion = "";
	private String mID = "";
	private Date mDate ;
	public Date getDate() {
		return mDate;
	}

	public void setDate(Date Date) {
		this.mDate = Date;
	}
	
	public String getID() {
		return mID;
	}

	public void setID(String ID) {
		this.mID = ID;
	}
	public String getVersion() {
		return mVersion;
	}

	public void setVersion(String Version) {
		this.mVersion = Version;
	}


}
