package com.example.android.personalfinance_v01.MyClasses;

import android.text.TextUtils;

/**
 * Created by iacob on 14-Mar
 */

public class Goal {
    public static final int REACHED = 0;
    public static final int NOT_REACHED = 1;

    private String name;
    private double goalAmount;
    private double savedAmount;
    private long targetDate;
    private int status;

    public Goal(String name, double goalAmount, double savedAmount, long targetDate) {
        this.name = name;
        this.goalAmount = goalAmount;
        this.savedAmount = savedAmount;
        this.targetDate = targetDate;
        this.status = Goal.NOT_REACHED;
    }

    public Goal(String name, double goalAmount, double savedAmount, long targetDate, int status) {
        this.name = name;
        this.goalAmount = goalAmount;
        this.savedAmount = savedAmount;
        this.targetDate = targetDate;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public double getGoalAmount() {
        return goalAmount;
    }

    public double getSavedAmount() {
        return savedAmount;
    }

    public long getTargetDate() {
        return targetDate;
    }

    public int getStatus() {
        return status;
    }

    public boolean isValid() {
        if (this.goalAmount < 0)
            return false;
        if (TextUtils.isEmpty(this.name))
            return false;

        return true;
    }
}
