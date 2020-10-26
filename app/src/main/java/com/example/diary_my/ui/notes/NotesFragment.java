package com.example.diary_my.ui.notes;

import androidx.annotation.NonNull;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SearchView;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;

import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import android.view.ViewGroup;



import com.example.diary_my.activities.BrowsingNote;
import com.example.diary_my.activities.CreateNote;
import com.example.diary_my.R;
import com.example.diary_my.activities.HomeActivity;
import com.example.diary_my.db.Contact_Database;
import com.example.diary_my.dialogs.Dialog_sort_notes;
import com.example.diary_my.helper.SyncNotes;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class NotesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, SwipeRefreshLayout.OnRefreshListener {

    private FragmentActivity myContext;

    private NotepadAdapter notesAdapter;
    // public final String TYPE_SORT_NOTES = null;
    private String SELECTION_SEARCH = null;
    public String SELECTION_ARGS_SEARCH = null;
    FloatingActionButton Add_notes;
    private SearchView searchView = null;
    private SearchView.OnQueryTextListener queryTextListener;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @RequiresApi(api = Build.VERSION_CODES.M)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_notes, container, false);
        RecyclerView recyclerView = root.findViewById(R.id.notes_rv);

        mSwipeRefreshLayout = root.findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        Log.i("Log tag", "OnCreate note fragment");

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        Add_notes = root.findViewById(R.id.add_note);
        Add_notes.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent CreateActivityIntent = new Intent(getActivity(), CreateNote.class);
                startActivity(CreateActivityIntent);
            }
        });
        notesAdapter = new NotepadAdapter(null, onNoteClickListener);
        recyclerView.setAdapter(notesAdapter);
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
        inflater.inflate(R.menu.main_notes, menu);
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
                        SELECTION_SEARCH = Contact_Database.Notes.TOPIC_COLUMN + " like ?";
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

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRefresh() {
        SyncNotes sync = new SyncNotes(getContext());
        sync.sync();
        restartloader();
        mSwipeRefreshLayout.setRefreshing(false);
    }


    @Override
    public void onAttach(Activity activity) {
        myContext=(FragmentActivity) activity;
        super.onAttach(activity);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_type_sort:
                DialogFragment DialogSortNotes;
                DialogSortNotes = new Dialog_sort_notes();
                FragmentManager fragManager = myContext.getSupportFragmentManager(); //If using fragments from support v4
                DialogSortNotes.show(fragManager, "dlg2");
                return true;
            default:
                break;
        }
        searchView.setOnQueryTextListener(queryTextListener);
        return super.onOptionsItemSelected(item);
    }

    private final NotepadAdapter.OnNoteClickListener onNoteClickListener  = new NotepadAdapter.OnNoteClickListener() {
        @Override
        public void onNoteClick(long noteId) {
            Intent intent = new Intent(getActivity(), BrowsingNote.class);
            intent.putExtra(BrowsingNote.EXTRA_NOTE_ID, noteId);
            startActivity(intent);
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.M)
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
                Contact_Database.Notes.URI, // URI
                Contact_Database.Notes.LIST_PROJECTION, // Столбцы
                SELECTION_SEARCH, // Параметры выборки
                to,
                HomeActivity.TYPE_SORT_NOTES// Сортировка по умолчанию
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        Log.i("Test", "Load finished: ");
        cursor.setNotificationUri(getActivity().getContentResolver(), Contact_Database.Notes.URI); // !!!!!!!!!!!!!!!!!!!!!!!!!!
        notesAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onResume() { // при удалении заметки попытка обновления в провайдере приводит к ошибке поэтому перезагружаем загрузчик и данные
        super.onResume();
        getLoaderManager().restartLoader(0, null, this);
        Log.i("Test", "Resume fragment");
    }

    public void restartloader() {
        getLoaderManager().restartLoader(0, null, this);
    }

}
