package com.example.android.personalfinance_v01.MyClasses;

import java.util.Date;

/**
 * Created by iacob on 26-Feb-18.
 */

public class ExpenseIncome {
    public static final boolean TYPE_INCOME = true;
    public static final boolean TYPE_EXPENSE = false;

    double amount;
    boolean type;
    Category category;
    Date date;

    public ExpenseIncome(double amount, boolean type, Category category, Date date) {
        this.amount = amount;
        this.type = type;
        this.category = category;
        this.date = date;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public boolean isType() {
        return type;
    }

    public void setType(boolean type) {
        this.type = type;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
