package com.zhd.messagedescription;

import java.io.UnsupportedEncodingException;

import com.zhd.datamanagetemplate.MessageDescription;
import com.zhd.parserinterface.IParser;

public class ReadsenMessageDescription extends MessageDescription {

	private IParser mParser;
	private int mCount = 0;
	
	public ReadsenMessageDescription(int messageDescriptionType,IParser parser) throws UnsupportedEncodingException {
	
		super("$readsen".getBytes("UTF-8"), messageDescriptionType, (byte) 0x0A, 500);// TODO Auto-generated constructor stub
		mParser = parser;
	}

	

	public void ResolveReceiveMesssage(byte[] entireMessage){
		mParser.parseReadSen(entireMessage);
	}


	@Override
	public int ComputeLen(byte[] lenBs) {
		// TODO Auto-generated method stub
		return 0;
	}
}
