package com.zhd.democors;

import java.util.concurrent.Executors;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.zhd.zhdcorsnet.FileHelper;
import com.zhd.democors.BackgroundWork;
import com.zhd.democors.R;
import com.zhd.democors.BackgroundWork.IDoWork;
import com.zhd.democors.BackgroundWork.ProgressReportListener;
import com.zhd.democors.BackgroundWork.RunWorkListener;
import com.zhd.democors.BackgroundWork.WorkHandler;

import com.zhd.zhdcorsnet.CorsGprsService;
import com.zhd.zhdcorsnet.NetHelper;
import com.zhd.zhdcorsnet.NetResult;
import com.zhd.zhdcorsnet.ZHDNetGprsService;

public class DiffNetWork {

	public static final int NET_RECEIVED = 1000;
	public static final int REFRESH_UI = 1001;
	public static final int NET_ERROR = 1002;

	public static boolean mIsReConnect = false;
	private int m_progressValue = 0;
	private SharedPreferences m_Preference;
	private String m_NetType;
	private String m_Ip;
	private String m_Port;
	private String m_First;
	private String m_Second;
	private String m_Third;
	private NetHelper m_NetHelper = new NetHelper();
	private Intent mIntent;
	private ProgressDialog m_pDialog;
	private ProgressBar m_proBar;
	public boolean m_IsReturn = false;
	private Context mApplicationContext;
	private Context mCurrentContext;

	private INetWorkListener mNetWorkListener;

	private boolean mIsConnect = false;

	public static DiffNetWork DIFFNETWORK = null;

	public Handler mConnectHandler = new Handler();

	private boolean mIsClose = false;

	private int mResult = 0;

	public DiffNetWork(Context applicationContext, Context currentContext) {
		mApplicationContext = applicationContext;
		mCurrentContext = currentContext;
	}

	public static DiffNetWork getInstantce(Context applicationContext,
										   Context currentContext) {
		if (DIFFNETWORK == null)
			DIFFNETWORK = new DiffNetWork(applicationContext, currentContext);

		return DIFFNETWORK;
	}

	public boolean isNetWorkAvailible() {
		return m_NetHelper.CheckNet(mApplicationContext);
	}

	public boolean isSetting() {
		m_Preference = PreferenceManager
				.getDefaultSharedPreferences(mCurrentContext);

		return getPar();
	}

	public void setConnectHandler(Handler handler) {
		mConnectHandler = handler;
	}

	public synchronized boolean startDiffNetService(ProgressDialog pDialog) {
		try {

			if (!isSetting())
				return false;

			mIsClose = false;

			m_IsReturn = false;

			m_pDialog = pDialog;

			if (pDialog != null) {
				m_pDialog.setTitle(R.string.option);
				m_pDialog.setMessage(mCurrentContext.getResources().getText(
						R.string.connectingServer));
				m_pDialog.setIndeterminate(false);
				m_pDialog.setCancelable(true);
				m_pDialog.show();
			}

			mNetWorkListener.statusChange(mCurrentContext.getString(R.string.connectingServer));

			BackgroundWork backwork = new BackgroundWork();
			backwork.setExec(Executors.newSingleThreadExecutor());
			backwork.setDowork(new IDoWork() {

				@Override
				public void doWork(WorkHandler handler, Object arg) {
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					if (m_NetType.equals("VRS")) {

						mIntent = new Intent(mCurrentContext,
								CorsGprsService.class);
						mIntent.putExtra("ip", m_Ip);
						mIntent.putExtra("port", m_Port);
						mIntent.putExtra("username", m_First);
						mIntent.putExtra("password", m_Second);
						mIntent.putExtra("sourcenode", m_Third);// RTCM2.3"0020028028"
						mIntent.putExtra("reconnect", true);

					} else if (m_NetType.equals("ZHD")) {

						mIntent = new Intent(mCurrentContext,
								ZHDNetGprsService.class);
						mIntent.putExtra("ip", m_Ip);
						mIntent.putExtra("port", m_Port);
						mIntent.putExtra("username", m_First);
						mIntent.putExtra("usergroup", m_Second);
						mIntent.putExtra("workgroup", m_Third);// RTCM2.3"0020028028"
						mIntent.putExtra("reconnect", true);
					}

					mCurrentContext.stopService(mIntent);
					mCurrentContext.startService(mIntent);
					while (!m_IsReturn) {
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}

					}
				}

			});

			backwork.setWorkListener(new RunWorkListener() {

				@Override
				public void started(Object arg) {

				}

				@Override
				public void complete(boolean isCancel, Exception ex,
									 Object result) {
					if (m_pDialog != null)
						m_pDialog.dismiss();

					// mNetWorkListener.statusChange("完成连接");
				}
			});

			backwork.setReport(new ProgressReportListener() {

				@Override
				public void onProgress(int count, String msg, Object obj) {
					if (m_pDialog != null)
						m_pDialog.setMessage(msg);
				}
			});

			backwork.run();

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public void stopDiffNetService() {
		if (m_NetType == null)
			return;

		if (m_NetType.equals("VRS")) {
			mIntent = new Intent(mCurrentContext, CorsGprsService.class);

		} else if (m_NetType.equals("ZHD")) {
			mIntent = new Intent(mCurrentContext, ZHDNetGprsService.class);

		}

		mCurrentContext.stopService(mIntent);

		m_IsReturn = false;
		mIsConnect = false;

		// mIsClose = true;

		if (mConnectHandler != null) {
			Message msg = Message.obtain();
			msg.what = NET_ERROR;
			mConnectHandler.sendMessage(msg);
		}
	}

	public boolean getIsConnceted() {
		return !mIsClose;
	}

	public boolean getIsClose() {
		return mIsClose;
	}

	public void setIsClose(boolean isClose) {
		mIsClose = isClose;
	}

	/**
	 * 获取存储的设置
	 *
	 * @return
	 */
	private boolean getPar() {

		if (m_Preference == null)
			return false;

		m_NetType = m_Preference.getString("nettype", "VRS");
		if (m_NetType == null) {
			return false;
		}

		if (m_NetType.equals("VRS")) {
			// corsConfigInfo = "网络类型：VRS%s%s\n";
			m_Ip = m_Preference.getString("vrs_ip", "");

			m_Port = m_Preference.getString("vrs_port", "");

			m_First = m_Preference.getString("vrs_username", "");

			m_Second = m_Preference.getString("vrs_password", "");

			m_Third = m_Preference.getString("vrs_sourcenode", "");

		} else if (m_NetType.equals("ZHD")) {
			m_Ip = m_Preference.getString("zhd_ip", "");

			m_Port = m_Preference.getString("zhd_port", "");

			m_First = m_Preference.getString("zhd_machinetype", "");

			m_Second = m_Preference.getString("zhd_usergroup", "");

			m_Third = m_Preference.getString("vrs_workgroup", "");
		}

		return true;
	}

	/**
	 * 广播接收信息处理
	 *
	 * @param
	 */
	private void netReceived(Bundle data) {
		try {
			if (mNetWorkListener == null)
				return;

			Intent intent = new Intent();
			m_IsReturn = true;

			Message msgMessage = Message.obtain();

			int myflags = data.getInt(CorsGprsService.MY_FLAGS_FOR_CORS, 0);
			if (myflags == CorsGprsService.FLAG_NETRESULT) {
				// String resultString =
				// data.getString(CorsGprsService.RESULT_NETRESULT);
				int result = data.getInt(CorsGprsService.RESULT_NETRESULT);

				mResult = result;

				Toast.makeText(mCurrentContext,
						getResultMsg(result, mCurrentContext),
						Toast.LENGTH_SHORT).show();
				if (result == NetResult.Success) {
					// 如果有外界的handler则通知外界的界面更新
					if (mConnectHandler != null) {
						msgMessage.what = NET_RECEIVED;
						mConnectHandler.sendMessage(msgMessage);
					}

					mNetWorkListener.statusChange(mCurrentContext
							.getString(R.string.receivingdate));
				} else {
					if (m_NetType.equals("VRS")) {
						intent = new Intent(mCurrentContext,
								CorsGprsService.class);
						mCurrentContext.stopService(intent);
					} else {
						intent = new Intent(mCurrentContext,
								ZHDNetGprsService.class);
						mCurrentContext.stopService(intent);
					}
					if (result == NetResult.Fail_And_Reconnection
							|| result == NetResult.GPRSSet_NoResponse_And_Reconnection) {
						if (!mIsClose) {
							m_Handler.postDelayed(m_reconncet, 1000);

							mIsReConnect = true;

							if (mConnectHandler != null) {
								msgMessage.what = NET_ERROR;
								mConnectHandler.sendMessage(msgMessage);
							}
						}
					} else if (result == NetResult.GPRSSet_UserName_Occupied) {

						if (!mIsClose) {
							m_Handler.postDelayed(m_reconncet, 20000);

							mIsReConnect = true;

							if (mConnectHandler != null) {
								msgMessage.what = NET_ERROR;
								mConnectHandler.sendMessage(msgMessage);
							}
						}
					}
				}
			} else if (myflags == CorsGprsService.FLAG_DATARECEIVE) {
				if (m_progressValue < 100) {
					m_progressValue += 10;
				} else {
					m_progressValue = 0;
				}

				// mNetWorkListener
				// .statusChange(Integer.toString(m_progressValue));
				if (m_proBar != null)
					m_proBar.setProgress(m_progressValue);

				if (mConnectHandler != null) {
					msgMessage.what = NET_RECEIVED;
					mConnectHandler.sendMessage(msgMessage);
				}

			} else if (myflags == CorsGprsService.FLAG_NETERROR) {
				if (m_NetType.equals("VRS")) {
					intent = new Intent(mCurrentContext, CorsGprsService.class);
					mCurrentContext.stopService(intent);
				} else {
					intent = new Intent(mCurrentContext,
							ZHDNetGprsService.class);
					mCurrentContext.stopService(intent);
				}

				if (m_proBar != null)
					m_proBar.setProgress(0);

				// 如果有外界的handler则通知外界的界面更新
				if (mConnectHandler != null) {
					msgMessage.what = NET_ERROR;
					mConnectHandler.sendMessage(msgMessage);
				}

				mNetWorkListener.statusChange("已断开网络");
				if (!mIsClose) {
					m_Handler.postDelayed(m_reconncet, 1000);
					mIsReConnect = true;
				}
			}
		} catch (Exception e) {
			Log.i("NetWorkActivity", "OnReceive:" + e.toString());
			e.printStackTrace();
		}
	}

	public void setProgressBar(ProgressBar progressBar) {
		m_proBar = progressBar;
	}

	public BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context mCurrentContext, Intent intent) {
			String action = intent.getAction();
			if (action.equals(CorsGprsService.ACTION_SENDRESULT)) {
				Message msg = Message.obtain();
				msg.what = NET_RECEIVED;
				msg.setData(intent.getExtras());
				m_Handler.removeMessages(NET_RECEIVED);
				m_Handler.sendMessage(msg);
			}
		}
	};

	private String getResultMsg(int result, Context mCurrentContext) {
		switch (result) {
			case NetResult.Success:
				return mCurrentContext.getString(R.string.toast_connection_success);
			case NetResult.Fail:
				return mCurrentContext.getString(R.string.toast_connection_fail);
			case NetResult.Fail_And_Reconnection:
				return mCurrentContext.getString(R.string.toast_fail_reconnection);
			case NetResult.GPRS_UserPassword_Fail:
				return mCurrentContext.getString(R.string.toast_userpassword_fail);
			case NetResult.GPRSSet_NoResponse:
				return mCurrentContext.getString(R.string.toast_no_response);
			case NetResult.GPRSSet_NoResponse_And_Reconnection:
				return mCurrentContext
						.getString(R.string.toast_no_response_reconnection);
			case NetResult.GPRSSet_UserName_Occupied:
				return mCurrentContext.getString(R.string.toast_auth_401);
			default:
				return "";
		}
	}

	protected Runnable m_reconncet = new Runnable() {

		@Override
		public void run() {
			if (!mIsReConnect) {
				return;
			}
			Reconnect();
		}

	};

	private void Reconnect() {
		try {
			mIsReConnect = false;
			if (m_NetHelper.CheckNet(mApplicationContext)) {

				getPar();

				if (m_NetType.equals("VRS")) {
					mIntent = new Intent(mCurrentContext, CorsGprsService.class);
					mIntent.putExtra("ip", m_Ip);
					mIntent.putExtra("port", m_Port);
					mIntent.putExtra("username", m_First);
					mIntent.putExtra("password", m_Second);
					mIntent.putExtra("sourcenode", m_Third);// RTCM2.3"0020028028"
					mIntent.putExtra("reconnect", true);
					mCurrentContext.stopService(mIntent);
					mCurrentContext.startService(mIntent);
				} else {
					mIntent = new Intent(mCurrentContext,
							ZHDNetGprsService.class);
					mIntent.putExtra("ip", m_Ip);
					mIntent.putExtra("port", m_Port);
					mIntent.putExtra("username", m_First);
					mIntent.putExtra("usergroup", m_Second);
					mIntent.putExtra("workgroup", m_Third);// RTCM2.3"0020028028"
					mIntent.putExtra("reconnect", true);
					mCurrentContext.stopService(mIntent);
					mCurrentContext.startService(mIntent);
				}
			} else {

				mIsReConnect = false;
				m_Handler.postDelayed(m_reconncet, 1000);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			mIsReConnect = true;
		}
	}

	Handler m_Handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == REFRESH_UI) {

			} else if (msg.what == NET_RECEIVED) {
				netReceived(msg.getData());
			}
		}
	};

	public interface INetWorkListener {
		public void statusChange(String msg);
	}

	public void setNetWorkListener(INetWorkListener listener) {
		mNetWorkListener = listener;
	}

	public int getmResult() {
		return mResult;
	}

	private int judgeNetStates() {
		final String status = FileHelper.read3gStatus("errorcode");
		if (!status.equals("")) {
			String sta = "";

			if (status.contains("\n")) {
				sta = status.replace("\n", "");
				sta = sta.trim();
			}

			if (!sta.equals(""))
				return Integer.parseInt(sta);
		}
		return -1;
	}

}
