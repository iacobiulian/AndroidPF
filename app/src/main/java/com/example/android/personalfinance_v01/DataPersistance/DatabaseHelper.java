package com.example.android.personalfinance_v01.DataPersistance;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.android.personalfinance_v01.DataPersistance.DatabaseContract.BalanceAccountEntry;
import com.example.android.personalfinance_v01.DataPersistance.DatabaseContract.BudgetEntry;
import com.example.android.personalfinance_v01.DataPersistance.DatabaseContract.DebtEntry;
import com.example.android.personalfinance_v01.DataPersistance.DatabaseContract.ExpenseIncomeEntry;
import com.example.android.personalfinance_v01.DataPersistance.DatabaseContract.GoalEntry;
import com.example.android.personalfinance_v01.DataPersistance.DatabaseContract.TransferEntry;
import com.example.android.personalfinance_v01.MyClasses.BalanceAccount;
import com.example.android.personalfinance_v01.MyClasses.Budget;
import com.example.android.personalfinance_v01.MyClasses.Debt;
import com.example.android.personalfinance_v01.MyClasses.ExpenseIncome;
import com.example.android.personalfinance_v01.MyClasses.Goal;
import com.example.android.personalfinance_v01.MyClasses.MyUtils;
import com.example.android.personalfinance_v01.MyClasses.Transfer;

import java.util.ArrayList;

/**
 * Created by iacob on 03-Mar-18.
 * All database related functions are in this class
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    public DatabaseHelper(Context context) {
        super(context, DatabaseContract.DATABASE_NAME, null, DatabaseContract.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createTableBalanceAccount = "CREATE TABLE " + BalanceAccountEntry.TABLE_NAME + "("
                + BalanceAccountEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + BalanceAccountEntry.COLUMN_ACCOUNT_NAME + " TEXT, "
                + BalanceAccountEntry.COLUMN_BALANCE_AMOUNT + " REAL, "
                + BalanceAccountEntry.COLUMN_CURRENCY_TYPE + " TEXT);";

        String createTableExpenseIncome = "CREATE TABLE " + ExpenseIncomeEntry.TABLE_NAME + "("
                + ExpenseIncomeEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ExpenseIncomeEntry.COLUMN_AMOUNT + " REAL, "
                + ExpenseIncomeEntry.COLUMN_TYPE + " INTEGER, "
                + ExpenseIncomeEntry.COLUMN_CATEGORY + " TEXT, "
                + ExpenseIncomeEntry.COLUMN_DATE_CREATED + " INTEGER, "
                + ExpenseIncomeEntry.COLUMN_ACCOUNT_ID + " INTEGER);";

        String createTableTransfers = "CREATE TABLE " + TransferEntry.TABLE_NAME + "("
                + TransferEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TransferEntry.COLUMN_AMOUNT + " REAL, "
                + TransferEntry.COLUMN_DATE_CREATED + " INTEGER, "
                + TransferEntry.COLUMN_FROM_ACCOUNT_ID + " INTEGER, "
                + TransferEntry.COLUMN_TO_ACCOUNT_ID + " INTEGER);";

        String createTableDebts = "CREATE TABLE " + DebtEntry.TABLE_NAME + "("
                + DebtEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + DebtEntry.COLUMN_TYPE + " INTEGER, "
                + DebtEntry.COLUMN_PAYEE + " TEXT, "
                + DebtEntry.COLUMN_AMOUNT + " REAL, "
                + DebtEntry.COLUMN_AMOUNT_PAID + " REAL, "
                + DebtEntry.COLUMN_CLOSED + " INTEGER, "
                + DebtEntry.COLUMN_DATE_CREATED + " INTEGER, "
                + DebtEntry.COLUMN_DATE_DUE + " INTEGER, "
                + DebtEntry.COLUMN_AMOUNTS_LIST + " TEXT, "
                + DebtEntry.COLUMN_AMOUNTS_TIME_LIST + " TEXT);";

        String createTableGoals = "CREATE TABLE " + GoalEntry.TABLE_NAME + "("
                + GoalEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + GoalEntry.COLUMN_GOAL_NAME + " TEXT, "
                + GoalEntry.COLUMN_TARGET_AMOUNT + " REAL, "
                + GoalEntry.COLUMN_SAVED_AMOUNT + " REAL, "
                + GoalEntry.COLUMN_TARGET_DATE + " INTEGER, "
                + GoalEntry.COLUMN_STATUS + " INTEGER, "
                + GoalEntry.COLUMN_AMOUNTS_LIST + " TEXT, "
                + GoalEntry.COLUMN_AMOUNTS_TIME_LIST + " TEXT);";

        String createTableBudgets = "CREATE TABLE " + BudgetEntry.TABLE_NAME + "("
                + BudgetEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + BudgetEntry.COLUMN_TYPE + " INTEGER, "
                + BudgetEntry.COLUMN_CATEGORY_NAME + " TEXT, "
                + BudgetEntry.COLUMN_TOTAL_AMOUNT + " REAL, "
                + BudgetEntry.COLUMN_CURRENT_AMOUNT + " REAL, "
                + BudgetEntry.COLUMN_DATE + " INTEGER, "
                + BudgetEntry.COLUMN_RESET_DATE + " INTEGER);";

        sqLiteDatabase.execSQL(createTableBalanceAccount);
        sqLiteDatabase.execSQL(createTableExpenseIncome);
        sqLiteDatabase.execSQL(createTableTransfers);
        sqLiteDatabase.execSQL(createTableDebts);
        sqLiteDatabase.execSQL(createTableGoals);
        sqLiteDatabase.execSQL(createTableBudgets);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        String dropBalanceAccount = "DROP TABLE IF EXISTS " + BalanceAccountEntry.TABLE_NAME + ";";
        String dropExpenseIncome = "DROP TABLE IF EXISTS " + ExpenseIncomeEntry.TABLE_NAME + ";";
        String dropTransfer = "DROP TABLE IF EXISTS " + TransferEntry.TABLE_NAME + ";";
        String dropDebt = "DROP TABLE IF EXISTS " + DebtEntry.TABLE_NAME + ";";
        String dropGoal = "DROP TABLE IF EXISTS " + GoalEntry.TABLE_NAME + ";";
        String dropBudget = "DROP TABLE IF EXISTS " + BudgetEntry.TABLE_NAME + ";";
        sqLiteDatabase.execSQL(dropBalanceAccount);
        sqLiteDatabase.execSQL(dropExpenseIncome);
        sqLiteDatabase.execSQL(dropTransfer);
        sqLiteDatabase.execSQL(dropDebt);
        sqLiteDatabase.execSQL(dropGoal);
        sqLiteDatabase.execSQL(dropBudget);
        onCreate(sqLiteDatabase);
    }

    // region Account Database Methods

    /**
     * @param balanceAccount Account added to the database
     * @return true if insertion was successful and false otherwise
     */
    public boolean addAccountData(BalanceAccount balanceAccount) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues balanceAccountContentValues = new ContentValues();
        balanceAccountContentValues.put(BalanceAccountEntry.COLUMN_ACCOUNT_NAME, balanceAccount.getName());
        balanceAccountContentValues.put(BalanceAccountEntry.COLUMN_BALANCE_AMOUNT, balanceAccount.getBalance());
        balanceAccountContentValues.put(BalanceAccountEntry.COLUMN_CURRENCY_TYPE, balanceAccount.getCurrency());

        long result = db.insert(BalanceAccountEntry.TABLE_NAME, null, balanceAccountContentValues);

        return (result != -1);
    }

    /**
     * @return Cursor containing all the balanceAccount data from the database
     */
    public Cursor getAccountData() {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + BalanceAccountEntry.TABLE_NAME + ";";
        return db.rawQuery(selectQuery, null);
    }

    /**
     * @param searchedAccount Account searched
     * @return id of the searchedAccount
     */
    public int getAccountID(BalanceAccount searchedAccount) {
        String selectQuery = "SELECT " + BalanceAccountEntry._ID + " FROM " + BalanceAccountEntry.TABLE_NAME +
                " WHERE " + BalanceAccountEntry.COLUMN_ACCOUNT_NAME + " = '" + searchedAccount.getName() + "' AND "
                + BalanceAccountEntry.COLUMN_BALANCE_AMOUNT + " = '" + searchedAccount.getBalance() + "';";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        int searchedId = -1;
        if (cursor.moveToFirst()) {
            searchedId = cursor.getInt(0);
        }
        cursor.close();

        return searchedId;
    }

    /**
     * @param id of the searched account
     * @return searched BalanceAccount
     */
    public BalanceAccount getBalanceAccount(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM " + BalanceAccountEntry.TABLE_NAME + " WHERE "
                + BalanceAccountEntry._ID + " = '" + id + "';";
        Cursor cursor = db.rawQuery(selectQuery, null);

        BalanceAccount balanceAccount = null;
        ArrayList<BalanceAccount> list = (ArrayList<BalanceAccount>) MyUtils.parseAccountCursor(cursor);

        if (!list.isEmpty()) {
            balanceAccount = list.get(0);
        }

        if (!cursor.isClosed()) {
            cursor.close();
        }

        return balanceAccount;
    }

    /**
     * @param accountId      Database id of the account being updated
     * @param updatedAccount Account containing updated data
     */
    public void updateAccount(int accountId, BalanceAccount updatedAccount) {
        SQLiteDatabase db = this.getWritableDatabase();
        String updateQuery = "UPDATE " + BalanceAccountEntry.TABLE_NAME + " SET "
                + BalanceAccountEntry.COLUMN_ACCOUNT_NAME + " = '" + updatedAccount.getName() + "', "
                + BalanceAccountEntry.COLUMN_BALANCE_AMOUNT + " = '" + updatedAccount.getBalance() + "', "
                + BalanceAccountEntry.COLUMN_CURRENCY_TYPE + " = '" + updatedAccount.getCurrency() + "' "
                + "WHERE " + BalanceAccountEntry._ID + " = '" + accountId + "';";
        db.execSQL(updateQuery);
    }

    /**
     * @param accountId  Database id of the account being updated
     * @param newBalance New balance amount
     */
    public void updateAccountBalanceAmount(int accountId, double newBalance) {
        SQLiteDatabase db = this.getWritableDatabase();
        String updateQuery = "UPDATE " + BalanceAccountEntry.TABLE_NAME + " SET "
                + BalanceAccountEntry.COLUMN_BALANCE_AMOUNT + " = '" + newBalance + "' "
                + "WHERE " + BalanceAccountEntry._ID + " = '" + accountId + "';";
        db.execSQL(updateQuery);
    }

    /**
     * @param id Database id of the account being deleted
     */
    public void deleteAccount(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String deleteQuery = "DELETE FROM " + BalanceAccountEntry.TABLE_NAME + " WHERE "
                + BalanceAccountEntry._ID + " = '" + id + "';";
        db.execSQL(deleteQuery);
    }
    // endregion

    //region Expense/Income Database Methods

    /**
     * @param expenseIncome added to the database
     * @return true if insertion was successful and false otherwise
     */
    public boolean addExpenseIncomeData(ExpenseIncome expenseIncome) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues expenseIncomeContentValues = new ContentValues();
        expenseIncomeContentValues.put(ExpenseIncomeEntry.COLUMN_AMOUNT, expenseIncome.getAmount());
        expenseIncomeContentValues.put(ExpenseIncomeEntry.COLUMN_TYPE, expenseIncome.getType());
        expenseIncomeContentValues.put(ExpenseIncomeEntry.COLUMN_CATEGORY, expenseIncome.getCategory().getName());
        expenseIncomeContentValues.put(ExpenseIncomeEntry.COLUMN_DATE_CREATED, expenseIncome.getDate());
        int id = getAccountID(expenseIncome.getAccount());
        expenseIncomeContentValues.put(ExpenseIncomeEntry.COLUMN_ACCOUNT_ID, id);

        Log.e("DBHELPER", expenseIncome.getAccount().getName() + expenseIncome.getAccount().getBalance() + "ID: " + id);

        long result = db.insert(ExpenseIncomeEntry.TABLE_NAME, null, expenseIncomeContentValues);

        return (result != -1);
    }

    /**
     * @return Cursor containing all the expenseIncome data from the database
     */
    public Cursor getExpenseIncomeData() {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + ExpenseIncomeEntry.TABLE_NAME + ";";
        return db.rawQuery(selectQuery, null);
    }

    /**
     * @param searchedExpenseIncome Expense/Income searched
     * @return id of the searched ExpenseIncome
     */
    public int getExpenseIncomeID(ExpenseIncome searchedExpenseIncome) {
        String selectQuery = "SELECT " + ExpenseIncomeEntry._ID + " FROM " + ExpenseIncomeEntry.TABLE_NAME +
                " WHERE " + ExpenseIncomeEntry.COLUMN_DATE_CREATED + " = '" + searchedExpenseIncome.getDate() + "';";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        int searchedId = -1;
        if (cursor.moveToFirst()) {
            searchedId = cursor.getInt(0);
        }
        cursor.close();

        return searchedId;
    }

    /**
     * @param id Database id of the expense/income being deleted
     */
    public void deleteExpenseIncome(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String deleteQuery = "DELETE FROM " + ExpenseIncomeEntry.TABLE_NAME + " WHERE "
                + ExpenseIncomeEntry._ID + " = '" + id + "';";
        db.execSQL(deleteQuery);
    }
    //endregion

    //region Transfer Database Methods

    /**
     * @param transfer added to the database
     * @return true if insertion was successful and false otherwise
     */
    public boolean addTransferData(Transfer transfer) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues transferContentValues = new ContentValues();
        transferContentValues.put(TransferEntry.COLUMN_AMOUNT, transfer.getAmount());
        transferContentValues.put(TransferEntry.COLUMN_DATE_CREATED, transfer.getCreationDate());
        int fromId = getAccountID(transfer.getFromAccount());
        transferContentValues.put(TransferEntry.COLUMN_FROM_ACCOUNT_ID, fromId);
        int toId = getAccountID(transfer.getToAccount());
        transferContentValues.put(TransferEntry.COLUMN_TO_ACCOUNT_ID, toId);

        long result = db.insert(TransferEntry.TABLE_NAME, null, transferContentValues);

        return (result != -1);
    }

    /**
     * @return Cursor containing all the Transfer data from the database
     */
    public Cursor getTransferData() {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TransferEntry.TABLE_NAME + ";";
        return db.rawQuery(selectQuery, null);
    }

    /**
     * @param searchedTransfer Transfer searched
     * @return id of the searched Transfer
     */
    public int getTransferID(Transfer searchedTransfer) {
        String selectQuery = "SELECT " + TransferEntry._ID + " FROM " + TransferEntry.TABLE_NAME +
                " WHERE " + TransferEntry.COLUMN_DATE_CREATED + " = '" + searchedTransfer.getCreationDate() + "';";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        int searchedId = -1;
        if (cursor.moveToFirst()) {
            searchedId = cursor.getInt(0);
        }
        cursor.close();

        return searchedId;
    }

    /**
     * @param id Database id of the expense/income being deleted
     */
    public void deleteTransfer(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String deleteQuery = "DELETE FROM " + TransferEntry.TABLE_NAME + " WHERE "
                + TransferEntry._ID + " = '" + id + "';";
        db.execSQL(deleteQuery);
    }
    //endregion

    //region Debt Database Methods

    /**
     * @param debt added to the database
     * @return true if insertion was successful and false otherwise
     */
    public boolean addDebtData(Debt debt) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues debtContentValues = new ContentValues();
        debtContentValues.put(DebtEntry.COLUMN_TYPE, debt.getType());
        debtContentValues.put(DebtEntry.COLUMN_PAYEE, debt.getPayee());
        debtContentValues.put(DebtEntry.COLUMN_AMOUNT, debt.getAmount());
        debtContentValues.put(DebtEntry.COLUMN_AMOUNT_PAID, debt.getAmountPaidBack());
        debtContentValues.put(DebtEntry.COLUMN_CLOSED, debt.isClosed());
        debtContentValues.put(DebtEntry.COLUMN_DATE_CREATED, debt.getCreationDate());
        debtContentValues.put(DebtEntry.COLUMN_DATE_DUE, debt.getPaybackDate());
        debtContentValues.put(GoalEntry.COLUMN_AMOUNTS_LIST, "");
        debtContentValues.put(GoalEntry.COLUMN_AMOUNTS_TIME_LIST, "");

        long result = db.insert(DebtEntry.TABLE_NAME, null, debtContentValues);

        return (result != -1);
    }

    /**
     * @return Cursor containing all the debt data from the database
     */
    public Cursor getDebtData() {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + DebtEntry.TABLE_NAME + ";";
        return db.rawQuery(selectQuery, null);
    }

    /**
     * @param searchedDebt Debt searched
     * @return id of the searchedDebt
     */
    public int getDebtID(Debt searchedDebt) {
        String selectQuery = "SELECT " + DebtEntry._ID + " FROM " + DebtEntry.TABLE_NAME +
                " WHERE " + DebtEntry.COLUMN_PAYEE + " = '" + searchedDebt.getPayee() + "' AND "
                + DebtEntry.COLUMN_DATE_DUE + " = '" + searchedDebt.getPaybackDate() + "';";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        int searchedId = -1;
        if (cursor.moveToFirst()) {
            searchedId = cursor.getInt(0);
        }
        cursor.close();

        return searchedId;
    }

    /**
     * @param debtId Database id of the debt being updated
     */
    public void updateDebtClose(int debtId) {
        SQLiteDatabase db = this.getWritableDatabase();
        String updateQuery = "UPDATE " + DebtEntry.TABLE_NAME + " SET "
                + DebtEntry.COLUMN_CLOSED + " = '" + Debt.CLOSED + "' "
                + "WHERE " + DebtEntry._ID + " = '" + debtId + "';";
        db.execSQL(updateQuery);
    }

    /**
     * @param debtId        Database id of the debt being updated
     * @param newAmountPaid Paid amount
     */
    public void updateDebtAmount(int debtId, double newAmountPaid) {
        SQLiteDatabase db = this.getWritableDatabase();
        String updateQuery = "UPDATE " + DebtEntry.TABLE_NAME + " SET "
                + DebtEntry.COLUMN_AMOUNT_PAID + " = '" + newAmountPaid + "' "
                + "WHERE " + DebtEntry._ID + " = '" + debtId + "';";
        db.execSQL(updateQuery);
    }

    public void updateDebtAmountsList(int debtId, String newAmountsList, String newTimeList) {
        SQLiteDatabase db = this.getWritableDatabase();
        String updateQuery = "UPDATE " + DebtEntry.TABLE_NAME + " SET "
                + DebtEntry.COLUMN_AMOUNTS_LIST + " = '" + newAmountsList + "', "
                + DebtEntry.COLUMN_AMOUNTS_TIME_LIST + " = '" + newTimeList + "' "
                + "WHERE " + DebtEntry._ID + " = '" + debtId + "';";
        db.execSQL(updateQuery);
    }

    /**
     * @param id Database id of the account being deleted
     */
    public void deleteDebt(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String deleteQuery = "DELETE FROM " + DebtEntry.TABLE_NAME + " WHERE "
                + DebtEntry._ID + " = '" + id + "';";
        db.execSQL(deleteQuery);
    }

    //endregion

    //region Goal Database Methods

    /**
     * @param goal added to the database
     * @return true if insertion was successful and false otherwise
     */
    public boolean addGoalData(Goal goal) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues goalContentValues = new ContentValues();
        goalContentValues.put(GoalEntry.COLUMN_GOAL_NAME, goal.getName());
        goalContentValues.put(GoalEntry.COLUMN_TARGET_AMOUNT, goal.getGoalAmount());
        goalContentValues.put(GoalEntry.COLUMN_SAVED_AMOUNT, goal.getSavedAmount());
        goalContentValues.put(GoalEntry.COLUMN_TARGET_DATE, goal.getTargetDate());
        goalContentValues.put(GoalEntry.COLUMN_STATUS, goal.getStatus());
        goalContentValues.put(GoalEntry.COLUMN_AMOUNTS_LIST, "");
        goalContentValues.put(GoalEntry.COLUMN_AMOUNTS_TIME_LIST, "");

        long result = db.insert(GoalEntry.TABLE_NAME, null, goalContentValues);

        return (result != -1);
    }

    /**
     * @return Cursor containing all the goal data from the database
     */
    public Cursor getGoalData() {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + GoalEntry.TABLE_NAME + ";";
        return db.rawQuery(selectQuery, null);
    }

    /**
     * @param searchedGoal Goal searched
     * @return id of the searchedGoal
     */
    public int getGoalId(Goal searchedGoal) {
        //TODO: IMPROVE THE SEARCH
        String selectQuery = "SELECT " + GoalEntry._ID + " FROM " + GoalEntry.TABLE_NAME +
                " WHERE " + GoalEntry.COLUMN_GOAL_NAME + " = '" + searchedGoal.getName() + "' AND "
                + GoalEntry.COLUMN_TARGET_DATE + " = '" + searchedGoal.getTargetDate() + "';";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        int searchedId = -1;
        if (cursor.moveToFirst()) {
            searchedId = cursor.getInt(0);
        }
        cursor.close();

        return searchedId;
    }

    /**
     * @param goalId Database id of the goal being updated
     */
    public void updateGoalReached(int goalId) {
        SQLiteDatabase db = this.getWritableDatabase();
        String updateQuery = "UPDATE " + GoalEntry.TABLE_NAME + " SET "
                + GoalEntry.COLUMN_STATUS + " = '" + Goal.REACHED + "' "
                + "WHERE " + GoalEntry._ID + " = '" + goalId + "';";
        db.execSQL(updateQuery);
    }

    /**
     * @param goalId         Database id of the goal being updated
     * @param newSavedAmount Updated saved amount
     */
    public void updateGoalSavedAmount(int goalId, double newSavedAmount) {
        SQLiteDatabase db = this.getWritableDatabase();
        String updateQuery = "UPDATE " + GoalEntry.TABLE_NAME + " SET "
                + GoalEntry.COLUMN_SAVED_AMOUNT + " = '" + newSavedAmount + "' "
                + "WHERE " + GoalEntry._ID + " = '" + goalId + "';";
        db.execSQL(updateQuery);
    }

    public void updateGoalAmountsList(int goalId, String newAmountsList, String newTimeList) {
        SQLiteDatabase db = this.getWritableDatabase();
        String updateQuery = "UPDATE " + GoalEntry.TABLE_NAME + " SET "
                + GoalEntry.COLUMN_AMOUNTS_LIST + " = '" + newAmountsList + "', "
                + GoalEntry.COLUMN_AMOUNTS_TIME_LIST + " = '" + newTimeList + "' "
                + "WHERE " + GoalEntry._ID + " = '" + goalId + "';";
        db.execSQL(updateQuery);
    }
    /**
     * @param id Database id of the goal being deleted
     */
    public void deleteGoal(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String deleteQuery = "DELETE FROM " + GoalEntry.TABLE_NAME + " WHERE "
                + GoalEntry._ID + " = '" + id + "';";
        db.execSQL(deleteQuery);
    }

    //endregion

    //region Budget Database Methods

    /**
     * @param budget added to the database
     * @return true if insertion was successful and false otherwise
     */
    public boolean addBudgetData(Budget budget) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues budgetContentValues = new ContentValues();
        budgetContentValues.put(BudgetEntry.COLUMN_TYPE, budget.getType());
        budgetContentValues.put(BudgetEntry.COLUMN_CATEGORY_NAME, budget.getCategory().getName());
        budgetContentValues.put(BudgetEntry.COLUMN_TOTAL_AMOUNT, budget.getTotalAmount());
        budgetContentValues.put(BudgetEntry.COLUMN_CURRENT_AMOUNT, budget.getCurrentAmount());
        budgetContentValues.put(BudgetEntry.COLUMN_DATE, budget.getCreationDate());
        budgetContentValues.put(BudgetEntry.COLUMN_RESET_DATE, budget.getResetDate());

        long result = db.insert(BudgetEntry.TABLE_NAME, null, budgetContentValues);

        return (result != -1);
    }

    /**
     * @return Cursor containing all the budget data from the database
     */
    public Cursor getBudgetData() {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + BudgetEntry.TABLE_NAME + ";";
        return db.rawQuery(selectQuery, null);
    }

    /**
     * @param searchedBudget Debt searched
     * @return id of the searchedBudget
     */
    public int getBudgetId(Budget searchedBudget) {
        String selectQuery = "SELECT " + BudgetEntry._ID + " FROM " + BudgetEntry.TABLE_NAME +
                " WHERE " + BudgetEntry.COLUMN_DATE + " = '" + searchedBudget.getCreationDate() + "' AND "
                + BudgetEntry.COLUMN_TOTAL_AMOUNT + " = '" + searchedBudget.getTotalAmount() + "';";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        int searchedId = -1;
        if (cursor.moveToFirst()) {
            searchedId = cursor.getInt(0);
        }
        cursor.close();

        return searchedId;
    }

    /**
     * @param budgetId       Database id of the budget being updated
     * @param newTotalAmount New total budget amount
     */
    public void updateBudgetTotalAmount(int budgetId, double newTotalAmount) {
        SQLiteDatabase db = this.getWritableDatabase();
        String updateQuery = "UPDATE " + BudgetEntry.TABLE_NAME + " SET "
                + BudgetEntry.COLUMN_TOTAL_AMOUNT + " = '" + newTotalAmount + "' "
                + "WHERE " + BudgetEntry._ID + " = '" + budgetId + "';";
        db.execSQL(updateQuery);
    }

    /**
     * @param budgetId         Database id of the debt being updated
     * @param newCurrentAmount New currently spent budget amount
     */
    public void updateBudgetCurrentAmount(int budgetId, double newCurrentAmount) {
        SQLiteDatabase db = this.getWritableDatabase();
        String updateQuery = "UPDATE " + BudgetEntry.TABLE_NAME + " SET "
                + BudgetEntry.COLUMN_CURRENT_AMOUNT + " = '" + newCurrentAmount + "' "
                + "WHERE " + BudgetEntry._ID + " = '" + budgetId + "';";
        db.execSQL(updateQuery);
    }

    public void updateBudgetResetDate(int budgetId, long newResetDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        String updateQuery = "UPDATE " + BudgetEntry.TABLE_NAME + " SET "
                + BudgetEntry.COLUMN_RESET_DATE + " = '" + newResetDate + "' "
                + "WHERE " + BudgetEntry._ID + " = '" + budgetId + "';";
        db.execSQL(updateQuery);
    }

    /**
     * @param budgetId Database id of the account being deleted
     */
    public void deleteBudget(int budgetId) {
        SQLiteDatabase db = this.getWritableDatabase();
        String deleteQuery = "DELETE FROM " + BudgetEntry.TABLE_NAME + " WHERE "
                + DebtEntry._ID + " = '" + budgetId + "';";
        db.execSQL(deleteQuery);
    }

    //endregion
}




















