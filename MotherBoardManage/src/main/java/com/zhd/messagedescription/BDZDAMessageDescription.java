package com.zhd.messagedescription;

import java.io.UnsupportedEncodingException;

import com.zhd.datamanagetemplate.MessageDescription;
import com.zhd.parserinterface.IParser;

public class BDZDAMessageDescription extends MessageDescription {
	private IParser mParser;
	
	public BDZDAMessageDescription(int messageDescriptionType, IParser parser) throws UnsupportedEncodingException {
		super("$BDZDA".getBytes("UTF-8"), messageDescriptionType, (byte) 0x0A, 500);
		// TODO Auto-generated constructor stub
		 mParser = parser;
	}


	public void ResolveReceiveMesssage(byte[] entireMessage){
		mParser.parseZDA(entireMessage);
	}

	@Override
	public int ComputeLen(byte[] lenBs) {
		// TODO Auto-generated method stub
		return 0;
	}
}
