package com.example.diary_my;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.diary_my.Receiver.ServiceReceiverOff;

public class Service_alarm_timemanager extends Service {

    MediaPlayer ambientMediaPlayer;

    @Override
    public IBinder onBind(Intent intent) {

        throw new UnsupportedOperationException("Not yet implemented");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate(){
        super.onCreate();

        String NOTIFICATION_CHANNEL_ID = "com.example.simpleapp";
        String channelName = "My Background Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.createNotificationChannel(chan);

        PendingIntent pStopSelf = PendingIntent.getBroadcast(this, 1, new Intent(this, ServiceReceiverOff.class), 0);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.ic_action_edit)
                .setContentTitle("Напоминание")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .addAction(R.drawable.ic_action_edit, "Остановить", pStopSelf)
                .build();

        ambientMediaPlayer=MediaPlayer.create(this, R.raw.tnt);
        ambientMediaPlayer.setLooping(true);

        startForeground(1, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        ambientMediaPlayer.start();

        return START_STICKY;
    }
    @Override
    public void onDestroy() {
        ambientMediaPlayer.stop();
    }
}