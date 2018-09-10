package com.mamahome360.mamahomele.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ReceiverCall extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, Location_ForeGround_Service.class));;
    }
}
