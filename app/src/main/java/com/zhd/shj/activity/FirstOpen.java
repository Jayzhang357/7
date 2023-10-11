package com.zhd.shj.activity;
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Process;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;



public class FirstOpen extends BaseActivity {
    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String[] PERMISSIONS_STORAGE = {
                Manifest.permission.INTERNET,


                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE

        };
        int REQUEST_EXTERNAL_STORAGE = 1;
        int permission = ActivityCompat.checkSelfPermission(FirstOpen.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int permission1 = ActivityCompat.checkSelfPermission(FirstOpen.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permission2 = ActivityCompat.checkSelfPermission(FirstOpen.this, Manifest.permission.READ_PHONE_STATE);
        int permission3 = ActivityCompat.checkSelfPermission(FirstOpen.this, Manifest.permission.INTERNET);
      //  int permission4 = ActivityCompat.checkSelfPermission(FirstOpen.this, Manifest.permission.FOREGROUND_SERVICE);
        if (permission != PackageManager.PERMISSION_GRANTED || permission1 != PackageManager.PERMISSION_GRANTED || permission2 != PackageManager.PERMISSION_GRANTED || permission3 != PackageManager.PERMISSION_GRANTED ) {
            // We don't have permission so prompt the user

            ActivityCompat.requestPermissions(
                    FirstOpen.this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
        else
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                startActivity(new Intent(FirstOpen.this,
                        MainActivity.class));
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 &&grantResults[0] ==PackageManager.PERMISSION_GRANTED && grantResults[1] ==PackageManager.PERMISSION_GRANTED&&grantResults[2] ==PackageManager.PERMISSION_GRANTED) {
                    Log.e("打开","打开");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        startActivity(new Intent(FirstOpen.this,
                                MainActivity.class));
                    }
                } else {
                    // Permission Denied 权限被拒绝
                    Log.e("打开","关闭");
                    Intent intenn = new Intent();
                    intenn.setAction("android.intent.action.MAIN");
                    intenn.addCategory("android.intent.category.HOME");
                    startActivity(intenn);
                    Process.killProcess(Process.myPid());
                }

                break;
            default:
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }
    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    protected void onPause() {
        super.onPause();
        /*isStart = false;
        RtcmSDKManager.getInstance().stop(QXWZ_SDK_CAP_ID_NOSR);*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /*  RtcmSDKManager.getInstance().cleanup();*/
    }

}
