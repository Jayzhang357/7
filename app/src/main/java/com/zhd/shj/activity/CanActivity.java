package com.zhd.shj.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.android.manager.IMcuManager;
import com.android.manager.IMcuManagerListener;
import com.zhd.bean.CanData;
import com.zhd.shj.R;
import com.zhd.utils.FileUtils;


import java.util.ArrayList;

public class CanActivity extends Activity implements View.OnClickListener {

    private static final String ChannelKey = "Channel";
    private static final String IdTypeKey = "IdType";
    private static final String FrameTypeKey = "FrameType";
    private SharedPreferences preferences;

    private Spinner spChannel;
    private Spinner spBaud;
    private Spinner spidType;
    private Spinner spFrameType;

    private EditText etMask;
    private EditText etId;
    private EditText etCount;
    private EditText etTime;
    private EditText etData;

    private CheckBox cbLoop;
    private CheckBox cbSave;

    private Button btSend;
    private Button btStop;
    private Button btFilter;
    private Button btCancelFilter;
    private ImageButton btClear;

    private ListView lvCan;
    ArrayAdapter<String> canDataAdapter;
    private int revCount = 0;
    private boolean isResume = false;

    private int sendCount;
    private int delay = 1000;
    private final int MSG_SEND_CAN_DATA = 0;
    private final int MSG_CAN_DATA = 1;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SEND_CAN_DATA:
                    if (sendCount <= 0) {
                        btSend.setVisibility(View.VISIBLE);
                        btStop.setVisibility(View.GONE);
                        btFilter.setEnabled(true);
                        btCancelFilter.setEnabled(true);

                        spChannel.setEnabled(true);
                        spBaud.setEnabled(true);
                        etMask.setEnabled(true);
                        etCount.setEnabled(true);
                        etTime.setEnabled(true);

                        spFrameType.setEnabled(true);
                        spidType.setEnabled(true);
                        etId.setEnabled(true);
                        etData.setEnabled(true);
                        cbLoop.setEnabled(true);
                    } else {
                        if (sendCanData()) {
                            sendCount--;
                        } else {
                            sendCount = 0;
                        }
                        sendEmptyMessageDelayed(MSG_SEND_CAN_DATA, delay);
                    }
                    break;
                case MSG_CAN_DATA:
                    CanData data = (CanData) msg.obj;
                    StringBuilder stringBuilder = new StringBuilder(String.format("%05d", ++revCount) + " [通道" + data.channel + "] " + (data.id_extend ? "[扩展帧]" : "[标准帧]") + " " + (data.frame_remote ? "[远程帧]" : "[数据帧]") + " id:[0x" + String.format("%08X", data.id) + "] " + "size:" + data.data.length + ", data: ");
                    for (int i = 0; i < data.data.length; i++) {
                        stringBuilder.append(String.format("%02X ", data.data[i]));
                    }

                    if (canDataAdapter.getCount() > 5000) {
                        canDataAdapter.clear();
                    }
                    canDataAdapter.insert(stringBuilder.toString(), 0);
                    if (cbSave.isChecked()) {
                        FileUtils.wirteStringToFile(stringBuilder.toString(), 0);
                    }
                    break;
            }
        }
    };

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
            Message msg = mHandler.obtainMessage();
            msg.what = MSG_CAN_DATA;
            msg.obj = new CanData(channel, id, id_extend, frame_remote, data);
            mHandler.sendMessage(msg);
            Log.e("can数据111",data.length+""+abc );
//            if(isResume){
//                final StringBuilder stringBuilder = new StringBuilder(String.format("%05d", ++revCount) + " [通道" + channel + "] " + (id_extend ? "[扩展帧]" : "[标准帧]") + " " + (frame_remote ? "[远程帧]" : "[数据帧]") + " id:[0x" + String.format("%08X", id) + "] " + "size:" + data.length + ", data: ");
//                for (int i = 0; i < data.length; i++) {
//                    stringBuilder.append(String.format("%02X ", data[i]));
//                }
//
//                if (canDataAdapter.getCount() > 5000) {
//                    canDataAdapter.clear();
//                }
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        canDataAdapter.insert(stringBuilder.toString(), 0);
//                    }
//                });
//            }
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
                    Log.d("mzj","onService");
                    iMcuManager.removeMcuManagerListener(iMcuManagerListener);
                    iMcuManager = null;
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_can);

        preferences = getSharedPreferences("config", Context.MODE_PRIVATE);

        canDataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<String>());
        lvCan = findViewById(R.id.tv_can);
        lvCan.setAdapter(canDataAdapter);

        etMask = findViewById(R.id.et_mask);
        etId = findViewById(R.id.et_id);
        etCount = findViewById(R.id.et_count);
        etTime = findViewById(R.id.et_time);
        etData = findViewById(R.id.et_data);

        cbLoop = findViewById(R.id.cb_loop);
        cbSave = findViewById(R.id.cb_save);

        btCancelFilter = findViewById(R.id.btn_cancel_filter);
        btCancelFilter.setOnClickListener(this);
        btFilter = findViewById(R.id.btn_filter);
        btFilter.setOnClickListener(this);
        btSend = findViewById(R.id.btn_send);
        btSend.setOnClickListener(this);
        btStop = findViewById(R.id.btn_stop);
        btStop.setOnClickListener(this);
        btClear = findViewById(R.id.btn_clear);
        btClear.setOnClickListener(this);


        String[] bauds = new String[]{"50K", "100K", "125K", "250K", "500K", "1M"};
        ArrayAdapter<String> baudAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, bauds);
        spBaud = findViewById(R.id.sp_baud);
        spBaud.setAdapter(baudAdapter);
        spBaud.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int channel = spChannel.getSelectedItemPosition() + 1;
                if (iMcuManager != null) {
                    try {
                        Log.d("mzj","onItemSelected");
                        if (iMcuManager.setCanBaud((byte) channel, (byte) position)) {
                            Toast.makeText(CanActivity.this, "设置成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(CanActivity.this, "设置失败", Toast.LENGTH_SHORT).show();
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

        int channel = preferences.getInt(ChannelKey, 0);
        String[] channels = new String[]{"CAN1", "CAN2"};
        ArrayAdapter<String> channelAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, channels);
        spChannel = findViewById(R.id.sp_channel);
        spChannel.setAdapter(channelAdapter);
        spChannel.setSelection(channel);
        spChannel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                preferences.edit().putInt(ChannelKey, position).apply();
                int channel = spChannel.getSelectedItemPosition() + 1;
                try {
                    if(iMcuManager != null){
                        int baud = iMcuManager.getCanBaud((byte) channel);
                        spBaud.setSelection(baud, true);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        int idType = preferences.getInt(IdTypeKey, 0);
        String[] types = new String[]{"标准", "扩展"};
        ArrayAdapter<String> idTypeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, types);
        spidType = findViewById(R.id.sp_idtype);
        spidType.setAdapter(idTypeAdapter);
        spidType.setSelection(idType);
        spidType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                preferences.edit().putInt(IdTypeKey, position).apply();
                if (position == 0) {
                    etMask.setText("000007FF");
                } else {
                    etMask.setText("1FFFFFFF");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        int frameType = preferences.getInt(FrameTypeKey, 0);
        String[] frameTypes = new String[]{"数据帧", "远程帧"};
        ArrayAdapter<String> frameTypeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, frameTypes);
        spFrameType = findViewById(R.id.sp_frametype);
        spFrameType.setAdapter(frameTypeAdapter);
        spFrameType.setSelection(frameType);
        spFrameType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                preferences.edit().putInt(FrameTypeKey, position).apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        Button   btn_close=(Button)findViewById(R.id.btn_close);

                btn_close.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        System.exit(0);

                    }
                });
        bindService();
        Log.d("mzj","bindService");
    }

    @Override
    protected void onResume() {
        super.onResume();
        isResume = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("can数据111","暂停");
        isResume = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("can数据111", "关闭");
        unbindService();

        if (iMcuManager != null) {
            try {
                iMcuManager.removeMcuManagerListener(iMcuManagerListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private void bindService() {
        Intent intent = new Intent();
        ComponentName componentName = new ComponentName("com.android.service", "com.android.service.McuService");
        intent.setComponent(componentName);
        intent.setAction("com.android.service.McuService");
        intent.setType(getPackageName());
        startService(intent);
        Log.d("mzj","bindService1111");
        bindService(intent, conn, BIND_AUTO_CREATE);
        abc=System.currentTimeMillis()+"";
    }
   String abc="";
    private void unbindService() {
        unbindService(conn);
        Log.d("mzj","关闭");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_filter:
                int channel = spChannel.getSelectedItemPosition() + 1;
                long id = Long.valueOf(etId.getText().toString(), 16);
                long mask = Long.valueOf(etMask.getText().toString(), 16);
                try {
                    Log.d("mzj","btn_filter");
                    if (iMcuManager.setCanFilter((byte) channel, id, mask)) {
                        Toast.makeText(this, "设置成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "设置失败", Toast.LENGTH_SHORT).show();
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_cancel_filter:
                channel = spChannel.getSelectedItemPosition() + 1;
                try {
                    Log.d("mzj","btn_cancel_filter");
                    if (iMcuManager.cancelCanFilter((byte) channel)) {
                        Toast.makeText(this, "取消CAN ID过滤成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "设置失败", Toast.LENGTH_SHORT).show();
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_send:
                if (sendCanData() && cbLoop.isChecked()) {
                    sendCount = Integer.valueOf(etCount.getText().toString()) - 1;
                    if (sendCount > 0) {
                        btSend.setVisibility(View.GONE);
                        btStop.setVisibility(View.VISIBLE);

                        btFilter.setEnabled(false);
                        btCancelFilter.setEnabled(false);

                        spChannel.setEnabled(false);
                        spBaud.setEnabled(false);
                        etMask.setEnabled(false);
                        etCount.setEnabled(false);
                        etTime.setEnabled(false);

                        spFrameType.setEnabled(false);
                        spidType.setEnabled(false);
                        etId.setEnabled(false);
                        etData.setEnabled(false);
                        cbLoop.setEnabled(false);

                        delay = Integer.valueOf(etTime.getText().toString());
                        mHandler.sendEmptyMessageDelayed(MSG_SEND_CAN_DATA, delay);
                    }
                }
                break;
            case R.id.btn_stop:
                sendCount = 0;
                break;
            case R.id.btn_clear:
                canDataAdapter.clear();
                break;
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
                Toast.makeText(this, "发送失败", Toast.LENGTH_SHORT).show();
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
