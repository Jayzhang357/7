package com.zhd.gnssmanager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import android.R.integer;
import android.R.string;
import android.nfc.tech.MifareClassic;
import android.os.Handler;

import com.zhd.bd970.manage.BD970Manager;
import com.zhd.bd970.manage.ZC200MUManager;
import com.zhd.ub280.*;
import com.zhd.serialportmanage.ManualResetEvent;
import com.zhd.serialportmanage.SerialPortManager;
import com.zhd.parserinterface.*;

/**
 * 定义一个全局的GPS数据接收类
 *
 * @author Administrator
 *
 */
public class Com4Receive {

	private int mGnssBaudrateCom4 = 115200;
	private static String mGnssCom4 = "/dev/ttymxc3";
	private static SerialPortManager mSpManagerCom4 = null;
	private static Com4Receive mGnssDataReceive = null;
	private BD970Manager mBd970Manager = null;
	private UB280Manager mUb280Manager = null;
	private final int COMMON_THREAD_DELAY = 200;
	private Object mLock = new Object();
	private boolean mIsSendCommand = false;
	private ManualResetEvent mAskToResolve = null;
	private static int mMotherBoardType = 0;
	private Handler mHandler = null;
	private int mCom1Baudrate = 115200;

	private IParser mParser = null;
	private static int mMotherType = 0;
	private ZC200MUManager mZC200MUManager = null;

	private ExecutorService mPool = null;
	public IParser getParser() {
		return mParser;
	}
	public Com4Receive() {
		if(mSpManagerCom4==null)
			mSpManagerCom4 = new SerialPortManager(mGnssBaudrateCom4,
					mGnssCom4, 4);

		initParser();
		mAskToResolve = new ManualResetEvent();
	}

	private void initParser() {
		if (mMotherType == GnssEnum.MotherBoardEnum.ZDT810.getValue()
				|| mMotherType == GnssEnum.MotherBoardEnum.ZDT820.getValue())// 天宝
		{
			mBd970Manager = new BD970Manager(mSpManagerCom4);
			mParser = (IParser) mBd970Manager;
		} else if (mMotherType == GnssEnum.MotherBoardEnum.ZDN810.getValue())// 诺瓦泰
		{
			mBd970Manager = new BD970Manager(mSpManagerCom4);
			mParser = (IParser) mBd970Manager;
		} else if (mMotherType == GnssEnum.MotherBoardEnum.ZDU820.getValue()
				|| mMotherType == GnssEnum.MotherBoardEnum.ZDU810.getValue())// 和芯星通
		{
			mUb280Manager = new UB280Manager(mSpManagerCom4);
			mParser = (IParser) mUb280Manager;

		} else if (mMotherType == GnssEnum.MotherBoardEnum.ZC200MU.getValue()) {
			mZC200MUManager = new ZC200MUManager(mSpManagerCom4);
			mParser = (IParser) mZC200MUManager;
		} else {
			mBd970Manager = new BD970Manager(mSpManagerCom4);
			mParser = (IParser) mBd970Manager;
		}

	}

	/**
	 * 获取实例
	 *
	 * @return
	 */
	public static Com4Receive getInstance(int motherBoardType, String Comname) {

		if (mGnssDataReceive == null || mSpManagerCom4 == null
				|| mGnssCom4 != Comname||mMotherType != motherBoardType) {
			mGnssCom4 = Comname;
			mMotherType = motherBoardType;
			mGnssDataReceive = new Com4Receive();
		}
		return mGnssDataReceive;
	}

	/**
	 * 关闭串口操作
	 *
	 * @return
	 */
	public void finalzeSerialPort() {
		if (mSpManagerCom4 != null)
			mSpManagerCom4.finalize();
		mSpManagerCom4 = null;
	}

	/**
	 * 重新实例化串口
	 *
	 * @return
	 */
	public void reWriteSerialPort(SerialPortManager mSP) {
		if (mSP != null) {
			mSpManagerCom4 = mSP;
			mSpManagerCom4.Start();
		}
		// mBd970Manager = new BD970Manager(mSpManagerCom3, mMotherBoardType);

	}

	public void reCreateSerialPort(SerialPortManager mSP) {
		if (mSP != null)
			mSpManagerCom4 = mSP;

	}

	public void stop() {
		if (mSpManagerCom4 != null) {
			mSpManagerCom4.finalize();
			mSpManagerCom4 = null;
		}
	}

	public void restartRecieve() {
		if (mSpManagerCom4 == null)
			mSpManagerCom4 = new SerialPortManager(mGnssBaudrateCom4,
					mGnssCom4, 4);

		mSpManagerCom4.Start();
	}

	//
	// public void reOpenSerialPort() {
	// mSpManagerCom3.reOpenSerialPort();
	// }

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

		mSpManagerCom4.Start();
	}

	public void setCom1Baudrate(int baudrate) {
		mCom1Baudrate = baudrate;
	}

	public void sendByte(byte[] message) {
		mSpManagerCom4.Send(message);
	}

	public void sendln(byte[] message) {
		mSpManagerCom4.Sendln(message);
	}

	public SerialPortManager getCom4GpsSerialPort() {
		return mSpManagerCom4;
	}

}
