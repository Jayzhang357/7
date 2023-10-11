package com.zhd.messagedescription;

import com.zhd.datamanagetemplate.MessageDescription;
import com.zhd.parserinterface.IParser;

public class Trimble35hMessageDescription extends MessageDescription {
	private IParser mParser;

	public Trimble35hMessageDescription(int messageDescriptionType,
			IParser parser) {
		// TODO Auto-generated constructor stub
		super(new byte[] { 0x02, 0x28, 0x40 }, messageDescriptionType, 3, 3,
				500, true);
		// super(new byte[] { 2 }, messageDescriptionType, (byte) 0x03, 500);
		mParser = parser;
	}

	public int ComputeLen(byte[] lenBs) {
		int i = lenBs[3] & 0xff;
		return i + 6;
	}

	public void ResolveReceiveMesssage(byte[] entireMessage) {
		// BD970Parser.ParseTrimble57hRecordData(entireMessage);
		mParser.parseTrimble4hData(entireMessage);
	}
}
