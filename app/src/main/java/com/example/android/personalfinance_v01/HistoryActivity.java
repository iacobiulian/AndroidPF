package com.example.android.personalfinance_v01;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.personalfinance_v01.MyClasses.ExpenseIncomeAdapter;
import com.example.android.personalfinance_v01.MyClasses.MyUtils;

public class HistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        ExpenseIncomeAdapter expenseIncomeAdapter = new ExpenseIncomeAdapter(this, MyUtils.expenseIncomeList);
        final ListView listView = findViewById(R.id.historyListView);
        listView.setAdapter(expenseIncomeAdapter);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                MyUtils.expenseIncomeList.remove(i);
                recreate();
                return true;
            }
        });
    }
}
