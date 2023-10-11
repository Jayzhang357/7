package com.zhd.shj.activity;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.zhd.democors.BackgroundWork;
import com.zhd.shj.BigConfigxml;
import com.zhd.shj.CorsGprsService;
import com.zhd.shj.QXsdkmap;
import com.zhd.shj.R;

import com.zhd.zhdcorsnet.NetHelper;
import com.zhd.zhdcorsnet.NetResult;
import com.zhd.zhdcorsnet.SourceNode;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NtripSetting extends BaseActivity {

    private Button mBtnBack, mbtnGet,mbtnConect,mbtnbreakOff;
    public static final String ACTION_SENDRESULT = "sendGGA";
    private EditText mEtIP, mEtPort,mEtUsername,mEtPassword;
    private String ip, port,username,password,nodesoure;
    private EditText mEtSoure;
    private Spinner mspSourece;
    private BigConfigxml mConfigxml;
    private   String[] nodeNames=new String[] {""};
    private void intView() {
        mEtUsername= (EditText) findViewById(R.id.EtUsername);
        mEtPassword= (EditText) findViewById(R.id.EtPassword);
        mReception01= (TextView) findViewById(R.id.TextViewReception);
        mConfigxml = BigConfigxml.getInstantce(NtripSetting.this);
        mEtSoure = (EditText) findViewById(R.id.EtPort1);
        mspSourece = (Spinner) findViewById(R.id.spinner1);
        ArrayAdapter<String> adapter4 = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, new String[]{mConfigxml.getSourece()});
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
        mspSourece.setVisibility(View.GONE);
        mbtnConect = (Button) findViewById(R.id.btnConect);
        mbtnConect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendResult_s(2310);
                starCos();

            }
        });
        mbtnbreakOff = (Button) findViewById(R.id.btnbreakOff);
        mbtnbreakOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(NtripSetting.this
                        ,
                        CorsGprsService.class);
                NtripSetting.this.stopService(mIntent);

            }
        });
        mBtnBack = (Button) findViewById(R.id.btnFinish);
        mEtIP = (EditText) findViewById(R.id.EtIP);

        mEtPort = (EditText) findViewById(R.id.EtPort);
        mEtIP.setText(mConfigxml.getip());
        mEtPort.setText(mConfigxml.getport()+"");

        mEtUsername.setText(mConfigxml.getusername()+"");
        mEtPassword.setText(mConfigxml.getpassword()+"");

        mEtSoure .setText(mConfigxml.getSourece()+"");

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
        mBtnBack.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                finish();


            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_ntrip_setting);
        intView();
        registerBoradcastReceiver();
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
                        getApplication(), android.R.layout.simple_spinner_item,
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
        Intent mIntent = new Intent(NtripSetting.this
                ,
                CorsGprsService.class);
        mIntent.putExtra("ip", ip);
        mIntent.putExtra("port", port);
        mIntent.putExtra("username", username);
        mIntent.putExtra("password", password);
        mIntent.putExtra("sourcenode", nodesoure);// RTCM2.3"0020028028"
        mIntent.putExtra("reconnect", true);

        NtripSetting.this.stopService(mIntent);
        NtripSetting.this.startService(mIntent);
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
                if (mReception01 != null) {

                    String mReception01text = "";
                    for (int k = mQXsdkmap.size() - 1; k >= 0; k--) {
                        mReception01text += mQXsdkmap.get(k).sdkTime
                                + "    返回代码："
                                + mQXsdkmap.get(k).sdktype
                                + "     "
                                + getResultMsg(mQXsdkmap
                                .get(k).sdktype, NtripSetting.this) + "\n";
                    }
                    mReception01.setText(mReception01text);
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
                Intent mIntent = new Intent(NtripSetting.this
                        ,
                        CorsGprsService.class);
                mIntent.putExtra("ip", ip);
                mIntent.putExtra("port", port);
                mIntent.putExtra("username", username);
                mIntent.putExtra("password", password);
                mIntent.putExtra("sourcenode", nodesoure);// RTCM2.3"0020028028"
                mIntent.putExtra("reconnect", true);
                NtripSetting.this.stopService(mIntent);
                Thread.sleep(5000);
                NtripSetting.this.startService(mIntent);
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

            if (action.equals(CorsGprsService.ACTION_SENDRESULT)) {
                int result = intent.getExtras().getInt(CorsGprsService.RESULT_NETRESULT);
                if (result == NetResult.GPRSSet_NoResponse || result == NetResult.GPRSSet_NoResponse_And_Reconnection) {
                    new Thread(sendable).start();
                }
                Log.e("界面", getResultMsg(result, NtripSetting.this) + "1234");
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
                } else {

                    sendResult_s(result);

                }
            }
        }
    };

}