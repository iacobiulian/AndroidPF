package com.example.android.personalfinance_v01;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.example.android.personalfinance_v01.CustomAdapters.BalanceAccountSpinnerAdapter;
import com.example.android.personalfinance_v01.CustomAdapters.StatsPagerAdapter;
import com.example.android.personalfinance_v01.Fragments.ExpenseStatsFragment;
import com.example.android.personalfinance_v01.Fragments.IncomeStatsFragment;
import com.example.android.personalfinance_v01.MyClasses.BalanceAccount;
import com.example.android.personalfinance_v01.MyClasses.ExpenseIncome;
import com.example.android.personalfinance_v01.MyClasses.MyUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

public class ChartsActivity extends AppCompatActivity {

    ExpenseStatsFragment expenseStatsFragment;
    IncomeStatsFragment incomeStatsFragment;

    ViewPager viewPager;

    Toolbar toolbar;

    Spinner spinner;

    BalanceAccount allAccountsOption = new BalanceAccount("All accounts", 0.0, "");
    public ChartsFilter chartsFilter = new ChartsFilter(allAccountsOption, 0);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charts);

        MyUtils.getExpenseIncomeFromDatabase(ChartsActivity.this);

        viewPager = findViewById(R.id.chartsViewPager);
        initViewPager(viewPager);

        TabLayout tabLayout = findViewById(R.id.chartsTabLayout);
        tabLayout.setupWithViewPager(viewPager);

        initToolbar();

        initSpinner();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_calendar, menu);

        //updateCharts();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.actionCalendar:
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_choose_date, null);
                AlertDialog alertDialog = initAlertDialog(dialogView);
                initAlertDialogButtons(dialogView, alertDialog);
                alertDialog.show();
                break;
            case R.id.actionFilter:
                MyUtils.makeToast(this, "Filter clicked");
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private AlertDialog initAlertDialog(View dialogView) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ChartsActivity.this);
        alertDialogBuilder.setView(dialogView);

        return alertDialogBuilder.create();
    }

    private void initAlertDialogButtons(View dialogView, final AlertDialog alertDialog) {
        RadioGroup radioGroup = dialogView.findViewById(R.id.dialogDateRadioGroup);
        radioGroup.check(radioGroup.getChildAt(chartsFilter.radioButtonIndex).getId());

        initStartDatePickerDialog((RadioButton) radioGroup.getChildAt(radioGroup.getChildCount() - 1));

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int radioButtonID = radioGroup.getCheckedRadioButtonId();
                RadioButton radioButton = radioGroup.findViewById(radioButtonID);
                int selectedIndex = radioGroup.indexOfChild(radioButton);
                MyUtils.makeToast(getApplicationContext(), "Index: " + selectedIndex);
                long currentTime = MyUtils.getCurrentDateTime();
                switch (selectedIndex) {
                    case 0:
                        chartsFilter.setRadioButtonIndex(0);
                        chartsFilter.setStartDate(0L);
                        chartsFilter.setEndDate(currentTime);
                        break;
                    case 1:
                        chartsFilter.setRadioButtonIndex(1);
                        chartsFilter.setStartDate(MyUtils.subtractDaysFromCurrentDateTime(1));
                        chartsFilter.setEndDate(currentTime);
                        break;
                    case 2:
                        chartsFilter.setRadioButtonIndex(2);
                        chartsFilter.setStartDate(MyUtils.subtractDaysFromCurrentDateTime(7));
                        chartsFilter.setEndDate(currentTime);
                        break;
                    case 3:
                        chartsFilter.setRadioButtonIndex(3);
                        chartsFilter.setStartDate(MyUtils.subtractDaysFromCurrentDateTime(30));
                        chartsFilter.setEndDate(currentTime);
                        break;
                    case 4:
                        chartsFilter.setRadioButtonIndex(4);
                        chartsFilter.setStartDate(MyUtils.subtractDaysFromCurrentDateTime(365));
                        chartsFilter.setEndDate(currentTime);
                        break;
                    case 5:
                        chartsFilter.setRadioButtonIndex(5);
                        break;
                }

                updateCharts();

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

                        chartsFilter.setStartDate(calendar.getTimeInMillis());

                        initEndDatePickerDialog(year, month, day);
                    }
                };

                DatePickerDialog datePickerDialog = new DatePickerDialog(ChartsActivity.this, android.R.style.Theme_Holo_Dialog_MinWidth,
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
                chartsFilter.setEndDate(calendar.getTimeInMillis() + oneDay);

                if (chartsFilter.isBadCustomDate()) {
                    MyUtils.makeToast(ChartsActivity.this, "Start date can not be after end date");
                }

                updateCharts();
            }
        };

        DatePickerDialog datePickerDialog = new DatePickerDialog(ChartsActivity.this, android.R.style.Theme_Holo_Dialog_MinWidth,
                dateSetListener, year, month, day);
        datePickerDialog.setTitle("End Date");
        datePickerDialog.show();
    }

    private void initSpinner() {
        spinner = findViewById(R.id.chartsAccountSpinner);

        BalanceAccountSpinnerAdapter balanceAccountSpinnerAdapter = new BalanceAccountSpinnerAdapter(this, MyUtils.accountList);
        balanceAccountSpinnerAdapter.add(allAccountsOption);
        spinner.setAdapter(balanceAccountSpinnerAdapter);
        spinner.setSelection(balanceAccountSpinnerAdapter.getCount() - 1);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                chartsFilter.setBalanceAccount((BalanceAccount) adapterView.getItemAtPosition(i));
                updateCharts();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void initToolbar() {
        toolbar = findViewById(R.id.chartsToolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initViewPager(ViewPager viewPager) {
        expenseStatsFragment = new ExpenseStatsFragment();
        incomeStatsFragment = new IncomeStatsFragment();

        StatsPagerAdapter adapter = new StatsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(expenseStatsFragment, "Expenses");
        adapter.addFragment(incomeStatsFragment, "Income");
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(!expenseStatsFragment.isAdded()) {
                    return;
                }

                if (position == 0) {
                    chartsFilter.setExpenseIncomeType(ExpenseIncome.TYPE_EXPENSE);
                    updateCharts();
                } else {
                    chartsFilter.setExpenseIncomeType(ExpenseIncome.TYPE_INCOME);
                    updateCharts();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private ArrayList<ExpenseIncome> filterExpenseIncomeList(ArrayList<ExpenseIncome> list, ChartsFilter filter) {
        ArrayList<ExpenseIncome> filteredList = new ArrayList<>();
        for (ExpenseIncome item : list) {
            if (filter.isGoodExpInc(item)) {
                filteredList.add(item);
            }
        }

        return filteredList;
    }

    private HashMap<String, Double> fromListToMap(ArrayList<ExpenseIncome> expenseIncomeList) {
        HashMap<String, Double> hashMap = new HashMap<>();

        for (ExpenseIncome item : expenseIncomeList) {
            String key = item.getCategory().getName();
            double value = item.getAmount();

            if (hashMap.containsKey(key)) {
                value += hashMap.get(key);
            }
            hashMap.put(key, value);
        }

        return hashMap;
    }

    public void updateExpenseChart() {
        expenseStatsFragment.setPieChartValues(fromListToMap(filterExpenseIncomeList(MyUtils.expenseIncomeList, chartsFilter)));
    }

    public void updateIncomeChart() {
        incomeStatsFragment.setPieChartValues(fromListToMap(filterExpenseIncomeList(MyUtils.expenseIncomeList, chartsFilter)));
    }

    public void updateCharts() {
        expenseStatsFragment.setPieChartValues(fromListToMap(filterExpenseIncomeList(MyUtils.expenseIncomeList, chartsFilter)));
        incomeStatsFragment.setPieChartValues(fromListToMap(filterExpenseIncomeList(MyUtils.expenseIncomeList, chartsFilter)));
    }

    private class ChartsFilter {
        BalanceAccount balanceAccount; //spinner account (acc1, acc2, all acc etc..)
        int radioButtonIndex; //time radio button (all, day, week, month, year, custom)
        long startDate; //if  custom date date start
        long endDate; //if custom date date end
        int expenseIncomeType;

        ChartsFilter(BalanceAccount balanceAccount, int radioButtonIndex) {
            this.balanceAccount = balanceAccount;
            this.radioButtonIndex = radioButtonIndex;
            this.startDate = 0;
            this.endDate = MyUtils.getCurrentDateTime();
            expenseIncomeType = ExpenseIncome.TYPE_EXPENSE;
        }

        public int getExpenseIncomeType() {
            return expenseIncomeType;
        }

        public void setExpenseIncomeType(int expenseIncomeType) {
            this.expenseIncomeType = expenseIncomeType;
        }

        public BalanceAccount getBalanceAccount() {
            return balanceAccount;
        }

        void setBalanceAccount(BalanceAccount balanceAccount) {
            this.balanceAccount = balanceAccount;
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

        boolean isGoodExpInc(ExpenseIncome item) {
            if (item.getType() != this.expenseIncomeType) {
                return false;
            }

            if (item.getDate() < this.startDate || item.getDate() > this.endDate) {
                return false;
            }

            if (this.balanceAccount.equals(allAccountsOption)) {
                return true;
            }

            if (!(item.getAccount().equals(this.balanceAccount))) {
                return false;
            }

            return true;
        }
    }
}
