package com.example.android.personalfinance_v01.MyClasses;

import java.io.Serializable;

/**
 * Created by iacob on 28-Feb-18.
 */

public class BalanceAccount implements Serializable {
    private String name;
    private double balance;
    private String currency;
    boolean selected;

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

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public void substractFromBalance(double amount) {
        this.balance -= amount;
    }

    public void addToBalance(double amount) {
        this.balance += amount;
    }
}
