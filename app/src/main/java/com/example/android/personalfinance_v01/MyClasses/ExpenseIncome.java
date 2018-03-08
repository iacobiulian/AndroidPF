package com.example.android.personalfinance_v01.MyClasses;

/**
 * Created by iacob on 26-Feb
 */

public class ExpenseIncome {
    public static final int TYPE_INCOME = 1;
    public static final int TYPE_EXPENSE = 2;

    private double amount;
    private int type;
    private Category category;
    private long date;
    private BalanceAccount account;

    public ExpenseIncome(double amount, int type, Category category, long date, BalanceAccount account) {
        this.amount = amount;
        this.type = type;
        this.category = category;
        this.date = date;
        this.account = account;
    }

    public double getAmount() {
        return amount;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Category getCategory() {
        return category;
    }

    public long getDate() {
        return date;
    }

    public BalanceAccount getAccount() {
        return account;
    }
}
