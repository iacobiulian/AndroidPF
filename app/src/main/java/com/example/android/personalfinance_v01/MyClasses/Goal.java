package com.example.android.personalfinance_v01.MyClasses;

import android.text.TextUtils;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by iacob on 14-Mar
 */

public class Goal implements Serializable {
    public static final int REACHED = 0;
    public static final int NOT_REACHED = 1;

    private String name;
    private double goalAmount;
    private double savedAmount;
    private long targetDate;
    private int status;
    private ArrayList<Double> addedAmounts;
    private ArrayList<Long> addedAmountsDates;

    public Goal(String name, double goalAmount, double savedAmount, long targetDate) {
        this.name = name;
        this.goalAmount = goalAmount;
        this.savedAmount = savedAmount;
        this.targetDate = targetDate;
        this.status = Goal.NOT_REACHED;
        addedAmounts = new ArrayList<>();
        addedAmountsDates = new ArrayList<>();
    }

    public Goal(String name, double goalAmount, double savedAmount, long targetDate, int status, ArrayList<Double> amountsList, ArrayList<Long> timesList) {
        this.name = name;
        this.goalAmount = goalAmount;
        this.savedAmount = savedAmount;
        this.targetDate = targetDate;
        this.status = status;
        this.addedAmounts = new ArrayList<>();
        this.addedAmounts.addAll(amountsList);
        this.addedAmountsDates = new ArrayList<>();
        this.addedAmountsDates.addAll(timesList);
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

    public void setSavedAmount(double savedAmount) {
        this.savedAmount = savedAmount;
    }

    public long getTargetDate() {
        return targetDate;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public ArrayList<Double> getAddedAmounts() {
        return addedAmounts;
    }

    public ArrayList<Long> getAddedAmountsDates() {
        return addedAmountsDates;
    }

    public boolean isValid() {
        if (this.goalAmount < 0)
            return false;
        if (TextUtils.isEmpty(this.name))
            return false;

        return true;
    }
}
