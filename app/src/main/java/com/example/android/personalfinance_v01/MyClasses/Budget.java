package com.example.android.personalfinance_v01.MyClasses;

import java.io.Serializable;
import java.util.Calendar;

public class Budget implements Serializable {
    public static final int NONE = 0;
    public static final int WEEKLY = 7;
    public static final int MONTHLY = 30;
    public static final int YEARLY = 365;

    private int type;
    private Category category;
    private double totalAmount;
    private double currentAmount;
    private long date;

    public Budget() {
    }

    public Budget(int type, Category category, double totalAmount) {
        this.type = type;
        this.category = category;
        this.totalAmount = totalAmount;
        currentAmount = 0;
        date = MyUtils.getCurrentDateTime();
    }

    public Budget(int type, Category category, double totalAmount, double currentAmount, long date) {
        this.type = type;
        this.category = category;
        this.totalAmount = totalAmount;
        this.currentAmount = currentAmount;
        this.date = date;
    }

    public long getDate() {
        return date;
    }

    public int getType() {
        return type;
    }

    public Category getCategory() {
        return category;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public double getCurrentAmount() {
        return currentAmount;
    }

    public boolean isResetBudget() {
        Calendar calendar = Calendar.getInstance();

        int day = 0;

        switch (this.getType()) {
            case NONE:
                break;
            case WEEKLY:
                day = calendar.get(Calendar.DAY_OF_WEEK);
                break;
            case MONTHLY:
                day = calendar.get(Calendar.DAY_OF_MONTH);
                break;
            case YEARLY:
                day = calendar.get(Calendar.DAY_OF_YEAR);
                break;
            default:
                break;
        }

        return (day == 1);
    }

    public boolean isValid() {
        return !(this.totalAmount < 0);
    }
}
