package com.zhd.ub280;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;

import android.util.Log;

import com.zhd.bd970.manage.interfaces.ReceiveOemListner;
import com.zhd.messagedescription.RangeMsgDescription;
import com.zhd.parserinterface.*;
import com.zhd.utility.filehelper.LogHelper;
import com.zhd.bd970.manage.interfaces.ReceiveABangleListner;
import com.zhd.bd970.manage.interfaces.ReceiveAGRICAListner;
import com.zhd.bd970.manage.interfaces.ReceiveBESTPOSAListner;
import com.zhd.bd970.manage.interfaces.ReceiveBestvelaListner;
import com.zhd.bd970.manage.interfaces.ReceiveCorrectimuListner;
import com.zhd.bd970.manage.interfaces.ReceiveGGAListner;
import com.zhd.bd970.manage.interfaces.ReceiveGGAMessageListner;
import com.zhd.bd970.manage.interfaces.ReceiveGSAListner;
import com.zhd.bd970.manage.interfaces.ReceiveGSVListner;
import com.zhd.bd970.manage.interfaces.ReceiveECUListner;
import com.zhd.bd970.manage.interfaces.ReceiveInsVersionListner;
import com.zhd.bd970.manage.interfaces.ReceiveMCUVersionListner;
import com.zhd.bd970.manage.interfaces.ReceiveMduVersionListner;
import com.zhd.bd970.manage.interfaces.ReceiveNAKListner;
import com.zhd.bd970.manage.interfaces.ReceiveNodeListner;
import com.zhd.bd970.manage.interfaces.ReceiveOpenedListner;
import com.zhd.bd970.manage.interfaces.ReceivePASHRListner;
import com.zhd.bd970.manage.interfaces.ReceiveQXwzListner;
import com.zhd.bd970.manage.interfaces.ReceiveRangeListner;
import com.zhd.bd970.manage.interfaces.ReceiveReadDeadZoneDateListner;
import com.zhd.bd970.manage.interfaces.ReceiveReadSenListner;
import com.zhd.bd970.manage.interfaces.ReceiveReadZXZYListner;
import com.zhd.bd970.manage.interfaces.ReceiveReadcarListner;
import com.zhd.bd970.manage.interfaces.ReceiveReaduhfListner;
import com.zhd.bd970.manage.interfaces.ReceiveRegListner;
import com.zhd.bd970.manage.interfaces.ReceiveSATVISListener;
import com.zhd.bd970.manage.interfaces.ReceiveSDKListner;
import com.zhd.bd970.manage.interfaces.ReceiveSelfOneListner;
import com.zhd.bd970.manage.interfaces.ReceiveSelfRetListner;
import com.zhd.bd970.manage.interfaces.ReceiveSelfTwoListner;
import com.zhd.bd970.manage.interfaces.ReceiveServerOffListner;
import com.zhd.bd970.manage.interfaces.ReceiveServerOkListner;
import com.zhd.bd970.manage.interfaces.ReceiveTB2VersionListner;
import com.zhd.bd970.manage.interfaces.ReceiveUMVersionListner;
import com.zhd.bd970.manage.interfaces.ReceiveVersionListner;
import com.zhd.bd970.manage.interfaces.ReceiveZC31Listner;
import com.zhd.bd970.manage.interfaces.ReceiveZDAListner;
import com.zhd.bd970.manage.interfaces.ReceiveRMCListner;
import com.zhd.bd970.manage.interfaces.ReceiveTrackStatListner;
import com.zhd.bd970.manage.interfaces.ReceiveTrimble40hListener;
import com.zhd.bd970.manage.interfaces.ReceiveVTGListner;
import com.zhd.bd970.manage.interfaces.ReceiveBaseStationListner;
import com.zhd.bd970.manage.interfaces.ReceiveZcbyDateListner;
import com.zhd.bd970.manage.interfaces.ReceiveZcbyListner;
import com.zhd.bd970.manage.interfaces.ReceivebootListner;
import com.zhd.bd970.manage.interfaces.ReceivereadpidListner;
import com.zhd.gps.manage.models.BestvelaEntity;
import com.zhd.gps.manage.models.GGAEntity;
import com.zhd.gps.manage.models.GSAEntity;
import com.zhd.gps.manage.models.GSVEntity;
import com.zhd.gps.manage.models.RangeEntity;
import com.zhd.gps.manage.models.SATVISEntity;
import com.zhd.gps.manage.models.ZDAEntity;
import com.zhd.gps.manage.models.GpsEnum;
import com.zhd.gps.manage.models.RMCEntity;
import com.zhd.gps.manage.models.SatelliteEntity;
import com.zhd.gps.manage.models.VTGEntity;
import com.zhd.gps.manage.models.GpsEnum.GpsType;
import com.zhd.datamanagetemplate.MessageDescription;
import com.zhd.datamanagetemplate.ReceiveSendManager;
import com.zhd.datamanagetemplate.ReceiveSendWraper;
import com.zhd.commonhelper.*;

public class UB280Manager implements IParser {

	private static ReceiveRangeListner mReceiveRangeListner = null;
	private static ReceiveRMCListner mReceiveRMCListner = null;
	private static HashMap<Integer, RangeEntity> mRangeMap = null;
	private static ReceiveGGAListner mReceiveGGAListner = null;
	private static ReceiveGGAMessageListner mReceiveGGAMessageListner = null;
	private static ReceiveGSVListner mReceiveGSVListner = null;
	private static ReceiveZDAListner mReceieZDAListener = null;
	private static ReceiveVTGListner mReceiveVTGListner = null;
	private static ReceiveGSAListner mReceiveGSAListner = null;
	private static ReceiveSATVISListener mReceiveSATVISListener = null;
	private static ReceiveBestvelaListner mReceiveBestvelaListener = null;
	private static BestvelaEntity mBestvelaEntity = new BestvelaEntity();
	private static GSVEntity mGsvEntity = null;
	private static ZDAEntity mZDAEntity = new ZDAEntity();
	private static RangeEntity mRangeEntity = null;
	private static SatelliteEntity mSATVISSatEntity = null;
	private static SATVISEntity mSATVISEntity = new SATVISEntity();
	private static SatelliteEntity mGsvSatEntity = null;
	private static GSAEntity mGSAEntity = new GSAEntity();
	private static RMCEntity mRMCEntity = new RMCEntity();
	private static Vector<ReceiveGGAListner> mGgaVetor = new Vector<ReceiveGGAListner>();
	private static HashMap<Integer, SatelliteEntity> mGsvMap = null;
	private HashMap<Integer, SatelliteEntity> mSATVISMap = null;
	private static GGAEntity mGgaEntity = new GGAEntity();
	private static VTGEntity mVtgEntity = new VTGEntity();
	private ReceiveSendManager mReceiveSendManager = null;
	private ReceiveSendWraper mReceiveSendWraper = null;
	private int mBeforeParse = 0;
	private int mAfterParse = 0;
	private int mCalfalse = 0;
	private int mCaltrue = 0;
	private Calendar cc = Calendar.getInstance();
	private int motherboardType = 0;// 0是天宝 1是诺瓦泰 2是和芯星通

	/**
	 *
	 * @param communicateMode
	 *            与主板交互的方式，是串口还是蓝牙
	 */
	public UB280Manager(ReceiveSendManager communicateMode) {
		mReceiveSendManager = communicateMode;
		try {

			MessageDescription[] MessageDescriptions = new MessageDescription[18];
			MessageDescriptions[0] = new com.zhd.messagedescription.GGAMessageDescription(
					0, this);
			MessageDescriptions[1] = new com.zhd.messagedescription.VTGMessageDescription(
					1, this);
			MessageDescriptions[2] = new com.zhd.messagedescription.GSVMessageDescription(
					2, this);
			MessageDescriptions[3] = new com.zhd.messagedescription.GLGSVMessageDescription(
					3, this);
			MessageDescriptions[4] = new com.zhd.messagedescription.GPGSVMessageDescription(
					4, this);
			MessageDescriptions[5] = new com.zhd.messagedescription.GNVTGMessageDescription(
					5, this);
			MessageDescriptions[6] = new com.zhd.messagedescription.GBDGSVMessageDescription(
					6, this);
			MessageDescriptions[7] = new com.zhd.messagedescription.Trimble40hMessageDescription(
					7, this);
			MessageDescriptions[8] = new com.zhd.messagedescription.GNGSAMessageDescription(
					8, this);
			MessageDescriptions[9] = new com.zhd.messagedescription.ZDAMessageDescription(
					9, this);
			MessageDescriptions[10] = new com.zhd.messagedescription.GPGSAMessageDescription(
					10, this);
			MessageDescriptions[11] = new com.zhd.messagedescription.Trimble35hMessageDescription(
					11, this);
			MessageDescriptions[12] = new com.zhd.messagedescription.GNGGAMessageDescription(
					12, this);
			MessageDescriptions[13] = new com.zhd.messagedescription.GNZDAMessageDescription(
					13, this);
			MessageDescriptions[14] = new com.zhd.messagedescription.PTNLMessageDescription(
					14, this);
			MessageDescriptions[15] = new com.zhd.messagedescription.ZcbyMessageDescription(
					15, this);
			MessageDescriptions[16] = new RangeMsgDescription(16, this);
			MessageDescriptions[17] = new com.zhd.messagedescription.BestvelaMessageDescription(
					17, this);

			mReceiveSendWraper = new ReceiveSendWraper(mReceiveSendManager,
					MessageDescriptions);

		} catch (UnsupportedEncodingException e) {

			e.printStackTrace();
		} catch (Exception ep) {

		}
	}

	/**
	 * 初始化主板，包括请求什么数据
	 *
	 */
	public void Init() {
		char[] rmcCmd = { 0x02, 0x00, 0x64, 0x0D, 0x00, 0x00, 0x00, 0x03, 0x00,
				0x01, 0x00, 0x07, 0x04, 0x06, 0x00, 0x05, 0x00, 0x8B, 0x03 };

		// String commandString = "$PASHS,NME,GGA,A,ON,1";
		// mReceiveSendManager.Send(commandString.getBytes());
		// commandString = "$PASHS,NME,GGV,A,ON,1";
	}

	/**
	 * 发送命令
	 *
	 * @param cmd
	 */
	public void SendCommand(byte[] cmd) {
		mReceiveSendManager.Send(cmd);
	}

	@Override
	public void parseGGA(byte[] message) {
		try {
			String ggaMessage = "";
			try {
				ggaMessage = new String(message, 0, message.length, "US-ASCII");

				if (mZDAEntity.getUtcDate().getYear() > 110
						&& mZDAEntity.getUtcDate().getYear() < 210) {
					LogHelper.recordNmea(ggaMessage, mZDAEntity.getUtcDate());
				}
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block

				e.printStackTrace();
			}
			// TODO Auto-generated method stub
			Boolean check = VerifyHelper.X8ORVerify(message);
			if (check == false) {
				// 测试代码
				mCalfalse++;
				System.out.println(" 校验不通过计数:：" + Integer.toString(mCalfalse));
				return;
			}

			// 测试代码
			mCaltrue++;
			System.out.println(" 校验通过计数:：" + Integer.toString(mCaltrue));

			String[] fields = null;

			double value = 0.0;
			String strField = "";
			mBeforeParse++;
			try {
				ggaMessage = new String(message, 0, message.length, "US-ASCII");
				System.out.println(" 解析之前：" + Integer.toString(mBeforeParse));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			fields = ggaMessage.split(",|\\*");

			// UTC
			mGgaEntity.setUtcTime(fields[1]);
			// Latitude
			strField = fields[2];
			value = strField.equals("") ? 0.0 : GpsHelper.todegree(strField);
			mGgaEntity.setLatitude(value);
			// N/S
			strField = fields[3];
			strField = strField.equals("") ? "" : strField.toUpperCase();
			mGgaEntity.setNthSthHemesphere(strField);
			// longitude
			strField = fields[4];
			value = strField.equals("") ? 0.0 : GpsHelper.todegree(strField);
			mGgaEntity.setLongitude(value);
			// W/E
			strField = fields[5];
			strField = strField.equals("") ? "" : strField.toUpperCase();
			mGgaEntity.setWstEstHemesphere(strField);
			// GPSStatus
			strField = fields[6];
			mGgaEntity.setGpsStatus(GpsHelper.toInt(strField));
			// CaculatedNum
			strField = fields[7];
			mGgaEntity.setCaculateSatNum(GpsHelper.toInt(strField));
			// Height
			strField = fields[9];
			value = GpsHelper.toDouble(strField);
			mGgaEntity.setHeight((float) value);
			// undulation
			strField = fields[11];
			value = GpsHelper.toDouble(strField);
			mGgaEntity.setUndulation((float) value);
			// DiffAge
			strField = fields[13];
			if (strField.startsWith("0"))
				strField = strField.substring(1);

			mGgaEntity.setDiffTime(strField);
			// Station
			strField = fields[14];
			mGgaEntity.setBaseStationNum(strField);
			// tell listener
			for (int i = 0; i < mGgaVetor.size(); i++) {
				mReceiveGGAListner = mGgaVetor.get(i);
				if (mReceiveGGAListner != null) {
					mReceiveGGAListner.TellReceiveGGA(mGgaEntity);
				}
			}

			Log.i("ggavector", Integer.toString(mGgaVetor.size()));

			if (mReceiveGGAMessageListner != null)
				mReceiveGGAMessageListner.TellReceiveGGAMessage(ggaMessage);

			mAfterParse++;
			System.out.println(" 解析之后：" + Integer.toString(mAfterParse));

		} catch (Exception e) {
			// TODO: handle exception

		}

	}

	@Override
	public void parseGSV(byte[] message) {
		// TODO Auto-generated method stub
		// gsv message parsing
		try {
			System.out.println("GSV开始解析");
			Boolean check = VerifyHelper.X8ORVerify(message);
			if (check == false) {
				System.out.println("gsv解析失败");
				return;
			}

			int gsvMsgType = 0;
			int lastGsvMsgType = 0;

			String[] fields = null;
			String gpsMessage = "";

			if (mGsvMap == null) {
				mGsvMap = new HashMap<Integer, SatelliteEntity>();
			}

			try {
				gpsMessage = new String(message, 0, message.length, "US-ASCII");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			fields = gpsMessage.split(",|\\*");
			if (fields.length < 4)
				return;

			int allMsgCount = GpsHelper.toInt(fields[1]);
			int mGsvIndex = GpsHelper.toInt(fields[2]);
			if (mGsvIndex == 1) {
				mGsvMap.clear();
				mGsvEntity = new GSVEntity();
			}

			mGsvEntity.setSeenSatNum(GpsHelper.toInt(fields[3]));

			GpsEnum.GpsType gpsType = null;
			int validFiledCount = (fields.length - 5) / 4;
			for (int j = 0; j < validFiledCount; j++) {
				if (fields[4 * j + 4].equals("")) {
					continue;
				}

				int prn = GpsHelper.toInt(fields[4 * j + 4]);
				if (gpsMessage.startsWith("$GPGSV")) {
					if (prn < 160) {
						if (prn >= 33 && prn <= 64) {
							gpsType = GpsEnum.GpsType.SBAS;
							prn += 87;
						}
					}
					gpsType = GpsEnum.GpsType.GPS;

				} else if (gpsMessage.startsWith("$BDGSV")) { // Novatel
					if (prn < 160) {
						prn += 160;
					}

					gpsType = GpsEnum.GpsType.BD;

					gsvMsgType = 0;
				} else if (gpsMessage.startsWith("$GBDGSV")) {// Trimble
					if (prn < 160) {
						prn += 100;
					}

					gpsType = GpsEnum.GpsType.BD;

					gsvMsgType = 1;
				} else if (gpsMessage.startsWith("$GLGSV")) {
					if (motherboardType == 1)
						prn += 64;

					gpsType = GpsEnum.GpsType.GLONASS;

					gsvMsgType = 2;
				} else {
					gsvMsgType = 3;
				}

				mGsvSatEntity = new SatelliteEntity();
				mGsvSatEntity.setSatPrn(prn);
				mGsvSatEntity.setGpsType(gpsType);
				mGsvSatEntity.setElevation(GpsHelper.toInt(fields[4 * j + 5]));
				mGsvSatEntity.setAzimath(GpsHelper.toInt(fields[4 * j + 6]));
				mGsvSatEntity.setSnrL1(GpsHelper.toInt(fields[4 * j + 7]));
				mGsvMap.put(prn, mGsvSatEntity);
			}

			if (mGsvIndex == allMsgCount) {

				mGsvEntity.setGsvMap(mGsvMap);
				if (mReceiveGSVListner != null)
					mReceiveGSVListner.TellReceiveGSV(mGsvEntity);
				HashMap<Integer, SatelliteEntity> mGSVmap = mGsvEntity
						.getGsvMap();

				lastGsvMsgType = gsvMsgType;

				System.out.println("卫星总数是： " + String.valueOf(mGSVmap.size()));

			}
		} catch (Exception e) {
			// TODO: handle exception

		}

	}

	@Override
	public void parseVTG(byte[] message) {

		// TODO Auto-generated method stub
		try {

			Boolean check = VerifyHelper.X8ORVerify(message);
			if (check == false)
				return;
			String[] fields = null;
			String vtgMessage = "";
			float value = 0.0f;
			String strField = "";
			mBeforeParse++;
			try {
				vtgMessage = new String(message, 0, message.length, "US-ASCII");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			fields = vtgMessage.split(",|\\*");
			// TDirection
			strField = fields[1];
			value = GpsHelper.toFloat(strField);
			mVtgEntity.setSpeedRate(value);
			// MDirection
			strField = fields[3];
			value = GpsHelper.toFloat(strField);
			mVtgEntity.setSpeedRate(value);
			// Speed
			strField = fields[7];
			value = GpsHelper.toFloat(strField);
			mVtgEntity.setSpeedRate(value);
			// tell listener
			if (mReceiveVTGListner != null)
				mReceiveVTGListner.TellReceiveVTG(mVtgEntity);
		} catch (Exception e) {
			// TODO: handle exception

		}
	}

	@Override
	public void parseRMC(byte[] message) {
		// TODO Auto-generated method stub
		try {
			Boolean check = VerifyHelper.X8ORVerify(message);
			if (check == false) {
				return;
			}

			String[] fields = null;
			String rmcMessage = "";

			try {
				rmcMessage = new String(message, 0, message.length, "US-ASCII");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			fields = rmcMessage.split(",|\\*");
			if (fields.length < 9)
				return;

			double value = 0.0;
			String strField = "";
			strField = fields[1];
			int hour = GpsHelper.toInt(strField.substring(0, 2));
			int minute = GpsHelper.toInt(strField.substring(2, 4));
			int second = GpsHelper.toInt(strField.substring(4));

			strField = fields[9];
			int day = GpsHelper.toInt(strField.substring(0, 2));
			int month = GpsHelper.toInt(strField.substring(2, 4));
			int year = 2000 + GpsHelper.toInt(strField.substring(4));

			mRMCEntity.setUtcDate(new Date(year - 1900, month - 1, day, hour,
					minute, second));

			if (mReceiveRMCListner != null)
				mReceiveRMCListner.TellReceiveRMC(mRMCEntity);

		} catch (Exception e) {
			// TODO: handle exception

		}
	}

	@Override
	public void parseZDA(byte[] message) {
		try {
			Boolean check = VerifyHelper.X8ORVerify(message);
			if (check == false) {
				return;
			}

			String[] fields = null;
			String rmcMessage = "";

			try {
				rmcMessage = new String(message, 0, message.length, "US-ASCII");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			fields = rmcMessage.split(",|\\*");
			if (fields.length < 8)
				return;

			int hour = 0;
			int minute = 0;
			int second = 0;
			int day = 16;
			int month = 9;
			int year = 1901;
			int offHour = 0;
			int offMin = 0;
			try {
				hour = GpsHelper.toInt(fields[1].substring(0, 2));
				minute = GpsHelper.toInt(fields[1].substring(2, 4));
				second = GpsHelper.toInt(fields[1].substring(4));
				day = GpsHelper.toInt(fields[2]);
				month = GpsHelper.toInt(fields[3]) - 1;
				year = GpsHelper.toInt(fields[4]);
				offHour = GpsHelper.toInt(fields[5]);
				offMin = GpsHelper.toInt(fields[6]);
			} catch (Exception e) {

			}

			if (minute + offMin >= 60)
				hour++;

			if (hour + offHour >= 24)
				day++;
			else if (hour + offHour < 0) {
				day--;
				hour = +24;
			}

			cc.set(year, month, day, (hour + offHour) % 24,
					(minute + offMin) % 60, second);
			Date d = cc.getTime();
			mZDAEntity.setUtcDate(d);

			if (mReceieZDAListener != null)
				mReceieZDAListener.TellReceiveZDA(mZDAEntity);

		} catch (Exception e) {
			// TODO: handle exception

		}
	}

	@Override
	public void parseTrackState(byte[] message) {

	}

	public void parseTrimble4hData(byte[] message) {

	}

	@Override
	public void parseGSA(byte[] message) {
		// TODO Auto-generated method stub
		Boolean check = VerifyHelper.X8ORVerify(message);
		if (check == false)
			return;
		String[] fields = null;
		String GSAMessage = "";
		float value = 0.0f;
		String strField = "";
		mBeforeParse++;
		try {
			GSAMessage = new String(message, 0, message.length, "US-ASCII");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		fields = GSAMessage.split(",|\\*");
		// PDOP
		strField = fields[15];
		value = GpsHelper.toFloat(strField);
		mGSAEntity.setPDOP(value);
		// HDOP
		strField = fields[16];
		value = GpsHelper.toFloat(strField);
		mGSAEntity.setHDOP(value);
		// VDOP
		strField = fields[17];
		value = GpsHelper.toFloat(strField);
		mGSAEntity.setVDOP(value);

		if (mReceiveGSAListner != null)
			mReceiveGSAListner.TellReceiveGSA(mGSAEntity);
	}

	@Override
	public void parseBestvela(byte[] entireMessage) {
		String[] fields = null;
		String GSAMessage = "";
		float value = 0.0f;
		String strField = "";
		mBeforeParse++;
		try {
			GSAMessage = new String(entireMessage, 0, entireMessage.length,
					"US-ASCII");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		fields = GSAMessage.split(",|\\*|\\;");
		// PDOP
		strField = fields[14];
		value = GpsHelper.toFloat(strField);
		mBestvelaEntity.setSpeedRate(value);
		if (mReceiveBestvelaListener != null)
			mReceiveBestvelaListener.TellReceiveBestvela(mBestvelaEntity);
	}

	@Override
	public void parseRangeCMP(byte[] message) {
		// TODO Auto-generated method stub
		int headLen = message[3] & 0xFF;
		byte[] bzHead = new byte[headLen];

		System.arraycopy(message, 0, bzHead, 0, 28);

		short msgID = (short) BitConverter.toUInt16(bzHead, 4);

		if (msgID == 140) {

			if (mRangeMap == null)
				mRangeMap = new HashMap<Integer, RangeEntity>();
			else
				mRangeMap.clear();
			short msgLen = (short) BitConverter.toUInt16(bzHead, 8);
			int whole = (int) (headLen + msgLen);
			if (message.length < whole)
				return;

			byte[] messageInfo = new byte[msgLen];
			System.arraycopy(message, headLen, messageInfo, 0, msgLen - 1);

			int iPos = 0;
			int prnNext = 0;
			while (iPos < messageInfo.length) {
				mRangeEntity = new RangeEntity();

				if (iPos + 24 > messageInfo.length) {
					break;
				}

				byte tempPrn = messageInfo[iPos + 21];

				int prn = tempPrn & 0xFF;

				// prn = GpsHelper.GetStandardPrn(prn);

				while (prn == 0) {
					iPos += 24;
					if (iPos >= messageInfo.length) {
						break;
					}
				}

				mRangeEntity.setPrn(prn);
				mRangeEntity.setSvType(GpsHelper.GetGpsTypeForUB280(prn));
				// 解析参见Novatel oem6 beidou addendum
				byte tempCno = messageInfo[iPos + 24];
				short tempcno = BitConverter.byteArrayToShort(messageInfo,
						iPos + 24);
				int snrNo = ((tempcno >> 5) & 0x1F) + 20;

				int lStatus = BitConverter
						.byteArrayToInt(messageInfo, iPos + 4);
				int sig_type = (lStatus >> 21) & 0x1F;

				if ((sig_type == 0) || (sig_type == 4)) {
					mRangeEntity.setL1(snrNo);
					mRangeMap.put(prn, mRangeEntity);
				} else if ((sig_type == 1) || (sig_type == 5)
						|| (sig_type == 9) || (sig_type == 17)) {
					try {
						RangeEntity tempRE = mRangeMap.get(prn);
						tempRE.setL2(snrNo);
						// mRangeMap.remove(prn);
						mRangeMap.put(prn, tempRE);
					} catch (Exception e) {
						Log.v("测试123", e+"");
					}
				}

				iPos += 24;

				// mRangeMap.put(prn, mRangeEntity);
			}
			mRangeMap.size();
			if (mReceiveRangeListner != null)
				mReceiveRangeListner.TellReceiveRange(mRangeMap);
		} else if (msgID == 48) {

			// TODO Auto-generated method stub

			mSATVISSatEntity = new SatelliteEntity();

			if (mSATVISMap == null)
				mSATVISMap = new HashMap<Integer, SatelliteEntity>();

			short msgLen = (short) BitConverter.toUInt16(bzHead, 8);
			int whole = (int) (headLen + msgLen);
			if (message.length < whole)
				return;

			byte[] messageInfo = new byte[msgLen];
			System.arraycopy(message, headLen, messageInfo, 0, msgLen - 1);
			long seensatnum = BitConverter.toUInt32(messageInfo, 8);

			for (int i = 0; i < seensatnum; i++) {
				int prn = BitConverter.toUInt16(messageInfo, 12 + i * 40);
				if (0 >= prn && prn >= 198)
					continue;
				double elev = BitConverter.byteArrayToDouble(messageInfo,
						20 + i * 40);
				double az = BitConverter.byteArrayToDouble(messageInfo,
						28 + i * 40);
				mSATVISSatEntity = new SatelliteEntity();
				mSATVISSatEntity.setSatPrn(prn);
				mSATVISSatEntity.setElevation(Math.round(elev));
				mSATVISSatEntity.setAzimath(Math.round(az));

				if (0 < prn && prn < 33)
					mSATVISSatEntity.setGpsType(GpsType.GPS);
				else if (37 < prn && prn < 62)
					mSATVISSatEntity.setGpsType(GpsType.GLONASS);
				else if (119 < prn && prn < 138)
					mSATVISSatEntity.setGpsType(GpsType.SBAS);
				else if (160 < prn && prn < 198)
					mSATVISSatEntity.setGpsType(GpsType.BD);

				mSATVISMap.put(prn, mSATVISSatEntity);
			}
			mSATVISEntity.setSATVISMap(mSATVISMap);
			mSATVISEntity.setSeenSatNum((int) seensatnum);
			if (mReceiveSATVISListener != null)
				mReceiveSATVISListener.TellReceiveSATVIS(mSATVISEntity);

		}

		else
			return;
	}

	@Override
	public void setReceiveGGAListener(ReceiveGGAListner listener) {
		// mReceiveGGAListner = listener;
		mGgaVetor.add(listener);
	}

	@Override
	public ReceiveGGAListner getReceiveGGAListener() {
		return mReceiveGGAListner;
	}

	@Override
	public void removeGgaListener(ReceiveGGAListner listener) {
		for (int i = 0; i < mGgaVetor.size(); i++) {
			if (mGgaVetor.get(i).equals(listener)) {
				mGgaVetor.remove(i);
			}
		}
	}

	public void setReceiveGSAListener(ReceiveGSAListner listener) {
		mReceiveGSAListner = listener;
	}

	public void setReceiveGGAMessageListener(ReceiveGGAMessageListner listener) {
		mReceiveGGAMessageListner = listener;
	}

	public void setReceiveVTGListener(ReceiveVTGListner listener) {
		mReceiveVTGListner = listener;
	}

	public void setReceiveRMCListener(ReceiveRMCListner listener) {
		mReceiveRMCListner = listener;
	}

	public void setReceiveGSVListener(ReceiveGSVListner listener) {
		mReceiveGSVListner = listener;
	}

	public void setReceiveTrackStatListener(ReceiveTrackStatListner listener) {
	}

	public void setReceiveTrimble40hListener(ReceiveTrimble40hListener listener) {
	}

	public void setReceiveZDAListener(ReceiveZDAListner listener) {
		mReceieZDAListener = listener;
	}

	public void setReceiveRangeListener(ReceiveRangeListner listener) {
		mReceiveRangeListner = listener;
	}

	public void setReceiveSATVISListener(ReceiveSATVISListener listener) {

		mReceiveSATVISListener = listener;

	}

	public void stopSerialPort() {
		mReceiveSendWraper.Stop();
		mReceiveSendWraper.setFlag(false);
	}

	@Override
	public void setReceiveBestvelaListener(ReceiveBestvelaListner listener) {
		mReceiveBestvelaListener = listener;
	}

	@Override
	public void parseTrimble35hData(byte[] message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setReceiveBaseStationListener(ReceiveBaseStationListner listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void parsePTNL(byte[] entireMessage) {
		// TODO Auto-generated method stub

	}

	@Override
	public void parseZcby(byte[] message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setReceiveZcbyListener(ReceiveZcbyListner listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void parseMB2(byte[] message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void parseZcbyDate(byte[] message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setReceiveZcbyDateListener(ReceiveZcbyDateListner listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void parseRASHR(byte[] message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setReceivePASHRListener(ReceivePASHRListner listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void parseReadcar(byte[] message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setReceiveReadcarListener(ReceiveReadcarListner listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void parseReaduhf(byte[] message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setReceiveReaduhfListener(ReceiveReaduhfListner listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void parseVersion(byte[] message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setReceiveVersionListener(ReceiveVersionListner listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void parseReadZXZY(byte[] message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setReceiveReadZXZYListener(ReceiveReadZXZYListner listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void parseReadSen(byte[] message) {
		// TODO Auto-generated method stub

	}



	@Override
	public void setReceiveReadSenListener(ReceiveReadSenListner listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setReceiveECUListener(ReceiveECUListner listener) {

	}



	@Override
	public void setReceiveNAKListener(ReceiveNAKListner listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void parseTB2Version(byte[] message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setReceiveTB2VersionListener(ReceiveTB2VersionListner listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void parseABangle(byte[] message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void parseECU(byte[] message) {

	}

	@Override
	public void setReceiveABangleListener(ReceiveABangleListner listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void parseDeadZone(byte[] message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setReceiveReadDeadZoneListener(
			ReceiveReadDeadZoneDateListner listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void parseReadPid(byte[] message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setReceiveReadPidListener(ReceivereadpidListner listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void parseAGRICA(byte[] message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setReceiveAGRICAleListener(ReceiveAGRICAListner listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void parseUMVersion(byte[] message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setReceiveUMVersionListener(ReceiveUMVersionListner listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void parseboot(byte[] message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setReceivebooListener(ReceivebootListner listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void parseInsVersion(byte[] message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setReceiveInsVersionListener(ReceiveInsVersionListner listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void parseMduVersion(byte[] message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setReceiveMduVersionListener(ReceiveMduVersionListner listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void parseCorrectIMU(byte[] message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setReceiveCorrectIMUListener(ReceiveCorrectimuListner listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void parseRegVersion(byte[] message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setReceiveRegistener(ReceiveRegListner listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void parseZC31(byte[] message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setReceiveZC31(ReceiveZC31Listner listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void parseServerOk(byte[] message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setReceiveServerOk(ReceiveServerOkListner listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void parseSelf(byte[] message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setReceiveSelfOne(ReceiveSelfOneListner listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setReceiveSelfTwo(ReceiveSelfTwoListner listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void parseServeroff(byte[] message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setReceiveServerOff(ReceiveServerOffListner listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void parseServerQXwz(byte[] message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setReceiveQXwzListner(ReceiveQXwzListner listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void parseOpened(byte[] message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setReceiveOpendedListner(ReceiveOpenedListner listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void parseServerSDK(byte[] message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setReceiveSDKListner(ReceiveSDKListner listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void parseoem(byte[] message) {
		
	}

	@Override
	public void parseBESTPOSA(byte[] message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setReceiveBESTPOSAListner(ReceiveBESTPOSAListner listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setoemListner(ReceiveOemListner listener) {

	}



	@Override
	public void parseServerNode(byte[] message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setReceiveNodeListner(ReceiveNodeListner listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setReceiveSelfRet(ReceiveSelfRetListner listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void parseMCUVersion(byte[] message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setReceiveMCUVersionListner(ReceiveMCUVersionListner listener) {
		// TODO Auto-generated method stub

	}
}
