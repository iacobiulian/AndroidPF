package com.example.android.personalfinance_v01.MyClasses;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.example.android.personalfinance_v01.DataPersistance.DatabaseHelper;
import com.example.android.personalfinance_v01.R;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by iacob on 21 Feb
 */

public class MyUtils {
    public static final String PLUS_SIGN = "+";
    public static final String MINUS_SIGN = "-";
    public static final String INTENT_KEY = "intentCode";

    //Global Balance Account list
    public static ArrayList<BalanceAccount> accountList = new ArrayList<>();
    //Global expense/income list
    public static ArrayList<ExpenseIncome> expenseIncomeList = new ArrayList<>();

    /**
     * Fetches the accounts from the database into the global accountList
     * @param context Activity context
     */
    public static void getBalanceAccountsFromDatabase(Context context) {
        accountList.clear();

        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        Cursor accountData = databaseHelper.getAccountData();

        while (accountData.moveToNext()) {
            String name = accountData.getString(1);
            double balance = accountData.getDouble(2);
            String currency = accountData.getString(3);

            BalanceAccount balanceAccount = new BalanceAccount(name, balance, currency);
            if (MyUtils.accountList.isEmpty()) {
                balanceAccount.setSelected(true);
            }
            MyUtils.accountList.add(balanceAccount);
        }

        if (!accountData.isClosed()) {
            accountData.close();
        }
    }

    /**
     * @return selected BalanceAccount
     */
    @Nullable
    public static BalanceAccount getSelectedAccount() {
        for (BalanceAccount item : accountList) {
            if (item.isSelected())
                return item;
        }
        return null;
    }

    /**
     * @param index of the selected BalanceAccount
     */
    public static void setSelected(int index) {
        for (BalanceAccount item : accountList) {
            item.setSelected(false);
        }
        accountList.get(index).setSelected(true);
    }

    /**
     * Fetches the expenses/incomes from the database into the global expenseIncomeList
     * @param context Activity context
     */
    public static void getExpenseIncomeFromDatabase(Context context) {
        expenseIncomeList.clear();

        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        Cursor expenseIncomeData = databaseHelper.getExpenseIncomeData();

        while (expenseIncomeData.moveToNext()) {
            double balance = expenseIncomeData.getDouble(1);
            int type = expenseIncomeData.getInt(2);
            String categoryName = expenseIncomeData.getString(3);
            long date = expenseIncomeData.getLong(4);
            int accountId = expenseIncomeData.getInt(5);

            BalanceAccount balanceAccount = databaseHelper.getBalanceAccount(accountId);

            Category category;
            if (type == ExpenseIncome.TYPE_INCOME) {
                category = searchIncomeCategoryList(categoryName);
            } else {
                category = searchExpenseCategoryList(categoryName);
            }

            ExpenseIncome expenseIncome = new ExpenseIncome(balance, type, category, date, balanceAccount);

            MyUtils.expenseIncomeList.add(expenseIncome);
        }

        if (!expenseIncomeData.isClosed()) {
            expenseIncomeData.close();
        }
    }

    //region Categories

    public static List<Category> getExpenseCategories() {
        List<Category> expenseCategories = new ArrayList<>();
        expenseCategories.add(new Category("Free time", R.drawable.categ_free_time));
        expenseCategories.add(new Category("Food", R.drawable.categ_food));
        expenseCategories.add(new Category("Housing", R.drawable.categ_housing));
        expenseCategories.add(new Category("Transport", R.drawable.categ_transport));
        return expenseCategories;
    }

    private static Category searchExpenseCategoryList(String categoryName) {
        for (Category item : getExpenseCategories()) {
            if (item.getName().equals(categoryName)) {
                return item;
            }
        }
        return null;
    }

    public static List<Category> getIncomeCategories() {
        List<Category> incomeCategories = new ArrayList<>();
        incomeCategories.add(new Category("Wage", R.drawable.categ_wage));
        incomeCategories.add(new Category("Gifts", R.drawable.categ_gift));
        incomeCategories.add(new Category("Sale", R.drawable.categ_wage));
        incomeCategories.add(new Category("Gambling", R.drawable.categ_free_time));
        return incomeCategories;
    }

    private static Category searchIncomeCategoryList(String categoryName) {
        for (Category item : getIncomeCategories()) {
            if (item.getName().equals(categoryName)) {
                return item;
            }
        }
        return null;
    }
    //endregion

    //region Utility functions

    public static String formatDecimalTwoPlaces(double numberToFormat) {
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(numberToFormat);
    }

    public static String formatDate(long unixTime) {
        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm", Locale.ENGLISH);
        return df.format(unixTime);
    }

    public static void startActivity(Context fromActivity, Class toActivity) {
        Intent intent = new Intent(fromActivity, toActivity);
        fromActivity.startActivity(intent);
    }

    public static void startActivityWithCode(Context fromActivity, Class toActivity, int intentCode) {
        Intent intent = new Intent(fromActivity, toActivity);
        intent.putExtra(MyUtils.INTENT_KEY, intentCode);
        fromActivity.startActivity(intent);
    }

    public static void startActivityWithBundle(Context fromActivity, Class toActivity, Bundle bundle) {
        Intent intent = new Intent(fromActivity, toActivity);
        intent.putExtras(bundle);
        fromActivity.startActivity(intent);
    }
    //endregion
}
