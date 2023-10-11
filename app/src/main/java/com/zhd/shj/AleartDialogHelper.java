package com.zhd.shj;
import java.io.File;

import com.zhd.shj.CustomDialog;
import com.zhd.shj.R;
import com.zhd.shj.R.string;


import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;

/**
 * 提示框、toast帮助类
 *
 * @author zzw
 *
 */
public class AleartDialogHelper {

    public static void aleartDialog(Context context, String message) {
        CustomDialog.Builder builder = new CustomDialog.Builder(context);
        builder.setMessage(message);
        builder.setTitle(R.string.alert_notice);
        builder.setPositiveButton(R.string.confirm,
                new OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        // 设置你的操作事项
                    }
                });
        builder.setNegativeButton(R.string.cancel,
                new OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        // 设置你的操作事项
                    }
                });

        builder.create().show();
    }

    public static void aleartConfirmDialog(Context context, String message) {
        CustomDialog.Builder builder = new CustomDialog.Builder(context);
        builder.setMessage(message);
        builder.setTitle(R.string.alert_notice);
        builder.setPositiveButton(R.string.confirm,
                new OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        // 设置你的操作事项
                    }
                });

        builder.create().show();
    }

    public static void aleartAlarmDialog(Context context, String message,
                                         int color) {
        CustomDialog.Builder builder = new CustomDialog.Builder(context);
        builder.setMessage(message);
        builder.setTitle(R.string.alert_notice);
        builder.setTextColor(color);
        builder.setPositiveButton(R.string.confirm,
                new OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        // 设置你的操作事项
                    }
                });

        builder.create().show();
    }

    public static void ConfirmDialog(final Context context, int message) {
        Builder builder = new Builder(context);
        builder.setTitle(R.string.alert_notice)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(R.string.confirm,
                        new OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                Activity activity = (Activity) context;
                                activity.finish();
                            }
                        }).create().show();

    }

    public static void QuitOrNotDialog(final Context context, int message) {
        Builder builder = new Builder(context);
        builder.setTitle(R.string.alert_notice)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setMessage(message)
                .setPositiveButton(R.string.confirm,
                        new OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                Activity activity = (Activity) context;
                                activity.finish();
                            }
                        })
                .setNegativeButton(R.string.cancel,
                        new OnClickListener() {

                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.cancel();// 取消弹出框
                            }
                        }).create().show();
    }

    public static Toast alertToast(Context context, String message) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.toast_normal, null);
        TextView tvName = (TextView) view.findViewById(R.id.Name);
        tvName.setText(message);

        Toast toast = new Toast(context);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(view);
        toast.setDuration(1000);
        toast.show();

        return toast;
    }

    public static Toast alertBottomToast(Context context, String message) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.toast_normal, null);
        TextView tvName = (TextView) view.findViewById(R.id.Name);
        tvName.setText(message);

        Toast toast = new Toast(context);
        toast.setGravity(Gravity.BOTTOM, 0, 60);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(view);
        toast.setDuration(1000);
        toast.show();

        return toast;
    }

    private static Toast TOAST = null;

    public static void alToast(Context context, String message, int seconds) {
        // Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        // // toast.setGravity(Gravity.BOTTOM, 0, 0);
        // toast.setDuration(1000);
        // toast.show();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.toast_normal, null);
        TextView tvName = (TextView) view.findViewById(R.id.Name);
        tvName.setText(message);
        if (TOAST == null) {
            TOAST = new Toast(context);
            TOAST.setGravity(Gravity.CENTER, 0, 0);
            TOAST.setDuration(Toast.LENGTH_LONG);
            TOAST.setDuration(seconds * 1000);
        }

        TOAST.setView(view);
        TOAST.show();
    }

    /**
     * 得到自定义的progressDialog
     *
     * @param context
     * @param msg
     * @return
     */
    public static Dialog createLoadingDialog(Context context, String msg) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.dialog_progress_circle, null);// 得到加载view
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);// 加载布局
        // main.xml中的ImageView
        ImageView spaceshipImage = (ImageView) v.findViewById(R.id.img);
        TextView tipTextView = (TextView) v.findViewById(R.id.tipTextView);// 提示文字
        // 加载动画
        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
                context, R.anim.loading_animation);
        // 使用ImageView显示动画
        spaceshipImage.startAnimation(hyperspaceJumpAnimation);
        tipTextView.setText(msg);// 设置加载信息

        Dialog loadingDialog = new Dialog(context, R.style.loading_dialog);// 创建自定义样式dialog

        loadingDialog.setCancelable(false);// 不可以用“返回键”取消
        loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.FILL_PARENT));// 设置布局
        return loadingDialog;
    }

    public static void setDialogCircleMesage(String msg){

    }
}