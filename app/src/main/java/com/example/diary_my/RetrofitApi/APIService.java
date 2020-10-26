package com.example.diary_my.RetrofitApi;

import com.example.diary_my.models.Notes;
import com.example.diary_my.models.Result;
import com.example.diary_my.models.Result_notes;
import com.example.diary_my.models.Result_tasks;
import com.example.diary_my.models.Tasks;

import org.json.JSONArray;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    //The register call
    @FormUrlEncoded
    @POST("register")
    Call<Result> createUser(
            @Field("name") String name,
            @Field("email") String email,
            @Field("password") String password
            );

    //the signin call
    @FormUrlEncoded
    @POST("login")
    Call<Result> userLogin(
            @Field("email") String email,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("update_password")
    Call<Result> updatePassword(
            @Field("email") String email,
            @Field("password_old") String password_old,
            @Field("password_new") String password_new
    );

    @FormUrlEncoded
    @POST("savenote")
    Call<Result> saveNote(
            @Field("topic") String topic,
            @Field("note") String note,
            @Field("created_ts") String created_ts,
            @Field("updated_ts") String updated_ts,
            @Field("user_id") long user_id
    );


    @FormUrlEncoded
    @POST("savetask")
    Call<Result> saveTask(
            @Field("task") String task,
            @Field("description") String description,
            @Field("created_ts") String created_ts,
            @Field("updated_ts") String updated_ts,
            @Field("notification_date") String notification_date,
            @Field("complete") int complete,
            @Field("user_id") long user_id
    );

    @FormUrlEncoded
    @POST("deletetask")
    Call<Result> deleteTask(
            @Field("created_ts") String created_ts,
            @Field("user_id") long user_id
    );


    @FormUrlEncoded
    @POST("updatenote")
    Call<Result> updateNote(
            @Field("topic") String topic,
            @Field("note") String note,
            @Field("created_ts") String created_ts,
            @Field("updated_ts") String updated_ts,
            @Field("user_id") long user_id
    );

    @FormUrlEncoded
    @POST("updatetask")
    Call<Result> updateTask(
            @Field("task") String task,
            @Field("description") String description,
            @Field("created_ts") String created_ts,
            @Field("updated_ts") String updated_ts,
            @Field("notification_date") String notification_date,
            @Field("complete") long complete,
            @Field("user_id") long user_id
    );

    @FormUrlEncoded
    @POST("deletenote")
    Call<Result> deleteNote(
            @Field("created_ts") String created_ts,
            @Field("user_id") long user_id
    );

    @Headers("Content-Type: application/json")
    @POST("syncnotes")
    Call<Result_notes> SyncNotes(
            @Body Notes body
    );

    @Headers("Content-Type: application/json")
    @POST("synctasks")
    Call<Result_tasks> SyncTasks(
            @Body Tasks body
    );

    @FormUrlEncoded
    @POST("syncchecked")
    Call<Result> SyncTaskChecked(
            @Field("user_id") long user_id,
            @Field("created_ts") String created_ts,
            @Field("updated_ts") String updated_ts,
            @Field("complete") long complete
    );
}