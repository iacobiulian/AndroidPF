package com.example.android.personalfinance_v01.MyClasses;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.personalfinance_v01.DataPersistance.DatabaseHelper;
import com.example.android.personalfinance_v01.HistoryTabbedActivity;
import com.example.android.personalfinance_v01.R;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by iacob on 21 Feb
 */

public class MyUtils {
    public static final String PLUS_SIGN = "+";
    public static final String MINUS_SIGN = "-";
    public static final String INTENT_KEY = "intentCode";
    private static final String STRING_DELIMITER = ",";

    //Global Balance Account list
    public static ArrayList<BalanceAccount> accountList = new ArrayList<>();
    //Global expense/income list
    public static ArrayList<ExpenseIncome> expenseIncomeList = new ArrayList<>();
    //Global Transfer list
    public static ArrayList<Transfer> transferList = new ArrayList<>();
    //Global Budget list
    public static ArrayList<Budget> budgetList = new ArrayList<>();
    //Global debt list
    public static ArrayList<Debt> debtList = new ArrayList<>();
    //Global Goal list
    public static ArrayList<Goal> goalList = new ArrayList<>();

    public static List<BalanceAccount> parseAccountCursor(Cursor cursor) {

        ArrayList<BalanceAccount> list = new ArrayList<>();

        while (cursor.moveToNext()) {
            String name = cursor.getString(1);
            double balance = cursor.getDouble(2);
            String currency = cursor.getString(3);

            list.add(new BalanceAccount(name, balance, currency));
        }

        return list;
    }

    private static List<ExpenseIncome> parseExpenseIncomeCursor(Cursor cursor, Context context) {
        ArrayList<ExpenseIncome> list = new ArrayList<>();
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        if (!cursor.moveToLast())
            return list;

        do {
            double balance = cursor.getDouble(1);
            int type = cursor.getInt(2);
            String categoryName = cursor.getString(3);
            long date = cursor.getLong(4);
            int accountId = cursor.getInt(5);

            BalanceAccount balanceAccount = databaseHelper.getBalanceAccount(accountId);

            Category category;
            if (type == ExpenseIncome.TYPE_INCOME) {
                category = searchIncomeCategoryList(categoryName);
            } else {
                category = searchExpenseCategoryList(categoryName);
            }

            ExpenseIncome expenseIncome = new ExpenseIncome(balance, type, category, date, balanceAccount);

            list.add(expenseIncome);
        }
        while (cursor.moveToPrevious());

        return list;
    }

    private static List<Transfer> parseTransferCursor(Cursor cursor, Context context) {
        ArrayList<Transfer> list = new ArrayList<>();
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        if (!cursor.moveToLast())
            return list;

        do {
            double balance = cursor.getDouble(1);
            long date = cursor.getLong(2);
            int fromAccountId = cursor.getInt(3);
            int toAccountId = cursor.getInt(4);

            BalanceAccount fromAcc = databaseHelper.getBalanceAccount(fromAccountId);
            BalanceAccount toAcc = databaseHelper.getBalanceAccount(toAccountId);

            Transfer transfer = new Transfer(balance, date, fromAcc, toAcc);

            list.add(transfer);
        }
        while (cursor.moveToPrevious());

        return list;
    }

    private static List<Debt> parseDebtCursor(Cursor cursor) {

        ArrayList<Debt> list = new ArrayList<>();

        while (cursor.moveToNext()) {
            int type = cursor.getInt(1);
            String payee = cursor.getString(2);
            double amount = cursor.getDouble(3);
            double amountPaid = cursor.getDouble(4);
            int closed = cursor.getInt(5);
            long dateCreated = cursor.getLong(6);
            long dateDue = cursor.getLong(7);
            String amountsList = cursor.getString(8);
            String timesList = cursor.getString(9);

            ArrayList<Double> amounts = fromStringToDoubleList(amountsList);
            ArrayList<Long> times = fromStringToLongList(timesList);

            list.add(new Debt(type, payee, amount, amountPaid, closed, dateCreated, dateDue, amounts, times));
        }

        return list;
    }

    private static List<Goal> parseGoalCursor(Cursor cursor) {

        ArrayList<Goal> list = new ArrayList<>();

        while (cursor.moveToNext()) {
            String name = cursor.getString(1);
            double targetAmount = cursor.getDouble(2);
            double savedAmount = cursor.getDouble(3);
            long targetDate = cursor.getLong(4);
            int status = cursor.getInt(5);
            String amountsList = cursor.getString(6);
            String timesList = cursor.getString(7);

            ArrayList<Double> amounts = fromStringToDoubleList(amountsList);
            ArrayList<Long> times = fromStringToLongList(timesList);

            list.add(new Goal(name, targetAmount, savedAmount, targetDate, status, amounts, times));
        }

        return list;
    }

    private static List<Budget> parseBudgetCursor(Cursor cursor) {

        ArrayList<Budget> list = new ArrayList<>();

        while (cursor.moveToNext()) {
            int type = cursor.getInt(1);
            String categoryName = cursor.getString(2);
            double totalAmount = cursor.getDouble(3);
            double currentAmount = cursor.getDouble(4);
            long dateCreated = cursor.getLong(5);
            long resetDate = cursor.getLong(6);

            list.add(new Budget(type, searchExpenseCategoryList(categoryName), totalAmount, currentAmount, dateCreated, resetDate));
        }

        return list;
    }

    /**
     * Fetches the accounts from the database into the global accountList
     *
     * @param context Activity context
     */
    public static void getBalanceAccountsFromDatabase(Context context) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        Cursor accountData = databaseHelper.getAccountData();

        accountList.clear();
        accountList.addAll(parseAccountCursor(accountData));

        MyUtils.setSelected(0);

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
        if (accountList.isEmpty()) {
            return;
        }

        for (BalanceAccount item : accountList) {
            item.setSelected(false);
        }
        accountList.get(index).setSelected(true);
    }

    /**
     * Fetches the expenses/incomes from the database into the global expenseIncomeList
     *
     * @param context Activity context
     */
    public static void getExpenseIncomeFromDatabase(Context context) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        Cursor expenseIncomeData = databaseHelper.getExpenseIncomeData();

        expenseIncomeList.clear();
        expenseIncomeList.addAll(parseExpenseIncomeCursor(expenseIncomeData, context));

        if (!expenseIncomeData.isClosed()) {
            expenseIncomeData.close();
        }
    }

    /**
     * Fetches the transfers from the database into the global transferList
     *
     * @param context Activity context
     */
    public static void getTransfersFromDatabase(Context context) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        Cursor transferData = databaseHelper.getTransferData();

        transferList.clear();
        transferList.addAll(parseTransferCursor(transferData, context));

        if (!transferData.isClosed()) {
            transferData.close();
        }
    }

    /**
     * Fetches the debts from the database into the global debtList
     *
     * @param context Activity context
     */
    public static void getDebtsFromDatabase(Context context) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        Cursor debtData = databaseHelper.getDebtData();

        debtList.clear();
        debtList.addAll(parseDebtCursor(debtData));

        if (!debtData.isClosed()) {
            debtData.close();
        }
    }

    /**
     * Fetches the goals from the database into the global goalList
     *
     * @param context Activity context
     */
    public static void getGoalsFromDatabase(Context context) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        Cursor goalData = databaseHelper.getGoalData();

        goalList.clear();
        goalList.addAll(parseGoalCursor(goalData));

        if (!goalData.isClosed()) {
            goalData.close();
        }
    }

    /**
     * Fetches the debts from the database into the global debtList
     *
     * @param context Activity context
     */
    public static void getBudgetsFromDatabase(Context context) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        Cursor budgetData = databaseHelper.getBudgetData();

        budgetList.clear();
        budgetList.addAll(parseBudgetCursor(budgetData));

        if (!budgetData.isClosed()) {
            budgetData.close();
        }
    }

    //region Categories

    public static void modifyBudgetCurrentAmount(Context context, Budget budgetModified, double newCurrentAmount) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);

        databaseHelper.updateBudgetCurrentAmount(databaseHelper.getBudgetId(budgetModified), newCurrentAmount);
    }

    public static void modifyBudgetResetDate(Context context, Budget budgetModified, long newResetDate) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);

        databaseHelper.updateBudgetResetDate(databaseHelper.getBudgetId(budgetModified), newResetDate);
    }

    public static List<Category> getExpenseCategories() {
        List<Category> expenseCategories = new ArrayList<>();
        expenseCategories.add(new Category("Communication, PC", R.drawable.categ_pc));
        expenseCategories.add(new Category("Fees, Fines", R.drawable.categ_fees_fines));
        expenseCategories.add(new Category("Food", R.drawable.categ_food));
        expenseCategories.add(new Category("Health care", R.drawable.categ_health));
        expenseCategories.add(new Category("Hobbies", R.drawable.categ_free_time));
        expenseCategories.add(new Category("Housing", R.drawable.categ_housing));
        expenseCategories.add(new Category("Transportation", R.drawable.categ_travel));
        expenseCategories.add(new Category("Vehicle", R.drawable.categ_vehicle));
        expenseCategories.add(new Category("Other", R.drawable.ic_dollar_sign));
        return expenseCategories;
    }

    public static HashMap<String, Boolean> getExpCategoriesMap() {
        HashMap<String, Boolean> hashMap = new HashMap<>();
        for (Category item : getExpenseCategories()) {
            hashMap.put(item.getName(), true);
        }

        return hashMap;
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
        incomeCategories.add(new Category("Sale", R.drawable.categ_sale));
        incomeCategories.add(new Category("Gambling", R.drawable.categ_gambling));
        incomeCategories.add(new Category("Other", R.drawable.ic_dollar_sign));
        return incomeCategories;
    }

    public static HashMap<String, Boolean> getIncCategoriesMap() {
        HashMap<String, Boolean> hashMap = new HashMap<>();
        for (Category item : getIncomeCategories()) {
            hashMap.put(item.getName(), true);
        }

        return hashMap;
    }
    //endregion

    //region Utility functions

    private static Category searchIncomeCategoryList(String categoryName) {
        for (Category item : getIncomeCategories()) {
            if (item.getName().equals(categoryName)) {
                return item;
            }
        }
        return null;
    }

    public static void createNotification(Context context, Intent intent, String title, String content, int iconId) {
        //TODO NOTIFICATIONS WHERE NEEDED

        //When budget resets without exceeding it: *smiley face* Congratulations
        Intent backIntent = new Intent(context, HistoryTabbedActivity.class);
        backIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivities(context, 0,
                new Intent[]{backIntent, intent}, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, "default")
                .setSmallIcon(iconId)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(1, mBuilder.build());
    }

    public static String formatDecimalOnePlace(double numberToFormat) {
        DecimalFormat df = new DecimalFormat("0.0");
        return df.format(numberToFormat);
    }

    public static String formatDecimalTwoPlaces(double numberToFormat) {
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(numberToFormat);
    }

    /**
     * @return current unix time
     */
    public static long getCurrentDateTime() {
        return Calendar.getInstance().getTime().getTime() + 10800000;
    }

    public static long subtractDaysFromCurrentDateTime(int days) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -days);
        return cal.getTime().getTime();
    }

    public static String formatDateWithTime(long unixTime) {
        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm", Locale.ENGLISH);
        return df.format(unixTime);
    }

    public static String formatDateWithoutTime(long unixTime) {
        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy", Locale.ENGLISH);
        return df.format(unixTime);
    }

    public static String formatDateDayMonth(long unixTime) {
        DateFormat df = new SimpleDateFormat("d MMM", Locale.ENGLISH);
        return df.format(unixTime);
    }

    public static float returnFloat(double d) {
        return (float) d;
    }

    public static void makeToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static double getDoubleFromEditText(EditText editText) {
        double value = -1.0;
        try {
            value = Double.parseDouble(editText.getText().toString());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return value;
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



    public static String fromDoubleListToString(ArrayList<Double> arr) {
        if (arr.size() == 0) {
            return "";
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (double number : arr) {
            stringBuilder.append(number);
            stringBuilder.append(STRING_DELIMITER);
        }

        return stringBuilder.toString();
    }

    public static ArrayList<Double> fromStringToDoubleList(String s) {
        ArrayList<Double> arr = new ArrayList<>();

        if(!TextUtils.isEmpty(s)) {
            String[] stringArr = s.split(STRING_DELIMITER);

            for (String item : stringArr) {
                arr.add(Double.parseDouble(item));
            }
        }

        return arr;
    }

    public static String fromLongListToString(ArrayList<Long> arr) {
        if (arr.size() == 0) {
            return "";
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (long number : arr) {
            stringBuilder.append(number);
            stringBuilder.append(STRING_DELIMITER);
        }

        return stringBuilder.toString();
    }

    public static ArrayList<Long> fromStringToLongList(String s) {
        ArrayList<Long> arr = new ArrayList<>();

        if(!TextUtils.isEmpty(s)) {
            String[] stringArr = s.split(STRING_DELIMITER);

            for (String item : stringArr) {
                arr.add(Long.parseLong(item));
            }
        }

        return arr;
    }
    //endregion
}
