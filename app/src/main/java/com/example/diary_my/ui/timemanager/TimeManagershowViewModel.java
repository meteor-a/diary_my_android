package com.example.diary_my.ui.timemanager;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TimeManagershowViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public TimeManagershowViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is timemanager fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}