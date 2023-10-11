package com.zhd.TCPSocketClientBase;

import java.util.HashMap;
import java.util.Map;

enum ZHDProtocolType
{
	Heart(10000),
	Disconnect(10001),
	FileDescription(10002),
	FileSlice(10003),
	FileReject(10004),//对方拒绝接收文件
	FileDescriptionOKFeedback(10006),
	FileDescriptionErrFeedback(10007),
	ServerTime(10008),//服务器端的时间

	echo(10009),
	EncryptSeed(10010);//加密种子

	//枚举值的构造函数
	ZHDProtocolType(int value){
		this.value=value;}
	private int value;

	private static final Map<Integer, ZHDProtocolType> intToEnum = new HashMap<Integer, ZHDProtocolType>();
	static {
		for (ZHDProtocolType val : values()) {
			intToEnum.put(val.value, val);
		}
	}

	public static ZHDProtocolType fromInt(int i) {
		return intToEnum.get(i);
	}

	public static int toInt(ZHDProtocolType t) {
		for(Map.Entry<Integer, ZHDProtocolType> kv : intToEnum.entrySet())
			if(kv.getValue()==t)
				return kv.getKey();
		return -1;
	}

	public int toInt(){
		return value;
	}
}
