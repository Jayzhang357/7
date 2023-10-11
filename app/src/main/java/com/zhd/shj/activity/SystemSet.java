package com.zhd.shj.activity;

import android.app.Activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.zhd.commoncontrol.MainBigImageControl;
import com.zhd.shj.R;
import com.zhd.shj.boardcast.CommonBroadCast;
import com.zhd.shj.entity.CommonEnum;
import com.zhd.shj.entity.JobInfo;


public class SystemSet extends BaseActivity {


    private Button btnBack;
    private MainBigImageControl btncssz,btnsbsms,btncffs,btnsbzc,btncsgx,btnccsz;
    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system);
        iniView();
    }

    private void iniView() {
        btncssz = (MainBigImageControl) findViewById(R.id.btncssz);
        btncssz.setImageResource(R.drawable.ic_cssz, getString(R.string.cssz));
        btncssz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SystemSet.this,
                        CanShuSet.class));
                finish();
            }
        });
        btnsbsms = (MainBigImageControl) findViewById(R.id.btnsbsms);
        btnsbsms.setImageResource(R.drawable.ic_sysm, getString(R.string.sysm));
        btnsbsms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        btncffs = (MainBigImageControl) findViewById(R.id.btncffs);
        btncffs.setImageResource(R.drawable.ic_stat, getString(R.string.cffs));
        btncffs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(SystemSet.this
                        ,
                        Difference.class);
                mIntent.putExtra("ip", 0);

                startActivity(mIntent);

                finish();
            }
        });
        btnsbzc = (MainBigImageControl) findViewById(R.id.btnsbzc);
        btnsbzc.setImageResource(R.drawable.ic_sbzc, getString(R.string.sbzc));
        btnsbzc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        btncsgx = (MainBigImageControl) findViewById(R.id.btncsgx);
        btncsgx.setImageResource(R.drawable.ic_csgx, getString(R.string.csgx));
        btncsgx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SystemSet.this,
                        Update.class));
                finish();
            }
        });
        btnccsz = (MainBigImageControl) findViewById(R.id.btnccsz);
        btnccsz.setImageResource(R.drawable.ic_hfcc, getString(R.string.hfcc));
        btnccsz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SystemSet.this,
                        Recover.class));
                finish();
            }
        });
        btnBack = (Button) findViewById(R.id.btnFinish);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}
