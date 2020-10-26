package com.example.diary_my.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.diary_my.R;
import com.example.diary_my.activities.ChangePasswordActivity;
import com.example.diary_my.activities.HomeActivity;
import com.example.diary_my.activities.LoginActivity;
import com.example.diary_my.activities.MainActivity;
import com.example.diary_my.helper.SharedPrefManager;

public class ProfileFragment extends Fragment {

    TextView username;
    TextView email;
    Button change_password;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_profile, container, false);
        username = root.findViewById(R.id.username);
        email = root.findViewById(R.id.email);
        change_password = root.findViewById(R.id.button_change_password);

        initdata();

        change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ChangePasswordActivity.class);
                startActivity(intent);
            }
        });

        return root;
    }

    public void initdata() {
        username.setText(SharedPrefManager.getInstance(getActivity()).getUser().getName());
        email.setText(SharedPrefManager.getInstance(getActivity()).getUser().getEmail());
    }
}
