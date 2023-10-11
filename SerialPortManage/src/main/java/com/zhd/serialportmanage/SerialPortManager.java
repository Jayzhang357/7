package com.zhd.serialportmanage;

import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import android.annotation.SuppressLint;
import android.provider.ContactsContract.Contacts.Data;
import android.util.Log;
import android_serialport_api.*;
import com.zhd.datamanagetemplate.*;

/**
 * 1、该类提供串口的读写操作支持 2、该类内部会创建一个线程轮询串口读取数据 3、由于串口是支持全双工通信，所以不需要在“串口读写之间做同步”
 * 4、外部的串口写入操作采用同步的方式
 *
 * @author Administrator
 *
 */
public class SerialPortManager extends ReceiveSendManager {

	private ConcurrentLinkedQueue<byte[]> mReceiveDatas;
	private Thread mReadSerialPortThread;
	private AtomicBoolean mIsConnected;
	private ManualResetEvent mAskToResolve;
	private SerialPort mSerialPort = null;
	private boolean mIsEof;// 指示是否读到了结尾
	private boolean flag = true;
	private InputStream mInputStream;
	private IReceiveDataListener mDataListener = null;
	private OutputStream mOutputStream;
	// byte[] allBuffer = new byte[10000];
	// int lastIndex = 0;
	private int mBaudrate = 0;
	private String mSerialPortName = "";
	private String mFileNamedByDate = "";
	private int mComnum;

	@SuppressLint("SimpleDateFormat")
	public SerialPortManager(int baudrate, String serialPortName, int Comnum) {
		mIsEof = false;
		mComnum	=Comnum;
		mIsConnected = new AtomicBoolean();
		mIsConnected.set(false);
		mBaudrate = baudrate;
		mSerialPortName = serialPortName;

		mAskToResolve = new ManualResetEvent();
		mReceiveDatas = new ConcurrentLinkedQueue<byte[]>();

		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
		mFileNamedByDate = sDateFormat.format(new java.util.Date()) + ".ZHD";

		try {
			mSerialPort = new SerialPort(new File(mSerialPortName), mBaudrate,
					0);

			mIsConnected.set(true);
			mInputStream = mSerialPort.getInputStream();
			Log.e("打开成功","打开成功");
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			mIsConnected.set(false);
			if (mSerialPort != null)
				mSerialPort.close();
			System.out.println("初始化不成功!");
		}
	}

	public static SerialPort getSerialPort(String path, int baudrate)
			throws SecurityException, IOException, InvalidParameterException {

		/* Check parameters */
		if ((path.length() == 0) || (baudrate == -1)) {
			throw new InvalidParameterException();
		}

		/* Open the serial port */
		SerialPort port = new SerialPort(new File(path), baudrate, 0);
		return port;
	}

	public SerialPort getSerialPort() throws SecurityException, IOException,
			InvalidParameterException {

		return mSerialPort;
	}

	public void finalize() {
		System.out.println("开始关闭串口并释放资源");

		flag = false;
		try {
			if(mInputStream!=null)
				mInputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (mReadSerialPortThread != null)
			mReadSerialPortThread.interrupt();

		if (mSerialPort != null) {
			mSerialPort.close();

		}
		mSerialPort = null;
		mReadSerialPortThread = null;
	}

	public String bytesToHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || src.length <= 0) {
			return null;
		}
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(" ");
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}

	public byte[] FetchNext() {
		try {
			// if(flag==false)
			// Thread.sleep(2000);
			// 等待通知有数据要处理

			// 测试TrackStat
			// byte[] bytes = FileHelper.readByte("trackstatb.gga");
			// return bytes;

			// 线程开多了 会受影响
			mAskToResolve.wait(10, TimeUnit.MILLISECONDS);

			if (mReceiveDatas.size() > 0) {
				try {
					return mReceiveDatas.poll();
					// mReceiveDatas.poll();

				} catch (Exception ep) {

				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 该函数不支持在多线程环境下使用
	 *
	 * @param
	 * @return
	 */
	public boolean Send(byte[] message) {
		if (mIsConnected.get() == true) {
			try {
				if (mSerialPort == null)
					return false;
				mOutputStream = mSerialPort.getOutputStream();
				mOutputStream.write(message);
				mOutputStream.flush();
				Log.v("track255", message.length + "");
				return true;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				reOpenSerialPort();
				/*
				 * if (mSerialPort != null) mSerialPort.close();
				 * mIsConnected.set(false);
				 */
			}
		}
		return false;
	}

	/**
	 * 该函数不支持在多线程环境下使用
	 *
	 * @param
	 * @return
	 */
	public boolean Sendln(byte[] message) {
		if (mIsConnected.get() == true) {
			try {
				if (mSerialPort == null)
					return false;
				mOutputStream = mSerialPort.getOutputStream();
				mOutputStream.write(message);
				mOutputStream.write("\r\n".getBytes());
				mOutputStream.flush();
				return true;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				reOpenSerialPort();
			}
		}
		return false;
	}

	public Boolean getConnectedState() {
		return mIsConnected.get();
	}

	public boolean getIsEof() {
		return mIsEof;
	}

	@Override
	public void Start() {
		// TODO Auto-generated method stub
		if (mReadSerialPortThread != null)
			return;

		flag = true;

		mReadSerialPortThread = new Thread(new Runnable() {

			@Override
			public void run() {

				// 读取串口的数据
				while (flag) {
					// 测试需要
					if (mIsConnected.get() == false) {
						System.out.println("尝试打开串口!");
						// 连接串口
						try {
							if (mSerialPort == null)
								mSerialPort = new SerialPort(new File(
										mSerialPortName), mBaudrate, 0);

							mIsConnected.set(true);
							mInputStream = mSerialPort.getInputStream();
							System.out.println("打开成功!");
						} catch (SecurityException e) {
							try {
								Thread.sleep(5000);// 延时
							} catch (InterruptedException e1) {
								e1.printStackTrace();
							}
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							try {
								Thread.sleep(5000);// 延时
							} catch (InterruptedException e1) {
								e1.printStackTrace();
							}
							// TODO Auto-generated catch block
							e.printStackTrace();
							reOpenSerialPort();
							/*
							 * mIsConnected.set(false); if (mSerialPort != null)
							 * mSerialPort.close();
							 */
							System.out.println("打开不成功错误!");
						}
					} else {
						try {
							if (mInputStream == null)
								return;
							byte[] buffer = new byte[2048];
							int readLen = mInputStream.read(buffer);
							// int readLen = mSerialPort.read(buffer, 0,
							// buffer.length);
							if (readLen > 0) {
								byte[] data = new byte[readLen];
								System.arraycopy(buffer, 0, data, 0, readLen);

								if (mDataListener != null)
									mDataListener.handleReceiveMessage(data);
								/*if (mSerialPortName == "/dev/ttymxc0") {

									if (mDataListener != null)
										mDataListener.handleReceiveMessage1(0,
												data);

								} else if (mSerialPortName == "/dev/ttymxc1") {

									if (mDataListener != null)
										mDataListener.handleReceiveMessage1(1,
												data);

								} else if (mSerialPortName == "/dev/ttymxc2") {
									if (mDataListener != null)
										mDataListener.handleReceiveMessage1(2,
												data);
								}*/
								if (mDataListener != null)
									mDataListener.handleReceiveMessage1(mComnum-1,
											data);
								mReceiveDatas.add(data);
								while (mReceiveDatas.size() > 1000) {
									// 防止缓存过多的时间
									mReceiveDatas.poll();
								}
								// 通知开始处理数据
								mAskToResolve.set();
							}
						} catch (IOException e) {
							e.printStackTrace();
							// mIsConnected.set(false);
							// mSerialPort.close();
							// System.out.println("串口读取错误!");
						}
						try {
							Thread.sleep(100);// 延时
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}

		});
		mReadSerialPortThread.start();
	}

	@Override
	public void ReStart() {
		// TODO Auto-generated method stub

	}

	@Override
	public void Pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void Stop() {
		// TODO Auto-generated method stub
		mIsConnected.set(false);
		if (mSerialPort != null) {
			mSerialPort.close();
		}
	}

	public void Dispose() {
		if (mSerialPort != null) {
			if (mSerialPort.isConnected()) {
				mSerialPort.close();
			}
			mSerialPort = null;
		}
	}

	public void reOpenSerialPort() {
		if (mSerialPort == null)
			try {
				mSerialPort = new SerialPort(new File(mSerialPortName),
						mBaudrate, 0);

				Start();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	}

	public interface IReceiveDataListener {
		public void handleReceiveMessage(byte[] messge);

		public void handleReceiveMessage1(int com, byte[] messge);
	}

	public void setReceiveMessageListener(IReceiveDataListener listener) {
		mDataListener = listener;
	}
}
