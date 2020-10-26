package com.example.diary_my.models;

public class Tasks {
    private String[] tasks;

    private String[] descriptions;

    private String[] created_ts;

    private String[] updated_ts;

    private String[] notification_dates;

    private String[] complete;

    private String[] sync_created_ts;

    private long user_id;

    public String[] getTasks() {
        return tasks;
    }

    public String[] getDescriptions() {
        return descriptions;
    }

    public String[] getCreated_ts() {
        return created_ts;
    }

    public String[] getUpdated_ts() {
        return updated_ts;
    }

    public String[] getNotification_dates() {
        return notification_dates;
    }

    public String[] getComplete() {
        return complete;
    }

    public String[] getSync_created_ts() {
        return sync_created_ts;
    }

    public long getUser_id() {
        return user_id;
    }

    public Tasks(String[] tasks, String[] descriptions, String[] created_ts, String[] updated_ts, String[] notification_date, String[] complete, long user_id, String[] sync_created_ts) {
        this.tasks = tasks;
        this.descriptions = descriptions;
        this.created_ts = created_ts;
        this.updated_ts = updated_ts;
        this.notification_dates = notification_date;
        this.complete = complete;
        this.sync_created_ts = sync_created_ts;
        this.user_id = user_id;
    }
}
