package com.example.android.personalfinance_v01;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.Spinner;

import com.example.android.personalfinance_v01.CustomAdapters.ExpenseIncomeAdapter;
import com.example.android.personalfinance_v01.DataPersistance.DatabaseHelper;
import com.example.android.personalfinance_v01.MyClasses.ExpenseIncome;
import com.example.android.personalfinance_v01.MyClasses.MyUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.GregorianCalendar;

public class HistoryActivity extends AppCompatActivity {

    Toolbar toolbar;
    Spinner spinner;

    ListView listView;
    ArrayAdapter<String> listViewAdapter;
    ArrayList<ExpenseIncome> expenseIncomes = new ArrayList<>();

    long startDate = 0;
    long endDate = MyUtils.getCurrentDateTime();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_history);

        initToolbar();

        initSpinner();

        //ListView
        MyUtils.getExpenseIncomeFromDatabase(HistoryActivity.this);

        expenseIncomes.addAll(MyUtils.expenseIncomeList);

        final ExpenseIncomeAdapter expenseIncomeAdapter = new ExpenseIncomeAdapter(this, expenseIncomes);
        listView = findViewById(R.id.historyListView);
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
                            case R.id.menuDelete:
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
                initStartDatePickerDialog();
                break;
            case R.id.actionFilter:
                View filterCategDialogView = getLayoutInflater().inflate(R.layout.dialog_filter_inc_categories, null);
                AlertDialog filterCategAlertDialog = initFilterCategAlertDialog(filterCategDialogView);
                filterCategAlertDialog.show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initStartDatePickerDialog() {
        Calendar cal = Calendar.getInstance();
        final int year = cal.get(Calendar.YEAR);
        final int month = cal.get(Calendar.MONTH);
        final int day = cal.get(Calendar.DAY_OF_MONTH);

        final DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int y, int m, int d) {
                Calendar calendar = new GregorianCalendar(y, m, d);

                startDate = calendar.getTimeInMillis();

                initEndDatePickerDialog(year, month, day);
            }
        };

        DatePickerDialog datePickerDialog = new DatePickerDialog(HistoryActivity.this, android.R.style.Theme_Holo_Dialog_MinWidth,
                dateSetListener, year, month, day);
        datePickerDialog.setTitle("Custom date start");
        datePickerDialog.show();
    }

    private void initEndDatePickerDialog(int year, int month, int day) {

        final DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                Calendar calendar = new GregorianCalendar(year, month, day);

                long oneDay = 24 * 3600 * 1000L;
                endDate = calendar.getTimeInMillis() + oneDay;

                filterExpIncList();
            }
        };

        DatePickerDialog datePickerDialog = new DatePickerDialog(HistoryActivity.this, android.R.style.Theme_Holo_Dialog_MinWidth,
                dateSetListener, year, month, day);
        datePickerDialog.setTitle("Custom date end");
        datePickerDialog.show();
    }

    private AlertDialog initFilterCategAlertDialog(View dialogView) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(HistoryActivity.this);
        alertDialogBuilder.setView(dialogView);

        return alertDialogBuilder.create();
    }

    private void initToolbar() {
        toolbar = findViewById(R.id.historyToolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initSpinner() {
        spinner = findViewById(R.id.historySpinner);

        listViewAdapter = new ArrayAdapter<>(
                this, R.layout.item_spinner_white, getResources().getStringArray(R.array.timeArray));

        listViewAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(listViewAdapter);
        spinner.setSelection(listViewAdapter.getCount() - 1);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        startDate = MyUtils.subtractDaysFromCurrentDateTime(1);
                        break;
                    case 1:
                        startDate = MyUtils.subtractDaysFromCurrentDateTime(7);
                        break;
                    case 2:
                        startDate = MyUtils.subtractDaysFromCurrentDateTime(30);
                        break;
                    case 3:
                        startDate = MyUtils.subtractDaysFromCurrentDateTime(365);
                        break;
                    case 4:
                        startDate = 0L;
                        break;
                }

                filterExpIncList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void deleteExpenseIncome(AdapterView<?> adapterView, int index) {
        ExpenseIncome expenseIncome = (ExpenseIncome) adapterView.getItemAtPosition(index);
        DatabaseHelper databaseHelper = new DatabaseHelper(HistoryActivity.this);

        databaseHelper.deleteExpenseIncome(databaseHelper.getExpenseIncomeID(expenseIncome));

        MyUtils.getExpenseIncomeFromDatabase(HistoryActivity.this);
    }

    private void filterExpIncList() {
        expenseIncomes.clear();
        expenseIncomes.addAll(MyUtils.expenseIncomeList);

        ArrayList<ExpenseIncome> toRemove = new ArrayList<>();

        for (ExpenseIncome item : expenseIncomes) {
            if (!isWithinTimeRange(item)) {
                toRemove.add(item);
            }
        }

        expenseIncomes.removeAll(toRemove);

        listViewAdapter.notifyDataSetChanged();

        MyUtils.makeToast(this, "Showing " + expenseIncomes.size() + " items.");
    }

    private boolean isWithinTimeRange(ExpenseIncome expenseIncome) {
        long date = expenseIncome.getDate();

        return !(date < startDate || date > endDate);

    }
}
