package com.example.diary_my.Receiver;

/**
 * Created by Ilya Anshmidt on 19.09.2017.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.example.diary_my.DismissAlarmActivity;
import com.example.diary_my.helper.DismissAlarmNotificationController;

public class AlarmBroadcastReceiver extends BroadcastReceiver {

    public static final String KEY_IS_ONE_TIME = "onetime";
    DismissAlarmNotificationController dismissAlarmNotificationController;

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.i("AlarmBroadCastReceiver", "onReceiver()");
        dismissAlarmNotificationController = new DismissAlarmNotificationController(context);

        // since Android Q it's not allowed to start activity from the background
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            dismissAlarmNotificationController.showNotification();
        } else {
            Intent dismissAlarmIntent = new Intent(context, DismissAlarmActivity.class);
            dismissAlarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(dismissAlarmIntent);
        }
    }



}
