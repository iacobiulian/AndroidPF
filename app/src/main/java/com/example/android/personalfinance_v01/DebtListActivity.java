package com.example.android.personalfinance_v01;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.example.android.personalfinance_v01.CustomAdapters.DebtAdapter;
import com.github.clans.fab.FloatingActionButton;

import com.example.android.personalfinance_v01.MyClasses.MyUtils;

public class DebtListActivity extends AppCompatActivity {

    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_debt);

        //Floating action button
        fab = findViewById(R.id.debtListFab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyUtils.startActivity(DebtListActivity.this, CreateDebtActivity.class);
            }
        });

        MyUtils.getDebtsFromDatabase(DebtListActivity.this);
        ListView listView =  findViewById(R.id.debtListView);
        DebtAdapter debtAdapter = new DebtAdapter(this, MyUtils.debtList);
        listView.setAdapter(debtAdapter);
    }
}
