package com.zhd.gnssmanager;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.sn.control.SNClass;
import com.zhd.serialportmanage.SerialPortManager;
import com.zhd.serialportmanage.SerialPortManager.IReceiveDataListener;

public class ResetMotherBoard {
	private int mBoudrateComfirm = 0;
	private int mBoudrateTemp = 0;
	private GnssDataReceive mGnssDataReceive = null;
	private SerialPortManager mSpManagerCom3 = null;
	private ProgressDialog progressDialog;
	private String mGnssCom3 = "/dev/ttymxc2";
	private int mMotherType = -1;
	private boolean mIsDataCome = false;
	private Boolean mflag = false;
	private static Context mContext = null;

	private static ResetMotherBoard RESET_MOTHERBORD_INSTANCE = null;

	public void setmMotherType(int mMotherType,String ComName) {
		mGnssCom3=	ComName;
		this.mMotherType = mMotherType;

	}

	public void setmFlag(Boolean flag) {
		this.mflag = flag;
	}

	public ResetMotherBoard(Context context) {
		mContext = context;
		checkMotherboard();

		// mMotherType=2;

		mGnssDataReceive = GnssDataReceive.getInstance(mMotherType,mGnssCom3,115200);
	}

	public static ResetMotherBoard getInstance(Context context) {

		if (RESET_MOTHERBORD_INSTANCE == null || !mContext.equals(context))
			RESET_MOTHERBORD_INSTANCE = new ResetMotherBoard(context);

		return RESET_MOTHERBORD_INSTANCE;
	}

	public int getmMotherType() {
		return mMotherType;
	}

	public void resetMotherboard() {
		if (mflag) {
			try {
				if (mMotherType == -1) {
					AleartDialogHelper.aleartDialog(mContext,
							R.string.debugging_NullMainboard);
					return;
				}
				// mBoardStyle = (mspBoardStyle.getSelectedItem()).toString();

				progressDialog = new ProgressDialog(mContext);
				progressDialog.setTitle(mContext.getString(R.string.Notice));
				progressDialog.setMessage(mContext
						.getString(R.string.debugging_mainboard_reset));
				progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				progressDialog.setCancelable(false);

				new resetAsyncTask().execute(mMotherType);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * 主板发送复位命令
	 *
	 * @return true表示复位成功，false表示复位失败
	 */
	protected void resetMotherboard(int Baudrate, int MotherType) {
		mGnssDataReceive.reWriteSerialPort(new SerialPortManager(Baudrate,
				mGnssCom3,3));
		mSpManagerCom3 = mGnssDataReceive.getCom3GpsSerialPort();

		if (mSpManagerCom3 == null)
			return;

		if (MotherType == GnssEnum.MotherBoardEnum.ZDT810.getValue()
				&& MotherType == GnssEnum.MotherBoardEnum.ZDT820.getValue()) {

			try {

				byte[] reset = { 0x02, 0x00, 0x6D, 0x02, 0x00, 0x00, 0x6F, 0x03 };
				mSpManagerCom3.Send(reset);
				Thread.sleep(50);

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		else if (MotherType == GnssEnum.MotherBoardEnum.ZDU820.getValue()
				|| MotherType == 2) {
			try {

				byte[] reset = "freset".getBytes();
				mSpManagerCom3.Sendln(reset);
				Thread.sleep(50);

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * COM3口波特率变为115200
	 *
	 * @return mBoudrateComfirm
	 */
	private void ChangeBaudrateTo115200(int Baudrate, int Motherstyle) {
		mGnssDataReceive.reWriteSerialPort(new SerialPortManager(Baudrate,
				mGnssCom3,3));
		mSpManagerCom3 = mGnssDataReceive.getCom3GpsSerialPort();

		if (mSpManagerCom3 == null)
			return;
		if (Motherstyle == GnssEnum.MotherBoardEnum.ZDT810.getValue()
				|| Motherstyle == GnssEnum.MotherBoardEnum.ZDT820.getValue()) {

			try {

				byte[] COM3BaudrateTo115200Cmd = { 0x02, 0x00, 0x64, 0x0D,
						0x00, 0x00, 0x00, 0x03, 0x00, 0x01, 0x00, 0x02, 0x04,
						0x02, 0x07, 0x00, 0x00, (byte) 0x84, 0x03 };
				mSpManagerCom3.Send(COM3BaudrateTo115200Cmd);
				Thread.sleep(50);

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		else if (Motherstyle == GnssEnum.MotherBoardEnum.ZDU820.getValue()
				|| Motherstyle == 2) {
			try {

				byte[] COM3BaudrateTo115200Cmd = "com com3 115200".getBytes();
				mSpManagerCom3.Sendln(COM3BaudrateTo115200Cmd);
				Thread.sleep(50);
				mSpManagerCom3.Sendln(COM3BaudrateTo115200Cmd);
				Thread.sleep(50);

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 复位成功后初始化串口
	 *
	 * @return mBoudrateComfirm
	 */
	public void initCom(int boardstyle) {
		// TODO Auto-generated method stub
		// 初始化数据接收
		mGnssDataReceive.finalzeSerialPort();
		mGnssDataReceive = GnssDataReceive.getInstance(boardstyle,mGnssCom3,115200);
		// mGnssDataReceive.reWriteSerialPort(new SerialPortManager(115200,
		// mGnssCom3));
		mGnssDataReceive.iniCommand(null);
		mGnssDataReceive.startRecieve();
	}

	/**
	 * 判读主板COM3口波特率
	 *
	 * @return mBoudrateComfirm
	 */
	private int judgebaudrate(int boardstyle) {

		int[] baudrates = { 115200, 38400, 9600, 19200, 57600, 230400 };
		mBoudrateComfirm = 0;
		mBoudrateTemp = 0;

		try {

			for (int baudrate : baudrates) {
				if (mBoudrateComfirm != 0) {
					System.out.println("波特率检测为：" + mBoudrateComfirm);
					return mBoudrateComfirm;
				}

				System.out.println("测试：" + baudrate);

				mGnssDataReceive.stop();

				mGnssDataReceive.reCreateSerialPort(new SerialPortManager(
						baudrate, mGnssCom3,3));
				mBoudrateTemp = baudrate;

				Log.i("reset", "rewriteserialport");

				// mGnssDataReceive.getCom3GpsSerialPort().setReceiveMessageListener(
				// mSerialPortDataListener);

				byte[] boardInfoCmdTrimble = { 0x02, 0x00, 0x06, 0x00, 0x06,
						0x03 };
				byte[] boardInfoCmdUnicore = "LOG VERSIONA ONCE".getBytes();

				// 天宝主板
				if (boardstyle == GnssEnum.MotherBoardEnum.ZDT810.getValue()
						|| boardstyle == GnssEnum.MotherBoardEnum.ZDT820
						.getValue()) {
					byte[] clearCommand = { 0x02, 0x00, 0x64, 0x0D, 0x00, 0x00,
							0x00, 0x03, 0x00, 0x01, 0x00, 0x07, 0x04, 0x00,
							0x00, 0x00, 0x00, (byte) 0x80, 0x03 };
					mGnssDataReceive.sendByte(clearCommand);

					Thread.sleep(300);

					mGnssDataReceive.sendByte(boardInfoCmdTrimble);

					Thread.sleep(300);
				}
				// ZDU820主板
				else if (boardstyle == GnssEnum.MotherBoardEnum.ZDU820
						.getValue()
						|| boardstyle == GnssEnum.MotherBoardEnum.ZDU810
						.getValue()) {
					byte[] clearCommand = "unlogall true".getBytes();
					mGnssDataReceive.sendln(clearCommand);

					Thread.sleep(200);

					mGnssDataReceive.sendln(boardInfoCmdUnicore);

					Thread.sleep(3000);
				}

				int i = 0;
				mIsDataCome = false;
				while (!mIsDataCome) {
					if (i > 1)
						break;

					else {
						if (boardstyle == GnssEnum.MotherBoardEnum.ZDT810
								.getValue()
								|| boardstyle == GnssEnum.MotherBoardEnum.ZDT820
								.getValue()) {
							mGnssDataReceive.sendByte(boardInfoCmdTrimble);

							Thread.sleep(200);
						} else {
							mGnssDataReceive.sendln(boardInfoCmdUnicore);
						}
					}

					// *********************************
					Log.i("reset", Integer.toString(baudrate));

					InputStream mInputStream = mGnssDataReceive
							.getCom3GpsSerialPort().getSerialPort()
							.getInputStream();

					if (mInputStream.available() > 0) {
						byte[] buffer = new byte[2048];
						int readLen = mInputStream.read(buffer);

						if (readLen > 0) {
							byte[] data = new byte[readLen];

							System.arraycopy(buffer, 0, data, 0, readLen);

							IsOrNotRecallMsg(data);

							mIsDataCome = true;
						}
					}
					mInputStream.close();
					i++;

					Thread.sleep(100);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		// mGnssDataReceive.getCom3GpsSerialPort().setReceiveMessageListener(null);

		return mBoudrateComfirm;

	}

	IReceiveDataListener mSerialPortDataListener = new IReceiveDataListener() {

		@Override
		public void handleReceiveMessage(byte[] data) {

			IsOrNotRecallMsg(data);

		}

		@Override
		public void handleReceiveMessage1(int com, byte[] messge) {
			// TODO Auto-generated method stub

		}
	};

	/**
	 * 判断主板的返回信息里是否返回固定信息
	 *
	 *
	 */
	protected void IsOrNotRecallMsg(byte[] data) {
		if (mBoudrateComfirm != 0)
			return;
		try {
			String outMsg = new String(data, "UTF-8");

			System.out.println(outMsg);

			if (mMotherType == GnssEnum.MotherBoardEnum.ZDT810.getValue()
					|| mMotherType == GnssEnum.MotherBoardEnum.ZDT820
					.getValue()) {
				System.out.println("检查波特率：" + mBoudrateTemp);
				if (outMsg.contains("BD")) {

					mBoudrateComfirm = mBoudrateTemp;
				}
			}

			else if (mMotherType == GnssEnum.MotherBoardEnum.ZDU820.getValue()
					|| mMotherType == 2) {

				if (outMsg.contains("OK")) {
					System.out.println("检查波特率：" + mBoudrateTemp);
					mBoudrateComfirm = mBoudrateTemp;
				}
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取平板电脑类型
	 */
	// ZDN810WA("081", "ZDN810(WA)"), ZDN810CA("082", "ZDN810(CA)"),
	// ZDT820WA("083", "ZDT820(WA)"), ZDT820CA("084", "ZDT820(CA)"),
	// ZDT810WA("085", "ZDT810(WA)"), ZDT810CA("086", "ZDT810(CA)"),
	// ZDH810WA("087", "ZDH810(WA)"), ZDH810CA("088", "ZDH810(CA)"),
	// ZDU810WA("089", "ZDU810(WA)"), ZDU810CA("090", "ZDU810(CA)"),
	// ZDC810WA("091", "ZDC810(WA)"), ZDC810CA("092", "ZDC810(CA)"),
	// ZD800WA("093", "ZD800(WA)"), ZD800CA("094", "ZD800(CA)"),
	// ZDU820WA("095", "ZDU820(WA)"), ZDU820CA("096", "ZDU820(CA)");
	// T 天宝 N 诺瓦泰 U ZDU820 H 和谐光电
	private void checkMotherboard() {
		try {
			String verifyMessage = "";
			SharedPreferences sharedPreferences = mContext
					.getSharedPreferences("gnss", Activity.MODE_PRIVATE);

			int motherboard = -1;
			motherboard = Helper.getMotherType(mContext);
			byte[] info = new byte[31];
			int isReadSucessNum = SNClass.SNRd(info);

			if (motherboard != -1) {
				mMotherType = motherboard;
				return;
			}

			// 读取失败返回错误
			if (isReadSucessNum == -1) {
			}
			verifyMessage = new String(info, "UTF-8");
			String[] verifyMessageArr = verifyMessage.split(",");
			if (verifyMessageArr.length == 4) {
				int m = Integer.valueOf(verifyMessageArr[1]);
				if (m == 81 || m == 82)
					motherboard = 1;
				else if (m == 83 || m == 84 || m == 85 || m == 86)
					motherboard = 0;
				else if (m == 89 || m == 90 || m == 95 || m == 96)
					motherboard = 2;
			}

			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putString("motherboard", Integer.toString(motherboard));
			editor.commit();

			mMotherType = motherboard;
			mflag = true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 后台处理主板复位，复位完成后用显示框提示结果是否成功
	 *
	 *
	 */
	public class resetAsyncTask extends AsyncTask<Integer, Void, Boolean> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			progressDialog.show();
		}

		@Override
		protected Boolean doInBackground(Integer... boardstyle) {
			// TODO Auto-generated method stub

			int baudrate;
			int mboardstyle = boardstyle[0];
			Boolean resetfBoolean = false;
			try {

				baudrate = judgebaudrate(mboardstyle);
				if (baudrate == 0) {
					System.out.println("识别主板波特率失败");
					return false;
				}
				System.out.println("当前主板波特率检查为：" + baudrate);
				// resetfBoolean = resetMainboard(baudrate, mboardstyle);

				// resetMotherboard(baudrate, mboardstyle);

				if (mboardstyle == GnssEnum.MotherBoardEnum.ZDU820.getValue()
						|| mboardstyle == 2)
					Thread.sleep(5000);

				// 改变波特率为115200
				if (baudrate != 115200) {
					ChangeBaudrateTo115200(baudrate, mboardstyle);

					// 判断是否为115200的波特率
					baudrate = judgebaudrate(mboardstyle);
				}

				if (baudrate == 115200) {

					resetfBoolean = true;

				} else {
					resetfBoolean = false;
				}

				Thread.sleep(1000);

				// 复位成功后初始化串口输出
				if (resetfBoolean) {
					initCom(mboardstyle);
				}

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return resetfBoolean;
		}

		@Override
		protected void onPostExecute(Boolean resetFlag) {
			super.onPostExecute(resetFlag);

			progressDialog.dismiss();

			if (!resetFlag) {
				AleartDialogHelper.aleartDialog(
						mContext,
						mContext.getResources().getString(
								R.string.debugging_reset_failed));
			}

			else {
				AleartDialogHelper.aleartDialog(
						mContext,
						mContext.getResources().getString(
								R.string.debugging_reset_completed));
			}
		}
	}
}
