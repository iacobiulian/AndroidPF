package com.example.android.personalfinance_v01;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupMenu;

import com.example.android.personalfinance_v01.CustomAdapters.BalanceAccountAdapter;
import com.example.android.personalfinance_v01.DataPersistance.DatabaseHelper;
import com.example.android.personalfinance_v01.MyClasses.BalanceAccount;
import com.example.android.personalfinance_v01.MyClasses.ExpenseIncome;
import com.example.android.personalfinance_v01.MyClasses.MyUtils;
import com.example.android.personalfinance_v01.MyClasses.Transfer;

public class ListAccountActivity extends AppCompatActivity {

    ListView listView;
    android.support.design.widget.FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_account);

        //Floating action button
        fab = findViewById(R.id.accountListFab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyUtils.startActivity(ListAccountActivity.this, AddAccountActivity.class);
            }
        });

        //ListView
        MyUtils.getBalanceAccountsFromDatabase(ListAccountActivity.this);

        listView = findViewById(R.id.accountListView);
        final BalanceAccountAdapter balanceAccountAdapter = new BalanceAccountAdapter(this, MyUtils.accountList);
        listView.setEmptyView(findViewById(R.id.accountListEmptyView));
        listView.setAdapter(balanceAccountAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> adapterView, View view, final int i, long l) {
                final BalanceAccount currentAccount = (BalanceAccount) adapterView.getItemAtPosition(i);
                final PopupMenu popupMenu = new PopupMenu(ListAccountActivity.this, view);
                popupMenu.getMenuInflater().inflate(R.menu.menu_edit_delete_item, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.optionMenuEdit:
                                editBalanceAccount(currentAccount);
                                break;
                            case R.id.optionMenuHistory:
                                BalanceAccount balanceAccount = (BalanceAccount) adapterView.getItemAtPosition(i);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("acc", balanceAccount);
                                MyUtils.startActivityWithBundle(ListAccountActivity.this, DetailedAccountHistoryActivity.class,
                                        bundle);
                                break;
                            case R.id.optionMenuDelete:
                                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        switch (which) {
                                            case DialogInterface.BUTTON_POSITIVE:
                                                balanceAccountAdapter.remove(currentAccount);
                                                balanceAccountAdapter.notifyDataSetChanged();
                                                showUndoSnackbar(currentAccount, balanceAccountAdapter, i);
                                                break;

                                            case DialogInterface.BUTTON_NEGATIVE:
                                                break;
                                        }
                                    }
                                };

                                AlertDialog.Builder builder = new AlertDialog.Builder(ListAccountActivity.this);
                                builder.setMessage(getString(R.string.delete) + " " + getString(R.string.balanceAccount) + "?").setPositiveButton(getString(R.string.delete), dialogClickListener)
                                        .setNegativeButton(getString(R.string.cancel), dialogClickListener).show();
                                break;
                        }
                        return false;
                    }
                });

                popupMenu.show();
            }
        });

        showSnackbarIfNeeded();
    }

    private void showUndoSnackbar(final BalanceAccount balanceAccount, final BalanceAccountAdapter balanceAccountAdapter, final int index) {
        Snackbar snackbar = MyUtils.makeSnackbar(findViewById(R.id.accountListRelLay), getString(R.string.balanceAccount) + " " + getString(R.string.deleted), Snackbar.LENGTH_LONG);
        snackbar.setAction(getString(R.string.undo), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Undo clicked - add the item back
                balanceAccountAdapter.insert(balanceAccount, index);
                balanceAccountAdapter.notifyDataSetChanged();
            }
        });

        snackbar.addCallback(new Snackbar.Callback() {

            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                if (event != Snackbar.Callback.DISMISS_EVENT_ACTION) {
                    //Undo not clicked - delete item from db
                    deleteBalanceAccount(balanceAccount);
                }
            }

            @Override
            public void onShown(Snackbar snackbar) {
            }
        });

        snackbar.show();
    }

    private void editBalanceAccount(BalanceAccount accountForEdit) {
        DatabaseHelper databaseHelper = new DatabaseHelper(ListAccountActivity.this);

        Bundle bundle = new Bundle();
        bundle.putSerializable("accountForEdit", accountForEdit);
        bundle.putInt("idForEdit", databaseHelper.getAccountID(accountForEdit));

        MyUtils.startActivityWithBundle(ListAccountActivity.this, AddAccountActivity.class, bundle);
    }

    private void deleteBalanceAccount(BalanceAccount accountForDeletion) {
        DatabaseHelper databaseHelper = new DatabaseHelper(ListAccountActivity.this);

        databaseHelper.deleteAccount(databaseHelper.getAccountID(accountForDeletion));

        for (ExpenseIncome expenseIncome : MyUtils.expenseIncomeList) {
            if (expenseIncome.getAccount().equals(accountForDeletion)) {
                databaseHelper.deleteExpenseIncome(databaseHelper.getExpenseIncomeID(expenseIncome));
            }
        }

        for (Transfer transfer : MyUtils.transferList) {
            if (transfer.getToAccount().equals(accountForDeletion) || transfer.getFromAccount().equals(accountForDeletion)) {
                databaseHelper.deleteTransfer(databaseHelper.getTransferID(transfer));
            }
        }

        MyUtils.getBalanceAccountsFromDatabase(ListAccountActivity.this);
    }

    private void showSnackbarIfNeeded() {
        Intent intent = getIntent();
        if (intent == null) {
            return;
        }

        Bundle bundle = getIntent().getExtras();

        if (bundle == null) {
            return;
        }

        int code = bundle.getInt(MyUtils.DONE_CODE);
        final BalanceAccount balanceAccount = (BalanceAccount) bundle.getSerializable(AddAccountActivity.ACCOUNT_FOR_EDIT);
        Snackbar snackbar;

        switch (code) {
            case 0:
                return;
            case AddAccountActivity.ERROR_INPUT_NO_NAME:
                snackbar = MyUtils.makeSnackbarError(findViewById(R.id.accountListRelLay), getString(R.string.error_name), Snackbar.LENGTH_LONG);
                snackbar.setAction(R.string.tryAgain, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (balanceAccount != null) {
                            editBalanceAccount(balanceAccount);
                        } else {
                            MyUtils.startActivity(ListAccountActivity.this, AddAccountActivity.class);
                        }
                    }
                });
                snackbar.show();
                break;
            case AddAccountActivity.SUCCESS_CREATE_ACCOUNT:
                snackbar = MyUtils.makeSnackbar(findViewById(R.id.accountListRelLay), getString(R.string.account_added), Snackbar.LENGTH_SHORT);
                snackbar.show();
                break;
            case AddAccountActivity.SUCCESS_UPDATE_ACCOUNT:
                snackbar = MyUtils.makeSnackbar(findViewById(R.id.accountListRelLay), getString(R.string.account_updated), Snackbar.LENGTH_SHORT);
                snackbar.show();
                break;
        }
    }
}
