package com.example.diary_my.helper;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.diary_my.RetrofitApi.APIService;
import com.example.diary_my.RetrofitApi.APIUrl;
import com.example.diary_my.db.Contact_Database;
import com.example.diary_my.db.DBHelper;
import com.example.diary_my.models.Result_tasks;
import com.example.diary_my.models.Tasks;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SyncTasks {
    private Context context;

    public SyncTasks(Context context) {
        this.context = context;
    }

    public void sync_tasks() {
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String query_rows = "SELECT  * FROM " + Contact_Database.Tasks.TABLE_NAME;
        Cursor c = db.rawQuery(query_rows, null);
        int num = c.getCount();

        int count_no_sync = 0;
        int count_sync = 0;
        String[] tmp_tasks = new String[num];
        String[] tmp_descriptions = new String[num];
        String[] tmp_created_ts = new String[num];
        String[] tmp_updated_ts = new String[num];
        String[] tmp_notification_date = new String[num];
        String[] tmp_complete = new String[num];
        String[] tmp_sync_created_ts = new String[num];

        int TaskColumn = c.getColumnIndex(Contact_Database.Tasks.TASK_COLUMN);
        int DescriptionColumn = c.getColumnIndex(Contact_Database.Tasks.DESCRIPTION_COLUMN);
        int CreatedColumn = c.getColumnIndex(Contact_Database.Tasks.CREATE_TS_COLUMN);
        int UpdatedColumn = c.getColumnIndex(Contact_Database.Tasks.UPDATED_TS_COLUMN);
        int NotificationColumn = c.getColumnIndex(Contact_Database.Tasks.NOTIFICATION_DATE_COLUMN);
        int CompleteColumn = c.getColumnIndex(Contact_Database.Tasks.COMPLETE);
        int SyncColumn = c.getColumnIndex(Contact_Database.Notes.SYNCHRONIZED);

        long user_id = SharedPrefManager.getInstance(context).getUser().getId();
        if (c.moveToFirst()) {
            do {
                if (c.getInt(SyncColumn) == 0) {
                    tmp_tasks[count_no_sync] = c.getString(TaskColumn);
                    tmp_descriptions[count_no_sync] = c.getString(DescriptionColumn);
                    tmp_created_ts[count_no_sync] = c.getString(CreatedColumn);
                    tmp_updated_ts[count_no_sync] = c.getString(UpdatedColumn);
                    tmp_notification_date[count_no_sync] = c.getString(NotificationColumn);
                    tmp_complete[count_no_sync] = c.getString(CompleteColumn);
                    ++count_no_sync;
                } else {
                    tmp_sync_created_ts[count_sync] = c.getString(CreatedColumn);
                    ++count_sync;
                }
            } while (c.moveToNext());
        }

        Tasks tasks = new Tasks(tmp_tasks, tmp_descriptions, tmp_created_ts, tmp_updated_ts, tmp_notification_date, tmp_complete, user_id, tmp_sync_created_ts);

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit_server = new Retrofit.Builder()
                .baseUrl(APIUrl.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();


        APIService service_server = retrofit_server.create(APIService.class);
        Call call_server = service_server.SyncTasks(tasks);
        int finalCount = count_no_sync;
        call_server.enqueue(new Callback<Result_tasks>() {
            @Override
            public void onResponse(@NonNull Call<Result_tasks> call, @NonNull Response<Result_tasks> response) {
                assert response.body() != null;
                String[] new_tasks = response.body().getNew_tasks();
                int num_new_tasks = new_tasks.length;
                ContentValues cv = new ContentValues();
                for (int ii = 0; ii < num_new_tasks; ++ii) {
                    String selectRows = "SELECT " + Contact_Database.Tasks._ID + " FROM " + Contact_Database.Tasks.TABLE_NAME + " WHERE " + Contact_Database.Tasks.CREATE_TS_COLUMN + " = ?";
                    @SuppressLint("Recycle") Cursor cursor = db.rawQuery(selectRows, new String[]{response.body().getNew_created_ts()[ii]});
                    if (cursor.getCount() != 0) {
                        cv.put(Contact_Database.Tasks.TASK_COLUMN, response.body().getNew_tasks()[ii]);
                        cv.put(Contact_Database.Tasks.DESCRIPTION_COLUMN, response.body().getNew_descriptions()[ii]);
                        cv.put(Contact_Database.Tasks.UPDATED_TS_COLUMN, response.body().getNew_updated_ts()[ii]);
                        cv.put(Contact_Database.Tasks.NOTIFICATION_DATE_COLUMN, response.body().getNew_notification_date()[ii]);
                        cv.put(Contact_Database.Tasks.COMPLETE, response.body().getNew_complete()[ii]);
                        cv.put(Contact_Database.Tasks.ALARM, 0);
                        cv.put(Contact_Database.Tasks.SYNCHRONIZED, 1);
                        int updCount = db.update(Contact_Database.Tasks.TABLE_NAME, cv, Contact_Database.Tasks.CREATE_TS_COLUMN + " = ?",
                                new String[]{response.body().getNew_created_ts()[ii]});
                    } else {
                        cv.put(Contact_Database.Tasks.TASK_COLUMN, response.body().getNew_tasks()[ii]);
                        cv.put(Contact_Database.Tasks.DESCRIPTION_COLUMN, response.body().getNew_descriptions()[ii]);
                        cv.put(Contact_Database.Tasks.UPDATED_TS_COLUMN, response.body().getNew_updated_ts()[ii]);
                        cv.put(Contact_Database.Tasks.CREATE_TS_COLUMN, response.body().getNew_created_ts()[ii]);
                        cv.put(Contact_Database.Tasks.NOTIFICATION_DATE_COLUMN, response.body().getNew_notification_date()[ii]);
                        cv.put(Contact_Database.Tasks.COMPLETE, response.body().getNew_complete()[ii]);
                        cv.put(Contact_Database.Tasks.ALARM, 0);
                        cv.put(Contact_Database.Tasks.SYNCHRONIZED, 1);
                        db.insert(Contact_Database.Tasks.TABLE_NAME, null, cv);
                    }
                    cv.clear();
                }
                if (!response.body().isErr()) {
                    for (int ii = 0; ii < finalCount; ++ii) {
                        cv.put(Contact_Database.Tasks.SYNCHRONIZED, 1);
                        int updCount = db.update(Contact_Database.Tasks.TABLE_NAME, cv, Contact_Database.Tasks.CREATE_TS_COLUMN + " = ?",
                                new String[]{tasks.getCreated_ts()[ii]});
                    }
                }
                String[] for_delete_tasks_created_ts = response.body().getDelete_tasks_created_ts();
                if (for_delete_tasks_created_ts != null) {
                    int num_delete_tasks = for_delete_tasks_created_ts.length;
                    for (int ii = 0; ii < num_delete_tasks; ++ii) {
                        db.delete(Contact_Database.Tasks.TABLE_NAME, Contact_Database.Tasks.CREATE_TS_COLUMN + " = ?", new String[]{for_delete_tasks_created_ts[ii]});
                    }
                }
                db.close();
            }

            @Override
            public void onFailure(Call<Result_tasks> call, Throwable t) {
            }
        });
    }
}
