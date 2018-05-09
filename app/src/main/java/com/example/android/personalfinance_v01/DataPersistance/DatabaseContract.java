package com.example.android.personalfinance_v01.DataPersistance;

import android.provider.BaseColumns;

/**
 * Database related names/values are stored here
 */

public final class DatabaseContract {
    public static final String DATABASE_NAME = "personal_finance.db";
    public static final int DATABASE_VERSION = 7;
    private DatabaseContract() {
    }

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

    public final static class TransferEntry implements BaseColumns {
        public static final String TABLE_NAME = "transfers";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_AMOUNT = "amount";
        public static final String COLUMN_DATE_CREATED = "date_created";
        public static final String COLUMN_FROM_ACCOUNT_ID = "balance_account_from_id";
        public static final String COLUMN_TO_ACCOUNT_ID = "balance_account_to_id";
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

    public final static class BudgetEntry implements BaseColumns {
        public static final String TABLE_NAME = "budgets";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_CATEGORY_NAME = "category_name";
        public static final String COLUMN_TOTAL_AMOUNT = "total_amount";
        public static final String COLUMN_CURRENT_AMOUNT = "current_amount";
        public static final String COLUMN_DATE = "creation_date";
        public static final String COLUMN_RESET_DATE = "reset_date";
    }
}
