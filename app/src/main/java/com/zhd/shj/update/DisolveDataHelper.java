package com.zhd.shj.update;

import android.util.Base64;

import org.ksoap2.serialization.SoapObject;

import java.text.ParseException;

/**
 * 将从.Net Webservices 获取的对象解析为本地的实体对象
 *
 * @author Administrator
 *
 */
public class DisolveDataHelper {

	/**
	 * 解析需要更新的APK信息
	 *
	 * @param soapObjects
	 * @return
	 * @throws ParseException
	 */
	public static UpdateFileInfo getUpdateFileInfo(Object soapObject) {

		SoapObject obj = (SoapObject) soapObject;
		if (obj == null)
			return null;

		UpdateFileInfo updateFileInfo = new UpdateFileInfo();
		updateFileInfo.NeedUpdate = Boolean.valueOf(String.valueOf(obj
				.getProperty("NeedUpdate")));
		updateFileInfo.UpdateFileLength = Integer.valueOf(String.valueOf(obj
				.getProperty("UpdateFileLength")));

		return updateFileInfo;
	}

	/**
	 *
	 * @param object
	 * @return
	 */
	public static AutoUpdateInfo getAutoUpdateInfo(Object object) {
		//SoapObject obj = (SoapObject) object;
		if (object == null)
			return null;

		AutoUpdateInfo autoUpdateInfo = new AutoUpdateInfo();
		/*autoUpdateInfo.IsFinish = Boolean.valueOf(String.valueOf(obj
				.getProperty("IsFinish")));*/
		try {
			/*SoapPrimitive soapPrimitive = (SoapPrimitive) obj
					.getProperty("FileValue");*/
			String str = object.toString();

			if(str==""||str=="anyType{}")
			{
				return null;
			}
			byte[] bytes = Base64.decode(str, Base64.DEFAULT);
			autoUpdateInfo.FileValue = bytes;
		} catch (ClassCastException ex) { // 下载空的附件时会引发该异常
			autoUpdateInfo.FileValue = null;
		}

		return autoUpdateInfo;
	}
}
