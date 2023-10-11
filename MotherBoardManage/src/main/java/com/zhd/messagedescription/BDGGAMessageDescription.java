package com.zhd.messagedescription;

import java.io.UnsupportedEncodingException;

import com.zhd.datamanagetemplate.ByteIndex;
import com.zhd.datamanagetemplate.MessageDescription;
import com.zhd.parserinterface.IParser;

public class BDGGAMessageDescription extends MessageDescription {

	private IParser mParser;
	private static ByteIndex[] mGGAbi;
	private int mCount = 0;
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

	public BDGGAMessageDescription(int messageDescriptionType,IParser parser) throws UnsupportedEncodingException {
		super(mGGAbi, messageDescriptionType, (byte) 0x0A, 500);
		// TODO Auto-generated constructor stub
		mParser = parser;
	}


	/**
	 * 子类重载该函数，当收到一条完整的数据后，调用该函数处理数据
	 * @param entireMessage
	 */
	public void ResolveReceiveMesssage(byte[] entireMessage){
		mParser.parseGGA(entireMessage);
	}


	@Override
	public int ComputeLen(byte[] lenBs) {
		// TODO Auto-generated method stub
		return 0;
	}
}
