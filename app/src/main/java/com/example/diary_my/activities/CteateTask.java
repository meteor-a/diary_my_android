package com.example.diary_my.activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.TimePickerDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.diary_my.R;
import com.example.diary_my.TimerManager;
import com.example.diary_my.db.Contact_Database;
import com.example.diary_my.db.DBHelper;
import com.example.diary_my.dialogs.Dialog_save_note;
import com.example.diary_my.helper.SharedPrefManager;
import com.example.diary_my.helper.SyncCreateTimeManager;
import com.example.diary_my.helper.SyncUpdateTask;
import com.google.android.material.textfield.TextInputLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.app.LoaderManager;

public class CteateTask extends AppCompatActivity implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor>, Dialog_save_note.NoticeDialogListener { // LoaderManager.LoaderCallbacks<Cursor>

    private final int DIALOG_TIME = 1;
    private final int DIALOG_SAVE_TASK = 2;
    private int myHour = -1;
    private int myMinute = -1;
    private TextView setTime;
    private CalendarView setData;
    private EditText inputTask;
    private EditText inputDescription;
    private TextInputLayout taskLayout;
    public static final String EXTRA_TASK_ID = "task_id";
    private long taskId;
    Calendar selectedDate;

    final int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 45;
    @SuppressLint("StaticFieldLeak")
    public  static  TimerManager timerManager;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cteate_task);

        timerManager = new TimerManager(this);

        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DIALOG_SAVE_TASK);
            }
        });

        setTime = findViewById(R.id.TextSetTime);
        setData = findViewById(R.id.SetDataView);
        inputTask = findViewById(R.id.TaskEditText);
        inputDescription = findViewById(R.id.DescriptionEditText);
        taskLayout = findViewById(R.id.TextLayoutTask);
        setTime.setOnClickListener(this);

        selectedDate = Calendar.getInstance();

        taskId = getIntent().getLongExtra(EXTRA_TASK_ID, -1);

        setData.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(@NonNull CalendarView arg0, int year, int month,
                                            int day) {
                selectedDate.set(Calendar.YEAR, year);
                selectedDate.set(Calendar.MONTH, month);
                selectedDate.set(Calendar.DAY_OF_MONTH, day);
            }
        });


        if (taskId != -1) {

            getLoaderManager().initLoader(
                    0, // Идентификатор загрузчика
                    null, // Аргументы
                    this // Callback для событий загрузчика
            );
        }

    }


    protected Dialog onCreateDialog(int id) {
        if (id == DIALOG_SAVE_TASK) {
            AlertDialog.Builder adb = new AlertDialog.Builder(this);
            adb.setTitle("Сохранить задачу?");
            adb.setMessage("Сохранить задачу?");
            adb.setPositiveButton(R.string.dialog_save_task_yes, myClickListenerOk);
            adb.setNegativeButton(R.string.dialog_save_task_no, myClickListenerNo);
            return adb.create();
        }

        if (id == DIALOG_TIME) {
            Date date = new Date();
            myHour = date.getHours();
            myMinute = date.getMinutes();
            return new TimePickerDialog(this, myCallBack, myHour, myMinute, true);
        }
        return super.onCreateDialog(id);
    }

    TimePickerDialog.OnTimeSetListener myCallBack = new TimePickerDialog.OnTimeSetListener() {
        @SuppressLint("SetTextI18n")
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            selectedDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
            selectedDate.set(Calendar.MINUTE, minute);
            selectedDate.set(Calendar.SECOND, 0);
            myHour = hourOfDay;
            myMinute = minute;
            if (myHour < 10) {
                setTime.setText("0" + myHour + ":" + myMinute);
            } else {
                setTime.setText(myHour + ":" + myMinute);
            }
        }
    };

    DialogInterface.OnClickListener myClickListenerOk = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            save_task();
        }
    };

    DialogInterface.OnClickListener myClickListenerNo = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            finish();
        }
    };

    @Override
    public void onClick(View v) {
        showDialog(DIALOG_TIME);
    }

    public boolean checkInputData() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String selectedDate = sdf.format(new Date(setData.getDate()));
        String task = inputTask.getText().toString();
        if (task.isEmpty()) {
            taskLayout.setError("Введите задачу");
            taskLayout.setErrorEnabled(true);
            return false;
        }
        if (myMinute == -1 && myHour == -1) {
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        Log.i("Test", "Back android pressed");
        DialogFragment DialogDeleteNote;
        DialogDeleteNote = new Dialog_save_note();
        DialogDeleteNote.show(getSupportFragmentManager(), "dlg2");
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        // User touched the dialog's positive button
        Log.i("Test", "Dialog delete yes");
        save_task();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        Log.i("Test", "Dialog delete no");
        finish();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.create_task, menu);
        return true;
    }


    @SuppressLint("NonConstantResourceId")
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                save_task();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void save_task() {
        if (checkInputData()) {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            long user_id = SharedPrefManager.getInstance(getApplicationContext()).getUser().getId();

            String task = inputTask.getText().toString();
            String description = inputDescription.getText().toString();
            Calendar c = Calendar.getInstance();
            String formattedDate = df.format(c.getTime());

            String time_notification = df.format(selectedDate.getTime());

            ContentValues contentValues = new ContentValues();

            contentValues.put(Contact_Database.Tasks.TASK_COLUMN, task);
            contentValues.put(Contact_Database.Tasks.DESCRIPTION_COLUMN, description);
            if (taskId == -1) {
                contentValues.put(Contact_Database.Tasks.CREATE_TS_COLUMN, formattedDate);
            }

            contentValues.put(Contact_Database.Tasks.UPDATED_TS_COLUMN, formattedDate);

            contentValues.put(Contact_Database.Tasks.SYNCHRONIZED, 0);

            contentValues.put(Contact_Database.Tasks.NOTIFICATION_DATE_COLUMN, time_notification);

            contentValues.put(Contact_Database.Tasks.COMPLETE, 0);
            contentValues.put(Contact_Database.Tasks.ALARM, 0);

            if (taskId == -1) {
                getContentResolver().insert(Contact_Database.Tasks.URI, contentValues);
                SyncCreateTimeManager synctask = new SyncCreateTimeManager(this, task, description, formattedDate, formattedDate, time_notification, 0, user_id);
                synctask.sync_new_task();

            } else {
                getContentResolver().update(ContentUris.withAppendedId(Contact_Database.Tasks.URI, taskId),
                        contentValues,
                        null,
                        null);
                DBHelper dbHelper = new DBHelper(this);
                SQLiteDatabase db = dbHelper.getWritableDatabase();

                String selectQuery = "SELECT  created_ts, updated_ts, notification_date FROM " + Contact_Database.Tasks.TABLE_NAME + " WHERE " + Contact_Database.Tasks._ID + " = " + taskId;
                @SuppressLint("Recycle") Cursor cursor = db.rawQuery(selectQuery, null);
                cursor.moveToFirst();
                int Create_tsColumn = cursor.getColumnIndex(Contact_Database.Tasks.CREATE_TS_COLUMN);
                int Update_tsColumn = cursor.getColumnIndex(Contact_Database.Tasks.UPDATED_TS_COLUMN);
                int notificationColumn = cursor.getColumnIndex(Contact_Database.Tasks.NOTIFICATION_DATE_COLUMN);
                SyncUpdateTask sync = new SyncUpdateTask(this, task, description, cursor.getString(Create_tsColumn), cursor.getString(Update_tsColumn), cursor.getString(notificationColumn), 0, user_id, taskId);
                sync.sync_task_update();

            }
            checkNotificationPolicy();
            checkOverlayPermission();
            timerManager.SetAlarmTimer();

            finish();
        }
    }

    private void checkNotificationPolicy() {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && !notificationManager.isNotificationPolicyAccessGranted()) {
            Intent intent = new Intent(
                    android.provider.Settings
                            .ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
            startActivity(intent);
        }
    }

    /**
     * needed for Android Q: on some devices activity doesn't show from fullScreenNotification without
     * permission SYSTEM_ALERT_WINDOW
     */
    private void checkOverlayPermission() {
        if ((Build.VERSION.SDK_INT > Build.VERSION_CODES.P) && (!Settings.canDrawOverlays(this))) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + this.getPackageName()));
            startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
        }
    }

    @SuppressLint({"SimpleDateFormat", "SetTextI18n"})
    public void displayTask(Cursor cursor) throws ParseException {
        if (!cursor.moveToFirst()) {
            // Если не получилось перейти к первой строке — завершаем Activity
            finish();
        }


        String date = cursor.getString(cursor.getColumnIndexOrThrow(Contact_Database.Tasks.NOTIFICATION_DATE_COLUMN));
        String task = cursor.getString(cursor.getColumnIndexOrThrow(Contact_Database.Tasks.TASK_COLUMN));
        String description = cursor.getString(cursor.getColumnIndexOrThrow(Contact_Database.Tasks.DESCRIPTION_COLUMN));

        inputTask.setText(task);
        inputDescription.setText(description);
        setData.setDate(new SimpleDateFormat("yyyy-MM-dd").parse(date).getTime(), true, true); // дата

        myHour = new SimpleDateFormat("yyyy-MM-dd hh:mm").parse(date).getHours();
        myMinute = new SimpleDateFormat("yyyy-MM-dd hh:mm").parse(date).getMinutes();
        if (myHour < 10) {
            setTime.setText("0" + myHour + ":" + myMinute);
        } else {
            setTime.setText(myHour + ":" + myMinute);
        }
    }

    public void initData() {

    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                this,  // Контекст
                ContentUris.withAppendedId(Contact_Database.Tasks.URI, taskId), // URI
                Contact_Database.Tasks.SINGLE_PROJECTION, // Столбцы
                null, // Параметры выборки
                null, // Аргументы выборки
                null // Сортировка по умолчанию
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        Log.i("Test", "Load finished: " + cursor.getCount());
        cursor.setNotificationUri(getContentResolver(), Contact_Database.Tasks.URI);
        try {
            displayTask(cursor);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
