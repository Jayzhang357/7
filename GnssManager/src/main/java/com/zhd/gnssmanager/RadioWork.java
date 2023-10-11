package com.zhd.gnssmanager;

import java.util.Vector;

import android.R.bool;
import android.R.integer;
import android.app.Service;
import android.content.Intent;
import android.content.ContextWrapper;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.zhd.gnssmanager.R.string;

import com.zhd.serialportmanage.*;

import com.uhf.control.UHFClass;

public class RadioWork {

	private UHFClass mUhfClass;
	public static RadioWork RADIO_WORK = null;
	private Thread mThread = null;
	private Thread mReadRssiThread = null;
	private byte[] mBuf = null;
	private byte[] mReadBuf = null;
	private boolean mFlag = true;
	private int mFd = 0;
	private IReceiveListener mReceiveListener = null;
	private boolean mIsRunning = false;
	private SerialPortManager mDiffSerialPort = null;
	private IConnectStatusListener mConnectStatusListener = null;
	private IRssiListener mRssiListener = null;
	private boolean mIsReconnect = false;
	private int mRssi = 0;
	public final int RADIO_RUNNING = 1000;
	public final int RADIO_INTERRUPT = 1001;
	private static String mDifSerialPortName = "/dev/ttymxc1";
	private final int mDifBaudrate = 19200;
	private Boolean mIsPause = false;
	private DifferDataReceive mDataReceive=null;
	private Vector<IRssiListener> mRssiListenerVector = new Vector<RadioWork.IRssiListener>();
	private boolean mIsReadingData = false;
	private int mDifferBaudrateCom2;

	/**
	 * 广播名称
	 */
	private final String SEND_RADIO_ACTION = "RADIO_DATA";

	/**
	 * 电台广播数据标识
	 */
	private final String FLAG_RSSI = "RSSI_STATUS";

	/**
	 * 差分数据广播标识
	 */
	private final String FLAG_DIFFDATA = "DIFF_DATA";
	private final String FLAG_RADIO_STATUS = "RADIO_STATUS";
	private int mMOTHER_BOARD_TYPE;
	public RadioWork(int MOTHER_BOARD_TYPE,int DifferBaudrateCom2) {

		mDifferBaudrateCom2=DifferBaudrateCom2;
		mMOTHER_BOARD_TYPE=MOTHER_BOARD_TYPE;
	}

	/**
	 * 获取实例
	 *
	 * @return
	 */
	public static RadioWork getInstance(int MOTHER_BOARD_TYPE,int DifferBaudrateCom2,String Comname) {
		mDifSerialPortName=Comname;
		if (RADIO_WORK == null)
			RADIO_WORK = new RadioWork(MOTHER_BOARD_TYPE,DifferBaudrateCom2);

		return RADIO_WORK;
	}

	/**
	 * 打开串口
	 *
	 * @return
	 */
	public boolean openRadioCom() {
		int value = mUhfClass.OpenCOM();
		return value == 1;

	}

	/**
	 * 发送数据
	 *
	 * @return
	 */
	public void sendData(byte[] buff) {
		mUhfClass.UhfSendData(0, buff, buff.length);

	}

	public void sendDiffDataBySerialPort(byte[] buff) {
//		if (mDiffSerialPort == null)
//			mDiffSerialPort = new SerialPortManager(mDifBaudrate,
//					mDifSerialPortName);
//		mDiffSerialPort.Send(buff);

		if(mDataReceive==null)
			mDataReceive=DifferDataReceive.getInstance(mMOTHER_BOARD_TYPE,mDifferBaudrateCom2,mDifSerialPortName);
		mDataReceive.sendByte(buff, 0);

	}

	/**
	 * 关闭串口
	 *
	 * @return
	 */
	public void closeRadioCom() {
		UHFClass.Close();
	}

	/**
	 * 收差分
	 */
	public void startReceiving() {

		if (mThread == null) {
			mThread = new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					mBuf = new byte[1024];
					int readBufLength = 0;
					mIsRunning = true;

					if (mConnectStatusListener != null)
						mConnectStatusListener.connectListen(true);

					while (mIsRunning) {

						try {

							if (mIsPause == false) {

								readBufLength = UHFClass.UhfRecvData(mFd,
										mBuf, mBuf.length);

								Log.i("radio", Integer.toString(readBufLength));

								if (readBufLength != -1 && readBufLength > 0) {

									mReadBuf = new byte[readBufLength];
									System.arraycopy(mBuf, 0, mReadBuf, 0,
											readBufLength);

									// mReceiveListener.HandlerReceiveData(mReadBuf);
									sendDiffDataBySerialPort(mReadBuf);

									if (mIsReconnect) {
										// 通知线程重连成功
										if (mConnectStatusListener != null)
											mConnectStatusListener
													.connectListen(true);

										mIsReconnect = false;
									}

									Log.i("radiobuf", Integer.toString(readBufLength));

									Thread.sleep(50);

								} else {
									try {
										// 如果读不到数据 则线程休眠300秒
										Thread.sleep(300);

										Log.i("radiobuf", "***");

									} catch (InterruptedException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
							} else {
//								UHFClass.Close();

								Thread.sleep(1000);

//								UHFClass.OpenCOM();
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();

							mFlag = false;
							mIsRunning = false;

							// 通知线程中止
							if (mConnectStatusListener != null)
								mConnectStatusListener.connectListen(false);

						}
					}
				}
			});

			mThread.start();
		}
	}

	public byte[] getReadBuf() {
		if (mReadBuf != null) {
			return mReadBuf;
		} else
			return null;
	}

	public void setThreadStatus(boolean flag) {
		mFlag = flag;
	}

	public void stop() {
		mFlag = false;
		if (mThread != null) {
			mIsRunning = false;

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			mThread.interrupt();
			mThread = null;

			mReadRssiThread.interrupt();
			mReadRssiThread = null;
			UHFClass.Close();
		}

//		if (mDiffSerialPort != null) {
//			mDiffSerialPort.finalize();
//			mDiffSerialPort = null;
//		}
//		if(mDataReceive!=null)
//		{
//			mDataReceive.finalize();
//			mDataReceive=null;
//		}
	}

	public void pause() {
		mIsPause = true;
	}

	public void resume() {
		openRadioCom();

		mIsPause = false;
	}

	public boolean isRunning() {
		return mIsRunning;

	}

	public boolean reOpenRadio() {
		mIsReconnect = true;
		stop();
		return start();

	}

	public boolean start() {
		boolean isOpen = false;

		try {
			isOpen = openRadioCom();
			if (isOpen) {
				mFlag = true;
				mIsRunning = true;
				startReceiving();
				startReadRssi();
			}

		} catch (Exception e) {
			// TODO: handle exception
			isOpen = false;
		}

		return isOpen;
	}

	/**
	 * 收状态值 超过2.0为收到差分
	 */
	public void startReadRssi() {
		if (mReadRssiThread == null) {
			mReadRssiThread = new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub

					// 通知场强大小
					while (mIsRunning) {
						try {

							if (mIsPause == false) {

								if (mRssiListenerVector.size() > 0) {
									mRssi = UHFClass.UhfRssiRead2();
									System.out.println("rssi:"
											+ Integer.toString(mRssi));
									synchronized (mRssiListenerVector) {
										for (int i = 0; i < mRssiListenerVector.size(); i++) {
											mRssiListener =	mRssiListenerVector.get(i);
											mRssiListener.updateRadioRssi(mRssi);
										}
									}

									Log.i("Rssi:", Integer.toString(mRssiListenerVector.size()));
								}

								Thread.sleep(500);

							} else {
								Thread.sleep(1000);
							}
						} catch (Exception e) {
							// TODO: handle exception
						}
					}

				}
			});

			mReadRssiThread.start();
		}
	}

	/**
	 * 处理差分数据的接口
	 *
	 * @author Administrator
	 *
	 */
	public interface IReceiveListener {

		public void HandlerReceiveData(byte[] data);
	}

	public void setReceiveListener(IReceiveListener listener) {
		mReceiveListener = listener;
	}

	public interface IConnectStatusListener {
		public void connectListen(boolean isConnect);

	}

	/**
	 * 处理连接状态接口
	 *
	 * @param listener
	 */
	public void setConnectStatusListener(IConnectStatusListener listener) {
		mConnectStatusListener = listener;
	}

	public interface IRssiListener {
		public void updateRadioRssi(int rrsivalue);

	}

	public void setRssiListener(IRssiListener listener) {
		mRssiListenerVector.add(listener);
	}

	public void removesRssiListener(IRssiListener listener) {
		synchronized (mRssiListenerVector) {
			mRssiListenerVector.remove(listener);
		}
	}
}
