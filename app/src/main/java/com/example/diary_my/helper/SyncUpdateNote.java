package com.example.diary_my.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.diary_my.RetrofitApi.APIService;
import com.example.diary_my.RetrofitApi.APIUrl;
import com.example.diary_my.db.Contact_Database;
import com.example.diary_my.db.DBHelper;
import com.example.diary_my.models.Result;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SyncUpdateNote {

    private Context context;
    private String topic;
    private String note;
    private String created_ts;
    private String updated_ts;
    private long user_id;
    private long note_id;

    public SyncUpdateNote(Context context, String topic, String note, String created_ts, String updated_ts, long user_id, long note_id) {
        this.context = context;
        this.topic = topic;
        this.note = note;
        this.created_ts = created_ts;
        this.updated_ts = updated_ts;
        this.user_id = user_id;
        this.note_id = note_id;
    }

    public void sync_note_update() {
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Retrofit retrofit_server = new Retrofit.Builder()
                .baseUrl(APIUrl.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        APIService service_server = retrofit_server.create(APIService.class);

        Call<Result> call_server = service_server.updateNote(topic, note, created_ts, updated_ts, user_id);

        call_server.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                assert response.body() != null;
                if (!response.body().getError()) {
                    ContentValues cv = new ContentValues();
                    cv.put(Contact_Database.Notes.SYNCHRONIZED, 1);
                    int updCount = db.update(Contact_Database.Notes.TABLE_NAME, cv, Contact_Database.Notes._ID + " = " + note_id,
                           null);
                    db.close();
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {

            }
        });

    }
}
