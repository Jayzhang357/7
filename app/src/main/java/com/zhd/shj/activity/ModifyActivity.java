package com.zhd.shj.activity;



import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.zhd.commonhelper.CommonUtil;
import com.zhd.shj.R;
import com.zhd.shj.boardcast.CommonBroadCast;
import com.zhd.shj.business.Job;
import com.zhd.shj.entity.CommonEnum;
import com.zhd.shj.entity.JobInfo;
import com.zhd.shj.listviewadpter.CommonAdapter;

import java.util.ArrayList;
import java.util.List;

public class ModifyActivity extends BaseActivity {

    private Button btnBack,btnFinish,rest;
    private GridView mGridView = null;
    private CommonAdapter mAdapter = null;
    private  EditText mEditText=null;
    private  EditText etWidth=null;
    private Job mJob = new Job(this);
    JobInfo ecuprj_in;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_job_modify);
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
        SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);

        String usernametext = sharedPreferences.getString("Width", "");
        if(usernametext.length()==0)
            etWidth.setText("5");
            else
        etWidth.setText(usernametext);

    }
    private void  iniView() {
        mEditText= (EditText) findViewById(R.id.etCreateField);
        etWidth= (EditText) findViewById(R.id.etWidth);
        etWidth.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {


            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.e( "设置","设置完毕");
            }
        });
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
                    if(etWidth.getText().toString().length()==0||etWidth.getText().toString().equals("0"))
                    {
                        Toast.makeText(getApplicationContext(), getString(R.string.width_error), Toast.LENGTH_LONG).show();
                        return;
                    }
                    SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("Width", etWidth.getText() + "");

                    editor.apply();
                    JobInfo mParamJobInfo = new JobInfo();
                    // 设置AB类型和作业名称
                    mParamJobInfo.AbType = (int)mAdapter.getCurrentPosition();
                    mParamJobInfo.IsSelected = CommonEnum.DbBoolean.True.getValue();
                    mParamJobInfo.ID = -1;
                    mParamJobInfo.JobName = mEditText.getText().toString();
                    mParamJobInfo.Width=Double.parseDouble(etWidth.getText().toString());
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
                    ecuprj_in.Width=Double.parseDouble(etWidth.getText().toString());
                    mJob.update(ecuprj_in);
                    Intent intent = new Intent(ModifyActivity.this, Project.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
        btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ModifyActivity.this, Project.class);
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