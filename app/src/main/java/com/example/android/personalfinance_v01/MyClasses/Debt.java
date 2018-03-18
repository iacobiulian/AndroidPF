package com.example.android.personalfinance_v01.MyClasses;

import android.text.TextUtils;

/**
 * Created by iacob on 09-Mar
 */

public class Debt {
    public static final int I_LEND = 0;
    public static final int I_BORROW = 1;

    public static final int CLOSED = 1;
    public static final int NOT_CLOSED = 0;

    private int type;
    private String payee;
    private double amount;
    private double amountPaidBack;
    private int closed;
    private long creationDate;
    private long paybackDate;

    public Debt(int type, String payee, double amount, long creationDate, long paybackDate) {
        this.type = type;
        this.payee = payee;
        this.amount = amount;
        this.amountPaidBack = 0.0;
        this.closed = NOT_CLOSED;
        this.creationDate = creationDate;
        this.paybackDate = paybackDate;
    }

    Debt(int type, String payee, double amount, double amountPaidBack, int closed, long creationDate, long paybackDate) {
        this.type = type;
        this.payee = payee;
        this.amount = amount;
        this.amountPaidBack = amountPaidBack;
        this.closed = closed;
        this.creationDate = creationDate;
        this.paybackDate = paybackDate;
    }

    public int getType() {
        return type;
    }

    public String getPayee() {
        return payee;
    }

    public double getAmount() {
        return amount;
    }

    public double getAmountPaidBack() {
        return amountPaidBack;
    }

    public int isClosed() {
        return closed;
    }

    public long getCreationDate() {
        return creationDate;
    }

    public long getPaybackDate() {
        return paybackDate;
    }

    public boolean isValid() {
        return !(this.amount <= 0.0 || TextUtils.isEmpty(this.payee));
    }
}
