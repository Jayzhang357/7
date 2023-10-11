package com.zhd.shj.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.zhd.AppHelper;
import com.zhd.shj.BigConfigxml;
import com.zhd.shj.R;
import com.zhd.shj.SerialPortFinderHelper;

import java.util.Locale;


public class Recover extends BaseActivity {

    private Button btnFinish, btnBack,rest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recover);
        iniView();
    }

    private Spinner mspCompath;
    private BigConfigxml mConfigxml;

    private void iniView() {
        mConfigxml = BigConfigxml
                .getInstantce(Recover.this);
        AppHelper.Language=mConfigxml.Language;
        mspCompath = (Spinner) findViewById(R.id.spinner1);
        SerialPortFinderHelper abcd = new SerialPortFinderHelper();
        String[] pathlist = {"中文(简体)", "English"};
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this,
                R.layout.myspinner, pathlist);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mspCompath.setAdapter(adapter1);
        mspCompath.setSelection(mConfigxml.Language);
        btnFinish = (Button) findViewById(R.id.btnFinish);

        mspCompath.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if(  AppHelper.Language==position)return;;
                // This is called when an item is selected from the Spinner
                String selectedItem = parentView.getItemAtPosition(position).toString();
                AlertDialog.Builder builder = new AlertDialog.Builder(Recover.this);
                builder.setTitle(getString(R.string.option))
                        .setMessage(getString(R.string.mesg12)+selectedItem+","+getString(R.string.mesg121))
                        .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                mConfigxml.Language=position;
                                mConfigxml.saveConfigXml();

                                dialog.dismiss();

                              /*  Configuration config = new Configuration(getResources().getConfiguration());
                                config.setLocale(Locale.US); // newLocale 是你想要切换的语言 Locale 对象
                                getResources().updateConfiguration(config, getResources().getDisplayMetrics());*/
                                Intent intent = null;
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                                    intent = new Intent(Recover.this, MainActivity.class);
                                }
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();

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

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }


        });
        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });
        btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Recover.this,
                        SystemSet.class));

                finish();
            }
        });
        rest= (Button) findViewById(R.id.rest);
        rest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}
