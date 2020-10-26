package com.example.diary_my.models;

import org.json.JSONArray;
import org.json.JSONObject;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;

public class Result_notes {


    @SerializedName("new_notes")
    private String[] new_notes;

    @SerializedName("new_topics")
    private String[] new_topics;

    @SerializedName("new_cr_time")
    private String[] new_created_ts;

    @SerializedName("new_up_time")
    private String[] new_updated_ts;

    @SerializedName("error")
    private boolean err = false;

    @SerializedName("delete_notes_created_ts")
    private String[] delete_notes_created_ts;

    public boolean isErr() {
        return err;
    }

    public String[] getDelete_notes_created_ts() {
        return delete_notes_created_ts;
    }

    public String[] getNew_notes() {
        return new_notes;
    }

    public String[] getNew_topics() {
        return new_topics;
    }

    public String[] getNew_created_ts() {
        return new_created_ts;
    }

    public String[] getNew_updated_ts() {
        return new_updated_ts;
    }

}
