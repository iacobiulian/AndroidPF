package com.example.android.personalfinance_v01;

import android.app.Application;

import java.util.concurrent.TimeUnit;

public class App extends Application {
    private static App instance;

    public static App getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;
    }
}
