package com.zhd.shj.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.InputFilter;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.android.manager.IMcuManager;
import com.android.manager.IMcuManagerListener;
import com.zhd.gnssmanager.Com4Receive;
import com.zhd.serialportmanage.SerialPortManager;
import com.zhd.shj.AleartDialogHelper;
import com.zhd.shj.BigConfigxml;
import com.zhd.shj.CustomDialog;
import com.zhd.shj.R;
import com.zhd.shj.SerialPortFinderHelper;

import java.util.regex.Pattern;


public class CanShuSet extends BaseActivity {

    public RelativeLayout root2, root3, root4;
    public RadioButton r1, r2;
    public RadioGroup radioGroup;
    public EditText kzzd, kzlmd, wdcs, ycxz, sdwsx,et_mask;
    private Button btnBack, btnFinish, hfmrcs, save,btn_send;
    private Com4Receive mCom4Receive = null;

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_canshu);
        iniView();
        mCom4Receive = mCom4Receive.getInstance(6, mConfigxml.getCompath());

        mCom4Receive.getCom4GpsSerialPort().setReceiveMessageListener(
                mSerialPortDataListener);
    }

    public BigConfigxml mConfigxml;
    public Spinner mspCompath, spChannel, sp_baud, spidType, spFrameType;
    public CheckBox gl,cb_loop;
    public boolean Sendcan=false;
    public EditText etId,etData,et_count,et_time;
    private WorkerThread workerThread;
    int counter;
    private class WorkerThread extends Thread {
        @Override
        public void run() {
             counter=Integer.parseInt(et_count.getText()+"");
            int time=Integer.parseInt(et_time.getText()+"");
            while (Sendcan && counter >0) {
                try {
                    Thread.sleep(time);
                    sendCanData();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
                Log.e("发送can123",counter+"");
                counter--;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                    et_count.setText(counter+"");
                    }
                });

            }
            Sendcan = false;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e("发送can123","结束");
                    Sendcan = false;
                    spChannel.setEnabled(true);
                    sp_baud.setEnabled(true);
                    spidType.setEnabled(true);
                    spFrameType.setEnabled(true);

                    etId.setEnabled(true);
                    etData.setEnabled(true);
                    et_time.setEnabled(true);
                    et_count.setEnabled(true);
                    btn_send.setText(R.string.send);
                }
            });
        }
    }
    public void intcan()

    {
        cb_loop= (CheckBox) findViewById(R.id.cb_loop);
        etId= (EditText) findViewById(R.id.et_id);
        etData= (EditText) findViewById(R.id.et_data);
        et_time= (EditText) findViewById(R.id.et_time);
        et_count= (EditText) findViewById(R.id.et_count);
        btn_send= (Button) findViewById(R.id.btn_send);
        btn_send.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SuspiciousIndentation")
            @Override
            public void onClick(View arg0) {

                if(cb_loop.isChecked())
                {
                  if(etData.getText().length()>0&&etId.getText().length()>0&&et_time.getText().length()>0&&et_count.getText().length()>0)
                    if (Sendcan) {
                        Sendcan = false;
                        spChannel.setEnabled(true);
                        sp_baud.setEnabled(true);
                        spidType.setEnabled(true);
                        spFrameType.setEnabled(true);

                        etId.setEnabled(true);
                        etData.setEnabled(true);
                        et_time.setEnabled(true);
                        et_count.setEnabled(true);
                        btn_send.setText(R.string.send);
                        if (workerThread != null) {
                            workerThread.interrupt();
                            workerThread = null;
                        }

                    } else {
                        spChannel.setEnabled(false);
                        sp_baud.setEnabled(false);
                        spidType.setEnabled(false);
                        spFrameType.setEnabled(false);

                        etId.setEnabled(false);
                        etData.setEnabled(false);
                        et_time.setEnabled(false);
                        et_count.setEnabled(false);
                        Sendcan = true;
                        btn_send.setText(R.string.stop);
                        workerThread = new WorkerThread();
                        workerThread.start();
                    }
                }
                else
                {
                    if(etData.getText().length()>0&&etId.getText().length()>0)
                    sendCanData();
                }

            }
        });
        gl= (CheckBox) findViewById(R.id.gl);
        et_mask= (EditText) findViewById(R.id.et_mask);
        mReceptioncan = (TextView) findViewById(R.id.TextViewReceptioncan);

        String[] channels = getResources().getStringArray(R.array.channels);;
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this,
                R.layout.myspinner, channels);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spChannel = findViewById(R.id.sp_channel);
        spChannel.setAdapter(adapter1);

        spChannel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                int channel = spChannel.getSelectedItemPosition() + 1;
                if (iMcuManager != null) {
                    try {

                        Log.d("比特率",  iMcuManager.getCanBaud((byte)channel)+"111");
                        if((int)iMcuManager.getCanBaud((byte)channel)<=5)
                            sp_baud.setSelection((int)iMcuManager.getCanBaud((byte)channel));
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        channels = getResources().getStringArray(R.array.bauds);;
        adapter1 = new ArrayAdapter<String>(this,
                R.layout.myspinner, channels);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        sp_baud = findViewById(R.id.sp_baud);
        sp_baud.setAdapter(adapter1);

        sp_baud.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int channel = spChannel.getSelectedItemPosition() + 1;
                if (iMcuManager != null) {
                    try {
                        Log.d("mzj","onItemSelected");
                        if (iMcuManager.setCanBaud((byte) channel, (byte) position)) {
                            Toast.makeText(CanShuSet.this, getString(R.string.szcg), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(CanShuSet.this, getString(R.string.szsb), Toast.LENGTH_SHORT).show();
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        channels = getResources().getStringArray(R.array.types);;
        adapter1 = new ArrayAdapter<String>(this,
                R.layout.myspinner, channels);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spidType = findViewById(R.id.sp_idtype);
        spidType.setAdapter(adapter1);

        spidType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        channels = getResources().getStringArray(R.array.frameTypes);;
        adapter1 = new ArrayAdapter<String>(this,
                R.layout.myspinner, channels);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spFrameType = findViewById(R.id.sp_frametype);
        spFrameType.setAdapter(adapter1);

        spFrameType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

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
    private EditText mEditTextEmission;
    private TextView mReception,mReceptioncan;
    private Button mbtnPasue ,btn_pasue;
    private Button sendbt = null;
    private Button buSet = null;
    private void iniView() {
        intcan();
        mConfigxml = BigConfigxml
                .getInstantce(CanShuSet.this);
        buSet = (Button) findViewById(R.id.buSet);
        buSet.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final EditText ed = new EditText(CanShuSet.this);
                ed.setBackgroundResource(R.drawable.ic_edittext);
                ed.setInputType(InputType.TYPE_CLASS_NUMBER
                        | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                ed.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});

                final CustomDialog alertDialog = new CustomDialog.Builder(CanShuSet.this)
                        .setTitle(getString(R.string.Notice))
                        .setContentView(ed)
                        .setMessage(getString(R.string.verification_hint))
                        .setPositiveButton(getString(R.string.confirm),
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String inputCode = ed.getText().toString();
                                        SharedPreferences sharedPreferences = getSharedPreferences(
                                                "code", Activity.MODE_PRIVATE);
                                        String code = sharedPreferences.getString("code",
                                                "");
                                        if (code.equals("")) {
                                            SharedPreferences.Editor editor = sharedPreferences
                                                    .edit();
                                            // 用putString的方法保存数据
                                            editor.putString("code", "666666");
                                            editor.commit();
                                            code = "666666";
                                        }

                                        if (inputCode.equals(code)
                                                || inputCode.equals("666666")) {
                                            dialog.dismiss();
                                            DisplayMetrics dm = new DisplayMetrics();
                                            getWindowManager().getDefaultDisplay().getMetrics(dm);
                                            BigConfigxml mConfigxml = BigConfigxml
                                                    .getInstantce(CanShuSet.this);
                                            mConfigxml.setCompath(mspCompath.getSelectedItem().toString());
                                            mConfigxml.saveConfigXml();
                                            new Thread() {
                                                @Override
                                                public void run() {
                                                    Intent intent = null;
                                                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                                                        intent = new Intent(CanShuSet.this, MainActivity.class);
                                                    }
                                                    startActivity(intent);
                                                    System.exit(0);
                                                }
                                            }.start();

                                        } else if (inputCode.equals("")) {
                                            AleartDialogHelper
                                                    .alertToast(
                                                            CanShuSet.this
                                                            ,
                                                            getString(R.string.null_message_input_vir));
                                        } else {
                                            AleartDialogHelper
                                                    .alertToast(
                                                            CanShuSet.this
                                                            ,
                                                            getString(R.string.wrong_message_input_vir));
                                            ed.setText("");

                                        }
                                    }
                                })
                        .setNegativeButton(getString(R.string.cancel),
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).create();

                alertDialog.show();
                ed.setOnEditorActionListener(new TextView.OnEditorActionListener() {

                    @Override
                    public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
                        // TODO Auto-generated method stub
                        if (arg1 == EditorInfo.IME_ACTION_DONE) {
                            String inputCode = ed.getText().toString();
                            SharedPreferences sharedPreferences = getSharedPreferences(
                                    "code", Activity.MODE_PRIVATE);
                            String code = sharedPreferences.getString("code",
                                    "");
                            if (code.equals("")) {
                                SharedPreferences.Editor editor = sharedPreferences
                                        .edit();
                                // 用putString的方法保存数据
                                editor.putString("code", "666666");
                                editor.commit();
                                code = "666666";
                            }

                            if (inputCode.equals(code)
                                    || inputCode.equals("666666")) {
                                alertDialog.cancel();
                                DisplayMetrics dm = new DisplayMetrics();

                                getWindowManager().getDefaultDisplay().getMetrics(dm);

                                BigConfigxml mConfigxml = BigConfigxml
                                        .getInstantce(CanShuSet.this);
                                mConfigxml.setCompath(mspCompath.getSelectedItem().toString());
                                mConfigxml.saveConfigXml();
                                new Thread() {
                                    @Override
                                    public void run() {
                                        Intent intent = null;
                                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                                            intent = new Intent(CanShuSet.this
                                                    , MainActivity.class);
                                        }

                                        startActivity(intent);
                                        System.exit(0);
                                    }
                                }.start();
                            } else if (inputCode.equals("")) {
                                AleartDialogHelper
                                        .alertToast(
                                                CanShuSet.this
                                                ,
                                                getString(R.string.null_message_input_vir));
                            } else {
                                AleartDialogHelper
                                        .alertToast(
                                                CanShuSet.this
                                                ,
                                                getString(R.string.wrong_message_input_vir));
                                ed.setText("");
                            }
                        }

                        return false;
                    }
                });


            }
        });
        sendbt = (Button) findViewById(R.id.sendbt);
        sendbt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String cmd = mEditTextEmission.getText() + "\r\n";
                mCom4Receive.sendByte(cmd.getBytes());
            }
        });
        mEditTextEmission = (EditText) findViewById(R.id.EditTextEmission);
        mbtnPasue = (Button) findViewById(R.id.btnPasue);
        mbtnPasue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                if (mPasue) {
                    mPasue = false;
                    mbtnPasue.setText(R.string.goon);
                } else {
                    mPasue = true;
                    mbtnPasue.setText(R.string.pause);
                }

            }
        });
        btn_pasue = (Button) findViewById(R.id.btn_pasue);
        btn_pasue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                if (mPasuecan) {
                    mPasuecan = false;
                    btn_pasue.setText(R.string.goon);
                } else {
                    mPasuecan = true;
                    btn_pasue.setText(R.string.pause);
                }

            }
        });
        mReception = (TextView) findViewById(R.id.TextViewReception);
        mspCompath = (Spinner) findViewById(R.id.spinner1);
        SerialPortFinderHelper abcd = new SerialPortFinderHelper();
        pathlist = abcd.getAllDevicesPath();
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this,
                R.layout.myspinner, pathlist);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mspCompath.setAdapter(adapter1);
        mspCompath.setSelection(bindpath(mConfigxml.getCompath()));
        save = findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(kzzd.length()==0||kzlmd.length()==0||wdcs.length()==0||ycxz.length()==0||sdwsx.length()==0)
                {
                    Toast.makeText(getApplicationContext(), getString(R.string.setmes), Toast.LENGTH_SHORT).show();

                    return;
                }
                else {
                    mConfigxml.kzzd =Integer.parseInt( kzzd.getText()+"");
                    mConfigxml.kzlmd =Integer.parseInt( kzlmd.getText()+"");
                    mConfigxml.wdcs =Integer.parseInt( wdcs.getText()+"");
                    mConfigxml.ycxz =Integer.parseInt( ycxz.getText()+"");
                    mConfigxml.sdwsx =Integer.parseInt( sdwsx.getText()+"");
                    mConfigxml.saveConfigXml();
                }
            }
        });
        hfmrcs = findViewById(R.id.hfmrcs);
        hfmrcs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mConfigxml.kzzd =20;
                mConfigxml.kzlmd =5;
                mConfigxml.wdcs =100;
                mConfigxml.ycxz =5;
                mConfigxml.sdwsx =10;
                mConfigxml.saveConfigXml();
                kzzd.setText(mConfigxml.kzzd + "");
                kzlmd.setText(mConfigxml.kzlmd + "");
                wdcs.setText(mConfigxml.wdcs + "");
                ycxz.setText(mConfigxml.ycxz + "");
                sdwsx.setText(mConfigxml.sdwsx + "");
            }
        });
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CanShuSet.this,
                        SystemSet.class));
                finish();
            }
        });
        btnFinish = findViewById(R.id.btnFinish);
        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });
        kzzd = findViewById(R.id.kzzd);
        kzlmd = findViewById(R.id.kzlmd);
        wdcs = findViewById(R.id.wdcs);
        ycxz = findViewById(R.id.ycxz);
        sdwsx = findViewById(R.id.sdwsx);

        kzzd.setText(mConfigxml.kzzd + "");
        kzlmd.setText(mConfigxml.kzlmd + "");
        wdcs.setText(mConfigxml.wdcs + "");
        ycxz.setText(mConfigxml.ycxz + "");
        sdwsx.setText(mConfigxml.sdwsx + "");
        radioGroup = findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // 处理选项的选择事件
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
                        bindService();
                        spChannel.setSelection(1);
                        root4.setVisibility(View.VISIBLE);
                        root3.setVisibility(View.INVISIBLE);
                        root2.setVisibility(View.INVISIBLE);
                        break;
                    // 处理更多选项
                }
            }
        });
        root2 = (RelativeLayout) findViewById(R.id.root2);
        root2.setVisibility(View.VISIBLE);
        root3 = (RelativeLayout) findViewById(R.id.root3);
        root3.setVisibility(View.INVISIBLE);
        root4= (RelativeLayout) findViewById(R.id.root4);
        root4.setVisibility(View.INVISIBLE);
        r1 = (RadioButton) findViewById(R.id.r1);
        r2 = (RadioButton) findViewById(R.id.r2);
        r1.setChecked(true);


    }
    private boolean mPasue = true;
    private boolean mPasuecan = true;
    private Handler mpostHandler = new Handler();
    SerialPortManager.IReceiveDataListener mSerialPortDataListener = new SerialPortManager.IReceiveDataListener() {
        @Override
        public void handleReceiveMessage1(int com, byte[] data) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mPasue && root3.getVisibility() == View.VISIBLE) {
                        if (mReception != null) {
                            if (mReception.getText().length() > 2000) {
                                Log.e("清空", "");
                                mReception.setText("");
                            }
                            mReception.append(new String(data));
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
                    }
                }
            });


        }

        @Override
        public void handleReceiveMessage(byte[] messge) {
            // TODO Auto-generated method stub

        }

    };
     private  boolean getServive=false;
    private void bindService() {
        getServive=true;
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
    private int revCount = 0;
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
            StringBuilder stringBuilder = new StringBuilder(String.format("%05d", ++revCount) +  " [通道" + channel + "] " + (id_extend ? "[扩展帧]" : "[标准帧]") + " " + (frame_remote ? "[远程帧]" : "[数据帧]") + " id:[0x" + String.format("%08X", id) + "] " + "size:" + data.length + ", data: ");
            if(gl.isChecked())
            {

                try {
                    long mask = Long.valueOf(et_mask.getText().toString(), 16);
                    Log.e("数据据",mask+";"+id);
                    // 现在 hexValue 变量中存储了输入的十六进制数的十进制表示
                    if(mask!=id) return;
                } catch (NumberFormatException e) {
                    // 处理无效的输入
                    return;
                }

            }


            for (int i = 0; i < data.length; i++) {
                stringBuilder.append(String.format("%02X ", data[i]));
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mPasuecan && root4.getVisibility() == View.VISIBLE) {
                        if (mReceptioncan != null) {
                            if (mReceptioncan.getText().length() > 20000) {
                                Log.e("清空", "");
                                mReceptioncan.setText("");
                            }
                            mReceptioncan.append(stringBuilder+"\r\n");
                            mpostHandler.post(new Runnable() {
                                // @Override
                                public void run() {
                                    ScrollView scroll = (ScrollView) findViewById(R.id.scrollcan);
                                    View inner = (TextView) findViewById(R.id.TextViewReceptioncan);
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
                }
            });
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
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(getServive)
        unbindService();
        if (iMcuManager != null) {
            try {
                iMcuManager.removeMcuManagerListener(iMcuManagerListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

    }

    boolean sendCanData() {
        int channel = spChannel.getSelectedItemPosition() + 1;
        long id = Long.valueOf(etId.getText().toString(), 16);
        boolean isIdExtend = spidType.getSelectedItemPosition() != 0 ? true : false;
        boolean isFrameRemote = spFrameType.getSelectedItemPosition() != 0 ? true : false;
        byte[] data = getBytes(etData.getText().toString());
        String abc = null;
        for(int i=0;i<data.length;i++)
        {
            abc+=data[0]+" ";
        }
        try {
            if (iMcuManager.sendCanData((byte) channel, id, isIdExtend, isFrameRemote, data)) {
                Log.d("发送", "CAN数据发送成功;发送ID"+id+";发送数据"+abc);
                return true;
            } else {
                Toast.makeText(this, getString(R.string.fssb), Toast.LENGTH_SHORT).show();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    byte[] getBytes(String hexString) {
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
}
