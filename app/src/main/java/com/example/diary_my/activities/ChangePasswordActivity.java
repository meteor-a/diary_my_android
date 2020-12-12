package com.example.diary_my.activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.diary_my.R;
import com.example.diary_my.RetrofitApi.APIService;
import com.example.diary_my.RetrofitApi.APIUrl;
import com.example.diary_my.helper.SharedPrefManager;
import com.example.diary_my.models.Result;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChangePasswordActivity extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button button_change_password = findViewById(R.id.button_change_password);
        button_change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (correct_password()) {
                    change_password();
                    Log.i("change_password", "chech correcvtion complete successfull");
                } else {
                    Log.i("change_password", "chech correcvtion complete with error");
                    Toast.makeText(getApplicationContext(), "Новый пароль должен быть не менее 7 символов и содержать как минимум одну заглавную букву и хотя бы одну цифру", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public boolean correct_password() {
        EditText new_pass_1 = findViewById(R.id.edittext_new_pass_1);
        EditText new_pass_2 = findViewById(R.id.edittext_new_pass_2);

        if (new_pass_1.getText().toString().equals(new_pass_2.getText().toString())) {
            return false;
        }

        String pass = new_pass_1.getText().toString();

        if (pass.length() < 7) {
            return false;
        }
        boolean isUpLetter = false;
        boolean isOneDigit = false;
        char[] array_pass = pass.toCharArray();
        for (char x : array_pass) {
            if (Character.isUpperCase(x)) {
                isUpLetter = true;
            }
            if (Character.isDigit(x)) {
                isOneDigit = true;
            }
            if (isOneDigit && isUpLetter) {
                return true;
            }
        }
        return false;
    }

    public void change_password() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        Log.i("change_password", "start response");
        progressDialog.setMessage("Смена пароля...");
        progressDialog.show();

        EditText inputOldPassword = findViewById(R.id.edittext_old_pass);
        EditText inputNewPassword = findViewById(R.id.edittext_new_pass_1);

        String old_pass = inputOldPassword.getText().toString();
        String new_pass = inputNewPassword.getText().toString();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIUrl.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        APIService service = retrofit.create(APIService.class);

        Call<Result> call = service.updatePassword(SharedPrefManager.getInstance(this).getUser().getEmail(), old_pass, new_pass);

        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(@NonNull Call<Result> call, @NonNull Response<Result> response) {
                progressDialog.dismiss();
                if (!response.body().getError()) {
                    progressDialog.dismiss();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Неправильно введен старый пароль", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Result> call, @NonNull Throwable t) {
                progressDialog.dismiss();
            }
        });


    }
}