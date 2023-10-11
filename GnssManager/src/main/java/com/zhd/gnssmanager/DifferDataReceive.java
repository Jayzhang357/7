package com.zhd.gnssmanager;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.util.Log;

import com.zhd.bd970.manage.BD970Manager;
import com.zhd.bd970.manage.ZC200MUManager;
import com.zhd.bd970.manage.interfaces.ReceiveGSAListner;
import com.zhd.bd970.manage.interfaces.ReceiveGSVListner;
import com.zhd.gps.manage.models.GGAEntity;
import com.zhd.parserinterface.IParser;
import com.zhd.serialportmanage.ManualResetEvent;
import com.zhd.serialportmanage.SerialPortManager;
import com.zhd.ub280.UB280Manager;

public class DifferDataReceive {
	private BD970Manager mBd970Manager = null;
	private UB280Manager mUb280Manager = null;
	private static SerialPortManager mSpManagerCom1 = null;

	private static int mMotherType = 0;
	private ZC200MUManager mZC200MUManager = null;
	private static String mDifferCom2 = "/dev/ttymxc1";
	private static SerialPortManager mSpManagerCom2 = null;
	private static int mDifferBaudrateCom2 = 115200;
	private static DifferDataReceive mDifferDataReceive = null;
	private int mDifferType = 0;// 0 代表电台差分，1代表网络差分,2代表兼容模式；
	private long mTime = 0;// 电台上次发送的时间，默认为0
	private long mTempTime = 0;// 差分数据发送的时间，默认为0
	private static ReceiveDiffListener mReceiveDiffListener = null;
	private ManualResetEvent mAskToResolve = null;
	private IParser mParser = null;
	SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMddhhmmss");

	public int getmDifferType() {
		return mDifferType;
	}

	public void setmDifferType(int mDifferType) {
		this.mDifferType = mDifferType;
	}

	public DifferDataReceive(int motherboardType, int DifferBaudrateCom2) {

		if(mSpManagerCom2==null)
		{
			mDifferBaudrateCom2 = DifferBaudrateCom2;
			mSpManagerCom2 = new SerialPortManager(mDifferBaudrateCom2,
					mDifferCom2, 2);


		}

		mMotherType = motherboardType;
		initParser();
		mAskToResolve = new ManualResetEvent();
	}

	/**
	 * 获取实例
	 *
	 * @return
	 */
	public static DifferDataReceive getInstance(int motherboardType,
												int DifferBaudrateCom2, String Comname) {

		if (mDifferDataReceive == null || mSpManagerCom2 == null
				|| mDifferBaudrateCom2 != DifferBaudrateCom2
				|| mDifferCom2 != Comname) {
			mDifferCom2 = Comname;
			mDifferDataReceive = new DifferDataReceive(motherboardType,
					DifferBaudrateCom2);
		}
		return mDifferDataReceive;
	}

	public synchronized void sendByte(byte[] message, int DifferType) {
		mTempTime = new Date().getTime();
		if (DifferType == 0) {
			mTime = mTempTime;
			mSpManagerCom2.Send(message);
			Log.e("send", "0");

		} else if (DifferType == 1) {
			if (mTempTime - mTime > 2000) {
				mSpManagerCom2.Send(message);

				Log.e("send", "1");
			}
		}
		if (mReceiveDiffListener != null) {
			mReceiveDiffListener.TellReceiveDiffType(DifferType);
		}
	}

	public SerialPortManager getCom3GpsSerialPort() {
		return mSpManagerCom2;
	}

	public IParser getParser() {
		return mParser;
	}

	public void restartRecieve() {
		if (mSpManagerCom2 == null)
			mSpManagerCom2 = new SerialPortManager(mDifferBaudrateCom2,
					mDifferCom2, 2);

		mSpManagerCom2.Start();
		initParser();
	}

	public void startRecieve() {

		// //创建一个锁 等待发送完命令后才进行下一步工作
		// try {
		// Thread.sleep(500);
		// } catch (InterruptedException e1) {
		// e1.printStackTrace();
		// }
		//
		// synchronized (mLock) {
		// while(!mIsSendCommand){
		// try {
		// mLock.wait();
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }
		// }
		// }
		//
		/*
		 * while (!mIsSendCommand) { try { Thread.sleep(500); } catch
		 * (InterruptedException e) {
		 *
		 * e.printStackTrace(); } }
		 */

		// mAskToResolve.wait(500, TimeUnit.MILLISECONDS);
		if (mSpManagerCom2 == null)
			mSpManagerCom2 = new SerialPortManager(mDifferBaudrateCom2,
					mDifferCom2, 2);
		mSpManagerCom2.Start();
	}

	public void finalize() {
		if (mSpManagerCom2 != null) {
			mSpManagerCom2.finalize();
			mSpManagerCom2 = null;
		}
	}

	public void sendByte(byte[] message) {
		if (mSpManagerCom2 == null)
			mSpManagerCom2 = new SerialPortManager(mDifferBaudrateCom2,
					mDifferCom2, 2);
		mSpManagerCom2.Send(message);
		String abc = new String(message);
		// Log.e("监听","发送到COM2"+abc);
	}

	public void sendln(byte[] message) {
		if (mSpManagerCom2 == null)
			mSpManagerCom2 = new SerialPortManager(mDifferBaudrateCom2,
					mDifferCom2, 2);
		mSpManagerCom2.Sendln(message);
	}

	public void setReceiveDiffListener(ReceiveDiffListener listener) {
		mReceiveDiffListener = listener;
	}

	public interface ReceiveDiffListener {
		public void TellReceiveDiffType(int Type);
	}

	public SerialPortManager getCom2GpsSerialPort() {
		if (mSpManagerCom2 == null)
			mSpManagerCom2 = new SerialPortManager(mDifferBaudrateCom2,
					mDifferCom2, 2);
		return mSpManagerCom2;
	}

	private void initParser() {
		if (mMotherType == GnssEnum.MotherBoardEnum.ZDT810.getValue()
				|| mMotherType == GnssEnum.MotherBoardEnum.ZDT820.getValue())// 天宝
		{
			mBd970Manager = new BD970Manager(mSpManagerCom2);
			mParser = (IParser) mBd970Manager;
		} else if (mMotherType == GnssEnum.MotherBoardEnum.ZDN810.getValue())// 诺瓦泰
		{
			mBd970Manager = new BD970Manager(mSpManagerCom2);
			mParser = (IParser) mBd970Manager;
		} else if (mMotherType == GnssEnum.MotherBoardEnum.ZDU820.getValue()
				|| mMotherType == GnssEnum.MotherBoardEnum.ZDU810.getValue())// 和芯星通
		{
			mUb280Manager = new UB280Manager(mSpManagerCom2);
			mParser = (IParser) mUb280Manager;

		} else if (mMotherType == GnssEnum.MotherBoardEnum.ZC200MU.getValue()) {
			mZC200MUManager = new ZC200MUManager(mSpManagerCom2);
			mParser = (IParser) mZC200MUManager;
		} else {
			mBd970Manager = new BD970Manager(mSpManagerCom2);
			mParser = (IParser) mBd970Manager;
		}
	}

	public void initParser(int MotherType) {
		if (MotherType == GnssEnum.MotherBoardEnum.ZDT810.getValue()
				&& MotherType == GnssEnum.MotherBoardEnum.ZDT820.getValue())// 天宝
		{
			mBd970Manager = new BD970Manager(mSpManagerCom2);
			mParser = (IParser) mBd970Manager;
		} else if (MotherType == GnssEnum.MotherBoardEnum.ZDN810.getValue())// 诺瓦泰
		{

		} else if (MotherType == GnssEnum.MotherBoardEnum.ZDU820.getValue()
				|| MotherType == GnssEnum.MotherBoardEnum.ZDU810.getValue())// 和芯星通
		{
			mUb280Manager = new UB280Manager(mSpManagerCom2);
			mParser = (IParser) mUb280Manager;
		} else if (MotherType == GnssEnum.MotherBoardEnum.ZC200MU.getValue()) {
			mZC200MUManager = new ZC200MUManager(mSpManagerCom2);
			mParser = (IParser) mZC200MUManager;
		} else {
			mBd970Manager = new BD970Manager(mSpManagerCom2);
			mParser = (IParser) mBd970Manager;
		}
	}
}
