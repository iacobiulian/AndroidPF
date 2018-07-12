package com.example.android.personalfinance_v01;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.example.android.personalfinance_v01.CustomAdapters.BalanceAccountSpinnerAdapter;
import com.example.android.personalfinance_v01.CustomAdapters.ExpenseIncomePagerAdapter;
import com.example.android.personalfinance_v01.DataPersistance.DatabaseHelper;
import com.example.android.personalfinance_v01.Fragments.HistoryExpenseFragment;
import com.example.android.personalfinance_v01.Fragments.HistoryIncomeFragment;
import com.example.android.personalfinance_v01.MyClasses.BalanceAccount;
import com.example.android.personalfinance_v01.MyClasses.Budget;
import com.example.android.personalfinance_v01.MyClasses.ExpenseIncome;
import com.example.android.personalfinance_v01.MyClasses.ExpenseIncomeFilter;
import com.example.android.personalfinance_v01.MyClasses.MyUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;

public class HistoryTabbedActivity extends AppCompatActivity {

    private static final String TAG = "HistoryTabbedActivity";
    private static final int FRAGMENT_EXPENSE = 0;
    private static final int FRAGMENT_INCOME = 1;
    private static BalanceAccount ALL_ACCOUNTS_OPTION = new BalanceAccount("All accounts", 0.0, "");
    private LinearLayout linearLayout;
    private ViewPager viewPager;
    private HistoryExpenseFragment expenseHistoryFragment;
    private HistoryIncomeFragment incomeHistoryFragment;
    private ExpenseIncomeFilter expenseIncomeFilter = new ExpenseIncomeFilter(ALL_ACCOUNTS_OPTION, 0);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_tabbed);

        linearLayout = findViewById(R.id.historyLinLay);

        expenseHistoryFragment = new HistoryExpenseFragment();
        incomeHistoryFragment = new HistoryIncomeFragment();

        MyUtils.getExpenseIncomeFromDatabase(HistoryTabbedActivity.this);

        initToolbar();
        initSpinner();

        viewPager = findViewById(R.id.historyViewPager);
        initViewPager(viewPager);

        TabLayout tabLayout = findViewById(R.id.historyTabLayout);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_calendar_filter, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.actionCalendar:
                View dateDialogView = getLayoutInflater().inflate(R.layout.dialog_choose_date, linearLayout, false);
                AlertDialog dateAlertDialog = initDateAlertDialog(dateDialogView);
                initDateAlertDialogButtons(dateDialogView, dateAlertDialog);
                dateAlertDialog.show();
                break;
            case R.id.actionFilter:
                if (viewPager.getCurrentItem() == 0) {
                    View filterCategDialogView = getLayoutInflater().inflate(R.layout.dialog_filter_exp_categories, linearLayout, false);
                    AlertDialog filterCategAlertDialog = initFilterCategAlertDialog(filterCategDialogView);
                    initFilterExpCategAlertDialogButtons(filterCategDialogView, filterCategAlertDialog, expenseIncomeFilter);
                    filterCategAlertDialog.show();
                } else if (viewPager.getCurrentItem() == 1) {
                    View filterCategDialogView = getLayoutInflater().inflate(R.layout.dialog_filter_inc_categories, linearLayout, false);
                    AlertDialog filterCategAlertDialog = initFilterCategAlertDialog(filterCategDialogView);
                    initFilterIncCategAlertDialogButtons(filterCategDialogView, filterCategAlertDialog, expenseIncomeFilter);
                    filterCategAlertDialog.show();
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private AlertDialog initDateAlertDialog(View dialogView) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(HistoryTabbedActivity.this);
        alertDialogBuilder.setView(dialogView);

        return alertDialogBuilder.create();
    }

    private void initDateAlertDialogButtons(View dialogView, final AlertDialog alertDialog) {
        RadioGroup radioGroup = dialogView.findViewById(R.id.dialogDateRadioGroup);
        radioGroup.check(radioGroup.getChildAt(expenseIncomeFilter.getRadioButtonIndex()).getId());

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
                        expenseIncomeFilter.setRadioButtonIndex(0);
                        expenseIncomeFilter.setStartDate(0L);
                        expenseIncomeFilter.setEndDate(currentTime);
                        break;
                    case 1:
                        expenseIncomeFilter.setRadioButtonIndex(1);
                        expenseIncomeFilter.setStartDate(MyUtils.subtractDaysFromCurrentDateTime(1));
                        expenseIncomeFilter.setEndDate(currentTime);
                        break;
                    case 2:
                        expenseIncomeFilter.setRadioButtonIndex(2);
                        expenseIncomeFilter.setStartDate(MyUtils.subtractDaysFromCurrentDateTime(7));
                        expenseIncomeFilter.setEndDate(currentTime);
                        break;
                    case 3:
                        expenseIncomeFilter.setRadioButtonIndex(3);
                        expenseIncomeFilter.setStartDate(MyUtils.subtractDaysFromCurrentDateTime(30));
                        expenseIncomeFilter.setEndDate(currentTime);
                        break;
                    case 4:
                        expenseIncomeFilter.setRadioButtonIndex(4);
                        expenseIncomeFilter.setStartDate(MyUtils.subtractDaysFromCurrentDateTime(365));
                        expenseIncomeFilter.setEndDate(currentTime);
                        break;
                    case 5:
                        expenseIncomeFilter.setRadioButtonIndex(5);
                        break;
                }

                updateLists();
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

                        expenseIncomeFilter.setStartDate(calendar.getTimeInMillis());

                        initEndDatePickerDialog(year, month, day);
                    }
                };

                DatePickerDialog datePickerDialog = new DatePickerDialog(HistoryTabbedActivity.this, android.R.style.Theme_Holo_Dialog_MinWidth,
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
                expenseIncomeFilter.setEndDate(calendar.getTimeInMillis() + oneDay);

                if (!expenseIncomeFilter.isBadCustomDate()) {
                    expenseIncomeFilter.setEndDate(calendar.getTimeInMillis() + oneDay);
                } else {
                    MyUtils.makeToast(HistoryTabbedActivity.this, "Start date can not be after end date");
                }

                updateLists();
            }
        };

        DatePickerDialog datePickerDialog = new DatePickerDialog(HistoryTabbedActivity.this, android.R.style.Theme_Holo_Dialog_MinWidth,
                dateSetListener, year, month, day);
        datePickerDialog.setTitle("End Date");
        datePickerDialog.show();
    }

    private AlertDialog initFilterCategAlertDialog(View dialogView) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(HistoryTabbedActivity.this);
        alertDialogBuilder.setView(dialogView);

        return alertDialogBuilder.create();
    }

    private void initFilterExpCategAlertDialogButtons(View dialogView, final AlertDialog alertDialog, final ExpenseIncomeFilter chartsFilter) {
        final ArrayList<CheckBox> cbList = new ArrayList<>();
        CheckBox cb1 = dialogView.findViewById(R.id.dialogFilterExpCheckBox1);
        cbList.add(cb1);
        CheckBox cb2 = dialogView.findViewById(R.id.dialogFilterExpCheckBox2);
        cbList.add(cb2);
        CheckBox cb3 = dialogView.findViewById(R.id.dialogFilterExpCheckBox3);
        cbList.add(cb3);
        CheckBox cb4 = dialogView.findViewById(R.id.dialogFilterExpCheckBox4);
        cbList.add(cb4);
        CheckBox cb5 = dialogView.findViewById(R.id.dialogFilterExpCheckBox5);
        cbList.add(cb5);
        CheckBox cb6 = dialogView.findViewById(R.id.dialogFilterExpCheckBox6);
        cbList.add(cb6);
        CheckBox cb7 = dialogView.findViewById(R.id.dialogFilterExpCheckBox7);
        cbList.add(cb7);
        CheckBox cb8 = dialogView.findViewById(R.id.dialogFilterExpCheckBox8);
        cbList.add(cb8);
        CheckBox cb9 = dialogView.findViewById(R.id.dialogFilterExpCheckBox9);
        cbList.add(cb9);

        int i = 0;
        for (Map.Entry<String, Boolean> e : chartsFilter.getExpCategoryMap().entrySet()) {
            cbList.get(i).setText(e.getKey());
            cbList.get(i).setChecked(e.getValue());
            i++;
        }

        Button saveBtn = dialogView.findViewById(R.id.dialogFilterExpSaveBtn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (CheckBox item : cbList) {
                    chartsFilter.getExpCategoryMap().put(item.getText().toString(), item.isChecked());
                    updateExpenseList();
                    alertDialog.dismiss();
                }
            }
        });

        Button cancelBtn = dialogView.findViewById(R.id.dialogFilterExpCancelBtn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        boolean allCategsChecked = true;
        for (Map.Entry<String, Boolean> e : chartsFilter.getIncCategoryMap().entrySet()) {
            if(!e.getValue())
                allCategsChecked = false;
        }

        final CheckBox checkBoxAll = dialogView.findViewById(R.id.dialogFilterExpCheckBoxAll);
        checkBoxAll.setChecked(allCategsChecked);
        checkBoxAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    for (CheckBox item : cbList) {
                        item.setChecked(true);
                    }
                } else {
                    for (CheckBox item : cbList) {
                        item.setChecked(false);
                    }
                }
            }
        });
    }

    private void initFilterIncCategAlertDialogButtons(View dialogView, final AlertDialog alertDialog, final ExpenseIncomeFilter chartsFilter) {
        final ArrayList<CheckBox> cbList = new ArrayList<>();
        CheckBox cb1 = dialogView.findViewById(R.id.dialogFilterIncCheckBox1);
        cbList.add(cb1);
        CheckBox cb2 = dialogView.findViewById(R.id.dialogFilterIncCheckBox2);
        cbList.add(cb2);
        CheckBox cb3 = dialogView.findViewById(R.id.dialogFilterIncCheckBox3);
        cbList.add(cb3);
        CheckBox cb4 = dialogView.findViewById(R.id.dialogFilterIncCheckBox4);
        cbList.add(cb4);
        CheckBox cb5 = dialogView.findViewById(R.id.dialogFilterIncCheckBox5);
        cbList.add(cb5);

        int i = 0;
        for (Map.Entry<String, Boolean> e : chartsFilter.getIncCategoryMap().entrySet()) {
            cbList.get(i).setText(e.getKey());
            cbList.get(i).setChecked(e.getValue());
            i++;
        }

        Button saveBtn = dialogView.findViewById(R.id.dialogFilterIncSaveBtn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (CheckBox item : cbList) {
                    chartsFilter.getIncCategoryMap().put(item.getText().toString(), item.isChecked());
                }
                updateIncomeList();
                alertDialog.dismiss();
            }
        });

        Button cancelBtn = dialogView.findViewById(R.id.dialogFilterIncCancelBtn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        boolean allCategsChecked = true;
        for (Map.Entry<String, Boolean> e : chartsFilter.getIncCategoryMap().entrySet()) {
            if(!e.getValue())
                allCategsChecked = false;
        }

        final CheckBox checkBoxAll = dialogView.findViewById(R.id.dialogFilterIncCheckBoxAll);
        checkBoxAll.setChecked(allCategsChecked);
        checkBoxAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    for (CheckBox item : cbList) {
                        item.setChecked(true);
                    }
                } else {
                    for (CheckBox item : cbList) {
                        item.setChecked(false);
                    }
                }
            }
        });
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.historyToolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initSpinner() {
        Spinner spinner = findViewById(R.id.historySpinner);

        BalanceAccountSpinnerAdapter balanceAccountSpinnerAdapter = new BalanceAccountSpinnerAdapter(this, MyUtils.accountList);
        balanceAccountSpinnerAdapter.add(ALL_ACCOUNTS_OPTION);
        spinner.setAdapter(balanceAccountSpinnerAdapter);
        spinner.setSelection(balanceAccountSpinnerAdapter.getCount() - 1);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                BalanceAccount balanceAccount = (BalanceAccount) adapterView.getItemAtPosition(i);
                expenseIncomeFilter.setBalanceAccount(balanceAccount);
                updateLists();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void initViewPager(ViewPager viewPager) {
        ExpenseIncomePagerAdapter adapter = new ExpenseIncomePagerAdapter(getSupportFragmentManager());
        adapter.addFragment(expenseHistoryFragment, "Expense History");
        adapter.addFragment(incomeHistoryFragment, "Income History");
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (!expenseHistoryFragment.isAdded()) {
                    return;
                }

                if (position == FRAGMENT_EXPENSE) {
                    expenseIncomeFilter.setExpenseIncomeType(ExpenseIncome.TYPE_EXPENSE);
                } else if (position == FRAGMENT_INCOME) {
                    expenseIncomeFilter.setExpenseIncomeType(ExpenseIncome.TYPE_INCOME);
                }

                updateLists();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private ArrayList<ExpenseIncome> filterExpenseIncomeList(ArrayList<ExpenseIncome> list, ExpenseIncomeFilter filter) {
        ArrayList<ExpenseIncome> filteredList = new ArrayList<>();
        for (ExpenseIncome item : list) {
            if (filter.isGoodExpInc(item)) {
                filteredList.add(item);
            }
        }

        return filteredList;
    }

    public void updateLists() {
        if (viewPager.getCurrentItem() == FRAGMENT_EXPENSE) {
            updateExpenseList();
        } else if (viewPager.getCurrentItem() == FRAGMENT_INCOME) {
            updateIncomeList();
        }
    }

    public void updateExpenseList() {
        expenseHistoryFragment.updateListView(filterExpenseIncomeList(MyUtils.expenseIncomeList, expenseIncomeFilter));
    }

    public void updateIncomeList() {
        incomeHistoryFragment.updateListView(filterExpenseIncomeList(MyUtils.expenseIncomeList, expenseIncomeFilter));
    }

    public void deleteExpenseIncome(ExpenseIncome expenseIncome) {
        DatabaseHelper databaseHelper = new DatabaseHelper(HistoryTabbedActivity.this);

        databaseHelper.deleteExpenseIncome(databaseHelper.getExpenseIncomeID(expenseIncome));
        updateAccount(expenseIncome, databaseHelper);
        updateBudgets(expenseIncome, databaseHelper);

        MyUtils.getExpenseIncomeFromDatabase(HistoryTabbedActivity.this);

        updateLists();
    }

    private void updateAccount(ExpenseIncome expenseIncome, DatabaseHelper databaseHelper) {
        double newBalance = expenseIncome.getAccount().getBalance();
        if (expenseIncome.getType() == ExpenseIncome.TYPE_INCOME) {
            newBalance -= expenseIncome.getAmount();
        } else {
            newBalance += expenseIncome.getAmount();
        }

        databaseHelper.updateAccountBalanceAmount(databaseHelper.getAccountID(expenseIncome.getAccount()), newBalance);
        expenseIncome.getAccount().setBalance(newBalance);
    }

    private void updateBudgets(ExpenseIncome expenseIncome, DatabaseHelper databaseHelper) {

        Date date = new Date(expenseIncome.getDate());
        ArrayList<Budget> budgets = new ArrayList<>();

        for (Budget budget : MyUtils.budgetList) {
            if (expenseIncome.getCategory().equals(budget.getCategory())) {
                if (budget.isDateInArea(date)) {
                    budgets.add(budget);
                }
            }
        }

        for (Budget budget : budgets) {
            double newBalance = budget.getCurrentAmount();
            newBalance -= expenseIncome.getAmount();
            databaseHelper.updateBudgetCurrentAmount(databaseHelper.getBudgetId(budget), newBalance);
        }
    }
}
