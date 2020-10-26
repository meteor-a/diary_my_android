package com.example.diary_my.models;

import com.google.gson.annotations.SerializedName;

public class TaskCheckedModel {
    public long getComplete() {
        return complete;
    }

    public void setComplete(long complete) {
        this.complete = complete;
    }

    public String getCreated_ts() {
        return created_ts;
    }

    public void setCreated_ts(String created_ts) {
        this.created_ts = created_ts;
    }

    public long getUser_id() {
        return user_id;
    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }

    public String getUpdated_ts() {
        return updated_ts;
    }

    public void setUpdated_ts(String updated_ts) {
        this.updated_ts = updated_ts;
    }

    public TaskCheckedModel(long complete, String created_ts, String updated_ts, long user_id) {
        this.complete = complete;
        this.created_ts = created_ts;
        this.updated_ts = updated_ts;
        this.user_id = user_id;
    }

    @SerializedName("complete")
    private long complete;

    @SerializedName("created_ts")
    private String created_ts;

    @SerializedName("updated_ts")
    private String updated_ts;

    @SerializedName("user_id")
    private long user_id;
}
