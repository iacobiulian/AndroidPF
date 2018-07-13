package com.example.android.personalfinance_v01.MyClasses;

import android.text.TextUtils;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by iacob on 09-Mar
 */

public class Debt implements Serializable {
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
    private ArrayList<Double> addedAmounts;
    private ArrayList<Long> addedAmountsDates;

    public Debt(int type, String payee, double amount, long creationDate, long paybackDate) {
        this.type = type;
        this.payee = payee;
        this.amount = amount;
        this.amountPaidBack = 0.0;
        this.closed = NOT_CLOSED;
        this.creationDate = creationDate;
        this.paybackDate = paybackDate;
        addedAmounts = new ArrayList<>();
        addedAmountsDates = new ArrayList<>();
    }

    Debt(int type, String payee, double amount, double amountPaidBack, int closed, long creationDate, long paybackDate, ArrayList<Double> amountsList, ArrayList<Long> timesList) {
        this.type = type;
        this.payee = payee;
        this.amount = amount;
        this.amountPaidBack = amountPaidBack;
        this.closed = closed;
        this.creationDate = creationDate;
        this.paybackDate = paybackDate;
        this.addedAmounts = new ArrayList<>();
        this.addedAmounts.addAll(amountsList);
        this.addedAmountsDates = new ArrayList<>();
        this.addedAmountsDates.addAll(timesList);
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

    public void setAmountPaidBack(double amountPaidBack) {
        this.amountPaidBack = amountPaidBack;
    }

    public int isClosed() {
        return closed;
    }

    public void setClosed(int closed) {
        this.closed = closed;
    }

    public long getCreationDate() {
        return creationDate;
    }

    public long getPaybackDate() {
        return paybackDate;
    }

    public ArrayList<Double> getAddedAmounts() {
        return addedAmounts;
    }

    public ArrayList<Long> getAddedAmountsDates() {
        return addedAmountsDates;
    }

    public boolean isValid() {
        return !(this.amount <= 0.0 || TextUtils.isEmpty(this.payee));
    }
}
