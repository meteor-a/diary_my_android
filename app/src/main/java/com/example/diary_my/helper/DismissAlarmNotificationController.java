package com.example.diary_my.helper;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class DismissAlarmNotificationController {

    private final Context context;

    public DismissAlarmNotificationController(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        this.context = context;
    }

    public void showNotification() {
        Intent my_intent = new Intent(context, com.example.diary_my.DismissAlarmActivity.class);
        my_intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(my_intent);

    }
}
