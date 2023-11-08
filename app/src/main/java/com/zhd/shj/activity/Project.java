package com.zhd.shj.activity;




import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;


import androidx.core.content.FileProvider;

import com.zhd.AppHelper;
import com.zhd.shj.AleartDialogHelper;
import com.zhd.shj.CallbackBundle;
import com.zhd.shj.CommonHelper;
import com.zhd.shj.CustomDialog;
import com.zhd.shj.OpenFileDialog;
import com.zhd.shj.R;
import com.zhd.shj.boardcast.CommonBroadCast;
import com.zhd.shj.business.Job;
import com.zhd.shj.entity.CommonEnum;
import com.zhd.shj.entity.JobInfo;
import com.zhd.shj.sqlite.DataAdapter;
import com.zhd.shj.sqlite.MemberInfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Project extends BaseActivity {

    private Job mJob = new Job(this);
    private ListView projectgd;
    private Button ic_back,newbt,deletebt,deleteRecord,selectbt, selectdelete, selectrecovery,modifybt,daoru,btnFinish;
    private List<Object> list;
    private DataAdapter dataAdapter;
    private String select = null;
    private int list_delete;
    private Dialog dialog;
    private EditText mEditText;
    private ImageView imageview,isearch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.project);
        iniView();
    }
    public void setDilog(String abc) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Project.this);

// 设置对话框的标题和消息
        builder.setTitle(getString(R.string.option));
        builder.setMessage(Html.fromHtml(abc));

// 设置对话框的按钮及其点击事件
        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // 处理确定按钮的点击事件
                dialog.dismiss();
            }
        });


// 创建并显示对话框
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void  iniView(){
        bindDate();
        btnFinish = (Button) findViewById(R.id.btnFinish);
        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    startActivity(new Intent(Project.this,
                            MainActivity.class));
                }
                finish();
            }
        });
        isearch = (ImageView) findViewById(R.id.isearch);

        //设置删除图片的点击事件
        isearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mEditText.getText().length()==0)return;
                for(int i=0;i<list.size();i++)
                {
                    MemberInfo info = (MemberInfo) list.get(i);
                    Log.e("qqqq",mEditText.getText()+"www"+i);
                    if((info.name.contains(mEditText.getText() + "")))
                    {

                        list_delete = i;
                        dataAdapter.setSelectedItem(i);

                        dataAdapter.notifyDataSetChanged();


                        select = info.name;
                        return;
                    }

                }

                setDilog(getString(R.string.findnone));

            }
        });
        mEditText = (EditText) findViewById(R.id.edittext);

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(mEditText, InputMethodManager.SHOW_IMPLICIT);

        mEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
        //EditText添加监听
        mEditText.addTextChangedListener(new TextWatcher() {

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }//文本改变之前执行

            @Override
            //文本改变的时候执行
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //如果长度为0
                if (s.length() == 0) {
                    //隐藏“删除”图片
                    imageview.setVisibility(View.GONE);
                } else {//长度不为0
                    //显示“删除图片”
                    imageview.setVisibility(View.VISIBLE);
                    //显示ListView

                }
            }

            public void afterTextChanged(Editable s) {
            }//文本改变之后执行
        });
        imageview = (ImageView) findViewById(R.id.imageview);

        //设置删除图片的点击事件
        imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditText.setText("");

            }
        });
        daoru= (Button) findViewById(R.id.daoru);
        daoru.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Map<String, Integer> images = new HashMap<String, Integer>();
                // 下面几句设置各文件类型的图标， 需要你先把图标添加到资源文件夹
                images.put(OpenFileDialog.sRoot, R.drawable.filedialog_root); // 根目录图标
                images.put(OpenFileDialog.sParent, R.drawable.filedialog_folder_up); // 返回上一层的图标
                images.put(OpenFileDialog.sFolder, R.drawable.filedialog_folder); // 文件夹图标
                images.put("bin", R.drawable.filedialog_file); // 文件图标
                images.put(OpenFileDialog.sEmpty, R.drawable.filedialog_root);
                 dialog = OpenFileDialog.createDialog(0, Project.this, getString(R.string.openfile),
                        new CallbackBundle() {
                            @SuppressLint("SuspiciousIndentation")
                            @Override
                            public void callback(Bundle bundle) {
                                String filepath = bundle.getString("path");
                                File file = new File(filepath);
                                Log.i("Update", filepath);
                                // 创建一个 Uri 对象
                                Uri uri = FileProvider.getUriForFile(Project.this, "com.example.myapp.fileprovider", file);

// 创建一个 Intent
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setDataAndType(uri, "text/plain");
                                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

// 在被打开的活动中读取文件内容
                                try {

                                    BufferedReader reader = new BufferedReader(new FileReader(file));
                                    StringBuilder sb = new StringBuilder();
                                    String line;
                                    int iq=0;
                                    while ((line = reader.readLine()) != null) {
                                  /*      sb.append(line);
                                        sb.append("\n");*/
                                        if(line.contains("UTM84"))
                                        Log.e("qq",line);
                                        if(line.contains("AcDbFace")) {
                                            iq=1;
                                        }

                                    }
                                    String fileContent = sb.toString();
                                    reader.close();
                                    // 在这里使用 fileContent
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                dialog.dismiss();
                            }
                        }, ".dxf;", "", images);
                dialog.show();
            }

        });
        modifybt = (Button) findViewById(R.id.modifybt);
        modifybt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (select == null) {
                    Toast.makeText(Project.this, getString(R.string.mes_6), Toast.LENGTH_SHORT).show();
                } else {

                    JobInfo mParamJobInfo = new JobInfo();
                    String[] args = {select};
                    String condition = "JobName = ?";

                    List<Object> objList = mJob.selectByCondition(condition, args, "ID");

                    for (Object object : objList) {
                        JobInfo jobInfo = (JobInfo) object;
                        // 设置AB类型和作业名称


                        Intent intent = new Intent(Project.this, ModifyActivity.class);

                        intent.putExtra("ecuprj_modify", jobInfo);

                        startActivity(intent);
                        finish();
                    }

                    finish();
                }

            }
        });
        selectdelete = (Button) findViewById(R.id.selectdelete);
        selectdelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CustomDialog.Builder builder = new CustomDialog.Builder(
                        Project.this);
                builder.setMessage(getString(R.string.if_reset_database));
                builder.setTitle(R.string.alert_notice);
                builder.setPositiveButton(R.string.confirm,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();

                                String result = AppHelper.deleteDatabse() ? getResources().getString(R.string.success) : getResources().getString(R.string.Faield);
                                AleartDialogHelper.alertToast(Project.this, result);
                                bindDate();
                            }
                        });
                builder.setNegativeButton(R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                            }
                        });

                builder.create().show();

            }
        });
        selectrecovery = (Button) findViewById(R.id.selectrecovery);
        selectrecovery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog.Builder builder = new CustomDialog.Builder(
                        Project.this);
                builder.setMessage(getString(R.string.if_recover_database));
                builder.setTitle(R.string.alert_notice);
                builder.setPositiveButton(R.string.confirm,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();

                                String result = AppHelper.copybackFile() ? getResources().getString(R.string.success) : getResources().getString(R.string.Faield);
                                AleartDialogHelper.alertToast(Project.this, result);
                                bindDate();
                            }
                        });
                builder.setNegativeButton(R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                            }
                        });

                builder.create().show();
            }
        });
        selectbt = (Button) findViewById(R.id.selectbt);
        selectbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (select == null) {
                    Toast.makeText(Project.this,  getString(R.string.mes_6), Toast.LENGTH_SHORT).show();
                } else {

                    JobInfo mParamJobInfo = new JobInfo();
                    String[] args = {select};
                    String condition = "JobName = ?";

                    List<Object> objList = mJob.selectByCondition(condition, args, "ID");

                    for (Object object : objList) {
                        JobInfo jobInfo = (JobInfo) object;
                        JobInfo abc = mJob.getSelectedJob();
                        if (abc != null) {
                            abc.IsSelected = CommonEnum.DbBoolean.False.getValue();
                            mJob.update(abc);
                        }
                        // 设置AB类型和作业名称
                        //    mJob.   updateSelected();
                        jobInfo.IsSelected = CommonEnum.DbBoolean.True.getValue();
                        mJob.update(jobInfo);
                        Log.e("作业名称",jobInfo.JobName);
                        //     mJob.update(jobInfo);
                        Intent intent = new Intent(CommonBroadCast.BC_JOB_LOAD);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("PAR", jobInfo);
                        intent.putExtras(bundle);

                        // 发送广播
                        sendBroadcast(intent);


                        finish();
                    }

                    finish();
                }


            }
        });

        deleteRecord = (Button) findViewById(R.id.deleteRecord);
        deleteRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (select != null) {

                    CustomDialog.Builder builder = new CustomDialog.Builder(
                            Project.this);
                    builder.setMessage(getString(R.string.alert_notice_IsDelete));
                    builder.setTitle(R.string.alert_notice);
                    builder.setPositiveButton(R.string.confirm,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    // dbManager.delData(select);

                                    // String str = list.get(arg2).get("name");
                                    //删掉item


                                    String[] args = {select};
                                    String condition = "JobName = ?";

                                    List<Object> objList = mJob.selectByCondition(condition, args, "ID");
                                    for (Object object : objList) {
                                        JobInfo abc = mJob.getSelectedJob();
                                        if (abc != null) {
                                            abc.IsSelected = CommonEnum.DbBoolean.False.getValue();
                                            mJob.update(abc);
                                        }
                                        JobInfo jobInfo = (JobInfo) object;
                                        jobInfo.CoverageArea=0;
                                        jobInfo.IsSelected=CommonEnum.DbBoolean.True.getValue();
                                        mJob.update(jobInfo);
                                        mJob.deleteJobCoverage(jobInfo);

                                    }
                                    bindDate();

                                    Toast.makeText(Project.this, getString(R.string.delete) + select + getString(R.string.gjd), Toast.LENGTH_LONG).show();
                                    dialog.dismiss();
                                }
                            });

                    builder.setNegativeButton(R.string.cancel,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    dialog.dismiss();
                                }
                            });

                    builder.create().show();

                } else {
                    Toast.makeText(Project.this, getString(R.string.mes_5), Toast.LENGTH_LONG).show();
                }

                ;


            }
        });
        deletebt = (Button) findViewById(R.id.deletebt);
        deletebt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (select != null) {

                    CustomDialog.Builder builder = new CustomDialog.Builder(
                            Project.this);
                    builder.setMessage(getString(R.string.alert_notice_IsDelete));
                    builder.setTitle(R.string.alert_notice);
                    builder.setPositiveButton(R.string.confirm,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    // dbManager.delData(select);

                                    // String str = list.get(arg2).get("name");
                                    //删掉item

                                    list.remove(list_delete);
                                    list_delete--;
                                    //动态更新listview
                                    dataAdapter.notifyDataSetChanged();

                                    String[] args = {select};
                                    String condition = "JobName = ?";

                                    List<Object> objList = mJob.selectByCondition(condition, args, "ID");
                                    for (Object object : objList) {
                                        JobInfo jobInfo = (JobInfo) object;
                                        Log.e("获取", jobInfo.RecordTableName);
                                        mJob.DropTabel(jobInfo.RecordTableName);
                                        mJob.DropTabel(jobInfo.RecordTableName.replace("Record","Cureve"));
                                    }
                                    select = null;

                                    mJob.deleteByCondition("JobName", args
                                    );
                                      dialog.dismiss();
                                }
                            });

                    builder.setNegativeButton(R.string.cancel,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    dialog.dismiss();
                                }
                            });

                    builder.create().show();

                } else {
                    Toast.makeText(Project.this, getString(R.string.mes_6), Toast.LENGTH_LONG).show();
                }

                ;


            }
        });
        ic_back=(Button) findViewById(R.id.ic_back);
        ic_back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
              finish();

            }
        });
        newbt=(Button) findViewById(R.id.newbt);
        newbt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(Project.this,
                        JobManageActivity.class));
                finish();

            }
        });
    }
    public void bindDate() {
        List<Object> objList = mJob.selectAll("ID", "desc");

        projectgd = (ListView) findViewById(R.id.projectgd);
        projectgd.post(new Runnable() {
            @Override
            public void run() {
                projectgd.smoothScrollToPosition(list_delete);
            }
        });
        projectgd.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {



                list_delete = position;
                dataAdapter.setSelectedItem(position);

                dataAdapter.notifyDataSetChanged();

                MemberInfo info = (MemberInfo) projectgd.getItemAtPosition(position);
                select = info.name;
                //获取内容


            }
        });

        ArrayList<MemberInfo> infoList = new ArrayList<MemberInfo>();
        int i_c = 0;


        for (Object object : objList) {


            MemberInfo abc = new MemberInfo();
            JobInfo jobInfo = (JobInfo) object;
            if (jobInfo.IsSelected == CommonEnum.DbBoolean.True.getValue()) {
                list_delete = i_c;
                select = jobInfo.JobName;
            }
            i_c++;
            abc.name = jobInfo.JobName;

            abc._id = jobInfo.ID;
            abc.jobtype= getResources().getStringArray(R.array.Job_Type)[jobInfo.AbType];
            abc.Cover = CommonHelper.decimalFormat
                    .format(jobInfo.CoverageArea);
            abc.SetH = jobInfo.setH+"";


            abc.Width= jobInfo.Width+"";
            abc.Cover= CommonHelper.decimalFormat
                    .format(jobInfo.CoverageArea);
            abc.jobtype= getResources().getStringArray(R.array.Job_Type)[jobInfo.AbType];
            infoList.add(abc);
        }


        list = new ArrayList<Object>();
        for (MemberInfo info : infoList) {
            list.add(info);
        }
        dataAdapter = new DataAdapter(this, list);

        projectgd.setAdapter(dataAdapter);

        dataAdapter.setSelectedItem(list_delete);
    }
}