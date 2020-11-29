package com.example.diary_my.helper;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * This notification is used for displaying DissmissAlarmActivity because of Android Q limitations.
 */
public class DismissAlarmNotificationController {

    private NotificationManager notificationManager;
    private Context context;

    public DismissAlarmNotificationController(Context context) {
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        this.context = context;
    }

    public void showNotification() {
        Intent my_intent = new Intent(context, com.example.diary_my.DismissAlarmActivity.class);
        my_intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(my_intent);

    }
}
