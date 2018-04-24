package com.example.android.personalfinance_v01.Jobs;

import android.support.annotation.NonNull;

import com.evernote.android.job.DailyJob;
import com.evernote.android.job.JobRequest;
import com.example.android.personalfinance_v01.MyClasses.Budget;
import com.example.android.personalfinance_v01.MyClasses.MyUtils;

import java.util.concurrent.TimeUnit;

public class BudgetResetJob extends DailyJob {
    public static final String TAG = "budget_reset_job_tag";

    public static void schedule() {
        // schedule between 1 and 2 AM
        DailyJob.schedule(new JobRequest.Builder(TAG), TimeUnit.HOURS.toMillis(1), TimeUnit.HOURS.toMillis(2));
    }

    @NonNull
    @Override
    protected DailyJobResult onRunDailyJob(Params params) {
        runActualJob();
        return DailyJobResult.SUCCESS;
    }

    private void runActualJob() {
        MyUtils.getBudgetsFromDatabase(getContext());

        for (Budget item : MyUtils.budgetList) {
            if (item.isResetBudget()) {
                MyUtils.modifyBudgetCurrentAmount(getContext(), item, 0.0);
            }
        }
    }
}
