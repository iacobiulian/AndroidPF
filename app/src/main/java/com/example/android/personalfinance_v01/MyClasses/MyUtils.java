package com.example.android.personalfinance_v01.MyClasses;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.example.android.personalfinance_v01.DataPersistance.DatabaseHelper;
import com.example.android.personalfinance_v01.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by iacob on 21-Feb-18.
 */

public class MyUtils {
    public static final String PLUS_SIGN = "+";
    public static final String MINUS_SIGN = "-";
    public static final String INTENT_KEY = "intentCode";

    public static String CURRENCY_TYPE = "EUR";


    //Global Balance Account list
    public static ArrayList<BalanceAccount> accountList = new ArrayList<>();

    /**
     * Puts the accounts from the database into the global accountList
     * @param context
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
    }

    /**
     * @return selected BalanceAccount
     */
    @Nullable
    public static BalanceAccount getSelected() {
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

    public static ArrayList<ExpenseIncome> expenseIncomeList = new ArrayList<>();

    public static List<Category> getExpenseCategories() {
        List<Category> expenseCategories = new ArrayList<>();
        expenseCategories.add(new Category("Free time", R.drawable.categ_free_time));
        expenseCategories.add(new Category("Food", R.drawable.categ_food));
        expenseCategories.add(new Category("Housing", R.drawable.categ_housing));
        expenseCategories.add(new Category("Transport", R.drawable.categ_transport));
        return expenseCategories;
    }

    public static List<Category> getIncomeCategories() {
        List<Category> incomeCategories = new ArrayList<>();
        incomeCategories.add(new Category("Wage", R.drawable.categ_wage));
        incomeCategories.add(new Category("Gifts", R.drawable.categ_gift));
        incomeCategories.add(new Category("Sale", R.drawable.categ_wage));
        incomeCategories.add(new Category("Gambling", R.drawable.categ_free_time));
        return incomeCategories;
    }

    //UTILITY FUNCTIONS

    public static String formatDecimalTwoPlaces(double numberToFormat) {
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(numberToFormat);
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
}
