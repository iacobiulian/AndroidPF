package com.example.android.personalfinance_v01.MyClasses;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

public class Budget implements Serializable {
    public static final int NONE = 0;
    public static final int WEEKLY = 7;
    public static final int MONTHLY = 30;
    public static final int YEARLY = 365;

    private int type;
    private Category category;
    private double totalAmount;
    private double currentAmount;
    private long creationDate;
    private long resetDate;

    public Budget() {
    }

    public Budget(int type, Category category, double totalAmount) {
        this.type = type;
        this.category = category;
        this.totalAmount = totalAmount;
        currentAmount = 0;
        creationDate = MyUtils.getCurrentDateTime();
        resetDate = getNextResetDate(this.creationDate);
    }

    public Budget(int type, Category category, double totalAmount, double currentAmount, long creationDate, long resetDate) {
        this.type = type;
        this.category = category;
        this.totalAmount = totalAmount;
        this.currentAmount = currentAmount;
        this.creationDate = creationDate;
        this.resetDate = resetDate;
    }

    public long getCreationDate() {
        return creationDate;
    }

    public int getType() {
        return type;
    }

    public String getTypeString() {
        switch (this.getType()) {
            case NONE:
                return "";
            case WEEKLY:
                return "weekly ";
            case MONTHLY:
                return "monthly ";
            case YEARLY:
                return "yearly ";
            default:
                return "";
        }
    }

    public long getResetDate() {
        return resetDate;
    }

    public Category getCategory() {
        return category;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public double getCurrentAmount() {
        return currentAmount;
    }

    public void setCurrentAmount(double currentAmount) {
        this.currentAmount = currentAmount;
    }

    public boolean isResetBudget() {

        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);

        Date rightNow = calendar.getTime();

        if(rightNow.after(new Date(this.resetDate))) {
            this.resetDate = getNextResetDate(this.resetDate);
            return true;
        }

        return false;
    }

    private long getNextResetDate(long date) {
        Date creationDate = new Date(date);
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.setTime(creationDate);
        calendar.add(Calendar.DATE, 1);

        switch (this.getType()) {
            case NONE:
                return Long.MAX_VALUE - 1;
            case WEEKLY:
                while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
                    calendar.add(Calendar.DATE, 1);
                }
                break;
            case MONTHLY:
                while (calendar.get(Calendar.DAY_OF_MONTH) != 1) {
                    calendar.add(Calendar.DATE, 1);
                }
                break;
            case YEARLY:
                while (calendar.get(Calendar.DAY_OF_YEAR) != 1) {
                    calendar.add(Calendar.DATE, 1);
                }
                break;
            default:
                return Long.MAX_VALUE - 1;
        }

        return calendar.getTimeInMillis();
    }

    public boolean isValid() {
        return !(this.totalAmount < 0);
    }

    public boolean isExceeded() {
        return this.currentAmount > this.totalAmount;
    }

    public boolean isExceededHalf() {
        return this.currentAmount > this.totalAmount / 2;
    }

    public boolean isDateInArea(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);

        switch (this.getType()) {
            case NONE:
                return true;
            case WEEKLY:
                calendar.add(Calendar.WEEK_OF_MONTH, -1);
                break;
            case MONTHLY:
                calendar.add(Calendar.MONTH, -1);
                break;
            case YEARLY:
                calendar.add(Calendar.YEAR, -1);
                break;
            default:
                return true;
        }

        return date.after(calendar.getTime());
    }
}
