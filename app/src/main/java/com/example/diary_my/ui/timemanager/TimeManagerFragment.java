package com.example.diary_my.ui.timemanager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import android.app.Fragment;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.diary_my.RetrofitApi.APIService;
import com.example.diary_my.RetrofitApi.APIUrl;
import com.example.diary_my.TimerManager;
import com.example.diary_my.activities.BrowsingTask;
import com.example.diary_my.activities.CteateTask;
import com.example.diary_my.R;
import com.example.diary_my.activities.HomeActivity;
import com.example.diary_my.db.Contact_Database;
import com.example.diary_my.db.DBHelper;
import com.example.diary_my.dialogs.Dialog_sort_task;
import com.example.diary_my.helper.SharedPrefManager;
import com.example.diary_my.helper.SyncTasks;
import com.example.diary_my.models.Result;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@RequiresApi(api = Build.VERSION_CODES.M)
public class TimeManagerFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, LoaderManager.LoaderCallbacks<Cursor>{

    FloatingActionButton Add_task;
    private SwipeRefreshLayout mSwipeRefreshLayout_timemanager;
    private TimeManagerAdapter tasksadapter;
    private FragmentActivity myContext;

    private SearchView searchView = null;
    private SearchView.OnQueryTextListener queryTextListener;

    private String SELECTION_SEARCH = null;
    public String SELECTION_ARGS_SEARCH = null;

    public TimerManager timerManager;

    @RequiresApi(api = Build.VERSION_CODES.M)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_timemanager, container, false);

        timerManager = new TimerManager(getContext());
        RecyclerView recyclerView = root.findViewById(R.id.tasks_rv_timemanager);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        Add_task = root.findViewById(R.id.add_task_timemanager);
        Add_task.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent CreateActivityIntent = new Intent(getActivity(), CteateTask.class);
                startActivity(CreateActivityIntent);
            }
        });

        mSwipeRefreshLayout_timemanager = root.findViewById(R.id.swipe_container_timemanager);
        mSwipeRefreshLayout_timemanager.setOnRefreshListener(this);

        tasksadapter = new TimeManagerAdapter(null, onTaskClickListener, onCheckClickListener);
        recyclerView.setAdapter(tasksadapter);

        setHasOptionsMenu(true);

        getLoaderManager().initLoader(
                0,
                null,
                this
        );

        return root;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_timemanager, menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);

        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            assert searchManager != null;
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));

            queryTextListener = new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextChange(String newText) {
                    Log.i("Log tag", "onQueryTextChange");
                    if (newText.isEmpty()) {
                        SELECTION_ARGS_SEARCH = null;
                        SELECTION_SEARCH = null;
                    } else {
                        SELECTION_ARGS_SEARCH = "%" + newText + "%";
                        SELECTION_SEARCH = Contact_Database.Tasks.TASK_COLUMN + " like ?";
                    }
                    restartloader();
                    return false;
                }
                @Override
                public boolean onQueryTextSubmit(String query) {
                    Log.i("onQueryTextSubmit", query);

                    return false;
                }
            };
            searchView.setOnQueryTextListener(queryTextListener);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onAttach(Activity activity) {
        myContext=(FragmentActivity) activity;
        super.onAttach(activity);
        Log.i("Test", "Resume fragment");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_type_sort:
                DialogFragment DialogSortTasks;
                DialogSortTasks = new Dialog_sort_task();
                FragmentManager fragManager = myContext.getSupportFragmentManager(); //If using fragments from support v4
                DialogSortTasks.show(fragManager, "dlg2");
                return true;
            default:
                break;
        }
        searchView.setOnQueryTextListener(queryTextListener);
        return super.onOptionsItemSelected(item);
    }


    private final TimeManagerAdapter.onTaskClickListener onTaskClickListener  = new TimeManagerAdapter.onTaskClickListener() {
        @Override
        public void onTaskClick(long taskId) {

            Intent intent = new Intent(getActivity(), BrowsingTask.class);
            intent.putExtra(BrowsingTask.EXTRA_TASK_ID, taskId);
            startActivity(intent);
        }
    };

    private final TimeManagerAdapter.onCheckClickListener onCheckClickListener  = new TimeManagerAdapter.onCheckClickListener() {
        @Override
        public void onCheckedChanged(long taskId) {
            ContentValues contentValues = new ContentValues();
            DBHelper dbHelper = new DBHelper(getContext());
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            String query_rows = "SELECT " + Contact_Database.Tasks.COMPLETE + " FROM " + Contact_Database.Tasks.TABLE_NAME + " WHERE " + Contact_Database.Tasks._ID + " = " + taskId;
            Cursor c = db.rawQuery(query_rows, null);
            c.moveToFirst();

            @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Calendar calendar = Calendar.getInstance();
            String formattedDate = df.format(calendar.getTime());
            contentValues.put(Contact_Database.Tasks.UPDATED_TS_COLUMN, formattedDate);
            int flag_checked;
            if (c.getLong(c.getColumnIndex(Contact_Database.Tasks.COMPLETE)) == 0) {
                contentValues.put(Contact_Database.Tasks.COMPLETE, 1);
                flag_checked = 1;
            } else {
                contentValues.put(Contact_Database.Tasks.COMPLETE, 0);
                flag_checked = 0;
            }
            contentValues.put(Contact_Database.Tasks.SYNCHRONIZED, 0);
            getActivity().getContentResolver().update(ContentUris.withAppendedId(Contact_Database.Tasks.URI, taskId),
                    contentValues,
                    null,
                    null);

            db.close();

            timerManager.SyncCheckedTask(taskId, flag_checked);
            timerManager.SetAlarmTimer();
        }
    };

    @Override
    public void onRefresh() {
        SyncTasks sync_task = new SyncTasks(getContext());
        sync_task.sync_tasks();
        restartloader();
        mSwipeRefreshLayout_timemanager.setRefreshing(false);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] to;
        if (SELECTION_ARGS_SEARCH == null) {
            to = null;
        } else {
            to = new String[]{
                    SELECTION_ARGS_SEARCH
            };
        }

        return new CursorLoader(
                getActivity(),  // Контекст
                Contact_Database.Tasks.URI, // URI
                Contact_Database.Tasks.LIST_PROJECTION, // Столбцы
                SELECTION_SEARCH, // Параметры выборки
                to,
                HomeActivity.TYPE_SORT_TASKS// Сортировка по умолчанию
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        Log.i("Test", "Load finished: ");
        cursor.setNotificationUri(getActivity().getContentResolver(), Contact_Database.Tasks.URI); // !!!!!!!!!!!!!!!!!!!!!!!!!!
        tasksadapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    public void restartloader() {
        getLoaderManager().restartLoader(0, null, this);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onResume() { // при удалении заметки попытка обновления в провайдере приводит к ошибке поэтому перезагружаем загрузчик и данные
        super.onResume();
        //getLoaderManager().restartLoader(0, null, this);
    }
}
