package com.fabb.notifica;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class UpdateServiceReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        UpdateService.Launch(context);
    }
}
