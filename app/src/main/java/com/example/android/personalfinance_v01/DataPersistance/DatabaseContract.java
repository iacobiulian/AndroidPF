package com.example.android.personalfinance_v01.DataPersistance;
import android.provider.BaseColumns;

/**
 * Database related names/values are stored here
 */

public final class DatabaseContract {
    private DatabaseContract() {}

    public static final String DATABASE_NAME = "personal_finance.db";
    public static final int DATABASE_VERSION = 4;

    public final static class BalanceAccountEntry implements BaseColumns {
        public static final String TABLE_NAME = "balance_accounts";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_ACCOUNT_NAME = "name";
        public static final String COLUMN_BALANCE_AMOUNT = "balance";
        public static final String COLUMN_CURRENCY_TYPE = "currency";
    }

    public final static class ExpenseIncomeEntry implements BaseColumns {
        public static final String TABLE_NAME = "expense_income";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_AMOUNT = "amount";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_CATEGORY = "category";
        public static final String COLUMN_DATE_CREATED = "date_created";
        public static final String COLUMN_ACCOUNT_ID = "balance_account_id";
    }

    public final static class DebtEntry implements BaseColumns {
        public static final String TABLE_NAME = "debts";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_PAYEE = "payee";
        public static final String COLUMN_AMOUNT = "amount_initial";
        public static final String COLUMN_AMOUNT_PAID = "amount_paid";
        public static final String COLUMN_CLOSED = "closed";
        public static final String COLUMN_DATE_CREATED = "date_created";
        public static final String COLUMN_DATE_DUE = "date_due";
    }

    public final static class GoalEntry implements BaseColumns {
        public static final String TABLE_NAME = "goals";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_GOAL_NAME = "name";
        public static final String COLUMN_TARGET_AMOUNT = "target_amount";
        public static final String COLUMN_SAVED_AMOUNT = "saved_amount";
        public static final String COLUMN_TARGET_DATE = "target_date";
        public static final String COLUMN_STATUS = "status";
    }
}
