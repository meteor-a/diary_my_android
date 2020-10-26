package com.example.diary_my.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

import com.example.diary_my.activities.MainActivity;

public class AlertReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        PowerManager pm = (PowerManager) context.getApplicationContext().getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = pm.newWakeLock((PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), "myapp:my_diary");
        wakeLock.acquire();
        wakeLock.release();

        MainActivity.alarm_ring = true;

        Intent intent1 = new Intent(context, MainActivity.class);
        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent1.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);

        context.getApplicationContext().startActivity(intent1);

    }
}
