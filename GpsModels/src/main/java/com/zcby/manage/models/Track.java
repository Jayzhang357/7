package com.zcby.manage.models;

import java.io.IOException;

public class Track {
	private String mSn="" ;//终端ID
	private  String mTime="";

	private long mLatitude=0; // 纬度
	private long mLongitude=0; // 经度
	private short mspeed=0;// 速度
	private short mdirection=0;// 方向
	private short mhight=0;// 高程
	private byte mlock_state=0 ;// 状态
	private byte mgps_state=6 ;// 状态
	private byte mjob_state=0 ;// 状态
	private short mfield_name_lenght=0;
	private short mjob_name_lenght=0;
	private String mfield_name="";
	private String mjob_name="";
	private short mwide=0;// 深度

	public byte[] ToByte() {
		Exchange ex = new Exchange();
		ex.AddBytes(ex.Stringtobytes16(mSn),false);
		ex.AddBytes(ex.Stringtobytes(mTime),false);
		ex.AddLongAsBytes(mLongitude);
		ex.AddLongAsBytes(mLatitude);

		ex.AddShortAsBytes(mspeed);
		ex.AddShortAsBytes(mdirection);
		ex.AddShortAsBytes(mhight);

		ex.AddByte(mgps_state);
		ex.AddByte(mjob_state);
		ex.AddShortAsBytes(mwide);
		ex.AddByte(mlock_state);
		//ex.AddShortAsBytes(mfield_name_lenght);
		ex.AddStringAsBytes_1(mfield_name);
		//	ex.AddShortAsBytes(mjob_name_lenght);
		ex.AddStringAsBytes_1(mjob_name);
		return ex.BytetoFull(ex.GetAllBytes(),(byte)0x47);
	}
	public void mlock_state_set(byte value) {

		mlock_state = value;
	}
	public byte mlock_state_get() {

		return mlock_state;
	}
	public void mfield_name_set(String value) {

		mfield_name = value;
	}
	public String mfield_name_get() {

		return mfield_name;
	}
	public void mjob_name_set(String value) {

		mjob_name = value;
	}
	public String mjob_name_get() {

		return mjob_name;
	}
	public void mjob_name_lenght_set(short value) {

		mjob_name_lenght = value;
	}
	public short mjob_name_lenght_get() {

		return mjob_name_lenght;
	}
	public void mfield_name_lenght_set(short value) {

		mfield_name_lenght = value;
	}
	public short mfield_name_lenght_get() {

		return mfield_name_lenght;
	}
	public void mjob_state_set(byte value) {

		mjob_state = value;
	}
	public byte mjob_state_get() {

		return mjob_state;
	}
	public void mgps_state_set(byte value) {

		mgps_state = value;
	}
	public byte mgps_state_get() {

		return mgps_state;
	}
	public void mwide_set(short value) {

		mwide = value;
	}
	public short mwide_get() {

		return mwide;
	}
	public void mTime_set(String value) {
		mTime = value;
	}
	public String mTime_get() {
		return mTime;
	}
	public void mSn_set(String value) {
		mSn = value;
	}
	public String mSn_get() {
		return mSn;
	}
	public void mLatitude_set(long value) {
		mLatitude = value;
	}
	public long mLatitude_get() {

		return mLatitude;
	}
	public void mLongitude_set(long value) {

		mLongitude = value;
	}
	public long mLongitude_get() {

		return mLongitude;
	}
	public void mhight_set(short value) {

		mhight = value;
	}
	public short mhight_get() {

		return mhight;
	}
	public void mspeed_set(short value) {

		mspeed = value;
	}
	public short mspeed_get() {

		return mspeed;
	}
	public void mdirection_set(short value) {

		mdirection = value;
	}
	public int mdirection_get() {

		return mdirection;
	}
}
