package com.zhd.shj;

import android.content.Context;
import android.provider.Settings;

import java.io.DataOutputStream;
import java.io.IOException;


public class ExecTool {

    public static final String path = "/sys/class/gpio";
    /**
     *
     * PB10
     * @param paramS
     */
    public static void beep(String paramS){
        try {
            Process process = Runtime.getRuntime().exec("su");
            process.getOutputStream().write(paramS.getBytes());
            DataOutputStream dataOutputStream = new DataOutputStream(process.getOutputStream());
            dataOutputStream.writeBytes(paramS);
            dataOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void export(int param){
        beep("echo "+param + " > " +path + "/export" + "\n");
    }

    public static void gpio(int param, String paramS, int param2){
        beep("echo "+ paramS + " > "+path + "/gpio" + param + "/direction" + "\n" + "echo "+ param2 + " > "+path+ "/gpio"+param + "/value" +"\n");
    }

    public void unexport(int paramInt){
        beep("echo "+paramInt + " > "+path + "/unexport"+ "\n");
    }


    public static String get_cpu_ser(Context context){
        return Settings.Global.getString(context.getContentResolver(),"cpu_serial_number");
    }
}
