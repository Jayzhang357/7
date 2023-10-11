package com.zhd.gnssmanager;

import java.io.File;

import com.zhd.gnssmanager.*;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.Gravity;
import android.widget.Toast;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;

public class AleartDialogHelper {
	private static ProgressDialog m_pDialog = null;

	public static void aleartDialog(Context context, String message) {
		AlertDialog.Builder builder = new Builder(context);
		builder.setMessage(message);

		builder.setTitle(R.string.dialog_notice);

		builder.setNegativeButton(R.string.dialog_confirm,
				new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int arg1) {
						// TODO Auto-generated method stub
						dialog.dismiss();

					}
				});

		builder.create().show();

	}

	public static ProgressDialog alertProgressDialog(Context context,
													 String message) {

		// 创建ProgressDialog对象
		m_pDialog = new ProgressDialog(context);

		// 设置进度条风格，风格为圆形，旋转的
		m_pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

		// 设置ProgressDialog 的进度条是否不明确
		m_pDialog.setIndeterminate(false);

		// 设置ProgressDialog 是否可以按退回按键取消
		m_pDialog.setCancelable(false);

		// 设置ProgressDialog 提示信息
		m_pDialog.setMessage(message);

		m_pDialog.show();

		return m_pDialog;

	}

	public static void hideProgressDialog() {

		if (m_pDialog != null)
			m_pDialog.dismiss();
	}

	public static void aleartDialog(Context context, int message) {
		AlertDialog.Builder builder = new Builder(context);
		builder.setMessage(message);

		builder.setTitle(R.string.alert_notice);

		builder.setNegativeButton(R.string.confirm, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int arg1) {
				// TODO Auto-generated method stub
				dialog.dismiss();

			}
		});

		builder.create().show();

	}

	public static void ConfirmDialog(final Context context, int message) {
		AlertDialog.Builder builder = new Builder(context);
		builder.setTitle(R.string.alert_notice)
				.setIcon(android.R.drawable.ic_dialog_info)
				.setMessage(message)
				.setPositiveButton(R.string.confirm,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
												int which) {
								Activity activity = (Activity) context;
								activity.finish();
							}
						}).create().show();

	}

	public static void QuitOrNotDialog(final Context context, int message) {
		AlertDialog.Builder builder = new Builder(context);
		builder.setTitle(R.string.alert_notice)
				.setIcon(android.R.drawable.ic_dialog_info)
				.setMessage(message)
				.setPositiveButton(R.string.confirm,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
												int which) {
								Activity activity = (Activity) context;
								activity.finish();
							}
						})
				.setNegativeButton(R.string.cancel,
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
												int which) {
								dialog.cancel();// 取消弹出框
							}
						}).create().show();

	}

	public static Toast alertToast(Context context, String message) {
		Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.setDuration(Toast.LENGTH_LONG);
		toast.show();

		return toast;

	}
}
