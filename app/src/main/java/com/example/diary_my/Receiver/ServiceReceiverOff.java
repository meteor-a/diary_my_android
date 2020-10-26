package com.example.diary_my.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.diary_my.activities.RingActivity;

public class ServiceReceiverOff extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        context.stopService(RingActivity.serv_intent);
    }
}
