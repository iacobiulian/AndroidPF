package com.example.android.personalfinance_v01;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;

import com.example.android.personalfinance_v01.CustomAdapters.BudgetAdapter;
import com.example.android.personalfinance_v01.DataPersistance.DatabaseHelper;
import com.example.android.personalfinance_v01.MyClasses.Budget;
import com.example.android.personalfinance_v01.MyClasses.MyUtils;
import com.github.clans.fab.FloatingActionButton;

public class ListBudgetActivity extends AppCompatActivity {
    BudgetAdapter budgetAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_budget);

        MyUtils.getBudgetsFromDatabase(ListBudgetActivity.this);

        budgetAdapter = new BudgetAdapter(ListBudgetActivity.this, MyUtils.budgetList);

        //Floating action button
        FloatingActionButton fab = findViewById(R.id.budgetListFab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyUtils.startActivity(ListBudgetActivity.this, AddBudgetActivity.class);
            }
        });

        initListView();
    }

    public void initListView() {
        final ListView listView = findViewById(R.id.budgetListView);
        listView.setEmptyView(findViewById(R.id.budgetListEmptyView));
        listView.setAdapter(budgetAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                final Budget currentBudget = (Budget) adapterView.getItemAtPosition(i);

                final PopupMenu popupMenu = new PopupMenu(ListBudgetActivity.this, view);
                popupMenu.getMenuInflater().inflate(R.menu.menu_budget_edit_delete, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.menuBudgetEdit:
                                View dialogView = getLayoutInflater().inflate(R.layout.dialog_enter_amount, listView, false);
                                AlertDialog alertDialog = initAlertDialog(dialogView);
                                initAlertDialogButtons(dialogView, alertDialog, currentBudget);
                                alertDialog.show();
                                break;
                            case R.id.menuBudgetDetails:
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("budget", currentBudget);
                                MyUtils.startActivityWithBundle(ListBudgetActivity.this, DetailedBudgetTabbedActivity.class,
                                        bundle);
                                break;
                            case R.id.menuBudgetDelete:
                                deleteBudget(currentBudget);
                                break;
                        }
                        budgetAdapter.notifyDataSetChanged();
                        return false;
                    }
                });

                popupMenu.show();
            }
        });

    }

    private AlertDialog initAlertDialog(View dialogView) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ListBudgetActivity.this);
        alertDialogBuilder.setView(dialogView);

        return alertDialogBuilder.create();
    }

    private void initAlertDialogButtons(View dialogView, final AlertDialog alertDialog, final Budget budgetForUpdate) {
        final EditText amountEt = dialogView.findViewById(R.id.dialogAmountEt);
        Button submitBtn = dialogView.findViewById(R.id.dialogBtn);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double amountInput = MyUtils.getDoubleFromEditText(amountEt);
                if (amountInput < 0)
                    return;
                modifyBudgetTotalAmount(budgetForUpdate, amountInput);
                alertDialog.hide();
            }
        });
    }

    private void modifyBudgetTotalAmount(Budget budgetModified, double newTotalAmount) {
        DatabaseHelper databaseHelper = new DatabaseHelper(ListBudgetActivity.this);

        databaseHelper.updateBudgetTotalAmount(databaseHelper.getBudgetId(budgetModified), newTotalAmount);

        MyUtils.getBudgetsFromDatabase(ListBudgetActivity.this);
    }

    private void deleteBudget(Budget budgetForDeletion) {
        DatabaseHelper databaseHelper = new DatabaseHelper(ListBudgetActivity.this);

        databaseHelper.deleteBudget(databaseHelper.getBudgetId(budgetForDeletion));

        MyUtils.getBudgetsFromDatabase(ListBudgetActivity.this);
    }
}
