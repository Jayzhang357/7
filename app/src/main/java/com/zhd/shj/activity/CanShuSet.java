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
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.annotation.RequiresApi;

import com.android.manager.IMcuManager;
import com.android.manager.IMcuManagerListener;
import com.zhd.entry.Canmand;
import com.zhd.gnssmanager.Com4Receive;
import com.zhd.serialportmanage.SerialPortManager;
import com.zhd.shj.AleartDialogHelper;
import com.zhd.shj.BigConfigxml;
import com.zhd.shj.CustomDialog;
import com.zhd.shj.R;
import com.zhd.shj.SerialPortFinderHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


public class CanShuSet extends BaseActivity {
    String[] mllist = new String[]{"油温传感器设置命令", "压力传感器设置命令", "提升/下降阀设置命令", "前倾/后仰阀设置命令", "左/右油缸阀设置命令"};
    List<String[]> mlistsum = new ArrayList<>();
    List<String[]> mlistsumtxt = new ArrayList<>();
    List<List<Canmand>> mlistsumall = new ArrayList<>();
    public RelativeLayout root2, root3, root4;
    public RadioButton r1, r2;
    public RadioGroup radioGroup;
    public EditText kzzd, kzlmd, wdcs, ycxz, sdwsx, et_mask, zhid, zhcontnt;
    private Button btnBack, btnFinish, hfmrcs, save, btn_send;
    private Com4Receive mCom4Receive = null;
    private CheckBox mlset;

    private TextView ttt1, ttt2, ttt3, ttt4;

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_canshu);
        databind();
        iniView();
        setml();
        mCom4Receive = mCom4Receive.getInstance(6, mConfigxml.getCompath());

        mCom4Receive.getCom4GpsSerialPort().setReceiveMessageListener(
                mSerialPortDataListener);
    }

    public void setml() {

        mlset = (CheckBox) findViewById(R.id.mlset);
        mlset.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    switch (id_stype.getSelectedItemPosition()) {
                        case 0:
                            zhcontnt.setText("020232CD03230625");
                            break;
                        case 1:
                            zhcontnt.setText("0201002800040014");
                            break;
                        case 2:
                            zhcontnt.setText("020264A0000003EB");
                            break;
                        case 3:
                            zhcontnt.setText("020164A0000003EB");
                            break;
                        case 4:
                            zhcontnt.setText("020164A0000003EB");
                            break;

                    }
                } else {

                }
            }
        });
        id_stype = (Spinner) findViewById(R.id.id_stype);
        zhid = (EditText) findViewById(R.id.zhid);
        zhcontnt = (EditText) findViewById(R.id.zhcontnt);
        btn_mlcomplete = (Button) findViewById(R.id.btn_mlcomplete);
        ArrayAdapter adapter1 = new ArrayAdapter<String>(this,
                R.layout.myspinner, mllist);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        id_stype.setAdapter(adapter1);
        id_stype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                setSpind(position);
                switch (position) {
                    case 0:
                        zhid.setText("18A");
                        break;
                    case 1:
                        zhid.setText("28A");
                        break;
                    case 2:
                        zhid.setText("38A");
                        break;
                    case 3:
                        zhid.setText("48A");
                        break;
                    case 4:
                        zhid.setText("18B");
                        break;

                }
                if (mlset.isChecked()) {
                    switch (position) {
                        case 0:
                            zhcontnt.setText("020232CD03230625");
                            break;
                        case 1:
                            zhcontnt.setText("0201002800040014");
                            break;
                        case 2:
                            zhcontnt.setText("020264A0000003EB");
                            break;
                        case 3:
                            zhcontnt.setText("020164A0000003EB");
                            break;
                        case 4:
                            zhcontnt.setText("020164A0000003EB");
                            break;


                    }

                } else {

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        btn_mlok = (Button) findViewById(R.id.btn_mlok);
        btn_mlcancel = (Button) findViewById(R.id.btn_mlcancel);
        btn_mlok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                rllll.setVisibility(View.INVISIBLE);
                etData.setText(zhcontnt.getText());
                etId.setText(zhid.getText());
            }
        });
        btn_mlcancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                rllll.setVisibility(View.INVISIBLE);
            }
        });
        btn_mlcomplete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                String a0="00";
                String a1=Integer.toHexString(lx_stype.getSelectedItemPosition()).toUpperCase(); ;
                String a2=Integer.toHexString(abctemp.a).toUpperCase(); ;
                String a3=Integer.toHexString(abctemp.b).toUpperCase(); ;
                String a4=get16(abctemp.c);
                String a5=get16(abctemp.d);
                zhcontnt.setText(a0+a1+a2+a3+a4+a5);
            }
        });
    }

    public void setSpind(int styleid) {
        lx_stype = (Spinner) findViewById(R.id.lx_stype);
        ttt1 = (TextView) findViewById(R.id.ttt1);
        ttt2 = (TextView) findViewById(R.id.ttt2);
        ttt3 = (TextView) findViewById(R.id.ttt3);
        ttt4 = (TextView) findViewById(R.id.ttt4);
        ttt1.setText(mlistsumtxt.get(styleid)[0]);
        ttt2.setText(mlistsumtxt.get(styleid)[1]);
        ttt3.setText(mlistsumtxt.get(styleid)[2]);
        ttt4.setText(mlistsumtxt.get(styleid)[3]);

        textv1 = (TextView) findViewById(R.id.textv1);
        textv1_1 = (TextView) findViewById(R.id.textv1_1);
        textv2 = (TextView) findViewById(R.id.textv2);
        textv2_1 = (TextView) findViewById(R.id.textv2_1);
        edit1_1 = (TextView) findViewById(R.id.edit1_1);
        edit2_1 = (TextView) findViewById(R.id.edit2_1);
        edit1 = (EditText) findViewById(R.id.edit1);
        edit2 = (EditText) findViewById(R.id.edit2);

        l3 = (LinearLayout) findViewById(R.id.l3);
        l4 = (LinearLayout) findViewById(R.id.l4);
        sb1 = (SeekBar) findViewById(R.id.sb1);
        sb2 = (SeekBar) findViewById(R.id.sb2);
        bb1 = (Button) findViewById(R.id.bb1);
        bb1_1 = (Button) findViewById(R.id.bb1_1);
        bb2 = (Button) findViewById(R.id.bb2);
        bb2_1 = (Button) findViewById(R.id.bb2_1);


        ArrayAdapter adapter1 = new ArrayAdapter<String>(this,
                R.layout.myspinner, mlistsum.get(styleid));
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lx_stype.setAdapter(adapter1);
        lx_stype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                abctemp = mlistsumall.get(id_stype.getSelectedItemPosition()).get(position);
                if (abctemp.canshu) {
                    l3.setVisibility(View.VISIBLE);
                    l4.setVisibility(View.VISIBLE);
                } else {
                    l3.setVisibility(View.INVISIBLE);
                    l4.setVisibility(View.INVISIBLE);
                }
                textv1.setText((abctemp.a + abctemp.a_add) * abctemp.a_cy + "");
                textv1_1.setText(abctemp.a_t + "");
                textv2.setText((abctemp.b + abctemp.b_add) * abctemp.b_cy + "");
                textv2_1.setText(abctemp.b_t + "");

                edit1_1.setText(abctemp.c_t + "");

                edit2_1.setText(abctemp.d_t + "");
                sb1.setMax(abctemp.a_max);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    sb1.setMin(abctemp.a_min);
                }
                sb2.setMax(abctemp.b_max);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    sb2.setMin(abctemp.b_min);
                }

                sb1.setProgress(abctemp.a);
                sb2.setProgress(abctemp.b);
                edit1.setText(abctemp.c+"");
                edit2.setText(abctemp.d+"");
                edit1.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                        // 文本改变前的操作
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                        // 文本改变中的操作
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        // 文本改变后的操作
                        if(editable.toString().length()==0)
                            return;
                        int text = Integer.parseInt(editable.toString());
                        if(text<abctemp.c_min||text>abctemp.c_max)
                        {
                            edit1.setText(abctemp.c+"");
                            AleartDialogHelper.alertToast(CanShuSet.this, "请输入"+abctemp.c_min+"到"+abctemp.c_max+"数值");
                        }
                        else
                        {
                            abctemp.c=text;
                        }
                        // 在这里你可以处理输入的文本
                    }
                });
                edit2.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                        // 文本改变前的操作
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                        // 文本改变中的操作
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        // 文本改变后的操作
                        if(editable.toString().length()==0)
                            return;
                        int text = Integer.parseInt(editable.toString());
                        if(text<abctemp.d_min||text>abctemp.d_max)
                        {
                            edit2.setText(abctemp.d+"");
                            AleartDialogHelper.alertToast(CanShuSet.this, "请输入"+abctemp.d_min+"到"+abctemp.d_max+"数值");
                        }
                        else
                        {
                            abctemp.d=text;
                        }
                        // 在这里你可以处理输入的文本
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        sb1
                .setOnSeekBarChangeListener(new OnSeekBarChangeListenerSB1());
        sb2
                .setOnSeekBarChangeListener(new OnSeekBarChangeListenerSB2());
        bb1.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        abctemp.a--;
                        if(  abctemp.a<abctemp.a_min)
                        {
                            abctemp.a=abctemp.a_min;
                        }
                        sb1.setProgress(abctemp.a);
                        textv1.setText(( abctemp.a + abctemp.a_add) * abctemp.a_cy + "");
                    }
                });
        bb1_1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                abctemp.a++;
                if(  abctemp.a>abctemp.a_max)
                {
                    abctemp.a=abctemp.a_max;
                }
                sb1.setProgress(abctemp.a);
                textv1.setText(( abctemp.a + abctemp.a_add) * abctemp.a_cy + "");


            }
        });
        bb2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                abctemp.b--;
                if(  abctemp.b<abctemp.b_min)
                {
                    abctemp.b=abctemp.b_min;
                }
                sb2.setProgress(abctemp.b);
                textv2.setText(( abctemp.b + abctemp.b_add) * abctemp.b_cy + "");


            }
        });
        bb2_1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                abctemp.b++;
                if(  abctemp.b>abctemp.b_max)
                {
                    abctemp.b=abctemp.b_max;
                }
                sb2.setProgress(abctemp.b);
                textv2.setText(( abctemp.b + abctemp.b_add) * abctemp.b_cy + "");

            }
        });

    }

    private Canmand abctemp;
   private String get16(int wordValue )
   {

       String hexString = String.format("%04X", wordValue);


       String highByte = hexString.substring(0, 2);
       String lowByte = hexString.substring(2, 4);

    return  highByte+lowByte;
   }
    private class OnSeekBarChangeListenerSB1 implements
            SeekBar.OnSeekBarChangeListener {

        // 触发操作，拖动
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            Log.e("参数百分比", progress + "");
            textv1.setText((progress + abctemp.a_add) * abctemp.a_cy + "");

        }

        // 表示进度条刚开始拖动，开始拖动时候触发的操作
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        // 停止拖动时候
        public void onStopTrackingTouch(SeekBar seekBar) {
            abctemp.a = (int) (((Double.parseDouble(textv1.getText() + "")) - abctemp.a_add) / abctemp.a_cy);
            Log.e("参数", abctemp.a + "");
        }
    }

    private class OnSeekBarChangeListenerSB2 implements
            SeekBar.OnSeekBarChangeListener {

        // 触发操作，拖动
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {


            textv2.setText((progress + abctemp.b_add) * abctemp.b_cy + "");

        }

        // 表示进度条刚开始拖动，开始拖动时候触发的操作
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        // 停止拖动时候
        public void onStopTrackingTouch(SeekBar seekBar) {
            abctemp.b = (int) ((Double.parseDouble(textv2.getText() + "") - abctemp.b_add) / abctemp.b_cy);
            Log.e("参数", abctemp.b + "");
        }
    }

    public void databind() {
        mlistsum.add(new String[]{"电压型", "电流型", "电阻型"});

        mlistsum.add(new String[]{"电压型", "电流型"});

        mlistsum.add(new String[]{"DO型", "PWM型", "PWMI型"});

        mlistsum.add(new String[]{"DO型", "PWM型"});

        mlistsum.add(new String[]{"DO型", "PWM型"});

        mlistsumtxt.add(new String[]{"油温传感器低温度点温度", "油温传感器高温度点温度", "油温传感器低温度点模拟量", "油温传感器高温度点模拟量"});

        mlistsumtxt.add(new String[]{"压力传感器低压力点压力", "压力传感器高压力点压力", "压力传感器低压力点模拟量", "压力传感器高压力点模拟量"});

        mlistsumtxt.add(new String[]{"比例阀频率", "比例阀内阻", "最小电流/占空比", "最大电流/占空比"});

        mlistsumtxt.add(new String[]{"比例阀频率", "比例阀内阻", "最小占空比", "最大占空比"});

        mlistsumtxt.add(new String[]{"比例阀频率", "比例阀内阻", "最小占空比", "最大占空比"});
        //18A
        List<Canmand> mlistsumtemp = new ArrayList<>();
        Canmand abc = new Canmand();
        abc.a_t = "℃";
        abc.a_max = 250;
        abc.a = 50;
        abc.a_min = 50;
        abc.a_add = -100;
        abc.b_t = "℃";
        abc.b_max = 250;
        abc.b_min = 50;
        abc.b = 50;
        abc.b_add = -100;
        abc.c_t = "mV";
        abc.d_t = "mV";

        mlistsumtemp.add(abc);
        abc = new Canmand();
        abc.a_t = "℃";
        abc.a_max = 250;
        abc.a = 50;
        abc.b = 50;
        abc.a_min = 50;
        abc.a_add = -100;
        abc.b_t = "℃";
        abc.b_max = 250;
        abc.b_min = 50;
        abc.b_add = -100;
        abc.c_t = "mA";
        abc.d_t = "mA";

        mlistsumtemp.add(abc);
        abc = new Canmand();
        abc.a_t = "℃";
        abc.a_max = 250;
        abc.a = 50;
        abc.b = 50;
        abc.a_min = 50;
        abc.a_add = -100;
        abc.b_t = "℃";
        abc.b_max = 250;
        abc.b_min = 50;
        abc.b_add = -100;
        abc.c_t = "Ω";
        abc.c = 1;
        abc.c_cy = 0.1;
        abc.d_t = "Ω";
        abc.d = 1;
        abc.d_cy = 0.1;

        mlistsumtemp.add(abc);
        mlistsumall.add(mlistsumtemp);
        //28A
        mlistsumtemp = new ArrayList<>();
        abc = new Canmand();
        abc.a_t = "Bar";
        abc.a_max = 60;
        abc.a_cy = 10;
        abc.b_t = "Bar";
        abc.b_max = 60;
        abc.b_cy = 10;
        abc.c_t = "mV";
        abc.d_t = "mV";
        mlistsumtemp.add(abc);
        abc = new Canmand();
        abc.a_t = "Bar";
        abc.b_max = 60;
        abc.a_cy = 10;
        abc.b_t = "Bar";
        abc.a_max = 60;
        abc.b_cy = 10;
        abc.c_t = "mA";
        abc.d_t = "mA";
        mlistsumtemp.add(abc);
        mlistsumall.add(mlistsumtemp);

        //38A
        mlistsumtemp = new ArrayList<>();
        abc = new Canmand();
        abc.canshu = false;
        mlistsumtemp.add(abc);
        abc = new Canmand();
        abc.a_t = "Hz";
        abc.a_max = 100;
        abc.a_cy = 10;
        abc.b_t = "Ω";
        abc.b_min = 1;
        abc.b_cy = 0.1;
        abc.b = 1;
        abc.c_t = "mA";
        abc.c_max = 2000;
        abc.d_t = "mA";
        abc.d_max = 2000;
        mlistsumtemp.add(abc);
        abc = new Canmand();
        abc.a_t = "Hz";
        abc.a_max = 100;
        abc.a_cy = 10;
        abc.b_t = "Ω";
        abc.b_cy = 0.1;
        abc.b_min = 1;
        abc.b = 1;
        abc.c_t = "%";
        abc.c_max = 1000;
        abc.d_t = "%";
        abc.d_max = 1000;
        mlistsumtemp.add(abc);
        mlistsumall.add(mlistsumtemp);

        //48A
        mlistsumtemp = new ArrayList<>();
        abc = new Canmand();
        abc.canshu = false;
        mlistsumtemp.add(abc);
        abc = new Canmand();
        abc.a_t = "Hz";
        abc.a_max = 100;
        abc.a_cy = 10;
        abc.b_t = "Ω";
        abc.b_min = 1;
        abc.b = 1;
        abc.b_cy = 0.1;
        abc.c_t = "%";
        abc.c_max = 1000;
        abc.d_t = "%";
        abc.d_max = 1000;
        mlistsumtemp.add(abc);

        mlistsumall.add(mlistsumtemp);


        //18B
        mlistsumtemp = new ArrayList<>();
        abc = new Canmand();
        abc.canshu = false;
        mlistsumtemp.add(abc);
        abc = new Canmand();
        abc.a_t = "Hz";
        abc.a_max = 100;
        abc.a_cy = 10;
        abc.b_min = 1;
        abc.b_t = "Ω";
        abc.b_cy = 0.1;
        abc.c_t = "%";
        abc.c_max = 1000;
        abc.d_t = "%";
        abc.d_max = 1000;
        mlistsumtemp.add(abc);

        mlistsumall.add(mlistsumtemp);

    }

    public Button bb1,bb1_1,bb2,bb2_1,btn_mlcomplete,btn_mlok,btn_mlcancel;
    public LinearLayout l3, l4;
    public SeekBar sb1, sb2;
    public TextView textv1, textv2, textv1_1, textv2_1, edit1_1, edit2_1;
    public EditText edit1, edit2;
    public BigConfigxml mConfigxml;
    public Spinner mspCompath, spChannel, sp_baud, spidType, spFrameType, id_stype, lx_stype;
    public CheckBox gl, cb_loop;
    public boolean Sendcan = false;
    public EditText etId, etData, et_count, et_time;
    private WorkerThread workerThread;
    int counter;

    private class WorkerThread extends Thread {
        @Override
        public void run() {
            counter = Integer.parseInt(et_count.getText() + "");
            int time = Integer.parseInt(et_time.getText() + "");
            while (Sendcan && counter > 0) {
                try {
                    Thread.sleep(time);
                    sendCanData();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
                Log.e("发送can123", counter + "");
                counter--;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        et_count.setText(counter + "");
                    }
                });

            }
            Sendcan = false;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e("发送can123", "结束");
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

    public void intcan() {
        cb_loop = (CheckBox) findViewById(R.id.cb_loop);
        etId = (EditText) findViewById(R.id.et_id);
        etData = (EditText) findViewById(R.id.et_data);
        et_time = (EditText) findViewById(R.id.et_time);
        et_count = (EditText) findViewById(R.id.et_count);
        btn_send = (Button) findViewById(R.id.btn_send);
        btn_send.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SuspiciousIndentation")
            @Override
            public void onClick(View arg0) {

                if (cb_loop.isChecked()) {
                    if (etData.getText().length() > 0 && etId.getText().length() > 0 && et_time.getText().length() > 0 && et_count.getText().length() > 0)
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
                } else {
                    if (etData.getText().length() > 0 && etId.getText().length() > 0)
                        sendCanData();
                }

            }
        });
        gl = (CheckBox) findViewById(R.id.gl);
        et_mask = (EditText) findViewById(R.id.et_mask);
        mReceptioncan = (TextView) findViewById(R.id.TextViewReceptioncan);

        String[] channels = getResources().getStringArray(R.array.channels);
        ;
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

                        Log.d("比特率", iMcuManager.getCanBaud((byte) channel) + "111");
                        if ((int) iMcuManager.getCanBaud((byte) channel) <= 5)
                            sp_baud.setSelection((int) iMcuManager.getCanBaud((byte) channel));
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        channels = getResources().getStringArray(R.array.bauds);
        ;
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
                        Log.d("mzj", "onItemSelected");
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


        channels = getResources().getStringArray(R.array.types);
        ;
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
        channels = getResources().getStringArray(R.array.frameTypes);
        ;
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
    private TextView mReception, mReceptioncan;
    private Button mbtnPasue, btn_pasue,btn_ml;
    private Button sendbt = null;
    private Button buSet = null;
    private RelativeLayout rllll;
    private void iniView() {
        intcan();
        mConfigxml = BigConfigxml
                .getInstantce(CanShuSet.this);
        rllll= (RelativeLayout) findViewById(R.id.rllll);
        btn_ml= (Button) findViewById(R.id.btn_ml);
        btn_ml.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                rllll.setVisibility(View.VISIBLE);
            }
        });
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
                if (kzzd.length() == 0 || kzlmd.length() == 0 || wdcs.length() == 0 || ycxz.length() == 0 || sdwsx.length() == 0) {
                    Toast.makeText(getApplicationContext(), getString(R.string.setmes), Toast.LENGTH_SHORT).show();

                    return;
                } else {
                    mConfigxml.kzzd = Integer.parseInt(kzzd.getText() + "");
                    mConfigxml.kzlmd = Integer.parseInt(kzlmd.getText() + "");
                    mConfigxml.wdcs = Integer.parseInt(wdcs.getText() + "");
                    mConfigxml.ycxz = Integer.parseInt(ycxz.getText() + "");
                    mConfigxml.sdwsx = Integer.parseInt(sdwsx.getText() + "");
                    mConfigxml.saveConfigXml();
                }
            }
        });
        hfmrcs = findViewById(R.id.hfmrcs);
        hfmrcs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mConfigxml.kzzd = 20;
                mConfigxml.kzlmd = 5;
                mConfigxml.wdcs = 100;
                mConfigxml.ycxz = 5;
                mConfigxml.sdwsx = 10;
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
        root4 = (RelativeLayout) findViewById(R.id.root4);
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
    private boolean getServive = false;

    private void bindService() {
        getServive = true;
        Intent intent = new Intent();
        ComponentName componentName = new ComponentName("com.android.service", "com.android.service.McuService");
        intent.setComponent(componentName);
        intent.setAction("com.android.service.McuService");
        intent.setType(getPackageName());
        startService(intent);
        bindService(intent, conn, BIND_AUTO_CREATE);
        abc = System.currentTimeMillis() + "";
    }

    String abc = "";
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
            StringBuilder stringBuilder = new StringBuilder(String.format("%05d", ++revCount) + " [通道" + channel + "] " + (id_extend ? "[扩展帧]" : "[标准帧]") + " " + (frame_remote ? "[远程帧]" : "[数据帧]") + " id:[0x" + String.format("%08X", id) + "] " + "size:" + data.length + ", data: ");
            if (gl.isChecked()) {

                try {
                    long mask = Long.valueOf(et_mask.getText().toString(), 16);
                    Log.e("数据据", mask + ";" + id);
                    // 现在 hexValue 变量中存储了输入的十六进制数的十进制表示
                    if (mask != id) return;
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
                            mReceptioncan.append(stringBuilder + "\r\n");
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
            Log.d("mzj", "onServiceConnected");
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

    @SuppressLint("SuspiciousIndentation")
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (getServive)
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
        for (int i = 0; i < data.length; i++) {
            abc += data[0] + " ";
        }
        try {
            if (iMcuManager.sendCanData((byte) channel, id, isIdExtend, isFrameRemote, data)) {
                Log.d("发送", "CAN数据发送成功;发送ID" + id + ";发送数据" + abc);
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
