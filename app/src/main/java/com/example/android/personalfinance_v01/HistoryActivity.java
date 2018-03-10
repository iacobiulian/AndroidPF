package com.example.android.personalfinance_v01;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupMenu;

import com.example.android.personalfinance_v01.CustomAdapters.ExpenseIncomeAdapter;
import com.example.android.personalfinance_v01.DataPersistance.DatabaseHelper;
import com.example.android.personalfinance_v01.MyClasses.ExpenseIncome;
import com.example.android.personalfinance_v01.MyClasses.MyUtils;

public class HistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_history);

        //ListView
        MyUtils.getExpenseIncomeFromDatabase(HistoryActivity.this);

        final ExpenseIncomeAdapter expenseIncomeAdapter = new ExpenseIncomeAdapter(this, MyUtils.expenseIncomeList);
        ListView listView = findViewById(R.id.historyListView);
        listView.setAdapter(expenseIncomeAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> adapterView, View view, final int i, long l) {
                final PopupMenu popupMenu = new PopupMenu(HistoryActivity.this, view);
                popupMenu.getMenuInflater().inflate(R.menu.menu_delete_item, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.accountListMenuEdit:
                                deleteExpenseIncome(adapterView, i);
                                expenseIncomeAdapter.notifyDataSetChanged();
                                break;
                        }
                        return false;
                    }
                });

                popupMenu.show();
            }
        });
    }

    private void deleteExpenseIncome(AdapterView<?> adapterView, int index) {
        ExpenseIncome expenseIncome = (ExpenseIncome) adapterView.getItemAtPosition(index);
        DatabaseHelper databaseHelper =  new DatabaseHelper(HistoryActivity.this);

        databaseHelper.deleteExpenseIncome(databaseHelper.getExpenseIncomeID(expenseIncome));
    }
}
