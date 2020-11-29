package com.example.diary_my;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.diary_my.Receiver.AlarmBroadcastReceiver;
import com.example.diary_my.db.Contact_Database;
import com.example.diary_my.db.DBHelper;
import com.example.diary_my.helper.SharedPreferencesHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.example.diary_my.Receiver.AlarmBroadcastReceiver.KEY_IS_ONE_TIME;

/**
 * Created by Ilya Anshmidt on 23.09.2017.
 */

public class TimerManager {
    private Context context;
    private AlarmManager alarmManager;
    private SharedPreferencesHelper sharPrefHelper;
    private final String LOG_TAG = com.example.diary_my.TimerManager.class.getSimpleName();

    public TimerManager(Context context) {
        this.context = context;
        this.alarmManager = (AlarmManager) this.context.getSystemService(Context.ALARM_SERVICE);
        this.sharPrefHelper = new SharedPreferencesHelper(context);
    }

    public void SetCompleteTask() {
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String update_rows = "UPDATE " + Contact_Database.Tasks.TABLE_NAME + " SET " + Contact_Database.Tasks.COMPLETE + " = 1" + " WHERE " + Contact_Database.Tasks.ALARM + " = 1";
        db.execSQL(update_rows);
    }

    public void SetAlarmTimer() {
        long millies = -1;
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String query_rows = "SELECT * FROM " + Contact_Database.Tasks.TABLE_NAME  + " ORDER BY " + Contact_Database.Tasks.NOTIFICATION_DATE_COLUMN + " ASC";
        Cursor cursor= db.rawQuery(query_rows, null);

        int column_index = cursor.getColumnIndexOrThrow(Contact_Database.Tasks.NOTIFICATION_DATE_COLUMN);
        int column_complete = cursor.getColumnIndexOrThrow(Contact_Database.Tasks.COMPLETE);

        Date date_notification = new Date();
        Date date_now = new Date();

        if (cursor.moveToFirst()) {
            for (int ii = 1; ii <= cursor.getCount(); ++ii) {

                try {
                    date_notification = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(cursor.getString(column_index));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (date_notification.after(date_now) && cursor.getLong(column_complete) == 0) {

                    String update_rows = "UPDATE " + Contact_Database.Tasks.TABLE_NAME + " SET " + Contact_Database.Tasks.ALARM + " = 0" + " WHERE " + Contact_Database.Tasks.ALARM + " = 1";
                    db.execSQL(update_rows);

                    Calendar c = Calendar.getInstance();
                    c.set(Calendar.HOUR_OF_DAY, date_notification.getHours());
                    c.set(Calendar.MINUTE, date_notification.getMinutes());
                    c.set(Calendar.SECOND, 0);
                    c.set(Calendar.YEAR, date_notification.getYear() + 1900);
                    c.set(Calendar.MONTH, date_notification.getMonth());
                    c.set(Calendar.DAY_OF_MONTH, date_notification.getDate());

                    Log.i("service", "start " + c.toString());

                    if (c.before(Calendar.getInstance())) {
                        c.add(Calendar.DATE, 1);
                    }

                    update_rows = "UPDATE " + Contact_Database.Tasks.TABLE_NAME + " SET " + Contact_Database.Tasks.ALARM + " = 1" + " WHERE " + Contact_Database.Tasks._ID + " = " + cursor.getLong(cursor.getColumnIndex(Contact_Database.Tasks._ID));
                    db.execSQL(update_rows);

                    db.close();

                    millies = c.getTimeInMillis();
                    break;
                }

                cursor.moveToNext();
            }

        }

        db.close();

        if (millies != -1) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(millies);
            Log.d(LOG_TAG, "single alarm scheduled to: "+ calendar.getTime());

            Intent intent = new Intent(context, AlarmBroadcastReceiver.class);
            intent.putExtra(KEY_IS_ONE_TIME, Boolean.FALSE);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.setAlarmClock(new AlarmManager.AlarmClockInfo(millies, pendingIntent), pendingIntent);
        }
    }

    public void cancelTimer() {
        Intent intent = new Intent(context, AlarmBroadcastReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        alarmManager.cancel(sender);
    }

    public void resetSingleAlarmTimer() {  //if alarm is turned on and preferences have changed
        cancelTimer();
        SetAlarmTimer();
        Log.d(LOG_TAG, "Alarm is reset");
    }

}
