package com.example.diary_my.helper;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


import androidx.annotation.NonNull;

import com.example.diary_my.RetrofitApi.APIService;
import com.example.diary_my.RetrofitApi.APIUrl;
import com.example.diary_my.db.Contact_Database;
import com.example.diary_my.db.DBHelper;
import com.example.diary_my.models.Notes;
import com.example.diary_my.models.Result_notes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class SyncNotes {

    private  Context context;

    public SyncNotes(Context context) {
        this.context = context;
    }

    public void sync() {
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String query_rows = "SELECT  * FROM " + Contact_Database.Notes.TABLE_NAME;
        Cursor c = db.rawQuery(query_rows, null);
        int num = c.getCount();

        String[] tmp_topics = new String[num];
        String[] tmp_notes = new String[num];
        String[] tmp_created_ts = new String[num];
        String[] tmp_updated_ts = new String[num];
        String[] tmp_sync_created_ts = new String[num];

        int TopicColumn = c.getColumnIndex(Contact_Database.Notes.TOPIC_COLUMN);
        int NoteColumn = c.getColumnIndex(Contact_Database.Notes.NOTE_COLUMN);
        int CreatedColumn = c.getColumnIndex(Contact_Database.Notes.CREATE_TS_COLUMN);
        int UpdatedColumn = c.getColumnIndex(Contact_Database.Notes.UPDATED_TS_COLUMN);
        int SyncColumn = c.getColumnIndex(Contact_Database.Notes.SYNCHRONIZED);

        long user_id = SharedPrefManager.getInstance(context).getUser().getId();
        int count_no_sync = 0;
        int count_sync = 0;
        if (c.moveToFirst()) {
            do {
                if (c.getInt(SyncColumn) == 0) {
                    tmp_topics[count_no_sync] = c.getString(TopicColumn);
                    tmp_notes[count_no_sync] = c.getString(NoteColumn);
                    tmp_created_ts[count_no_sync] = c.getString(CreatedColumn);
                    tmp_updated_ts[count_no_sync] = c.getString(UpdatedColumn);
                    ++count_no_sync;
                } else {
                    tmp_sync_created_ts[count_sync] = c.getString(CreatedColumn);
                    ++count_sync;
                }
            } while (c.moveToNext());
        }

        Notes notes = new Notes(tmp_topics, tmp_notes, tmp_created_ts, tmp_updated_ts, user_id, tmp_sync_created_ts);

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit_server = new Retrofit.Builder()
                .baseUrl(APIUrl.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        APIService service_server = retrofit_server.create(APIService.class);
        Call call_server = service_server.SyncNotes(notes);
        int finalCount = count_no_sync;
        call_server.enqueue(new Callback<Result_notes>() {
            @Override
            public void onResponse(@NonNull Call<Result_notes> call, @NonNull Response<Result_notes> response) {
                assert response.body() != null;
                String[] new_notes = response.body().getNew_notes();
                int num_new_notes = new_notes.length;
                ContentValues cv = new ContentValues();
                for (int ii = 0; ii < num_new_notes; ++ii) {
                    String selectRows = "SELECT " + Contact_Database.Notes._ID + " FROM " + Contact_Database.Notes.TABLE_NAME + " WHERE " + Contact_Database.Notes.CREATE_TS_COLUMN + " = ?";
                    @SuppressLint("Recycle") Cursor cursor = db.rawQuery(selectRows, new String[]{response.body().getNew_created_ts()[ii]});
                    if (cursor.getCount() != 0) {
                        cv.put(Contact_Database.Notes.TOPIC_COLUMN, response.body().getNew_topics()[ii]);
                        cv.put(Contact_Database.Notes.NOTE_COLUMN, response.body().getNew_notes()[ii]);
                        cv.put(Contact_Database.Notes.UPDATED_TS_COLUMN, response.body().getNew_updated_ts()[ii]);
                        cv.put(Contact_Database.Notes.SYNCHRONIZED, 1);
                        int updCount = db.update(Contact_Database.Notes.TABLE_NAME, cv, Contact_Database.Notes.CREATE_TS_COLUMN + " = ?",
                                new String[]{response.body().getNew_created_ts()[ii]});
                    } else {
                        cv.put(Contact_Database.Notes.TOPIC_COLUMN, response.body().getNew_topics()[ii]);
                        cv.put(Contact_Database.Notes.NOTE_COLUMN, response.body().getNew_notes()[ii]);
                        cv.put(Contact_Database.Notes.UPDATED_TS_COLUMN, response.body().getNew_updated_ts()[ii]);
                        cv.put(Contact_Database.Notes.CREATE_TS_COLUMN, response.body().getNew_created_ts()[ii]);
                        cv.put(Contact_Database.Notes.SYNCHRONIZED, 1);
                        db.insert(Contact_Database.Notes.TABLE_NAME, null, cv);
                    }
                    cv.clear();
                }
                if (!response.body().isErr()) {
                    for (int ii = 0; ii < finalCount; ++ii) {
                        cv.put(Contact_Database.Notes.SYNCHRONIZED, 1);
                        int updCount = db.update(Contact_Database.Notes.TABLE_NAME, cv, Contact_Database.Notes.CREATE_TS_COLUMN + " = ?",
                                new String[]{notes.getCreated_ts()[ii]});
                    }
                }
                String[] for_delete_notes_created_ts = response.body().getDelete_notes_created_ts();
                if (for_delete_notes_created_ts != null) {
                    int num_delete_notes = for_delete_notes_created_ts.length;
                    for (int ii = 0; ii < num_delete_notes; ++ii) {
                        db.delete(Contact_Database.Notes.TABLE_NAME, Contact_Database.Notes.CREATE_TS_COLUMN + " = ?", new String[]{for_delete_notes_created_ts[ii]});
                    }
                }
                db.close();
            }
            @Override
            public void onFailure(Call<Result_notes> call, Throwable t) {
            }
        });
    }
}
