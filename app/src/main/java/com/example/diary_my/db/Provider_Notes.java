package com.example.diary_my.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

public class Provider_Notes extends ContentProvider {

    private static DBHelper my_notesDbHelper;
    private DBHelper notesDbHelper;

    private static final int NOTES = 1;
    private static final int NOTE = 2;

    private static final int TASKS = 3;
    private static final int TASK = 4;


    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        URI_MATCHER.addURI(Contact_Database.AUTHORITY, "notes", NOTES);
        URI_MATCHER.addURI(Contact_Database.AUTHORITY, "notes/#", NOTE);
        URI_MATCHER.addURI(Contact_Database.AUTHORITY, "tasks/", TASKS);
        URI_MATCHER.addURI(Contact_Database.AUTHORITY, "tasks/#", TASK);
    }

    @Override
    public boolean onCreate() {
        notesDbHelper = new DBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (URI_MATCHER.match(uri)) {
            case NOTES:
                return Contact_Database.Notes.URI_TYPE_NOTE_DIR;
            case NOTE:
                return Contact_Database.Notes.URI_TYPE_NOTE_ITEM;
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
        SQLiteDatabase db = notesDbHelper.getReadableDatabase();
        switch (URI_MATCHER.match(uri)) {
            case NOTES:
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = Contact_Database.Notes.UPDATED_TS_COLUMN + " DESC";
                }
                return db.query(Contact_Database.Notes.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
            case NOTE:
                String id = uri.getLastPathSegment();

                if (TextUtils.isEmpty(selection)) {
                    selection = Contact_Database.Notes._ID + " = ?";
                    selectionArgs = new String[]{id};
                } else {
                    selection = selection + " AND " + Contact_Database.Notes._ID + " = ?";

                    String[] newSelectionArgs = new String[selectionArgs.length + 1];

                    System.arraycopy(selectionArgs, 0, newSelectionArgs, 0, selectionArgs.length);

                    newSelectionArgs[newSelectionArgs.length - 1] = id;

                    selectionArgs = newSelectionArgs;
                }

                return db.query(Contact_Database.Notes.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
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
                String id_task = uri.getLastPathSegment();

                if (TextUtils.isEmpty(selection)) {
                    selection = Contact_Database.Tasks._ID + " = ?";
                    selectionArgs = new String[]{id_task};
                } else {
                    selection = selection + " AND " + Contact_Database.Tasks._ID + " = ?";

                    String[] newSelectionArgs = new String[selectionArgs.length + 1];

                    System.arraycopy(selectionArgs, 0, newSelectionArgs, 0, selectionArgs.length);

                    newSelectionArgs[newSelectionArgs.length - 1] = id_task;

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
        SQLiteDatabase db = notesDbHelper.getWritableDatabase();

        switch (URI_MATCHER.match(uri)) {
            case NOTES:
                long rowId = db.insert(Contact_Database.Notes.TABLE_NAME,
                        null,
                        contentValues);

                if (rowId > 0) {
                    Uri noteUri = ContentUris.withAppendedId(Contact_Database.Notes.URI, rowId);
                    getContext().getContentResolver().notifyChange(uri, null);
                    return noteUri;
                }

                return null;
            case TASKS:
                long rowId_task = db.insert(Contact_Database.Tasks.TABLE_NAME,
                        null,
                        contentValues);

                if (rowId_task > 0) {
                    Uri noteUri = ContentUris.withAppendedId(Contact_Database.Tasks.URI, rowId_task);
                    getContext().getContentResolver().notifyChange(uri, null);
                    return noteUri;
                }

                return null;
            default:
                return null;
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = notesDbHelper.getWritableDatabase();

        switch (URI_MATCHER.match(uri)) {
            case NOTE:
                String noteId = uri.getLastPathSegment();

                if (TextUtils.isEmpty(selection)) {
                    selection = Contact_Database.Notes._ID + " = ?";
                    selectionArgs = new String[]{noteId};
                } else {
                    selection = selection + " AND " + Contact_Database.Notes._ID + " = ?";
                    String[] newSelectionArgs = new String[selectionArgs.length + 1];
                    System.arraycopy(selectionArgs, 0, newSelectionArgs, 0, selectionArgs.length);
                    newSelectionArgs[newSelectionArgs.length - 1] = noteId;
                    selectionArgs = newSelectionArgs;
                }

                int noteRowsUpdated = db.delete(Contact_Database.Notes.TABLE_NAME, selection, selectionArgs);

                //getContext().getContentResolver().notifyChange(uri, null);

                return noteRowsUpdated;
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
        SQLiteDatabase db = notesDbHelper.getWritableDatabase();

        switch (URI_MATCHER.match(uri)) {

            case NOTE:
                String id = uri.getLastPathSegment();

                if (TextUtils.isEmpty(selection)) {
                    selection = Contact_Database.Notes._ID + " = ?";
                    selectionArgs = new String[]{id};
                } else {
                    selection = selection + " AND " + Contact_Database.Notes._ID + " = ?";
                    String[] newSelectionArgs = new String[selectionArgs.length + 1];
                    System.arraycopy(selectionArgs, 0, newSelectionArgs, 0, selectionArgs.length);
                    newSelectionArgs[newSelectionArgs.length - 1] = id;
                    selectionArgs = newSelectionArgs;
                }

                int rowsUpdated = db.update(Contact_Database.Notes.TABLE_NAME, contentValues, selection, selectionArgs);

                Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri, null);

                return rowsUpdated;
            case TASK:
                String id_task = uri.getLastPathSegment();

                if (TextUtils.isEmpty(selection)) {
                    selection = Contact_Database.Tasks._ID + " = ?";
                    selectionArgs = new String[]{id_task};
                } else {
                    selection = selection + " AND " + Contact_Database.Tasks._ID + " = ?";
                    String[] newSelectionArgs = new String[selectionArgs.length + 1];
                    System.arraycopy(selectionArgs, 0, newSelectionArgs, 0, selectionArgs.length);
                    newSelectionArgs[newSelectionArgs.length - 1] = id_task;
                    selectionArgs = newSelectionArgs;
                }

                int task_rowsUpdated = db.update(Contact_Database.Tasks.TABLE_NAME, contentValues, selection, selectionArgs);

                Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri, null);

                return task_rowsUpdated;
        }

        return 0;
    }

}
