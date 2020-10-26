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

public class SyncUpdateTask {
    private Context context;
    private String task;
    private String description;
    private String created_ts;
    private String updated_ts;
    private String notification_date;
    private long complete;
    private long user_id;
    private long task_id;

    public SyncUpdateTask(Context context, String task, String description, String created_ts, String updated_ts, String notification_date, long complete, long user_id, long task_id) {
        this.context = context;
        this.task = task;
        this.description = description;
        this.created_ts = created_ts;
        this.updated_ts = updated_ts;
        this.notification_date = notification_date;
        this.complete = complete;
        this.user_id = user_id;
        this.task_id = task_id;
    }

    public void sync_task_update() {
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Retrofit retrofit_server = new Retrofit.Builder()
                .baseUrl(APIUrl.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        APIService service_server = retrofit_server.create(APIService.class);

        Call<Result> call_server = service_server.updateTask(task, description, created_ts, updated_ts, notification_date, complete, user_id);

        call_server.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                assert response.body() != null;
                if (!response.body().getError()) {
                    ContentValues cv = new ContentValues();
                    cv.put(Contact_Database.Tasks.SYNCHRONIZED, 1);
                    int updCount = db.update(Contact_Database.Tasks.TABLE_NAME, cv, Contact_Database.Tasks._ID + " = " + task_id,
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
