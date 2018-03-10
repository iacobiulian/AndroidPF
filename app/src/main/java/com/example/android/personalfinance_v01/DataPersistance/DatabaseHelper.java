package com.example.android.personalfinance_v01.DataPersistance;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.android.personalfinance_v01.DataPersistance.DatabaseContract.BalanceAccountEntry;
import com.example.android.personalfinance_v01.DataPersistance.DatabaseContract.DebtEntry;
import com.example.android.personalfinance_v01.DataPersistance.DatabaseContract.ExpenseIncomeEntry;
import com.example.android.personalfinance_v01.MyClasses.BalanceAccount;
import com.example.android.personalfinance_v01.MyClasses.Debt;
import com.example.android.personalfinance_v01.MyClasses.ExpenseIncome;

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

        String createTableDebts = "CREATE TABLE " + DebtEntry.TABLE_NAME + "("
                + DebtEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + DebtEntry.COLUMN_TYPE + " INTEGER, "
                + DebtEntry.COLUMN_PAYEE + " TEXT, "
                + DebtEntry.COLUMN_AMOUNT + " REAL, "
                + DebtEntry.COLUMN_AMOUNT_PAID + " REAL, "
                + DebtEntry.COLUMN_CLOSED + " INTEGER, "
                + DebtEntry.COLUMN_DATE_CREATED + " INTEGER, "
                + DebtEntry.COLUMN_DATE_DUE + " INTEGER);";

        sqLiteDatabase.execSQL(createTableBalanceAccount);
        sqLiteDatabase.execSQL(createTableExpenseIncome);
        sqLiteDatabase.execSQL(createTableDebts);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        String dropBalanceAccount = "DROP TABLE IF EXISTS " + BalanceAccountEntry.TABLE_NAME + ";";
        String dropExpenseIncome = "DROP TABLE IF EXISTS " + ExpenseIncomeEntry.TABLE_NAME + ";";
        String dropDebt = "DROP TABLE IF EXISTS " + DebtEntry.TABLE_NAME + ";";
        sqLiteDatabase.execSQL(dropBalanceAccount);
        sqLiteDatabase.execSQL(dropExpenseIncome);
        sqLiteDatabase.execSQL(dropDebt);
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

        if (cursor.moveToFirst()) {
            String name = cursor.getString(1);
            double balance = cursor.getDouble(2);
            String currency = cursor.getString(3);

            balanceAccount = new BalanceAccount(name, balance, currency);
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

    //region Debt Database Methods

    /**
     * @param debt Account added to the database
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
     * @param id of the searched debt
     * @return searched Debt
     */
    public Debt getDebt(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM " + DebtEntry.TABLE_NAME + " WHERE "
                + DebtEntry._ID + " = '" + id + "';";
        Cursor cursor = db.rawQuery(selectQuery, null);

        Debt debt = null;

        if (cursor.moveToFirst()) {
            int type = cursor.getInt(1);
            String payee = cursor.getString(2);
            double amount = cursor.getDouble(3);
            double amountPaid = cursor.getDouble(4);
            int closed = cursor.getInt(5);
            long dateCreated = cursor.getLong(6);
            long dateDue = cursor.getLong(7);

            debt = new Debt(type, payee, amount, amountPaid, closed, dateCreated, dateDue);
        }

        if (!cursor.isClosed()) {
            cursor.close();
        }

        return debt;
    }

    /**
     * @param debtId      Database id of the debt being updated
     * @param updatedDebt Debt containing updated data
     */
    public void updateDebt(int debtId, Debt updatedDebt) {
        SQLiteDatabase db = this.getWritableDatabase();
        String updateQuery = "UPDATE " + DebtEntry.TABLE_NAME + " SET "
                + DebtEntry.COLUMN_TYPE + " = '" + updatedDebt.getType() + "', "
                + DebtEntry.COLUMN_PAYEE + " = '" + updatedDebt.getPayee() + "', "
                + DebtEntry.COLUMN_AMOUNT + " = '" + updatedDebt.getAmount() + "', "
                + DebtEntry.COLUMN_AMOUNT_PAID + " = '" + updatedDebt.getAmountPaidBack() + "', "
                + DebtEntry.COLUMN_CLOSED + " = '" + updatedDebt.isClosed() + "', "
                + DebtEntry.COLUMN_DATE_CREATED + " = '" + updatedDebt.getCreationDate() + "', "
                + DebtEntry.COLUMN_DATE_DUE + " = '" + updatedDebt.getPaybackDate() + "' "
                + "WHERE " + DebtEntry._ID + " = '" + debtId + "';";
        db.execSQL(updateQuery);
    }

    /**
     * @param debtId  Database id of the debt being updated
     * @param newAmountPaid Paid amount
     */
    public void updateDebtAmount(int debtId, double newAmountPaid) {
        SQLiteDatabase db = this.getWritableDatabase();
        String updateQuery = "UPDATE " + DebtEntry.TABLE_NAME + " SET "
                + DebtEntry.COLUMN_AMOUNT_PAID + " = '" + newAmountPaid + "' "
                + "WHERE " + BalanceAccountEntry._ID + " = '" + debtId + "';";
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
}





















