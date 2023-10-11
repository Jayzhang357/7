package com.zhd.messagedescription;

import com.zhd.datamanagetemplate.MessageDescription;
import com.zhd.parserinterface.IParser;

import java.io.UnsupportedEncodingException;

public class OemMessageDescription extends MessageDescription {
	private IParser mParser;

	public OemMessageDescription(int messageDescriptionType, IParser parser) throws UnsupportedEncodingException {
		super("$ins".getBytes("UTF-8"), messageDescriptionType, (byte) 0x0A, 500);
		// TODO Auto-generated constructor stub
		 mParser = parser;
	}

	

	public void ResolveReceiveMesssage(byte[] entireMessage){
		mParser.parseoem(entireMessage);
	}

	@Override
	public int ComputeLen(byte[] lenBs) {
		// TODO Auto-generated method stub
		return 0;
	}
}
