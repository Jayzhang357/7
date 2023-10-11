package com.zhd.bdxt;

import com.zhd.zhdcorsnet.BDXTNetGprsService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;

public class BDXTBootReceiver extends BroadcastReceiver {

	private static Context mContext;

	@Override
	public void onReceive(Context context, Intent intent) {
		mContext = context;
		mHandler.sendEmptyMessageDelayed(0x01, 30000);//延迟30秒启动服务，等待设备连接好网络。
	}

	private static Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			startBDXTService(mContext);
		}
	};

	/**
	 * 开启北斗星通基站连接服务
	 * @param context
	 */
	private static void startBDXTService(Context context){
		SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(context);
		String ip = preference.getString("bdxt_ip", "");
		String port = preference.getString("bdxt_port", "");
		String sim = preference.getString("bdxt_simnumber", "");
		String diffformat = preference.getString("bdxt_diffformat", "");
		String serialport = preference.getString("bdxt_serialbaudrate", "");

		Intent bdxtIntent = new Intent(context, BDXTNetGprsService.class);
		bdxtIntent.putExtra(BDXTNetGprsService.DBXTIP, ip);
		bdxtIntent.putExtra(BDXTNetGprsService.DBXTPORT, port);
		bdxtIntent.putExtra(BDXTNetGprsService.DBXTSIMNUMBER, sim);
		bdxtIntent.putExtra(BDXTNetGprsService.DBXTDIFFFORMAT, diffformat);
		bdxtIntent.putExtra(BDXTNetGprsService.DBXTSERIALBAUDRATE, serialport);

		context.startService(bdxtIntent);
	}

}
