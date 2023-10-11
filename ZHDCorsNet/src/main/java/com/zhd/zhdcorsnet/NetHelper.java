package com.zhd.zhdcorsnet;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.R.integer;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Environment;

public class NetHelper {
	public static Date UTC_DATE = new Date();

	public NetHelper() {
	}

	/**
	 * 检查网络是否通畅<br>
	 * 需要权限 android.permission.ACCESS_NETWORK_STATE
	 *
	 * @param mContext
	 * @return
	 */
	public boolean CheckNet(Context mContext) {
		String status = FileHelper.read3gStatus("errorcode");

		if (!status.equals("")) {
			String sta = "";

			if (status.contains("\n")) {
				sta = status.replace("\n", "");
				sta = sta.trim();
			}

			if (sta.equals(""))
				return true;
			else {
				if (Integer.parseInt(sta) == 9)
					return true;
				else {
					return false;
				}
			}
		}

		return true;
	}

	public static boolean CheckNet() {

		// Process proc = null;
		//
		String ip = "202.108.22.5";

		boolean a = false;
		Socket m_CorsSocket = new Socket();
		try {
			// proc = Runtime.getRuntime().exec("ping 202.108.22.5 ");
			// BufferedReader buf = new BufferedReader(new InputStreamReader(
			// proc.getInputStream()));
			// String line;

			if (m_CorsSocket.isConnected() == false) {
				SocketAddress remoteAddr = new InetSocketAddress(ip, 80);
				m_CorsSocket.connect(remoteAddr, 3000);
				InputStream m_CorsInputStream = m_CorsSocket.getInputStream();
			}
		} catch (Exception e) {
			if (!m_CorsSocket.isClosed()) {
				try {
					m_CorsSocket.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			m_CorsSocket = null;

			return false;
		} finally {
			if (m_CorsSocket != null) {
				if (!m_CorsSocket.isClosed()) {
					try {
						m_CorsSocket.close();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				m_CorsSocket = null;
			}
		}

		return true;
	}

	/**
	 * 获取源节点
	 */
	public static List<SourceNode> GetSourceNode(String ip, String port) {
		try {
			List<SourceNode> nodeList = null;
			Socket getNodeSocket = null;
			OutputStream m_OutputStream;
			InputStream m_InputStream;
			StringBuilder responseData = new StringBuilder();
			// writeLog(getDiffSetLog(), String.format("ip:%s, port:%s", ip,
			// port));
			try {

				// getNodeSocket = new Socket(ip, Integer.parseInt(port));
				// getNodeSocket.setSoTimeout(4000); // !MUST set timeout for

				if (getNodeSocket == null)
					getNodeSocket = new Socket();

				SocketAddress remoteAddr = new InetSocketAddress(ip,
						Integer.parseInt(port));
				getNodeSocket.connect(remoteAddr, 3000);

			} catch (Exception e) {
				// TODO: handle exception
				return null;
			}
			// read
			m_OutputStream = getNodeSocket.getOutputStream();
			m_InputStream = getNodeSocket.getInputStream();

			byte[] bytData = null;
			// 重复读取次数
			int timeOut = 3;
			int iSum = 0;
			String msg = "GET / HTTP/1.1\r\n";
			msg += "User-Agent: NTRIP GPS\r\n";
			msg += "Accept: */*\r\nConnection: close\r\n";
			msg += "\r\n";
			m_OutputStream.write(msg.getBytes());
			m_OutputStream.flush();
			while (true) {
				Thread.sleep(1000);
				if (timeOut == 0)
					return null;
				iSum = m_InputStream.available();
				if (iSum > 0) {
					bytData = new byte[iSum];
					int num = m_InputStream.read(bytData);
					if (num <= 0)
						return null;
					responseData.append(new String(bytData, 0, num));
					writeLog(getDiffSetLog(), responseData.toString());

					// ////////////////////////////////
					// 匹配源列表,获得源列表项
					// /////////////////////////////
					Pattern head = Pattern.compile("SOURCETABLE");
					Pattern end = Pattern.compile("ENDSOURCETABLE");
					Matcher hm = head.matcher(responseData.toString());
					Matcher em = end.matcher(responseData.toString());
					if (hm.find() && em.find()) {
						nodeList = new ArrayList<SourceNode>();
						String Use = responseData.toString().substring(
								hm.start(), em.start() - hm.start());
						Pattern str = Pattern.compile("STR;.*\\r\\n");
						Matcher nodeMatcher = str.matcher(Use);

						while (nodeMatcher.find()) {
							String source = nodeMatcher.group();
							String[] szArray = source.split(";");
							if (szArray[11].equals("0")
									&& szArray[11].equals("1"))
								continue;
							SourceNode node = getNode(szArray);
							nodeList.add(node);
						}
						m_InputStream.close();
						m_OutputStream.close();
						getNodeSocket.close();

						return nodeList.size() == 0 ? null : nodeList;
					}
				}
				timeOut--;
			}
		} catch (Exception e) {
			// writeLog(getDiffSetLog(), e.toString());
		}
		return null;
	}

	/**
	 * 获取源节点对象
	 *
	 * @param nodeInfo
	 * @return
	 */
	private static SourceNode getNode(String[] nodeInfo) {
		if (nodeInfo.length > 18) {
			SourceNode node = new SourceNode();
			node.Mountpoint = nodeInfo[1];
			node.Identifier = nodeInfo[2];
			node.Format = nodeInfo[3];
			node.RTCMDetail = nodeInfo[4];
			node.Carrier = nodeInfo[5]; // 0:No(e.g DGPS);1:Yes L1(e.g
			// RTK);2:Yes L1+L2(e.g RTK)
			node.GNSS = nodeInfo[6];
			node.NetWork = nodeInfo[7];
			node.Country = nodeInfo[8]; // 3个字符
			node.Latitude = nodeInfo[9]; // two digits after decimal point
			node.Longitude = nodeInfo[10]; // 小数点后两位
			node.NMEASent = nodeInfo[11]; // 0:User Must Not Send Nmea to
			// caster;1:User must Send nmea To
			// Caster
			node.Solution = nodeInfo[12]; // 0:User Get Data from single
			// referenced station;
			// 1:Data generated from neworked referenced stations
			node.Generator = nodeInfo[13];
			node.Compression = nodeInfo[14];
			node.Authentication = nodeInfo[15]; // N:None;B:Basic;D:Digest
			node.Fee = nodeInfo[16]; // N:No User Fee;Y:Usage is charged
			node.BitRate = nodeInfo[17]; // bps
			return node;
		}
		return null;
	}

	public static String getSDPath() {
		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
		if (sdCardExist) {
			sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
		}
		return sdDir.toString();
	}

	/**
	 * byte数组转换成16进制字符数组
	 *
	 * @param src
	 * @return
	 */
	public static String[] bytesToHexStrings(byte[] src) {
		if (src == null || src.length <= 0) {
			return null;
		}
		String[] str = new String[src.length];
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				str[i] = "0";
			}
			str[i] = hv;
		}
		return str;
	}

	// private string ConstructGGA(ZHD.CommunicationLibrary.BLH Blh84, int Hour,
	// int Minute, int Second, ZHD.CommunicationLibrary.GpsPositionType
	// PositionType, int EphemerisCount, double HorizontalRms)
	// {
	// StringBuilder sbGGA = new StringBuilder("$GPGGA,");
	// string ggaTime = string.Empty;
	// //if (Hour < 8)
	// //{
	// // ggaTime = (Hour + 16).ToString("00") +
	// // Minute.ToString("00") +
	// // Second.ToString("00") + ".00,";
	// //}
	// //else
	// //{
	// // ggaTime = (Hour - 8).ToString("00") +
	// // Minute.ToString("00") +
	// // Second.ToString("00") + ".00,";
	// //}
	// ggaTime = Hour.ToString("00") +
	// Minute.ToString("00") +
	// Second.ToString("00") + ".00,";
	// sbGGA.Append(ggaTime);
	// changer.Radian = Blh84.B;
	// string tempB = changer.ToString(ZHD.Help.StringType.DD_MM, true, true);
	// sbGGA.Append(tempB.Substring(0, tempB.Length - 1) + "," +
	// tempB.Substring(tempB.Length - 1) + ",");
	// changer.Radian = Blh84.L;
	// string tempL = changer.ToString(ZHD.Help.StringType.DD_MM, false, true);
	// sbGGA.Append(tempL.Substring(0, tempL.Length - 1) + "," +
	// tempL.Substring(tempL.Length - 1) + ",");
	// switch (PositionType)
	// {
	// case ZHD.CommunicationLibrary.GpsPositionType.FixPos:
	// sbGGA.Append("1");
	// break;
	// case ZHD.CommunicationLibrary.GpsPositionType.None:
	// sbGGA.Append("0");
	// break;
	// case ZHD.CommunicationLibrary.GpsPositionType.RTD:
	// sbGGA.Append("2");
	// break;
	// case ZHD.CommunicationLibrary.GpsPositionType.RTKFloat:
	// sbGGA.Append("2");
	// break;
	// case ZHD.CommunicationLibrary.GpsPositionType.RTKInt:
	// sbGGA.Append("2");
	// break;
	// case ZHD.CommunicationLibrary.GpsPositionType.Single:
	// sbGGA.Append("1");
	// break;
	// case ZHD.CommunicationLibrary.GpsPositionType.Unknow:
	// sbGGA.Append("0");
	// break;
	// case ZHD.CommunicationLibrary.GpsPositionType.WAAS:
	// sbGGA.Append("2");
	// break;
	// default:
	// sbGGA.Append("1");
	// break;
	// }
	//
	// sbGGA.Append("," +
	// EphemerisCount.ToString("00") + "," +
	// HorizontalRms.ToString("0.00") + "," +
	// Blh84.H.ToString("0.00") + ",M,,,,");
	//
	// string checksum = CalcNMEACheckSum(sbGGA.ToString());
	// sbGGA.Append(checksum);
	// return sbGGA.ToString();
	// }

	public static String getGPGGAFromGGA(String GGA) {
		StringBuilder sb = new StringBuilder();
		if (GGA != null && GGA.startsWith("$G") && GGA.contains("*")) {
			String[] tmpGGA = GGA.split("[,]");// ，+、*、|、\等符号为正则表达示特殊字符,需要加[]、或是\\
			if (tmpGGA != null && tmpGGA.length > 14) {// 保证包括数据头在内有15位数据
				sb.append("$GPGGA");
				for (int i = 0; i < 14; i++) {// 标准GPGGA数据除去校验位，只有14位
					sb.append(",");
					sb.append(tmpGGA[i + 1]);// 去掉头
				}
				int endIndex = sb.indexOf("*");// 结束标志符的位置
				if (endIndex != -1) {
					sb.delete(endIndex, sb.length() - 1);
				}
				sb.append(calcNMEACheckSum(sb.toString()));
				sb.append("\r\n");
			}
		}
		return sb.toString();
	}

	private static String calcNMEACheckSum(String nmea) {
		int checkSum = 0;
		char[] chr_nmea = nmea.toCharArray();
		int count = chr_nmea.length;
		for (int i = 0; i < count; i++) {
			if (chr_nmea[i] != '$' && chr_nmea[i] != '\r'
					&& chr_nmea[i] != '\n' && chr_nmea[i] != '*') {
				checkSum = checkSum ^ ((int) chr_nmea[i]);
			}
		}
		return "*" + Integer.toHexString(checkSum);
	}

	/**
	 * 记录日志
	 *
	 * @param message
	 */
	public static void writeLog(String logPath, String message) {

		try {
			String nowtime = mTimeFormat.format(UTC_DATE);
			synchronized (nowtime) {
				BufferedWriter bw = new BufferedWriter(new FileWriter(logPath,
						true));
				bw.write(String.format("\r\n%s:%s", nowtime, message));
				bw.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String getDiffSetLog() {
		Date nowdate = new Date();
		File logDir = new File(getSDPath() + "/ZNZCORS");
		if (!logDir.exists()) {
			logDir.mkdir();
		}
		String logpath = String.format("%s/%s.txt", logDir.toString(),
				mDateFormat.format(nowdate));
		return logpath;
	}

	public static SimpleDateFormat mDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd");
	public static SimpleDateFormat mTimeFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
}
