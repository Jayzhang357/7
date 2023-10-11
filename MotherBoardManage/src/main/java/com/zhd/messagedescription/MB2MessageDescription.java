package com.zhd.messagedescription;

import java.io.UnsupportedEncodingException;

import com.zhd.datamanagetemplate.MessageDescription;
import com.zhd.parserinterface.IParser;

public class MB2MessageDescription extends MessageDescription {

	private IParser mParser;
	private int mCount = 0;
	
	public MB2MessageDescription(int messageDescriptionType,IParser parser) throws UnsupportedEncodingException {
	//	super(new byte[] {(byte) 0xD3  }, messageDescriptionType, (byte) 0x0A, 500);
		// TODO Auto-generated constructor stub
		
		super(new byte[] {(byte) 0xD3  }, messageDescriptionType, 0, 3,
				3, true);
		// TODO Auto-generated constructor stub
		mParser = parser;
	}

	

	public void ResolveReceiveMesssage(byte[] entireMessage){
		mParser.parseMB2(entireMessage);
	}


	@Override
	public int ComputeLen(byte[] lenBs) {
	
		// TODO Auto-generated method stub
		 int a=lenBs[1]&0xff;
		  int b=lenBs[2]&0xff;
		  int c=a*256+b;
		  if(c>1000)
			  return 0;
		return c;
	}
}
