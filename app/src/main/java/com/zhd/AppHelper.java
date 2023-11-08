package com.zhd;

import android.app.AlertDialog;
import android.app.Application;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Environment;
import android.util.Log;


import com.zhd.shj.CommonHelper;
import com.zhd.shj.QXsdkmap;
import com.zhd.shj.R;
import com.zhd.shj.entity.JobInfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Locale;

public class AppHelper extends Application {
    public static String kzqversion;
    private static Context mContext;
    public static  String AS="";
    public  static String AK="";
    private static String WORK_FOLDER_PATH = "";
    public static int  Language=0;
    private static AppHelper INSTANCE;
    public static JobInfo JOB_INFO = null;
    public static int JobRecordID=-1;;
    public static int zhuanY = 200;
    public static int zhuanX = 400;
    public  static float RATIO_VALUE = 1f; // 绘图的比例
    public  static float width = 3f; // 绘图的比例
    public static String USB_PATH = "/mnt/udisk/ExportData";
    public static String mQXsdkmap ="";
    /* Checks if external storage is available for read and write */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }
    @Override
    public void onCreate() {

        super.onCreate();
        mContext = getApplicationContext();
    }
    public static String get8(int wordValue) {
        String abc = Integer.toHexString(wordValue).toUpperCase();
        if (abc.length() == 1)
            abc = "0" + abc;


        return abc;
    }
   public static void showmessage(Context context, String abc,String ok)
   {
       AlertDialog.Builder builder = new AlertDialog.Builder(context);

       builder.setMessage(abc); // 设置对话框内容
       builder.setPositiveButton(ok, new DialogInterface.OnClickListener() {
           public void onClick(DialogInterface dialog, int whichButton) {
               // 点击"确定"按钮后的处理
               dialog.dismiss();
           }
       });

       AlertDialog dialog = builder.create();
       dialog.show();
   }
    public static String get16(int wordValue) {

        String hexString = String.format("%04X", wordValue);


        String highByte = hexString.substring(0, 2);
        String lowByte = hexString.substring(2, 4);

        return highByte + lowByte;
    }
    public static byte[] getBytes(String hexString) {
        if (hexString == null) {
            return new byte[]{};
        }
        if (hexString.length() % 2 != 0) {
            hexString = "0" + hexString;
        }

        int size = hexString.length() / 2;
        byte[] hex = new byte[size];
        for (int i = 0; i < size; i++) {
            hex[i] = Integer.valueOf(hexString.substring(i * 2, i * 2 + 2), 16).byteValue();
        }
        return hex;
    }
    public static Context getContext() {
        return mContext;
    }

    public static boolean copyJobDataToUsb(String path) {
        boolean isw = isExternalStorageWritable();
        Log.i("storage", Boolean.toString(isw));

        CommonHelper.createFolder(path+"/ExportData");
        String exportDbPathString = getWorkFolderPath() + "/export/shj.db3";
        String copyPath = path+"/ExportData" + "/shj.db3";
        FileOutputStream fos = null;

        if (CommonHelper.fileIsExist(exportDbPathString)) {

            CommonHelper.deleteFile(copyPath);
            // 获得封装数据库文件的InputStream对象
            try {
                byte[] buffer = new byte[8192];
                int count = 0;

                // 拷贝导出数据库到USB
                InputStream fis = new FileInputStream(exportDbPathString);
                fos = new FileOutputStream(copyPath);
                buffer = new byte[8192];
                count = 0;
                // 开始复制数据库文件
                while ((count = fis.read(buffer)) > 0) {
                    fos.write(buffer, 0, count);
                }
                fos.close();
                fis.close();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }
    public static boolean copyJobDataToNow(String path) {
        boolean isw = isExternalStorageWritable();
        Log.i("storage", Boolean.toString(isw));

        CommonHelper.createFolder(path+"/ExportData");
        String copyPath = getWorkFolderPath() + "/inport/shj.db3";
        String exportDbPathString = path+"/ExportData" + "/shj.db3";
        FileOutputStream fos = null;

        if (CommonHelper.fileIsExist(exportDbPathString)) {

            CommonHelper.deleteFile(copyPath);
            // 获得封装数据库文件的InputStream对象
            try {
                byte[] buffer = new byte[8192];
                int count = 0;

                // 拷贝导出数据库到USB
                InputStream fis = new FileInputStream(exportDbPathString);
                fos = new FileOutputStream(copyPath);
                buffer = new byte[8192];
                count = 0;
                // 开始复制数据库文件
                while ((count = fis.read(buffer)) > 0) {
                    fos.write(buffer, 0, count);
                }
                fos.close();
                fis.close();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }
    public static String getExternalDbPath() {
        return USB_PATH + "/shj.db3";
    }

    /**
     * 删除数据库文件
     *
     * @return
     */
    public static boolean deleteDatabse() {
        try {
            String dbPath = getDBPath();
            if (!dbPath.equals("")) {
                if (CommonHelper.fileIsExist(dbPath)) {
                    CommonHelper.deleteFile(dbPath);
                    // 重新生成新的数据库
                    getDBPath();
                }
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }
    /**
     * 获取系统工作文件夹路径（存放系统各个配置文件及数据库文件的文件夹路径）
     *
     * @return
     */
    public static String getWorkFolderPath() {
        if (WORK_FOLDER_PATH == "") {
            Boolean sdCardExist = Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
            if (sdCardExist) {
                File sdDir = Environment.getExternalStorageDirectory();// 获取根目录
                String sdPath = sdDir.toString();
                WORK_FOLDER_PATH = sdPath + "/SHJ";
                CommonHelper.createFolder(WORK_FOLDER_PATH);
            }
            // File sdDir = Environment.getDataDirectory();// 获取根目录
            // String sdPath = sdDir.toString();
            // WORK_FOLDER_PATH = sdPath + "/HiFarm";
            // Helper.createFolder(WORK_FOLDER_PATH);
        }
        return WORK_FOLDER_PATH;
    }

    public AppHelper() {
        INSTANCE = this;
    }

    public static AppHelper getInstance() {
        if (null == INSTANCE) {
            INSTANCE = new AppHelper();

        }
        // INSTANCE.getResources();
        return INSTANCE;
    }
    public static DecimalFormat DOUBLE_EIGHT_FORMAT = new DecimalFormat(
            "0.00000000");
    public static void copyFile() {
        try {
            int bytesum = 0;
            int byteread = 0;
            String oldPath = getDBPath();
            String newPath = getWorkFolderPath() + "/copy";
            File oldfile = new File(oldPath);
            File newfile = new File(newPath);
            if (!newfile.exists()) {
                newfile.mkdirs();
            }
            newPath += "/shj.db3";
            if (oldfile.exists()) { // 文件存在时
                InputStream inStream = new FileInputStream(oldPath); // 读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                int length;
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; // 字节数 文件大小
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
        } catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();

        }

    }
    public static boolean copybackFile() {
        boolean come = false;
        try {

            int bytesum = 0;
            int byteread = 0;
            String newPath = getDBPath();
            String oldPath = getWorkFolderPath() + "/copy/shj.db3";
            File oldfile = new File(oldPath);

            if (oldfile.exists()) { // 文件存在时
                InputStream inStream = new FileInputStream(oldPath); // 读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                int length;
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; // 字节数 文件大小
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
                come = true;
            }
            return come;
        } catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();
            return come;
        }

    }
    /**
     * 获取系统使用的数据库路径
     *
     * @return
     */
    public static String getDBPath() {
        String workPath = getWorkFolderPath();
        String dbPath = workPath + "/shj.db3";

        try {
            // 如果目录中不存在
            // 数据库文件，则从res\raw目录中复制这个文件到 根目录
            if (!CommonHelper.fileIsExist(dbPath)) {
                // 获得封装数据库文件的InputStream对象

                InputStream is = AppHelper.getInstance().getResources()
                        .openRawResource(R.raw.shj);
                FileOutputStream fos = new FileOutputStream(dbPath);
                byte[] buffer = new byte[8192];
                int count = 0;
                // 开始复制数据库文件
                while ((count = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, count);
                }

                fos.close();
                is.close();
            }
        } catch (Exception ex) {
            return null;
        }

        return dbPath;
    }

}
