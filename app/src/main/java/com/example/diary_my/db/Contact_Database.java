package com.example.diary_my.db;

import android.net.Uri;
import android.provider.BaseColumns;

public final class Contact_Database {


    public static final String DB_NAME = "notes.db";
    public static final int DB_VERSION = 1;

    public static final String AUTHORITY = "com.example.diary_my.provider";
    public static final String URI = "content://" + AUTHORITY;

    // массив запросов

    public static final String[] CREATE_DATABASE_QUERIES = {
            Tasks.CREATE_TABLE, Notes.CREATE_TABLE,
            Notes.CREATE_UPDATED_TS_INDEX
    };

    private Contact_Database() {
    }

    public static abstract class Notes implements BaseColumns {

        // Поля таблицы
        public static String TABLE_NAME = "notes";

        public static final Uri URI = Uri.parse(Contact_Database.URI + "/" + TABLE_NAME);



        // Список заметок
        public static final String URI_TYPE_NOTE_DIR = "vnd.android.cursor.dir/vnd.diary_my.note";

        // Одна заметка
        public static final String URI_TYPE_NOTE_ITEM = "vnd.android.cursor.item/vnd.diary_my.note";
        public static final String TOPIC_COLUMN = "topic";
        public static final String NOTE_COLUMN = "notes";
        public static final String CREATE_TS_COLUMN = "created_ts";
        public static final String UPDATED_TS_COLUMN = "updated_ts";
        public static final String SYNCHRONIZED = "synchronized";

        // создание таблицы

        public static final String CREATE_TABLE = String.format("CREATE TABLE %s " +
                        "(%s INTEGER PRIMARY KEY, " +
                        "%s TEXT NOT NULL, " +
                        "%s TEXT, " +
                        "%s TEXT NOT NULL, " +
                        "%s TEXT NOT NULL, " +
                        "%s INTEGER);",
                TABLE_NAME,
                _ID,
                TOPIC_COLUMN,
                NOTE_COLUMN,
                CREATE_TS_COLUMN,
                UPDATED_TS_COLUMN,
                SYNCHRONIZED);

        //

        public static final String CREATE_UPDATED_TS_INDEX = String.format("CREATE INDEX updated_ts_index " +
                        "ON %s (%s);",
                TABLE_NAME,
                UPDATED_TS_COLUMN);

        public static final String[] LIST_PROJECTION = {
                _ID,
                TOPIC_COLUMN,
                CREATE_TS_COLUMN,
                UPDATED_TS_COLUMN,
                SYNCHRONIZED
        };

        public static final String[] SINGLE_PROJECTION = {
                _ID,
                TOPIC_COLUMN,
                NOTE_COLUMN,
                CREATE_TS_COLUMN,
                UPDATED_TS_COLUMN,
                SYNCHRONIZED
        };
    }



    public static abstract class Tasks implements BaseColumns {
        public static String TABLE_NAME = "tasks";

        public static final Uri URI = Uri.parse(Contact_Database.URI + "/" + TABLE_NAME);



        // Список заметок
        public static final String URI_TYPE_TASK_DIR = "vnd.android.cursor.dir/vnd.diary_my.note";

        // Одна заметка
        public static final String URI_TYPE_TASK_ITEM = "vnd.android.cursor.item/vnd.diary_my.note";
        public static final String TASK_COLUMN = "task";
        public static final String DESCRIPTION_COLUMN = "description";
        public static final String CREATE_TS_COLUMN = "created_ts";
        public static final String UPDATED_TS_COLUMN = "updated_ts";
        public static final String NOTIFICATION_DATE_COLUMN = "notification_date";
        public static final String SYNCHRONIZED = "synchronized";
        public static final String COMPLETE = "complete";
        public static final String ALARM = "alarm";


        public static final String CREATE_TABLE = String.format("CREATE TABLE %s " +
                        "(%s INTEGER PRIMARY KEY, " +
                        "%s TEXT NOT NULL, " +
                        "%s TEXT, " +
                        "%s TEXT NOT NULL, " +
                        "%s TEXT NOT NULL, " +
                        "%s TEXT NOT NULL, " +
                        "%s INTEGER, " +
                        "%s INTEGER, " +
                        "%s INTEGER);",
                TABLE_NAME,
                _ID,
                TASK_COLUMN,
                DESCRIPTION_COLUMN,
                CREATE_TS_COLUMN,
                UPDATED_TS_COLUMN,
                NOTIFICATION_DATE_COLUMN,
                COMPLETE,
                ALARM,
                SYNCHRONIZED);

        //

        public static final String[] LIST_PROJECTION = {
                _ID,
                TASK_COLUMN,
                CREATE_TS_COLUMN,
                UPDATED_TS_COLUMN,
                NOTIFICATION_DATE_COLUMN,
                COMPLETE,
                ALARM,
                SYNCHRONIZED
        };

        public static final String[] SINGLE_PROJECTION = {
                _ID,
                TASK_COLUMN,
                DESCRIPTION_COLUMN,
                CREATE_TS_COLUMN,
                UPDATED_TS_COLUMN,
                NOTIFICATION_DATE_COLUMN,
                COMPLETE,
                ALARM,
                SYNCHRONIZED
        };

    }
}
