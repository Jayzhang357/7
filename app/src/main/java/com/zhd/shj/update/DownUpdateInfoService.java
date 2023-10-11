package com.zhd.shj.update;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class DownUpdateInfoService extends Service {

	private DownLoadUpdateFileBinder downLoadUpdateFileBinder = new DownLoadUpdateFileBinder();
	private UpdateFileInfo updateFileInfo = null;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		updateFileInfo = (UpdateFileInfo) intent.getExtras().getSerializable(
				"UpdateFileInfo");
		return downLoadUpdateFileBinder;
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	public class DownLoadUpdateFileBinder extends Binder {

		private Handler messageHandler = null;
		private File tempApkFile = null;
		private FileOutputStream tempApkStream = null;
		private int hadDownloadCount = 0; // 已經下載的大小
		private boolean cancelDownLoad = false; // 是否取消下载

		public Handler getHandler() {
			return messageHandler;
		}

		public void setHandler(Handler handler) {
			this.messageHandler = handler;
		}

		/**
		 * 开始下载任务
		 *
		 * @param
		 */
		public void startUpdate() {

			new Thread() {
				public void run() {
					downLoadUpdateInfo(updateFileInfo);
				}
			}.start(); /* 开始执行线程 */
		}

		public void cancelUpdate() {
			cancelDownLoad = true;
		}

		/**
		 * 下载更新
		 *
		 * @param
		 */
		private void downLoadUpdateInfo(UpdateFileInfo updateFileInfo) {
			boolean isFinish = false;
			int index = 0;
			hadDownloadCount = 0;
			cancelDownLoad = false;
			sendMessage(messageHandler, ResponseType.Info_StartUpdate);
			try {
				if (prePareforUpdate()) {
					while (!isFinish && !cancelDownLoad) {
						Object obj = AutoUpdateServiceAgent.getUpdateInfo(
								updateFileInfo.WebserviceUrl,
								updateFileInfo.ApkName, index,updateFileInfo.method);

						AutoUpdateInfo autoUpdateInfo = DisolveDataHelper
								.getAutoUpdateInfo(obj);
						if (autoUpdateInfo != null) {
							// 写入文件
							tempApkStream.write(autoUpdateInfo.FileValue, 0,
									autoUpdateInfo.FileValue.length);
							tempApkStream.flush();

							hadDownloadCount += autoUpdateInfo.FileValue.length;
							Log.v("下载好",autoUpdateInfo.FileValue.length +":"+hadDownloadCount);
							Message message = new Message();
							message.what = ResponseType.Info_GetUpdateSuccess;
							message.obj = hadDownloadCount;
							messageHandler.sendMessage(message);
							index++;
						} else {
							throw new NullPointerException("与服务器的交互出现网络异常");
						}
						if (index >= 100) {
							isFinish=true;
						}

						// isFinish = autoUpdateInfo.IsFinish;
					}

					if (cancelDownLoad) {
						sendMessage(messageHandler,
								ResponseType.Info_CancelUpdate);
						tempApkStream.close();
						tempApkFile.delete();
					} else {

						tempApkStream.flush();
						tempApkStream.close();

						hadDownloadCount = 0;
						Message message = new Message(); // 下载成功
						message.what = ResponseType.Info_UpdateSuccess;
						message.obj = tempApkFile;
						messageHandler.sendMessage(message);
					}
				} else {
					sendMessage(messageHandler, ResponseType.Info_NoSDCard);
				}
			} catch (SocketException e) {
				ExceptionLogHelper.PublishException(e);
				sendMessage(messageHandler, ResponseType.Info_NetWorkDisconnect);
				return;
			} catch (SocketTimeoutException e) {
				ExceptionLogHelper.PublishException(e);
				sendMessage(messageHandler, ResponseType.Info_NetWorkDisconnect);
				return;
			} catch (Exception e) {
				ExceptionLogHelper.PublishException(e);
				sendMessage(messageHandler,
						ResponseType.Info_GetUpdateInfoException);
				return;
			}
		}

		/*
		 * 发送消息
		 */
		private void sendMessage(Handler messageHandler, int message) {
			Message m = new Message();
			m.what = message;
			messageHandler.sendMessage(m);
		}

		/**
		 * 下載前的準備
		 *
		 * @throws FileNotFoundException
		 */
		private boolean prePareforUpdate() throws FileNotFoundException {
			// 判断SD卡是否存在，并且是否具有读写权限
			if (Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {
				// 保存下载更新文件的路径
				String mSavePath = Environment.getExternalStorageDirectory()
						+ "/SJB" ;
				File file = new File(mSavePath);
				// 判断文件目录是否存在
				if (!file.exists()) {
					file.mkdir();
				}
				tempApkFile = new File(mSavePath, updateFileInfo.ApkName);
				if (tempApkFile.exists())
					tempApkFile.delete();
				tempApkStream = new FileOutputStream(tempApkFile);

				return true;
			}
			return false;
		}
	}
}
