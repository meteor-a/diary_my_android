package com.example.diary_my.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;

import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.os.PowerManager;
import android.view.View;
import android.view.WindowManager;

import com.example.diary_my.R;
import com.example.diary_my.Receiver.AlertReceiver;

import com.example.diary_my.helper.SharedPrefManager;



public class MainActivity extends AppCompatActivity {

    public static AlarmManager alarmManager;
    public static PendingIntent pendingIntent;
    public static boolean alarm_ring = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        pendingIntent = PendingIntent.getBroadcast(this, 1, new Intent(this, AlertReceiver.class), PendingIntent.FLAG_UPDATE_CURRENT);

        if (SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, HomeActivity.class));
        }

        if (alarm_ring) {
            PowerManager pm = (PowerManager) this.getApplicationContext().getSystemService(Context.POWER_SERVICE);
            PowerManager.WakeLock wakeLock = pm.newWakeLock((PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), "myapp:my_diary");
            wakeLock.acquire();
            wakeLock.release();

            Intent intent = new Intent(this, RingActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            startActivity(intent);
            this.finish();
            alarm_ring = false;
        }

    }

    public void on_ChooseLoginButton_clicked(View view) {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void on_ChooseRegisterButton_clicked(View view) {
        Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
        startActivity(intent);
        finish();
    }

}
