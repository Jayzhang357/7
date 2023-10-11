package com.zhd.BussinessManageInterface;

import com.zhd.BussinessMessage.*;
import com.zhd.TCPSocketClientBase.NetClient;


public interface BussinessManageListener {


	void TellReceiveAgLandInfo(AgLandInfos alis);

	void TellConnectBreak();
}
