package com.example.android.personalfinance_v01.MyClasses;

public class Transfer {
    private double amount;
    private long creationDate;
    private BalanceAccount fromAccount;
    private BalanceAccount toAccount;

    public Transfer(double amount, BalanceAccount fromAccount, BalanceAccount toAccount) {
        this.amount = amount;
        this.creationDate = MyUtils.getCurrentDateTime();
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
    }

    public Transfer(double amount, long creationDate, BalanceAccount fromAccount, BalanceAccount toAccount) {
        this.amount = amount;
        this.creationDate = creationDate;
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
    }

    public Transfer() {
    }

    public double getAmount() {
        return amount;
    }

    public long getCreationDate() {
        return creationDate;
    }

    public BalanceAccount getFromAccount() {
        return fromAccount;
    }

    public BalanceAccount getToAccount() {
        return toAccount;
    }
}
