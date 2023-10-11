package com.zhd.messagedescription;

import java.io.IOException;

import com.zhd.datamanagetemplate.MessageDescription;
import com.zhd.parserinterface.IParser;
import com.zhd.commonhelper.Exchange;

public class TrackStatMessageDescription extends MessageDescription {

	private int mMessageLength = 0;
	private byte[] mHeader;
	private Boolean mIsHeadMsg = true;
	private IParser mParser = null;

	public TrackStatMessageDescription(int messageDescriptionType,
			IParser parser) {
		super(new byte[] { (byte) 170, 68, 18 }, messageDescriptionType, 3, 3,
				2000, false);
		mParser = parser;
	}

	public int ComputeLen(byte[] lenBs) {
		return lenBs[3];
	}

	public void ResolveReceiveMesssage(byte[] entireMessage) {
		mMessageLength = 0;

		if (mIsHeadMsg == true) {
			// mMessageLength = BitConverter.toUInt16(entireMessage, 8) + 4;
			try {
				mMessageLength = Exchange.BytestoUInt16(entireMessage, 8) + 4;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mHeader = entireMessage;
			mIsHeadMsg = false;
			return;
		} else {
			byte[] wholeMessag = new byte[mHeader.length + entireMessage.length];
			System.arraycopy(mHeader, 0, wholeMessag, 0, mHeader.length);
			System.arraycopy(entireMessage, 0, wholeMessag, mHeader.length,
					entireMessage.length);
			mIsHeadMsg = false;

			mParser.parseTrackState(wholeMessag);
		}

	}

	public int AskToContinueReceiveLenData() {

		return mMessageLength;
	}
}
