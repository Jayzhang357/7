package com.zhd.AgricultureClientManage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;

import android.graphics.PointF;
import android.util.Log;

import com.zhd.ProtocolFilterManage.*;
import com.zhd.TCPSocketClientBase.*;
import com.zhd.commonhelper.Exchange;

/**
 * 兼容《道路运输车辆卫星定位系统北斗兼容车载终端通讯协议技术规范》的客户端
 *
 * @author Administrator
 *
 */
public class AgricultureClient implements TCPEventListener {

	private NetClient mNetClient;
	private ProtocolFilter m_ProtocolFilter = new ProtocolFilter();
	private Dictionary<Integer, IResolveFragmentMessages> m_RFMHs;
	public List<String> successful_get = new ArrayList<String>(3);
	public List<String> project_name = new ArrayList<String>(3);
	public int sendcount_f = 1;
	public byte[] jianquan=new byte[]{};
	public boolean GetName=false;
	public String Regisit = "";
	public int Recode = 0;
	public AgricultureClient() {

	}
	private IReceiveDataListener mDataListener = null;
	public interface IReceiveDataListener {
		public void handleReceiveMessage(byte[] messge);


	}
	public void setReceiveMessageListener(IReceiveDataListener listener) {
		mDataListener = listener;
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

	public boolean NetClientisalive() {
		return mNetClient.getIsHasConnected();
	}

	@Override
	public void ConnectBreak(NetClient netClient) {
		// TODO Auto-generated method stub

	}

	public boolean mNetClientisalive() {
		if(mNetClient!=null)
			return mNetClient.getIsHasConnected();
		else
			return false;
	}
	public void Send(ProtocolMessage_Can[] Data) {
		byte[] tmp = m_ProtocolFilter.ToByte(Data);

		mNetClient.AddDataToSend(tmp);
	}
	public void Send(ProtocolMessage[] Data) {
		byte[] tmp = m_ProtocolFilter.ToByte(Data);
        String abc="";
		for(int i=0;i<tmp.length;i++)
			abc+=Integer.toHexString(tmp[i] & 0xFF)+" ;";
		Log.e("测试222",abc);
		mNetClient.AddDataToSend(tmp);
	}

	public void Send(byte[] Data) {
		mNetClient.AddDataToSend(Data);
	}

	@Override
	public void Receive(byte[] Data, NetClient netClient, Object CustomerClient) {
		// TODO Auto-generated method stub

		try {
			ProtocolMessage[] pms;
			if(mDataListener!=null)
			mDataListener.handleReceiveMessage(Data);
			pms = m_ProtocolFilter.Filter(Data);
			String abc="";
			for(int i=0;i<Data.length;i++)
				abc+=Integer.toHexString(Data[i] & 0xFF)+" ;";

			Log.v("收到数据", "收到数据" +abc);
			if (pms != null) {
				/*
				 * IResolveFragmentMessages RFMH; if
				 * ((RFMH=m_RFMHs.get(pms[0].mHead.MessageID) )!= null) {
				 * RFMH.ResolveFragmentMessagesHandler(pms); }
				 */
				Log.v("测试222", "接到心跳" + pms[0].mHead.MessageID);
				if ((pms[0].mHead.MessageID == (short) 0x8100)) {
					Log.v("收到数据", "终端注册应答");
				if( pms[0].mProtocolContent.length!=11) {
					jianquan = new byte[11];
					for (int i = 1; i < pms[0].mProtocolContent.length; i++)
						jianquan[i - 1] = pms[0].mProtocolContent[i];
					Log.v("测试222", "收到鉴权");
				}

				}
				else  if((pms[0].mHead.MessageID == (short) 0x8001))
				{
					sendcount_f=0;
					GetName=true;
					Log.v("测试222", "收到应答");
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
