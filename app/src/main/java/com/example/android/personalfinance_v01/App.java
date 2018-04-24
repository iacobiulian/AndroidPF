package com.example.android.personalfinance_v01;

import android.app.Application;

import com.evernote.android.job.JobManager;
import com.example.android.personalfinance_v01.Jobs.MyJobCreator;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        JobManager.create(this).addJobCreator(new MyJobCreator());
    }
}
