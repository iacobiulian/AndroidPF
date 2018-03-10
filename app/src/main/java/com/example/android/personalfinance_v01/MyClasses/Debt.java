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

    int type;
    String payee;
    double amount;
    double amountPaidBack;
    int closed;
    long creationDate;
    long paybackDate;

    public Debt(int type, String payee, double amount, long creationDate, long paybackDate) {
        this.type = type;
        this.payee = payee;
        this.amount = amount;
        this.amountPaidBack = 0.0;
        this.closed = NOT_CLOSED;
        this.creationDate = creationDate;
        this.paybackDate = paybackDate;
    }

    public Debt(int type, String payee, double amount, double amountPaidBack, int closed, long creationDate, long paybackDate) {
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

    public void setAmountPaidBack(double amountPaidBack) {
        this.amountPaidBack = amountPaidBack;
    }

    public void setClosed(int closed) {
        this.closed = closed;
    }

    public boolean isValid() {
        if (this.amount <= 0.0 || TextUtils.isEmpty(this.payee)) {
            return false;
        }

        return true;
    }

    public double amountRemainingToBePaid() {
        return this.amount - this.amountPaidBack;
    }
}
