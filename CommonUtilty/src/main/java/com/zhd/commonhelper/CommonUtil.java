package com.zhd.commonhelper;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import android.R.integer;
import android.R.string;
import android.util.Log;

public class CommonUtil {
    public static int ECU_TYPE = EcuType.Ecu_S1.getValue();
    public static String ECU_IP = "10.1.1.1";
    public static java.util.Date mUTC_DATE = new java.util.Date();

    public enum EcuType {
        Ecu_S1(0), Ecu_H(1);

        EcuType(int value) {
            this.value = value;
        }

        private int value;

        public int getValue() {
            return this.value;
        }
    }

    /**
     * 度单位数值转弧度单位数值
     * @param
     * @return
     */
    public static double ConvertToRadian(double degreeValue) {
        return degreeValue * Math.PI / 180.00;
    }

    /**
     * 弧度转度
     * @param
     * @return
     */
    public static double ConvertToDegree(double radianValue) {
        return radianValue * 180 / Math.PI;
    }

    public static boolean pingEcu() {
        boolean result = false;
        try {
            String ipString = getLocalIpAddress();
            if (ipString != null) {
                if (ipString.contains("10.1.1")) {
                    ipString = "10.1.1.1";
                    ECU_TYPE = EcuType.Ecu_S1.getValue();
                } else if (ipString.contains("192.168")) {
                    ipString = "192.168.10.1";
                    ECU_TYPE = EcuType.Ecu_H.getValue();
                } else {
                    ipString = "";
                }
                result = ping(10, ipString);
            } else {
                ipString = "";
            }

            ECU_IP = ipString;
            Log.i("ecu", ECU_IP + "/" + Integer.toString(ECU_TYPE) + "/ipString:"
                    + ipString);
        } catch (Exception e) {
            return false;
        }

        return result;
    }

    public static boolean ping(int times, String ip) {
        if (ip.equals(""))
            return false;

        Process proc = null;
        try {

            String str = "ping -c 1 -w 1 " + ip;
            System.out.println(str);
            proc = Runtime.getRuntime().exec(str);
            int result = proc.waitFor();
            if (result == 0) {
                System.out.println("ping连接成功");
                return true;
            } else {
                System.out.println("ping测试失败");
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            proc.destroy();
        }

        return false;
    }

    /**
     * 获取本机的Ip地址
     * @return
     */
    public static String getLocalIpAddress() {
        String ipv4 = "";

        try {
            ArrayList<NetworkInterface> nilist = Collections.list(NetworkInterface
                    .getNetworkInterfaces());
            for (NetworkInterface ni : nilist) {
                ArrayList<InetAddress> ialist = Collections.list(ni.getInetAddresses());
                for (InetAddress address : ialist) {
                    if (!address.isLoopbackAddress()
                            &&address instanceof Inet4Address) {
                        return ipv4;
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("GetIp", ex.toString());
        }
        return ipv4;
    }

    public static float get3decimal(float value) {
        return ((int) (value * 10000)) / 10000f;
    }

    public static String stringFilter(String str) throws PatternSyntaxException {
        String regEx = "[/\\:*?<>|\"\n\t!！￥……@%^&（）：“”《》？，。；‘’]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("");
    }

    /**
     * 执行命令
     * @param paramString
     * @return
     */
    public static int execRootCmdSilent(String paramString) {
        try {
            Process localProcess = Runtime.getRuntime().exec("su");
            Object localObject = localProcess.getOutputStream();
            DataOutputStream localDataOutputStream = new DataOutputStream(
                    (OutputStream) localObject);
            String str = String.valueOf(paramString);
            localObject = str + "\n";
            localDataOutputStream.writeBytes((String) localObject);
            localDataOutputStream.flush();
            localDataOutputStream.writeBytes("exit\n");
            localDataOutputStream.flush();
            localProcess.waitFor();
            localObject = localProcess.exitValue();
            localDataOutputStream.close();
            return (Integer) localObject;
        } catch (Exception localException) {
            localException.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取目录下所有文件(按时间排序)
     * @param path
     * @return
     */
    public static List<File> getFileSort(String path) {
        List<File> list = getFiles(path, new ArrayList<File>());
        if (list != null && list.size() > 0) {
            Collections.sort(list, new Comparator<File>() {
                public int compare(File file, File newFile) {
                    String file1PathString = "";
                    String file2PathString = "";
                    String[] file1PathArr = null;
                    String[] file2PathArr = null;

                    file1PathString = file.getName();
                    file1PathString = file1PathString.split("\\.")[0];
                    file1PathArr = file1PathString.split("\\-");

                    file2PathString = newFile.getName();
                    file2PathString = file2PathString.split("\\.")[0];
                    file2PathArr = file2PathString.split("\\-");

                    return compareTwoString(file1PathArr, file2PathArr, 0);
                }
            });
        }

        return list;
    }

    /**
     * 递归获取两个日期字符串的对比结果
     * @param strArr1
     * @param strArr2
     * @param index
     * @return
     */
    private static int compareTwoString(String[] strArr1, String[] strArr2, int index) {
        if (strArr1.length != 0 && strArr2.length != 0 && index < (strArr1.length - 1)) {
            if (strArr1[index].compareTo(strArr2[index]) > 0)
                return -1;
            else if (strArr1[index].compareTo(strArr2[index]) == 0) {
                index++;
                return compareTwoString(strArr1, strArr2, index);
            } else
                return 1;
        } else {
            return 1;
        }
    }

    /**
     * 获取目录下所有文件
     * @param realpath
     * @param files
     * @return
     */
    public static List<File> getFiles(String realpath, List<File> files) {
        File realFile = new File(realpath);
        if (realFile.isDirectory()) {
            File[] subfiles = realFile.listFiles();
            for (File file : subfiles) {
                if (file.isDirectory()) {
                    getFiles(file.getAbsolutePath(), files);
                } else {
                    files.add(file);
                }
            }
        }
        return files;
    }
}
