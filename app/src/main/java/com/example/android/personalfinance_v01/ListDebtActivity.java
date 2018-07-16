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

import com.example.android.personalfinance_v01.CustomAdapters.DebtAdapter;
import com.example.android.personalfinance_v01.DataPersistance.DatabaseHelper;
import com.example.android.personalfinance_v01.MyClasses.Debt;
import com.example.android.personalfinance_v01.MyClasses.MyUtils;

public class ListDebtActivity extends AppCompatActivity {

    DebtAdapter debtAdapter;
    FloatingActionButton fab;
    private ListView listView;
    private AlertDialog alertDialog;
    private double paidTowardsDebt = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_debt);

        //Floating action button
        fab = findViewById(R.id.debtListFab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyUtils.startActivity(ListDebtActivity.this, AddDebtActivity.class);
            }
        });

        //List View
        MyUtils.getDebtsFromDatabase(ListDebtActivity.this);
        listView = findViewById(R.id.debtListView);
        debtAdapter = new DebtAdapter(this, MyUtils.debtList);
        listView.setEmptyView(findViewById(R.id.debtListEmptyView));
        listView.setAdapter(debtAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> adapterView, View view, final int i, long l) {
                final Debt currentDebt = (Debt) adapterView.getItemAtPosition(i);

                final PopupMenu popupMenu = new PopupMenu(ListDebtActivity.this, view);
                popupMenu.getMenuInflater().inflate(R.menu.menu_debt_options, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.debtListMenuPay:
                                View dialogView = getLayoutInflater().inflate(R.layout.dialog_enter_amount, listView, false);
                                alertDialog = initAlertDialog(dialogView);
                                initAlertDialogButtons(dialogView, alertDialog, currentDebt);
                                alertDialog.show();
                                break;
                            case R.id.debtListMenuDetails:
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("debt", currentDebt);
                                MyUtils.startActivityWithBundle(ListDebtActivity.this, DetailedDebtTabbedActivity.class,
                                        bundle);
                                break;
                            case R.id.debtListMenuClose:
                                closeDebt(currentDebt);
                                debtAdapter.notifyDataSetChanged();
                                showUndoReachedSnackbar(currentDebt, debtAdapter);
                                break;
                            case R.id.debtListMenuDelete:
                                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        switch (which) {
                                            case DialogInterface.BUTTON_POSITIVE:
                                                debtAdapter.remove(currentDebt);
                                                debtAdapter.notifyDataSetChanged();
                                                showUndoSnackbar(currentDebt, debtAdapter, i);
                                                MyUtils.changeFabAnchor(fab, debtAdapter, R.id.debtListEmptyView, R.id.debtListView);
                                                break;

                                            case DialogInterface.BUTTON_NEGATIVE:
                                                break;
                                        }
                                    }
                                };

                                AlertDialog.Builder builder = new AlertDialog.Builder(ListDebtActivity.this);
                                builder.setMessage(getString(R.string.delete) + " " + getString(R.string.debt) + "?").setPositiveButton(getString(R.string.delete), dialogClickListener)
                                        .setNegativeButton(getString(R.string.cancel), dialogClickListener).show();
                                break;
                        }
                        return false;
                    }
                });

                final PopupMenu popupMenuDelete = new PopupMenu(ListDebtActivity.this, view);
                popupMenuDelete.getMenuInflater().inflate(R.menu.menu_debt_delete_details, popupMenuDelete.getMenu());

                popupMenuDelete.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.menuDebtDetails:
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("debt", currentDebt);
                                MyUtils.startActivityWithBundle(ListDebtActivity.this, DetailedDebtTabbedActivity.class,
                                        bundle);
                                break;
                            case R.id.menuDebtDelete:
                                debtAdapter.remove(currentDebt);
                                debtAdapter.notifyDataSetChanged();
                                showUndoSnackbar(currentDebt, debtAdapter, i);
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

        MyUtils.changeFabAnchor(fab, debtAdapter, R.id.debtListEmptyView, R.id.debtListView);

        showSnackbarIfNeeded();
    }

    private void showUndoReachedSnackbar(final Debt debt, final DebtAdapter debtAdapter) {
        Snackbar snackbar = MyUtils.makeSnackbar(findViewById(R.id.debtRelLay), getString(R.string.debtClosed), Snackbar.LENGTH_LONG);
        snackbar.setAction(getString(R.string.undo), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Undo clicked - reopen the debt
                debt.setClosed(Debt.NOT_CLOSED);
                debt.setAmountPaidBack(paidTowardsDebt);
                debtAdapter.notifyDataSetChanged();
            }
        });

        snackbar.addCallback(new Snackbar.Callback() {

            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                if (event != Snackbar.Callback.DISMISS_EVENT_ACTION) {
                    //Undo not clicked - close debt for real
                    closeDebtDb(debt);
                }
            }

            @Override
            public void onShown(Snackbar snackbar) {
            }
        });

        snackbar.show();
    }

    private void showUndoSnackbar(final Debt debt, final DebtAdapter debtAdapter, final int index) {
        Snackbar snackbar = MyUtils.makeSnackbar(findViewById(R.id.debtRelLay), getString(R.string.debt) + " " + getString(R.string.deleted), Snackbar.LENGTH_LONG);
        snackbar.setAction(getString(R.string.undo), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Undo clicked - add the item back
                debtAdapter.insert(debt, index);
                debtAdapter.notifyDataSetChanged();
                MyUtils.changeFabAnchor(fab, debtAdapter, R.id.debtListEmptyView, R.id.debtListView);
            }
        });

        snackbar.addCallback(new Snackbar.Callback() {

            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                if (event != Snackbar.Callback.DISMISS_EVENT_ACTION) {
                    //Undo not clicked - delete item from db
                    deleteDebt(debt);
                }
            }

            @Override
            public void onShown(Snackbar snackbar) {
            }
        });

        snackbar.show();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (alertDialog != null)
            alertDialog.dismiss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (alertDialog != null)
            alertDialog.dismiss();
    }

    private AlertDialog initAlertDialog(View dialogView) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ListDebtActivity.this);
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
                if (amountInput < 0) {
                    return;
                }
                int debtId = new DatabaseHelper(ListDebtActivity.this).getDebtID(debtForUpdate);
                debtForUpdate.getAddedAmounts().add(amountInput);
                debtForUpdate.getAddedAmountsDates().add(MyUtils.getCurrentDateTime());
                String amounts = MyUtils.fromDoubleListToString(debtForUpdate.getAddedAmounts());
                String times = MyUtils.fromLongListToString(debtForUpdate.getAddedAmountsDates());
                double newAmount = debtForUpdate.getAmountPaidBack() + amountInput;
                modifyDebtAmount(debtId, newAmount);
                updateGoalAmountLists(debtId, amounts, times);
                MyUtils.getDebtsFromDatabase(ListDebtActivity.this);
                alertDialog.hide();
            }
        });
    }

    private void updateGoalAmountLists(int debtId, String amounts, String times) {
        DatabaseHelper databaseHelper = new DatabaseHelper(ListDebtActivity.this);

        databaseHelper.updateDebtAmountsList(debtId, amounts, times);
    }

    private void modifyDebtAmount(int debtId, double newAmount) {
        DatabaseHelper databaseHelper = new DatabaseHelper(ListDebtActivity.this);

        databaseHelper.updateDebtAmount(debtId, newAmount);
    }

    private void closeDebt(Debt debtToBeClosed) {
        double debtAmount = debtToBeClosed.getAmount();
        paidTowardsDebt = debtToBeClosed.getAmountPaidBack();
        debtToBeClosed.setAmountPaidBack(debtAmount);
        debtToBeClosed.setClosed(Debt.CLOSED);
    }

    private void closeDebtDb(Debt debtToBeClosed) {
        DatabaseHelper databaseHelper = new DatabaseHelper(ListDebtActivity.this);
        int id = databaseHelper.getDebtID(debtToBeClosed);

        databaseHelper.updateDebtClose(id);
        databaseHelper.updateDebtAmount(id, debtToBeClosed.getAmount());

        MyUtils.getDebtsFromDatabase(ListDebtActivity.this);
    }

    private void deleteDebt(Debt debtForDeletion) {
        DatabaseHelper databaseHelper = new DatabaseHelper(ListDebtActivity.this);

        databaseHelper.deleteDebt(databaseHelper.getDebtID(debtForDeletion));

        MyUtils.getDebtsFromDatabase(ListDebtActivity.this);
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
            case AddDebtActivity.ERROR_ADD_DEBT:
                snackbar = MyUtils.makeSnackbarError(findViewById(R.id.debtRelLay), getString(R.string.error_debt), Snackbar.LENGTH_LONG);
                snackbar.setAction(R.string.tryAgain, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MyUtils.startActivity(ListDebtActivity.this, AddDebtActivity.class);
                    }
                });
                snackbar.show();
                break;
            case AddDebtActivity.SUCCESS_ADD_DEBT:
                snackbar = MyUtils.makeSnackbar(findViewById(R.id.debtRelLay), getString(R.string.debt_added), Snackbar.LENGTH_SHORT);
                snackbar.show();
                break;
        }
    }
}
