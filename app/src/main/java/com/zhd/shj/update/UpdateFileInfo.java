package com.zhd.shj.update;

import java.io.Serializable;

@SuppressWarnings("serial")
public class UpdateFileInfo implements Serializable {

	public int UpdateFileLength = -1; // 更新文件总大小
	public boolean NeedUpdate = false; // 是否需要更新
	public String WebserviceUrl = null; // 服務器地址
	public String ApkName = null; // APK名稱
	public String method = ""; // APK名稱
}
