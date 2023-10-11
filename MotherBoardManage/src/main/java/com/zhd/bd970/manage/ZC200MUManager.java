package com.zhd.bd970.manage;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.util.Log;

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
import com.zhd.bd970.manage.interfaces.ReceiveOemListner;
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

import com.zhd.gps.manage.models.AGRICEntity;
import com.zhd.gps.manage.models.GGAEntity;
import com.zhd.gps.manage.models.PidEntity;
import com.zhd.gps.manage.models.ReadSenEntity;
import com.zhd.gps.manage.models.GSAEntity;
import com.zhd.gps.manage.models.BaseStationEntity;
import com.zhd.gps.manage.models.GSVEntity;
import com.zhd.gps.manage.models.RangeEntity;
import com.zhd.gps.manage.models.ReadcarEntity;
import com.zhd.gps.manage.models.ReaduhfEntity;
import com.zhd.gps.manage.models.SelfOneEntity;
import com.zhd.gps.manage.models.SelfRetEntity;
import com.zhd.gps.manage.models.SelfTwoEntity;
import com.zhd.gps.manage.models.VersionEntity;
import com.zhd.gps.manage.models.ZC31Entity;
import com.zhd.gps.manage.models.ZCBYDateEntity;
import com.zhd.gps.manage.models.ZCBYEntity;
import com.zhd.gps.manage.models.ZDAEntity;
import com.zhd.gps.manage.models.GpsEnum;
import com.zhd.gps.manage.models.RMCEntity;
import com.zhd.gps.manage.models.SatelliteEntity;
import com.zhd.gps.manage.models.TrackStatEntity;
import com.zhd.gps.manage.models.Trimble40hEntity;
import com.zhd.gps.manage.models.VTGEntity;

import com.zhd.datamanagetemplate.MessageDescription;
import com.zhd.datamanagetemplate.ReceiveSendManager;
import com.zhd.datamanagetemplate.ReceiveSendWraper;
import com.zhd.parserinterface.*;
import com.zhd.utility.filehelper.LogHelper;
import com.zhd.commonhelper.*;

public class ZC200MUManager implements IParser {

	private static ReceiveTrackStatListner mReceiveTrackStatListner = null;
	private static ReceiveRangeListner mReceiveRangeListner = null;
	private static ReceiveRMCListner mReceiveRMCListner = null;
	private static TrackStatEntity mTrackStatEntity = null;
	private static HashMap<Integer, TrackStatEntity> mTrackStatMap = null;
	private static HashMap<Integer, RangeEntity> mRangeMap = null;
	private static ReceiveGGAListner mReceiveGGAListner = null;
	private static ReceiveZcbyListner mReceiveZcbyListner = null;
	private static ReceiveReadZXZYListner mReceiveReadZXYListner = null;
	private static ReceiveReadSenListner mReceiveReadSenListner = null;
	private static ReceiveZcbyDateListner mReceiveZcbyDateListner = null;
	private static ReceiveReadcarListner mReceiveReadcarListner = null;
	private static ReceiveReaduhfListner mReceiveReaduhfListner = null;
	private static ReceiveUMVersionListner mReceiveUMVersionListner = null;
	private static ReceiveReadDeadZoneDateListner mReceiveDeadZoneListner = null;
	private static ReceiveCorrectimuListner mReceiveCorrectimuListner = null;
	private static ReceiveServerOkListner mReceiveServerOkListner = null;
	private static ReceiveServerOffListner mReceiveServerOffListner = null;
	private static ReceiveQXwzListner mReceiveQXwzListner = null;

	private static ReceiveNodeListner mReceiveNodeListner = null;
	private static ReceiveSDKListner mReceiveSDKListner = null;
	private static ReceiveBESTPOSAListner mReceiveBESTPOSAListner = null;

	private static ReceiveVersionListner mReceiveVersionListner = null;
	private static ReceiveInsVersionListner mReceiveInsVersionListner = null;
	private static ReceiveMduVersionListner mReceiveMduVersionListner = null;
	private static ReceiveTB2VersionListner mReceiveTB2VersionListner = null;
	private static ReceiveABangleListner ABangleVersionListner = null;
	private static ReceiveGGAMessageListner mReceiveGGAMessageListner = null;
	private static ReceiveOpenedListner mReceiveOpenedListner = null;
	private static ReceiveOemListner mReceiveOemListner  = null;
	private static ReceiveGSVListner mReceiveGSVListner = null;
	private static ReceiveTrimble40hListener mReceie40hListener = null;
	private static ReceiveBaseStationListner mReceiveBaseStationListener = null;
	private static ReceiveZDAListner mReceieZDAListener = null;
	private static ReceiveVTGListner mReceiveVTGListner = null;
	private static ReceiveGSAListner mReceiveGSAListner = null;
	private static ReceiveECUListner mReceiveECUListner = null;
	private static ReceiveNAKListner mReceiveNAKListner = null;
	private static ReceivereadpidListner mReceivereadpidListner = null;
	private static ReceiveAGRICAListner mReceiveAGRICAListner = null;
	private static ReceivebootListner mReceivebootListner = null;
	private static ReceiveRegListner mReceiveRegListner = null;
	private static ReceiveZC31Listner mReceiveZC31Listner = null;
	private static ReceiveSelfOneListner mReceiveSelfOneListner = null;
	private static ReceiveSelfTwoListner mReceiveSelfTwoListner = null;
	private static ReceiveSelfRetListner mReceiveSelfRetListner = null;
	private static ReceivePASHRListner mReceiveRASHRListner = null;
	private static GSVEntity mGsvEntity = null;
	private static ZCBYEntity mZCBYEntity = null;
	private static ZCBYDateEntity mZCBYDateEntity = null;
	private static ZDAEntity mZDAEntity = new ZDAEntity();
	private static ReadSenEntity mReadSenEntity = new ReadSenEntity();
	private static VersionEntity mVersionEntity = new VersionEntity();

	private static RangeEntity mRangeEntity = null;
	private static SatelliteEntity mGsvSatEntity = null;
	private static SatelliteEntity mGsofSatEntity = null;
	private static Trimble40hEntity m40hEntity = null;
	private static GSAEntity mGSAEntity = new GSAEntity();
	private static ReadcarEntity mReadcarEntity = new ReadcarEntity();
	private static ReaduhfEntity mReaduhfEntity = new ReaduhfEntity();
	private static RMCEntity mRMCEntity = new RMCEntity();
	private static HashMap<Integer, SatelliteEntity> mGsvMap = null;
	private static HashMap<Integer, SatelliteEntity> mGsofMap = null;
	private static GGAEntity mGgaEntity = new GGAEntity();
	private static VTGEntity mVtgEntity = new VTGEntity();
	private static Vector<ReceiveGGAListner> mGgaVetor = new Vector<ReceiveGGAListner>();
	private ReceiveSendManager mReceiveSendManager = null;
	private ReceiveSendWraper mReceiveSendWraper = null;
	private int mBeforeParse = 0;
	private int mAfterParse = 0;
	private int mCalfalse = 0;
	private int mCalfalse_1 = 0;
	private int mCaltrue = 0;
	private Calendar cc = Calendar.getInstance();
	private int motherboardType = 0;// 0是天宝 1是诺瓦泰 2是和芯星通
	private static ReceiveMCUVersionListner mReceiveMCUVersionListner = null;

	/**
	 *
	 * @param communicateMode
	 *            与主板交互的方式，是串口还是蓝牙
	 */
	public ZC200MUManager(ReceiveSendManager communicateMode) {
		mReceiveSendManager = communicateMode;
		try {

			MessageDescription[] MessageDescriptions = new MessageDescription[11];
			MessageDescriptions[0] = new com.zhd.messagedescription.OemMessageDescription(
					0, this);
			MessageDescriptions[1] = new com.zhd.messagedescription.VTGMessageDescription(
					1, this);
			MessageDescriptions[2] = new com.zhd.messagedescription.ECUMessageDescription(
					2, this);
			MessageDescriptions[3] = new com.zhd.messagedescription.MduVersionMessageDescription(
					3, this);
			MessageDescriptions[4] = new com.zhd.messagedescription.GGAMessageDescription(
					4, this);
			MessageDescriptions[5] = new com.zhd.messagedescription.GNGGAMessageDescription(
					5, this);
			MessageDescriptions[6] = new com.zhd.messagedescription.GBGGAMessageDescription(
					6, this);
			MessageDescriptions[7] = new com.zhd.messagedescription.VTGMessageDescription(
					7, this);
			MessageDescriptions[8] = new com.zhd.messagedescription.GNVTGMessageDescription(
					8, this);
			MessageDescriptions[9] = new com.zhd.messagedescription.GBVTGMessageDescription(
					9, this);
			MessageDescriptions[10] = new com.zhd.messagedescription.AGRICAMessageDescription(
					10, this);
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
			// TODO Auto-generated method stub
			String ggaMessage = "";
			try {
				// ggaMessage = new String(message, 0, message.length,
				// "US-ASCII");
				ggaMessage = new String(message, "GB2312");
				// Log.e("年份", mZDAEntity.getUtcDate().getYear()+"");
/*
				if (mZDAEntity.getUtcDate().getYear() > 110
						&& mZDAEntity.getUtcDate().getYear() < 210) {
					LogHelper.recordNmea(ggaMessage, mZDAEntity.getUtcDate());
				}*/

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

			String[] fields = null;
			double value = 0.0;
			String strField = "";
			mBeforeParse++;
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
			strField = fields[8];
			value = GpsHelper.toDouble(strField);
			mGgaEntity.setHdop((float) value);
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

			if (mReceiveGGAListner != null) {
				mReceiveGGAListner.TellReceiveGGA(mGgaEntity);
				mReceiveGGAListner.TellReceiveGGA(message);
			}

			Log.i("ggavector", Integer.toString(mGgaVetor.size()));

			if (mReceiveGGAMessageListner != null)
				mReceiveGGAMessageListner.TellReceiveGGAMessage(ggaMessage);

			mAfterParse++;
			System.out.println(" 解析之后：" + Integer.toString(mAfterParse));

		} catch (Exception e) {

		}

	}

	public void parsePTNL(byte[] message) {

		try {

			// TODO Auto-generated method stub
			String ggaMessage = "";
			try {
				ggaMessage = new String(message, 0, message.length, "US-ASCII");

				if (mZDAEntity.getUtcDate().getYear() > 110
						&& mZDAEntity.getUtcDate().getYear() < 210) {
					// LogHelper.recordNmea(ggaMessage,
					// mZDAEntity.getUtcDate());
				}
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block

				e.printStackTrace();
			}
			// TODO Auto-generated method stub

		} catch (Exception e) {

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
			Log.v("GSV信息", gpsMessage);
			fields = gpsMessage.split(",|\\*");
			if (fields.length < 4)
				return;

			int allMsgCount = GpsHelper.toInt(fields[1]);
			int mGsvIndex = GpsHelper.toInt(fields[2]);

			if (mGsvIndex == 1) {
				// mGsvMap.clear();
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
					if (prn >= 1 && prn <= 32) {
						gpsType = GpsEnum.GpsType.GPS;
					} else if (prn >= 32 && prn <= 37) {
						gpsType = GpsEnum.GpsType.BD;
					} else if (prn >= 65 && prn <= 96) {
						gpsType = GpsEnum.GpsType.GLONASS;
					} else if (prn >= 120 && prn <= 141) {
						gpsType = GpsEnum.GpsType.SBAS;
					} else if (prn >= 183 && prn <= 187) {
						gpsType = GpsEnum.GpsType.SBAS;
					} else {
						gpsType = GpsEnum.GpsType.GPS;
					}

				} else if (gpsMessage.startsWith("$BDGSV")) { // Novatel
					if (prn < 160) {
						prn += 160;
					}

					gpsType = GpsEnum.GpsType.BD;
				} else if (gpsMessage.startsWith("$GBDGSV")) {// Trimble
					if (prn < 160) {
						prn += 100;
					}

					gpsType = GpsEnum.GpsType.BD;
				} else if (gpsMessage.startsWith("$GBGSV")) {// Trimble
					if (prn < 160) {
						prn += 100;
					}

					gpsType = GpsEnum.GpsType.BD;
				} else if (gpsMessage.startsWith("$GLGSV")) {
					if (motherboardType == 1)
						prn += 64;

					gpsType = GpsEnum.GpsType.GLONASS;
				}
				if (GpsHelper.toInt(fields[4 * j + 7]) != 0) {
					try {
						mGsvSatEntity = new SatelliteEntity();
						mGsvSatEntity.setSatPrn(prn);
						mGsvSatEntity.setGpsType(gpsType);
						mGsvSatEntity.setElevation(GpsHelper
								.toInt(fields[4 * j + 5]));
						mGsvSatEntity.setAzimath(GpsHelper
								.toInt(fields[4 * j + 6]));
						mGsvSatEntity.setSnrL1(GpsHelper
								.toInt(fields[4 * j + 7]));
						SatelliteEntity abc = mGsvMap.get(prn);
						if (abc != null) {
							if (abc.getSnrL2() != 0)
								mGsvSatEntity.setSnrL2(abc.getSnrL2());
						}
						mGsvMap.put(prn, mGsvSatEntity);
					} catch (Exception e) {
					}
					Log.v("GSV信息", gpsType + ";");
				}
			}

			if (mGsvIndex == allMsgCount) {
				mGsvEntity.setGsvMap(mGsvMap);
				HashMap<Integer, SatelliteEntity> mGSVmap = mGsvEntity
						.getGsvMap();
				mGsvEntity.setSeenSatNum(mGSVmap.size());

				if (mReceiveGSVListner != null)
					mReceiveGSVListner.TellReceiveGSV(mGsvEntity);

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
			String ggaMessage = "";
			try {
				// ggaMessage = new String(message, 0, message.length,
				// "US-ASCII");
				ggaMessage = new String(message, "GB2312");
				if (mZDAEntity.getUtcDate().getYear() > 110
						&& mZDAEntity.getUtcDate().getYear() < 210) {
					// LogHelper.recordNmea(ggaMessage,
					// mZDAEntity.getUtcDate());
				}
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block

				e.printStackTrace();
			}
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
			mVtgEntity.setTDirection(value);
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
			// Log.e("测试", rmcMessage);
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
				second = GpsHelper.toInt(fields[1].substring(4, 6));
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
		try {

			int headLen = message[3] & 0xFF;
			byte[] bzHead = new byte[28];

			System.arraycopy(message, 0, bzHead, 0, 28);

			if (mTrackStatMap == null)
				mTrackStatMap = new HashMap<Integer, TrackStatEntity>();
			else
				mTrackStatMap.clear();

			short msgID = (short) BitConverter.toUInt16(bzHead, 4);
			short msgLen = (short) BitConverter.toUInt16(bzHead, 8);
			int whole = (int) (headLen + msgLen);
			if (message.length < whole)
				return;

			byte[] messageInfo = new byte[msgLen];
			System.arraycopy(message, 28, messageInfo, 0, msgLen - 1);

			int iPos = 16;
			while (iPos < messageInfo.length) {
				mTrackStatEntity = new TrackStatEntity();

				if (iPos + 2 >= messageInfo.length) {
					break;
				}

				int prn = BitConverter.toUInt16(messageInfo, iPos);
				prn = GpsHelper.GetStandardPrn(prn);

				while (prn == 0) {
					iPos += 40;
					if (iPos + 2 >= messageInfo.length) {
						break;
					}
					prn = BitConverter.toUInt16(messageInfo, iPos);
				}
				if (iPos + 4 >= messageInfo.length) {
					break;
				}

				mTrackStatEntity.setPrn(prn);
				mTrackStatEntity.setSvType(GpsHelper.GetGpsTypeForNovatel(prn));

				float snrL1 = BitConverter.byteArrayToSingle(messageInfo,
						iPos + 20);

				mTrackStatEntity.setL1(snrL1);

				iPos += 40;
				if (iPos + 2 >= messageInfo.length) {
					break;
				}

				int prnNext = BitConverter.toUInt16(messageInfo, iPos);
				prnNext = GpsHelper.GetStandardPrn(prn);
				// 临时措施
				if (prnNext == prn) {
					mTrackStatEntity.setL2(BitConverter.byteArrayToSingle(
							messageInfo, iPos + 20));

					iPos += 40;
				}
				mTrackStatMap.put(prn, mTrackStatEntity);

			}

			if (mReceiveTrackStatListner != null)
				mReceiveTrackStatListner.TellReceiveTrackStat(mTrackStatMap);
		} catch (Exception e) {
			// TODO: handle exception

		}

	}

	public void parseTrimble4hData(byte[] message) {
		// GSOF头是2
		// if (message[0] != 2)
		// return;
		// 对当前页数据进行校验
		try {

			String str = "";
			for (byte b : message) {
				str += (b & 0xFF) + " ";
			}
			System.out.println(str);
			// 解析单页数据
			int packetType = message[2] & 0xFF;
			int packetPage = message[4] & 0xFF;
			int msgCurIndex = message[5] & 0xFF;
			int maxMsgIndex = message[6] & 0xFF;
			int recordType = message[7] & 0xFF;
			int geofLength = message[8] & 0xFF;
			int prn = 0;
			int satIndex = 0;

			if (msgCurIndex == 1 || msgCurIndex > 2 || msgCurIndex < 0)
				return;

			if (recordType == 34) {
				m40hEntity = new Trimble40hEntity();
				if (msgCurIndex == 0) {
					mGsofMap = new HashMap<Integer, SatelliteEntity>();
				} else if (msgCurIndex == 1) {

				}

				int seenSatNum = message[9] & 0xFF;
				if (seenSatNum > 40)
					seenSatNum = 40;

				m40hEntity.setSeenSatNum(seenSatNum);

				for (int i = 0; i < seenSatNum; i++) {
					satIndex = 10 * (i + 1);
					prn = message[satIndex] & 0xFF;
					if (prn == 0 || prn > 173)
						continue;

					int gpsTypevalue = message[satIndex + 1] & 0xFF;
					GpsEnum.GpsType gpsType = null;
					switch (gpsTypevalue) {
						case 0:
							gpsType = GpsEnum.GpsType.GPS;
							break;
						case 1:
							gpsType = GpsEnum.GpsType.SBAS;
							break;
						case 2:
							gpsType = GpsEnum.GpsType.GLONASS;
							prn += 64;
							break;
						case 5:
							gpsType = GpsEnum.GpsType.BD;
							prn += 160;
							break;
					}

					mGsofSatEntity = new SatelliteEntity();
					mGsofSatEntity.setSatPrn(prn);
					mGsofSatEntity.setGpsType(gpsType);
					mGsofSatEntity
							.setElevation((float) (message[satIndex + 4] & 0xFF));
					mGsofSatEntity
							.setAzimath((float) ((message[satIndex + 5] & 0xFF) * 256 + (message[satIndex + 6] & 0xFF)));
					mGsofSatEntity
							.setSnrL1((int) ((message[satIndex + 7] & 0xFF) / 4 + 0.5));
					mGsofSatEntity
							.setSnrL2((int) ((message[satIndex + 8] & 0xFF) / 4 + 0.5));
					mGsofMap.put(prn, mGsofSatEntity);
				}
				m40hEntity.setGsofMap(mGsofMap);
				if (mReceie40hListener != null)
					mReceie40hListener.TellReceiveTrimble40hData(m40hEntity);
			} else if (recordType == 35) { // 基站数据
				BaseStationEntity baseStationEntity = new BaseStationEntity();
				String baseName = new String(message, 10, 8, "US-ASCII");
				int baseStationId = BitConverter.byteArrayToShort(message, 18); // 基站ID

				double latitude = CommonUtil.ConvertToDegree(BitConverter
						.GetDouble(message, 20));
				double longitude = CommonUtil.ConvertToDegree(BitConverter
						.GetDouble(message, 28));
				double height = BitConverter.GetDouble(message, 36);
				baseStationEntity.setBaseName(baseName);
				baseStationEntity.setBaseId(baseStationId);
				baseStationEntity.setLatitude(latitude);
				baseStationEntity.setLongitude(longitude);
				baseStationEntity.setHeight(height);
				if (mReceiveBaseStationListener != null)
					mReceiveBaseStationListener
							.TellReceiveBaseStation(baseStationEntity);
			}

		} catch (Exception e) {

		}
	}

	@Override
	public void parseTrimble35hData(byte[] message) {

	}

	@Override
	public void parseGSA(byte[] message) {
		// TODO Auto-generated method stub
		try {

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

			fields = GSAMessage.split(",|\\*");
			if (mReceiveGSAListner != null)
				mReceiveGSAListner.TellReceiveGSA(mGSAEntity);

		} catch (Exception e) {
			// TODO: handle exception

		}
	}

	@Override
	public void parseRangeCMP(byte[] message) {
		// TODO Auto-generated method stub

		try {

			int headLen = message[3] & 0xFF;
			byte[] bzHead = new byte[headLen];

			System.arraycopy(message, 0, bzHead, 0, 28);

			if (mRangeMap == null)
				mRangeMap = new HashMap<Integer, RangeEntity>();
			else
				mRangeMap.clear();

			short msgID = (short) BitConverter.toUInt16(bzHead, 4);

			if (msgID != 140)
				return;

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

				byte tempCno = messageInfo[iPos + 24];
				int snrNo = ((tempCno >> 5) & 0x1F) + 20;

				if (prnNext == prn) {
					mRangeEntity.setL2(snrNo);
				} else {
					mRangeEntity.setL1(snrNo);
				}

				iPos += 24;

				prnNext = prn;

				mRangeMap.put(prn, mRangeEntity);
			}

			if (mReceiveRangeListner != null)
				mReceiveRangeListner.TellReceiveRange(mRangeMap);

		} catch (Exception e) {
			// TODO: handle exception

		}
	}

	@Override
	public void setReceiveGGAListener(ReceiveGGAListner listener) {
		// mReceiveGGAListner = listener;
		mReceiveGGAListner = listener;
	}

	@Override
	public ReceiveGGAListner getReceiveGGAListener() {
		return mReceiveGGAListner;
	}

	@Override
	public void removeGgaListener(ReceiveGGAListner listener) {
		mReceiveGGAListner = null;
	}

	@Override
	public void setReceiveGSAListener(ReceiveGSAListner listener) {
		mReceiveGSAListner = listener;
	}

	@Override
	public void setReceiveGGAMessageListener(ReceiveGGAMessageListner listener) {
		mReceiveGGAMessageListner = listener;
	}

	@Override
	public void setReceiveVTGListener(ReceiveVTGListner listener) {
		mReceiveVTGListner = listener;
	}

	@Override
	public void setReceiveRMCListener(ReceiveRMCListner listener) {
		mReceiveRMCListner = listener;
	}

	@Override
	public void setReceiveGSVListener(ReceiveGSVListner listener) {
		mReceiveGSVListner = listener;
	}

	@Override
	public void setReceiveTrackStatListener(ReceiveTrackStatListner listener) {
		mReceiveTrackStatListner = listener;
	}

	@Override
	public void setReceiveTrimble40hListener(ReceiveTrimble40hListener listener) {
		mReceie40hListener = listener;
	}

	@Override
	public void setReceiveZDAListener(ReceiveZDAListner listener) {
		mReceieZDAListener = listener;
	}

	@Override
	public void setReceiveRangeListener(ReceiveRangeListner listener) {
		mReceiveRangeListner = listener;
	}

	public void stopSerialPort() {
		mReceiveSendWraper.Stop();
		mReceiveSendWraper.setFlag(false);
	}

	@Override
	public void setReceiveSATVISListener(ReceiveSATVISListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void parseBestvela(byte[] entireMessage) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setReceiveBestvelaListener(ReceiveBestvelaListner listener) {
		// TODO Auto-generated method stub
	}

	@Override
	public void setReceiveBaseStationListener(ReceiveBaseStationListner listener) {
		mReceiveBaseStationListener = listener;

	}

	public boolean verification(byte[] message) {
		if (message.length < 10)
			return false;
		if (message[6] != message.length - 10)
			return false;
		int sum = 0;
		for (int i = 3; i < message.length - 2; i++) {
			sum += message[i];
		}
		String tes = Integer.toHexString(sum);
		if (tes.length() >= 2) {
			tes = tes.substring(tes.length() - 2, tes.length());
		} else {
			tes = "0" + tes;
		}

		if (Integer.parseInt(tes, 16) != message[message.length - 2]
				&& message[message.length - 1] != (byte) (0x0d)) {
			return false;
		} else {
			return true;
		}
	}

	public static String bytesToHexString(byte[] src, int size) {
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || size <= 0) {
			return null;
		}
		for (int i = 0; i < size; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
			stringBuilder.append(" ");
		}
		return stringBuilder.toString();
	}

	public String InttoDouble(String i) {
		if (!i.contains("."))
			i += ".0";
		return i;
	}

	int zcby_c = 0;

	@Override
	public void parseZcby(byte[] message) {
		Boolean check = VerifyHelper.X8ORVerify(message);
		if (check == false) {
			// 测试代码
			mCalfalse++;
			System.out.println(" 校验不通过计数:：" + Integer.toString(mCalfalse));
			return;
		}

		// TODO Auto-generated method stub
		String data_S = "";

		try {
			data_S = new String(message, "GB2312");

			/*
			 * if (mZDAEntity.getUtcDate().getYear() > 110 &&
			 * mZDAEntity.getUtcDate().getYear() < 210) { zcby_c++; if (zcby_c >
			 * 1000) zcby_c = 0; else if (zcby_c % 3 == 0)
			 *
			 * LogHelper.recordNmea_z(data_S, mZDAEntity.getUtcDate()); }
			 */
			data_S = data_S.replaceAll("nan", "0");
			String data_p[] = data_S.split(",");
			// if(data_p.length!=19) return;

			// if(!data_p[18].contains("0*00")) return;
			mZCBYEntity = new ZCBYEntity();
			mZCBYEntity.setDeviation(Double.parseDouble(data_p[1]));
			mZCBYEntity.setDirectiondifference(Double.parseDouble(data_p[2]));
			mZCBYEntity.setVoltage(Double.parseDouble(data_p[3]));
			mZCBYEntity.setElectric(Double.parseDouble(data_p[4]) / 1000);
			mZCBYEntity.setRadioPower(Double.parseDouble(data_p[5]));
			mZCBYEntity.setHeading(Double.parseDouble(data_p[6]));
			mZCBYEntity.setLevel(Double.parseDouble(data_p[7]));
			mZCBYEntity.setSpeed(Double.parseDouble(data_p[8]));
			int State = Integer.parseInt(data_p[9]);
			mZCBYEntity.setHeadingState(State / 10);
			mZCBYEntity.setGPSState(State % 10);
			mZCBYEntity.setSatellitenumber(Integer.parseInt(data_p[10]));
			mZCBYEntity.setDifferentialage(Double.parseDouble(data_p[11]));
			mZCBYEntity.setB(Double.parseDouble(data_p[12]));
			mZCBYEntity.setL(Double.parseDouble(data_p[13]));
			mZCBYEntity.setTime(Double.parseDouble(data_p[14]));
			mZCBYEntity.setWorkon(Integer.parseInt(data_p[15]));
			mZCBYEntity.setmErrorType_s(Integer.parseInt(data_p[16], 16));
			int errortype = Integer.parseInt(data_p[16], 16);

			boolean[] go = new boolean[10];

			if ((errortype & 0x0002) == 0x0002)
				go[0] = true;
			if ((errortype & 0x0004) == 0x0004)
				go[1] = true;
			if ((errortype & 0x0008) == 0x0008)
				go[2] = true;
			if ((errortype & 0x0010) == 0x0010)
				go[3] = true;
			if ((errortype & 0x0020) == 0x0020)
				go[4] = true;
			if ((errortype & 0x0040) == 0x0040)
				go[5] = true;
			if ((errortype & 0x0080) == 0x0080)
				go[6] = true;
			if ((errortype & 0x0100) == 0x0100)
				go[7] = true;
			if ((errortype & 0x0200) == 0x0200)
				go[8] = true;
			if ((errortype & 0x0400) == 0x0400)
				go[9] = true;
			if ((errortype & 0x0800) == 0x0800)
				mZCBYEntity.setmRV(true);
			mZCBYEntity.setErrorType(go);
			if (mReceiveZcbyListner != null
					&& Integer.parseInt(data_p[16], 16) >= 0
					&& Integer.parseInt(data_p[16], 16) <= 65535)
				mReceiveZcbyListner.TellReceiveZcby(mZCBYEntity, data_S);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	int zcbydate_c = 0;

	@Override
	public void parseZcbyDate(byte[] message) {
		Boolean check = VerifyHelper.X8ORVerify(message);
		if (check == false) {
			// 测试代码
			mCalfalse++;
			System.out.println(" 校验不通过计数:：" + Integer.toString(mCalfalse));
			return;
		}
		// TODO Auto-generated method stub
		String data_S = "";
		try {
			data_S = new String(message, "GB2312");

			data_S = data_S.replaceAll("nan", "0");
			String data_p[] = data_S.split(",");
			// if(data_p.length!=19) return;

			if (mZDAEntity.getUtcDate().getYear() > 110
					&& mZDAEntity.getUtcDate().getYear() < 210) {
			}

			// if(!data_p[18].contains("0*00")) return;
			mZCBYDateEntity = new ZCBYDateEntity();
			mZCBYDateEntity.setGX(Double.parseDouble(data_p[1]));
			mZCBYDateEntity.setGY(Double.parseDouble(data_p[2]));
			mZCBYDateEntity.setGZ(Double.parseDouble(data_p[3]));
			mZCBYDateEntity.setAx(Double.parseDouble(data_p[4]));
			mZCBYDateEntity.setAy(Double.parseDouble(data_p[5]));
			mZCBYDateEntity.setAz(Double.parseDouble(data_p[6]));
			mZCBYDateEntity.setRoll(Double.parseDouble(data_p[7]));
			mZCBYDateEntity.setPitch(Double.parseDouble(data_p[8]));
			mZCBYDateEntity.setYaw(Double.parseDouble(data_p[9]));
			mZCBYDateEntity.setTargetangle(Double.parseDouble(data_p[10]));
			mZCBYDateEntity.setMeasurement(Double.parseDouble(data_p[11]));
			mZCBYDateEntity.setCalculationangle(Double.parseDouble(data_p[12]));
			mZCBYDateEntity.setB(Double.parseDouble(data_p[13]));
			mZCBYDateEntity.setL(Double.parseDouble(data_p[14]));
			mZCBYDateEntity.setX(Double.parseDouble(data_p[15]));
			mZCBYDateEntity.setY(Double.parseDouble(data_p[16]));
			mZCBYDateEntity.setXab(Double.parseDouble(data_p[17]));
			mZCBYDateEntity.setYab(Double.parseDouble(data_p[18]));
			mZCBYDateEntity.setHeading1(Double.parseDouble(data_p[19]));
			mZCBYDateEntity.setHeading2(Double.parseDouble(data_p[20]));
			mZCBYDateEntity.setHeight(Double.parseDouble(data_p[21]));
			mZCBYDateEntity.setHDOP(Double.parseDouble(data_p[22]));
			mZCBYDateEntity.setRaw(Double.parseDouble(data_p[23]));
			mZCBYDateEntity.setTurnSpeed(Double.parseDouble(data_p[24]));

			if (mReceiveZcbyDateListner != null)
				mReceiveZcbyDateListner
						.TellReceiveZcby(mZCBYDateEntity, data_S);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void setReceiveZcbyListener(ReceiveZcbyListner listener) {
		// TODO Auto-generated method stub
		mReceiveZcbyListner = listener;
	}

	@Override
	public void setReceiveZcbyDateListener(ReceiveZcbyDateListner listener) {
		// TODO Auto-generated method stub
		mReceiveZcbyDateListner = listener;
	}

	@Override
	public void parseMB2(byte[] message) {
		// TODO Auto-generated method stub
		try {
			if (message.length < 17 && message.length > 800)
				return;

			int a = message[1] & 0xff;
			int b = message[2] & 0xff;
			int version = (int) (message[5] >> 5);

			int position_p = (int) (message[7] & 0x03);
			int[] GPSlenght = GpsHelper.getGpsType(message, 8);

			int now = 0;

			int[] messagetobyte = GpsHelper.returnBYTE(message, 13);
			for (int i = 0; i < 8; i++) {
				if (GPSlenght[i] == 0)
					continue;
				if (now > messagetobyte.length)
					return;
				else {
					int Resolution = messagetobyte[now + 13];
					int[] temp = GpsHelper
							.getNcellnum(message, 13 + 2, version);

					int[] Nsat;
					int Nsig = 0;
					int CellMask = 0;
					int l2 = 0;
					int[] l2_s;

					if (version == 2) {
						Nsat = GpsHelper.reOneNsat(messagetobyte, now + 16, 64,
								i);
						Nsig = GpsHelper.getOnenum(messagetobyte,
								now + 16 + 64, 32);
						l2 = GpsHelper
								.reL2(messagetobyte, now + 16 + 64, 32, i);
						CellMask = GpsHelper.getOnenum(messagetobyte,
								now + 96 + 16, Nsat.length * Nsig);
						l2_s = GpsHelper.reL2_s(messagetobyte, now + 96 + 16,
								Nsat.length * Nsig, i, l2, Nsat.length, Nsig);
						now += (16 + 96 + Nsat.length * Nsig);
					} else {
						Nsat = GpsHelper.reOneNsat(messagetobyte, now + 16, 40,
								i);
						Nsig = GpsHelper.getOnenum(messagetobyte,
								now + 16 + 40, 24);
						l2 = GpsHelper
								.reL2(messagetobyte, now + 16 + 40, 24, i);
						CellMask = GpsHelper.getOnenum(messagetobyte,
								now + 72 + 16, Nsat.length * Nsig);
						l2_s = GpsHelper.reL2_s(messagetobyte, now + 72 + 16,
								Nsat.length * Nsig, i, l2, Nsat.length, Nsig);
						now += (16 + 72 + Nsat.length * Nsig);
					}

					now += (50 * Nsat.length);
					if (Resolution == 0)
						now += (39) * CellMask;
					else
						now += (52) * CellMask;
					String ahi = "";
					for (int j = 0; j < Nsat.length; j++) {
						ahi += Nsat[j] + "xx" + l2_s[j] + ";";

					}
					if (mGsvMap != null) {
						for (int j = 0; j < Nsat.length; j++) {

							SatelliteEntity abc = mGsvMap.get(Nsat[j]);
							if (abc != null) {
								int l2get = GpsHelper.getL2(messagetobyte, now,
										l2_s[j], Resolution);
								if (i != 1
										&& Math.abs(abc.getSnrL1() - l2get) < 10)
									abc.setSnrL2(l2get);

							}

						}

					}
					if (Resolution == 0)
						now += (62) * CellMask;
					else
						now += (74) * CellMask;
					Log.v("测试230", i + ";" + l2 + ":::" + ahi + "." + now + "."
							+ messagetobyte.length);

				}
			}
			mGsvEntity.setGsvMap(mGsvMap);
			if (mReceiveGSVListner != null)
				mReceiveGSVListner.TellReceiveGSV(mGsvEntity);

		} catch (Exception e) {
		}
	}

	@Override
	public void parseRASHR(byte[] message) {
		// TODO Auto-generated method stub
		Boolean check = VerifyHelper.X8ORVerify(message);
		if (check == false) {
			// 测试代码
			mCalfalse++;
			System.out.println(" 校验不通过计数:：" + Integer.toString(mCalfalse));
			return;
		}
		// TODO Auto-generated method stub
		String data_S = "";
		data_S = new String(message);
		String data_p[] = data_S.split(",");
		Log.v("升级", data_S);
		if (data_p[1].contains("NAK")) {
			mReceiveNAKListner.TellReceiveRAK(true);
		}
		if (data_S.charAt(11) == '5') {
			if (mReceiveRASHRListner != null)
				mReceiveRASHRListner.TellReceivePASHR(true);
		}

	}

	@Override
	public void setReceivePASHRListener(ReceivePASHRListner listener) {
		// TODO Auto-generated method stub
		mReceiveRASHRListner = listener;
	}

	@Override
	public void parseReadcar(byte[] message) {
		// TODO Auto-generated method stub
		Boolean check = VerifyHelper.X8ORVerify(message);
		if (check == false) {
			// 测试代码
			mCalfalse++;
			System.out.println(" 校验不通过计数:：" + Integer.toString(mCalfalse));
			return;
		}
		// TODO Auto-generated method stub
		String data_S = "";
		data_S = new String(message);
		String data_p[] = data_S.split(",");
		mReadcarEntity = new ReadcarEntity();
		mReadcarEntity.setWheelBase(Float.parseFloat(data_p[1]));
		mReadcarEntity.setHeight(Float.parseFloat(data_p[2]));
		mReadcarEntity.setForeOrAftValue(Float.parseFloat(data_p[3]));
		mReadcarEntity.setLateralOffset(Float.parseFloat(data_p[4]));
		mReadcarEntity.setCarstye((int) Float.parseFloat(data_p[5]));
		mReadcarEntity.setDirection((int) Float.parseFloat(data_p[6]));
		if (mReceiveReadcarListner != null)
			mReceiveReadcarListner.TellReceiveReader(mReadcarEntity);
	}

	@Override
	public void setReceiveReadcarListener(ReceiveReadcarListner listener) {
		// TODO Auto-generated method stub
		mReceiveReadcarListner = listener;
	}

	@Override
	public void parseReaduhf(byte[] message) {
		// TODO Auto-generated method stub
		Boolean check = VerifyHelper.X8ORVerify(message);
		if (check == false) {
			// 测试代码
			mCalfalse++;
			System.out.println(" 校验不通过计数:：" + Integer.toString(mCalfalse));
			return;
		}
		// TODO Auto-generated method stub
		String data_S = "";
		data_S = new String(message);
		String data_p[] = data_S.split(",");
		mReaduhfEntity = new ReaduhfEntity();
		mReaduhfEntity.setChannelValue((int) Float.parseFloat(data_p[1]));
		mReaduhfEntity.setBaudrateValue((int) Float.parseFloat(data_p[2]));

		if (mReceiveReaduhfListner != null)
			mReceiveReaduhfListner.TellReceiveReader(mReaduhfEntity);
	}

	@Override
	public void setReceiveReaduhfListener(ReceiveReaduhfListner listener) {
		// TODO Auto-generated method stub
		mReceiveReaduhfListner = listener;
	}

	@Override
	public void parseVersion(byte[] message) {
		// TODO Auto-generated method stub
		Boolean check = VerifyHelper.X8ORVerify(message);
		if (check == false) {
			// 测试代码
			mCalfalse++;
			System.out.println(" 校验不通过计数:：" + Integer.toString(mCalfalse));
			return;
		}
		// TODO Auto-generated method stub
		String data_S = "";
		data_S = new String(message);
		Log.v("测试234", data_S);
		String data_p[] = data_S.split(",");
		mVersionEntity = new VersionEntity();
		mVersionEntity.setVersion(data_p[2]);
		mVersionEntity.setID(data_p[3]);
		Date d = new Date();
		try {
			d = new SimpleDateFormat("yyyyMMdd").parse(data_p[4]);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mVersionEntity.setDate(d);
		if (mReceiveVersionListner != null)
			mReceiveVersionListner.TellReceiveVersion(mVersionEntity);
	}

	@Override
	public void setReceiveVersionListener(ReceiveVersionListner listener) {
		// TODO Auto-generated method stub
		mReceiveVersionListner = listener;

	}

	@Override
	public void parseReadZXZY(byte[] message) {
		// TODO Auto-generated method stub
		Boolean check = VerifyHelper.X8ORVerify(message);
		if (check == false) {
			// 测试代码
			mCalfalse++;
			System.out.println(" 校验不通过计数:：" + Integer.toString(mCalfalse));
			return;
		}
		// TODO Auto-generated method stub
		String data_S = "";
		data_S = new String(message);
		String data_p[] = data_S.split(",");
		if (mReceiveReadZXYListner != null)
			mReceiveReadZXYListner.TellReceiveReadZXZY(Integer
					.parseInt(data_p[1]));
	}

	@Override
	public void setReceiveReadZXZYListener(ReceiveReadZXZYListner listener) {
		// TODO Auto-generated method stub
		mReceiveReadZXYListner = listener;
	}

	@Override
	public void parseReadSen(byte[] message) {
		// TODO Auto-generated method stub
		Boolean check = VerifyHelper.X8ORVerify(message);
		if (check == false) {
			// 测试代码
			mCalfalse++;
			System.out.println(" 校验不通过计数:：" + Integer.toString(mCalfalse));
			return;
		}
		// TODO Auto-generated method stub
		String data_S = "";
		data_S = new String(message);
		String data_p[] = data_S.split(",");
		mReadSenEntity = new ReadSenEntity();
		if (data_p[0].contains("0")) {
			mReadSenEntity.setType(0);
		} else {
			mReadSenEntity.setType(1);
		}
		mReadSenEntity.setLine(Float.parseFloat(data_p[1]));
		mReadSenEntity.setTurn(Float.parseFloat(data_p[2]));
		try {
			mReadSenEntity.setBack(Float.parseFloat(data_p[3]));
		} catch (Exception e) {

		}
		if (mReceiveReadSenListner != null)
			mReceiveReadSenListner.TellReceiveReadSen(mReadSenEntity);
	}

	@Override
	public void setReceiveReadSenListener(ReceiveReadSenListner listener) {
		// TODO Auto-generated method stub
		mReceiveReadSenListner = listener;
	}





	@Override
	public void setReceiveNAKListener(ReceiveNAKListner listener) {
		// TODO Auto-generated method stub
		mReceiveNAKListner = listener;
	}

	@Override
	public void parseTB2Version(byte[] message) {
		// TODO Auto-generated method stub
		String data_S = "";
		data_S = new String(message);
		String data_p[] = data_S.split(" ");

		if (mReceiveTB2VersionListner != null)
			mReceiveTB2VersionListner.TellReceiveTB2Version(data_p[1].replace(
					"\r\n", ""));
	}

	@Override
	public void setReceiveTB2VersionListener(ReceiveTB2VersionListner listener) {
		// TODO Auto-generated method stub
		mReceiveTB2VersionListner = listener;
	}

	@Override
	public void parseABangle(byte[] message) {
		// TODO Auto-generated method stub
		Boolean check = VerifyHelper.X8ORVerify(message);
		if (check == false) {
			// 测试代码
			mCalfalse++;
			System.out.println(" 校验不通过计数:：" + Integer.toString(mCalfalse));
			return;
		}
		// TODO Auto-generated method stub

		if (ABangleVersionListner != null)
			ABangleVersionListner.TellReceiveABangle(true);

	}

	@Override
	public void parseECU(byte[] message) {
		Boolean check = VerifyHelper.X8ORVerify(message);
		String abc = new String(message);
		Log.e("发送","发送"+abc);
		if (check == false) {
			// 测试代码
			Log.e("发送","发送"+abc);
			mCalfalse_1++;
			if(mCalfalse_1>10)
			{
				mCalfalse_1=0;


			}
			return;
		}
		if (mReceiveECUListner != null)
			mReceiveECUListner.TellReceiveECU(true);
	}

	@Override
	public void setReceiveECUListener(ReceiveECUListner listener) {
		mReceiveECUListner=listener;
	}
	@Override
	public void setReceiveABangleListener(ReceiveABangleListner listener) {
		// TODO Auto-generated method stub
		ABangleVersionListner = listener;
	}

	@Override
	public void parseDeadZone(byte[] message) {
		// TODO Auto-generated method stub
		Boolean check = VerifyHelper.X8ORVerify(message);
		if (check == false) {
			// 测试代码
			mCalfalse++;
			System.out.println(" 校验不通过计数:：" + Integer.toString(mCalfalse));
			return;
		}
		// TODO Auto-generated method stub
		String data_S = "";
		data_S = new String(message);
		String data_p[] = data_S.split(",");
		try {
			if (mReceiveDeadZoneListner != null)
				mReceiveDeadZoneListner.TellReceiveReadDeadZone(Integer
						.parseInt(data_p[1]));
		} catch (Exception e) {

		}

	}

	@Override
	public void setReceiveReadDeadZoneListener(
			ReceiveReadDeadZoneDateListner listener) {
		// TODO Auto-generated method stub
		mReceiveDeadZoneListner = listener;
	}

	@Override
	public void parseReadPid(byte[] message) {
		// TODO Auto-generated method stub
		Boolean check = VerifyHelper.X8ORVerify(message);
		if (check == false) {
			// 测试代码
			mCalfalse++;
			System.out.println(" 校验不通过计数:：" + Integer.toString(mCalfalse));
			return;
		}
		// TODO Auto-generated method stub
		String data_S = "";
		data_S = new String(message);
		String data_p[] = data_S.split(",");
		PidEntity abc = new PidEntity();
		abc.s1 = Float.parseFloat(data_p[1]);
		abc.s2 = Float.parseFloat(data_p[2]);
		abc.s3 = Float.parseFloat(data_p[3]);
		abc.s4 = Float.parseFloat(data_p[4]);
		abc.s5 = Float.parseFloat(data_p[5]);
		abc.s6 = Float.parseFloat(data_p[6]);
		abc.s7 = Float.parseFloat(data_p[7]);
		abc.s8 = Float.parseFloat(data_p[8]);
		abc.s9 = Float.parseFloat(data_p[9]);

		if (mReceivereadpidListner != null)
			mReceivereadpidListner.TellReceivereadpid(abc);
	}

	@Override
	public void setReceiveReadPidListener(ReceivereadpidListner listener) {
		// TODO Auto-generated method stub
		mReceivereadpidListner = listener;
	}

	@Override
	public void parseAGRICA(byte[] message) {
		// TODO Auto-generated method stub
		// Boolean check = VerifyHelper.X8ORVerify(message);
		/*
		 * if (check == false) { // 测试代码 mCalfalse++;
		 * System.out.println(" 校验不通过计数:：" + Integer.toString(mCalfalse));
		 * return; }
		 */
		// TODO Auto-generated method stub
		String data_S = "";
		data_S = new String(message);
		String data_p[] = data_S.split(";");
		String data_d[] = data_p[1].split(",");
		AGRICEntity abc = new AGRICEntity();
		abc.setB(Double.parseDouble(data_d[41]));
		abc.setL(Double.parseDouble(data_d[42]));
		if (mReceiveAGRICAListner != null)
			mReceiveAGRICAListner.TellReceiveAGRICA(abc);
	}

	@Override
	public void setReceiveAGRICAleListener(ReceiveAGRICAListner listener) {
		// TODO Auto-generated method stub
		mReceiveAGRICAListner = listener;
	}

	@Override
	public void parseUMVersion(byte[] message) {
		// TODO Auto-generated method stub

		// TODO Auto-generated method stub
		String data_S = "";
		data_S = new String(message);
		String data_p[] = data_S.split(";");
		String data_d[] = data_p[1].split(",");
		String abc = data_d[0].replace("UB482", "") + data_d[1];
		if (mReceiveUMVersionListner != null)
			mReceiveUMVersionListner.TellReceiveUMVersion(abc);
	}

	@Override
	public void setReceiveUMVersionListener(ReceiveUMVersionListner listener) {
		// TODO Auto-generated method stub
		mReceiveUMVersionListner = listener;
	}

	@Override
	public void parseboot(byte[] message) {
		// TODO Auto-generated method stub
		String data_S = "";
		data_S = new String(message);
		if (mReceivebootListner != null)
			mReceivebootListner.TellReceiveboot(true);
	}

	@Override
	public void setReceivebooListener(ReceivebootListner listener) {
		// TODO Auto-generated method stub
		mReceivebootListner = listener;
	}

	@Override
	public void parseInsVersion(byte[] message) {
		// TODO Auto-generated method stub
		Boolean check = VerifyHelper.X8ORVerify(message);
		if (check == false) {
			// 测试代码
			mCalfalse++;
			System.out.println(" 校验不通过计数:：" + Integer.toString(mCalfalse));
			return;
		}
		// TODO Auto-generated method stub
		String data_S = "";
		data_S = new String(message);
		String data_p[] = data_S.split(",");

		if (mReceiveInsVersionListner != null)
			mReceiveInsVersionListner.TellReceiveInsVersion(data_p[2]);
	}

	@Override
	public void setReceiveInsVersionListener(ReceiveInsVersionListner listener) {
		// TODO Auto-generated method stub
		mReceiveInsVersionListner = listener;
	}

	@Override
	public void parseMduVersion(byte[] message) {
		// TODO Auto-generated method stub
		Boolean check = VerifyHelper.X8ORVerify(message);
		if (check == false) {
			// 测试代码
			mCalfalse++;
			System.out.println(" 校验不通过计数:：" + Integer.toString(mCalfalse));
			return;
		}
		// TODO Auto-generated method stub
		String data_S = "";
		data_S = new String(message);
		String data_p[] = data_S.split(",");

		if (mReceiveMduVersionListner != null&&data_p.length>2)
			mReceiveMduVersionListner.TellReceiveMduVersion(data_p[2]);
	}

	@Override
	public void setReceiveMduVersionListener(ReceiveMduVersionListner listener) {
		// TODO Auto-generated method stub
		mReceiveMduVersionListner = listener;
	}

	@Override
	public void parseCorrectIMU(byte[] message) {
		// TODO Auto-generated method stub
		Boolean check = VerifyHelper.X8ORVerify(message);

		// TODO Auto-generated method stub
		String data_S = "";
		data_S = new String(message);
		if (mReceiveCorrectimuListner != null)
			mReceiveCorrectimuListner.TellReceiveCorrectimu(true);
	}

	@Override
	public void setReceiveCorrectIMUListener(ReceiveCorrectimuListner listener) {
		// TODO Auto-generated method stub
		mReceiveCorrectimuListner = listener;
	}

	@Override
	public void parseRegVersion(byte[] message) {
		// TODO Auto-generated method stub
		String data_S = "";
		data_S = new String(message);
		if (mReceiveRegListner != null)
			mReceiveRegListner.TellReceiveReg(true);
	}

	@Override
	public void setReceiveRegistener(ReceiveRegListner listener) {
		// TODO Auto-generated method stub
		mReceiveRegListner = listener;
	}

	@Override
	public void parseZC31(byte[] message) {
		// TODO Auto-generated method stub
		String data_S = "";
		data_S = new String(message);
		Log.e("注册", data_S);
		String data_p[] = data_S.split(",");
		ZC31Entity abc = new ZC31Entity();
		abc.setbaseversion(data_p[1]);
		abc.setbaseFirmwareversion(data_p[2]);
		abc.setbaseRegist(data_p[3]);
		abc.setbaseRegistDate(data_p[4]);
		if (mReceiveZC31Listner != null)
			mReceiveZC31Listner.TellReceiveZDA(abc);
	}

	@Override
	public void setReceiveZC31(ReceiveZC31Listner listener) {
		// TODO Auto-generated method stub
		mReceiveZC31Listner = listener;
	}

	@Override
	public void parseServerOk(byte[] message) {
		// TODO Auto-generated method stub
		String data_S = "";
		data_S = new String(message);
		Log.e("mes11", data_S + "");
		String data_p[] = data_S.split(",");
		if (data_p[1].equals("ok"))

			if (mReceiveServerOkListner != null)
				mReceiveServerOkListner.TellReceiveServerOk();
	}

	@Override
	public void setReceiveServerOk(ReceiveServerOkListner listener) {
		// TODO Auto-generated method stub
		mReceiveServerOkListner = listener;
	}

	@Override
	public void parseSelf(byte[] message) {
		// TODO Auto-generated method stub
		Boolean check = VerifyHelper.X8ORVerify(message);

		// TODO Auto-generated method stub
		String data_S = "";

		data_S = new String(message);
		Log.e("收到了自检", data_S + "");
		String data_p[] = data_S.split(",");
		if (data_p[1].equals("bufone")) {
			SelfOneEntity abc = new SelfOneEntity();

			abc.setself_SN_NUM(data_p[2]);
			abc.setself_version(data_p[3] + "." + data_p[4]);
			abc.setself_cardone_CCID(data_p[5]);
			abc.setself_cardtwo_CCID(data_p[6]);
			abc.setself_cardthree_CCID(data_p[7]);

			abc.setself_ip(data_p[8]);
			abc.setself_port(data_p[9]);

			abc.setself_time(data_p[10]);
			if (mReceiveSelfOneListner != null)
				mReceiveSelfOneListner.TellReceiveSelfone(abc);
		} else if (data_p[1].equals("buftwo")) {
			SelfTwoEntity abc = new SelfTwoEntity();
			abc.setself_server_connect_state(data_p[2]);

			abc.setself_diff_state(data_p[3]);
			abc.setself_server_card(data_p[4]);
			abc.setself_work_mode(data_p[5]);
			abc.setself_EC20_state(data_p[6]);
			abc.setself_SIM_state(data_p[7]);
			abc.setself_forbid_state(data_p[8]);
			abc.setself_GSMreg_state(data_p[9]);
			abc.setself_GPRSreg_state(data_p[10]);
			abc.setself_cardone_signal(data_p[11]);
			abc.setself_cardtwo_signal(data_p[12]);
			abc.setself_cardthree_signal(data_p[13]);
			abc.setself_cardone_delayed(data_p[14]);
			abc.setself_cardtwo_delayed(data_p[15]);
			abc.setself_cardthree_delayed(data_p[16]);
			if (mReceiveSelfTwoListner != null)
				mReceiveSelfTwoListner.TellReceiveSelfTwo(abc);
		} else if (data_p[1].equals("retbuf") && data_p.length > 22) {
			SelfRetEntity abc = new SelfRetEntity();
			abc.setself_SN_NUM(data_p[2]);
			abc.setself_version(data_p[3] + "," + data_p[4]);
			abc.setself_cardone_CCID(data_p[5]);
			abc.setself_cardtwo_CCID(data_p[6]);
			abc.setself_cardthree_CCID(data_p[7]);
			abc.setself_ip(data_p[8]);
			abc.setself_port(data_p[9]);
			abc.setself_time(data_p[10]);
			abc.setself_server_connect_state(data_p[11]);
			abc.setself_diff_state(data_p[12]);
			abc.setself_server_card(data_p[13]);
			abc.setself_work_mode(data_p[14]);
			abc.setself_cardone_sate(data_p[15]);
			abc.setself_cardtwo_sate(data_p[16]);
			abc.setself_cardthree_sate(data_p[17]);
			abc.setself_error(data_p[18]);
			abc.setself_forbid_state(data_p[19]);
			abc.setself_cardone_signal(data_p[20]);
			abc.setself_cardtwo_signal(data_p[21]);
			abc.setself_cardthree_signal(data_p[22]);
			if (data_p.length > 26) {
				abc.setnumber_of_cuts(data_p[23]);
				abc.settesting1(data_p[24]);
				abc.settesting2(data_p[25]);
				abc.settesting3(data_p[26]);
			}
			if (mReceiveSelfRetListner != null)
				mReceiveSelfRetListner.TellReceiveSelfRet(abc);
		}

	}

	@Override
	public void setReceiveSelfRet(ReceiveSelfRetListner listener) {
		// TODO Auto-generated method stub
		mReceiveSelfRetListner = listener;
	}

	@Override
	public void setReceiveSelfOne(ReceiveSelfOneListner listener) {
		// TODO Auto-generated method stub
		mReceiveSelfOneListner = listener;
	}

	@Override
	public void setReceiveSelfTwo(ReceiveSelfTwoListner listener) {
		// TODO Auto-generated method stub
		mReceiveSelfTwoListner = listener;
	}

	@Override
	public void parseServeroff(byte[] message) {
		// TODO Auto-generated method stub
		Log.e("关闭", "关闭");
		String data_S = "";
		data_S = new String(message);
		Log.e("mes11", data_S + "");
		String data_p[] = data_S.split(",");
		if (data_p[1].equals("ok"))

			if (mReceiveServerOffListner != null)
				mReceiveServerOffListner.TellReceiveServeroff();

	}

	@Override
	public void setReceiveServerOff(ReceiveServerOffListner listener) {
		// TODO Auto-generated method stub
		mReceiveServerOffListner = listener;
	}

	@Override
	public void parseServerQXwz(byte[] message) {
		// TODO Auto-generated method stub
		String data_S = "";
		data_S = new String(message);
		Log.e("mes11", data_S + "");
		String data_p[] = data_S.split(",");
		if (data_p[1].equals("ok"))

			if (mReceiveQXwzListner != null)
				mReceiveQXwzListner.TellReceiveQXwz();

	}

	@Override
	public void setReceiveQXwzListner(ReceiveQXwzListner listener) {
		// TODO Auto-generated method stub
		mReceiveQXwzListner = listener;
	}

	@Override
	public void parseOpened(byte[] message) {
		// TODO Auto-generated method stub
		String data_S = "";
		data_S = new String(message);
		Log.e("mes11", data_S + "");

		if (mReceiveOpenedListner != null)
			mReceiveOpenedListner.TellReceiveOpened();
	}

	@Override
	public void setReceiveOpendedListner(ReceiveOpenedListner listener) {
		// TODO Auto-generated method stub
		mReceiveOpenedListner = listener;
	}

	@Override
	public void parseServerSDK(byte[] message) {
		// TODO Auto-generated method stub
		String data_S = "";
		data_S = new String(message);
		Log.e("mes11", data_S + "");
		String data_p[] = data_S.split(",");
		if (data_p[1].equals("on"))

			if (mReceiveSDKListner != null)
				mReceiveSDKListner.TellReceiveServerSDK();

	}

	@Override
	public void setReceiveSDKListner(ReceiveSDKListner listener) {
		// TODO Auto-generated method stub
		mReceiveSDKListner = listener;
	}

	@Override
	public void parseoem(byte[] message) {
		String data_S = "";
		data_S = new String(message);
		String data_p[] = data_S.split(",");

		if (data_p.length >= 4)
			Log.e("版本",data_S);
			if (mReceiveOemListner != null)
				mReceiveOemListner.TellReceiveOEM(data_p[2]);
	}

	@Override
	public void parseBESTPOSA(byte[] message) {
		// TODO Auto-generated method stub


	}

	public static boolean isInteger(String input) {
		Matcher mer = Pattern.compile("^[0-9]+$").matcher(input);
		return mer.find();
	}

	@Override
	public void setReceiveBESTPOSAListner(ReceiveBESTPOSAListner listener) {
		// TODO Auto-generated method stub
		mReceiveBESTPOSAListner = listener;
	}

	@Override
	public void setoemListner(ReceiveOemListner listener) {
		mReceiveOemListner=listener;
	}

	@Override
	public void parseServerNode(byte[] message) {
		// TODO Auto-generated method stub
		String data_S = "";
		data_S = new String(message);
		Boolean check = VerifyHelper.X8ORVerify(message);
		if (check == false) {

			return;
		}
		Log.e("获取源节点", "取");

		if (mReceiveNodeListner != null)
			mReceiveNodeListner.TellReceiveNode(data_S);
	}

	@Override
	public void setReceiveNodeListner(ReceiveNodeListner listener) {
		// TODO Auto-generated method stub
		mReceiveNodeListner = listener;
	}

	@Override
	public void parseMCUVersion(byte[] message) {

		// TODO Auto-generated method stub
		Boolean check = VerifyHelper.X8ORVerify(message);
		if (check == false) {
			// 测试代码
			mCalfalse++;
			System.out.println(" 校验不通过计数:：" + Integer.toString(mCalfalse));
			return;
		}
		String data_S = "";

		data_S = new String(message);
		Log.e("版本", data_S);
		String data_p[] = data_S.split(",");
		if (data_p.length > 3) {
			if (mReceiveMCUVersionListner != null)
				mReceiveMCUVersionListner.TellReceiveMCUVersion(data_p[2]);
		}

	}

	@Override
	public void setReceiveMCUVersionListner(ReceiveMCUVersionListner listener) {
		// TODO Auto-generated method stub
		mReceiveMCUVersionListner = listener;
	}

}
