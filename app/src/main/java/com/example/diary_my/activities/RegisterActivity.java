package com.example.diary_my.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.example.diary_my.R;
import com.example.diary_my.RetrofitApi.APIService;
import com.example.diary_my.RetrofitApi.APIUrl;
import com.example.diary_my.activities.HomeActivity;
import com.example.diary_my.activities.MainActivity;
import com.example.diary_my.helper.SharedPrefManager;
import com.example.diary_my.models.Result;
import com.example.diary_my.models.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterActivity extends AppCompatActivity{

    private static final int DIALOG_CHECK_PASSWORD = 1;
    private static final int  DIALOG_CHECK_NAME = 2;
    private static final int  DIALOG_CHECK_EMAIL = 3;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

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
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
    }


    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        finish();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }

    /*
    1. Хотя бы одна заглавная буква
    2. Хотя бы одна цифра
    3. Длинна не менее 7 знаков
     */

    public boolean CheckCorrectionPassword(String pass) {
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

    public boolean CheckCorrectionUserName(String username) {
        if (username.length() == 0) {
            return false;
        }
        if (username.contains("@") && username.contains("!") && username.contains("#") && username.contains("$") && username.contains("%") && username.contains("&")) {
            return false;
        }
        return true;
    }

    public boolean CheckCorrectionEmail(String email) {
        if (email.length() == 0) {
            return false;
        }

        if (!email.contains("@") && !email.contains(".")) {
            return false;
        }

        return true;
    }

    protected Dialog onCreateDialog(int id) {
        if (id == DIALOG_CHECK_PASSWORD) {
            AlertDialog.Builder adb = new AlertDialog.Builder(this);
            adb.setTitle(R.string.dialog_correct_title);
            adb.setMessage("Пароль должен быть не менее 7 символов и содержать как минимум одну заглавную букву и хотя бы одну цифру");
            adb.setPositiveButton(R.string.dialog_correct_okey, myClickListener);
            return adb.create();
        }

        if (id == DIALOG_CHECK_NAME) {
            AlertDialog.Builder adb = new AlertDialog.Builder(this);
            adb.setTitle(R.string.dialog_correct_title);
            adb.setMessage("Имя пользователя не должно быть пустым и не должно содержать специальных символов($, %, #, & и т.д.)");
            adb.setPositiveButton(R.string.dialog_correct_okey, myClickListener);
            return adb.create();
        }

        if (id == DIALOG_CHECK_EMAIL) {
            AlertDialog.Builder adb = new AlertDialog.Builder(this);
            adb.setTitle(R.string.dialog_correct_title);
            adb.setMessage("Проверьте, правильно ли введен e-mail");
            adb.setPositiveButton(R.string.dialog_correct_okey, myClickListener);
            return adb.create();
        }
        return super.onCreateDialog(id);
    }

    DialogInterface.OnClickListener myClickListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {

        }
    };

    public void on_Register_clicked(View view) {
        boolean correct = true;

        EditText inputPassword = findViewById(R.id.InputPassword);
        if (!CheckCorrectionPassword(inputPassword.getText().toString())) {
            correct = false;
            showDialog(DIALOG_CHECK_PASSWORD);
            //userRegister();
        }

        EditText inputName = findViewById(R.id.InputName);
        if (!CheckCorrectionUserName(inputName.getText().toString())) {
            correct = false;
            showDialog(DIALOG_CHECK_NAME);
        }

        EditText inputEmail = findViewById(R.id.InputEmail);
        if (!CheckCorrectionUserName(inputEmail.getText().toString())) {
            correct = false;
            showDialog(DIALOG_CHECK_EMAIL);
        }

        if (correct) {
            userRegister();
        }
    }

    private void userRegister() {

        //defining a progress dialog to show while signing up
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Signing Up...");
        progressDialog.show();

        //getting the user values

        EditText inputEmail = findViewById(R.id.InputEmail);
        EditText inputPassword = findViewById(R.id.InputPassword);
        EditText inputName = findViewById(R.id.InputName);

        String name = inputName.getText().toString().trim();
        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString();


        //building retrofit object
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIUrl.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        //Defining retrofit api service
        APIService service = retrofit.create(APIService.class);

        //Defining the user object as we need to pass it with the call
        User user = new User(name, email, password);

        //defining the call
        Call<Result> call = service.createUser(
                user.getName(),
                user.getEmail(),
                user.getPassword()
        );

        //calling the api
        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                //hiding progress dialog
                progressDialog.dismiss();

                //displaying the message from the response as toast
                Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();

                //if there is no error
                if (!response.body().getError()) {
                    //starting profile activity
                    finish();
                    SharedPrefManager.getInstance(getApplicationContext()).userLogin(response.body().getUser());
                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
