package com.example.diary_my.activities;


import android.app.Fragment;


import android.content.Intent;

import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;

import android.app.FragmentTransaction;


import com.example.diary_my.R;
import com.example.diary_my.db.Contact_Database;
import com.example.diary_my.db.DBHelper;
import com.example.diary_my.dialogs.Dialog_sort_notes;
import com.example.diary_my.dialogs.Dialog_sort_task;
import com.example.diary_my.helper.SharedPrefManager;
import com.example.diary_my.helper.SyncNotes;

import com.example.diary_my.helper.SyncTasks;
import com.example.diary_my.ui.notes.NotesFragment;
import com.example.diary_my.ui.profile.ProfileFragment;
import com.example.diary_my.ui.timemanager.TimeManagerFragment;
import com.example.updateapp.UpdateChecker;
import com.google.android.material.navigation.NavigationView;



public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, Dialog_sort_notes.NoticeDialogListener, Dialog_sort_task.NoticeDialogListener {

    public static String TYPE_SORT_NOTES;
    public static String TYPE_SORT_TASKS;


    Toolbar toolbar;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TYPE_SORT_NOTES = null;
        TYPE_SORT_TASKS = null;

        if (!SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
        setContentView(R.layout.activity_home);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SyncNotes sync = new SyncNotes(getApplicationContext());
        sync.sync();

        SyncTasks sync_task = new SyncTasks(getApplicationContext());
        sync_task.sync_tasks();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_notes);

        View headerView = navigationView.getHeaderView(0);
        TextView textViewName = (TextView) headerView.findViewById(R.id.textViewName);
        textViewName.setText(SharedPrefManager.getInstance(this).getUser().getName());

        TextView textViewEmail = (TextView) headerView.findViewById(R.id.textViewEmail);
        textViewEmail.setText(SharedPrefManager.getInstance(this).getUser().getEmail());

        //loading home fragment by default
        displaySelectedScreen(R.id.nav_notes);
        toolbar.setTitle("Заметки");

     //   UpdateChecker.checkForDialog(HomeActivity.this);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onDialogSortNotesAlphabetClick(DialogFragment dialog){
        TYPE_SORT_NOTES = Contact_Database.Notes.TOPIC_COLUMN;
        displaySelectedScreen(R.id.nav_notes);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onDialogSortNotesEditDataClick(DialogFragment dialog){
        TYPE_SORT_NOTES = Contact_Database.Notes.UPDATED_TS_COLUMN;
        displaySelectedScreen(R.id.nav_notes);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onDialogSortNotesCreateDateClick(DialogFragment dialog){ // По дате создания изменения (сначало новые)
        TYPE_SORT_NOTES = null;
        displaySelectedScreen(R.id.nav_notes);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onDialogSortTasksAlphabetClick(DialogFragment dialog){
        TYPE_SORT_TASKS = Contact_Database.Tasks.TASK_COLUMN;
        displaySelectedScreen(R.id.nav_time_manager);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onDialogSortTasksEditDataClick(DialogFragment dialog){
        TYPE_SORT_TASKS = Contact_Database.Tasks.UPDATED_TS_COLUMN;
        displaySelectedScreen(R.id.nav_time_manager);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onDialogSortNoteTasksCreateDateClick(DialogFragment dialog){ // По дате создания изменения (сначало новые)
        TYPE_SORT_TASKS = null;
        displaySelectedScreen(R.id.nav_time_manager);
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void displaySelectedScreen(int itemId) {
        Fragment fragment = null;
        switch (itemId) {
            case R.id.nav_profile:
                fragment = new ProfileFragment();
                toolbar.setTitle("Профиль");
                break;
            case R.id.nav_time_manager:
                fragment = new TimeManagerFragment();
                toolbar.setTitle("Задачи");
                break;
            case R.id.nav_notes:
                fragment = new NotesFragment();
                toolbar.setTitle("Заметки");
                break;
            case R.id.nav_logout:
                DBHelper notesDbHelper = new DBHelper(this);
                SQLiteDatabase db = notesDbHelper.getReadableDatabase();
                db.delete(Contact_Database.Notes.TABLE_NAME, null, null);
                db.delete(Contact_Database.Tasks.TABLE_NAME, null, null);
                db.close();
                logout();
                break;
            default:
                fragment = new NotesFragment();
                break;
        }

        //replacing the fragment
        if (fragment != null) {
            FragmentTransaction ft;
            ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    private void logout() {
        SharedPrefManager.getInstance(this).logout();
        finish();
        startActivity(new Intent(this, LoginActivity.class));
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        displaySelectedScreen(item.getItemId());
        return true;
    }

}