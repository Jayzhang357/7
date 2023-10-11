package com.zhd.shj.update;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.KeyEvent;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.zhd.shj.AleartDialogHelper;
import com.zhd.shj.BuildConfig;
import com.zhd.shj.CustomDialog;
import com.zhd.shj.R;

import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.text.MessageFormat;


public class AutoUpdateHelper {

	private static AutoUpdateHelper autoUpdateHelper = null;
	private UpdateFileInfo updateFileInfo = null;
	private ProgressDialog myDialog = null;
	private Context context = null;
	private DownUpdateInfoService.DownLoadUpdateFileBinder downLoadUpdateFileBinder = null;
	private AutoUpdateCallBack autoUpdateCallBack = null;

	private AutoUpdateHelper() {
	}

	public static AutoUpdateHelper getInstance() {
		if (autoUpdateHelper == null)
			autoUpdateHelper = new AutoUpdateHelper();
		return autoUpdateHelper;
	}

	public String GetDownloadJob(Context context, String webserviceUrl,
								 String equipmentname) {
		String obj = null;
		try {
			obj = AutoUpdateServiceAgent.GetDownloadJob(webserviceUrl,
					equipmentname);

		} catch (SocketException e) {

			AleartDialogHelper.alertBottomToast(context,
					context.getString(R.string.TimeOutUpgrade));
			return "";
		} catch (SocketTimeoutException e) {

			AleartDialogHelper.alertBottomToast(context,
					context.getString(R.string.TimeOutUpgrade));
			return "";
		} catch (XmlPullParserException e) {
			AleartDialogHelper.alertBottomToast(context,
					context.getString(R.string.UrlErrorUpgrade));
			return "";
		} catch (Exception e) {
			AleartDialogHelper.alertBottomToast(context,
					context.getString(R.string.Faield));
			return "";
		}

		return obj;
	}
	public String GetDownloadJob_1(Context context, String webserviceUrl,
								   double  b,double l) {
		String obj = null;
		try {
			obj = AutoUpdateServiceAgent.GetDownloadJob_1(webserviceUrl,
					b,l);

		} catch (SocketException e) {

			AleartDialogHelper.alertBottomToast(context,
					context.getString(R.string.TimeOutUpgrade));
			return "";
		} catch (SocketTimeoutException e) {

			AleartDialogHelper.alertBottomToast(context,
					context.getString(R.string.TimeOutUpgrade));
			return "";
		} catch (XmlPullParserException e) {
			AleartDialogHelper.alertBottomToast(context,
					context.getString(R.string.UrlErrorUpgrade));
			return "";
		} catch (Exception e) {
			AleartDialogHelper.alertBottomToast(context,
					context.getString(R.string.Faield));
			return "";
		}

		return obj;
	}
	public boolean UpdateJob(Context context, String webserviceUrl,
							 String SN_num, String Fieldname, String Jobname, double APointB,
							 double APointL, double BPointB, double BPointL, double OriginPointB,
							 double OriginPointL, double APointX,
							 double APointY, double BPointX, double BPointY, double OriginPointX,
							 double OriginPointY) {
		Object obj = null;
		try {
			obj = AutoUpdateServiceAgent.UpdateJob(webserviceUrl, SN_num,
					Fieldname, Jobname, APointB, APointL, BPointB, BPointL,OriginPointB,OriginPointL, APointX, APointY, BPointX, BPointY,OriginPointX,OriginPointY);
		} catch (SocketException e) {

			AleartDialogHelper.alertBottomToast(context,
					context.getString(R.string.TimeOutUpgrade));
			return false;
		} catch (SocketTimeoutException e) {

			AleartDialogHelper.alertBottomToast(context,
					context.getString(R.string.TimeOutUpgrade));
			return false;
		} catch (XmlPullParserException e) {
			AleartDialogHelper.alertBottomToast(context,
					context.getString(R.string.UrlErrorUpgrade));
			return false;
		} catch (Exception e) {
			AleartDialogHelper.alertBottomToast(context,
					context.getString(R.string.Faield));
			return false;
		}

		if (obj.toString().equals("2")) {
			AleartDialogHelper.alertBottomToast(context,
					context.getString(R.string.success));
			return true;
		} else
			return false;
	}

	/**
	 * 检测是否需要更新
	 *
	 * @param context
	 * @param webserviceUrl
	 * @param apkName
	 *            apk的名称（注意：需要包含后缀名）
	 * @param version
	 * @return
	 */
	public boolean checkIfNeedUpdateInfo(Context context, String webserviceUrl,
										 String apkName, String version, boolean isAlert) {
		Object obj = null;
		try {
			obj = AutoUpdateServiceAgent.checkUpdate(webserviceUrl, apkName,
					version);
		} catch (SocketException e) {
			if (isAlert)
				AleartDialogHelper.alertBottomToast(context,
						context.getString(R.string.TimeOutUpgrade));
			return false;
		} catch (SocketTimeoutException e) {
			if (isAlert)
				AleartDialogHelper.alertBottomToast(context,
						context.getString(R.string.TimeOutUpgrade));
			return false;
		} catch (XmlPullParserException e) {
			AleartDialogHelper.alertBottomToast(context,
					context.getString(R.string.UrlErrorUpgrade));
			return false;
		} catch (Exception e) {
			AleartDialogHelper.alertBottomToast(context,
					context.getString(R.string.FailedUpgrade));
			return false;
		}
		try {
			String abc = obj.toString();
			updateFileInfo = new UpdateFileInfo();
			updateFileInfo.UpdateFileLength = Integer.parseInt(abc);
			if (updateFileInfo.UpdateFileLength > 0)
				updateFileInfo.NeedUpdate = true;
			// updateFileInfo = DisolveDataHelper.getUpdateFileInfo(obj);
			updateFileInfo.ApkName = apkName;
			updateFileInfo.WebserviceUrl = webserviceUrl;
			updateFileInfo.method = "GetFile1";
			this.context = context;

		} catch (Exception e) {
			return false;
		}
		return updateFileInfo.NeedUpdate;
	}

	/**
	 * 检测是否需要更新
	 *
	 * @param context
	 * @param webserviceUrl
	 * @param
	 * //apk的名称（注意：需要包含后缀名）
	 * @param
	 * @return
	 */
	public String getUrlWeb(Context context, String webserviceUrl,
							boolean isAlert) {
		Object obj = null;
		try {
			obj = AutoUpdateServiceAgent.GetWebUrl(webserviceUrl);
		} catch (SocketException e) {
			if (isAlert)
				AleartDialogHelper.alertBottomToast(context,
						context.getString(R.string.TimeOutUpgrade));
			return "";
		} catch (SocketTimeoutException e) {
			if (isAlert)
				AleartDialogHelper.alertBottomToast(context,
						context.getString(R.string.TimeOutUpgrade));
			return "";
		} catch (XmlPullParserException e) {
			AleartDialogHelper.alertBottomToast(context,
					context.getString(R.string.UrlErrorUpgrade));
			return "";
		} catch (Exception e) {
			AleartDialogHelper.alertBottomToast(context,
					context.getString(R.string.FailedUpgrade));
			return "";
		}
		try {
			String abc = obj.toString();

			return abc;

		} catch (Exception e) {
			return "";
		}

	}

	/**
	 * 检测是否需要惯导更新
	 *
	 * @param context
	 * @param webserviceUrl
	 * @param //apkName
	 *            apk的名称（注意：需要包含后缀名）
	 * @param //version
	 * @return
	 */
	public String checkMCUName(Context context, String webserviceUrl,
							   boolean isAlert) {
		Object obj = null;
		String abc;
		try {
			obj = AutoUpdateServiceAgent.checkmcuversion(webserviceUrl);
		} catch (SocketException e) {
			if (isAlert)
				AleartDialogHelper.alertBottomToast(context,
						context.getString(R.string.TimeOutUpgrade));
			return "";
		} catch (SocketTimeoutException e) {
			if (isAlert)
				AleartDialogHelper.alertBottomToast(context,
						context.getString(R.string.TimeOutUpgrade));
			return "";
		} catch (XmlPullParserException e) {
			AleartDialogHelper.alertBottomToast(context,
					context.getString(R.string.UrlErrorUpgrade));
			return "";
		} catch (Exception e) {
			AleartDialogHelper.alertBottomToast(context,
					context.getString(R.string.FailedUpgrade));
			return "";
		}
		try {
			abc = obj.toString();
			String data_p[] = abc.split(";");
			updateFileInfo = new UpdateFileInfo();
			updateFileInfo.UpdateFileLength = Integer.parseInt(data_p[1]);
			if (updateFileInfo.UpdateFileLength > 0)
				updateFileInfo.NeedUpdate = true;
			// updateFileInfo = DisolveDataHelper.getUpdateFileInfo(obj);
			updateFileInfo.ApkName = data_p[0];
			updateFileInfo.method = "GetFilemcu";
			updateFileInfo.WebserviceUrl = webserviceUrl;
			this.context = context;
			return data_p[0];
		} catch (Exception e) {
			return "";
		}

	}

	/**
	 * 检测是否需要惯导更新
	 *
	 * @param context
	 * @param webserviceUrl
	 * @param //apkName
	 *            apk的名称（注意：需要包含后缀名）
	 * @param //version
	 * @return
	 */
	public String checkUMName(Context context, String webserviceUrl,
							  boolean isAlert) {
		Object obj = null;
		String abc;
		try {
			obj = AutoUpdateServiceAgent.checkumversion(webserviceUrl);
		} catch (SocketException e) {
			if (isAlert)
				AleartDialogHelper.alertBottomToast(context,
						context.getString(R.string.TimeOutUpgrade));
			return "";
		} catch (SocketTimeoutException e) {
			if (isAlert)
				AleartDialogHelper.alertBottomToast(context,
						context.getString(R.string.TimeOutUpgrade));
			return "";
		} catch (XmlPullParserException e) {
			AleartDialogHelper.alertBottomToast(context,
					context.getString(R.string.UrlErrorUpgrade));
			return "";
		} catch (Exception e) {
			AleartDialogHelper.alertBottomToast(context,
					context.getString(R.string.FailedUpgrade));
			return "";
		}
		try {
			abc = obj.toString();
			String data_p[] = abc.split(";");
			updateFileInfo = new UpdateFileInfo();
			updateFileInfo.UpdateFileLength = Integer.parseInt(data_p[1]);
			if (updateFileInfo.UpdateFileLength > 0)
				updateFileInfo.NeedUpdate = true;
			// updateFileInfo = DisolveDataHelper.getUpdateFileInfo(obj);
			updateFileInfo.ApkName = data_p[0];
			updateFileInfo.method = "GetFileum";
			updateFileInfo.WebserviceUrl = webserviceUrl;
			this.context = context;
			return data_p[0];
		} catch (Exception e) {
			return "";
		}

	}

	/**
	 * 检测是否需要MDU更新
	 *
	 * @param context
	 * @param webserviceUrl
	 * @param //apkName
	 *            apk的名称（注意：需要包含后缀名）
	 * @param //version
	 * @return
	 */
	public String checkSBGName(Context context, String webserviceUrl,
							   boolean isAlert) {
		Object obj = null;
		String abc;
		try {
			obj = AutoUpdateServiceAgent.checkSBGversion(webserviceUrl);
		} catch (SocketException e) {
			if (isAlert)
				AleartDialogHelper.alertBottomToast(context,
						context.getString(R.string.TimeOutUpgrade));
			return "";
		} catch (SocketTimeoutException e) {
			if (isAlert)
				AleartDialogHelper.alertBottomToast(context,
						context.getString(R.string.TimeOutUpgrade));
			return "";
		} catch (XmlPullParserException e) {
			AleartDialogHelper.alertBottomToast(context,
					context.getString(R.string.UrlErrorUpgrade));
			return "";
		} catch (Exception e) {
			AleartDialogHelper.alertBottomToast(context,
					context.getString(R.string.FailedUpgrade));
			return "";
		}
		try {
			abc = obj.toString();
			String data_p[] = abc.split(";");
			updateFileInfo = new UpdateFileInfo();
			updateFileInfo.UpdateFileLength = Integer.parseInt(data_p[1]);
			if (updateFileInfo.UpdateFileLength > 0)
				updateFileInfo.NeedUpdate = true;
			// updateFileInfo = DisolveDataHelper.getUpdateFileInfo(obj);
			updateFileInfo.ApkName = data_p[0];
			updateFileInfo.method = "GetFileSBG";
			updateFileInfo.WebserviceUrl = webserviceUrl;
			this.context = context;
			return data_p[0];
		} catch (Exception e) {
			return "";
		}

	}

	/**
	 * 检测是否需要MDU更新
	 *
	 * @param context
	 * @param webserviceUrl
	 * @param //apkName
	 *            apk的名称（注意：需要包含后缀名）
	 * @param //version
	 * @return
	 */
	public String checkMDUName(Context context, String webserviceUrl,
							   boolean isAlert) {
		Object obj = null;
		String abc;
		try {
			obj = AutoUpdateServiceAgent.checkMDUversion(webserviceUrl);
		} catch (SocketException e) {
			if (isAlert)
				AleartDialogHelper.alertBottomToast(context,
						context.getString(R.string.TimeOutUpgrade));
			return "";
		} catch (SocketTimeoutException e) {
			if (isAlert)
				AleartDialogHelper.alertBottomToast(context,
						context.getString(R.string.TimeOutUpgrade));
			return "";
		} catch (XmlPullParserException e) {
			AleartDialogHelper.alertBottomToast(context,
					context.getString(R.string.UrlErrorUpgrade));
			return "";
		} catch (Exception e) {
			AleartDialogHelper.alertBottomToast(context,
					context.getString(R.string.FailedUpgrade));
			return "";
		}
		try {
			abc = obj.toString();
			String data_p[] = abc.split(";");
			updateFileInfo = new UpdateFileInfo();
			updateFileInfo.UpdateFileLength = Integer.parseInt(data_p[1]);
			if (updateFileInfo.UpdateFileLength > 0)
				updateFileInfo.NeedUpdate = true;
			// updateFileInfo = DisolveDataHelper.getUpdateFileInfo(obj);
			updateFileInfo.ApkName = data_p[0];
			updateFileInfo.method = "GetFileMDU";
			updateFileInfo.WebserviceUrl = webserviceUrl;
			this.context = context;
			return data_p[0];
		} catch (Exception e) {
			return "";
		}

	}

	/**
	 * 检测是否需要NW更新
	 *
	 * @param context
	 * @param webserviceUrl
	 * @param //apkName
	 *            apk的名称（注意：需要包含后缀名）
	 * @param //version
	 * @return
	 */
	public String checkNWName(Context context, String webserviceUrl,
							  boolean isAlert) {
		Object obj = null;
		String abc;
		try {
			obj = AutoUpdateServiceAgent.checkNWversion(webserviceUrl);
		} catch (SocketException e) {
			if (isAlert)
				AleartDialogHelper.alertBottomToast(context,
						context.getString(R.string.TimeOutUpgrade));
			return "";
		} catch (SocketTimeoutException e) {
			if (isAlert)
				AleartDialogHelper.alertBottomToast(context,
						context.getString(R.string.TimeOutUpgrade));
			return "";
		} catch (XmlPullParserException e) {
			AleartDialogHelper.alertBottomToast(context,
					context.getString(R.string.UrlErrorUpgrade));
			return "";
		} catch (Exception e) {
			AleartDialogHelper.alertBottomToast(context,
					context.getString(R.string.FailedUpgrade));
			return "";
		}
		try {
			abc = obj.toString();
			String data_p[] = abc.split(";");
			updateFileInfo = new UpdateFileInfo();
			updateFileInfo.UpdateFileLength = Integer.parseInt(data_p[1]);
			if (updateFileInfo.UpdateFileLength > 0)
				updateFileInfo.NeedUpdate = true;
			// updateFileInfo = DisolveDataHelper.getUpdateFileInfo(obj);
			updateFileInfo.ApkName = data_p[0];
			updateFileInfo.method = "GetFileNW";
			updateFileInfo.WebserviceUrl = webserviceUrl;
			this.context = context;
			return data_p[0];
		} catch (Exception e) {
			return "";
		}

	}

	/**
	 * 检测是否需要RD更新
	 *
	 * @param context
	 * @param webserviceUrl
	 * @param //apkName
	 *            apk的名称（注意：需要包含后缀名）
	 * @param //version
	 * @return
	 */
	public String checkRDName(Context context, String webserviceUrl,
							  boolean isAlert) {
		Object obj = null;
		String abc;
		try {
			obj = AutoUpdateServiceAgent.checkRDversion(webserviceUrl);
		} catch (SocketException e) {
			if (isAlert)
				AleartDialogHelper.alertBottomToast(context,
						context.getString(R.string.TimeOutUpgrade));
			return "";
		} catch (SocketTimeoutException e) {
			if (isAlert)
				AleartDialogHelper.alertBottomToast(context,
						context.getString(R.string.TimeOutUpgrade));
			return "";
		} catch (XmlPullParserException e) {
			AleartDialogHelper.alertBottomToast(context,
					context.getString(R.string.UrlErrorUpgrade));
			return "";
		} catch (Exception e) {
			AleartDialogHelper.alertBottomToast(context,
					context.getString(R.string.FailedUpgrade));
			return "";
		}
		try {
			abc = obj.toString();
			String data_p[] = abc.split(";");
			updateFileInfo = new UpdateFileInfo();
			updateFileInfo.UpdateFileLength = Integer.parseInt(data_p[1]);
			if (updateFileInfo.UpdateFileLength > 0)
				updateFileInfo.NeedUpdate = true;
			// updateFileInfo = DisolveDataHelper.getUpdateFileInfo(obj);
			updateFileInfo.ApkName = data_p[0];
			updateFileInfo.method = "GetFileRD";
			updateFileInfo.WebserviceUrl = webserviceUrl;
			this.context = context;
			return data_p[0];
		} catch (Exception e) {
			return "";
		}

	}

	/**
	 * 检测是否需要一体机更新
	 *
	 * @param context
	 * @param webserviceUrl
	 * @param //apkName
	 *            apk的名称（注意：需要包含后缀名）
	 * @param //version
	 * @return
	 */
	public String checkOneName(Context context, String webserviceUrl,
							   boolean isAlert) {
		Object obj = null;
		String abc;
		try {
			obj = AutoUpdateServiceAgent.checkoneversion(webserviceUrl);
		} catch (SocketException e) {
			if (isAlert)
				AleartDialogHelper.alertBottomToast(context,
						context.getString(R.string.TimeOutUpgrade));
			return "";
		} catch (SocketTimeoutException e) {
			if (isAlert)
				AleartDialogHelper.alertBottomToast(context,
						context.getString(R.string.TimeOutUpgrade));
			return "";
		} catch (XmlPullParserException e) {
			AleartDialogHelper.alertBottomToast(context,
					context.getString(R.string.UrlErrorUpgrade));
			return "";
		} catch (Exception e) {
			AleartDialogHelper.alertBottomToast(context,
					context.getString(R.string.FailedUpgrade));
			return "";
		}
		try {
			abc = obj.toString();
			String data_p[] = abc.split(";");
			updateFileInfo = new UpdateFileInfo();
			updateFileInfo.UpdateFileLength = Integer.parseInt(data_p[1]);
			if (updateFileInfo.UpdateFileLength > 0)
				updateFileInfo.NeedUpdate = true;
			// updateFileInfo = DisolveDataHelper.getUpdateFileInfo(obj);
			updateFileInfo.ApkName = data_p[0];
			updateFileInfo.method = "GetFileone";
			updateFileInfo.WebserviceUrl = webserviceUrl;
			this.context = context;
			return data_p[0];
		} catch (Exception e) {
			return "";
		}

	}

	/**
	 * 检测是否需要一体机更新
	 *
	 * @param context
	 * @param webserviceUrl
	 * @param //apkName
	 *            apk的名称（注意：需要包含后缀名）
	 * @param //version
	 * @return
	 */
	public String checkallName(Context context, String webserviceUrl,
							   String getfile, String checkname, boolean isAlert) {
		Object obj = null;
		String abc;
		try {
			obj = AutoUpdateServiceAgent.checksumversion(webserviceUrl,
					checkname);
		} catch (SocketException e) {
			if (isAlert)
				AleartDialogHelper.alertBottomToast(context,
						context.getString(R.string.TimeOutUpgrade));
			return "";
		} catch (SocketTimeoutException e) {
			if (isAlert)
				AleartDialogHelper.alertBottomToast(context,
						context.getString(R.string.TimeOutUpgrade));
			return "";
		} catch (XmlPullParserException e) {
			AleartDialogHelper.alertBottomToast(context,
					context.getString(R.string.UrlErrorUpgrade));
			return "";
		} catch (Exception e) {
			AleartDialogHelper.alertBottomToast(context,
					context.getString(R.string.FailedUpgrade));
			return "";
		}
		try {
			abc = obj.toString();
			updateFileInfo = new UpdateFileInfo();

			// updateFileInfo = DisolveDataHelper.getUpdateFileInfo(obj);
			this.context = context;
			updateFileInfo.WebserviceUrl = webserviceUrl;
			updateFileInfo.method = getfile;
			return abc;
		} catch (Exception e) {
			return "";
		}

	}

	public void startUpdatesum(String apkName, String abc) {

		updateFileInfo.ApkName = apkName;
		updateFileInfo.UpdateFileLength = Integer.parseInt(abc);
		if (updateFileInfo.UpdateFileLength > 0)
			updateFileInfo.NeedUpdate = true;
		if (updateFileInfo.NeedUpdate) {
			Intent intent = new Intent(context, DownUpdateInfoService.class);
			intent.putExtra("UpdateFileInfo", updateFileInfo);
			context.getApplicationContext().bindService(intent,
					downloadUpdateFilecon, Context.BIND_AUTO_CREATE);

		}
	}

	public void startUpdateone() {
		if (updateFileInfo.NeedUpdate) {
			Intent intent = new Intent(context, DownUpdateInfoService.class);
			intent.putExtra("UpdateFileInfo", updateFileInfo);
			context.getApplicationContext().bindService(intent,
					downloadUpdateFilecon, Context.BIND_AUTO_CREATE);

		}
	}

	public void startUpdateNW() {
		if (updateFileInfo.NeedUpdate) {
			Intent intent = new Intent(context, DownUpdateInfoService.class);
			intent.putExtra("UpdateFileInfo", updateFileInfo);
			context.getApplicationContext().bindService(intent,
					downloadUpdateFilecon, Context.BIND_AUTO_CREATE);

		}
	}

	public void startUpdateRD() {
		if (updateFileInfo.NeedUpdate) {
			Intent intent = new Intent(context, DownUpdateInfoService.class);
			intent.putExtra("UpdateFileInfo", updateFileInfo);
			context.getApplicationContext().bindService(intent,
					downloadUpdateFilecon, Context.BIND_AUTO_CREATE);

		}
	}

	public void startUpdate() {
		if (updateFileInfo.NeedUpdate) {
			CustomDialog.Builder builder = new CustomDialog.Builder(context);
			builder.setMessage(context.getString(R.string.NewEditionUpgrade));
			builder.setTitle(context.getString(R.string.Upgrade));
			builder.setPositiveButton(R.string.confirm,
					new OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();

							Intent intent = new Intent(context,
									DownUpdateInfoService.class);
							intent.putExtra("UpdateFileInfo", updateFileInfo);
							context.getApplicationContext().bindService(intent,
									downloadUpdateFilecon,
									Context.BIND_AUTO_CREATE);
						}
					});
			builder.setNegativeButton(R.string.cancel,
					new OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							if (autoUpdateCallBack != null)
								autoUpdateCallBack
										.HandleResponseType(ResponseType.Info_NotUpdate);
						}
					});

			builder.create().show();

			// AlertDialog noticeDialog = builder.create();
			// noticeDialog.setOnKeyListener(new DialogInterface.OnKeyListener()
			// { // 屏蔽返回键、搜索键
			// public boolean onKey(DialogInterface dialog, int keyCode,
			// KeyEvent event) {
			// if (keyCode == KeyEvent.KEYCODE_SEARCH || keyCode ==
			// KeyEvent.KEYCODE_BACK) {
			// return true;
			// }
			// return false;
			// }
			// });
			// noticeDialog.show();
		}
	}

	/**
	 * 设置回调
	 *
	 * @param autoUpdateCallBack
	 */
	public void setAutoUpdateCallBack(AutoUpdateCallBack autoUpdateCallBack) {
		this.autoUpdateCallBack = autoUpdateCallBack;
	}

	private ServiceConnection downloadUpdateFilecon = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			downLoadUpdateFileBinder = (DownUpdateInfoService.DownLoadUpdateFileBinder) service;
			downLoadUpdateFileBinder.setHandler(downUpdateInfoMessageHandler);
			downLoadUpdateFileBinder.startUpdate();
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
		}
	};

	/**
	 * 安装apk
	 *
	 * @param //url
	 */
	private void installApk(File apkfile) {
		if (updateFileInfo.method != "GetFile1"&&updateFileInfo.method != "GetFileapkS")
			return;
		if (!apkfile.exists()) {
			return;
		}
		Intent update  = new Intent(Intent.ACTION_VIEW);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			update.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
			Uri contentUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", new File(Uri.parse("file://" + apkfile.toString()).getPath()));
			update.setDataAndType(contentUri, "application/vnd.android.package-archive");
		} else {
			update.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
			update.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		}
		context.startActivity(update);

		/*intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setDataAndType(Uri.parse("file://" + apkfile.toString()),
				"application/vnd.android.package-archive");
		context.startActivity(intent);*/
	}

	/*
	 * 检测网络状态：别忘记在mainfest.xml中添加android.permission.ACCESS_NETWORK_STATE权限
	 */
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			return false;
		} else {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private Handler downUpdateInfoMessageHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			boolean success = false;
			File apkfile = null;
			String message = null;
			switch (msg.what) {
				case ResponseType.Info_StartUpdate:
					myDialog = new ProgressDialog(context);
					myDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
					myDialog.setTitle(context.getString(R.string.UpGrading));
					myDialog.setMessage(context.getString(R.string.RequstUpgrade));
					myDialog.setButton(context.getString(R.string.CancleUpgrade),
							new OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
													int which) {
									downLoadUpdateFileBinder.cancelUpdate();
								}
							});

					myDialog.setOnKeyListener(new DialogInterface.OnKeyListener() { // 屏蔽搜索键
						public boolean onKey(DialogInterface dialog, int keyCode,
											 KeyEvent event) {
							if (keyCode == KeyEvent.KEYCODE_SEARCH) {
								return true;
							}
							return false;
						}
					});
					myDialog.setMax(100);
					myDialog.setCancelable(false); // 屏蔽返回键
					myDialog.show();

					if (autoUpdateCallBack != null)
						autoUpdateCallBack
								.HandleResponseType(ResponseType.Info_StartUpdate);
					super.handleMessage(msg);
					return;
				case ResponseType.Info_GetUpdateSuccess:
					int hadDownloadCount = Integer.valueOf(String.valueOf(msg.obj)) / 1024;
					message = MessageFormat.format("{0} k/ {1} k",
							hadDownloadCount,
							updateFileInfo.UpdateFileLength / 1024);
					myDialog.setMessage(message);
					int process = (int) ((hadDownloadCount * 100) / ((float) updateFileInfo.UpdateFileLength / 1024)); // 总进度为100，因此需要处理
					myDialog.setProgress(process);

					if (autoUpdateCallBack != null)
						autoUpdateCallBack
								.HandleResponseType(ResponseType.Info_GetUpdateSuccess);
					return;
				case ResponseType.Info_NetWorkDisconnect:
					message = context.getString(R.string.NetErrorUpgrade);

					if (autoUpdateCallBack != null)
						autoUpdateCallBack
								.HandleResponseType(ResponseType.Info_NetWorkDisconnect);
					break;
				case ResponseType.Info_GetUpdateInfoException:
					message = context.getString(R.string.DownloadErrorUpgrade);

					if (autoUpdateCallBack != null)
						autoUpdateCallBack
								.HandleResponseType(ResponseType.Info_GetUpdateInfoException);
					break;
				case ResponseType.Info_NoSDCard:
					message = context.getString(R.string.SDcardRequstUpgrade);

					if (autoUpdateCallBack != null)
						autoUpdateCallBack
								.HandleResponseType(ResponseType.Info_NoSDCard);
					break;
				case ResponseType.Info_UpdateSuccess:
					success = true;
					message = context.getString(R.string.SuccessUpgrade);
					apkfile = (File) msg.obj;

					if (autoUpdateCallBack != null)
						autoUpdateCallBack
								.HandleResponseType(ResponseType.Info_UpdateSuccess);
					break;
				case ResponseType.Info_CancelUpdate:
					message = context.getString(R.string.CancleAutoUpgrade);

					if (autoUpdateCallBack != null)
						autoUpdateCallBack
								.HandleResponseType(ResponseType.Info_CancelUpdate);
					break;
				default:
					break;
			}

			myDialog.dismiss();
			context.getApplicationContext()
					.unbindService(downloadUpdateFilecon); // 解除服务绑定
			Toast.makeText(context, message, Toast.LENGTH_LONG).show();
			if (success)
				installApk(apkfile);
			else
				super.handleMessage(msg);
		}
	};

	public interface ICheckUpgradeListener {
		public void upgrade(boolean isNeed);
	}

}
