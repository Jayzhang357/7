package com.zhd.shj.activity;

import static com.zhd.shj.RestartAPPTool.restartAPP;
import static com.zhd.shj.activity.NtripSetting.ACTION_SENDRESULT;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;


import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.telephony.TelephonyManager;
import android.text.InputFilter;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Size;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;


import com.android.manager.IMcuManager;
import com.android.manager.IMcuManagerListener;
import com.bumptech.glide.Glide;
import com.zhd.AppHelper;
import com.zhd.bd970.manage.interfaces.ReceiveAGRICAListner;
import com.zhd.bd970.manage.interfaces.ReceiveECUListner;
import com.zhd.bd970.manage.interfaces.ReceiveGGAListner;
import com.zhd.bd970.manage.interfaces.ReceiveMduVersionListner;
import com.zhd.bd970.manage.interfaces.ReceiveOemListner;
import com.zhd.bd970.manage.interfaces.ReceiveVTGListner;
import com.zhd.bean.CanData;
import com.zhd.commoncontrol.MainBigImageControl;
import com.zhd.commoncontrol.MainBigImageControlsmall;
import com.zhd.gnssmanager.Com4Receive;

import com.zhd.gps.manage.models.AGRICEntity;
import com.zhd.gps.manage.models.GGAEntity;
import com.zhd.gps.manage.models.VTGEntity;
import com.zhd.serialportmanage.SerialPortManager;
import com.zhd.shj.AleartDialogHelper;
import com.zhd.shj.BigConfigxml;
import com.zhd.shj.Cockroach;
import com.zhd.shj.CommonHelper;
import com.zhd.shj.Convertor;
import com.zhd.shj.CorsGprsService;
import com.zhd.shj.CustomDialog;
import com.zhd.shj.DrawView.CoverCoutView;
import com.zhd.shj.DrawView.DrawCoverView;
import com.zhd.shj.DrawView.DrawView;
import com.zhd.shj.JobHelper;
import com.zhd.shj.R;
import com.zhd.shj.SerialPortFinderHelper;
import com.zhd.shj.boardcast.CommonBroadCast;
import com.zhd.shj.camare.CameraHelper;
import com.zhd.shj.dal.Job;
import com.zhd.shj.entity.JobInfo;
import com.zhd.shj.entity.JobRecordInfo;
import com.zhd.zhdcorsnet.NetResult;


import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ViewFlipper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class MainActivity extends BaseActivity implements GestureDetector.OnGestureListener, ReceiveAGRICAListner, ReceiveVTGListner, ReceiveOemListner, ReceiveECUListner, ReceiveMduVersionListner, ReceiveGGAListner {
    private Com4Receive mCom4Receive = null;
    private int orginX = 500;
    private int orginY = 250;
    private Context mContext = MainActivity.this;
    private RelativeLayout  mRllrtk;

    private MainBigImageControlsmall mbtnzyms, btnxtsz, btnjzdg, btnpdms;

    private MainBigImageControl mbtnSet, mbtnNet, mbtnSpeed, mbtnStlye, mbtn4G;
    private TextView mversionTxt;
    private TextView mMDUversionTxt, mTextDiff, mTextState, mTextHeigh, cover;
    private EditText  mEditHeight;
    private Job mJob = new Job(this);
    private DrawView mView;
    private DrawCoverView mView1;
    private CoverCoutView mView2;
    private boolean mUdateMode = false;
    private boolean mPasue = true;

    private BigConfigxml mConfigxml;
    private int
            mID = 0;
    private String[] pathlist;

    public int bindpath(String com) {
        int x = 0;
        for (int i = 0; i < pathlist.length; i++) {
            if (pathlist[i].toString().equals(com.toString())) {
                x = i;
            }
        }
        return x;
    }

    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(CorsGprsService.ACTION_SENDRESULT);

        // 注册广播
        registerReceiver(mBroadcastReceiver, myIntentFilter);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(CommonBroadCast.BC_JOB_LOAD);
        getApplicationContext().registerReceiver(mBcrLoadJobCreate,
                intentFilter);
    }

    /**
     * 接收作业的广播
     */
    public BroadcastReceiver mBcrLoadJobCreate = new BroadcastReceiver() {
        @Override
        public void onReceive(Context mCurrentContext, Intent intent) {
            AppHelper.JOB_INFO = (JobInfo) intent.getSerializableExtra("PAR");
            Message msg = Message.obtain();
            mBcrLoadJobHandler.sendMessage(msg);
            Log.e("作业名称1", AppHelper.JOB_INFO.JobName);
        }
    };
    private double mL0 = 0;
    /************************************* 处理加载作业后的代码 **********************************/
    Handler mBcrLoadJobHandler = new Handler() {

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void handleMessage(Message msg) {

            runOnUiThread(new Runnable() {

                @SuppressLint("SuspiciousIndentation")
                @Override
                public void run() {
                    txtMessage.setText("");
                    mView.endDraw();
                    mView1.endDraw();
                    AppHelper.RATIO_VALUE = 1f;
                    AppHelper.zhuanX = orginX;
                    AppHelper.zhuanY = orginY;
                    btnkspd.setBackgroundResource(R.drawable.back_g1);
                    btnkspd.setText(getString(R.string.kspd));

                    beginPD = false;
                    mView.setbegin = false;



                    if (AppHelper.JOB_INFO == null)

                        return;
                    if (AppHelper.JOB_INFO.ID == -1) {
                        txtMessage.setText(getString(R.string.jobname_tx) + ":" + AppHelper.JOB_INFO.JobName);
                        mSetJobRelate.setVisibility(View.VISIBLE);
                        mSetCount.setVisibility(View.INVISIBLE);
                        mSetCount_1.setVisibility(View.INVISIBLE);
                        btnSave.setVisibility(View.INVISIBLE);
                        if(AppHelper.JOB_INFO.AbType==1)
                        {
                            btnSave.setVisibility(View.VISIBLE);
                            btnJS.setVisibility(View.INVISIBLE);
                        }
                        txta_w.setText("");
                        txtb_w.setText("");
                        txtc_w.setText("");
                        txta_j.setText("");
                        txtb_j.setText("");
                        txtc_j.setText("");
                        txta_g.setText("");
                        txtb_g.setText("");
                        txtc_g.setText("");

                        mSetA = false;
                        mSetB = false;
                        mSetC = false;
                        Log.e("获取作业", "" + AppHelper.JOB_INFO.ID);
                    } else {

                        double[]       xyza = Convertor.BLHtoxyhGS3(AppHelper.JOB_INFO.APointB, AppHelper.JOB_INFO.APointL, 0, mL0);

                        mView.setApointXY(xyza[0], xyza[1]);
                        mView.CurrentImpXY(xyza[0], xyza[1]);
                        mView1.CurrentImpXY(xyza[0], xyza[1]);
                        double[]     xyzb = Convertor.BLHtoxyhGS3(AppHelper.JOB_INFO.BPointB, AppHelper.JOB_INFO.BPointL, 0, mL0);

                        mView.setBpointXY(xyzb[0], xyzb[1]);
                        double[]      xyzc = Convertor.BLHtoxyhGS3(AppHelper.JOB_INFO.CPointB, AppHelper.JOB_INFO.CPointL, 0, mL0);

                        mView.setCpointXY(xyzc[0], xyzc[1]);
                        mJob.updateAndRefreshTableName(AppHelper.JOB_INFO);
                        mView1.setJob(mJob);
                        Log.e("获取作业", "" + AppHelper.JOB_INFO.ID);

                        mView1.seth(AppHelper.JOB_INFO.setH);
                        if(AppHelper.JOB_INFO.AbType==1) {
                            mView1.setPD(true);
                            mView1.setPD(xyza[0],xyza[1],AppHelper.JOB_INFO.APointH,xyzb[0],xyzb[1],AppHelper.JOB_INFO.BPointH,xyzc[0],xyzc[1],AppHelper.JOB_INFO.BPointH);
                        }
                        else
                        mView1.setPD(false);
                        txtMessage.setText(getString(R.string.jobname_tx) + ":" + AppHelper.JOB_INFO.JobName + "        " + getString(R.string.height_set_tx) + ":" + AppHelper.JOB_INFO.setH + "M");
                        mView.setbegin = true;
                        cover.setText(df.format(AppHelper.JOB_INFO.CoverageArea) + "亩");
                    }

                }
            });


        }

        ;
    };
    DecimalFormat df = new DecimalFormat("#.##");
    private boolean geton = false;

    @Override
    protected void onPause() {
        super.onPause();
        geton = false;

        Log.e("界面", "停止");
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCom4Receive = mCom4Receive.getInstance(6, mConfigxml.getCompath());
        mCom4Receive.getParser().setReceiveECUListener(MainActivity.this);

        mCom4Receive.getParser().setReceiveVTGListener(this);
        mCom4Receive.getParser().setoemListner(this);
        mCom4Receive.getParser().setReceiveAGRICAleListener(this);
        mCom4Receive.getParser().setReceiveGGAListener(this);
        mCom4Receive.getParser().setReceiveMduVersionListener(this);
        mCom4Receive.startRecieve();
        geton = true;

        Log.e("界面", "继续");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent mIntent = new Intent(MainActivity.this
                ,
                CorsGprsService.class);
        MainActivity.this.stopService(mIntent);
        unbindService();
        if (iMcuManager != null) {
            try {
                iMcuManager.removeMcuManagerListener(iMcuManagerListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        /*  RtcmSDKManager.getInstance().cleanup();*/
    }

    Runnable sendable = new Runnable() {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            try {
                // Thread.sleep(5000);
                Intent mIntent = new Intent(MainActivity.this
                        ,
                        CorsGprsService.class);
                mConfigxml = BigConfigxml.getInstantce(MainActivity.this);

                mIntent.putExtra("ip", mConfigxml.getip());
                mIntent.putExtra("port", mConfigxml.getport() + "");
                mIntent.putExtra("username", mConfigxml.getusername());
                mIntent.putExtra("password", mConfigxml.getpassword());
                mIntent.putExtra("sourcenode", mConfigxml.getSourece());// RTCM2.3"0020028028"
                mIntent.putExtra("reconnect", true );
                MainActivity.this.stopService(mIntent);
                Thread.sleep(5000);
                MainActivity.this.startService(mIntent);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };
    public BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context mCurrentContext, Intent intent) {
            String action = intent.getAction();
            if (geton) {
                if (action.equals(CorsGprsService.ACTION_SENDRESULT)) {
                    int result = intent.getExtras().getInt(CorsGprsService.RESULT_NETRESULT);
                    if (result == NetResult.GPRSSet_NoResponse || result == NetResult.GPRSSet_NoResponse_And_Reconnection) {
                        new Thread(sendable).start();
                    }
                    if (result == NetResult.sendCF) {
                        byte[] bytes = intent.getExtras().getByteArray("CF");
                        mCom4Receive.sendByte(bytes);
                        Log.e("界面", "转发差分");
                    }
                    if (result == NetResult.sendQX) {
                        byte[] bytes = intent.getExtras().getByteArray("QX");
                        mCom4Receive.sendByte(bytes);
                        Log.e("千寻", "转发差分");
                    }
                } else {


                }

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mConfigxml = BigConfigxml.getInstantce(MainActivity.this);
        AppHelper.Language=mConfigxml.Language;
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_main);
        gestureDetector = new GestureDetector(this, this);
        iniView();
        Log.e("获取作业", "重新打开");

        SerialPortFinderHelper abcd = new SerialPortFinderHelper();
        pathlist = abcd.getAllDevicesPath();


        mCom4Receive = mCom4Receive.getInstance(6, mConfigxml.getCompath());
        mCom4Receive.getParser().setReceiveECUListener(MainActivity.this);

        mCom4Receive.getParser().setReceiveVTGListener(this);
        mCom4Receive.getParser().setoemListner(this);
        mCom4Receive.getParser().setReceiveAGRICAleListener(this);
        mCom4Receive.getParser().setReceiveGGAListener(this);
        mCom4Receive.getParser().setReceiveMduVersionListener(this);
        mCom4Receive.startRecieve();

        Thread mReadThread = new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                while (true) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (mCom4Receive != null && System.currentTimeMillis() - mGGAStartTime > 5000) {
                        Log.e("发送","时间"+(System.currentTimeMillis() - mGGAStartTime) );
                        String cmd = "gpgga 0.4\r\n";
                        mCom4Receive.sendByte(cmd.getBytes());
                        try {
                            Thread.sleep(300);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        cmd = "gpvtg 1\r\n";
                        mCom4Receive.sendByte(cmd.getBytes());
                        try {
                            Thread.sleep(300);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        cmd = "Agrica 5\r\n";
                        mCom4Receive.sendByte(cmd.getBytes());
                    }
                }
            }

        });
        mReadThread.start();
        registerBoradcastReceiver();
        if(mConfigxml.differenstyle==1)
        {

            Intent mIntent = new Intent(MainActivity.this
                    ,
                    Difference.class);
            mIntent.putExtra("ip", 1);
            Log.e("打开","123");
            startActivity(mIntent);

        }
        else if(mConfigxml.differenstyle==2)
        {
            Intent mIntent = new Intent(MainActivity.this
                    ,
                    CorsGprsService.class);
            mIntent.putExtra("ip", mConfigxml.ip);
            mIntent.putExtra("port", mConfigxml. port+"");
            mIntent.putExtra("username",  mConfigxml.username);
            mIntent.putExtra("password",  mConfigxml.password);
            mIntent.putExtra("sourcenode",  mConfigxml.Sourece);// RTCM2.3"0020028028"
            mIntent.putExtra("reconnect", true);

          //  MainActivity.this.stopService(mIntent);
            MainActivity.this.startService(mIntent);
            Log.e("打开","2");
        }
        Cockroach.install(new Cockroach.ExceptionHandler() {

            // handlerException内部建议手动try{  你的异常处理逻辑  }catch(Throwable e){ } ，以防handlerException内部再次抛出异常，导致循环调用handlerException

            @Override
            public void handlerException(final Thread thread, final Throwable throwable) {
                //开发时使用Cockroach可能不容易发现bug，所以建议开发阶段在handlerException中用Toast谈个提示框，
                //由于handlerException可能运行在非ui线程中，Toast又需要在主线程，所以new了一个new Handler(Looper.getMainLooper())，
                //所以千万不要在下面的run方法中执行耗时操作，因为run已经运行在了ui线程中。
                //new Handler(Looper.getMainLooper())只是为了能弹出个toast，并无其他用途
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            //建议使用下面方式在控制台打印异常，这样就可以在Error级别看到红色log
                            Log.e("AndroidRuntime", "--->CockroachException:" + thread + "<---", throwable);
                            //Toast.makeText(App.this, "Exception Happend\n" + thread + "\n" + throwable.toString(), Toast.LENGTH_SHORT).show();
//	                        throw new RuntimeException("..."+(i++));
                            saveCrashInfo2File(throwable);
                            new Thread() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(mContext, MainActivity.class);
                                    @SuppressLint("WrongConstant") PendingIntent restartIntent = PendingIntent.getActivity(mContext, 0, intent, Intent.FLAG_ACTIVITY_NEW_TASK);
                                    AlarmManager mgr = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
                                    mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, restartIntent);
                                    android.os.Process.killProcess(android.os.Process.myPid());
                                }
                            }.start();
                        } catch (Throwable e) {

                        }
                    }
                });
            }
        });
        //Cockroach.uninstall();
        bindService();
    }

    private Map<String, String> infos = new HashMap<String, String>();
    private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    int send_num = 0;
    int send_num_1 = 0;

    private String saveCrashInfo2File(Throwable ex) {

        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, String> entry : infos.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key + "=" + value + "\n");
        }

        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String result = writer.toString();
        sb.append(result);
        try {
            long timestamp = System.currentTimeMillis();
            String time = formatter.format(new Date(System.currentTimeMillis()));
            String fileName = "crash" + ".log";
            if (Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
                String path = Environment.getExternalStorageDirectory() + "/SHJLog/";
                File dir = new File(path);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                BufferedWriter bw = new BufferedWriter(new FileWriter(
                        path + fileName, true));
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式

                bw.write("当前重启时间：" + df.format(new Date()) + ":::::" + sb.toString());
                bw.close();

            }
            return fileName;
        } catch (Exception e) {

        }
        return null;
    }

    private int mCheckedIndex = -1;
    private Button  btnJS, btnkspd;
    private ImageView ic_arow_Right, ic_arow_Left, btndown, btnup, btnAdd, btnReduce, btnLocation, btnLoA, camraic;
    private RelativeLayout mSetJobRelate, mSetJobRelate_1, mSetCount, mSetCount_1;

    private TextView txta_w, txtb_w, txtc_w, txta_j, txtb_j, txtc_j, txta_g, txtb_g, txtc_g, textHeightConnt, txtMessage;
    private boolean mSetA, mSetB, mSetC;
    private Button btnxtsza, btnxtszb, btnxtszc, btnSave;

    private void showSetAB() {
        ViewFlipper fpSetAb = (ViewFlipper) findViewById(R.id.JobMenuerror);
        fpSetAb.removeAllViews();
        mSetCount = (RelativeLayout) findViewById(R.id.setCount);
        btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                try {

                    AppHelper.JOB_INFO.APointB = Double.parseDouble(txta_w.getText().toString());
                    AppHelper.JOB_INFO.APointL = Double.parseDouble(txta_j.getText().toString());
                    AppHelper.JOB_INFO.APointH = Double.parseDouble(txta_g.getText().toString());


                    AppHelper.JOB_INFO.BPointB = Double.parseDouble(txtb_w.getText().toString());
                    AppHelper.JOB_INFO.BPointL = Double.parseDouble(txtb_j.getText().toString());
                    AppHelper.JOB_INFO.BPointH = Double.parseDouble(txtb_g.getText().toString());


                    AppHelper.JOB_INFO.CPointB = Double.parseDouble(txtc_w.getText().toString());
                    AppHelper.JOB_INFO.CPointL = Double.parseDouble(txtc_j.getText().toString());
                    AppHelper.JOB_INFO.CPointH = Double.parseDouble(txtc_g.getText().toString());
                    if(AppHelper.JOB_INFO.AbType==1) {
                        AppHelper.JOB_INFO.setH =   Double.parseDouble(String.format("%.2f",(AppHelper.JOB_INFO.APointH+ AppHelper.JOB_INFO.BPointH+ AppHelper.JOB_INFO.CPointH)/3));
                    }
                    else {
                        Double abc = Double.parseDouble(mEditHeight.getText().toString());
                        AppHelper.JOB_INFO.setH = abc;
                    }
                    mJob.insertJob(AppHelper.JOB_INFO, true);
                    AppHelper.JOB_INFO = mJob.getSelectedJob();
                    mSetJobRelate.setVisibility(View.INVISIBLE);
                    Thread abcdd = new Thread() {
                        @Override
                        public void run() {
                            AppHelper.copyFile();
                        }
                    };

                    abcdd.start();
                    mJob.updateAndRefreshTableName(AppHelper.JOB_INFO);
                    mView.setbegin = true;
                    mView1.seth(AppHelper.JOB_INFO.setH);
                    double[]       xyza = Convertor.BLHtoxyhGS3(AppHelper.JOB_INFO.APointB, AppHelper.JOB_INFO.APointL, 0, mL0);


                    double[]     xyzb = Convertor.BLHtoxyhGS3(AppHelper.JOB_INFO.BPointB, AppHelper.JOB_INFO.BPointL, 0, mL0);


                    double[]      xyzc = Convertor.BLHtoxyhGS3(AppHelper.JOB_INFO.CPointB, AppHelper.JOB_INFO.CPointL, 0, mL0);

                    if(AppHelper.JOB_INFO.AbType==1) {
                        mView1.setPD(true);
                        mView1.setPD(xyza[0],xyza[1],AppHelper.JOB_INFO.APointH,xyzb[0],xyzb[1],AppHelper.JOB_INFO.BPointH,xyzc[0],xyzc[1],AppHelper.JOB_INFO.BPointH);
                    }
                    else
                        mView1.setPD(false);
                    txtMessage.setText(getString(R.string.jobname_tx) + ":" + AppHelper.JOB_INFO.JobName + "        " + getString(R.string.height_set_tx) + ":" + AppHelper.JOB_INFO.setH + "M");
                } catch (Exception e) {
                    return;
                }

            }
        });
        btnJS = (Button) findViewById(R.id.btnJS);
        btnJS.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                mSetCount.setVisibility(View.VISIBLE);
                mEditHeight.setText(String.format("%.2f", (Double.parseDouble(txta_g.getText().toString()) + Double.parseDouble(txtb_g.getText().toString()) + Double.parseDouble(txtc_g.getText().toString())) / 3));

                btnSave.setVisibility(View.VISIBLE);
            }
        });

        mSetCount_1 = (RelativeLayout) findViewById(R.id.setCount_1);
        mEditHeight = (EditText) findViewById(R.id.EditHeight);
        btndown = (ImageView) findViewById(R.id.btndown);
        btnup = (ImageView) findViewById(R.id.btnup);
        btnup.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                try {
                    Double abc = Double.parseDouble(mEditHeight.getText().toString()) + 0.01;
                    mEditHeight.setText(String.format("%.2f", abc));
                } catch (Exception e) {

                }

            }
        });
        btndown.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                try {
                    Double abc = Double.parseDouble(mEditHeight.getText().toString()) - 0.01;
                    mEditHeight.setText(String.format("%.2f", abc));
                } catch (Exception e) {

                }

            }
        });
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.page_vehicle_set, null);
        fpSetAb.addView(view);
        fpSetAb.setDisplayedChild(0);

        txta_w = (TextView) view.findViewById(R.id.txta_w);
        txtb_w = (TextView) view.findViewById(R.id.txtb_w);
        txtc_w = (TextView) view.findViewById(R.id.txtc_w);
        txta_j = (TextView) view.findViewById(R.id.txta_j);
        txtb_j = (TextView) view.findViewById(R.id.txtb_j);
        txtc_j = (TextView) view.findViewById(R.id.txtc_j);
        txta_g = (TextView) view.findViewById(R.id.txta_g);
        txtb_g = (TextView) view.findViewById(R.id.txtb_g);
        txtc_g = (TextView) view.findViewById(R.id.txtc_g);
        btnxtsza = (Button) view.findViewById(R.id.btnxtsza);
        btnxtsza.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                txta_w.setText(mB + "");
                txta_j.setText(mL + "");
                txta_g.setText(String.format("%.2f", mH) + "");
                double[] xyza = Convertor.BLHtoxyhGS3(mB, mL, 0, mL0);

                mView.setApointXY(xyza[0], xyza[1]);
                mView.CurrentImpXY(xyza[0], xyza[1]);
                mView1.CurrentImpXY(xyza[0], xyza[1]);

                mSetA = true;
            }
        });
        btnxtszb = (Button) view.findViewById(R.id.btnxtszb);
        btnxtszb.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                txtb_w.setText(mB + "");
                txtb_j.setText(mL + "");
                txtb_g.setText(String.format("%.2f", mH) + "");
                double[] xyza = Convertor.BLHtoxyhGS3(mB, mL, 0, mL0);
                mView.setBpointXY(xyza[0], xyza[1]);
                mSetB = true;
            }
        });
        btnxtszc = (Button) view.findViewById(R.id.btnxtszc);
        btnxtszc.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                txtc_w.setText(mB + "");
                txtc_j.setText(mL + "");
                txtc_g.setText(String.format("%.2f", mH) + "");
                double[] xyza = Convertor.BLHtoxyhGS3(mB, mL, 0, mL0);

                mView.setCpointXY(xyza[0], xyza[1]);
 
                mSetC = true;
            }
        });

    }

    private RelativeLayout layout_draw;
    private RelativeLayout layout_draw1;
    private RelativeLayout layout_draw2, camararl;
    GestureDetector gestureDetector;

    @SuppressLint("ResourceType")
    private void iniView() {
        cover = (TextView) findViewById(R.id.cover);
        btnLoA = (ImageView) findViewById(R.id.btnLoA);
        btnLoA.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                if (mView.setA) {
                   /* AppHelper.zhuanX+=(int)(400-mView.centerX);
                    AppHelper.zhuanY+=(int)(200-mView.centerY);
                    Log.e("回到原位",  AppHelper.zhuanX+";"+  AppHelper.zhuanY);
                    Log.e("回到原位1",  mView.ce-nterX+";"+ mView.centerY);*/

                    double[] xyza = Convertor.BLHtoxyhGS3(AppHelper.JOB_INFO.APointB, AppHelper.JOB_INFO.APointL, 0, mL0);


                    mView.CurrentImpXY(xyza[0], xyza[1]);
                    mView1.CurrentImpXY(xyza[0], xyza[1]);

                    AppHelper.zhuanX = orginX;
                    AppHelper.zhuanY = orginY;
                    mView1.mSetGB = true;
                    mView1.invalidate();

                }


            }
        });
        btnLocation = (ImageView) findViewById(R.id.btnLocation);
        btnLocation.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                if (mView.setA) {
                   /* AppHelper.zhuanX+=(int)(400-mView.centerX);
                    AppHelper.zhuanY+=(int)(200-mView.centerY);
                    Log.e("回到原位",  AppHelper.zhuanX+";"+  AppHelper.zhuanY);
                    Log.e("回到原位1",  mView.centerX+";"+ mView.centerY);*/
                    mView.CurrentImpXY(mCurrentImpX, mCurrentImpY);
                    mView1.CurrentImpXY(mCurrentImpX, mCurrentImpY);
                    AppHelper.zhuanX = orginX;
                    AppHelper.zhuanY = orginY;
                    mView1.mSetGB = true;
                    mView1.invalidate();

                }


            }
        });
        btnAdd = (ImageView) findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (mView.setA) {
                    if (AppHelper.RATIO_VALUE < 10) {
                        AppHelper.RATIO_VALUE += 1;
                        mView1.mSetGB = true;
                        mView1.invalidate();
                    }
                }


            }
        });

        btnReduce = (ImageView) findViewById(R.id.btnReduce);
        btnReduce.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (mView.setA) {
                    if (AppHelper.RATIO_VALUE >= 2) {
                        mView1.mSetGB = true;
                        AppHelper.RATIO_VALUE -= 1;
                        mView1.invalidate();
                    }
                }


            }
        });

        txtMessage = (TextView) findViewById(R.id.txtMessage);

        layout_draw = (RelativeLayout) findViewById(R.id.root);

        textHeightConnt = (TextView) findViewById(R.id.textHeightConnt);
        layout_draw1 = (RelativeLayout) findViewById(R.id.root1);
        layout_draw2 = (RelativeLayout) findViewById(R.id.root2);
        camararl = (RelativeLayout) findViewById(R.id.camararl);
        camraic = (ImageView) findViewById(R.id.camraic);


        InputStream is = getResources().openRawResource(R.drawable.ic_trace);

        Bitmap mBitmap = BitmapFactory.decodeStream(is);
        InputStream is1 = getResources().openRawResource(R.drawable.compassview);
        Bitmap mBitmap1 = BitmapFactory.decodeStream(is1);

        InputStream is2 = getResources().openRawResource(R.drawable.back);
        Bitmap mBitmap2 = BitmapFactory.decodeStream(is2);
        mView1 = new DrawCoverView(this, mBitmap2);
        mView2 = new CoverCoutView(this);
        mView = new DrawView(this, mBitmap, mBitmap1);

        layout_draw.addView(mView);
        layout_draw1.addView(mView1);
        layout_draw2.addView(mView2);
        layout_draw.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return true;
            }
        });
        showSetAB();

        ic_arow_Right = (ImageView) findViewById(R.id.ic_arow_Right);
        ic_arow_Left = (ImageView) findViewById(R.id.ic_arow_Left);
        mSetJobRelate = (RelativeLayout) findViewById(R.id.setJobRelate);
        mSetJobRelate.setVisibility(View.INVISIBLE);
        mSetJobRelate_1 = (RelativeLayout) findViewById(R.id.setJobRelate_1);
        Glide.with(this).asGif().load(R.drawable.ic_left_arow).into(ic_arow_Left);
        Glide.with(this).asGif().load(R.drawable.ic_right_arow).into(ic_arow_Right);
        ic_arow_Right.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                mSetJobRelate_1.setVisibility(View.INVISIBLE);
                ic_arow_Right.setVisibility(View.INVISIBLE);
                ic_arow_Left.setVisibility(View.VISIBLE);

            }
        });

        ic_arow_Left.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                mSetJobRelate_1.setVisibility(View.VISIBLE);
                ic_arow_Right.setVisibility(View.VISIBLE);
                ic_arow_Left.setVisibility(View.INVISIBLE);


            }
        });
        mRllrtk = (RelativeLayout) findViewById(R.id.rllrtk);
        mRllrtk.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent mIntent = new Intent(MainActivity.this
                        ,
                        Difference.class);
                mIntent.putExtra("ip", 0);

                startActivity(mIntent);


            }
        });



        mbtnSet = (MainBigImageControl) findViewById(R.id.btnSet);

        btnkspd = (Button) findViewById(R.id.btnkspd);
        btnkspd.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (mView.setbegin) {
                    if (beginPD) {
                        beginPD = false;
                        btnkspd.setBackgroundResource(R.drawable.back_g1);
                        btnkspd.setText(getString(R.string.kspd));
                    } else {
                        beginPD = true;
                        btnkspd.setBackgroundResource(R.drawable.back_r);
                        btnkspd.setText(getString(R.string.jspd));
                        if (AppHelper.JobRecordID == -1)
                            AppHelper.JobRecordID = mJob.getMaxRecordSegmentIndex();
                        AppHelper.JobRecordID += 1;
                        if (AppHelper.JobRecordID != -1) {
                            Log.e("插入成", mCurrentImpX + ";" + mCurrentImpY);
                            mView1.setCurrentRecordXY(mCurrentImpX, mCurrentImpY, mH, AppHelper.JobRecordID);
                            mCurrentRecordX = mCurrentImpX;
                            mCurrentRecordY = mCurrentImpY;
                            mJobRecordInfo.B = mCurrentImpX;
                            mJobRecordInfo.H = mH;

                            mJobRecordInfo.L = mCurrentImpY;
                            mJobRecordInfo.SegmentIndex = AppHelper.JobRecordID;
                            mJob.insertJobRecord(mJobRecordInfo);
                        }
                    }

                }
            }
        });
        mbtnSet.setImageResource(R.drawable.ic_stat, getString(R.string.sta));

        mbtnSet.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

            }
        });


        mbtnNet = (MainBigImageControl) findViewById(R.id.btnNet);
        mbtnNet.setImageResource(R.drawable.ic_base, "NET");

        mbtnSpeed = (MainBigImageControl) findViewById(R.id.btnSpeed);
        mbtnSpeed.setImageResource(R.drawable.ic_speed, getString(R.string.speed));

        mbtnStlye = (MainBigImageControl) findViewById(R.id.btnStlye);
        mbtnStlye.setImageResource(R.drawable.ic_water,  getString(R.string.style));
        mbtnzyms = (MainBigImageControlsmall) findViewById(R.id.btnzyms);
        mbtnzyms.setImageResource(R.drawable.ic_jobm,  getString(R.string.zyms));
        btnxtsz = (MainBigImageControlsmall) findViewById(R.id.btnxtsz);
        btnxtsz.setImageResource(R.drawable.ic_set, getString(R.string.xtsz));
        btnjzdg = (MainBigImageControlsmall) findViewById(R.id.btnjzdg);
        btnjzdg.setImageResource(R.drawable.ic_seth, getString(R.string.jzdg));
        btnjzdg.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
           /*     startActivity(new Intent(MainActivity.this,
                        CanActivity.class));*/


            }
        });
        btnpdms = (MainBigImageControlsmall) findViewById(R.id.btnpdms);
        btnpdms.setImageResource(R.drawable.ic_water, getString(R.string.pdms));
        btnxtsz.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,
                        SystemSet.class));


            }
        });

        mbtnzyms.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,
                        Project.class));


            }
        });

        btnpdms.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,
                        JobManageActivity.class));
            }
        });
        mbtn4G = (MainBigImageControl) findViewById(R.id.btn4G);
        mbtn4G.setImageResource(R.drawable.ic_4g, "4G");


        mversionTxt = (TextView) findViewById(R.id.txtVersion);
        mMDUversionTxt = (TextView) findViewById(R.id.txtMDUVersion);
        mTextDiff = (TextView) findViewById(R.id.textDiff);
        mTextState = (TextView) findViewById(R.id.textState);
        mTextHeigh = (TextView) findViewById(R.id.textHeigh);
        camraic.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (camararl.getVisibility() == View.VISIBLE) {
                    cameraHelper.  closeCamera();
                    camararl.setVisibility(View.INVISIBLE);
                } else {
                    cameraHelper.openCamera();
                    camararl.setVisibility(View.VISIBLE);
                }

            }
        });

        textureView = findViewById(R.id.textureView);
        cameraHelper = new CameraHelper(this,textureView);
    }
    private CameraHelper cameraHelper;
    private TextureView textureView;
    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted 授予权限
                    mConfigxml = BigConfigxml.getInstantce(MainActivity.this);
                    mCom4Receive = mCom4Receive.getInstance(6,  mConfigxml.getCompath());

                    mCom4Receive.getParser().setReceiveGGAListener(this);
                    mCom4Receive.getParser().setReceiveAGRICAleListener(this);
                    mCom4Receive.getParser().setReceiveVTGListener(this);
                    mCom4Receive.getParser().setReceiveECUListener(MainActivity.this);
                    mCom4Receive.getParser().setoemListner(this);
                    mCom4Receive.getParser().setReceiveMduVersionListener(this);
                    mCom4Receive.startRecieve();
                } else {
                    // Permission Denied 权限被拒绝
                    Intent intenn = new Intent();
                    intenn.setAction("android.intent.action.MAIN");
                    intenn.addCategory("android.intent.category.HOME");
                    startActivity(intenn);
                    android.os.Process.killProcess(android.os.Process.myPid());
                }
                break;
            default:
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        return true;
    }


    @Override
    public void TellReceiveMduVersion(String MduVersion) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                mMDUversionTxt.setText("OEM版本：" + MduVersion);
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        return super.onOptionsItemSelected(item);
    }

    private TextView mReception;
    private TextView mReception1;
    private Handler mpostHandler = new Handler();
    boolean mupdate = false;
    int i_c = 0;
    long mGGAStartTime = System.currentTimeMillis();

    @Override
    public void TellReceiveOEM(String abc) {

        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                mversionTxt.setText("OEM版本：" + abc);
            }
        });
    }

    @Override
    public void TellReceiveECU(boolean reResult) {
        Log.e("收到了", "错误");

        //   mbtnOem.setEnabled(true);
    }

    public String getCurrentNetType(Context context) {
        String type = "";
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info == null) {
            type = "null";
        } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {
            type = "wifi";
        } else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
            int subType = info.getSubtype();
            if (subType == TelephonyManager.NETWORK_TYPE_CDMA
                    || subType == TelephonyManager.NETWORK_TYPE_GPRS
                    || subType == TelephonyManager.NETWORK_TYPE_EDGE) {
                type = "2g";
            } else if (subType == TelephonyManager.NETWORK_TYPE_UMTS
                    || subType == TelephonyManager.NETWORK_TYPE_HSDPA
                    || subType == TelephonyManager.NETWORK_TYPE_EVDO_A
                    || subType == TelephonyManager.NETWORK_TYPE_EVDO_0
                    || subType == TelephonyManager.NETWORK_TYPE_EVDO_B) {
                type = "3g";
            } else if (subType == TelephonyManager.NETWORK_TYPE_LTE) {// LTE是3g到4g的过渡，是3.9G的全球标准
                type = "4g";
            }

        }

        return type;
    }
    private  int  mView_count=0;
    private Handler mHandleJapiEvent = new Handler() {
        @SuppressLint("HandlerLeak")
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        public void handleMessage(Message msg) {
            mView.invalidate();
            mView_count++;
            if(mView_count>4) {
                mView_count=0;
                mView1.invalidate();
               // mView2.invalidate();
            }
        }

    };
    double mB, mL, mH;
    boolean tunoff = false;
    private JobRecordInfo mJobRecordInfo = new JobRecordInfo();
    double i_d, i_q,i_h;
    double mCurrentRecordX, mCurrentRecordY, mCurrentImpX, mCurrentImpY;
    double mCurrentRecordYaw = 0;
    boolean beginPD = false;

    @Override
    public void TellReceiveGGA(GGAEntity entity) {

        mGGAStartTime = System.currentTimeMillis();

        runOnUiThread(new Runnable() {

            @Override
            public void run() {

                mL = entity.getLongitude();
                mB = entity.getLatitude();
//                mL =113.4297489515;
//                mB = 23.164931605;
                mSpeed = 9;
                if ((entity.getGpsStatus() > 0 && entity.getGpsStatus() <= 5)) {
                    mL0 = Convertor.getMeridian(mL, 0);

                }
                if (mSetA && !mSetB) {
                    i_d += 0.0001;
                    mView.setYaw((float)0);
                } else if ((mSetB && !mSetC)) {
                    i_q -= 0.0001;
                    mView.setYaw((float)270);
                }
                if(beginPD)
                {
                    if(!tunoff) {
                        mView.setYaw((float)180);
                        i_d -= 0.00001;
                    }
                    else {
                        mView.setYaw((float)0);
                        i_d += 0.00001;
                    }
                    if(i_d<-0.0005)
                    {
                        tunoff=true;
                        i_q -= 0.00005;
                    }
                    if(i_d>0.002) {
                        tunoff = false;
                        i_q -= 0.00005;
                    }
                }
                Log.e("坐标坐便盆",i_d+";;"+i_q);
                mB += i_d;
                mL += i_q;
                double[] xyza = Convertor.BLHtoxyhGS3(mB, mL, 0, mL0);
                mCurrentImpX = xyza[0];
                mCurrentImpY = xyza[1];
                mH = entity.getHeight();
                Random random = new Random();
                int positiveOrNegative = random.nextInt(2); // 生成一个0或1的随机整数
                int randomNumber = random.nextInt(10);
                if (positiveOrNegative == 0) {
                    mH=((float)(5530+randomNumber))/100;
                }
                else
                {
                    mH=((float)(5530-randomNumber))/100;
                }

                if (mCurrentRecordX == 0 && mCurrentRecordY == 0) {
                    mCurrentRecordX = xyza[0];
                    mCurrentRecordY = xyza[1];
                }
                mView.CurrentTraceXY(xyza[0], xyza[1]);

                if (!mSetB && mSetA) {
                    mView.setBpointXY(xyza[0], xyza[1]);
                }
                if (!mSetC && mSetB) {
                    mView.setCpointXY(xyza[0], xyza[1]);
                }
                if(mView.setbegin) {
                    textHeightConnt.setText("当前高程"+String.format("%.2f", (mH-AppHelper.JOB_INFO.setH)*100) +"（cm）");
                    if (beginPD && mSpeed > 0.5) {
                        double distance = JobHelper.lineSpace(mCurrentRecordX, mCurrentRecordY,
                                mCurrentImpX, mCurrentImpY);
                        if (AppHelper.JobRecordID == -1)
                            AppHelper.JobRecordID = mJob.getMaxRecordSegmentIndex();
                        if (distance < 10 && distance > 2) {
                            if (AppHelper.JobRecordID != -1) {
                                mView2.CurrentImpXY(mCurrentImpX,mCurrentImpY);
                                mView2.CurrentRecord(mCurrentRecordX,mCurrentRecordY);
                                mView2.setJob(mJob);
                                mView2.invalidate();

                                mCurrentRecordX = mCurrentImpX;
                                mCurrentRecordY = mCurrentImpY;
                                Log.e("插入成1", mCurrentImpX + ";" + mCurrentImpY);
                                mView1.setCurrentRecordXY(mCurrentRecordX, mCurrentRecordY, mH, AppHelper.JobRecordID);
                                mCurrentRecordYaw = mView.Yaw;
                                mJobRecordInfo.B = mCurrentRecordX;
                                mJobRecordInfo.L = mCurrentRecordY;
                                mJobRecordInfo.SegmentIndex = AppHelper.JobRecordID;
                                mJobRecordInfo.H = mH;
                                AppHelper.JOB_INFO.CoverageArea+= mView2.getArea()*distance*AppHelper.width* 0.0015;
                                mJob.insertJobRecord(mJobRecordInfo);
                              //  AppHelper.JOB_INFO.CoverageArea += distance;
                                  cover.setText(df.format( AppHelper.JOB_INFO.CoverageArea)+"亩");

                                Log.e("插入成1面积", mView2.getArea()+"");
                                //   AppHelper.JOB_INFO.CoverageArea += distance * (AppHelper.JOB_INFO.Handover + AppHelper.JOB_INFO.ImplementWidth) * 0.0015;
                                //       AppHelper.JOB_INFO.CoverageArea=0;
                                if (AppHelper.JOB_INFO.CoverageArea > 0)
                                    mJob.update(AppHelper.JOB_INFO);

                            }
                            Log.e("设置轨迹点", distance + ";" + AppHelper.JobRecordID);

                        }
                        if (Math.abs(mCurrentRecordYaw - mView.Yaw) > 5 && distance > 1.5) {
                            if (AppHelper.JobRecordID != -1) {
                                mView2.CurrentImpXY(mCurrentImpX,mCurrentImpY);
                                mView2.CurrentRecord(mCurrentRecordX,mCurrentRecordY);
                                mCurrentRecordX = mCurrentImpX;
                                mCurrentRecordY = mCurrentImpY;
                                Log.e("插入成2", mCurrentImpX + ";" + mCurrentImpY);
                                mView1.setCurrentRecordXY(mCurrentRecordX, mCurrentRecordY, mH, AppHelper.JobRecordID);
                                mCurrentRecordYaw = mView.Yaw;
                                mJobRecordInfo.B = mCurrentRecordX;
                                mJobRecordInfo.L = mCurrentRecordY;
                                mJobRecordInfo.SegmentIndex = AppHelper.JobRecordID;
                                mJobRecordInfo.H = mH;
                                mView2.setJob(mJob);
                                mView2.invalidate();
                                mView2.getArea();
                                mJob.insertJobRecord(mJobRecordInfo);
                                AppHelper.JOB_INFO.CoverageArea+= mView2.getArea()*distance*AppHelper.width* 0.0015;
                                cover.setText(df.format( AppHelper.JOB_INFO.CoverageArea)+"亩");
                                Log.e("插入成2面积", mView2.getArea()+"");
                                //   AppHelper.JOB_INFO.CoverageArea += distance * (AppHelper.JOB_INFO.Handover + AppHelper.JOB_INFO.ImplementWidth) * 0.0015;
                                //       AppHelper.JOB_INFO.CoverageArea=0;
                                if (AppHelper.JOB_INFO.CoverageArea > 0)
                                    mJob.update(AppHelper.JOB_INFO);
                            }
                            Log.e("设置轨迹点", distance + ";" + AppHelper.JobRecordID);
                        }
                    }
                }
                mbtnSet.setText(getString(R.string.sta)+ entity.getCaculateSatNum());
                mTextDiff.setText(entity.getDiffTime() + "");
                int postionType = entity.getGpsStatus();

                mTextState.setText(" "
                        + getResources().getStringArray(
                        R.array.GpsPositionType)[postionType]);

                mTextHeigh.setText(getString(R.string.dsgd)+"（" + String.format("%.2f", mH) + "M)");
                mbtn4G.setText(getCurrentNetType(mContext));
                if (mSetA && mSetB && mSetC && !txta_g.getText().toString().equals("") && !txtb_g.getText().toString().equals("") && !txtc_g.getText().toString().equals("")) {

                    mSetCount_1.setVisibility(View.VISIBLE);
                }
                mHandleJapiEvent.sendEmptyMessage(0);

            }
        });
    }

    private static String GGA = "$GPGGA,000001,3112.518576,N,12127.901251,E,1,8,1,0,M,-32,M,3,0*4B";
    private Intent intent = new Intent(ACTION_SENDRESULT);

    @Override
    public void TellReceiveGGA(byte[] byte1) {
        GGA = new String(byte1);
        intent.putExtra("GGA", GGA);

        sendBroadcast(intent);
    }

    private double mSpeed = 0;

    @Override
    public void TellReceiveVTG(VTGEntity entity) {
        runOnUiThread(new Runnable() {

            @SuppressLint("SuspiciousIndentation")
            @Override
            public void run() {
             /*   if (entity.getSpeedRate() > 1)
                    mView.setYaw((float) entity.getTDirection());*/
                mbtnSpeed.setText(String.format("%.1f", entity.getSpeedRate()) + "KM/H");
                mSpeed = entity.getSpeedRate();
                Log.e("vtg方向", entity.getTDirection() + "");
            }
        });

    }

    @Override
    public void TellReceiveAGRICA(AGRICEntity mAGRICAEntity) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if (mAGRICAEntity.getB() != 0 && mAGRICAEntity.getL() != 0 && mB != 0
                        && mL != 0) {
                    double lengthBL = CommonHelper.GetDistance(mAGRICAEntity.getL(),
                            mAGRICAEntity.getB(), mL, mB);
                    mbtnNet.setText(String.format("%.1f", lengthBL) + "KM");
                } else {
                    mbtnNet.setText("N/A");
                }
            }
        });

    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }
    private long lastGestureTime = 0;
    private int max = 5;
    private int change = 30;
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastGestureTime > 500&&mView.setA) {
            if (Math.abs(distanceX) > Math.abs(distanceY)) {
                if (distanceX > max) {
                    // 向左滑动
                    AppHelper.zhuanX-=change;
                    mView1.mSetGB=true;
                    mView1.invalidate();
                    Log.e("手势", "向左" + AppHelper.zhuanX);
                } else if (distanceX < -max) {
                    // 向右滑动
                    AppHelper.zhuanX+=change;
                    mView1.mSetGB=true;
                    mView1.invalidate();
                    Log.e("手势", "向右" + AppHelper.zhuanX);
                }
            } else {
                if (distanceY > max) {
                    // 向上滑动
                    AppHelper.zhuanY+=change;
                    mView1.mSetGB=true;
                    mView1.invalidate();
                    Log.e("手势", "向上" + AppHelper.zhuanY);
                } else if (distanceY < -max) {
                    // 向下滑动
                    AppHelper.zhuanY-=change;
                    mView1.mSetGB=true;
                    mView1.invalidate();
                    Log.e("手势", "向下" + AppHelper.zhuanY);
                }
            }
            lastGestureTime = currentTime;
        }
        return true;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK ) {
            //do something.
            return true;
        } else {
            return super.dispatchKeyEvent(event);
        }
    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }


    private void bindService() {
        Intent intent = new Intent();
        ComponentName componentName = new ComponentName("com.android.service", "com.android.service.McuService");
        intent.setComponent(componentName);
        intent.setAction("com.android.service.McuService");
        intent.setType(getPackageName());
        startService(intent);
        bindService(intent, conn, BIND_AUTO_CREATE);
        abc=System.currentTimeMillis()+"";
    }
    String abc="";

    private IMcuManagerListener iMcuManagerListener = new IMcuManagerListener.Stub() {
        @Override
        public boolean onAccState(byte state) throws RemoteException {
            return false;
        }

        @Override
        public void onGpioState(int gpio, byte state) throws RemoteException {

        }

        @Override
        public void onGSensor(int x, int y, int z) throws RemoteException {

        }

        @Override
        public void onCanReceived(int channel, long id, boolean id_extend, boolean frame_remote, byte[] data) throws RemoteException {

          //  Log.e("can数据","9"+abc);
//
        }
    };

    private IMcuManager iMcuManager = null;
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d("mzj","onServiceConnected");
            iMcuManager = IMcuManager.Stub.asInterface(service);
            if (iMcuManager != null) {
                try {
                    iMcuManager.addMcuManagerListener(iMcuManagerListener);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            if (iMcuManager != null) {
                try {
                    iMcuManager.removeMcuManagerListener(iMcuManagerListener);
                    iMcuManager = null;
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    };
    private void unbindService() {
        unbindService(conn);
    }
}

