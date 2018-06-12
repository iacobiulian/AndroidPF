package com.example.android.personalfinance_v01;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import com.github.clans.fab.FloatingActionButton;

public class AccountListActivity extends AppCompatActivity {

    private static final String TAG = "AccountListActivity";
    ListView listView;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_account);

        //Floating action button
        fab = findViewById(R.id.accountListFab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyUtils.startActivity(AccountListActivity.this, CreateAccountActivity.class);
            }
        });

        //ListView
        MyUtils.getBalanceAccountsFromDatabase(AccountListActivity.this);

        listView = findViewById(R.id.accountListView);
        final BalanceAccountAdapter balanceAccountAdapter = new BalanceAccountAdapter(this, MyUtils.accountList);
        listView.setEmptyView(findViewById(R.id.accountListEmptyView));
        listView.setAdapter(balanceAccountAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> adapterView, View view, final int i, long l) {
                final PopupMenu popupMenu = new PopupMenu(AccountListActivity.this, view);
                popupMenu.getMenuInflater().inflate(R.menu.menu_edit_delete_item, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.optionMenuEdit:
                                editBalanceAccount(adapterView, i);
                                break;
                            case R.id.optionMenuHistory:
                                BalanceAccount balanceAccount = (BalanceAccount) adapterView.getItemAtPosition(i);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("acc", balanceAccount);
                                MyUtils.startActivityWithBundle(AccountListActivity.this, DetailedAccountHistoryActivity.class,
                                        bundle);
                                break;
                            case R.id.optionMenuDelete:
                                deleteBalanceAccount(adapterView, i);
                                balanceAccountAdapter.notifyDataSetChanged();
                                break;
                        }
                        return false;
                    }
                });

                popupMenu.show();
            }
        });
    }

    private void editBalanceAccount(AdapterView<?> adapterView, int index) {
        BalanceAccount accountForEdit = (BalanceAccount) adapterView.getItemAtPosition(index);
        DatabaseHelper databaseHelper = new DatabaseHelper(AccountListActivity.this);

        Bundle bundle = new Bundle();
        bundle.putSerializable("accountForEdit", accountForEdit);
        bundle.putInt("idForEdit", databaseHelper.getAccountID(accountForEdit));

        MyUtils.startActivityWithBundle(AccountListActivity.this, CreateAccountActivity.class, bundle);
    }

    private void deleteBalanceAccount(AdapterView<?> adapterView, int index) {
        BalanceAccount accountForDeletion = (BalanceAccount) adapterView.getItemAtPosition(index);
        DatabaseHelper databaseHelper = new DatabaseHelper(AccountListActivity.this);

        databaseHelper.deleteAccount(databaseHelper.getAccountID(accountForDeletion));

        for (ExpenseIncome expenseIncome : MyUtils.expenseIncomeList) {
            if (expenseIncome.getAccount().equals(accountForDeletion)) {
                databaseHelper.deleteExpenseIncome(databaseHelper.getExpenseIncomeID(expenseIncome));
            }
        }

        for (Transfer transfer : MyUtils.transferList) {
            if (transfer.getToAccount().equals(accountForDeletion) || transfer.getFromAccount().equals(accountForDeletion)) {
                Log.e(TAG, "FROMACC: " + transfer.getFromAccount().getName() + " TOACC: " + transfer.getToAccount().getName());
                databaseHelper.deleteTransfer(databaseHelper.getTransferID(transfer));
            }
        }

        MyUtils.getBalanceAccountsFromDatabase(AccountListActivity.this);
    }
}
