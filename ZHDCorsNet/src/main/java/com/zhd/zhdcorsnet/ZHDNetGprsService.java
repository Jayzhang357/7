package com.zhd.zhdcorsnet;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.security.InvalidParameterException;
import java.sql.Ref;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.GpsStatus.NmeaListener;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android_serialport_api.SerialPort;

import com.zhd.bd970.manage.BD970Manager;
import com.zhd.bd970.manage.interfaces.ReceiveGGAMessageListner;
import com.zhd.gnssmanager.DifferDataReceive;
import com.zhd.gnssmanager.GnssDataReceive;
import com.zhd.gnssmanager.Helper;
import com.zhd.serialportmanage.*;

public class ZHDNetGprsService extends Service implements
		ReceiveGGAMessageListner {
	private static String m_GGA;
	private static String m_Ip;
	private static int m_Port;
	private static String m_UserName;
	private static String m_UserGroup;
	private static String m_WorkGroup;
	private static boolean m_IsReconnect;
	private static DatagramSocket m_ZhdNetSocket;
	DatagramPacket datagramPacket;
	DatagramPacket receivePacket;
	private boolean m_IsConnected = false;
	private Thread m_SendNetDataThread;
	private SerialPortManager mDiffSerialPortManager;
	public static final int BAURATE = 19200;
	// private OutputStream m_OutputCom1;
	// private LocationManager locationManager = null;
	private SerialPortManager mSerialPortManager = null;
	private GnssDataReceive mGnssDataReceive = null;
	private BD970Manager mBd970Manager = null;
	private DifferDataReceive mDifferDataReceive = null;

	private String mLogPath = "";

	public final String GGA_MSG = "$GPGGA,064247.00,2259.01424235,N,11322.05875381,E,2,20,0.6,39.346,M,-6.251,M,41.0,0137*5D\r\n";


	public static final String SERIAL_COM_2 = "/dev/ttymxc1";

	public static final String ACTION_SENDRESULT = "sendresult";


	public static final int FLAG_NETRESULT = 0x108;

	public static final int FLAG_DATARECEIVE = 0x109;

	public static final String RESULT_NETRESULT = "NetResult";

	public static final String RESULT_SOLTYPE = "Soltype";

	public static final String RESULT_DIFFAGE = "DiffAge";

	Intent intent = new Intent(ACTION_SENDRESULT);
	StateBroadcast stateBroadcast;


	@Override
	public void onCreate() {
		// locationManager = (LocationManager)
		// getSystemService(Context.LOCATION_SERVICE);
		// locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
		// 1000, 0, mLocationListener);
		// locationManager.addNmeaListener(this);
		stateBroadcast = new StateBroadcast(getApplicationContext());

		// mSerialPortManager = mGnssDataReceive.getCom3GpsSerialPort();

		mGnssDataReceive.getParser().setReceiveGGAMessageListener(this);

		stateBroadcast = new StateBroadcast(getApplicationContext());
	}

	public void ConnectServer() {

		getLogPath();

		try {
			if (SendUDP()) {
				m_IsConnected = true;
				if (m_SendNetDataThread == null) {
					m_SendNetDataThread = new Thread() {
						@Override
						public void run() {
							sendNetData();
						}
					};
					m_SendNetDataThread.start();
				}
				intent.putExtra(RESULT_NETRESULT, NetResult.Success);
				intent.putExtra(CorsGprsService.MY_FLAGS_FOR_CORS,
						FLAG_NETRESULT);
				sendBroadcast(intent);
			} else {
				m_IsConnected = false;
				if (m_IsReconnect) {
					intent.putExtra(RESULT_NETRESULT,
							NetResult.Fail_And_Reconnection);
				} else {
					intent.putExtra(RESULT_NETRESULT, NetResult.Fail);
				}
				intent.putExtra(CorsGprsService.MY_FLAGS_FOR_CORS,
						FLAG_NETRESULT);
				sendBroadcast(intent);
				m_ZhdNetSocket.close();
				m_ZhdNetSocket = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onDestroy() {
		CutServer();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		try {

			if (intent == null) {
				return START_STICKY;
			}
			m_Ip = intent.getExtras().getString("ip");
			try {
				m_Port = Integer.parseInt(intent.getExtras().getString("port"));
			} catch (Exception e) {
				m_Port = 0;
			}
			m_UserName = intent.getExtras().getString("username");
			m_UserGroup = intent.getExtras().getString("usergroup");
			m_WorkGroup = intent.getExtras().getString("workgroup");
			m_IsReconnect = intent.getExtras().getBoolean("reconnect");
			m_ZhdNetSocket = new DatagramSocket(m_Port);
			m_ZhdNetSocket.setBroadcast(true);
			m_ZhdNetSocket.setSoTimeout(5000);
			new MyThread().start();
		} catch (Exception e) {

		}
		return START_STICKY;
	}

	public boolean CutServer() {
		stateBroadcast.breakDiff();
		try {
			m_IsConnected = false;

			if (m_ZhdNetSocket != null) {
				m_ZhdNetSocket.close();
				m_ZhdNetSocket = null;
			}
			return true;
		} catch (Exception e) {
		}

		return false;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}


	private boolean SendUDP() {
		try {
			byte[] udpcommend = SendHexCmd("GL", "0", 1,
					Integer.parseInt(m_UserName),
					Integer.parseInt(m_UserGroup),
					Integer.parseInt(m_WorkGroup), m_Port);

			writeToNet(udpcommend);

			int timeout = 3;
			byte[] comData;
			byte[] commandGL = new byte[19];
			boolean GlAnalyzeMark = false;
			int GLCount = 0;
			while (true) {
				Thread.sleep(300);

				comData = readFromNet();

				if (comData != null) {
					for (int i = 0; i < comData.length; i++) {
						if (comData[i] == 36 && comData[i + 1] == 71
								&& comData[i + 2] == 76) {// $GL
							GlAnalyzeMark = true;
							GLCount = 0;
						}
						if (GlAnalyzeMark == true) {
							commandGL[GLCount++] = comData[i];

							if (GLCount >= 18) {
								int port17 = commandGL[14] & 0xFF;
								int port18 = commandGL[15] & 0xFF;

								m_Port = port18 + port17 * 256;
								return true;
							}
						}
					}
				}
				timeout--;
				if (timeout == 0) {
					return false;
				} else {
					writeToNet(udpcommend);
				}
			}
		} catch (Exception e) {

			//NetHelper.writeLog(mLogPath, "SendUDP:" + e.toString());
		}
		return false;
	}


	private void SendGM(String data) {
		byte[] udpcommend = SendHexCmd("GM", data, data.getBytes().length,
				Integer.parseInt(m_UserName), Integer.parseInt(m_UserGroup),
				Integer.parseInt(m_WorkGroup), m_Port);
		writeToNet(udpcommend);
	}

	private boolean writeToNet(byte[] data) {
		stateBroadcast.receiveDiff();

		boolean result = false;
		try {
			if (m_ZhdNetSocket == null) {
				m_ZhdNetSocket = new DatagramSocket(m_Port);
				m_ZhdNetSocket.setBroadcast(true);
				m_ZhdNetSocket.setSoTimeout(5000);
			}

			byte[] m_GprsData = new byte[1024];
			InetAddress host = InetAddress.getByName(m_Ip);
			datagramPacket = new DatagramPacket(m_GprsData, m_GprsData.length,
					host, m_Port);
			datagramPacket.setData(data);
			datagramPacket.setLength(data.length);
			m_ZhdNetSocket.send(datagramPacket);
			result = true;
		} catch (Exception e) {

			NetHelper.writeLog(mLogPath, "writeToNet:" + e.toString());
		}
		return result;
	}

	private byte[] readFromNet() {
		byte[] data = null;
		try {
			if (m_ZhdNetSocket == null) {

				m_ZhdNetSocket = new DatagramSocket();

			}
			byte[] receiveData = new byte[2048];
			InetAddress host = InetAddress.getByName(m_Ip);
			receivePacket = new DatagramPacket(receiveData, receiveData.length,
					host, m_Port);
			m_ZhdNetSocket.receive(receivePacket);

			int num = receivePacket.getLength();
			data = new byte[num];
			System.arraycopy(receiveData, 0, data, 0, num);
		} catch (Exception e) {

			NetHelper.writeLog(mLogPath, "readFromNet:" + e.toString());
		}
		return data;
	}

	private void sendNetData() {
		try {

			/*if (mDifferDataReceive == null)
				mDifferDataReceive = DifferDataReceive.getInstance();
*/
			int times = 0;


			SendGM(GGA_MSG);

			while (m_IsConnected) {
				byte[] buff = readFromNet();

				if (buff != null) {
					if (decodePackage(buff) != null) {
						mGnssDataReceive.sendByte(decodePackage(buff));

						Log.i("diffdata", bytesToHexString(buff));

						intent.putExtra(CorsGprsService.MY_FLAGS_FOR_CORS,
								FLAG_DATARECEIVE);
						sendBroadcast(intent);
					}

					times = 0;
				} else {
					times++;
					if (times > 15) {
						intent.putExtra(CorsGprsService.MY_FLAGS_FOR_CORS,
								CorsGprsService.FLAG_NETERROR);
						sendBroadcast(intent);
						break;
					}
				}

				Thread.sleep(400);
			}

		} catch (Exception e) {

			NetHelper.writeLog(mLogPath, "sendNetData:" + e.toString());
		}
	}


	private byte[] mZhdDataBuff = null;

	private byte[] decodePackage(byte[] packageBuf) {

		try {

			int buffLength = packageBuf.length;

			if (buffLength > 17) {

				if (packageBuf[0] == 36 && packageBuf[1] == 71) { //

					int pkgLength = buffLength - 16;//
					mZhdDataBuff = new byte[pkgLength];

					System.arraycopy(packageBuf, 16, mZhdDataBuff, 0, pkgLength);

					return mZhdDataBuff;

				} else {
					return packageBuf;
				}

			}
		} catch (Exception e) {
			// TODO: handle exception

			return null;
		}

		return null;
	}


	private byte[] SendHexCmd(String cmdType, String cmdData, int cmdLen,
							  int N, int U, int G, int port) {

		int k = 0;

		byte[] message;
		if (cmdLen >= 0) {
			message = new byte[16 + cmdLen];
		} else {
			message = new byte[15];
		}

		message[k++] = (byte) ('$');

		// message[1] = Convert.ToByte(cmdType[0]);
		// message[2] = Convert.ToByte(cmdType[1]);
		message[k++] = (byte) cmdType.charAt(0);
		message[k++] = (byte) cmdType.charAt(1);

		if (N != 0) {

			int nID = N;
			byte[] bID = intToBytes(nID);// String.valueOf(nID).getBytes();
			message[k++] = bID[3];
			message[k++] = bID[2];
			message[k++] = bID[1];
			message[k++] = bID[0];
		}

		if (U == 0)
			U = 1000000;

		if (U != 0) {
			int nUse = U / 1000;
			byte[] bUse = intToBytes(nUse);// String.valueOf(nUse).getBytes();
			message[k++] = (byte) (nUse >> 8);
			message[k++] = (byte) nUse;
			// Array.Copy(bUse, 0, message, 8, 1);
			// Array.Copy(bUse, 1, message, 7, 1);
			message[k++] = (byte) (U % 1000);

			message[k++] = (byte) (G);
		}


		message[k++] = (byte) (cmdLen + 2);

		byte XCmd = CheckXOR(message, 0, 12);

		message[k++] = XCmd;

		k++;

		if (cmdLen >= 0) {
			// Data
			byte[] data = new byte[cmdLen + 2];
			// data.Initialize();

			byte[] by = intToBytes(port);// String.valueOf(port).getBytes();
			data[0] = by[1];
			data[1] = by[0];


			message[k++] = data[0];
			message[k++] = data[1];

			// k=16
			message[k - 3] = CheckXOR(message, k - 2, 2);

			if (cmdType == "GL") {
				byte[] b = intToBytes(Integer.parseInt(cmdData));
				message[16] = b[0];
				message[k - 3] = CheckXOR(message, k - 2, 3);

				String aString = bytesToHexString(message);
				String a;

			} else if (cmdType == "GH") {
				// data = BitConverter.GetBytes(port);
			} else if (cmdType == "GM") {
				message[k - 5] = (byte) (2 + cmdLen);
				message[k - 4] = CheckXOR(message, k - 16, 12);

				byte[] cmdDataByte = cmdData.getBytes();
				message[k - 3] = (byte) (message[k - 3] ^ CheckXOR(cmdDataByte,
						0, cmdLen));

				for (int i = 0; i < cmdLen; i++, k++) {
					message[k] = cmdDataByte[i];
				}
			}
		} else {
			message[13] = (byte) 13;
			message[14] = (byte) 10;
		}
		return message;
	}


	private byte CheckXOR(byte[] data, int nstart, int Len) {
		byte checkReturn = 0x00;
		for (int i = nstart; i < nstart + Len; i++)
			checkReturn ^= (byte) data[i];
		return checkReturn;
	}


	private byte[] intToBytes(int n) {
		byte[] b = new byte[4];
		for (int i = 0; i < 4; i++) {
			b[3 - i] = (byte) (n << (i * 8) >> 24);// (n >> (24 - i * 8));
		}
		return b;
	}


	private int byteToInt(byte[] b) {
		return (((int) b[0]) << 24) + (((int) b[1]) << 16)
				+ (((int) b[2]) << 8) + b[3];
	}

	private class MyThread extends Thread {

		@Override
		public void run() {
			try {
				ConnectServer();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private int mTimes = 0;

	@Override
	public void TellReceiveGGAMessage(String message) {

		// TODO Auto-generated method stub
		mTimes++;
		if (mTimes != 1 && (mTimes % 3 != 0))
			return;

		String tmpGPGGA = message;
		if (tmpGPGGA.contains("GGA") && !tmpGPGGA.startsWith("$GPGGA")) {
			tmpGPGGA = NetHelper.getGPGGAFromGGA(tmpGPGGA);
		}
		if (tmpGPGGA.contains("$GPGGA") && tmpGPGGA.contains("\r\n")) {
			if (tmpGPGGA.length() > 50) {
				m_GGA = tmpGPGGA;
			}
			if (m_IsConnected) {
				SendGM(m_GGA);
			}
		}

		if (mTimes > 100000)
			mTimes = 0;
	}

	private void getLogPath() {
		if (mLogPath == null || mLogPath.equals("")) {
			mLogPath = NetHelper.getSDPath();
			mLogPath += "/ZNZCORS";
			File logPath = new File(mLogPath);
			if (!logPath.exists()) {
				logPath.mkdirs();
			}

			String dateString = NetHelper.mDateFormat
					.format(NetHelper.UTC_DATE);

			mLogPath += "/ZHDLog_" + dateString + ".txt";
		}
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
}
