package com.zhd.AgricultureClientManage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.List;

import android.graphics.PointF;
import android.util.Log;

import com.zhd.ProtocolFilterManage.*;
import com.zhd.TCPSocketClientBase.*;
import com.zhd.commonhelper.Exchange;

/**
 *兼容《道路运输车辆卫星定位系统北斗兼容车载终端通讯协议技术规范》的客户端
 * 
 * @author Administrator
 * 
 */
public class ThreeInOneClient implements TCPEventListener {

	private NetClient mNetClient;
	private ThreeInOneFilter m_ProtocolFilter = new ThreeInOneFilter();
	private Dictionary<Integer, IResolveFragmentMessages> m_RFMHs;
	public List<String> successful_get = new ArrayList<String>(3);
	public List<String> project_name = new ArrayList<String>(3);
	public int sendcount_f = 1;
	public String Regisit = "";
	public byte[] Token;
	public String ip;
	public int port;

	public ThreeInOneClient() {

	}

	/**
	 * 
	 * @param ipStr
	 *            服务器IP地址
	 * @param tcpPort
	 *            服务器监听端口
	 */
	public void Start(String ipStr, int tcpPort) {
		mNetClient = new NetClient(ipStr, tcpPort, false);
		mNetClient.AddBasicNetworkEventListener(this);
		mNetClient.Start();
	}

	public void Stop() {
		if (mNetClient != null) {
			mNetClient.Stop();
		}
	}

	@Override
	public void ConnectSuccessful(NetClient client) {
		// TODO Auto-generated method stub

	}

	public boolean mNetClient() {
		if (mNetClient == null)
			return false;
		else

			return true;
	}

	public boolean NetClientisalive() {
		return mNetClient.getIsHasConnected();
	}

	@Override
	public void ConnectBreak(NetClient netClient) {
		// TODO Auto-generated method stub

	}

	public boolean mNetClientisalive() {
		return mNetClient.getIsHasConnected();
	}

	public void Send(byte[] Data) {
		String ret = "";
		for (int i = 0; i < Data.length; i++) {
			String hex = Integer.toHexString(Data[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			ret += hex.toUpperCase() ;
		}
		Log.e("发送", ret);
		mNetClient.AddDataToSend(Data);
	}

	@Override
	public void Receive(byte[] Data, NetClient netClient, Object CustomerClient) {
		// TODO Auto-generated method stub
		String ret = "";
		for (int i = 0; i < Data.length; i++) {
			String hex = Integer.toHexString(Data[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			ret += hex.toUpperCase();
		}
		Log.e("接送", ret);
		try {
			ProtocolMessage31 pms;
			pms = m_ProtocolFilter.Filter(Data);
			Log.v("测试", "接到心跳长度" + Data.length);
			if (pms != null) {
				/*
				 * IResolveFragmentMessages RFMH; if
				 * ((RFMH=m_RFMHs.get(pms[0].mHead.MessageID) )!= null) {
				 * RFMH.ResolveFragmentMessagesHandler(pms); }
				 */
				Log.e("测试", "報文id" + pms.id);
				if (pms.id == 0x09) {
					if (pms.mProtocolContent.length == 33) {
						Log.e("测试", "收到了token" + Data.length);
						Token = new byte[32];
						System.arraycopy(pms.mProtocolContent, 1, Token, 0,
								pms.mProtocolContent.length - 1);
						Log.e("测试", "收到了token" + Token.length);
					}
				}

				if (pms.id == 0x24) {
					if (pms.mProtocolContent.length == 21) {

						String data_S = "";
						data_S = new String(pms.mProtocolContent);
						String data_p[] = data_S.split(":");
						if (data_p.length == 2) {
							ip = data_p[0];
							port= Integer.parseInt(data_p[1]);
							Log.e("测试", "收到" + ip+";"+port);
						}
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
