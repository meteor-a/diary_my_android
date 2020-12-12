package com.example.diary_my.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.LoaderManager;
import android.app.TimePickerDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;


import com.example.diary_my.R;
import com.example.diary_my.RetrofitApi.APIService;
import com.example.diary_my.RetrofitApi.APIUrl;
import com.example.diary_my.db.Contact_Database;
import com.example.diary_my.db.DBHelper;
import com.example.diary_my.dialogs.Dialog_save_note;
import com.example.diary_my.helper.SharedPrefManager;
import com.example.diary_my.helper.SyncCreateNote;
import com.example.diary_my.helper.SyncNotes;
import com.example.diary_my.helper.SyncUpdateNote;
import com.example.diary_my.models.Result;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class CreateNote extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, Dialog_save_note.NoticeDialogListener {

    private final int DIALOG_SAVE_NOTE = 1;

    private TextInputEditText topicET;
    private TextInputEditText noteET;

    private TextInputLayout topicLayout;
    private TextInputLayout noteLayout;

    public static final String EXTRA_NOTE_ID = "note_id";
    private long noteId;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);

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
                showDialog(DIALOG_SAVE_NOTE);
            }
        });

        topicLayout = (TextInputLayout) findViewById(R.id.TextLayoutTopic);
        noteLayout = (TextInputLayout) findViewById(R.id.TextLayoutNote);

        topicET = (TextInputEditText) findViewById(R.id.topicEditText);
        noteET = (TextInputEditText) findViewById(R.id.noteEditText);

        //  Toolbar toolbar = findViewById(R.id.toolbar);
        // setSupportActionBar(toolbar);


        noteId = getIntent().getLongExtra(EXTRA_NOTE_ID, -1);

        if (noteId != -1) {
            getLoaderManager().initLoader(
                    0, // Идентификатор загрузчика
                    null, // Аргументы
                    this // Callback для событий загрузчика
            );
        }
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                this,  // Контекст
                ContentUris.withAppendedId(Contact_Database.Notes.URI, noteId), // URI
                Contact_Database.Notes.SINGLE_PROJECTION, // Столбцы
                null, // Параметры выборки
                null, // Аргументы выборки
                null // Сортировка по умолчанию
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        Log.i("Test", "Load finished: " + cursor.getCount());

        cursor.setNotificationUri(getContentResolver(), Contact_Database.Notes.URI);

        displayNote(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


    protected Dialog onCreateDialog(int id) {
        if (id == DIALOG_SAVE_NOTE) {
            AlertDialog.Builder adb = new AlertDialog.Builder(this);
            adb.setTitle("Сохранить заметку?");
            adb.setMessage("Сохранить заметку?");
            adb.setPositiveButton(R.string.dialog_save_note_yes, myClickListenerOk);
            adb.setNegativeButton(R.string.dialog_save_note_no, myClickListenerNo);
            return adb.create();
        }
        return super.onCreateDialog(id);
    }

    DialogInterface.OnClickListener myClickListenerOk = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            saveNote();
        }
    };

    DialogInterface.OnClickListener myClickListenerNo = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            finish();
        }
    };


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
        saveNote();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        Log.i("Test", "Dialog delete no");
        finish();
    }


    private void displayNote(Cursor cursor) {
        if (!cursor.moveToFirst()) {
            // Если не получилось перейти к первой строке — завершаем Activity
            finish();
        }

        String title = cursor.getString(cursor.getColumnIndexOrThrow(Contact_Database.Notes.TOPIC_COLUMN));
        String noteText = cursor.getString(cursor.getColumnIndexOrThrow(Contact_Database.Notes.NOTE_COLUMN));

        topicET.setText(title);
        noteET.setText(noteText);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.create_notes, menu);

        return true;
    }


    @SuppressLint("NonConstantResourceId")
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveNote();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    String topicString;
    String noteString;

    private void saveNote() {
        topicString = topicET.getText().toString().trim();
        noteString = noteET.getText().toString().trim();

        boolean isCorrectInput = true;

        if (TextUtils.isEmpty(topicString)) {
            isCorrectInput = false;

            topicLayout.setError(getString(R.string.error_empty_topic));
            topicLayout.setErrorEnabled(true);
        } else {
            topicLayout.setErrorEnabled(false);
        }

        noteLayout.setErrorEnabled(false);

        if (isCorrectInput) {
            //long currentTime = System.currentTimeMillis();

            @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Calendar c = Calendar.getInstance();
            String formattedDate = df.format(c.getTime());

            /////////////////////////////

            long user_id = SharedPrefManager.getInstance(getApplicationContext()).getUser().getId();

            ContentValues contentValues = new ContentValues();
            contentValues.put(Contact_Database.Notes.SYNCHRONIZED, 0);
            contentValues.put(Contact_Database.Notes.TOPIC_COLUMN, topicString);
            contentValues.put(Contact_Database.Notes.NOTE_COLUMN, noteString);
            if (noteId == -1) {
                contentValues.put(Contact_Database.Notes.CREATE_TS_COLUMN, formattedDate);
            }

            contentValues.put(Contact_Database.Notes.UPDATED_TS_COLUMN, formattedDate);


            if (noteId == -1) {
                getContentResolver().insert(Contact_Database.Notes.URI, contentValues);
                SyncCreateNote sync = new SyncCreateNote(this, topicString, noteString, formattedDate, formattedDate, user_id);
                sync.sync_new_note();
                finish();
            } else {
                getContentResolver().update(ContentUris.withAppendedId(Contact_Database.Notes.URI, noteId),
                        contentValues,
                        null,
                        null);

                DBHelper dbHelper = new DBHelper(this);
                SQLiteDatabase db = dbHelper.getWritableDatabase();

                String selectQuery = "SELECT  created_ts, updated_ts FROM " + Contact_Database.Notes.TABLE_NAME + " WHERE " + Contact_Database.Notes._ID + " = " + noteId;
                @SuppressLint("Recycle") Cursor cursor = db.rawQuery(selectQuery, null);
                cursor.moveToFirst();
                int Create_tsColumn = cursor.getColumnIndex(Contact_Database.Notes.CREATE_TS_COLUMN);
                int Update_tsColumn = cursor.getColumnIndex(Contact_Database.Notes.UPDATED_TS_COLUMN);
                SyncUpdateNote sync = new SyncUpdateNote(this, topicString, noteString, cursor.getString(Create_tsColumn), cursor.getString(Update_tsColumn), user_id, noteId);
                sync.sync_note_update();

                finish();
            }
        }
    }
}
