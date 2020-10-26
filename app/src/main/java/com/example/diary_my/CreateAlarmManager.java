package com.example.diary_my;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.diary_my.activities.MainActivity;
import com.example.diary_my.db.Contact_Database;
import com.example.diary_my.db.DBHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.example.diary_my.activities.MainActivity.alarmManager;
import static com.example.diary_my.activities.MainActivity.pendingIntent;

public class CreateAlarmManager {

    Context context;
    String task;
    String description;

    public CreateAlarmManager(Context context) {
        this.context = context;
    }


    @SuppressLint("SimpleDateFormat")
    public void create_alarm() {
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String query_rows = "SELECT * FROM " + Contact_Database.Tasks.TABLE_NAME  + " ORDER BY " + Contact_Database.Tasks.NOTIFICATION_DATE_COLUMN + " ASC";
        Cursor cursor= db.rawQuery(query_rows, null);

        int column_index = cursor.getColumnIndexOrThrow(Contact_Database.Tasks.NOTIFICATION_DATE_COLUMN);
        int column_complete = cursor.getColumnIndexOrThrow(Contact_Database.Tasks.COMPLETE);

        Date date_notification = new Date();
        Date date_now = new Date();
        boolean flag = false;

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
                    flag = true;
                    MainActivity.alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);

                    update_rows = "UPDATE " + Contact_Database.Tasks.TABLE_NAME + " SET " + Contact_Database.Tasks.ALARM + " = 1" + " WHERE " + Contact_Database.Tasks._ID + " = " + cursor.getLong(cursor.getColumnIndex(Contact_Database.Tasks._ID));
                    db.execSQL(update_rows);

                    db.close();

                    break;
                }

                cursor.moveToNext();
            }

        }

        if (!flag) {
            alarmManager.cancel(pendingIntent);
        }

        db.close();
    }
}
