package com.example.android.personalfinance_v01;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.android.personalfinance_v01.CustomAdapters.BalanceAccountSpinnerAdapter;
import com.example.android.personalfinance_v01.CustomAdapters.TransferAdapter;
import com.example.android.personalfinance_v01.DataPersistance.DatabaseHelper;
import com.example.android.personalfinance_v01.MyClasses.BalanceAccount;
import com.example.android.personalfinance_v01.MyClasses.MyUtils;
import com.example.android.personalfinance_v01.MyClasses.Transfer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class TransferHistoryActivity extends AppCompatActivity {

    private static BalanceAccount ALL_ACCOUNTS_OPTION = new BalanceAccount("All accounts", 0.0, "");
    TransferFilter transferFilter = new TransferFilter(ALL_ACCOUNTS_OPTION);

    ListView listView;
    TextView emptyListView;
    TransferAdapter transferAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_history);

        MyUtils.getTransfersFromDatabase(TransferHistoryActivity.this);

        initToolbar();
        initSpinner();

        initListView();

        updateTransferList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_calendar, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.actionCalendar:
                View dateDialogView = getLayoutInflater().inflate(R.layout.dialog_choose_date, listView, false);
                AlertDialog dateAlertDialog = initDateAlertDialog(dateDialogView);
                initDateAlertDialogButtons(dateDialogView, dateAlertDialog);
                dateAlertDialog.show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private AlertDialog initDateAlertDialog(View dialogView) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(TransferHistoryActivity.this);
        alertDialogBuilder.setView(dialogView);

        return alertDialogBuilder.create();
    }

    private void initDateAlertDialogButtons(View dialogView, final AlertDialog alertDialog) {
        RadioGroup radioGroup = dialogView.findViewById(R.id.dialogDateRadioGroup);
        radioGroup.check(radioGroup.getChildAt(transferFilter.getRadioButtonIndex()).getId());

        initStartDatePickerDialog((RadioButton) radioGroup.getChildAt(radioGroup.getChildCount() - 1));

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int radioButtonID = radioGroup.getCheckedRadioButtonId();
                RadioButton radioButton = radioGroup.findViewById(radioButtonID);
                int selectedIndex = radioGroup.indexOfChild(radioButton);
                long currentTime = MyUtils.getCurrentDateTime();
                switch (selectedIndex) {
                    case 0:
                        transferFilter.setRadioButtonIndex(0);
                        transferFilter.setStartDate(0L);
                        transferFilter.setEndDate(currentTime);
                        break;
                    case 1:
                        transferFilter.setRadioButtonIndex(1);
                        transferFilter.setStartDate(MyUtils.subtractDaysFromCurrentDateTime(1));
                        transferFilter.setEndDate(currentTime);
                        break;
                    case 2:
                        transferFilter.setRadioButtonIndex(2);
                        transferFilter.setStartDate(MyUtils.subtractDaysFromCurrentDateTime(7));
                        transferFilter.setEndDate(currentTime);
                        break;
                    case 3:
                        transferFilter.setRadioButtonIndex(3);
                        transferFilter.setStartDate(MyUtils.subtractDaysFromCurrentDateTime(30));
                        transferFilter.setEndDate(currentTime);
                        break;
                    case 4:
                        transferFilter.setRadioButtonIndex(4);
                        transferFilter.setStartDate(MyUtils.subtractDaysFromCurrentDateTime(365));
                        transferFilter.setEndDate(currentTime);
                        break;
                    case 5:
                        transferFilter.setRadioButtonIndex(5);
                        break;
                }

                updateTransferList();
                alertDialog.dismiss();
            }
        });
    }

    private void initStartDatePickerDialog(RadioButton radioButton) {
        View.OnClickListener showDatePicker = new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                Calendar cal = Calendar.getInstance();
                final int year = cal.get(Calendar.YEAR);
                final int month = cal.get(Calendar.MONTH);
                final int day = cal.get(Calendar.DAY_OF_MONTH);

                final DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int y, int m, int d) {
                        Calendar calendar = new GregorianCalendar(y, m, d);

                        transferFilter.setStartDate(calendar.getTimeInMillis());
                        transferFilter.setStartDate(calendar.getTimeInMillis());

                        initEndDatePickerDialog(year, month, day);
                    }
                };

                DatePickerDialog datePickerDialog = new DatePickerDialog(TransferHistoryActivity.this, android.R.style.Theme_Holo_Dialog_MinWidth,
                        dateSetListener, year, month, day);
                datePickerDialog.setTitle("Start Date");
                datePickerDialog.show();
            }
        };

        radioButton.setOnClickListener(showDatePicker);
    }

    private void initEndDatePickerDialog(int year, int month, int day) {

        final DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                Calendar calendar = new GregorianCalendar(year, month, day);

                long oneDay = 24 * 3600 * 1000L;
                transferFilter.setEndDate(calendar.getTimeInMillis() + oneDay);

                if (!transferFilter.isBadCustomDate()) {
                    transferFilter.setEndDate(calendar.getTimeInMillis() + oneDay);
                } else {
                    MyUtils.makeToast(TransferHistoryActivity.this, "Start date can not be after end date");
                }

                updateTransferList();
            }
        };

        DatePickerDialog datePickerDialog = new DatePickerDialog(TransferHistoryActivity.this, android.R.style.Theme_Holo_Dialog_MinWidth,
                dateSetListener, year, month, day);
        datePickerDialog.setTitle("End Date");
        datePickerDialog.show();
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.historyTransferToolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initSpinner() {
        Spinner spinner = findViewById(R.id.historyTransferSpinner);

        BalanceAccountSpinnerAdapter balanceAccountSpinnerAdapter = new BalanceAccountSpinnerAdapter(this, MyUtils.accountList);
        balanceAccountSpinnerAdapter.add(ALL_ACCOUNTS_OPTION);
        spinner.setAdapter(balanceAccountSpinnerAdapter);
        spinner.setSelection(balanceAccountSpinnerAdapter.getCount() - 1);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                BalanceAccount balanceAccount = (BalanceAccount) adapterView.getItemAtPosition(i);
                transferFilter.setSpinnerAccount(balanceAccount);
                updateTransferList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void initListView() {
        listView = findViewById(R.id.historyTransferListView);
        emptyListView = findViewById(R.id.historyTransferEmptyTv);
        listView.setEmptyView(emptyListView);
        transferAdapter = new TransferAdapter(TransferHistoryActivity.this, new ArrayList<Transfer>());
        listView.setAdapter(transferAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> adapterView, View view, final int i, long l) {
                final PopupMenu popupMenu = new PopupMenu(TransferHistoryActivity.this, view);
                popupMenu.getMenuInflater().inflate(R.menu.menu_delete_item, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.menuDelete:
                                deleteTransfer(adapterView, i);
                                break;
                        }
                        return false;
                    }
                });

                popupMenu.show();
            }
        });
    }

    public void deleteTransfer(AdapterView<?> adapterView, int index) {
        Transfer transfer = (Transfer) adapterView.getItemAtPosition(index);
        DatabaseHelper databaseHelper = new DatabaseHelper(TransferHistoryActivity.this);

        databaseHelper.deleteTransfer(databaseHelper.getTransferID(transfer));
        updateAccounts(transfer.getFromAccount(), transfer.getToAccount(), transfer.getAmount());

        MyUtils.getTransfersFromDatabase(TransferHistoryActivity.this);

        updateTransferList();
    }

    private void updateAccounts(BalanceAccount fromAcc, BalanceAccount toAcc, double transferAmount) {
        DatabaseHelper databaseHelper = new DatabaseHelper(TransferHistoryActivity.this);
        double newFromAccBalance = fromAcc.getBalance() + transferAmount;
        double newToAccBalance = toAcc.getBalance() - transferAmount;

        databaseHelper.updateAccountBalanceAmount(databaseHelper.getAccountID(fromAcc), newFromAccBalance);
        databaseHelper.updateAccountBalanceAmount(databaseHelper.getAccountID(toAcc), newToAccBalance);
    }

    private ArrayList<Transfer> filterTransferList(ArrayList<Transfer> list, TransferFilter filter) {
        ArrayList<Transfer> filteredList = new ArrayList<>();
        for (Transfer item : list) {
            if (filter.isGoodTransfer(item)) {
                filteredList.add(item);
            }
        }

        MyUtils.makeToast(this, "Showing " + filteredList.size() + " items");

        return filteredList;
    }

    public void updateTransferList() {
        updateListView(filterTransferList(MyUtils.transferList, transferFilter));
    }

    public void updateListView(ArrayList<Transfer> transferList) {
        if (!transferAdapter.isEmpty()) {
            transferAdapter.clear();
        }
        transferAdapter.addAll(transferList);
        transferAdapter.notifyDataSetChanged();
    }

    private class TransferFilter {
        long startDate;
        long endDate;
        BalanceAccount spinnerAccount;
        int radioButtonIndex;

        TransferFilter(BalanceAccount balanceAccount) {
            startDate = 0L;
            endDate = MyUtils.getCurrentDateTime();
            spinnerAccount = balanceAccount;
            radioButtonIndex = 0;
        }

        boolean isGoodTransfer(Transfer item) {
            if (item.getCreationDate() < this.startDate || item.getCreationDate() > this.endDate) {
                return false;
            }

            if (this.spinnerAccount.equals(ALL_ACCOUNTS_OPTION)) {
                return true;
            }

            if (!(this.spinnerAccount.equals(item.getFromAccount())) && !(this.spinnerAccount.equals(item.getToAccount()))) {
                return false;
            }

            return true;
        }

        boolean isBadCustomDate() {
            if (this.startDate > this.endDate) {
                this.startDate = 0;
                this.endDate = MyUtils.getCurrentDateTime();

                return true;
            }

            return false;
        }

        int getRadioButtonIndex() {
            return radioButtonIndex;
        }

        void setRadioButtonIndex(int radioButtonIndex) {
            this.radioButtonIndex = radioButtonIndex;
        }

        void setSpinnerAccount(BalanceAccount spinnerAccount) {
            this.spinnerAccount = spinnerAccount;
        }

        long getStartDate() {
            return startDate;
        }

        void setStartDate(long startDate) {
            this.startDate = startDate;
        }

        long getEndDate() {
            return endDate;
        }

        void setEndDate(long endDate) {
            this.endDate = endDate;
        }
    }
}
