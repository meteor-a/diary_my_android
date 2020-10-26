package com.example.diary_my.Receiver;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.diary_my.R;

public class NotificationHelper extends ContextWrapper {
    String NOTIFICATION_CHANNEL_ID = "com.example.diary_my";
    String channelName = "My Background Service";

    private NotificationManager mManager;

    public NotificationHelper(Context base) {
        super(base);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createChannel() {
        NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_HIGH);

        getManager().createNotificationChannel(channel);
    }

    public NotificationManager getManager() {
        if (mManager == null) {
            mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }

        return mManager;
    }

    public NotificationCompat.Builder getChannelNotification() {
        return new NotificationCompat.Builder(getApplicationContext(), NOTIFICATION_CHANNEL_ID)
                .setContentTitle("Alarm!")
                .setContentText("ALARMMMMMMMMM.")
                .setSmallIcon(R.drawable.ic_launcher_foreground);
    }
}