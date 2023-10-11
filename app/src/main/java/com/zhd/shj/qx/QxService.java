package com.zhd.shj.qx;

import static com.qxwz.sdk.core.Constants.QXWZ_SDK_CAP_ID_NOSR;
import static com.qxwz.sdk.core.Constants.QXWZ_SDK_STAT_AUTH_SUCC;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.qxwz.sdk.configs.AccountInfo;
import com.qxwz.sdk.configs.SDKConfig;
import com.qxwz.sdk.core.CapInfo;
import com.qxwz.sdk.core.Constants;
import com.qxwz.sdk.core.IRtcmSDKCallback;
import com.qxwz.sdk.core.RtcmSDKManager;
import com.qxwz.sdk.types.KeyType;
import com.zhd.AppHelper;
import com.zhd.gps.manage.models.GGAEntity;
import com.zhd.shj.CorsGprsService;
import com.zhd.shj.QXsdkmap;
import com.zhd.shj.R;
import com.zhd.shj.SystemPropertiesHelper;
import com.zhd.shj.activity.Difference;
import com.zhd.shj.activity.NtripSetting;
import com.zhd.zhdcorsnet.NetResult;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class QxService extends Service implements IRtcmSDKCallback {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    private String SN,LX;
    public static final String ACTION_SENDRESULT = "sendresult";
    private Intent intent = new Intent(ACTION_SENDRESULT);
    private  int i_q;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
             isStart = false;
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(NtripSetting.ACTION_SENDRESULT);
        registerReceiver(mBroadcastReceiver, myIntentFilter);
        RtcmSDKManager.getInstance().stop(QXWZ_SDK_CAP_ID_NOSR);
        RtcmSDKManager.getInstance().cleanup();
        SN =  SystemPropertiesHelper.getSystemProperty("persist.sys.serialno");
        LX = "EL100";
        sendresult(100);
        i_q = intent.getExtras().getInt("ip");
        Log.e("千寻","连接"+i_q);
        flags = Service.START_FLAG_REDELIVERY;

        return super.onStartCommand(intent, flags, startId);
    }
    public void sendresult(int code)
    {
        QXsdkmap abc = new QXsdkmap();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");// HH:mm:ss
//获取当前时间
        Date date = new Date(System.currentTimeMillis());
        abc.sdkTime = simpleDateFormat.format(date);
        abc.sdktype = code;
        mQXsdkmap.add(abc);
        startQX();
        String mReception01text = "";
        for (int k = mQXsdkmap.size() - 1; k >= 0; k--) {
            mReception01text += mQXsdkmap.get(k).sdkTime
                    + "    返回代码："
                    + mQXsdkmap.get(k).sdktype
                    + "     "
                    + CommonEnum.getQxSDK(mQXsdkmap
                    .get(k).sdktype) + "\n";
        }
        Log.e("千寻","返回结果"+mReception01text);
        intent.putExtra("result", mReception01text);
        intent.putExtra(RESULT_NETRESULT, NetResult.QXresult);
        AppHelper.mQXsdkmap=mReception01text;
        sendBroadcast(intent);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        isStart = false;


        unregisterReceiver(mBroadcastReceiver);
        Log.e("千寻","断开"+i_q);
        sendresult(99);

    }
    public BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context mCurrentContext, Intent intent) {
            String action = intent.getAction();

            if (action.equals(NtripSetting.ACTION_SENDRESULT)) {
                String result = intent.getExtras().getString("GGA");


                String tmpGPGGA = result;

                if (tmpGPGGA.contains("GGA") && tmpGPGGA.contains("\r\n")) {
                    if (tmpGPGGA.length() > 50) {
                        GGA = tmpGPGGA;
                        Log.e("千寻",GGA);
                    }

                }

            }
        }
    };
    public void startQX() {
        new Thread() {
            public void run() {
                AppHelper.AK = "";
                AppHelper.AS = "";
                try {
                    SignatureDemo.Abc(SN, LX, QxService.this);
                    Thread.sleep(2000);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Log.e("qq", AppHelper.AK + ";" + AppHelper.AS+";"+LX+";"+SN);
                SDKConfig.Builder builder = SDKConfig
                        .builder()
                        .setAccountInfo(
                                AccountInfo.builder()
                                        .setKeyType(KeyType.QXWZ_SDK_KEY_TYPE_DSK)
                                        .setKey(AppHelper.AK)
                                        .setSecret(AppHelper.AS)
                                        .setDeviceId(SN)
                                        .setDeviceType(LX).build())
                        .setRtcmSDKCallback(QxService.this);

                RtcmSDKManager.getInstance().init(builder.build());
                RtcmSDKManager.getInstance().auth();
            }
        }.start();
    }
    public static final String RESULT_NETRESULT = "NetResult";

    public static final String MY_FLAGS_FOR_CORS = "myFlags";
    public static final int FLAG_DATARECEIVE = 0x109;
    @Override
    public void onData(int type, byte[] bytes) {
        Log.e("千寻", "收到数据");
        intent.putExtra("QX", bytes);
        intent.putExtra(RESULT_NETRESULT, NetResult.sendQX);
      //  intent.putExtra(MY_FLAGS_FOR_CORS, FLAG_DATARECEIVE);
        sendBroadcast(intent);
    }

    private static final String TAG = "qxwz";

    @Override
    public void onStatus(int status) {
        Log.d(TAG, "status changed to " + status);
    }

    private static String GGA = "$GPGGA,000001,3112.518576,N,12127.901251,E,1,8,1,0,M,-32,M,3,0*4B";
    public static ArrayList<QXsdkmap> mQXsdkmap = new ArrayList<QXsdkmap>();
    @Override
    public void onAuth(int code, List<CapInfo> caps) {
        QXsdkmap abc = new QXsdkmap();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");// HH:mm:ss
//获取当前时间
        Date date = new Date(System.currentTimeMillis());
        abc.sdkTime = simpleDateFormat.format(date);
        abc.sdktype = code;
        mQXsdkmap.add(abc);


        String mReception01text = "";
        for (int k = mQXsdkmap.size() - 1; k >= 0; k--) {
            mReception01text += mQXsdkmap.get(k).sdkTime
                    + "    返回代码："
                    + mQXsdkmap.get(k).sdktype
                    + "     "
                    + CommonEnum.getQxSDK(mQXsdkmap
                    .get(k).sdktype) + "\n";
        }
        Log.e("千寻","返回结果"+mReception01text);
        intent.putExtra("result", CommonEnum.getQxSDK(code));
        intent.putExtra(RESULT_NETRESULT, NetResult.QXresult);
        AppHelper.mQXsdkmap=mReception01text;
        sendBroadcast(intent);

        if (code == QXWZ_SDK_STAT_AUTH_SUCC) {
            Log.d(TAG, "auth successfully.");
            for (CapInfo capInfo : caps) {
                Log.d(TAG, "capInfo:" + capInfo.toString());
            }
            /* if you want to call the start api in the callback function, you must invoke it in a new thread. */
            new Thread() {
                public void run() {
                    RtcmSDKManager.getInstance().start(QXWZ_SDK_CAP_ID_NOSR);
                }
            }.start();
        } else {
            Log.d(TAG, "failed to auth, code is " + code);
        }
    }

    private boolean isStart = false;

    @Override
    public void onStart(int code, int capId) {
        if (code == Constants.QXWZ_SDK_STAT_CAP_START_SUCC) {
            Log.d(TAG, "start successfully.");
            isStart = true;

            new Thread() {
                public void run() {

                    while (isStart) {
                        RtcmSDKManager.getInstance().sendGga(GGA);
                        SystemClock.sleep(1000);
                        Log.e("千寻","发送GGA");
                    }
                }
            }.start();
        } else {
            Log.d(TAG, "failed to start, code is " + code);
        }
    }

}
