package com.example.diary_my.db_timemanager;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.diary_my.db.Contact_Database;
import com.example.diary_my.db.DBHelper;

import java.util.Objects;

public class Provider_Tasks extends ContentProvider {

    private DBHelper tasksDbHelper;

    private static final int TASKS = 1;
    private static final int TASK = 2;


    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        URI_MATCHER.addURI(Contact_Database.AUTHORITY, "tasks", TASKS);
        URI_MATCHER.addURI(Contact_Database.AUTHORITY, "tasks/#", TASK);
    }

    @Override
    public boolean onCreate() {
        tasksDbHelper = new DBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (URI_MATCHER.match(uri)) {
            case TASKS:
                return Contact_Database.Tasks.URI_TYPE_TASK_DIR;
            case TASK:
                return Contact_Database.Tasks.URI_TYPE_TASK_ITEM;
            default:
                return null;
        }
    }

    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase db = tasksDbHelper.getReadableDatabase();
        switch (URI_MATCHER.match(uri)) {
            case TASKS:
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = Contact_Database.Tasks.UPDATED_TS_COLUMN + " DESC";
                }
                return db.query(Contact_Database.Tasks.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
            case TASK:
                String id = uri.getLastPathSegment();

                if (TextUtils.isEmpty(selection)) {
                    selection = Contact_Database.Tasks._ID + " = ?";
                    selectionArgs = new String[]{id};
                } else {
                    selection = selection + " AND " + Contact_Database.Tasks._ID + " = ?";

                    String[] newSelectionArgs = new String[selectionArgs.length + 1];

                    System.arraycopy(selectionArgs, 0, newSelectionArgs, 0, selectionArgs.length);

                    newSelectionArgs[newSelectionArgs.length - 1] = id;

                    selectionArgs = newSelectionArgs;
                }

                return db.query(Contact_Database.Tasks.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);

            default:
                return null;
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        SQLiteDatabase db = tasksDbHelper.getWritableDatabase();

        switch (URI_MATCHER.match(uri)) {
            case TASKS:
                long rowId = db.insert(Contact_Database.Tasks.TABLE_NAME,
                        null,
                        contentValues);

                if (rowId > 0) {
                    Uri taskUri = ContentUris.withAppendedId(Contact_Database.Tasks.URI, rowId);
                    getContext().getContentResolver().notifyChange(uri, null);
                    return taskUri;
                }

                return null;

            default:
                return null;
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = tasksDbHelper.getWritableDatabase();

        switch (URI_MATCHER.match(uri)) {
            case TASK:
                String taskId = uri.getLastPathSegment();

                if (TextUtils.isEmpty(selection)) {
                    selection = Contact_Database.Tasks._ID + " = ?";
                    selectionArgs = new String[]{taskId};
                } else {
                    selection = selection + " AND " + Contact_Database.Tasks._ID + " = ?";
                    String[] newSelectionArgs = new String[selectionArgs.length + 1];
                    System.arraycopy(selectionArgs, 0, newSelectionArgs, 0, selectionArgs.length);
                    newSelectionArgs[newSelectionArgs.length - 1] = taskId;
                    selectionArgs = newSelectionArgs;
                }

                int taskRowsUpdated = db.delete(Contact_Database.Tasks.TABLE_NAME, selection, selectionArgs);

                //getContext().getContentResolver().notifyChange(uri, null);

                return taskRowsUpdated;

        }

        return 0;

    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues,
                      @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = tasksDbHelper.getWritableDatabase();

        switch (URI_MATCHER.match(uri)) {

            case TASK:
                String id = uri.getLastPathSegment();

                if (TextUtils.isEmpty(selection)) {
                    selection = Contact_Database.Tasks._ID + " = ?";
                    selectionArgs = new String[]{id};
                } else {
                    selection = selection + " AND " + Contact_Database.Tasks._ID + " = ?";
                    String[] newSelectionArgs = new String[selectionArgs.length + 1];
                    System.arraycopy(selectionArgs, 0, newSelectionArgs, 0, selectionArgs.length);
                    newSelectionArgs[newSelectionArgs.length - 1] = id;
                    selectionArgs = newSelectionArgs;
                }

                int rowsUpdated = db.update(Contact_Database.Tasks.TABLE_NAME, contentValues, selection, selectionArgs);

                Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri, null);

                return rowsUpdated;

        }

        return 0;
    }

}
