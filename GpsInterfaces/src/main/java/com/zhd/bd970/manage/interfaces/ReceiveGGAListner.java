package com.zhd.bd970.manage.interfaces;

import com.zhd.gps.manage.models.GGAEntity;


public interface ReceiveGGAListner {

	public void TellReceiveGGA(GGAEntity entity);
	public void TellReceiveGGA(byte[] byte1);
}
