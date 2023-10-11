package com.zhd.zhdcorsnet;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android_serialport_api.SerialPort;

import com.zhd.serialportmanage.*;

public class BDXTNetGprsService extends Service implements LocationListener {

	public static final String SERIAL_COM_1 = "/dev/s3c2410_serial1";
	// public static final int BAURATE = 115200;
	public static final String DBXTIP = "bdxt_ip";
	public static final String DBXTPORT = "bdxt_port";
	public static final String DBXTSIMNUMBER = "bdxt_simnumber";
	public static final String DBXTDIFFFORMAT = "bdxt_diffformat";
	public static final String DBXTSERIALBAUDRATE = "bdxt_serialbaudrate";
	public static final byte[] BDXTHEADER = new byte[] { (byte) 0xFE,
			(byte) 0xEF };

	public static boolean mIsConnecting = false;
	private Socket mBDXTSocket;
	private OutputStream m_BDXTOutputStream;
	private InputStream m_BDXTInputStream;
	private SerialPort m_SerialCom1;
	private OutputStream m_OutputCom1;
	private boolean m_IsConnected = false;
	private SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat mTimeFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
	private String mLogPath = "";

	private String m_IP;
	private int m_Port;
	private String m_SIMNumber;
	private String m_DiffFormat;
	private int m_SerialBaudrate = 115200;
	private boolean mIsRorWing = false;
	private LocationManager mLocationManager = null;

	private byte[] m_time_ms_bytes = new byte[2];
	private byte[] m_byt4lon = new byte[4];
	private byte[] m_byt4lat = new byte[4];
	private byte m_bytspeed = 0x00;
	private byte m_bytdirection = 0x00;

	private int mStartFlags = 0;

	@Override
	public void onCreate() {
		mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				10000, 0, this);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		mStartFlags++;
		writeLog(String.format("mStartFlags:%s,flags:%s,startId:%s",
				mStartFlags, flags, startId));
		if (intent == null) {
			return START_STICKY;
		}
		if (mLogPath != "") {
			return START_STICKY;
		}
		mLogPath = getSDPath();
		writeLog("启动后台服务");
		m_IP = intent.getExtras().getString(DBXTIP);
		m_Port = Integer.parseInt(intent.getExtras().getString(DBXTPORT));
		m_SIMNumber = intent.getExtras().getString(DBXTSIMNUMBER);
		m_DiffFormat = intent.getExtras().getString(DBXTDIFFFORMAT);
		try {
			m_SerialBaudrate = Integer.parseInt(intent.getExtras().getString(
					DBXTSERIALBAUDRATE));
		} catch (Exception e) {
			m_SerialBaudrate = 115200;
		}

		String connectinfo = String.format(
				"连接信息：IP：%s,端口：%s,sim卡号：%s,波特率：%s,差分格式：%s", m_IP, m_Port,
				m_SIMNumber, m_SerialBaudrate, m_DiffFormat);
		writeLog(connectinfo);

		new Thread(new Runnable() {

			@Override
			public void run() {
				connectServer();
			}
		}).start();

		return START_STICKY;
	}

	private void connectServer() {
		try {
			// while (true) {
			// writeLog("循环连接测试中...");
			// Thread.sleep(1000);
			// if (m_IsConnected) {
			// break;
			// }
			// }
			if (mIsConnecting || m_IsConnected) {
				return;
			}
			writeLog("连接服务器");
			mIsConnecting = true;
			mBDXTSocket = new Socket(m_IP, m_Port);
			mBDXTSocket.setSoTimeout(10000);
			m_BDXTInputStream = mBDXTSocket.getInputStream();
			m_BDXTOutputStream = mBDXTSocket.getOutputStream();

			writeLog("服务器连接成功");
			writeLog("sim卡登陆开始");

			m_BDXTOutputStream.write(getLoginData());
			m_BDXTOutputStream.flush();

			int rc = 0;
			int sum = 0;
			// int timeout = 3;

			while (true) {
				Thread.sleep(300);
				sum = m_BDXTInputStream.available();
				if (sum > 0) {
					byte[] buff = new byte[sum];
					rc = m_BDXTInputStream.read(buff, 0, buff.length);
					if (rc > 0) {
						if (analyzeLoginData(buff)) {
							m_IsConnected = true;
							writeLog("SIM卡登陆成功");
							// 启动差分数据输出线程
							new Thread((new Runnable() {
								public void run() {
									sendNetDataToSerial();
								}
							})).start();
							// 启动发送心跳线程
							new Thread((new Runnable() {

								@Override
								public void run() {
									sendHeartbeat();
								}
							})).start();
							return;
						} else {
							m_IsConnected = false;
							writeLog("SIM卡登陆失败");
							mBDXTSocket.close();
							return;
						}
					}
				} else {
					writeLog("服务器无响应，重新登陆！");
					m_BDXTOutputStream.write(getLoginData());
					m_BDXTOutputStream.flush();
					Thread.sleep(2000);
				}
			}
		} catch (Exception e) {
			mIsConnecting = false;
			m_IsConnected = false;
			writeLog("连接服务器失败");
			writeException(e);
			new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {
					}
					connectServer();
				}
			}).start();
		}

	}

	private void sendNetDataToSerial() {
		try {
			writeLog("接收差分数据...");
			writeLog(String.format("打开串口，串口号：%s，波特率：%s", SERIAL_COM_1,
					m_SerialBaudrate));
			m_SerialCom1 = SerialPortManager.getSerialPort(SERIAL_COM_1,
					m_SerialBaudrate);
			writeLog("串口打开成功！");
			writeLog("开始获取串口输出流。");
			m_OutputCom1 = m_SerialCom1.getOutputStream();
			writeLog("获取串口输出流对象成功！");
			byte[] buff = new byte[8192];
			int time = 0;
			while (m_IsConnected) {
				int rc = 0;
				buff = new byte[8192];
				try {
					// TODO 读取网络差分数据
					while (mIsRorWing) {
						Thread.sleep(300);
					}
					mIsRorWing = true;
					writeLog("开始从网络读取差分数据");
					if (m_BDXTInputStream == null) {
						writeLog("m_BDXTInputStream == null");
						if (mBDXTSocket.getInputStream() == null) {
							writeLog("mBDXTSocket.getInputStream() == null");
						}
						m_BDXTInputStream = mBDXTSocket.getInputStream();
					}

					rc = m_BDXTInputStream.read(buff);
				} catch (Exception e) {
					writeException(e);
				} finally {
					mIsRorWing = false;
				}

				if (rc > 0) {
					writeLog("读取差分数据成功！");
					analyzeData(buff, rc);
					time = 0;
				} else {
					if (time++ > 4) {
						try {
							mBDXTSocket.sendUrgentData(0xFF);
							time = 0;
						} catch (Exception e) {
							writeLog("网络异常！");
							onDestroy();
							connectServer();
							break;
						}
					}
				}
				Thread.sleep(300);
			}
		} catch (Exception e) {
			writeLog("接收差分数据出错！");
			writeException(e);
			onDestroy();
			connectServer();
		}

	}

	@Override
	public void onDestroy() {
		try {
			mIsConnecting = false;
			m_IsConnected = false;
			if (mBDXTSocket != null) {
				m_BDXTInputStream.close();
				m_BDXTOutputStream.close();
				mBDXTSocket.close();
				mBDXTSocket = null;
			}

			if (m_SerialCom1.isConnected()) {
				m_OutputCom1.close();
				m_SerialCom1.close();
				m_SerialCom1 = null;
			}
			releaseLog();
			mLocationManager.removeUpdates(this);
			mLocationManager = null;
		} catch (Exception e) {

		} finally {
			writeLog("释放资源！");
		}

	}

	FileWriter fileWrite;
	BufferedWriter log_bw;

	/**
	 * 记录日志
	 *
	 * @param message
	 */
	private synchronized void writeLog(String message) {

		try {
			Date nowdate = new Date();
			String logpath = String.format("%s/%s.txt", mLogPath,
					mDateFormat.format(nowdate));
			String nowtime = mTimeFormat.format(nowdate);
			if (log_bw == null) {
				fileWrite = new FileWriter(logpath, true);
				log_bw = new BufferedWriter(fileWrite);
			}
			log_bw.write(String.format("\r\n%s:%s", nowtime, message));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void releaseLog() {
		try {
			if (log_bw != null) {
				log_bw.close();
				fileWrite.close();
				log_bw = null;
				fileWrite = null;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void writeException(Exception e) {
		writeLog(e.getMessage());
		for (StackTraceElement ste : e.getStackTrace()) {
			writeLog(ste.toString());
		}
	}

	public String getSDPath() {
		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
		if (sdCardExist) {
			sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
			sdDir = new File(String.format("%s/bdxtLog",
					sdDir.getAbsoluteFile()));
			if (!sdDir.exists()) {
				sdDir.mkdir();
			}
		}
		return sdDir.toString();

	}

	private byte[] getLoginData() {
		byte commandID = (byte) 0x80;
		short len = 14;// 登陆信息为定长信息14字节
		byte[] simdata = m_SIMNumber.getBytes();
		byte[] logindata = new byte[len];

		System.arraycopy(BDXTHEADER, 0, logindata, 0, 2);
		logindata[2] = commandID;
		System.arraycopy(simdata, 0, logindata, 3, 11);

		writeLog(String.format("登陆数据：%s", bytesToHexString(logindata)));

		return logindata;
	}

	private byte[] getSIMbytes() {
		byte[] simdata = new byte[11];
		char[] tmpsims = m_SIMNumber.toCharArray();
		simdata = getBytes(tmpsims);
		return simdata;
	}

	private void analyzeData(byte[] data, int len) {
		for (int i = 0; i < len; i++) {
			if (data[i] == (byte) 0xFE && data[i + 1] == (byte) 0xEF) {
				if (data[i + 2] == 0x0b) {
					if (m_OutputCom1 != null) {
						try {
							m_OutputCom1.write(data, i + 3, len - (i + 4));
							m_OutputCom1.flush();
							writeLog("输出差分数据成功！");
						} catch (IOException e) {
							writeLog("输出差分数据出错！");
							writeException(e);
						}
					}
				}
			}
		}
	}

	/**
	 * 解析移动站登陆应答信息
	 *
	 * @param data
	 * @return
	 */
	private boolean analyzeLoginData(byte[] data) {
		writeLog(String.format("登陆应答原始数据：%s", bytesToHexString(data)));
		boolean loginresult = false;
		for (int i = 0, len = data.length; i < len; i++) {
			if (i + 3 < len) {
				if (data[i] == (byte) 0xFE && data[i + 1] == (byte) 0xEF) {
					if (data[i + 2] == 0x01) {
						if (data[i + 3] == 0x01) {
							loginresult = true;
						}
					}
				}
			}
		}
		return loginresult;
	}

	private byte[] getBytes(short s, boolean asc) {
		byte[] buf = new byte[2];
		if (asc)
			for (int i = buf.length - 1; i >= 0; i--) {
				buf[i] = (byte) (s & 0x00ff);
				s >>= 8;
			}
		else
			for (int i = 0; i < buf.length; i++) {
				buf[i] = (byte) (s & 0x00ff);
				s >>= 8;
			}
		return buf;
	}

	private byte[] getBytes(char[] chars) {
		Charset cs = Charset.forName("UTF-8");
		CharBuffer cb = CharBuffer.allocate(chars.length);
		cb.put(chars);
		cb.flip();
		ByteBuffer bb = cs.encode(cb);
		return bb.array();
	}

	/**
	 * 将int转为byte数组
	 *
	 * @param i
	 * @param asc
	 * @return
	 */
	private byte[] getBytes(int i, boolean asc) {
		byte[] b = new byte[4];
		if (asc) {
			b[0] = (byte) ((i >> 24) & 0xff);
			b[1] = (byte) ((i >> 16) & 0xff);
			b[2] = (byte) ((i >> 8) & 0xff);
			b[3] = (byte) (i & 0xff);
		} else {
			b[3] = (byte) ((i >> 24) & 0xff);
			b[2] = (byte) ((i >> 16) & 0xff);
			b[1] = (byte) ((i >> 8) & 0xff);
			b[0] = (byte) (i & 0xff);
		}
		return b;
	}

	/**
	 * 二进制转到字符串
	 *
	 * @param src
	 * @return
	 */
	private String bytesToHexString(byte[] src) {
		int size = src.length;
		StringBuffer sb = new StringBuffer();
		if (src == null || size <= 0) {
			return null;
		}
		for (int i = 0; i < size; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				sb.append(0);
			}
			sb.append(hv);
			sb.append(" ");
		}
		return sb.toString();
	}

	@Override
	public void onLocationChanged(Location location) {
		if (location != null) {
			short time_ms = (short) (new Date(location.getTime()).getSeconds() * 1000);
			m_time_ms_bytes = getBytes(time_ms, false);

			double lon = location.getLongitude();
			int int4lon = (int) (lon * 1000000);
			m_byt4lon = getBytes(int4lon, false);

			double lat = location.getLatitude();
			int int4lat = (int) (lat * 1000000);
			m_byt4lat = getBytes(int4lat, false);

			int speed = (int) (location.getSpeed() * 10);
			m_bytspeed = (byte) speed;

			int direction = (int) location.getBearing() / 2;// 方向单位为2
			m_bytdirection = (byte) direction;

			// writeLog(String.format("输出当前GPS信息：经度:%s,纬度:%s,速度:%s,方向:%s",
			// int4lon, int4lat, speed, direction));
		}
	}

	/**
	 * 往基准站发送心跳信息
	 */
	private void sendHeartbeat() {
		while (m_IsConnected) {
			// 组建心跳信息
			byte commandID = (byte) 0x82;
			short len = 19;// 移动站位置信息为定长信息19字节

			ArrayList<Byte> locationdata = new ArrayList<Byte>();
			locationdata.add(BDXTHEADER[0]);
			locationdata.add(BDXTHEADER[1]);// 添加消息头 2字节
			locationdata.add(commandID);// 添加消息ID 1字节

			locationdata.add(m_time_ms_bytes[0]);
			locationdata.add(m_time_ms_bytes[1]);// 添加时间信息“分内毫秒”，2字节

			for (byte blon : m_byt4lon) {
				locationdata.add(blon);// 添加经度信息，4字节
			}

			for (byte blat : m_byt4lat) {
				locationdata.add(blat);// 添加纬度信息，4字节
			}

			locationdata.add(m_bytspeed);// 添加速度信息，1字节

			locationdata.add(m_bytdirection);// 添加方向信息，1字节

			locationdata.add((byte) 0x02);// GPS接收机状态：0-不定位，1-单点定位，2-差分定位。1字节

			locationdata.add((byte) 0x00);
			locationdata.add((byte) 0x00);
			locationdata.add((byte) 0x00);// 预留3字节

			byte[] byt_datas = new byte[len];
			for (int i = 0; i < len; i++) {
				byt_datas[i] = locationdata.get(i);
			}

			// 往基准站发送心跳信息
			if (m_BDXTOutputStream != null) {
				try {
					// TODO 发送心跳数据
					while (mIsRorWing) {
						Thread.sleep(300);
					}
					mIsRorWing = true;
					writeLog(String.format("开始发送心跳数据：%s",
							bytesToHexString(byt_datas)));
					m_BDXTOutputStream.write(byt_datas);
					writeLog("发送心跳数据成功！");
				} catch (Exception e) {
					writeLog("发送心跳数据失败！");
					writeException(e);
				} finally {
					mIsRorWing = false;
				}
			}
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {

			}
		}
	}

	@Override
	public boolean onUnbind(Intent intent) {
		releaseLog();
		return super.onUnbind(intent);
	}

	@Override
	public void onProviderDisabled(String provider) {

	}

	@Override
	public void onProviderEnabled(String provider) {

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {

	}
}
