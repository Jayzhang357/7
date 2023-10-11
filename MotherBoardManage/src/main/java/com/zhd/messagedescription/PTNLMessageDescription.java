package com.zhd.messagedescription;

import java.io.UnsupportedEncodingException;

import com.zhd.datamanagetemplate.ByteIndex;
import com.zhd.datamanagetemplate.MessageDescription;
import com.zhd.parserinterface.IParser;

public class PTNLMessageDescription extends MessageDescription {

	private IParser mParser;
	private int mCount = 0;
	private static ByteIndex[] mGGAbi;
	static{
		try {
			byte[] bs="$GPGGA".getBytes("UTF-8");
			mGGAbi=new ByteIndex[]{
					new ByteIndex(bs[0],0),
					new ByteIndex(bs[3],3),
					new ByteIndex(bs[4],4),
					new ByteIndex(bs[5],5)};
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public PTNLMessageDescription(int messageDescriptionType,IParser parser) throws UnsupportedEncodingException {
		super("$PTNL".getBytes("UTF-8"), messageDescriptionType, (byte) 0x0A, 500);
		// TODO Auto-generated constructor stub
		mParser = parser;
	}

	

	public void ResolveReceiveMesssage(byte[] entireMessage){
		mParser.parsePTNL(entireMessage);
	}


	@Override
	public int ComputeLen(byte[] lenBs) {
		// TODO Auto-generated method stub
		return 0;
	}
}
