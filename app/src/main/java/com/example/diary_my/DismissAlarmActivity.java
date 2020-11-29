package com.example.diary_my;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextClock;

import androidx.appcompat.app.AppCompatActivity;

import com.example.diary_my.activities.CteateTask;
import com.example.diary_my.db.DBHelper;
import com.example.diary_my.helper.DismissAlarmNotificationController;
import com.example.diary_my.helper.SharedPreferencesHelper;
import com.example.diary_my.models.AlarmParams;

import java.util.concurrent.TimeUnit;

import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE;

/**
 * Created by Ilya Anshmidt on 19.09.2017.
 */

public class DismissAlarmActivity extends AppCompatActivity implements RingtonePlayer.OnFinishListener {

    Button dismissButton;
    TextClock textClock;
    RingtonePlayer ringtonePlayer;
    SharedPreferencesHelper sharPrefHelper;
    int numberOfAlreadyRangAlarms;
    TimerManager timerManager;
    private final String LOG_TAG = com.example.diary_my.DismissAlarmActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dismiss);
        showOnLockedScreen();

        ringtonePlayer = new RingtonePlayer(com.example.diary_my.DismissAlarmActivity.this);
        sharPrefHelper = new SharedPreferencesHelper(com.example.diary_my.DismissAlarmActivity.this);


        View layout = findViewById(R.id.dismissLayout);

        layout.setSystemUiVisibility(SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        dismissButton = (Button) findViewById(R.id.button_dismiss_alarm);
        textClock = (TextClock) findViewById(R.id.text_clock_dismiss);
        DismissButtonNameGiver dismissButtonNameGiver = new DismissButtonNameGiver(com.example.diary_my.DismissAlarmActivity.this);
        dismissButton.setText(dismissButtonNameGiver.getName());

        numberOfAlreadyRangAlarms = sharPrefHelper.getNumberOfAlreadyRangAlarms() + 1;
        Log.d(LOG_TAG, "numberOfAlreadyRangAlarms (including current one) = " + numberOfAlreadyRangAlarms);
        sharPrefHelper.setNumberOfAlreadyRangAlarms(numberOfAlreadyRangAlarms);

        ringtonePlayer.start();

        dismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timerManager = new TimerManager(com.example.diary_my.DismissAlarmActivity.this);
                ringtonePlayer.stop();
                timerManager.SetCompleteTask();
                timerManager.resetSingleAlarmTimer();
                finish();
            }
        });
    }

    @Override
    public void onPlayerFinished() {
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (hasWindowFocus()) {
            ringtonePlayer.stop();
        }
    }


    private void showOnLockedScreen() {

        final Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
    }
}
