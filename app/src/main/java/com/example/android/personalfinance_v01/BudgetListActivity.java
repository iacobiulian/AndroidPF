package com.example.android.personalfinance_v01;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.android.personalfinance_v01.MyClasses.MyUtils;
import com.github.clans.fab.FloatingActionButton;

public class BudgetListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget_list);

        //Floating action button
        FloatingActionButton fab = findViewById(R.id.budgetListFab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyUtils.startActivity(BudgetListActivity.this, AddBudgetActivity.class);
            }
        });
    }
}
