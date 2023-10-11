package com.zhd.shj.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.zhd.commoncontrol.MainBigImageControl;
import com.zhd.shj.CommonHelper;
import com.zhd.shj.R;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Update extends BaseActivity {

    private Button btnFinish, btnBack, appupdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        iniView();
    }

    private static final String NAMESPACE = "http://tempuri.org/";

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

    private ExecutorService mPool = Executors.newFixedThreadPool(8);

    private void iniView() {
        appupdate = (Button) findViewById(R.id.appupdate);
        appupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(downloadrl.getVisibility()==View.VISIBLE)return;
                UpdateThread mUpdateThread = new UpdateThread();

                mPool.execute(mUpdateThread);
            }
        });
        btnFinish = (Button) findViewById(R.id.btnFinish);
        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(downloadrl.getVisibility()==View.VISIBLE)return;

                finish();
            }
        });
        btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(downloadrl.getVisibility()==View.VISIBLE)return;
                startActivity(new Intent(Update.this,
                        SystemSet.class));

                finish();
            }
        });
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

    private void startDownload(String abc) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // 如果未授权，向用户请求权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            // 如果已授权，开始下载
            String fileUrl = "http://114.117.161.248:4502/apk/" + abc;
            String fileName = abc;
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
                            installApk(context, downloadId);
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
                    errorMessage =getString(R.string.download2);
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

}
