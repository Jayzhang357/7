package com.zhd.gnssmanager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import android.R.integer;
import android.R.string;
import android.nfc.tech.MifareClassic;
import android.os.Handler;
import android.util.Log;

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
public class GnssDataReceive {

	private BD970Manager mBd970Manager = null;
	private ZC200MUManager mZC200MUManager = null;
	private UB280Manager mUb280Manager = null;
	private static int mGnssBaudrateCom3 = 115200;
	private static String mGnssCom3 = "/dev/ttymxc2";
	private static SerialPortManager mSpManagerCom3 = null;
	private static GnssDataReceive mGnssDataReceive = null;
	private int mGnssBaudrateCom1 = 19200;
	private final int COMMON_THREAD_DELAY = 200;
	private Object mLock = new Object();
	private boolean mIsSendCommand = false;
	private ManualResetEvent mAskToResolve = null;
	private static int mMotherBoardType = 0;
	private Handler mHandler = null;
	private int mCom1Baudrate = 115200;

	private IParser mParser = null;
	private static int mMotherType = 0;

	private ExecutorService mPool = null;

	public GnssDataReceive(int motherboardType) {
		if(mSpManagerCom3==null)
			mSpManagerCom3 = new SerialPortManager(mGnssBaudrateCom3,
					mGnssCom3, 3);


		mMotherType = motherboardType;

		initParser();

		mAskToResolve = new ManualResetEvent();

	}

	public void setMotherType(int type) {
		mMotherType = type;
	}

	private void initParser() {
		if (mMotherType == GnssEnum.MotherBoardEnum.ZDT810.getValue()
				|| mMotherType == GnssEnum.MotherBoardEnum.ZDT820.getValue())// 天宝
		{
			mBd970Manager = new BD970Manager(mSpManagerCom3);
			mParser = (IParser) mBd970Manager;
		} else if (mMotherType == GnssEnum.MotherBoardEnum.ZDN810.getValue())// 诺瓦泰
		{
			mBd970Manager = new BD970Manager(mSpManagerCom3);
			mParser = (IParser) mBd970Manager;
		} else if (mMotherType == GnssEnum.MotherBoardEnum.ZDU820.getValue()
				|| mMotherType == GnssEnum.MotherBoardEnum.ZDU810.getValue())// 和芯星通
		{
			mUb280Manager = new UB280Manager(mSpManagerCom3);
			mParser = (IParser) mUb280Manager;

		} else if (mMotherType == GnssEnum.MotherBoardEnum.ZC200MU.getValue()) {
			mZC200MUManager = new ZC200MUManager(mSpManagerCom3);
			mParser = (IParser) mZC200MUManager;
		} else {
			mBd970Manager = new BD970Manager(mSpManagerCom3);
			mParser = (IParser) mBd970Manager;
		}
	}

	public void initParser(int MotherType) {
		if (MotherType == GnssEnum.MotherBoardEnum.ZDT810.getValue()
				&& MotherType == GnssEnum.MotherBoardEnum.ZDT820.getValue())// 天宝
		{
			mBd970Manager = new BD970Manager(mSpManagerCom3);
			mParser = (IParser) mBd970Manager;
		} else if (MotherType == GnssEnum.MotherBoardEnum.ZDN810.getValue())// 诺瓦泰
		{

		} else if (MotherType == GnssEnum.MotherBoardEnum.ZDU820.getValue()
				|| MotherType == GnssEnum.MotherBoardEnum.ZDU810.getValue())// 和芯星通
		{
			mUb280Manager = new UB280Manager(mSpManagerCom3);
			mParser = (IParser) mUb280Manager;
		} else if (MotherType == GnssEnum.MotherBoardEnum.ZC200MU.getValue()) {
			mZC200MUManager = new ZC200MUManager(mSpManagerCom3);
			mParser = (IParser) mZC200MUManager;
		} else {
			mBd970Manager = new BD970Manager(mSpManagerCom3);
			mParser = (IParser) mBd970Manager;
		}
	}

	/**
	 * 获取实例
	 *
	 * @return
	 */
	public static GnssDataReceive getInstance(int motherBoardType,
											  String Comname,int GnssBaudrateCom3) {

		if (mGnssDataReceive == null || mSpManagerCom3 == null
				|| mMotherType != motherBoardType || mGnssCom3 != Comname||mGnssBaudrateCom3!=GnssBaudrateCom3) {
			mGnssCom3 = Comname;
			mMotherType = motherBoardType ;
			mGnssBaudrateCom3=GnssBaudrateCom3;
			mGnssDataReceive = new GnssDataReceive(motherBoardType);
		}

		return mGnssDataReceive;
	}

	/**
	 * 获取实例的Bd970
	 *
	 * @return
	 */
	// public BD970Manager getBd970Manager() {
	// return mBd970Manager;
	// }

	public IParser getParser() {
		return mParser;
	}

	/**
	 * 关闭串口操作
	 *
	 * @return
	 */
	public void finalzeSerialPort() {
		if (mSpManagerCom3 != null)
			mSpManagerCom3.finalize();
		mSpManagerCom3 = null;
	}

	/**
	 * 重新实例化串口
	 *
	 * @return
	 */
	public void reWriteSerialPort(SerialPortManager mSP) {
		if (mSP != null) {
			mSpManagerCom3 = mSP;
			mSpManagerCom3.Start();
		}
		// mBd970Manager = new BD970Manager(mSpManagerCom3, mMotherBoardType);
		initParser();
	}

	public void reCreateSerialPort(SerialPortManager mSP) {
		if (mSP != null)
			mSpManagerCom3 = mSP;

		initParser();
	}

	public void stop() {
		if (mSpManagerCom3 != null) {
			mSpManagerCom3.finalize();
			mSpManagerCom3 = null;
		}
	}

	public void restartRecieve() {
		if (mSpManagerCom3 == null)
			mSpManagerCom3 = new SerialPortManager(mGnssBaudrateCom3,
					mGnssCom3, 3);

		mSpManagerCom3.Start();
		// initParser();
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
		/*while (!mIsSendCommand) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {

				e.printStackTrace();
			}
		}*/

		// mAskToResolve.wait(500, TimeUnit.MILLISECONDS);

		mSpManagerCom3.Start();
	}

	public void setCom1Baudrate(int baudrate) {
		mCom1Baudrate = baudrate;
	}

	public void sendByte(byte[] message) {
		if (mSpManagerCom3 != null)
			mSpManagerCom3.Send(message);
		else {
			mSpManagerCom3 = new SerialPortManager(mGnssBaudrateCom3,
					mGnssCom3, 3);

			mSpManagerCom3.Start();
		}

	}

	public void sendln(byte[] message) {
		if (mSpManagerCom3 != null) {
			mSpManagerCom3.Sendln(message);

		} else {
			mSpManagerCom3 = new SerialPortManager(mGnssBaudrateCom3,
					mGnssCom3, 3);

			mSpManagerCom3.Start();
		}

	}

	public SerialPortManager getCom3GpsSerialPort() {
		return mSpManagerCom3;
	}

	public void sendGnssGGACommand() {
		if (mSpManagerCom3 != null) {
			// gga 02 00 64 0D 00 00 00 03 00 01 00 07 04 06 02 03 00 8B 03
			byte[] cmd = { 0x02, 0x00, 0x64, 0x0D, 0x00, 0x00, 0x00, 0x03,
					0x00, 0x01, 0x00, 0x07, 0x04, 0x06, 0x02, 0x03, 0x00,
					(byte) 0x8B, 0x03 };
			mSpManagerCom3.Send(cmd);
		}

	}

	public void sendGnssRMCCommand() {
		if (mSpManagerCom3 != null) {
			// rmc 02 00 64 0D 00 00 00 03 00 01 00 07 04 28 02 03 00 AD 03
			byte[] cmd = { 0x02, 0x00, 0x64, 0x0D, 0x00, 0x00, 0x00, 0x03,
					0x00, 0x01, 0x00, 0x07, 0x04, 0x28, 0x02, 0x03, 0x00,
					(byte) 0xAD, 0x03 };
			mSpManagerCom3.Send(cmd);
		}

	}

	public void sendGnssGSVCommand() {
		if (mSpManagerCom3 != null) {
			// gsv 02 00 64 0D 00 00 00 03 00 01 00 07 04 12 02 05 00 99 03
			if (mMotherType == GnssEnum.MotherBoardEnum.ZDTM820.getValue()) {
				byte[] boardInfoCmdTrimble830 = "$PASHS,NME,GSV,D,ON,5"
						.getBytes();
				mSpManagerCom3.Sendln(boardInfoCmdTrimble830);

			} else {
				byte[] cmd = { 0x02, 0x00, 0x64, 0x0D, 0x00, 0x00, 0x00, 0x03,
						0x00, 0x01, 0x00, 0x07, 0x04, 0x12, 0x02, 0x05, 0x00,
						(byte) 0x99, 0x03 };
				mSpManagerCom3.Send(cmd);
			}
		}

	}

	public void sendbestposa() {
		byte[] boardInfoCmdTrimble830 = "log com3 bestposa ontime 1".getBytes();
		mSpManagerCom3.Sendln(boardInfoCmdTrimble830);
	}

	public void sendGnssGSACommand() {
		if (mSpManagerCom3 != null) {
			// gsa 02 00 64 0D 00 00 00 03 00 01 00 07 04 26 02 03 00 AB 03
			byte[] cmd = { 0x02, 0x00, 0x64, 0x0D, 0x00, 0x00, 0x00, 0x03,
					0x00, 0x01, 0x00, 0x07, 0x04, 0x26, 0x02, 0x03, 0x00,
					(byte) 0xAB, 0x03 };

			mSpManagerCom3.Send(cmd);
		}
	}

	public void sendGnssVTGCommand() {
		if (mSpManagerCom3 != null) {
			// vtg 02 00 64 0D 00 00 00 03 00 01 00 07 04 28 02 03 00 AD 03
			byte[] cmd = { 0x02, 0x00, 0x64, 0x0D, 0x00, 0x00, 0x00, 0x03,
					0x00, 0x01, 0x00, 0x07, 0x04, 0x0c, 0x02, 0x03, 0x00,
					(byte) 0x91, 0x03 };

			mSpManagerCom3.Send(cmd);
		}
	}

	public void sendGSOFCommand() {
		if (mSpManagerCom3 != null) {
			// gsof 02 00 64 0D 00 00 00 03 00 01 00 07 04 06 02 03 00 8B 03
			if (mMotherType == GnssEnum.MotherBoardEnum.ZDTM820.getValue()) {
				byte[] boardInfoCmdTrimble830 = "$PASHS,SNS,DUO,2".getBytes();
				mSpManagerCom3.Sendln(boardInfoCmdTrimble830);
				boardInfoCmdTrimble830 = "$PASHS,ATM,RNX,D,ON,5.0".getBytes();
				mSpManagerCom3.Sendln(boardInfoCmdTrimble830);

				/*
				 * byte[] boardInfoCmdTrimble830 = "$PASHS,GSF,3,D,ON,5"
				 * .getBytes(); mSpManagerCom3.Sendln(boardInfoCmdTrimble830);
				 */

			} else {
				byte[] cmd = { 0x02, 0x00, 0x64, 0x11, 0x00, 0x00, 0x00, 0x03,
						0x00, 0x01, 0x00, 0x07, 0x08, 0x0a, 0x02, 0x06, 0x00,
						0x22, 0x00, 0x00, 0x00, (byte) 0xbc, 0x03 };

				mSpManagerCom3.Send(cmd);
			}
		}
	}

	public void sendZDACommand() {
		if (mSpManagerCom3 != null) {
			// zda 02 00 64 0D 00 00 00 03 00 01 00 07 04 08 02 03 00 8D 03
			byte[] cmd = { 0x02, 0x00, 0x64, 0x0D, 0x00, 0x00, 0x00, 0x03,
					0x00, 0x01, 0x00, 0x07, 0x04, 0x08, 0x02, 0x03, 0x00,
					(byte) 0x8D, 0x03 };

			mSpManagerCom3.Send(cmd);
		}
	}

	public void changeCom1Baudrate() {
		if (mSpManagerCom3 != null) {
			// 02 00 64 0D 00 00 00 03 00 01 00 02 04 01 04 00 00 80 03
			if (mCom1Baudrate == 19200) {
				byte[] cmd19200 = { 0x02, 0x00, 0x64, 0x0D, 0x00, 0x00, 0x00,
						0x03, 0x00, 0x01, 0x00, 0x02, 0x04, 0x01, 0x04, 0x00,
						0x00, (byte) 0x80, 0x03 };

				mSpManagerCom3.Send(cmd19200);

			} else if (mCom1Baudrate == 38400) {
				byte[] cmd38400 = { 0x02, 0x00, 0x64, 0x0D, 0x00, 0x00, 0x00,
						0x03, 0x00, 0x01, 0x00, 0x02, 0x04, 0x00, 0x04, 0x00,
						0x00, 0x7F, 0x03 };

				mSpManagerCom3.Send(cmd38400);

			} else if (mCom1Baudrate == 57600) {
				byte[] cmd57600 = { 0x02, 0x00, 0x64, 0x0D, 0x00, 0x00, 0x00,
						0x03, 0x00, 0x01, 0x00, 0x02, 0x04, 0x00, 0x06, 0x00,
						0x00, (byte) 0x81, 0x03 };

				mSpManagerCom3.Send(cmd57600);

			} else if (mCom1Baudrate == 115200) {
				byte[] cmd115200 = { 0x02, 0x00, 0x64, 0x0D, 0x00, 0x00, 0x00,
						0x03, 0x00, 0x01, 0x00, 0x02, 0x04, 0x00, 0x07, 0x00,
						0x00, (byte) 0x82, 0x03 };

				mSpManagerCom3.Send(cmd115200);
			}
		}
	}

	public void clearCom3Command() {
		// 02 00 64 0D 00 00 00 03 00 01 00 07 04 FF 02 00 00 81 03
		if (mSpManagerCom3 != null) {
			byte[] cmd = { 0x02, 0x00, 0x64, 0x0D, 0x00, 0x00, 0x00, 0x03,
					0x00, 0x01, 0x00, 0x07, 0x04, (byte) 0xFF, 0x02, 0x00,
					0x00, (byte) 0x81, 0x03 };
			mSpManagerCom3.Send(cmd);
		}
	}

	public void sendTbM820Command() {
		try {
			byte[] boardInfoCmdTrimble830 = "$PASHS,SNS,DUO,2".getBytes();
			mSpManagerCom3.Sendln(boardInfoCmdTrimble830);

			/*
			 * Thread.sleep(COMMON_THREAD_DELAY); boardInfoCmdTrimble830 =
			 * "$PASHS,2,BLN,ON,2,1".getBytes();
			 * mSpManagerCom3.Sendln(boardInfoCmdTrimble830);
			 *
			 * Thread.sleep(COMMON_THREAD_DELAY); boardInfoCmdTrimble830 =
			 * "$PASHS,1,BLN,ON,?".getBytes();
			 * mSpManagerCom3.Sendln(boardInfoCmdTrimble830);
			 *
			 * Thread.sleep(COMMON_THREAD_DELAY); boardInfoCmdTrimble830 =
			 * "$PASHS,3,BLN,SAM".getBytes();
			 * mSpManagerCom3.Sendln(boardInfoCmdTrimble830);
			 *
			 * Thread.sleep(COMMON_THREAD_DELAY); boardInfoCmdTrimble830 =
			 * "$PASHS,3DF,ON,2".getBytes();
			 * mSpManagerCom3.Sendln(boardInfoCmdTrimble830);
			 *
			 * Thread.sleep(COMMON_THREAD_DELAY); boardInfoCmdTrimble830 =
			 * "$PASHS,RTK,ON,1,3".getBytes();
			 * mSpManagerCom3.Sendln(boardInfoCmdTrimble830);
			 *
			 * Thread.sleep(COMMON_THREAD_DELAY); boardInfoCmdTrimble830 =
			 * "$PASHS,BRV,OFF".getBytes();
			 * mSpManagerCom3.Sendln(boardInfoCmdTrimble830);
			 */
			Thread.sleep(COMMON_THREAD_DELAY);

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private class UBCommandThread extends Thread {

		@Override
		public void run() {

			super.run();

			System.out.println("start ECU command");
			try {
				// 启动卫星系统
				Thread.sleep(1000);

				// String command = "syscontrol enable GPS";
				// mSpManagerCom3.Sendln(command.getBytes());
				// Thread.sleep(COMMON_THREAD_DELAY);
				//
				// command = "assignall GPS auto";
				// mSpManagerCom3.Sendln(command.getBytes());
				// Thread.sleep(COMMON_THREAD_DELAY);
				//
				// command = "syscontrol enable BD2";
				// mSpManagerCom3.Sendln(command.getBytes());
				//
				// Thread.sleep(COMMON_THREAD_DELAY);
				//
				// command = "assignall BD2 auto";
				// mSpManagerCom3.Sendln(command.getBytes());
				// Thread.sleep(COMMON_THREAD_DELAY);

				String command = "unlogall com3 true";
				mSpManagerCom3.Sendln(command.getBytes());
				Thread.sleep(COMMON_THREAD_DELAY);

				command = "unlogall com1 true";
				mSpManagerCom3.Sendln(command.getBytes());
				Thread.sleep(COMMON_THREAD_DELAY);

				command = "unlogall com2 true";
				mSpManagerCom3.Sendln(command.getBytes());
				Thread.sleep(COMMON_THREAD_DELAY);

				command = "NMEATALKER GP";
				mSpManagerCom3.Sendln(command.getBytes());
				Thread.sleep(COMMON_THREAD_DELAY);

				if (mMotherType == GnssEnum.MotherBoardEnum.ZDU820.getValue()) {
					command = "log com1 headinga onchanged";
					mSpManagerCom3.Sendln(command.getBytes());
					Thread.sleep(COMMON_THREAD_DELAY);
				}

				// com 1 命令初始化
				command = "com com1 115200";
				mSpManagerCom3.Sendln(command.getBytes());
				Thread.sleep(COMMON_THREAD_DELAY);

				command = "log com1 gpgga ontime 0.1";
				mSpManagerCom3.Sendln(command.getBytes());
				Thread.sleep(COMMON_THREAD_DELAY);

				command = "log com1 gpvtg ontime 0.1";
				mSpManagerCom3.Sendln(command.getBytes());
				Thread.sleep(COMMON_THREAD_DELAY);

				command = "log com1 gpzda ontime 1";
				mSpManagerCom3.Sendln(command.getBytes());
				Thread.sleep(COMMON_THREAD_DELAY);

				System.out.println("start gnss command");

				command = "com com2 19200";
				mSpManagerCom3.Sendln(command.getBytes());
				Thread.sleep(COMMON_THREAD_DELAY);

				uCom3Gnss();

				mIsSendCommand = true;

				// mAskToResolve.set();

				// mLock.notify();
				if (mHandler != null)
					mHandler.sendEmptyMessage(0);

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private class ZB200UBCommandThread extends Thread {

		@Override
		public void run() {

			super.run();

			System.out.println("start ECU command");
			try {
				// 启动卫星系统
				Thread.sleep(1000);

				String command = "unlog";
				mSpManagerCom3.Sendln(command.getBytes());
				Thread.sleep(COMMON_THREAD_DELAY);

				command = "mode rover";
				mSpManagerCom3.Sendln(command.getBytes());
				Thread.sleep(COMMON_THREAD_DELAY);

				command = "agrica 5";
				mSpManagerCom3.Sendln(command.getBytes());
				Thread.sleep(COMMON_THREAD_DELAY);

				// com 1 命令初始化
				command = "gpgga com1 0.1";
				mSpManagerCom3.Sendln(command.getBytes());
				Thread.sleep(COMMON_THREAD_DELAY);

				command = "gpvtg com1 0.1";
				mSpManagerCom3.Sendln(command.getBytes());
				Thread.sleep(COMMON_THREAD_DELAY);

				command = "gpzda com1 1";
				mSpManagerCom3.Sendln(command.getBytes());
				Thread.sleep(COMMON_THREAD_DELAY);

				command = "gphdt com1 0.1";
				mSpManagerCom3.Sendln(command.getBytes());
				Thread.sleep(COMMON_THREAD_DELAY);

				command = "CONFIG PPS ENABLE2 GPS NEGATIVE 1000 1000 0 0";
				mSpManagerCom3.Sendln(command.getBytes());
				Thread.sleep(COMMON_THREAD_DELAY);

				command = "gpgga com3 1";
				mSpManagerCom3.Sendln(command.getBytes());
				Thread.sleep(COMMON_THREAD_DELAY);

				command = "gpvtg com3 1";
				mSpManagerCom3.Sendln(command.getBytes());
				Thread.sleep(COMMON_THREAD_DELAY);

				command = "gpzda com3 1";
				mSpManagerCom3.Sendln(command.getBytes());
				Thread.sleep(COMMON_THREAD_DELAY);

				command = "gphdt com3 1";
				mSpManagerCom3.Sendln(command.getBytes());
				Thread.sleep(COMMON_THREAD_DELAY);

				command = "config com2 19200";
				mSpManagerCom3.Sendln(command.getBytes());
				Thread.sleep(COMMON_THREAD_DELAY);

				command = "version";
				mSpManagerCom3.Sendln(command.getBytes());
				Thread.sleep(COMMON_THREAD_DELAY);

				command = "saveconfig";
				mSpManagerCom3.Sendln(command.getBytes());
				Thread.sleep(COMMON_THREAD_DELAY);
				// uCom3Gnss();

				mIsSendCommand = true;

				// mAskToResolve.set();

				// mLock.notify();
				if (mHandler != null)
					mHandler.sendEmptyMessage(0);

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 *
	 */
	private void iniTrimbleEcuCommand() {
		try {
			if (mMotherType == GnssEnum.MotherBoardEnum.ZDTM820.getValue()) {
				byte[] boardInfoCmdTrimble830 = "$PASHS,PRT,A,9".getBytes();
				mSpManagerCom3.Sendln(boardInfoCmdTrimble830);
				Thread.sleep(COMMON_THREAD_DELAY);
				boardInfoCmdTrimble830 = "$PASHS,PRT,B,6".getBytes();
				mSpManagerCom3.Sendln(boardInfoCmdTrimble830);
				Thread.sleep(COMMON_THREAD_DELAY);
			}
			changeCom1Baudrate();
			sendTbM820Command();
			Thread.sleep(COMMON_THREAD_DELAY);

			byte[] gga = { 0x02, 0x00, 0x64, 0x0D, 0x00, 0x00, 0x00, 0x03,
					0x00, 0x01, 0x00, 0x07, 0x04, 0x06, 0x00, 0x01, 0x00,
					(byte) 0x87, 0x03 };
			mSpManagerCom3.Send(gga);

			Thread.sleep(COMMON_THREAD_DELAY);

			byte[] vtg = { 0x02, 0x00, 0x64, 0x0D, 0x00, 0x00, 0x00, 0x03,
					0x00, 0x01, 0x00, 0x07, 0x04, 0x0c, 0x00, 0x01, 0x00,
					(byte) 0x8D, 0x03 };
			mSpManagerCom3.Send(vtg);

			Thread.sleep(COMMON_THREAD_DELAY);

			byte[] zdacmd = { 0x02, 0x00, 0x64, 0x0D, 0x00, 0x00, 0x00, 0x03,
					0x00, 0x01, 0x00, 0x07, 0x04, 0x08, 0x00, 0x03, 0x00,
					(byte) 0x8B, 0x03 };

			mSpManagerCom3.Send(zdacmd);

			Thread.sleep(COMMON_THREAD_DELAY);

			// 发送AVR 命令 10hz
			if (mMotherType == GnssEnum.MotherBoardEnum.ZDT820.getValue()) {
				// avr 02 00 64 0D 00 00 00 03 00 01 00 07 04 1D 00 01 00 9E 03
				byte[] avr = { 0x02, 0x00, 0x64, 0x0D, 0x00, 0x00, 0x00, 0x03,
						0x00, 0x01, 0x00, 0x07, 0x04, 0x1D, 0x00, 0x01, 0x00,
						(byte) 0x9E, 0x03 };

				mSpManagerCom3.Send(avr);
				Thread.sleep(COMMON_THREAD_DELAY);
			} else if (mMotherType == GnssEnum.MotherBoardEnum.ZDTM820
					.getValue()) {
				byte[] boardInfoCmdTrimble830 = "$PASHS,NME,AVR,A,ON,0.1"
						.getBytes();
				mSpManagerCom3.Sendln(boardInfoCmdTrimble830);
				Thread.sleep(COMMON_THREAD_DELAY);
			}

		} catch (InterruptedException e) {

			e.printStackTrace();
		}
	}

	/**
	 * 启动命令线程，只发ECU命令、COM3 GGA、ZDA命令
	 *
	 * @author Administrator
	 *
	 */
	private class TrimbleCommandThread extends Thread {
		@Override
		public void run() {

			super.run();

			System.out.println("start ECU command");
			try {
				Thread.sleep(2000);
				// 02 00 64 0D 00 00 00 03 00 01 00 07 04 06 00 01 00 87 03

				// System.out.println("start gnss command");

				iniTrimbleEcuCommand();

				Thread.sleep(COMMON_THREAD_DELAY);

				changeCom2Baudrate();

				Thread.sleep(COMMON_THREAD_DELAY);

				tCom3Gnss();

				mIsSendCommand = true;

				// mAskToResolve.set();

				// mLock.notify();\
				if (mHandler != null)
					mHandler.sendEmptyMessage(0);

			} catch (InterruptedException e) {

				e.printStackTrace();
			}

		}

	}

	// public void iniUBcommand(Handler handler) {
	// if (mSpManagerCom3 == null)
	// return;
	//
	// mHandler = handler;
	//
	// mPool = Executors.newSingleThreadExecutor();
	//
	// Thread thread= new UBCommandThread();
	// mPool.execute(thread);
	// mPool.shutdown();
	// }

	public void changeCom2Baudrate() {
		// 02 00 64 0D 00 00 00 03 00 01 00 02 04 01 04 00 00 80 03
		if (mSpManagerCom3 != null) {
			if (mMotherType == GnssEnum.MotherBoardEnum.ZDTM820.getValue()) {

				byte[] boardInfoCmdTrimble830 = "$PASHS,PRT,B,6".getBytes();
				mSpManagerCom3.Sendln(boardInfoCmdTrimble830);

			} else {
				byte[] cmd = { 0x02, 0x00, 0x64, 0x0D, 0x00, 0x00, 0x00, 0x03,
						0x00, 0x01, 0x00, 0x02, 0x04, 0x01, 0x04, 0x00, 0x00,
						(byte) 0x80, 0x03 };

				mSpManagerCom3.Send(cmd);
			}
		}
	}

	/*
	 * 系统启动时的命令
	 */
	public void iniCommand(Handler handler) {

		// 02 00 64 0D 00 00 00 03 00 01 00 07 04 00 00 00 00 80 03
		if (mSpManagerCom3 == null)
			return;

		mHandler = handler;

		mPool = Executors.newSingleThreadExecutor();

		Thread thread = null;
		if (mMotherType == GnssEnum.MotherBoardEnum.ZDT810.getValue()
				&& mMotherType == GnssEnum.MotherBoardEnum.ZDT820.getValue()
				&& mMotherType == GnssEnum.MotherBoardEnum.ZDTM820.getValue())// 天宝
		{
			thread = new TrimbleCommandThread();
		} else if (mMotherType == GnssEnum.MotherBoardEnum.ZDN810.getValue())// 诺瓦泰
		{
			thread = new UBCommandThread();
		} else if (mMotherType == GnssEnum.MotherBoardEnum.ZDU810.getValue()
				|| mMotherType == GnssEnum.MotherBoardEnum.ZDU820.getValue())// 和芯星通
		{
			thread = new UBCommandThread();
		} else if (mMotherType == GnssEnum.MotherBoardEnum.ZC200MU.getValue())// 和芯星通
		{
			thread = new ZB200UBCommandThread();
		} else {
			thread = new TrimbleCommandThread();
		}

		if (thread != null) {
			mPool.execute(thread);
			mPool.shutdown();
		}
	}

	public void sendDYN5() {
		Thread sendDYN5Thread = null;
		if (mMotherType == GnssEnum.MotherBoardEnum.ZDTM820.getValue()) {
			sendDYN5Thread = new Thread(new Runnable() {

				@Override
				public void run() {

					try {

						byte[] boardInfoDYN5 = "$PASHS,DYN,5".getBytes();
						if (mSpManagerCom3 != null)
							mSpManagerCom3.Sendln(boardInfoDYN5);
						Thread.sleep(1000);
						boardInfoDYN5 = "$PASHQ,DYN".getBytes();
						if (mSpManagerCom3 != null)
							mSpManagerCom3.Sendln(boardInfoDYN5);

					} catch (InterruptedException e) {

						e.printStackTrace();
					}
				}
			});
			if (sendDYN5Thread != null) {
				mPool = Executors.newSingleThreadExecutor();
				mPool.execute(sendDYN5Thread);
				mPool.shutdown();
			}
		}

	}

	/*
	 * 星空图发送的命令
	 */
	public void iniSatInfoCommand() {
		Thread satInfoThread = null;

		if (mMotherType == GnssEnum.MotherBoardEnum.ZDT810.getValue()
				|| mMotherType == GnssEnum.MotherBoardEnum.ZDT820.getValue()
				|| mMotherType == GnssEnum.MotherBoardEnum.ZDTM820.getValue())// 天宝
		{
			satInfoThread = new Thread(new Runnable() {

				@Override
				public void run() {

					try {

						clearCom3Command();

						Thread.sleep(COMMON_THREAD_DELAY);

						sendGnssGGACommand();

						Thread.sleep(COMMON_THREAD_DELAY);

						sendGSOFCommand();

						Thread.sleep(COMMON_THREAD_DELAY);

						sendGnssGSACommand();

						Thread.sleep(COMMON_THREAD_DELAY);

						sendGnssVTGCommand();

						Thread.sleep(COMMON_THREAD_DELAY);
						sendGnssGSVCommand();

						Thread.sleep(COMMON_THREAD_DELAY);
						sendGSOFCommand();

						Thread.sleep(COMMON_THREAD_DELAY);

					} catch (InterruptedException e) {

						e.printStackTrace();
					}
				}
			});

		} else if (mMotherType == GnssEnum.MotherBoardEnum.ZDN810.getValue())// 诺瓦泰
		{
			satInfoThread = new Thread(new Runnable() {

				@Override
				public void run() {

				}

			});
		} else if (mMotherType == GnssEnum.MotherBoardEnum.ZDU810.getValue()
				|| mMotherType == GnssEnum.MotherBoardEnum.ZDU820.getValue())// 和芯星通
		{
			satInfoThread = new Thread(new Runnable() {

				@Override
				public void run() {

					try {

						String command = "log com3 satvisb ontime 6";
						mSpManagerCom3.Sendln(command.getBytes());
						Thread.sleep(COMMON_THREAD_DELAY);

						command = "log com3 gpgga ontime 1";
						mSpManagerCom3.Sendln(command.getBytes());
						Thread.sleep(COMMON_THREAD_DELAY);

						command = "log com3 rangecmpb ontime 6";
						mSpManagerCom3.Sendln(command.getBytes());
						Thread.sleep(COMMON_THREAD_DELAY);

						command = "log com3 gpgsa ontime 1";
						mSpManagerCom3.Sendln(command.getBytes());
						Thread.sleep(COMMON_THREAD_DELAY);

						command = "log com3 gpgsv ontime 5";
						mSpManagerCom3.Sendln(command.getBytes());
						Thread.sleep(COMMON_THREAD_DELAY);

						command = "log com3 bestvela ontime 1";
						mSpManagerCom3.Sendln(command.getBytes());
						Thread.sleep(COMMON_THREAD_DELAY);

						command = "saveconfig";
						mSpManagerCom3.Sendln(command.getBytes());
						Thread.sleep(COMMON_THREAD_DELAY);

					} catch (InterruptedException e) {

						e.printStackTrace();
					}
				}
			});
		} else if (mMotherType == GnssEnum.MotherBoardEnum.ZC200MU.getValue())// 和芯星通
		{
			satInfoThread = new Thread(new Runnable() {

				@Override
				public void run() {

					try {

						String command = "unlog com3 ";
						mSpManagerCom3.Sendln(command.getBytes());
						Thread.sleep(COMMON_THREAD_DELAY);

						/*
						 * command = "mode rover";
						 * mSpManagerCom3.Sendln(command.getBytes());
						 * Thread.sleep(COMMON_THREAD_DELAY);
						 *
						 * command = "agrica 5";
						 * mSpManagerCom3.Sendln(command.getBytes());
						 * Thread.sleep(COMMON_THREAD_DELAY);
						 */

						// com 1 命令初始化
						/*
						 * command = "gpgga com1 0.1";
						 * mSpManagerCom3.Sendln(command.getBytes());
						 * Thread.sleep(COMMON_THREAD_DELAY);
						 *
						 * command = "gpvtg com1 0.1";
						 * mSpManagerCom3.Sendln(command.getBytes());
						 * Thread.sleep(COMMON_THREAD_DELAY);
						 *
						 * command = "gpzda com1 1";
						 * mSpManagerCom3.Sendln(command.getBytes());
						 * Thread.sleep(COMMON_THREAD_DELAY);
						 *
						 * command = "gphdt com1 0.1";
						 * mSpManagerCom3.Sendln(command.getBytes());
						 * Thread.sleep(COMMON_THREAD_DELAY);
						 */
						command = "gpgga com3 1";
						mSpManagerCom3.Sendln(command.getBytes());
						Thread.sleep(COMMON_THREAD_DELAY);

						command = "gpvtg com3 1";
						mSpManagerCom3.Sendln(command.getBytes());
						Thread.sleep(COMMON_THREAD_DELAY);

						command = "gpzda com3 1";
						mSpManagerCom3.Sendln(command.getBytes());
						Thread.sleep(COMMON_THREAD_DELAY);

						command = "gphdt com3 1";
						mSpManagerCom3.Sendln(command.getBytes());
						Thread.sleep(COMMON_THREAD_DELAY);

						command = "gpgsv com3 1";
						mSpManagerCom3.Sendln(command.getBytes());
						Thread.sleep(COMMON_THREAD_DELAY);

						command = "gngsv com3 1";
						mSpManagerCom3.Sendln(command.getBytes());
						Thread.sleep(COMMON_THREAD_DELAY);
						sendbestposa();
						Thread.sleep(COMMON_THREAD_DELAY);

						/*
						 * command = "bdgsv com3 1";
						 * mSpManagerCom3.Sendln(command.getBytes());
						 * Thread.sleep(COMMON_THREAD_DELAY);
						 *
						 * command = "gpgsv com3 1";
						 * mSpManagerCom3.Sendln(command.getBytes());
						 * Thread.sleep(COMMON_THREAD_DELAY);
						 */
						/*
						 * command = "config com2 19200";
						 * mSpManagerCom3.Sendln(command.getBytes());
						 * Thread.sleep(COMMON_THREAD_DELAY);
						 */
						/*
						 * command = "saveconfig";
						 * mSpManagerCom3.Sendln(command.getBytes());
						 * Thread.sleep(COMMON_THREAD_DELAY);
						 */

					} catch (InterruptedException e) {

						e.printStackTrace();
					}
				}
			});
		} else {
			satInfoThread = new Thread(new Runnable() {

				@Override
				public void run() {

					try {

						clearCom3Command();

						Thread.sleep(COMMON_THREAD_DELAY);

						sendGnssGGACommand();

						Thread.sleep(COMMON_THREAD_DELAY);

						sendGSOFCommand();

						Thread.sleep(COMMON_THREAD_DELAY);

						sendGnssGSACommand();

						Thread.sleep(COMMON_THREAD_DELAY);

						sendGnssVTGCommand();

						Thread.sleep(COMMON_THREAD_DELAY);
					} catch (InterruptedException e) {

						e.printStackTrace();
					}
				}
			});
		}

		mPool = Executors.newSingleThreadExecutor();
		mPool.execute(satInfoThread);
		mPool.shutdown();
	}

	/*
	 * 离开星空图发送的命令
	 */
	public void iniBaseCommand() {
		Thread baseCommandThread = null;

		if (mMotherType == GnssEnum.MotherBoardEnum.ZDT810.getValue()
				|| mMotherType == GnssEnum.MotherBoardEnum.ZDT820.getValue()
				|| mMotherType == GnssEnum.MotherBoardEnum.ZDTM820.getValue())// 天宝
		{
			baseCommandThread = new Thread(new Runnable() {

				@Override
				public void run() {

					try {

						tCom3Gnss();

					} catch (InterruptedException e) {

						e.printStackTrace();
					}
				}
			});

		} else if (mMotherType == GnssEnum.MotherBoardEnum.ZDN810.getValue())// 诺瓦泰
		{

		} else if (mMotherType == GnssEnum.MotherBoardEnum.ZDU810.getValue()
				|| mMotherType == GnssEnum.MotherBoardEnum.ZDU820.getValue())// 和芯星通
		{
			baseCommandThread = new Thread(new Runnable() {

				@Override
				public void run() {

					try {
						uCom3Gnss();
					} catch (InterruptedException e) {

						e.printStackTrace();
					}
				}
			});
		} else if (mMotherType == GnssEnum.MotherBoardEnum.ZC200MU.getValue())// 和芯星通
		{
			baseCommandThread = new Thread(new Runnable() {

				@Override
				public void run() {

					try {

						String command = "unlog com3";
						mSpManagerCom3.Sendln(command.getBytes());
						Thread.sleep(COMMON_THREAD_DELAY);

						/*
						 * command = "mode rover";
						 * mSpManagerCom3.Sendln(command.getBytes());
						 * Thread.sleep(COMMON_THREAD_DELAY);
						 *
						 * command = "agrica 5";
						 * mSpManagerCom3.Sendln(command.getBytes());
						 * Thread.sleep(COMMON_THREAD_DELAY);
						 */

						/*
						 * // com 1 命令初始化 command = "gpgga com1 0.1";
						 * mSpManagerCom3.Sendln(command.getBytes());
						 * Thread.sleep(COMMON_THREAD_DELAY);
						 *
						 * command = "gpvtg com1 0.1";
						 * mSpManagerCom3.Sendln(command.getBytes());
						 * Thread.sleep(COMMON_THREAD_DELAY);
						 *
						 * command = "gpzda com1 1";
						 * mSpManagerCom3.Sendln(command.getBytes());
						 * Thread.sleep(COMMON_THREAD_DELAY);
						 *
						 * command = "gphdt com1 0.1";
						 * mSpManagerCom3.Sendln(command.getBytes());
						 * Thread.sleep(COMMON_THREAD_DELAY);
						 */

						command = "gpgga com3 1";
						mSpManagerCom3.Sendln(command.getBytes());
						Thread.sleep(COMMON_THREAD_DELAY);

						command = "gpvtg com3 1";
						mSpManagerCom3.Sendln(command.getBytes());
						Thread.sleep(COMMON_THREAD_DELAY);

						command = "gpzda com3 1";
						mSpManagerCom3.Sendln(command.getBytes());
						Thread.sleep(COMMON_THREAD_DELAY);

						command = "gphdt com3 1";
						mSpManagerCom3.Sendln(command.getBytes());
						Thread.sleep(COMMON_THREAD_DELAY);

						/*
						 * command = "config com2 19200";
						 * mSpManagerCom3.Sendln(command.getBytes());
						 * Thread.sleep(COMMON_THREAD_DELAY);
						 */
						/*
						 * command = "saveconfig";
						 * mSpManagerCom3.Sendln(command.getBytes());
						 */
						Thread.sleep(COMMON_THREAD_DELAY);

					} catch (InterruptedException e) {

						e.printStackTrace();
					}
				}
			});
		}

		if (baseCommandThread != null) {
			mPool = Executors.newSingleThreadExecutor();
			mPool.execute(baseCommandThread);
			mPool.shutdown();
		}

	}

	/**
	 * Trimble Com3 ini command
	 *
	 * @throws InterruptedException
	 */
	private void tCom3Gnss() throws InterruptedException {
		/*
		 * if (mMotherType == GnssEnum.MotherBoardEnum.ZDTM820.getValue()) {
		 * sendTbM820Command(); }
		 */

		clearCom3Command();

		Thread.sleep(COMMON_THREAD_DELAY);

		sendGnssGGACommand();

		Thread.sleep(COMMON_THREAD_DELAY);

		sendZDACommand();
		Thread.sleep(COMMON_THREAD_DELAY);
		sendGnssVTGCommand();
		Thread.sleep(COMMON_THREAD_DELAY);

		// 35h 基站坐标
		byte[] cmd = { 0x02, 0x00, 0x64, 0x11, 0x00, 0x00, 0x00, 0x03, 0x00,
				0x01, 0x00, 0x07, 0x08, 0x0a, 0x02, 0x05, 0x00, 0x23, 0x00,
				0x00, 0x00, (byte) 0xbc, 0x03 };

		mSpManagerCom3.Send(cmd);
		Thread.sleep(COMMON_THREAD_DELAY);
		if (mMotherType == GnssEnum.MotherBoardEnum.ZDT820.getValue()) {
			// avr 02 00 64 0D 00 00 00 03 00 01 00 07 04 1D 00 01 00 9E 03
			byte[] avr = { 0x02, 0x00, 0x64, 0x0D, 0x00, 0x00, 0x00, 0x03,
					0x00, 0x01, 0x00, 0x07, 0x04, 0x1D, 0x02, 0x03, 0x00,
					(byte) 0xa2, 0x03 };

			mSpManagerCom3.Send(avr);
			Thread.sleep(COMMON_THREAD_DELAY);
		} else if (mMotherType == GnssEnum.MotherBoardEnum.ZDTM820.getValue()) {
			/*
			 * byte[] boardInfoCmdTrimble830 =
			 * "$PASHS,NME,AVR,D,ON,1".getBytes();
			 * mSpManagerCom3.Sendln(boardInfoCmdTrimble830);
			 * Thread.sleep(COMMON_THREAD_DELAY); boardInfoCmdTrimble830 =
			 * "$PASHS,ATM,RNX,D,ON,3.0".getBytes();
			 * mSpManagerCom3.Sendln(boardInfoCmdTrimble830);
			 * Thread.sleep(COMMON_THREAD_DELAY);
			 */
		}
	}

	/**
	 * Unicore Com3 ini command
	 *
	 * @throws InterruptedException
	 */
	private void uCom3Gnss() throws InterruptedException {

		// com 3 命令初始化
		String command = "unlogall com3 true";
		mSpManagerCom3.Sendln(command.getBytes());
		Thread.sleep(COMMON_THREAD_DELAY);

		command = "NMEATALKER GP";
		mSpManagerCom3.Sendln(command.getBytes());
		Thread.sleep(COMMON_THREAD_DELAY);

		command = "log com3 gpgga ontime 1";
		mSpManagerCom3.Sendln(command.getBytes());
		Thread.sleep(COMMON_THREAD_DELAY);

		command = "log com3 gpzda ontime 1";
		mSpManagerCom3.Sendln(command.getBytes());
		Thread.sleep(COMMON_THREAD_DELAY);

		command = "log com3 gpvtg ontime 1";
		mSpManagerCom3.Sendln(command.getBytes());
		Thread.sleep(COMMON_THREAD_DELAY);

		command = "rtktimeout 5";
		mSpManagerCom3.Sendln(command.getBytes());
		Thread.sleep(COMMON_THREAD_DELAY);

		command = "saveconfig";
		mSpManagerCom3.Sendln(command.getBytes());
		Thread.sleep(COMMON_THREAD_DELAY);

		System.out.println("end gnss command");
	}
}
