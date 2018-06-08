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

import com.example.android.personalfinance_v01.CustomAdapters.DebtAdapter;
import com.example.android.personalfinance_v01.DataPersistance.DatabaseHelper;
import com.example.android.personalfinance_v01.MyClasses.Debt;
import com.example.android.personalfinance_v01.MyClasses.MyUtils;
import com.github.clans.fab.FloatingActionButton;

public class DebtListActivity extends AppCompatActivity {

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_debt);

        //Floating action button
        FloatingActionButton fab = findViewById(R.id.debtListFab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyUtils.startActivity(DebtListActivity.this, CreateDebtActivity.class);
            }
        });

        //List View
        MyUtils.getDebtsFromDatabase(DebtListActivity.this);
        listView = findViewById(R.id.debtListView);
        final DebtAdapter debtAdapter = new DebtAdapter(this, MyUtils.debtList);
        listView.setEmptyView(findViewById(R.id.debtListEmptyView));
        listView.setAdapter(debtAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> adapterView, View view, final int i, long l) {
                final Debt currentDebt = (Debt) adapterView.getItemAtPosition(i);

                final PopupMenu popupMenu = new PopupMenu(DebtListActivity.this, view);
                popupMenu.getMenuInflater().inflate(R.menu.menu_debt_options, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.debtListMenuPay:
                                View dialogView = getLayoutInflater().inflate(R.layout.dialog_enter_amount, listView,false);
                                AlertDialog alertDialog = initAlertDialog(dialogView);
                                initAlertDialogButtons(dialogView, alertDialog, currentDebt);
                                alertDialog.show();
                                break;
                            case R.id.debtListMenuClose:
                                closeDebt(currentDebt);
                                debtAdapter.notifyDataSetChanged();
                                break;
                            case R.id.debtListMenuDelete:
                                deleteDebt(currentDebt);
                                debtAdapter.notifyDataSetChanged();
                                break;
                        }
                        return false;
                    }
                });

                final PopupMenu popupMenuDelete = new PopupMenu(DebtListActivity.this, view);
                popupMenuDelete.getMenuInflater().inflate(R.menu.menu_delete_item, popupMenuDelete.getMenu());

                popupMenuDelete.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.menuDelete:
                                deleteDebt(currentDebt);
                                debtAdapter.notifyDataSetChanged();
                                break;
                        }
                        return false;
                    }
                });

                if (currentDebt.isClosed() == Debt.NOT_CLOSED) {
                    popupMenu.show();
                } else {
                    popupMenuDelete.show();
                }

            }
        });
    }

    private AlertDialog initAlertDialog(View dialogView) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(DebtListActivity.this);
        alertDialogBuilder.setView(dialogView);

        return alertDialogBuilder.create();
    }

    private void initAlertDialogButtons(View dialogView, final AlertDialog alertDialog, final Debt debtForUpdate) {
        final EditText amountEt = dialogView.findViewById(R.id.dialogAmountEt);
        Button submitBtn = dialogView.findViewById(R.id.dialogBtn);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double amountInput = MyUtils.getDoubleFromEditText(amountEt);
                double newAmount = debtForUpdate.getAmountPaidBack() + amountInput;
                modifyDebtAmount(debtForUpdate, newAmount);
                alertDialog.hide();
            }
        });
    }

    private void modifyDebtAmount(Debt debtModified, double newAmount) {
        DatabaseHelper databaseHelper = new DatabaseHelper(DebtListActivity.this);

        databaseHelper.updateDebtAmount(databaseHelper.getDebtID(debtModified), newAmount);

        MyUtils.getDebtsFromDatabase(DebtListActivity.this);
    }

    private void closeDebt(Debt debtToBeClosed) {
        DatabaseHelper databaseHelper = new DatabaseHelper(DebtListActivity.this);

        databaseHelper.updateDebtClose(databaseHelper.getDebtID(debtToBeClosed));

        MyUtils.getDebtsFromDatabase(DebtListActivity.this);
    }

    private void deleteDebt(Debt debtForDeletion) {
        DatabaseHelper databaseHelper = new DatabaseHelper(DebtListActivity.this);

        databaseHelper.deleteDebt(databaseHelper.getDebtID(debtForDeletion));

        MyUtils.getDebtsFromDatabase(DebtListActivity.this);
    }
}
