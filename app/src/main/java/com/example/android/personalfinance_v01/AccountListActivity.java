package com.example.android.personalfinance_v01;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.personalfinance_v01.MyClasses.BalanceAccountAdapter;
import com.example.android.personalfinance_v01.MyClasses.MyUtils;
import com.github.clans.fab.FloatingActionButton;

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
        listView = findViewById(R.id.accountListView);
        BalanceAccountAdapter balanceAccountAdapter = new BalanceAccountAdapter(this, MyUtils.accountList);
        listView.setEmptyView(findViewById(R.id.accountListEmptyView));
        listView.setAdapter(balanceAccountAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("accountForEdit", MyUtils.accountList.get(i));
                bundle.putInt("indexForEdit", i);
                MyUtils.startActivityWithBundle(AccountListActivity.this, CreateAccountActivity.class, bundle);
            }
        });
    }
}
