package com.example.android.personalfinance_v01;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.personalfinance_v01.DataPersistance.DatabaseHelper;
import com.example.android.personalfinance_v01.MyClasses.BalanceAccount;
import com.example.android.personalfinance_v01.MyClasses.BalanceAccountAdapter;
import com.example.android.personalfinance_v01.MyClasses.MyUtils;
import com.github.clans.fab.FloatingActionButton;

import java.io.Serializable;

public class AccountListActivity extends AppCompatActivity {

    ListView listView;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_list);

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
        BalanceAccountAdapter balanceAccountAdapter = new BalanceAccountAdapter(this, MyUtils.accountList);
        listView.setEmptyView(findViewById(R.id.accountListEmptyView));
        listView.setAdapter(balanceAccountAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                BalanceAccount accountForEdit = (BalanceAccount) adapterView.getItemAtPosition(i);
                DatabaseHelper databaseHelper =  new DatabaseHelper(AccountListActivity.this);

                Bundle bundle = new Bundle();
                bundle.putSerializable("accountForEdit", accountForEdit);
                bundle.putInt("idForEdit", databaseHelper.getAccountID(accountForEdit));

                MyUtils.startActivityWithBundle(AccountListActivity.this, CreateAccountActivity.class, bundle);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                BalanceAccount accountForDeletion = (BalanceAccount) adapterView.getItemAtPosition(i);
                DatabaseHelper databaseHelper =  new DatabaseHelper(AccountListActivity.this);

                databaseHelper.deleteAccount(databaseHelper.getAccountID(accountForDeletion));

                recreate();

                return true;
            }
        });
    }
}
