package com.zhd.BussinessMessage;

import java.io.UnsupportedEncodingException;

import com.zhd.ProtocolFilterManage.BCDDecode;
import com.zhd.ProtocolFilterManage.ProtocolHead;
import com.zhd.ProtocolFilterManage.ProtocolMessage;

public class RegistMsg  extends GeneralBMessage{
	byte [] mphonenum = new byte[6];
	public RegistMsg(String phonenum ){
		try {
			mphonenum = BCDDecode.str2Bcd(phonenum);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    @Override
    public  void Resolve(ProtocolMessage[] fragmentMessages)
    {
        super.Resolve(fragmentMessages);

    }
    @Override
    public  ProtocolMessage[] ToProtocolMessage()
    {
        ProtocolHead ph = new ProtocolHead();
        ph.MessageID = 0x0700;
        try {
			ph.PhoneNum = mphonenum;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        ProtocolMessage[] pm = new ProtocolMessage[1];
        pm[0] =new ProtocolMessage(ph,null );
        
        return pm;
    }
}
