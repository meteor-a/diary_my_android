package com.example.updateapp;

class Constants {


    // json {"url":"http://192.168.205.33:8080/Hello/app_v3.0.1_Other_20150116.apk","versionCode":2,"updateMessage":"版本更新信息"}

    static final String APK_DOWNLOAD_URL = "url";
    static final String APK_UPDATE_ERROR = "error";
    static final String APK_UPDATE_MESSAGE = "message";


    static final int TYPE_NOTIFICATION = 2;

    static final int TYPE_DIALOG = 1;

    static final String TAG = "UpdateChecker";

    static final String UPDATE_URL = "https://diary.inform-tech.ru:7780/get_path_ver";
}
