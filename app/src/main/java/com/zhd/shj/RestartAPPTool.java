package com.zhd.shj;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.zhd.killSelfService;

public class RestartAPPTool {
    public static void restartAPP(Context context, long Delayed){

        /**开启一个新的服务，用来重启本APP*/
        Intent intent1=new Intent(context, killSelfService.class);
        intent1.putExtra("PackageName",context.getPackageName());
        intent1.putExtra("Delayed",Delayed);
        Log.e("重启",context.getPackageName());
        context.startService(intent1);

        /**杀死整个进程**/
        android.os.Process.killProcess(android.os.Process.myPid());
    }
    /***重启整个APP*/
    public static void restartAPP(Context context){
        restartAPP(context,2000);
    }
}
