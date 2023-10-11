package com.zhd.shj.activity;



import static com.qxwz.sdk.core.Constants.QXWZ_SDK_CAP_ID_NOSR;
import static com.qxwz.sdk.core.Constants.QXWZ_SDK_STAT_AUTH_SUCC;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.qxwz.sdk.configs.AccountInfo;
import com.qxwz.sdk.configs.SDKConfig;
import com.qxwz.sdk.core.CapInfo;
import com.qxwz.sdk.core.Constants;
import com.qxwz.sdk.core.IRtcmSDKCallback;
import com.qxwz.sdk.core.RtcmSDKManager;
import com.qxwz.sdk.types.KeyType;
import com.zhd.AppHelper;
import com.zhd.democors.BackgroundWork;
import com.zhd.gnssmanager.Com4Receive;
import com.zhd.gps.manage.models.GGAEntity;
import com.zhd.shj.BigConfigxml;
import com.zhd.shj.CorsGprsService;
import com.zhd.shj.QXsdkmap;
import com.zhd.shj.R;
import com.zhd.shj.SerialPortFinderHelper;
import com.zhd.shj.SystemPropertiesHelper;
import com.zhd.shj.qx.CommonEnum;
import com.zhd.shj.qx.QxService;
import com.zhd.shj.qx.SignatureDemo;
import com.zhd.zhdcorsnet.NetHelper;
import com.zhd.zhdcorsnet.NetResult;
import com.zhd.zhdcorsnet.SourceNode;

import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class Difference extends BaseActivity   implements IRtcmSDKCallback {

    private Button btnFinish,btnBack,mbtnGet,mbtnConect,mbtnbreakOff,mbtnConectqx,mbtnbreakOffqx;
    public RelativeLayout root2, root3,root4;
    public RadioButton r1, r2,r3;
    public RadioGroup radioGroup;
    private TextView LX,SN,mReception,TextViewReceptionqxreuslt,TextViewReceptionresult;
    private Com4Receive mCom4Receive = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_difference);
        iniView();
        Intent intent12 = getIntent();
        i_q = intent12.getExtras().getInt("ip");
        if(i_q==1)
        {
            RtcmSDKManager.getInstance().stop(QXWZ_SDK_CAP_ID_NOSR);
            RtcmSDKManager.getInstance().cleanup();
            isStart = true;

            startQX();
            finish();
        }
        else  if(i_q==2)
        {

            starCos();
            finish();
        }
        registerBoradcastReceiver();

    }
    private Spinner mspCompath;
    private Spinner mspSourece;
    private   String[] nodeNames=new String[] {""};
    public BigConfigxml mConfigxml;
    private EditText mEtIP, mEtPort,mEtUsername,mEtPassword;
    private EditText mEtSoure;
    boolean isButtonClickable = true;
    private  int i_q=0;
    private void iniView() {
        mConfigxml = BigConfigxml
                .getInstantce(Difference.this);

        mReception = (TextView) findViewById(R.id.TextViewReceptionqx);
        TextViewReceptionresult= (TextView) findViewById(R.id.TextViewReceptionresult);
        TextViewReceptionqxreuslt= (TextView) findViewById(R.id.TextViewReceptionqxreuslt);

        TextViewReceptionqxreuslt.setText(AppHelper.mQXsdkmap);
        mbtnConectqx = (Button) findViewById(R.id.btnConectqx);
        mbtnConectqx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isButtonClickable) {
                    // 禁用按钮
                    isButtonClickable = false;

                    // 在一秒后启用按钮
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isButtonClickable = true;
                        }
                    }, 1000); // 1000毫秒，即1秒
                    RtcmSDKManager.getInstance().stop(QXWZ_SDK_CAP_ID_NOSR);
                    RtcmSDKManager.getInstance().cleanup();
                    isStart = true;
                    Intent mIntent = new Intent(Difference.this
                            ,
                            CorsGprsService.class);
                    Difference.this.stopService(mIntent);
                    startQX();

                }

            }
        });
        mbtnbreakOffqx = (Button) findViewById(R.id.btnBreakqx);
        mbtnbreakOffqx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RtcmSDKManager.getInstance().stop(QXWZ_SDK_CAP_ID_NOSR);
                RtcmSDKManager.getInstance().cleanup();
                isStart =false;

            }
        });

        LX=(TextView) findViewById(R.id.LX);
        SN=(TextView) findViewById(R.id.SN);
        LX.setText("EL100");


        String serialNo = SystemPropertiesHelper.getSystemProperty("persist.sys.serialno");
        SN.setText(serialNo);
        Log.e("SN号","输出"+serialNo);
        mReception01= (TextView) findViewById(R.id.TextViewReception);
        mEtIP= (EditText) findViewById(R.id.ipED);
        mEtPort= (EditText) findViewById(R.id.portED);
        mEtUsername= (EditText) findViewById(R.id.nameED);
        mEtPassword= (EditText) findViewById(R.id.passwordED);

        mEtSoure = (EditText) findViewById(R.id.EtPort1);
        mspSourece = (Spinner) findViewById(R.id.spinner2);
        ArrayAdapter<String> adapter4 = new ArrayAdapter<String>(this,
                R.layout.myspinner, new String[]{mConfigxml.getSourece()});
        adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mspSourece.setAdapter(adapter4);

        mspSourece.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                if (nodeNames!=null&&nodeNames.length > 0)
                    mEtSoure.setText(nodeNames[position]);
            }

            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mEtIP.setText(mConfigxml.getip());
        mEtPort.setText(mConfigxml.getport()+"");

        mEtUsername.setText(mConfigxml.getusername()+"");
        mEtPassword.setText(mConfigxml.getpassword()+"");

        mEtSoure .setText(mConfigxml.getSourece()+"");
        mspSourece.setVisibility(View.GONE);
        mbtnGet = (Button) findViewById(R.id.btnGet);
        mbtnGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ip = mEtIP.getText().toString();
                port = mEtPort.getText().toString();


                if (ip.length() == 0) {
                    Toast.makeText(getApplicationContext(),
                            R.string.toast_ip_null, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (port.length() == 0) {
                    Toast.makeText(getApplicationContext(),
                                    R.string.toast_port_null, Toast.LENGTH_SHORT)
                            .show();
                    return;
                }
                showNetProgressDialog();

                mConfigxml.setip(ip);
                mConfigxml.setport(Integer.valueOf(port));
                mConfigxml.saveConfigXml();
            }
        });
        mbtnConect = (Button) findViewById(R.id.btnConect);
        mbtnConect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RtcmSDKManager.getInstance().stop(QXWZ_SDK_CAP_ID_NOSR);
                RtcmSDKManager.getInstance().cleanup();
                isStart =false;
                sendResult_s(2310);
                starCos();

            }
        });

        mbtnbreakOff = (Button) findViewById(R.id.btnBreak);
        mbtnbreakOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(Difference.this
                        ,
                        CorsGprsService.class);
                Difference.this.stopService(mIntent);

            }
        });

        root2 = (RelativeLayout) findViewById(R.id.root2);
        root2.setVisibility(View.VISIBLE);
        root3 = (RelativeLayout) findViewById(R.id.root3);
        root3.setVisibility(View.INVISIBLE);
        root4 = (RelativeLayout) findViewById(R.id.root4);
        root4.setVisibility(View.INVISIBLE);
        r1 = (RadioButton) findViewById(R.id.r1);
        r2 = (RadioButton) findViewById(R.id.r2);
        r3 = (RadioButton) findViewById(R.id.r3);
        Log.e("方式",mConfigxml.differenstyle+"");
        switch (mConfigxml.differenstyle)
        {
            case 0:r1.setChecked(true); root2.setVisibility(View.VISIBLE);
                root3.setVisibility(View.INVISIBLE);
                root4.setVisibility(View.INVISIBLE);break;
            case 1:r2.setChecked(true); root3.setVisibility(View.VISIBLE);
                root2.setVisibility(View.INVISIBLE);
                root4.setVisibility(View.INVISIBLE);break;
            case 2:r3.setChecked(true); root4.setVisibility(View.VISIBLE);
                root3.setVisibility(View.INVISIBLE);
                root2.setVisibility(View.INVISIBLE);break;
        }
        //  setcheck (mConfigxml.differenstyle);
        radioGroup = findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // 处理选项的选择事件
                Log.e("方式","qq"+checkedId);
                setcheck (checkedId);
            }
        });
        mspCompath = (Spinner) findViewById(R.id.spinner1);
        SerialPortFinderHelper abcd = new SerialPortFinderHelper();
        String[] pathlist =  getResources().getStringArray(R.array.btl);;
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this,
                R.layout.myspinner, pathlist);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mspCompath.setAdapter(adapter1);
        btnFinish = (Button) findViewById(R.id.btnFinish);
        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });
        btnBack = (Button) findViewById(R.id.btnBack);//保存
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(r1.isChecked()) {
                    mConfigxml.differenstyle = 0;
                    Intent mIntent = new Intent(Difference.this
                            ,
                            CorsGprsService.class);

                    Difference.this.stopService(mIntent);
                }
                else  if(r3.isChecked()) {
                    mConfigxml.differenstyle = 2;
                    starCos();
                }
                else {
                    mConfigxml.differenstyle = 1;
                    Intent mIntent = new Intent(Difference.this
                            ,
                            CorsGprsService.class);
                    Difference.this.stopService(mIntent);
                }
                Log.e("方式",mConfigxml.differenstyle+"");
                mConfigxml.saveConfigXml();
                finish();
            }
        });
    }
    public void setcheck(int checkedId)
    {
        switch (checkedId) {
            case R.id.r1:
                // 选项1被选中
                root2.setVisibility(View.VISIBLE);
                root3.setVisibility(View.INVISIBLE);
                root4.setVisibility(View.INVISIBLE);
                break;
            case R.id.r2:
                // 选项2被选中
                root3.setVisibility(View.VISIBLE);
                root2.setVisibility(View.INVISIBLE);
                root4.setVisibility(View.INVISIBLE);
                break;
            case R.id.r3:
                // 选项2被选中
                root3.setVisibility(View.INVISIBLE);
                root2.setVisibility(View.INVISIBLE);
                root4.setVisibility(View.VISIBLE);
                break;
            // 处理更多选项
        }
    }
    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(CorsGprsService.ACTION_SENDRESULT);

        // 注册广播
        registerReceiver(mBroadcastReceiver, myIntentFilter);
    }
    private void showNetProgressDialog() {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage(getString(R.string.progress_wait));
        dialog.setTitle(R.string.option);
        BackgroundWork backwork = new BackgroundWork();

        backwork.setDowork(new BackgroundWork.IDoWork() {

            @Override
            public void doWork(BackgroundWork.WorkHandler handler, Object arg) {
                // TODO Auto-generated method stub
                List<SourceNode> nodess;

                try {
                    // boolean status = InetAddress.getByName(ip)
                    // .isReachable(3000);
                    Socket mBaiduSocket = new Socket();
                    boolean isNetOk = false;

                    if (mBaiduSocket.isConnected() == false) {
                        try {

                            SocketAddress remoteAddr = new InetSocketAddress(
                                    ip, Integer.parseInt(port));
                            mBaiduSocket.connect(remoteAddr, 3000);

                        } catch (Exception e) {
                            // TODO: handle exception
                            isNetOk = false;
                        }

                        isNetOk = true;
                    }

                    if (isNetOk) {
                        nodess = NetHelper.GetSourceNode(ip, port);
                    } else {
                        nodess = null;
                    }

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();

                    nodess = null;
                }

                handler.complete(false, null, nodess);
            }

        });

        backwork.setWorkListener(new BackgroundWork.RunWorkListener() {

            @Override
            public void started(Object arg) {
                // TODO Auto-generated method stub

            }

            @Override
            public void complete(boolean isCancel, Exception ex, Object result) {
                // TODO Auto-generated method stub
                dialog.dismiss();
                if (result == null) {

                    //showInputSourceDialog();
                    mspSourece.setVisibility(View.GONE);
                    mEtSoure.setText("");
                    mEtSoure.setVisibility(View.VISIBLE);


                    return;
                }
                mEtSoure.setText("");
                mEtSoure.setVisibility(View.GONE);
                mspSourece.setVisibility(View.VISIBLE);

                List<SourceNode> nodes = (List<SourceNode>) result;
                nodeNames = new String[nodes.size()];
                if (nodes.get(0) == null) return;
                for (int i = 0; i < nodeNames.length; i++) {
                    nodeNames[i] = nodes.get(i).Mountpoint;
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                        getApplication(), R.layout.myspinner,
                        nodeNames);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mspSourece.setAdapter(adapter);
            }
        });

        backwork.run();
        dialog.setCancelable(false);
        dialog.show();
    }
    private TextView mReception01;
    private void starCos() {

        username = mEtUsername.getText().toString();
        password = mEtPassword.getText().toString();
        ip = mEtIP.getText().toString();
        port = mEtPort.getText().toString();

        nodesoure = mEtSoure.getText() + "";


        if (ip.length() == 0) {
            Toast.makeText(getApplicationContext(),
                    R.string.toast_ip_null, Toast.LENGTH_SHORT).show();
            return;
        }
        if (port.length() == 0) {
            Toast.makeText(getApplicationContext(),
                            R.string.toast_port_null, Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        if (username.length() == 0) {
            Toast.makeText(getApplicationContext(),
                    R.string.toast_user_null, Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() == 0) {
            Toast.makeText(getApplicationContext(),
                            R.string.toast_password_null, Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        if (nodesoure.length() == 0) {
            Toast.makeText(getApplicationContext(),
                            R.string.toast_node_null, Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        mConfigxml.setip(ip);
        mConfigxml.setport(Integer.valueOf(port)  );
        mConfigxml.setusername(username);
        mConfigxml.setpassword(password);
        mConfigxml.setSourece(nodesoure);
        mConfigxml.saveConfigXml();
        Intent mIntent = new Intent(Difference.this
                ,
                CorsGprsService.class);
        mIntent.putExtra("ip", ip);
        mIntent.putExtra("port", port);
        mIntent.putExtra("username", username);
        mIntent.putExtra("password", password);
        mIntent.putExtra("sourcenode", nodesoure);// RTCM2.3"0020028028"
        mIntent.putExtra("reconnect", true);

        Difference.this.stopService(mIntent);
        Difference.this.startService(mIntent);
    }
    public static ArrayList<QXsdkmap> mQXsdkmap = new ArrayList<QXsdkmap>();
    private String getResultMsg(int result, Context mCurrentContext) {
        switch (result) {
            case NetResult.Success:
                return mCurrentContext.getString(R.string.toast_connection_success);
            case NetResult.Fail:
                return mCurrentContext.getString(R.string.toast_connection_fail);
            case NetResult.Fail_And_Reconnection:
                return mCurrentContext.getString(R.string.toast_fail_reconnection);
            case NetResult.GPRS_UserPassword_Fail:
                return mCurrentContext.getString(R.string.toast_userpassword_fail);
            case NetResult.GPRSSet_NoResponse:


                return mCurrentContext.getString(R.string.toast_no_response);
            case NetResult.GPRSSet_NoResponse_And_Reconnection:


                return mCurrentContext
                        .getString(R.string.toast_no_response_reconnection);

            case NetResult.GPRSSet_UserName_Occupied:
                return mCurrentContext.getString(R.string.toast_auth_401);
            case 2310:
                return getString(R.string.mesg111);
            default:
                return "";
        }
    }
    private void sendResult_s(int result) {
        //  Log.e("界面",result+"111");
        QXsdkmap abc = new QXsdkmap();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");// HH:mm:ss
//获取当前时间
        Date date = new Date(System.currentTimeMillis());
        abc.sdkTime = simpleDateFormat.format(date);
        abc.sdktype = result;
        mQXsdkmap.add(abc);
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if (TextViewReceptionresult != null) {

                    String mReception01text = "";
                    for (int k = mQXsdkmap.size() - 1; k >= 0; k--) {
                        mReception01text += mQXsdkmap.get(k).sdkTime
                                + "    返回代码："
                                + mQXsdkmap.get(k).sdktype
                                + "     "
                                + getResultMsg(mQXsdkmap
                                .get(k).sdktype, Difference.this) + "\n";
                    }
                    TextViewReceptionresult.setText(mReception01text);
                }
            }

        });
    }
    Runnable sendable = new Runnable() {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            try {
                // Thread.sleep(5000);
                Intent mIntent = new Intent(Difference.this
                        ,
                        CorsGprsService.class);
                mIntent.putExtra("ip", ip);
                mIntent.putExtra("port", port);
                mIntent.putExtra("username", username);
                mIntent.putExtra("password", password);
                mIntent.putExtra("sourcenode", nodesoure);// RTCM2.3"0020028028"
                mIntent.putExtra("reconnect", true);
                Difference.this.stopService(mIntent);
                Thread.sleep(5000);
                Difference.this.startService(mIntent);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };
    private Handler mpostHandler = new Handler();

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
            if (action.equals(CorsGprsService.ACTION_SENDRESULT)) {
                int result = intent.getExtras().getInt(CorsGprsService.RESULT_NETRESULT);
                if (result == NetResult.GPRSSet_NoResponse || result == NetResult.GPRSSet_NoResponse_And_Reconnection) {
                    new Thread(sendable).start();
                }
                Log.e("界面", getResultMsg(result, Difference.this) + "1234");
                if (result == NetResult.sendCF) {
                    byte[] bytes = intent.getExtras().getByteArray("CF");
                    if (mReception01 != null) {
                        if (mReception01.getText().length() > 2000) {
                            Log.e("清空", "");
                            mReception01.setText("");
                        }

                        mReception01.append(new String(bytes));
                        mpostHandler.post(new Runnable() {
                            // @Override
                            public void run() {
                                ScrollView scroll = (ScrollView) findViewById(R.id.scroll);
                                View inner = (TextView) findViewById(R.id.TextViewReception);
                                // mSv.fullScroll(ScrollView.FOCUS_DOWN);
                                if (scroll == null || inner == null) {
                                    return;
                                }
                                // 内层高度超过外层
                                int offset = inner.getMeasuredHeight()
                                        - scroll.getMeasuredHeight();
                                if (offset < 0) {
                                    offset = 0;
                                }
                                scroll.scrollTo(0, offset);
                            }

                        });

                    }
                }else if(result == NetResult.sendQX)
                {
                    byte[] bytes = intent.getExtras().getByteArray("QX");



                    if (mReception != null) {
                        if (mReception.getText().length() > 2000) {
                            Log.e("清空", "");
                            mReception.setText("");
                        }

                        mReception.append(new String(bytes));
                        mpostHandler.post(new Runnable() {
                            // @Override
                            public void run() {
                                ScrollView scroll = (ScrollView) findViewById(R.id.scrollqx);
                                View inner = (TextView) findViewById(R.id.TextViewReceptionqx);
                                // mSv.fullScroll(ScrollView.FOCUS_DOWN);
                                if (scroll == null || inner == null) {
                                    return;
                                }
                                // 内层高度超过外层
                                int offset = inner.getMeasuredHeight()
                                        - scroll.getMeasuredHeight();
                                if (offset < 0) {
                                    offset = 0;
                                }
                                scroll.scrollTo(0, offset);
                            }

                        });

                    }
                }
                else if(result == NetResult.QXresult)
                {

              //      String reuslt = intent.getExtras().getString("result");

                    TextViewReceptionqxreuslt.setText(AppHelper.mQXsdkmap);
                }
                else {

                    sendResult_s(result);

                }
            }
        }
    };
    private String ip, port,username,password,nodesoure;


   public  void  stopQX()
   {

   }
    public void startQX() {
        new Thread() {
            public void run() {
                AppHelper.AK = "";
                AppHelper.AS = "";
                try {
                    SignatureDemo.Abc(SN.getText().toString(), LX.getText().toString(), Difference.this);
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
                                        .setDeviceId(SN.getText().toString())
                                        .setDeviceType(LX.getText().toString()).build())
                        .setRtcmSDKCallback(Difference.this);

                RtcmSDKManager.getInstance().init(builder.build());
                RtcmSDKManager.getInstance().auth();
            }
        }.start();
    }
    public static final String RESULT_NETRESULT = "NetResult";

    public static final String MY_FLAGS_FOR_CORS = "myFlags";
    public static final int FLAG_DATARECEIVE = 0x109;
    public static final String ACTION_SENDRESULT = "sendresult";
    private Intent intent = new Intent(ACTION_SENDRESULT);
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
