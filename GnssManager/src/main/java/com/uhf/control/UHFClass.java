package com.uhf.control;

public class UHFClass {

	public static native int IOCTLVIB(int cmd, int args);

	public static native int UhfRecvData(int fd, byte[] jbuff, int len);

	public static native int UhfSendData(int fd, byte[] jbuff, int len);

	public static native int OpenCOM();

	public static native int Close();
	
	public static native int UhfRssiRead();
	
	public static native int UhfRssiRead2();
	
	public static native int ioset();

	static {
		System.loadLibrary("UHF");
	}
}
