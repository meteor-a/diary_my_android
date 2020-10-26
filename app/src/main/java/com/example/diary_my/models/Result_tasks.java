package com.example.diary_my.models;

import com.google.gson.annotations.SerializedName;

public class Result_tasks {
    @SerializedName("new_tasks")
    private String[] new_tasks;

    @SerializedName("new_descriptions")
    private String[] new_descriptions;

    @SerializedName("new_cr_time")
    private String[] new_created_ts;

    @SerializedName("new_up_time")
    private String[] new_updated_ts;

    @SerializedName("error")
    private boolean err = false;

    @SerializedName("delete_tasks_created_ts")
    private String[] delete_tasks_created_ts;

    @SerializedName("new_complete")
    private String[] new_complete;

    @SerializedName("new_notification_dates")
    private String[] new_notification_date;


    public String[] getNew_tasks() {
        return new_tasks;
    }

    public String[] getNew_descriptions() {
        return new_descriptions;
    }

    public String[] getNew_created_ts() {
        return new_created_ts;
    }

    public String[] getNew_updated_ts() {
        return new_updated_ts;
    }

    public boolean isErr() {
        return err;
    }

    public String[] getDelete_tasks_created_ts() {
        return delete_tasks_created_ts;
    }

    public String[] getNew_complete() {
        return new_complete;
    }

    public String[] getNew_notification_date() {
        return new_notification_date;
    }
}
