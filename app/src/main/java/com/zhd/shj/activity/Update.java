package com.zhd.shj.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.zhd.AppHelper;
import com.zhd.commoncontrol.MainBigImageControl;
import com.zhd.entry.KZQupdate;
import com.zhd.gnssmanager.Com4Receive;
import com.zhd.serialportmanage.SerialPortManager;
import com.zhd.shj.AleartDialogHelper;
import com.zhd.shj.CallbackBundle;
import com.zhd.shj.CommonHelper;
import com.zhd.shj.CustomDialog;
import com.zhd.shj.OpenFileDialog;
import com.zhd.shj.R;
import com.zhd.shj.SysAdapter;
import com.zhd.shj.update.AutoUpdateHelper;
import com.zhd.utils.Utils;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Update extends BaseActivity implements View.OnClickListener {
    private Com4Receive mCom4Receive = null;

    private Button btnFinish, btnBack, appupdate, gjupdate,mbtnOK,mbtnCancle;
    static private int openfileDialogId = 0;

    private  TextView kzqversion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        iniView();
        mCom4Receive = mCom4Receive.getInstance(6, "/dev/ttyS4");
        mCom4Receive.startRecieve();
        mCom4Receive.getCom4GpsSerialPort().setReceiveMessageListener(
                mSerialPortDataListener);
    }

    SerialPortManager.IReceiveDataListener mSerialPortDataListener = new SerialPortManager.IReceiveDataListener() {
        @Override
        public void handleReceiveMessage1(int com, byte[] data) {
           if(data.length>4&&data[0]==(byte) (0xEB)&&data[1]==(byte) (0x90)&&data[2]==(byte) (0xEB)&&data[3]==(byte) (0x90))

           {
               getmessage=true; /* Log.e("返回字符串返回", new String(data));*/

               String abcd = "";
               for (int i = 0; i < data.length; i++) {
                   abcd +=  String.format("%02X", data[i]) + " ";
               }
               Log.e("返回字符串返回", abcd);

               if(data.length>15&&data[14]==(byte) (0x55))
                   getmessagecontent=true;
           }

        }

        @Override
        public void handleReceiveMessage(byte[] messge) {
            // TODO Auto-generated method stub

        }

    };

    private static final String NAMESPACE = "http://tempuri.org/";
    protected RelativeLayout mRlrlchoice = null;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.gjupdate:
                mRlrlchoice.setVisibility(View.INVISIBLE);

                CustomDialog.Builder builder = new CustomDialog.Builder(
                        Update.this);
                builder.setMessage(getString(R.string.upgrade_choice));
                builder.setTitle(R.string.alert_notice);
                builder.setPositiveButton(R.string.upgrade_local,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {

                                dialog.dismiss();
                                showDialog(openfileDialogId);
                            }
                        });

                builder.setNegativeButton(R.string.upgrade_network,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                                UpdateSumThread mUpdateThread = new UpdateSumThread();

                                mPool.execute(mUpdateThread);
                            }
                        });

                builder.create().show();



                break;
            case R.id.appupdate:
                if (downloadrl.getVisibility() == View.VISIBLE) return;
                UpdateThread mUpdateThread = new UpdateThread();

                mPool.execute(mUpdateThread);
                break;
            case R.id.btnFinish:
                if (downloadrl.getVisibility() == View.VISIBLE) return;

                finish();
                break;
            case R.id.btnBack:
                if (downloadrl.getVisibility() == View.VISIBLE) return;
                startActivity(new Intent(Update.this,
                        SystemSet.class));

                finish();
                break;
            case R.id.btnOK:
                progressBar = findViewById(R.id.progressBar);
                progressText = findViewById(R.id.progressText);
                btnCancelDownload = findViewById(R.id.progresscancle);
                btnCancelDownload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cancelDownload();
                    }
                });
                // Check if update is needed and show AlertDialog
                // 注册广播接收器
                BroadcastReceiver receiver = new DownloadReceiver();
                IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
                registerReceiver(receiver, filter);
                startDownloadkqz(mSysAdapter.getItem(mCheckedIndex));
                mRlrlchoice.setVisibility(View.INVISIBLE);
                Log.e("下载",mSysAdapter.getItem(mCheckedIndex));
                break;
            case R.id.btnCancel:
                mRlrlchoice.setVisibility(View.INVISIBLE);
                break;

        }
    }
    class UpdateSumThread extends Thread {
        @Override
        public void run() {
            Looper.prepare();
            String method = "checkkzqS";
            SoapObject rpc = new SoapObject(NAMESPACE, method);

            /*
             * PropertyInfo pi = new PropertyInfo(); pi.setName("requestFileName");
             * pi.setValue(apkName); pi.setType(apkName.getClass());
             * rpc.addProperty(pi);
             */

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                    SoapEnvelope.VER11);
            envelope.bodyOut = rpc;
            envelope.dotNet = true;
            envelope.setOutputSoapObject(rpc);

            HttpTransportSE ht = new HttpTransportSE("http://114.117.161.248:4502/SQL_Date/Equipment_link.asmx", 5000);
            ht.debug = true;
            try {
                ht.call(NAMESPACE + method, envelope);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }
            try {
                Object object = envelope.getResponse();
                Log.e("收到", "软件名称" + object.toString());
                String name=object.toString();
                if (name == "") {
                    AleartDialogHelper.alertBottomToast(
                            Update.this,
                            getString(R.string.networkone_error));
                } else {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            mRlrlchoice.setVisibility(View.VISIBLE);
                            mRlrlchoice.bringToFront();
                            String data_p[] = name.split(";");


                            mCheckedIndex = 0;
                            bindData(data_p, 0);
                        }
                    });

                }

            } catch (Exception e) {
                e.printStackTrace();
            }


            Looper.loop();


        }
    }
    class UpdateThread extends Thread {
        @Override
        public void run() {

            Looper.prepare();

            String method = "checkUpdatepdj";
            SoapObject rpc = new SoapObject(NAMESPACE, method);

            /*
             * PropertyInfo pi = new PropertyInfo(); pi.setName("requestFileName");
             * pi.setValue(apkName); pi.setType(apkName.getClass());
             * rpc.addProperty(pi);
             */
            rpc.addProperty("apkname", Update.this.getResources().getString(R.string.app_name_sum) + ".apk");

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                    SoapEnvelope.VER11);
            envelope.bodyOut = rpc;
            envelope.dotNet = true;
            envelope.setOutputSoapObject(rpc);

            HttpTransportSE ht = new HttpTransportSE("http://114.117.161.248:4502/SQL_Date/Equipment_link.asmx", 5000);
            ht.debug = true;
            try {
                ht.call(NAMESPACE + method, envelope);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }
            try {
                Object object = envelope.getResponse();
                Log.e("收到", "软件名称" + object.toString());
                if (object.toString().contains("apk") && !object.toString().equals(Update.this.getResources().getString(R.string.Version) + ".apk")) {

                    setDownloadview(object.toString());
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.yjzxbb), Toast.LENGTH_SHORT).show();

                }

            } catch (Exception e) {
                e.printStackTrace();
            }


            Looper.loop();

        }
    }
    private int mCheckedIndex = -1;
    private void bindData(String[] strs, int currentPosition) {
        if (strs != null && strs.length != 0) {
            mSysAdapter = new SysAdapter(this, Arrays.asList(strs));
            mSysAdapter.setCurrentPosition(currentPosition);
            mGridView.setAdapter(mSysAdapter);
        }
    }
    private GridView mGridView = null;
    protected SysAdapter mSysAdapter;
    private ExecutorService mPool = Executors.newFixedThreadPool(8);
    private ProgressDialog progressDialog;

    private void iniView() {
        kzqversion= (TextView) findViewById(R.id.kzqversion);
        kzqversion.setText(AppHelper. kzqversion);
        mbtnOK = (Button) findViewById(R.id.btnOK);
        mbtnOK.setOnClickListener(this);
        mbtnCancle = (Button) findViewById(R.id.btnCancel);
        mbtnCancle.setOnClickListener(this);
        mGridView = (GridView) findViewById(R.id.gvField);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onItemClick(AdapterView<?> listView, View view,
                                    int position, long rowId) {
                mSysAdapter.notifyDataSetChanged();
                GridView gv = (GridView) listView;
                mCheckedIndex = mSysAdapter.getCheckedIndex();

                if (mCheckedIndex != position) {
                    { // 定位到现在处于点击状态的radio
                        System.out.println("checkedIndex != arg2");

                        int childId = mCheckedIndex
                                - gv.getFirstVisiblePosition();
                        if (childId >= 0) { // 如果checked
                            // =true的radio在显示的窗口内，改变其状态为false
                            View item = gv.getChildAt(childId);
                            if (item != null) {
                                RadioButton rb = (RadioButton) ((LinearLayout) item)
                                        .getChildAt(2);
                                if (rb != null)
                                    rb.setChecked(false);
                            }
                        }

                        // 将当前点击的radio的checked变为true
                        RadioButton rb1 = (RadioButton) ((LinearLayout) view)
                                .getChildAt(2);
                        if (rb1 != null)
                            rb1.setChecked(true);
                        mCheckedIndex = position;

                        mSysAdapter.setCurrentPosition(position);
                        Log.e("设置", mCheckedIndex + "");

                    }
                }
            }
        });
        progressDialog = new ProgressDialog(Update.this);
        progressDialog.setTitle(Update.this.getString(R.string.Notice));
        progressDialog.setMessage(Update.this
                .getString(R.string.kzmess));
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(false);
        progressDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface arg0, int arg1, KeyEvent arg2) {
                // TODO Auto-generated method stub

                return false;
            }
        });
        mRlrlchoice = (RelativeLayout) findViewById(R.id.rlchoice);
        gjupdate = (Button) findViewById(R.id.gjupdate);
        gjupdate.setOnClickListener(this);
        appupdate = (Button) findViewById(R.id.appupdate);
        appupdate.setOnClickListener(this);
        btnFinish = (Button) findViewById(R.id.btnFinish);
        btnFinish.setOnClickListener(this);
        btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(this);
        downloadrl = (RelativeLayout) findViewById(R.id.downloadrl);
        downloadrl.setVisibility(View.GONE);
    }

    private Button btnCancelDownload;
    private RelativeLayout downloadrl;

    private void setDownloadview(String ABC) {


        progressBar = findViewById(R.id.progressBar);
        progressText = findViewById(R.id.progressText);
        btnCancelDownload = findViewById(R.id.progresscancle);
        btnCancelDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelDownload();
            }
        });
        // Check if update is needed and show AlertDialog

        showUpdateDialog(ABC);


        // 注册广播接收器
        BroadcastReceiver receiver = new DownloadReceiver();
        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(receiver, filter);
    }

    private ProgressBar progressBar;
    private TextView progressText;
    private long downloadId;

    private void showUpdateDialog(String abc) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.option))
                .setMessage(getString(R.string.updatemes))
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                downloadrl.setVisibility(View.VISIBLE);
                            }
                        });
                        startDownload(abc);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss(); // Close the activity if the user cancels the update

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                downloadrl.setVisibility(View.GONE);
                            }
                        });
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
    }

    private void startDownloadkqz(String abc) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // 如果未授权，向用户请求权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            // 如果已授权，开始下载
            String fileUrl = "http://114.117.161.248:4502/kzq/" + abc;
            String fileName = abc;
            xiazlx=1;
            Log.e("收到", "软件名称1111" + fileUrl);
            downloadFile(fileUrl, fileName);
        }
    }

    private  int xiazlx=0;


    private void startDownload(String abc) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // 如果未授权，向用户请求权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            // 如果已授权，开始下载
            String fileUrl = "http://114.117.161.248:4502/apk/" + abc;
            String fileName = abc;
            xiazlx=0;
            Log.e("收到", "软件名称1111" + fileUrl);
            downloadFile(fileUrl, fileName);
        }
    }

    private void downloadFile(String fileUrl, String fileName) {

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(fileUrl));
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        downloadId = downloadManager.enqueue(request);

        // 可选：使用 Handler 定期查询进度
        final Handler handler = new Handler();
        final Runnable progressRunnable = new Runnable() {
            @Override
            public void run() {
                updateProgressBar();
                handler.postDelayed(this, 1000); // 每秒更新一次进度
            }
        };
        handler.post(progressRunnable);
    }

    private void updateProgressBar() {
        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(downloadId);
        Cursor cursor = downloadManager.query(query);
        if (cursor.moveToFirst()) {
            @SuppressLint("Range") int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
            if (status == DownloadManager.STATUS_RUNNING) {
                @SuppressLint("Range") int bytesDownloaded = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                @SuppressLint("Range") int bytesTotal = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                int progress = (int) ((bytesDownloaded * 100L) / bytesTotal);

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        progressBar.setProgress(progress);
                        progressText.setText(progress + "%");
                    }
                });

            }
        }
        cursor.close();
    }

    private class DownloadReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                long receivedId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (receivedId == downloadId) {
                    Log.e("检查状态",receivedId+";"+downloadId);
                    // 检查下载状态
                    DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                    DownloadManager.Query query = new DownloadManager.Query();
                    query.setFilterById(downloadId);
                    Cursor cursor = downloadManager.query(query);
                    if (cursor.moveToFirst()) {
                        @SuppressLint("Range") int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                        Log.e("结果", status + "");
                        if (status == DownloadManager.STATUS_SUCCESSFUL) {
                            // 下载成功，执行安装操作
                            if(xiazlx==0)
                            installApk(context, downloadId);
                            else if(xiazlx==1)
                            {
                                 downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                                Uri uri = downloadManager.getUriForDownloadedFile(downloadId);

                                if (uri != null) {
                                    String filePath = getFilePathFromUri(context, uri);
                                    new updateAsytaskum().execute(filePath);
                                } else {
                                    String errorMessage = "下载失败，原因：" ;

                                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                                    // 隐藏进度条并解除屏幕锁定
                                    downloadrl.setVisibility(View.GONE);
                                }
                            }

                        } else {
                            // 下载失败，显示错误信息
                            @SuppressLint("Range") int reason = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_REASON));
                            String errorMessage = "下载失败，原因：" + getDownloadErrorMessage(reason);
                            Log.e("结果", errorMessage + "");
                            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                            // 隐藏进度条并解除屏幕锁定
                            downloadrl.setVisibility(View.GONE);

                        }
                    }
                    cursor.close();
                }
            }
        }
        private String getFilePathFromUri(Context context, Uri uri) {
            String filePath = null;
            String[] projection = {MediaStore.Downloads.DATA};

            Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex(MediaStore.Downloads.DATA);
                filePath = cursor.getString(columnIndex);
                cursor.close();
            }

            return filePath;
        }
        private void installApk(Context context, long downloadId) {
            DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            Uri uri = downloadManager.getUriForDownloadedFile(downloadId);
            if (uri != null) {
                Intent installIntent = new Intent(Intent.ACTION_VIEW);
                installIntent.setDataAndType(uri, "application/vnd.android.package-archive");
                installIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                installIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(installIntent);
            } else {
                Toast.makeText(context, getString(R.string.updatefail), Toast.LENGTH_SHORT).show();
            }
            // 隐藏进度条并解除屏幕锁定
            downloadrl.setVisibility(View.GONE);


        }

        private String getDownloadErrorMessage(int reason) {
            String errorMessage;
            switch (reason) {
                case DownloadManager.ERROR_CANNOT_RESUME:
                    errorMessage = getString(R.string.download1);
                    break;
                case DownloadManager.ERROR_DEVICE_NOT_FOUND:
                    errorMessage = getString(R.string.download2);
                    break;
                case DownloadManager.ERROR_FILE_ALREADY_EXISTS:
                    errorMessage = getString(R.string.download3);
                    break;
                // 添加其他错误情况的处理...
                default:
                    errorMessage = getString(R.string.download4);
                    break;
            }
            return errorMessage;
        }
    }

    @Override
    public void onBackPressed() {
        // 取消下载并关闭Activity
        cancelDownload();
        super.onBackPressed();
    }

    private void cancelDownload() {
        downloadrl.setVisibility(View.GONE);
        if (downloadId != -1) {
            DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            downloadManager.remove(downloadId);
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        OpenFileDialog.spath = "/";
        if (id == openfileDialogId) {

            Map<String, Integer> images = new HashMap<String, Integer>();
            // 下面几句设置各文件类型的图标， 需要你先把图标添加到资源文件夹
            images.put(OpenFileDialog.sRoot, R.drawable.filedialog_root); // 根目录图标
            images.put(OpenFileDialog.sParent,
                    R.drawable.filedialog_folder_up); // 返回上一层的图标
            images.put(OpenFileDialog.sFolder, R.drawable.filedialog_folder); // 文件夹图标
            images.put("app", R.drawable.filedialog_file); // 文件图标
            images.put(OpenFileDialog.sEmpty, R.drawable.filedialog_root);
            Dialog dialog = OpenFileDialog.createDialog(id, this, "打开文件",
                    new CallbackBundle() {
                        @Override
                        public void callback(Bundle bundle) {
                            String filepath = bundle.getString("path");

                            Log.i("Update", filepath);
                            AlertDialog.Builder builder = new AlertDialog.Builder(Update.this);
                            builder.setTitle(getString(R.string.option))
                                    .setMessage(getString(R.string.upgrade_sure))
                                    .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            new updateAsytaskum().execute(filepath);
                                            dismissDialog(openfileDialogId);
                                            dialog.dismiss();
                                        }
                                    })
                                    .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss(); // Close the activity if the user cancels the update

                                        }
                                    });

                            AlertDialog dialog = builder.create();
                            dialog.setCancelable(false);
                            dialog.show();


                        }
                    }, ".app;", "", images);
            return dialog;
        }
        return null;
    }
    public  Boolean getmessage=false;
    public  Boolean getmessagecontent=false;

    Handler mHandler = new Handler() {
        @SuppressLint("HandlerLeak")
        public void handleMessage(Message msg) {
            progressDialog.setProgress(msg.what);

        }
    };

    public class updateAsytaskum extends AsyncTask<String, Void, Boolean> {

        @Override
        protected void onPreExecute() {

            super.onPreExecute();

            progressDialog.setProgress(0);

            progressDialog.show();

        }

        @Override
        protected Boolean doInBackground(String... path) {

            boolean result = false;
            byte[] be = CommonHelper.file2BetyArray(path[0]);
            Message abc = new Message();
            abc.what = (int) (0);
            mHandler.sendMessage(abc);
            try {
                KZQupdate qqqq = new KZQupdate();
                qqqq.content = new byte[]{(byte) (0x0F), (byte) (0x00), (byte) (0x00)};
                byte[] commandbyte = qqqq.setCommand();
                int c=0;
                getmessage=false;
                while (!getmessage) {
                    c++;
                    Log.v("监听", "等待");
                    mCom4Receive.sendByte(commandbyte);
                    Thread.sleep(200);
                    if (c > 5)
                        return false;
                }


                qqqq.content = new byte[]{(byte) (0x01), (byte) (0x00), (byte) (0x00)};
                commandbyte = qqqq.setCommand();
                c=0;
                getmessage=false;
                while (!getmessage) {
                    c++;
                    Log.v("监听", "等待");
                    mCom4Receive.sendByte(commandbyte);
                    Thread.sleep(200);
                    if (c > 5)
                        return false;
                }

                qqqq.content = new byte[]{(byte) (0x0A), (byte) (0x00), (byte) (0x00)};
                commandbyte = qqqq.setCommand();
                c=0;
                getmessage=false;
                while (!getmessage) {
                    c++;
                    Log.v("监听", "等待");
                    mCom4Receive.sendByte(commandbyte);
                    Thread.sleep(200);
                    if (c > 5)
                        return false;
                }

                qqqq.content = new byte[]{(byte) (0x20), (byte) (0x01), (byte) (0x00), (byte) (0x55)};
                commandbyte = qqqq.setCommand();
                c=0;
                getmessage=false;
                while (!getmessage) {
                    c++;
                    Log.v("监听", "等待");
                    mCom4Receive.sendByte(commandbyte);
                    Thread.sleep(200);
                    if (c > 5)
                        return false;
                }

                qqqq.content = new byte[]{(byte) (0x21), (byte) (0x03), (byte) (0x00), (byte) (0x55), (byte) (0x00), (byte) (0x00)};
                commandbyte = qqqq.setCommand();
                c=0;
                getmessage=false;
                while (!getmessage) {
                    c++;
                    Log.v("监听", "等待");
                    mCom4Receive.sendByte(commandbyte);
                    Thread.sleep(200);
                    if (c > 5)
                        return false;
                }

                qqqq.content = new byte[]{(byte) (0x21), (byte) (0x03), (byte) (0x00), (byte) (0x55), (byte) (0x01), (byte) (0x00)};
                commandbyte = qqqq.setCommand();
                c=0;
                getmessage=false;
                while (!getmessage) {
                    c++;
                    Log.v("监听", "等待");
                    mCom4Receive.sendByte(commandbyte);
                    Thread.sleep(200);
                    if (c > 5)
                        return false;
                }

                byte[]  temp=  Utils.getCRC(path[0]);
                int zsjs;
                if(be.length%512==0)
                 zsjs=be.length/512;
               else
                    zsjs=be.length/512+1;
                byte[]  zs=  Utils.Inttobyte(zsjs);
                byte[]   beda=Utils.Longtobyte(be.length);
                qqqq.content = new byte[]{(byte) (0x0B), (byte) (0x0D), (byte) (0x00), zs[0],zs[1],temp[0],temp[1], (byte) (0x00), (byte) (0x00)
                        , (byte) (0x08), (byte) (0x08),beda[0],beda[1],beda[2],beda[3], (byte) (0x01)};

                commandbyte = qqqq.setCommand();
                getmessagecontent=false;
                c=0;
                while (!getmessagecontent) {
                    c++;
                    Log.v("返回字符串", "头");
                    mCom4Receive.sendByte(commandbyte);
                    Thread.sleep(200);
                    if (c > 10)
                        return false;
                }
                Thread.sleep(1000);
                for(int i=0;i<zsjs;i++) {
                    abc = new Message();
                    abc.what = (int) (((float) i) /zsjs * 100);
                    Log.v("返回字符串", "内容"+ abc.what);
                    mHandler.sendMessage(abc);
                    qqqq.content = Utils.fzcontent(be, i);
                    commandbyte = qqqq.setCommand();
                    getmessagecontent=false;
                    c=0;
                    while (!getmessagecontent) {
                        c++;
                        Log.v("返回字符串", "内容");
                        mCom4Receive.sendByte(commandbyte);
                        Thread.sleep(200);
                        if (c > 10)
                            return false;
                    }
                }





            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {

            super.onPostExecute(result);
            progressDialog.dismiss();
            if (result) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Update.this);
                builder.setTitle(getString(R.string.option))
                        .setMessage(getString(R.string.Successful_upgrade_1))
                        .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                dialog.dismiss();
                            }
                        })
                       ;

                AlertDialog dialog = builder.create();
                dialog.setCancelable(false);
                dialog.show();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(Update.this);
                builder.setTitle(getString(R.string.option))
                        .setMessage(getString(R.string.fail_upgrade))
                        .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                dialog.dismiss();
                            }
                        })
                       ;

                AlertDialog dialog = builder.create();
                dialog.setCancelable(false);
                dialog.show();
            }




        }
    }

}
