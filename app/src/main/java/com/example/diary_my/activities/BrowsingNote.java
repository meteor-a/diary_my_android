package com.example.diary_my.activities;

import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;


import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;


import com.example.diary_my.R;
import com.example.diary_my.RetrofitApi.APIService;
import com.example.diary_my.RetrofitApi.APIUrl;
import com.example.diary_my.db.Contact_Database;
import com.example.diary_my.db.DBHelper;
import com.example.diary_my.dialogs.Dialog_delete_note;
import com.example.diary_my.helper.SharedPrefManager;
import com.example.diary_my.models.Result;

import java.io.IOException;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class BrowsingNote extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, Dialog_delete_note.NoticeDialogListener{

    private TextView noteTextView;
    public static final String EXTRA_NOTE_ID = "note_id";
    private long noteId;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browsing_note);

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
                finish();
            }
        });

        noteTextView = (TextView) findViewById(R.id.BrowsingNoteText);

        noteId = getIntent().getLongExtra(EXTRA_NOTE_ID, -1);
        if (noteId == -1) {
            finish();
        }

        getLoaderManager().initLoader(
                0, // Идентификатор загрузчика
                null, // Аргументы
                this // Callback для событий загрузчика
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.edit_notes, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                editNote();
                return true;
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_remove:
                DialogFragment DialogDeleteNote;
                DialogDeleteNote = new Dialog_delete_note();
                DialogDeleteNote.show(getSupportFragmentManager(), "dlg2");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onDialogPositiveClick(DialogFragment dialog) throws IOException {
        // User touched the dialog's positive button
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Deleting...");
        progressDialog.show();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIUrl.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        APIService service = retrofit.create(APIService.class);

        long user_id = SharedPrefManager.getInstance(this).getUser().getId();
        String selectQuery = "SELECT  created_ts FROM " + Contact_Database.Notes.TABLE_NAME + " WHERE " + Contact_Database.Notes._ID + " = " + noteId;
        Cursor cursor = db.rawQuery(selectQuery, null);
        int Created_tsColumn = cursor.getColumnIndex(Contact_Database.Notes.CREATE_TS_COLUMN);
        cursor.moveToFirst();

        Call<Result> call = service.deleteNote(cursor.getString(Created_tsColumn), user_id);

        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                if (!response.body().getError()) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(Contact_Database.Notes._ID, noteId);
                    getContentResolver().delete(ContentUris.withAppendedId(Contact_Database.Notes.URI, noteId), null, null);
                    progressDialog.dismiss();
                    db.close();
                    finish();
                }

            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                progressDialog.dismiss();
            }
        });



    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        Log.i("Test", "Dialog delete no");
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

    private void editNote() {
        Intent EditNoteIntent = new Intent(this, CreateNote.class);
        EditNoteIntent.putExtra(CreateNote.EXTRA_NOTE_ID, noteId);

        startActivity(EditNoteIntent);
    }

    private void displayNote(Cursor cursor) {
        if (!cursor.moveToFirst()) {
            // Если не получилось перейти к первой строке — завершаем Activity
            finish();
        }

        String title = cursor.getString(cursor.getColumnIndexOrThrow(Contact_Database.Notes.TOPIC_COLUMN));
        String noteText = cursor.getString(cursor.getColumnIndexOrThrow(Contact_Database.Notes.NOTE_COLUMN));

        setTitle(title);
        noteTextView.setText(noteText);
    }
}
