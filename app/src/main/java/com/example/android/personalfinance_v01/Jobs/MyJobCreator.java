package com.example.android.personalfinance_v01.Jobs;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;

public class MyJobCreator implements JobCreator{
    @Nullable
    @Override
    public Job create(@NonNull String tag) {
        switch (tag) {
            case BudgetResetJob.TAG:
                return new BudgetResetJob();
            default:
                return null;
        }
    }
}
