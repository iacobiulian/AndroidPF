package com.example.android.personalfinance_v01.MyClasses;

public class HistoryItem {
    private double amount;
    private long time;

    public HistoryItem(double amount, long time) {
        this.amount = amount;
        this.time = time;
    }

    public double getAmount() {
        return amount;
    }

    public long getTime() {
        return time;
    }
}
