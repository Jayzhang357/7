package com.zhd.messagedescription;

import com.zhd.datamanagetemplate.MessageDescription;
import com.zhd.parserinterface.IParser;
import com.zhd.commonhelper.*;

public class RangeMsgDescription extends MessageDescription {

	private int mMessageLength = 0;
	private byte[] mHeader;
	private Boolean mIsHeadMsg = true;
	private IParser mParser = null;

	public RangeMsgDescription(int messageDescriptionType, IParser parser) {
		super(new byte[] { (byte) 0xaa, 0x44, 0x12 }, messageDescriptionType,
				3, 3, 2000, false);
		mParser = parser;
	}

	public int ComputeLen(byte[] lenBs) {
		return lenBs[3];
	}

	public void ResolveReceiveMesssage(byte[] entireMessage) {
		mMessageLength = 0;
		if (entireMessage.length <= 8)
			return;
		if (mIsHeadMsg == true) {
			mMessageLength = Exchange.byteArrayToShort(entireMessage, 8);
			if(mMessageLength<24)return;
			mHeader = entireMessage;
			mIsHeadMsg = false;
			return;
		} else {
			
			if(mHeader.length<=8)return;
			else if(entireMessage.length<ComputeLen(mHeader))
				return;
			
			byte[] wholeMessag = new byte[mHeader.length + entireMessage.length];
			System.arraycopy(mHeader, 0, wholeMessag, 0, mHeader.length);
			System.arraycopy(entireMessage, 0, wholeMessag, mHeader.length,
					entireMessage.length);
			mIsHeadMsg = true;

			mParser.parseRangeCMP(wholeMessag);
		}

	}
	public int AskToContinueReceiveLenData() {

		return mMessageLength;
	}
}
