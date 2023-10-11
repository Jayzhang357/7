package com.zhd.shj;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Stack;

/**
 * 应用程序Activity管理类：用于Activity管理和应用程序退出
 * @created 2012-3-21
 */
public class AppManager {

    private static Stack<Activity> activityStack;
    private static AppManager instance;

    private AppManager(){}
    /**
     * 单一实例
     */
    public static AppManager getAppManager(){
        if(instance==null){
            instance=new AppManager();
        }
        return instance;
    }
    /**
     * 添加Activity到堆栈
     */
    public void addActivity(Activity activity){
        if(activityStack==null){
            activityStack=new Stack<Activity>();
        }
        activityStack.add(activity);
    }
    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    public Activity currentActivity(){
        Activity activity=activityStack.lastElement();
        return activity;
    }
    /**
     * 结束当前Activity（堆栈中最后一个压入的）
     */
    public void finishActivity(){

        saveCrashInfo2File("退出了软件1");
        Activity activity=activityStack.lastElement();
        finishActivity(activity);

    }
    private String saveCrashInfo2File(String ex) {


        try {

            String fileName = "crash" + ".log";
            if (Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
                String path =  Environment.getExternalStorageDirectory()+"/HiFarmLog/";
                File dir = new File(path);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                BufferedWriter bw = new BufferedWriter(new FileWriter(
                        path + fileName, true));
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式

                bw.write("当前时间："+df.format(new Date())+":::::"+ex.toString());
                bw.close();

            }
            return fileName;
        } catch (Exception e) {

        }
        return null;
    }
    /**
     * 结束指定的Activity
     */
    public void finishActivity(Activity activity){

        if(activity!=null){
            activityStack.remove(activity);
            activity.finish();
            activity=null;
        }

    }
    /**
     * 结束指定类名的Activity
     */
    public void finishActivity(Class<?> cls){
        Log.e("退出软件", "退出软件3");

        for (Activity activity : activityStack) {
            if(activity.getClass().equals(cls) ){
                finishActivity(activity);
            }
        }

    }
    /**
     * 结束所有Activity
     */
    public void finishAllActivity(){
        Log.e("退出软件", "退出软件5");

        for (int i = 0, size = activityStack.size(); i < size; i++){
            if (null != activityStack.get(i)){
                activityStack.get(i).finish();
            }
        }
        activityStack.clear();
    }
    /**
     * 退出应用程序
     */
    public void AppExit(Context context) {
        Log.e("退出软件", "退出软件6");
        saveCrashInfo2File("退出了软件5");
        try {
            finishAllActivity();
            ActivityManager activityMgr= (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            activityMgr.restartPackage(context.getPackageName());
            System.exit(0);
        } catch (Exception e) {        }
    }
}