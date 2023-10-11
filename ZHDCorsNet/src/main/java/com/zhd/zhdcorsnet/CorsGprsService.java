package com.zhd.zhdcorsnet;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.text.method.DateTimeKeyListener;
import android.util.Base64;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import android_serialport_api.SerialPort;

import com.zhd.bd970.manage.BD970Manager;
import com.zhd.bd970.manage.interfaces.*;
import com.zhd.gnssmanager.DifferDataReceive;
import com.zhd.gnssmanager.GnssDataReceive;
import com.zhd.gnssmanager.Helper;

import com.zhd.serialportmanage.*;

public class CorsGprsService extends Service implements
        ReceiveGGAMessageListner {

    private static String m_GGA = "$GPGGA,094828.000,2259.01616,N,11322.05815,E,2,16,0.7,062.97,M,-6.0,M,,*77\r\n";
    private static String m_Ip;
    private static int m_Port;
    private static String m_UserName;
    private static String m_PassWord;
    private static String m_SourceNoe;
    private static Socket m_CorsSocket;
    private static boolean m_IsReconnect;
    private OutputStream m_CorsOutputStream;
    private InputStream m_CorsInputStream;
    private boolean m_IsConnected = false;
    private Thread m_SendNetDataThread;
    public static final int BAURATE = 19200;
    private String mLogPath = "";
    // private long mCheckTimestamp = 0l;//记录上次检测网络的时间
    // private static final long CHECKINTERVAL = 5000;//每次检测网络的时间间隔(ms)
    // private boolean mCheckNetResult = false;
    private String mStrHead = "";
    private Object mObject = new Object();
    private StateBroadcast stateBroadcast;
    private SerialPortManager mSerialPortManager = null;
    private SerialPortManager mDiffSerialPortManager = null;
    private BD970Manager mBd970Manager = null;
   // private GnssDataReceive mGnssDataReceive = null;
    private DifferDataReceive mDifferDataReceive = null;

    private Socket mBaiduSocket;
    private Thread mThread = null;

    private boolean mIsRunning = true;

    // 为本服务的广播参数
    private Intent intent = new Intent(ACTION_SENDRESULT);

    /**
     * GGA文件
     */
    public static final String FILE_GGA = "/dev/gps_gpgga";
    /**
     * 串口文件
     */
    public static final String SERIAL_COM_2 = "/dev/ttymxc1";

    public static final String ACTION_SENDRESULT = "sendresult";

    public static final String MY_FLAGS_FOR_CORS = "myFlags";
    /**
     * 网络异常
     */
    public static final int FLAG_NETERROR = 0x107;
    /**
     * 返回连接状态的标志
     */
    public static final int FLAG_NETRESULT = 0x108;
    /**
     * 广播转发差分数据信息的标志
     */
    public static final int FLAG_DATARECEIVE = 0x109;
    /**
     * 返回连接状态的参数名
     */
    public static final String RESULT_NETRESULT = "NetResult";
    /**
     * 返回解状态的参数名
     */
    public static final String RESULT_NETPROGRESS = "Isreceive";

    private boolean mIsFirst = true;

    @Override
    public void onCreate() {
        stateBroadcast = new StateBroadcast(getApplicationContext());
        Configxml configxml = Configxml.getInstantce(getApplicationContext());

        //int motherType = Helper.getMotherType(getApplicationContext());

      /*  mGnssDataReceive = GnssDataReceive.getInstance(6, configxml.Compath, Integer.parseInt(configxml.Baudrate));
        mSerialPortManager = mGnssDataReceive.getCom3GpsSerialPort();
        mGnssDataReceive.getParser().setReceiveGGAMessageListener(this);*/
    //    mSerialPortManager.Start();
    }

    public void ConnectServer() {
        try {
            //
            Log.v("cors", "start connect");

            getLogPath();


            //	NetHelper.writeLog(mLogPath, "开始连接");
            String auth = ecodeBase64((m_UserName + ":" + m_PassWord)
                    .getBytes(Charset.forName("UTF-8")));
            auth = auth.substring(0, auth.length() - 1);
            mStrHead = "GET /" + m_SourceNoe + " HTTP/1.1\r\n";
            mStrHead += "User-Agent: NTRIP ZHDGPS\r\n";
            mStrHead += "Accept: */*\r\nConnection: close\r\n";
            mStrHead += "Authorization: Basic " + auth + "\r\n";
            mStrHead += "\r\n";

            writeToNet(mStrHead.getBytes(), 10000);

            int timeout = 3;
            while (mIsRunning) {
                Thread.sleep(300);
                //
                Log.e("cos", "read from net..");

                byte[] buff = readFromNet();
                if (buff != null && buff.length > 0) {
                    // 验证登陆
                    String result = new String(buff).trim();
                    Log.e("COS", result);

                    //	NetHelper.writeLog(mLogPath, result);
                    if (result.contains("Bad Password")) {
                        intent.putExtra(RESULT_NETRESULT, NetResult.GPRS_UserPassword_Fail);
                        intent.putExtra(MY_FLAGS_FOR_CORS, FLAG_NETRESULT);
                        sendBroadcast(intent);
                    } else if (result.contains("200")) {
                        m_IsConnected = true;
                        if (m_SendNetDataThread == null) {
                            m_SendNetDataThread = new Thread() {
                                @Override
                                public void run() {
                                    //
                                    Log.e("cors", "send to net..");


                                    sendNetData();
                                }
                            };
                            m_SendNetDataThread.start();
                        }
                        intent.putExtra(RESULT_NETRESULT, NetResult.Success);
                        intent.putExtra(MY_FLAGS_FOR_CORS, FLAG_NETRESULT);
                        sendBroadcast(intent);

                        return;
                    } else if (result.contains("401 Unauthorized")) {

                        //
                        Log.v("cors", "401 Unauthorized");

                        m_IsConnected = false;
                        // 登陆失败
                        intent.putExtra(RESULT_NETRESULT,
                                NetResult.GPRSSet_UserName_Occupied);
                        //				NetHelper.writeLog(mLogPath, "用户不可用");
                        intent.putExtra(MY_FLAGS_FOR_CORS, FLAG_NETRESULT);
                        sendBroadcast(intent);
                        closeCorsSocket();
                        return;
                    }
                } else {
                    //
                    Log.v("cors", "connect failed");

                    timeout--;
                    if (timeout > 0) {
                        writeToNet(mStrHead.getBytes(), 10000);
                        Thread.sleep(1000);
                    } else {

                        intent.putExtra(RESULT_NETRESULT,
                                NetResult.GPRSSet_NoResponse);


                        //		NetHelper.writeLog(mLogPath, "服务器无响应");
                        intent.putExtra(MY_FLAGS_FOR_CORS, FLAG_NETRESULT);
                        sendBroadcast(intent);
                        closeCorsSocket();
                        Log.e("cos","qqqqqqq");

                        return;
                    }
                }
            }
        } catch (Exception e) {
            Log.e("cors", "errorConnectServer--" + e.toString());
            // NetHelper.writeLog(mLogPath, "ConnectServer:" + e.toString());
        }

        if (m_IsReconnect) {
            intent.putExtra(RESULT_NETRESULT, NetResult.Fail_And_Reconnection);
        } else {
            intent.putExtra(RESULT_NETRESULT, NetResult.Fail);
        }

        //NetHelper.writeLog(mLogPath, "登陆失败");

        closeCorsSocket();
        Log.e("cos", "");
        intent.putExtra(MY_FLAGS_FOR_CORS, FLAG_NETRESULT);
        sendBroadcast(intent);
    }

    /**
     * 关闭Socket
     */
    private void closeCorsSocket() {
        try {
            if (m_CorsSocket != null) {
                if (!m_CorsSocket.isClosed()) {
                    m_CorsSocket.close();
                }
                m_CorsSocket = null;
            }

            // stateBroadcast.breakDiff();

        } catch (Exception e) {
            Log.i("Cors", "ConnectServer--" + e.toString());

            //NetHelper.writeLog(mLogPath, "closeCorsSocket:" + e.toString());

        }
    }

    public boolean CutServer() {
        try {
            m_IsConnected = false;
            if (m_CorsInputStream != null) {
                m_CorsInputStream.close();
                m_CorsInputStream = null;
            }
            if (m_CorsOutputStream != null) {
                m_CorsOutputStream.close();
                m_CorsOutputStream = null;
            }

            closeCorsSocket();

            mIsRunning = false;

            if (mThread != null) {
                mThread.interrupt();
            }

            return true;
        } catch (Exception e) {
            Log.i("Cors", "ConnectServer--" + e.toString());

            // NetHelper.writeLog(mLogPath, "CutServer:" + e.toString());
        }
        return false;
    }

    /**
     * 读数据
     *
     * @return
     * @throws IOException
     */
    private byte[] readFromNet() {
        try {
            // if (!checkNetConnected()) {
            // closeCorsSocket();
            // return null;
            // }
            synchronized (mObject) {
                if (m_CorsSocket == null) {
                    m_CorsSocket = new Socket();
                }

                if (m_CorsSocket.isConnected() == false) {
                    SocketAddress remoteAddr = new InetSocketAddress(m_Ip,
                            m_Port);

                    //
                    Log.v("cors", "connecting");

                    m_CorsSocket.connect(remoteAddr, 10000);
                    m_CorsInputStream = m_CorsSocket.getInputStream();
                    m_CorsOutputStream = m_CorsSocket.getOutputStream();
                    m_CorsOutputStream.write(mStrHead.getBytes());
                    m_CorsOutputStream.flush();
                }

                if (m_CorsInputStream == null) {
                    m_CorsInputStream = m_CorsSocket.getInputStream();
                }

                int sum = m_CorsInputStream.available();

                // Log.e("===readFromNet", String.valueOf(sum));
                if (sum > 0) {
                    byte[] reData = new byte[sum];
                    int result = m_CorsInputStream.read(reData, 0,
                            reData.length);
                    if (result > 0) {
                        System.out.println("diffdata:" + new String(reData));

                        return reData;
                    }
                }
            }
        } catch (Exception ex) {
            Log.i("Cors", "ConnectServer--" + ex.toString());

            // NetHelper.writeLog(mLogPath, "readFromNet:" + ex.toString());

            // closeCorsSocket();
        }
        return null;
    }

    /**
     * 写数据
     *
     * @param data
     * @throws IOException
     */
    private boolean writeToNet(byte[] data, int timeOut) {
        stateBroadcast.receiveDiff();
        try {
            // if (!checkNetConnected()) {
            // closeCorsSocket();
            // return;
            // }
            synchronized (mObject) {
                if (m_CorsSocket == null) {
                    m_CorsSocket = new Socket();
                }

                if (m_CorsSocket.isConnected() == false) {
                    SocketAddress remoteAddr = new InetSocketAddress(m_Ip,
                            m_Port);
                    m_CorsSocket.connect(remoteAddr, timeOut);
                    // m_CorsInputStream = m_CorsSocket.getInputStream();
                    m_CorsOutputStream = m_CorsSocket.getOutputStream();
                    m_CorsOutputStream.write(mStrHead.getBytes());
                }

                if (m_CorsOutputStream == null) {
                    m_CorsOutputStream = m_CorsSocket.getOutputStream();
                }

                m_CorsOutputStream.write(data);
                m_CorsOutputStream.flush();
            }
        } catch (Exception ex) {
            Log.i("cors", ex.toString());

            // NetHelper.writeLog(mLogPath, "writeToNet:" + ex.toString());

            // closeCorsSocket();

            return false;
        }

        return true;
    }

    private void sendNetData() {
        try {

		/*	if (mDifferDataReceive == null)
				mDifferDataReceive = DifferDataReceive.getInstance();
*/
            byte[] buff = null;
            int times = 0;
            boolean get_cf=false;
            while (m_IsConnected) {
                buff = readFromNet();
                Log.e("cos", "循环1");
                if (buff != null && buff.length > 0) {
                    //Log.e("差分", buff.length+"");
                   Log.e("cos", "收到差分");
                    get_cf=true;
                    intent.putExtra("CF", buff);
                    intent.putExtra(RESULT_NETRESULT, NetResult.sendCF);
                    intent.putExtra(MY_FLAGS_FOR_CORS, FLAG_DATARECEIVE);
                    sendBroadcast(intent);
                    times = 0;
                } else {
                    times++;
                    Log.e("没收到差分", times+"");
                    // NetHelper.writeLog(mLogPath, "times:" +
                    // Integer.toString(times));

                    if (times > 20) {
                        m_IsConnected = false;
                        intent.putExtra(RESULT_NETRESULT, NetResult.GPRSSet_NoResponse_And_Reconnection);
                        intent.putExtra(MY_FLAGS_FOR_CORS, FLAG_NETERROR);
                        sendBroadcast(intent);
                        //	NetHelper.writeLog(mLogPath, "网络异常,掉线了");
                        Log.e("cos", "彻底掉线了");
                        return;
                    }
                }
                Thread.sleep(500);
            }
        } catch (Exception e) {
            Log.i("CorsGprsService", "sendNetData_2:" + e.toString());

            //	NetHelper.writeLog(mLogPath, "sendNetData:" + e.toString());
        }
    }

    private String ecodeBase64(byte[] buf) {
        return Base64.encodeToString(buf, Base64.DEFAULT);
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
        Log.e("cos", "光笔");
        //mGnssDataReceive.getParser().setReceiveGGAMessageListener(null);
        CutServer();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent == null) {
            return START_STICKY;
        }
        m_Ip = intent.getExtras().getString("ip");
        try {
            m_Port = Integer.parseInt(intent.getExtras().getString("port"));
        } catch (Exception e) {

            m_Port = 0;
        }
        m_UserName = intent.getExtras().getString("username");
        m_PassWord = intent.getExtras().getString("password");
        m_SourceNoe = intent.getExtras().getString("sourcenode");
        m_IsReconnect = intent.getExtras().getBoolean("reconnect");
        mThread = new MyThread();
        mThread.start();
        flags = Service.START_FLAG_REDELIVERY;

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class MyThread extends Thread {

        @Override
        public void run() {
            try {
                ConnectServer();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // /**
    // * 检测网络是否连通
    // * @return
    // */
    // private boolean checkNetConnected(){
    // if (mCheckTimestamp == 0l) {
    // mCheckNetResult = NetWorkHelper.isAvailable(CorsGprsService.this);
    // mCheckTimestamp = System.currentTimeMillis();
    // } else {
    // long tmpCurTimeMs = System.currentTimeMillis();
    // if (mCheckTimestamp + CHECKINTERVAL < tmpCurTimeMs) {
    // mCheckNetResult = NetWorkHelper.isAvailable(CorsGprsService.this);
    // mCheckTimestamp = tmpCurTimeMs;
    // }
    // }
    // return mCheckNetResult;
    // }

    private void getLogPath() {
        if (mLogPath == null || mLogPath.equals("")) {
            mLogPath = NetHelper.getSDPath();
            mLogPath += "/ZNZCORS";
            File logPath = new File(mLogPath);
            if (!logPath.exists()) {
                logPath.mkdirs();
            }

            String dateString = NetHelper.mDateFormat
                    .format(NetHelper.UTC_DATE);

            mLogPath += "/VRSLog_" + dateString + ".txt";
        }
    }

    // 20s 发送一次
    private int mTimes = 0;

    /**
     * 发送整条GGA完整数据给站点，取代NMEAListener
     */
    @Override
    public void TellReceiveGGAMessage(String message) {
        // TODO Auto-generated method stub
        Log.e("cos", "转发GGA");
        mTimes++;
        if (mTimes != 1 && (mTimes % 2 != 0))
            return;

        Log.e("gga", "sended gga" + " times:" + Integer.toString(mTimes));

        String tmpGPGGA = message;
        if (tmpGPGGA.contains("GGA") && !tmpGPGGA.startsWith("$GPGGA")) {
            tmpGPGGA = NetHelper.getGPGGAFromGGA(tmpGPGGA);
        }
        if (tmpGPGGA.contains("$GPGGA") && tmpGPGGA.contains("\r\n")) {
            if (tmpGPGGA.length() > 50) {
                m_GGA = tmpGPGGA;
            }
            if (m_IsConnected) {
                try {
                    writeToNet(m_GGA.getBytes(), 8000);
                } catch (Exception e) {
                    e.printStackTrace();

                }
            }
        }

        if (mTimes > 100000)
            mTimes = 0;
    }
}
