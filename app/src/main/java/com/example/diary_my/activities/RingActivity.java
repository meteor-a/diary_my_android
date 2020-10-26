package com.example.diary_my.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.diary_my.CreateAlarmManager;
import com.example.diary_my.R;
import com.example.diary_my.Receiver.NotificationHelper;
import com.example.diary_my.Service_alarm_timemanager;
import com.example.diary_my.db.Contact_Database;
import com.example.diary_my.db.DBHelper;

import in.shadowfax.proswipebutton.ProSwipeButton;

public class RingActivity extends AppCompatActivity {

    TextView task;
    TextView description;
    public static  Intent serv_intent;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ring);
        getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        ProSwipeButton proSwipeBtn = (ProSwipeButton) findViewById(R.id.off_alarm);

        serv_intent = new Intent(this, Service_alarm_timemanager.class);

        startForegroundService(serv_intent);

        proSwipeBtn.setOnSwipeListener(new ProSwipeButton.OnSwipeListener() {
            @Override
            public void onSwipeConfirm() {
                stopService(serv_intent);
                finish();
            }
        });

        task = findViewById(R.id.alarm_task);
        description = findViewById(R.id.alarm_description);

        setData();

    }

    public void setData() {
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String query_rows = "SELECT * FROM " + Contact_Database.Tasks.TABLE_NAME  + " WHERE " + Contact_Database.Tasks.ALARM + " = 1";
        Cursor cursor = db.rawQuery(query_rows, null);

        cursor.moveToFirst();

        int column_task = cursor.getColumnIndex(Contact_Database.Tasks.TASK_COLUMN);
        int column_description = cursor.getColumnIndex(Contact_Database.Tasks.DESCRIPTION_COLUMN);

        task.setText(cursor.getString(column_task));
        description.setText(cursor.getString(column_description));

        query_rows = "UPDATE " + Contact_Database.Tasks.TABLE_NAME + " SET " + Contact_Database.Tasks.COMPLETE + " = 1" + " WHERE " + Contact_Database.Tasks.ALARM + " = 1";
        db.execSQL(query_rows);

        String update_rows = "UPDATE " + Contact_Database.Tasks.TABLE_NAME + " SET " + Contact_Database.Tasks.ALARM + " = 0" + " WHERE " + Contact_Database.Tasks.ALARM + " = 1";
        db.execSQL(update_rows);

        db.close();

        CreateAlarmManager alarm = new CreateAlarmManager(getApplicationContext());

        alarm.create_alarm();
    }

    @Override
    public void onPause() {
        super.onPause();
        finish();
    }


}
