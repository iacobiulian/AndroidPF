package com.example.android.personalfinance_v01.MyClasses;

import android.text.TextUtils;

import java.io.Serializable;

/**
 * Created by iacob on 28-Feb
 */

public class BalanceAccount implements Serializable {
    private String name;
    private double balance;
    private String currency;
    private boolean selected;

    public BalanceAccount(String name, double balance, String currency) {
        this.name = name;
        this.balance = balance;
        this.currency = currency;
        this.selected = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getBalance() {
        return balance;
    }

    public String getBalanceString() {
        return this.balance + "";
    }

    public String getCurrency() {
        return currency;
    }

    boolean isSelected() {
        return selected;
    }

    void setSelected(boolean selected) {
        this.selected = selected;
    }

    public void substractFromBalance(double amount) {
        this.balance -= amount;
    }

    public void addToBalance(double amount) {
        this.balance += amount;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    @Override
    public boolean equals(Object obj) {

        if (obj == this) {
            return true;
        }

        if (!(obj instanceof BalanceAccount)) {
            return false;
        }

        // typecast o to Complex so that we can compare data members
        BalanceAccount balanceAccount = (BalanceAccount) obj;

        // Compare the data members and return accordingly
        return Double.compare(this.getBalance(), balanceAccount.getBalance()) == 0
                && TextUtils.equals(this.getName(), balanceAccount.getName()) && TextUtils.equals(this.getCurrency(), balanceAccount.getCurrency());
    }
}
