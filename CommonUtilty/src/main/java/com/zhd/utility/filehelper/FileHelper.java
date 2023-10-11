package com.zhd.utility.filehelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.R.string;
import android.os.Environment;
import android.util.Log;

public class FileHelper {

    private static DateFormat TIME_FORMATTER = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss");

    public static void write3gStatus(String msg, String fileName) {
        try {
            // long timestamp = System.currentTimeMillis();
            // String time = formatter.format(new Date());
            if (Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {

                String logPath = "";
                logPath = Environment.getExternalStorageDirectory()+"/call3g";

                File dir = new File(logPath);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                // 覆盖的方式
                FileOutputStream fos = new FileOutputStream(logPath + "/"
                        + fileName, false);
                fos.write(msg.getBytes());
                fos.close();
            }
        } catch (Exception e) {
            Log.e("serialport", "an error occured while writing file...", e);
        }
    }

    public static String read3gStatus(String fileName) {
        File file = new File( Environment.getExternalStorageDirectory()+"/call3g", fileName);
        byte[] buffer = new byte[500];
        FileInputStream fis;
        String result = "";

        try {
            fis = new FileInputStream(file);
            int length = fis.available();

            if (length == 0)
                return result;

            fis.read(buffer);
            fis.close();

            result = new String(buffer, "UTF-8");

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "";
        }

        return result;

    }

    public static Boolean checkIP(String IP) {
        if (IP == null || IP == "")
            return false;

        try {
            String[] fields = IP.split("\\.");
            if (fields.length != 4)
                return false;
            for (String str : fields) {
                int field = Integer.valueOf(str);
                if (field > 255 || field < 0)
                    return false;
            }
            return true;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
    }

    /*
     * 文件是否存在
     */
    public static boolean fileIsExist(String filePath) {
        File f = new File(filePath);
        if (!f.exists()) {
            return false;
        }
        return true;
    }

    /**
     * 删除文件
     *
     * @param filePath
     * @return
     */
    public static boolean deleteFile(String filePath) {
        File f = new File(filePath);
        if (f.exists()) {
            return f.delete();
        }
        return false;
    }

    /**
     * 创建文件夹
     *
     * @param folderPath
     *            文件夹路径
     * @return
     */
    public static boolean createFolder(String folderPath) {
        File file = new File(folderPath);
        if (!file.exists()) {
            try {
                // 按照指定的路径创建文件夹
                file.mkdirs();
            } catch (Exception e) {
                // TODO: handle exception
                return false;
            }
        }
        return true;
    }

    public static int reInt(File[] subFile, int i) {
        String filename = subFile[i].getName();
        String abc = filename.replace("-", "");
        abc = abc.replace(".txt", "");
        try{
            return Integer.parseInt(abc);
        }
        catch (Exception e)
        {
            return 0;
        }

    }
    public static void deleteFile1() {
        String logPath = "";

        logPath =  Environment.getExternalStorageDirectory()+"/GnssLog/zcby";
        File file = new File(logPath);
        if (file.exists()) {
            File[] subFile = file.listFiles();
            int small = 0;
            for (int iFileLength = 0; iFileLength < subFile.length; iFileLength++) {
                // 判断是否为文件夹
                if (!subFile[iFileLength].isDirectory()) {
                    String filename = subFile[iFileLength].getName();
                    Log.v("eee", iFileLength + "文件名 ： " + filename + ";"
                            + filename.length());
                    if (filename.length() != 14) {
                        deleteFile(logPath + "/" + filename);

                    }
                }
            }
            subFile = file.listFiles();
            int len = subFile.length;// 单独把数组长度拿出来，提高效率
            File insertNumfile;
            int insertNum;
            for (int i = 1; i < len; i++) {// 因为第一次不用，所以从1开始

                insertNumfile=subFile[i];
                insertNum=reInt( subFile,  i);
                int j = i - 1;// 序列元素个数

                while (j >= 0 && reInt( subFile,  j)  > insertNum) {// 从后往前循环，将大于insertNum的数向后移动
                    subFile[j+1] = subFile[j];// 元素向后移动
                    j--;
                }
                subFile[j + 1] =insertNumfile;// 找到位置，插入当前元素
            }
            for (int i = 0; i < len-10; i++) {// 因为第一次不用，所以从1开始
                String filename = subFile[ i ].getName();
                Log.v("eee",  i  + "文件名 ： " + filename + ";"
                        + filename.length());
                deleteFile(logPath + "/" + filename);
            }

        }

    }

    public static void deleteFile() {
        String logPath = "";

        logPath = Environment.getExternalStorageDirectory()+"/GnssLog";
        File file = new File(logPath);
        if (file.exists()) {
            File[] subFile = file.listFiles();
            int small = 0;
            for (int iFileLength = 0; iFileLength < subFile.length; iFileLength++) {
                // 判断是否为文件夹
                if (!subFile[iFileLength].isDirectory()) {
                    String filename = subFile[iFileLength].getName();
                    Log.v("eee", iFileLength + "文件名 ： " + filename + ";"
                            + filename.length());
                    if (filename.length() != 14) {
                        deleteFile(logPath + "/" + filename);

                    }
                }
            }
            subFile = file.listFiles();
            int len = subFile.length;// 单独把数组长度拿出来，提高效率
            File insertNumfile;
            int insertNum;
            for (int i = 1; i < len; i++) {// 因为第一次不用，所以从1开始

                insertNumfile=subFile[i];
                insertNum=reInt( subFile,  i);
                int j = i - 1;// 序列元素个数

                while (j >= 0 && reInt( subFile,  j)  > insertNum) {// 从后往前循环，将大于insertNum的数向后移动
                    subFile[j+1] = subFile[j];// 元素向后移动
                    j--;
                }
                subFile[j + 1] =insertNumfile;// 找到位置，插入当前元素
            }
            for (int i = 0; i < len-10; i++) {// 因为第一次不用，所以从1开始
                String filename = subFile[ i ].getName();
                Log.v("eee",  i  + "文件名 ： " + filename + ";"
                        + filename.length());
                deleteFile(logPath + "/" + filename);
            }

        }

    }

}
