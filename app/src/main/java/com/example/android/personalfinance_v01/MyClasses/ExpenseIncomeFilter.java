package com.example.android.personalfinance_v01.MyClasses;

import com.example.android.personalfinance_v01.ChartsActivity;

import java.util.HashMap;

public class ExpenseIncomeFilter {

    private BalanceAccount balanceAccount; //spinner account (acc1, acc2, all acc etc..)
    private int radioButtonIndex; //time radio button (all, day, week, month, year, custom)
    private long startDate; //if  custom date, date start
    private long endDate; //if custom date, date end
    private int expenseIncomeType;
    private HashMap<String, Boolean> expCategoryMap;
    private HashMap<String, Boolean> incCategoryMap;

    public ExpenseIncomeFilter(BalanceAccount balanceAccount, int radioButtonIndex) {
        this.balanceAccount = balanceAccount;
        this.radioButtonIndex = radioButtonIndex;
        this.startDate = 0;
        this.endDate = MyUtils.getCurrentDateTime();
        expenseIncomeType = ExpenseIncome.TYPE_EXPENSE;

        expCategoryMap = new HashMap<>();
        expCategoryMap.putAll(MyUtils.getExpCategoriesMap());

        incCategoryMap = new HashMap<>();
        incCategoryMap.putAll(MyUtils.getIncCategoriesMap());
    }

    public HashMap<String, Boolean> getExpCategoryMap() {
        return expCategoryMap;
    }

    public HashMap<String, Boolean> getIncCategoryMap() {
        return incCategoryMap;
    }

    public int getRadioButtonIndex() {
        return radioButtonIndex;
    }

    public void setRadioButtonIndex(int radioButtonIndex) {
        this.radioButtonIndex = radioButtonIndex;
    }

    public void setExpenseIncomeType(int expenseIncomeType) {
        this.expenseIncomeType = expenseIncomeType;
    }

    public void setBalanceAccount(BalanceAccount balanceAccount) {
        this.balanceAccount = balanceAccount;
    }

    public boolean isBadCustomDate() {
        if (this.startDate > this.endDate) {
            this.startDate = 0;
            this.endDate = MyUtils.getCurrentDateTime();

            return true;
        }

        return false;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public boolean isGoodExpInc(ExpenseIncome item) {
        if (item.getType() != this.expenseIncomeType) {
            return false;
        }

        if (this.expenseIncomeType == ExpenseIncome.TYPE_EXPENSE) {
            if (!this.expCategoryMap.get(item.getCategory().getName())) {
                return false;
            }
        } else {
            if (!this.incCategoryMap.get(item.getCategory().getName())) {
                return false;
            }
        }

        if (item.getDate() < this.startDate || item.getDate() > this.endDate) {
            return false;
        }

        if (this.balanceAccount.equals(ChartsActivity.ALL_ACCOUNTS_OPTION)) {
            return true;
        }

        if (!(item.getAccount().equals(this.balanceAccount))) {
            return false;
        }

        return true;
    }
}
