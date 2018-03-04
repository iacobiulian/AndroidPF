package com.example.android.personalfinance_v01.DataPersistance;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.personalfinance_v01.DataPersistance.DatabaseContract.BalanceAccountEntry;
import com.example.android.personalfinance_v01.MyClasses.BalanceAccount;

/**
 * Created by iacob on 03-Mar-18.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    public DatabaseHelper(Context context) {
        super(context, DatabaseContract.DATABASE_NAME, null, DatabaseContract.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_BALANCEACCOUNT_TABLE = "CREATE TABLE " + BalanceAccountEntry.TABLE_NAME + "("
                + BalanceAccountEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + BalanceAccountEntry.COLUMN_ACCOUNT_NAME + " TEXT, "
                + BalanceAccountEntry.COLUMN_BALANCE_AMOUNT + " REAL, "
                + BalanceAccountEntry.COLUMN_CURRENCY_TYPE + " TEXT);";

        sqLiteDatabase.execSQL(SQL_CREATE_BALANCEACCOUNT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        String upgradeDB = "DROP TABLE IF EXISTS " + BalanceAccountEntry.TABLE_NAME + ";";
        onCreate(sqLiteDatabase);
    }

    /**
     * @param balanceAccount Account added to the database
     * @return false if there was an error inserting
     */
    public boolean addAccountData(BalanceAccount balanceAccount) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(BalanceAccountEntry.COLUMN_ACCOUNT_NAME, balanceAccount.getName());
        contentValues.put(BalanceAccountEntry.COLUMN_BALANCE_AMOUNT, balanceAccount.getBalance());
        contentValues.put(BalanceAccountEntry.COLUMN_CURRENCY_TYPE, balanceAccount.getCurrency());

        long result = db.insert(BalanceAccountEntry.TABLE_NAME, null, contentValues);

        if (result == -1) { //ERROR HERE
            return false;
        }

        return true;
    }

    /**
     * @return Cursor containing all the balanceAccount data from the database
     */
    public Cursor getAccountData() {
        SQLiteDatabase db = this.getReadableDatabase();
        final String SQL_BALANCEACCOUNT_QUERY = "SELECT * FROM " + BalanceAccountEntry.TABLE_NAME + ";";
        Cursor cursor = db.rawQuery(SQL_BALANCEACCOUNT_QUERY, null);
        return cursor;
    }

    /**
     * @param searchedAccount
     * @return id of the searchedAccount
     */
    public int getAccountID(BalanceAccount searchedAccount) {
        String query = "SELECT " + BalanceAccountEntry._ID + " FROM " + BalanceAccountEntry.TABLE_NAME +
                " WHERE " + BalanceAccountEntry.COLUMN_ACCOUNT_NAME + " = '" + searchedAccount.getName() + "' AND "
                + BalanceAccountEntry.COLUMN_BALANCE_AMOUNT + " = '" + searchedAccount.getBalance() + "';";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        int searchedId = -1;
        if (cursor.moveToFirst()) {
            searchedId = cursor.getInt(0);
        }

        return searchedId;
    }

    /**
     * @param accountId      Database id of the account being updated
     * @param updatedAccount Account containing updated data
     */
    public void updateAccount(int accountId, BalanceAccount updatedAccount) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + BalanceAccountEntry.TABLE_NAME + " SET "
                + BalanceAccountEntry.COLUMN_ACCOUNT_NAME + " = '" + updatedAccount.getName() + "', "
                + BalanceAccountEntry.COLUMN_BALANCE_AMOUNT + " = '" + updatedAccount.getBalance() + "', "
                + BalanceAccountEntry.COLUMN_CURRENCY_TYPE + " = '" + updatedAccount.getCurrency() + "' "
                + "WHERE " + BalanceAccountEntry._ID + " = '" + accountId + "';";
        db.execSQL(query);
    }

    /**
     * @param accountId  Database id of the account being updated
     * @param newBalance New balance amount
     */
    public void updateAccountBalanceAmount(int accountId, double newBalance) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + BalanceAccountEntry.TABLE_NAME + " SET "
                + BalanceAccountEntry.COLUMN_BALANCE_AMOUNT + " = '" + newBalance + "' "
                + "WHERE " + BalanceAccountEntry._ID + " = '" + accountId + "';";
        db.execSQL(query);
    }

    /**
     * @param id Database id of the account being deleted
     */
    public void deleteAccount(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + BalanceAccountEntry.TABLE_NAME + " WHERE "
                + BalanceAccountEntry._ID + " = '" + id + "';";
        db.execSQL(query);
    }

}





















