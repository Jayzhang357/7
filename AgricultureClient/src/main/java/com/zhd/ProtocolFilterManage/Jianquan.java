package com.zhd.ProtocolFilterManage;

public class Jianquan {

   public byte[] jianquan_num=new byte[]{};

	public byte[] ToByte() {
		Exchange ex = new Exchange();
     	ex.AddBytes(jianquan_num, false);

		return ex.GetAllBytes();
	}



}
