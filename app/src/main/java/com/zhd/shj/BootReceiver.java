package com.zhd.shj;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.zhd.shj.activity.FirstOpen;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED"))
        {
            Intent i = new Intent(context, FirstOpen.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }
}
