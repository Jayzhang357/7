package com.zhd.utility.filehelper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.zhd.commonhelper.CommonUtil;

import android.R.string;
import android.os.Environment;
import android.text.method.DateTimeKeyListener;
import android.util.Log;

public class LogHelper {
    private static File sdCardDir = Environment.getExternalStorageDirectory();
    private static Boolean isFirst = true;
    private static String STEERPATH =  Environment.getExternalStorageDirectory()+"/HiFarm/steerLog";

    public static void writeByte(byte[] buffer, String name, Boolean isClear)
            throws IOException {

        File saveFile = new File(sdCardDir, name);
        FileOutputStream outStream;

        if (!saveFile.exists()) {
            saveFile.createNewFile();
        } else {

        }

        try {
            outStream = new FileOutputStream(saveFile, !isClear);
            if (isClear) {
                byte[] b = { 32 };
                outStream.write(b);
            } else
                outStream.write(buffer);

            outStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public final static DateFormat TIMEFORMAT = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss");
    public final static DateFormat DATEFORMAT = new SimpleDateFormat(
            "yyyy-MM-dd");

    public static void recordError(String str) {
        try {
            String fileName = "serialport-crash.txt";
            if (Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {

                String logPath = "";
                logPath =  Environment.getExternalStorageDirectory()+"/HiFarm/SerialportError";

                File dir = new File(logPath);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                FileOutputStream fos = new FileOutputStream(logPath + "/"
                        + fileName);
                fos.write(str.getBytes());
                fos.close();
            }
        } catch (Exception e) {
            Log.e("serialport", "an error occured while writing file...", e);
        }
    }

    public static void recordSteer(String str, Date date) {
        try {
            if (Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
                FileHelper.createFolder(STEERPATH);

                String logPath = "";
                String time = TIMEFORMAT.format(date);
                String dateString = DATEFORMAT.format(date);
                logPath = STEERPATH + "/" + dateString + ".txt";
                try {
                    BufferedWriter bw = new BufferedWriter(new FileWriter(
                            logPath, true));
                    bw.write(String.format("\r\n%s:%s", time, str));
                    bw.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String GNSS_PATH =  Environment.getExternalStorageDirectory()+"/GnssLog";

    public static void recordNmea(String str, Date date) {
        try {
            if (Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
                FileHelper.createFolder(GNSS_PATH);

                String logPath = "";
                String time = TIMEFORMAT.format(date);
                String dateString = new SimpleDateFormat("yyyy-MM-dd")
                        .format(date);

                logPath = GNSS_PATH + "/" + dateString + ".txt";
                try {
                    BufferedWriter bw = new BufferedWriter(new FileWriter(
                            logPath, true));
                    bw.write(str);
                    bw.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void recordNmea(String str, String date) {
        try {
            if (Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
                FileHelper.createFolder(GNSS_PATH);

                String logPath = "";

                logPath = GNSS_PATH + "/" + date + ".txt";
                try {
                    BufferedWriter bw = new BufferedWriter(new FileWriter(
                            logPath, true));
                    bw.write(str);
                    bw.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public static void recordNmea_z(String str, Date date) {
        try {
            if (Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
                FileHelper.createFolder(GNSS_PATH + "/zcby");

                String logPath = "";

                String dateString = new SimpleDateFormat("yyyy-MM-dd")
                        .format(date);
                logPath = GNSS_PATH + "/zcby/" + dateString + ".txt";
                try {
                    BufferedWriter bw = new BufferedWriter(new FileWriter(
                            logPath, true));
                    bw.write(str);
                    bw.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void recordNmea_z(String str, String date) {
        try {
            if (Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
                FileHelper.createFolder(GNSS_PATH+ "/zcby");

                String logPath = "";

                logPath = GNSS_PATH + "/zcby/" + date + ".txt";
                try {
                    BufferedWriter bw = new BufferedWriter(new FileWriter(
                            logPath, true));
                    bw.write(str);
                    bw.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String TEST_PATH =  Environment.getExternalStorageDirectory()+"/testLog";

    public static void recordTest(String str, Date date, boolean isAppend) {
        try {
            if (Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
                FileHelper.createFolder(TEST_PATH);

                String logPath = "";
                String time = TIMEFORMAT.format(date);
                String dateString = DATEFORMAT.format(date);
                logPath = TEST_PATH + "/" + dateString + ".txt";
                try {
                    BufferedWriter bw = new BufferedWriter(new FileWriter(
                            logPath, isAppend));
                    bw.write(str);
                    bw.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static final String COMM_LOG_PATH =  Environment.getExternalStorageDirectory()+"/communicationLog";

    public static void recordCommLog(String str, Date date, boolean isAppend) {
        try {
            if (Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
                FileHelper.createFolder(COMM_LOG_PATH);

                String logPath = "";
                String dateString = DATEFORMAT.format(date);
                logPath = COMM_LOG_PATH + "/" + dateString + ".txt";
                try {
                    BufferedWriter bw = new BufferedWriter(new FileWriter(
                            logPath, isAppend));
                    bw.write(str);
                    bw.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
