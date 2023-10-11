package com.zhd.shj.activity;


import android.app.Activity;

import android.content.Context;

import android.content.res.Configuration;
import android.os.Bundle;

import com.zhd.shj.BigConfigxml;

import java.util.Locale;

/**
 * 普通页面的控制背光，设置白黑夜模式类
 * @author zzw
 */
public class BaseActivity extends Activity {

	protected boolean isEcuActivity = false;
	private Context mContext = BaseActivity.this;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		BigConfigxml mConfigxml = BigConfigxml
				.getInstantce(this);

		Configuration config = new Configuration(getResources().getConfiguration());
		if(mConfigxml.Language==0)

		config.setLocale(Locale.SIMPLIFIED_CHINESE); // newLocale 是你想要切换的语言 Locale 对象
		else
			config.setLocale(Locale.US); // newLocale 是你想要切换的语言 Locale 对象
		getResources().updateConfiguration(config, getResources().getDisplayMetrics());
		super.onCreate(savedInstanceState);


	}


}
