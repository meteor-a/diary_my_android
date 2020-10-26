package com.example.diary_my.models;

public class Notes {

    private String[] topics;

    private String[] notes;

    private String[] created_ts;

    private String[] updated_ts;

    private String[] sync_created_ts;

    private long user_id;

    public Notes(String[] topics, String[] notes, String[] created_ts, String[] updated_ts, long user_id, String[] sync_created_ts) {
        this.topics = topics;
        this.notes = notes;
        this.created_ts = created_ts;
        this.updated_ts = updated_ts;
        this.user_id = user_id;
        this.sync_created_ts = sync_created_ts;
    }

    public String[] getTopics() { return topics;}

    public String[] getNotes() { return notes;}

    public String[] getCreated_ts() {
        return created_ts;
    }

    public String[] getUpdated_ts() {
        return updated_ts;
    }

    public long getUser_id() {
        return user_id;
    }
}
