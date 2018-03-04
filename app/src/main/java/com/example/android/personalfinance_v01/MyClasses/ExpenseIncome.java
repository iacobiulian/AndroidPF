package com.example.android.personalfinance_v01.MyClasses;

import java.util.Date;

/**
 * Created by iacob on 26-Feb-18.
 */

public class ExpenseIncome {
    public static final int TYPE_INCOME = 1;
    public static final int TYPE_EXPENSE = 2;

    double amount;
    int type;
    Category category;
    Date date;
    BalanceAccount account;

    public ExpenseIncome(double amount, int type, Category category, Date date, BalanceAccount account) {
        this.amount = amount;
        this.type = type;
        this.category = category;
        this.date = date;
        this.account = account;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public int isType() {
        return type;
    }

    public void setType(int type) {
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

    public BalanceAccount getAccount() {
        return account;
    }

    public void setAccount(BalanceAccount account) {
        this.account = account;
    }
}
