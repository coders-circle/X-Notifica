package com.fabb.notifica;

import android.content.Intent;

import com.google.android.gms.iid.InstanceIDListenerService;

public class GcmRegisterListener extends InstanceIDListenerService {

    @Override
    public void onTokenRefresh() {
        Intent intent = new Intent(this, GcmRegisterIntent.class);
        startService(intent);
    }
}
