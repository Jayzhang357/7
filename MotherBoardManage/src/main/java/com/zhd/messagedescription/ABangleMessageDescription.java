package com.zhd.messagedescription;

import java.io.UnsupportedEncodingException;

import com.zhd.datamanagetemplate.MessageDescription;
import com.zhd.parserinterface.IParser;

public class ABangleMessageDescription extends MessageDescription {

	private IParser mParser;
	private int mCount = 0;

	public ABangleMessageDescription(int messageDescriptionType,IParser parser) throws UnsupportedEncodingException {

		super("$ab_angle".getBytes("UTF-8"), messageDescriptionType, (byte) 0x0A, 500);// TODO Auto-generated constructor stub
		mParser = parser;
	}


	/**
	 * 子类重载该函数，当收到一条完整的数据后，调用该函数处理数据
	 * @param entireMessage
	 */
	public void ResolveReceiveMesssage(byte[] entireMessage){
		mParser.parseABangle(entireMessage);
	}


	@Override
	public int ComputeLen(byte[] lenBs) {
		// TODO Auto-generated method stub
		return 0;
	}
}
