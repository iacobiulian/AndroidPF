package com.example.android.personalfinance_v01;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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

        showSnackbarIfNeeded();
    }

    public void initListView() {
        final ListView listView = findViewById(R.id.budgetListView);
        listView.setEmptyView(findViewById(R.id.budgetListEmptyView));
        listView.setAdapter(budgetAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {

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
                                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        switch (which) {
                                            case DialogInterface.BUTTON_POSITIVE:
                                                budgetAdapter.remove(currentBudget);
                                                budgetAdapter.notifyDataSetChanged();
                                                showUndoSnackbar(currentBudget, budgetAdapter, i);
                                                break;

                                            case DialogInterface.BUTTON_NEGATIVE:
                                                break;
                                        }
                                    }
                                };

                                AlertDialog.Builder builder = new AlertDialog.Builder(ListBudgetActivity.this);
                                builder.setMessage(getString(R.string.delete) + " " + getString(R.string.budget) + "?").setPositiveButton(getString(R.string.delete), dialogClickListener)
                                        .setNegativeButton(getString(R.string.cancel), dialogClickListener).show();
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

    private void showUndoSnackbar(final Budget budget, final BudgetAdapter budgetAdapter, final int index) {
        Snackbar snackbar = MyUtils.makeSnackbar(findViewById(R.id.budgetListRelLay), getString(R.string.budget) + " " + getString(R.string.deleted), Snackbar.LENGTH_LONG);
        snackbar.setAction(getString(R.string.undo), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Undo clicked - add the item back
                budgetAdapter.insert(budget, index);
                budgetAdapter.notifyDataSetChanged();
            }
        });

        snackbar.addCallback(new Snackbar.Callback() {

            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                if (event != Snackbar.Callback.DISMISS_EVENT_ACTION) {
                    //Undo not clicked - delete item from db
                    deleteBudget(budget);
                }
            }

            @Override
            public void onShown(Snackbar snackbar) {
            }
        });

        snackbar.show();
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

    private void showSnackbarIfNeeded() {
        Intent intent = getIntent();
        if (intent == null) {
            return;
        }

        int code = intent.getIntExtra(MyUtils.INTENT_KEY, 0);
        Snackbar snackbar;

        switch (code) {
            case 0:
                return;
            case AddBudgetActivity.ERROR_AMOUNT:
                snackbar = MyUtils.makeSnackbarError(findViewById(R.id.budgetListRelLay), getString(R.string.error_amount), Snackbar.LENGTH_LONG);
                snackbar.setAction(R.string.tryAgain, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MyUtils.startActivity(ListBudgetActivity.this, AddBudgetActivity.class);
                    }
                });
                snackbar.show();
                break;
            case AddBudgetActivity.SUCCESS_ADD_BUDGET:
                snackbar = MyUtils.makeSnackbar(findViewById(R.id.budgetListRelLay), getString(R.string.budgetAdded), Snackbar.LENGTH_SHORT);
                snackbar.show();
                break;
        }
    }
}
