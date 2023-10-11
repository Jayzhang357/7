package com.zhd.zhdcorsnet;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import com.zhd.parserinterface.IParser;

import android.R.bool;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

public class GprsNetService extends Service {

	private String mLogPath = "";
	// private long mCheckTimestamp = 0l;//记录上次检测网络的时间
	// private static final long CHECKINTERVAL = 5000;//每次检测网络的时间间隔(ms)
	// private boolean mCheckNetResult = false;

	private StateBroadcast stateBroadcast;

	// 为本服务的广播参数
	private Intent mIntent = new Intent(ACTION_OPEN_RESULT);

	public static final String ACTION_OPEN_RESULT = "ACTION_OPEN_RESULT";

	public static final String OPEN_RESULT = "OPEN_RESULT";

	public static final String ACTION_OPEN_PURPOSE = "ACTION_OPEN_PURPOSE";

	public static final String OPEN_PURPOSE = "OPEN_PURPOSE";

	public static final String ACTION_OPEN_PURPOSE_ADDIP = "ACTION_ADDIP";

	public static final String ADD_IP_NAME = "ADD_IP";

	/**
	 * 网络异常
	 */
	public static final int OPEN3G_ERROR = 200;
	/**
	 *
	 */
	public static final int OPENSU_ERROR = 201;
	/**
	 *
	 */
	public static final int OPEN3G_TRUE = 202;

	public static final int CLOSE3G_ERROR = 203;

	public static final int CLOSE3G_TRUE = 204;

	public static final int OPEN_3G = 301;

	public static final int CLOSE_3G = 302;

	public static final int ADD_IP = 401;

	@Override
	public void onCreate() {
		boolean result = getAdminAuthoration();
		if (!result)
			mIntent.putExtra(OPEN_RESULT, OPENSU_ERROR);

		// sendBroadcast(mIntent);

		Log.i("gprsservice", "service start....");
	}

	/**
	 * 获取超级管理员权限
	 *
	 * @return
	 */
	private boolean getAdminAuthoration() {
		boolean result = true;

		try {
			Process process = Runtime.getRuntime().exec("su");
			DataOutputStream os = new DataOutputStream(
					process.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			result = false;
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * 关闭3G网络
	 *
	 * @return
	 */
	private void close3G() {
		boolean result = true;
		try {
			execRootCmdSilent("echo \"0\" > /data/user/flag" + "\n");

		} catch (Exception e) {
			// TODO: handle exception
			result = false;
		}

		mIntent.removeExtra(OPEN_RESULT);

		if (result)
			mIntent.putExtra(OPEN_RESULT, CLOSE3G_TRUE);
		else {
			mIntent.putExtra(OPEN_RESULT, CLOSE3G_ERROR);
		}
		// sendBroadcast(mIntent);

		Log.i("gprsservice", "3g close....");
	}

	/**
	 * 打开3G网络
	 *
	 * @return
	 */
	private void open3G() {

		// 获取管理员权限
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		mIntent.removeExtra(OPEN_RESULT);

		boolean result = true;

		try {
			Thread.sleep(1000);

			// open3G
			execRootCmdSilent("echo \"1\" > /data/user/flag" + "\n");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			mIntent.putExtra(OPEN_RESULT, OPEN3G_ERROR);

			try {
				NetHelper.writeLog(getLogPath(), "初始化3G服务出错");
			} catch (Exception e2) {
				// TODO: handle exception
			}

			result = false;
		}

		if (result)
			mIntent.putExtra(OPEN_RESULT, OPEN3G_TRUE);

		//发送广播
		sendBroadcast(mIntent);

		Log.i("gprsservice", "3g open....");
	}

	/**
	 * 添加路由
	 * @param ip
	 */
	private void addIp(String ip) {
		// add ip
		execRootCmdSilent("ip route add " + ip + " via 0.0.0.0 dev ppp0" + "\n");
	}

	/**
	 * 执行命令
	 *
	 * @param paramString
	 * @return
	 */
	private int execRootCmdSilent(String paramString) {
		try {
			Process localProcess = Runtime.getRuntime().exec("su");
			Object localObject = localProcess.getOutputStream();
			DataOutputStream localDataOutputStream = new DataOutputStream(
					(OutputStream) localObject);
			String str = String.valueOf(paramString);
			localObject = str + "\n";
			localDataOutputStream.writeBytes((String) localObject);
			localDataOutputStream.flush();
			localDataOutputStream.writeBytes("exit\n");
			localDataOutputStream.flush();
			localProcess.waitFor();
			localObject = localProcess.exitValue();
			return (Integer) localObject;
		} catch (Exception localException) {
			localException.printStackTrace();
		}
		return 0;
	}

	public BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context mCurrentContext, Intent intent) {
			String action = intent.getAction();
			if (action.equals(GprsNetService.ACTION_OPEN_PURPOSE)) {
				int purpose = intent.getExtras().getInt(OPEN_PURPOSE);
				switch (purpose) {
					case OPEN_3G:
						open3G();
						break;
					case CLOSE_3G:
						close3G();
						break;
					case ADD_IP:
						if(intent != null)
						{
							String ip = intent.getExtras().getString(ADD_IP_NAME);
							addIp(ip);
						}
						break;
					default:
						break;
				}
			}
		}
	};

	@Override
	public void onDestroy() {
		close3G();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// if (intent == null) {
		// return START_STICKY;
		// }

		// try {
		// Thread.sleep(1000);
		// } catch (InterruptedException e1) {
		// // TODO Auto-generated catch block
		// e1.printStackTrace();
		// }

		IntentFilter myIntentFilter = new IntentFilter();
		myIntentFilter.addAction(ACTION_OPEN_PURPOSE);
		registerReceiver(mBroadcastReceiver, myIntentFilter);

		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private String getLogPath() {
		if (mLogPath == null || mLogPath.equals("")) {
			mLogPath = NetHelper.getSDPath();
			mLogPath += "/ZNZCORS";
			File logPath = new File(mLogPath);
			if (!logPath.exists()) {
				logPath.mkdirs();
			}
			mLogPath += "/GprsLog.txt";
		}

		return mLogPath;
	}
}
