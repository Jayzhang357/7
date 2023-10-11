package com.zhd.shj.activity;



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.zhd.commonhelper.CommonUtil;
import com.zhd.shj.AppManager;
import com.zhd.shj.CommonHelper;
import com.zhd.shj.R;
import com.zhd.shj.boardcast.CommonBroadCast;
import com.zhd.shj.business.Job;
import com.zhd.shj.entity.CommonEnum;
import com.zhd.shj.entity.JobInfo;
import com.zhd.shj.listviewadpter.CommonAdapter;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class JobManageActivity extends BaseActivity {

    private Button btnBack,btnFinish,rest;
    private GridView mGridView = null;
    private CommonAdapter mAdapter = null;
    private  EditText mEditText=null;
    private Job mJob = new Job(this);
    JobInfo ecuprj_in;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_job_manage);
        iniView();
        Intent intent1 = getIntent();


        ecuprj_in = (JobInfo) intent1.getSerializableExtra("ecuprj_modify");
        if(ecuprj_in==null)
        {
            Log.e("作业","收不到作业");
        }
        else
        {
            mEditText.setText(ecuprj_in.JobName);
            mAdapter.setCurrentPosition(ecuprj_in.AbType);
        }
    }
    private void bindData()
    {
        String text = "HN1";

        int count = 1;
        text = "HN" + count;
       while (mJob.existName(text)) {
        count++;
        text = "HN" + count;

       }
        mEditText.setText(text);

    }
    private void  iniView() {
        mEditText= (EditText) findViewById(R.id.etCreateField);
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                Log.e("设置","设置完毕1");
                String editable = mEditText.getText().toString();
                String str = CommonUtil.stringFilter(editable); // 过滤特殊字符
                if (!editable.equals(str)) {
                    mEditText.setText(str);
                }
                mEditText.setSelection(mEditText.length());
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.e( "设置","设置完毕");
            }
        });
        bindData();
        List<String> data = new ArrayList<String>();
        String[] names = getResources().getStringArray(R.array.Job_Type);
        for (int i = 0; i < names.length; i++) {
            data.add(names[i]);
        }
        mAdapter = new CommonAdapter(this, data);
        mGridView = (GridView) findViewById(R.id.gvField);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view,
                                    int position, long arg3) {
                //曲线特殊处理
                mAdapter.setCurrentPosition(position);
                mAdapter.notifyDataSetChanged();
            }
        });
        mGridView.setAdapter(mAdapter);
        mAdapter.setCurrentPosition(0);
        rest= (Button) findViewById(R.id.rest);
        rest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(ecuprj_in==null)
                {
                    if(mJob.existName(mEditText.getText().toString()))
                    {
                        Toast.makeText(getApplicationContext(), getString(R.string.job_name_exit), Toast.LENGTH_LONG).show();
                        return;
                    }
                    JobInfo mParamJobInfo = new JobInfo();
                    // 设置AB类型和作业名称
                    mParamJobInfo.AbType = (int)mAdapter.getCurrentPosition();
                    mParamJobInfo.IsSelected = CommonEnum.DbBoolean.True.getValue();
                    mParamJobInfo.ID = -1;
                    mParamJobInfo.JobName = mEditText.getText().toString();
                    Intent intent = new Intent(CommonBroadCast.BC_JOB_LOAD);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("PAR", mParamJobInfo);
                    intent.putExtras(bundle);
                    // 发送广播+
                    sendBroadcast(intent);
                    finish();
                }
                else
                {
                    ecuprj_in.AbType = (int)mAdapter.getCurrentPosition();
                    ecuprj_in.IsSelected = CommonEnum.DbBoolean.True.getValue();
                    ecuprj_in.JobName = mEditText.getText().toString();
                    mJob.update(ecuprj_in);
                    Intent intent = new Intent(JobManageActivity.this, Project.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
        btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(JobManageActivity.this, Project.class);
                startActivity(intent);
                finish();

            }
        });
        btnFinish = (Button) findViewById(R.id.btnFinish);
        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });
    }
}