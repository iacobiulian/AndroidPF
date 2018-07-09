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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.example.android.personalfinance_v01.CustomAdapters.BalanceAccountSpinnerAdapter;
import com.example.android.personalfinance_v01.CustomAdapters.ExpenseIncomePagerAdapter;
import com.example.android.personalfinance_v01.Fragments.StatsExpenseFragment;
import com.example.android.personalfinance_v01.Fragments.StatsIncomeFragment;
import com.example.android.personalfinance_v01.MyClasses.BalanceAccount;
import com.example.android.personalfinance_v01.MyClasses.ExpenseIncome;
import com.example.android.personalfinance_v01.MyClasses.ExpenseIncomeFilter;
import com.example.android.personalfinance_v01.MyClasses.MyUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

public class ChartsActivity extends AppCompatActivity {

    public static BalanceAccount ALL_ACCOUNTS_OPTION = new BalanceAccount("All accounts", 0.0, "");
    private ExpenseIncomeFilter chartsFilter = new ExpenseIncomeFilter(ALL_ACCOUNTS_OPTION, 0);
    private ViewPager viewPager;
    private StatsExpenseFragment statsExpenseFragment;
    private StatsIncomeFragment statsIncomeFragment;
    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charts);

        linearLayout = findViewById(R.id.chartsLinLay);
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
                    initFilterExpCategAlertDialogButtons(filterCategDialogView, filterCategAlertDialog, chartsFilter);
                    filterCategAlertDialog.show();
                } else {
                    View filterCategDialogView = getLayoutInflater().inflate(R.layout.dialog_filter_inc_categories, linearLayout, false);
                    AlertDialog filterCategAlertDialog = initFilterCategAlertDialog(filterCategDialogView);
                    initFilterIncCategAlertDialogButtons(filterCategDialogView, filterCategAlertDialog, chartsFilter);
                    filterCategAlertDialog.show();
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private AlertDialog initDateAlertDialog(View dialogView) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ChartsActivity.this);
        alertDialogBuilder.setView(dialogView);

        return alertDialogBuilder.create();
    }

    private void initDateAlertDialogButtons(View dialogView, final AlertDialog alertDialog) {
        RadioGroup radioGroup = dialogView.findViewById(R.id.dialogDateRadioGroup);
        radioGroup.check(radioGroup.getChildAt(chartsFilter.getRadioButtonIndex()).getId());

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

    private AlertDialog initFilterCategAlertDialog(View dialogView) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ChartsActivity.this);
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
                    updateExpenseChart();
                    alertDialog.dismiss();
                }
            }
        });

        Button selectAllBtn = dialogView.findViewById(R.id.dialogFilterExpSelectBtn);
        selectAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (CheckBox item : cbList) {
                    item.setChecked(true);
                }
            }
        });

        Button unSelectAllBtn = dialogView.findViewById(R.id.dialogFilterExpUnselectBtn);
        unSelectAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (CheckBox item : cbList) {
                    item.setChecked(false);
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
                    updateIncomeChart();
                    alertDialog.dismiss();
                }
            }
        });

        Button selectAllBtn = dialogView.findViewById(R.id.dialogFilterIncSelectBtn);
        selectAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (CheckBox item : cbList) {
                    item.setChecked(true);
                }
            }
        });

        Button unSelectAllBtn = dialogView.findViewById(R.id.dialogFilterIncUnselectBtn);
        unSelectAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (CheckBox item : cbList) {
                    item.setChecked(false);
                }
            }
        });
    }

    private void initSpinner() {
        Spinner spinner = findViewById(R.id.chartsAccountSpinner);

        BalanceAccountSpinnerAdapter balanceAccountSpinnerAdapter = new BalanceAccountSpinnerAdapter(this, MyUtils.accountList);
        balanceAccountSpinnerAdapter.add(ALL_ACCOUNTS_OPTION);
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
        Toolbar toolbar = findViewById(R.id.chartsToolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initViewPager(ViewPager viewPager) {
        statsExpenseFragment = new StatsExpenseFragment();
        statsIncomeFragment = new StatsIncomeFragment();

        ExpenseIncomePagerAdapter adapter = new ExpenseIncomePagerAdapter(getSupportFragmentManager());
        adapter.addFragment(statsExpenseFragment, "Expenses");
        adapter.addFragment(statsIncomeFragment, "Income");
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (!statsExpenseFragment.isAdded()) {
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

    private ArrayList<ExpenseIncome> filterExpenseIncomeList(ArrayList<ExpenseIncome> list, ExpenseIncomeFilter filter) {
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
        statsExpenseFragment.setPieChartValues(fromListToMap(filterExpenseIncomeList(MyUtils.expenseIncomeList, chartsFilter)));
    }

    public void updateIncomeChart() {
        statsIncomeFragment.setPieChartValues(fromListToMap(filterExpenseIncomeList(MyUtils.expenseIncomeList, chartsFilter)));
    }

    public void updateCharts() {
        statsExpenseFragment.setPieChartValues(fromListToMap(filterExpenseIncomeList(MyUtils.expenseIncomeList, chartsFilter)));
        statsIncomeFragment.setPieChartValues(fromListToMap(filterExpenseIncomeList(MyUtils.expenseIncomeList, chartsFilter)));
    }

}
