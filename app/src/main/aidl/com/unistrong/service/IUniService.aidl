// IUniService.aidl
package com.unistrong.service;

// Declare any non-default types here with import statements
import com.unistrong.service.IUniKeyListener;
import com.unistrong.service.IUniGpioListener;

interface IUniService {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
     String installAppSlient(String  filePath,int type);
	 void registerCallback(IUniKeyListener cb);
     void unregisterCallback(IUniKeyListener cb);
     int setAppfilter();
     void cancelAppfileter();
     int setApplist(in List<String> datas);
     List<String> getApplist();
	 int otaUpdate(String filePath);
	 void restoreFactotySettings();
	 void setTime(String time);
	 
	 void setGpioHightOrLow(String name,int status);
	 void registerGpioCallback(IUniGpioListener cb);
	 void unregisterGpioCallback(IUniGpioListener cb);
}
