package com.example.android.personalfinance_v01;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;

import com.example.android.personalfinance_v01.CustomAdapters.ExpenseIncomePagerAdapter;
import com.example.android.personalfinance_v01.CustomAdapters.MyXAxisValueFormatter;
import com.example.android.personalfinance_v01.DataPersistance.DatabaseHelper;
import com.example.android.personalfinance_v01.Fragments.DetailedBudgetChartFragment;
import com.example.android.personalfinance_v01.Fragments.DetailedBudgetHistoryFragment;
import com.example.android.personalfinance_v01.MyClasses.Budget;
import com.example.android.personalfinance_v01.MyClasses.Category;
import com.example.android.personalfinance_v01.MyClasses.ExpenseIncome;
import com.example.android.personalfinance_v01.MyClasses.MyUtils;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import static com.example.android.personalfinance_v01.MyClasses.MyUtils.expenseIncomeList;
import static com.example.android.personalfinance_v01.MyClasses.MyUtils.formatDecimalTwoPlaces;

public class DetailedBudgetTabbedActivity extends AppCompatActivity {

    private static final int FRAGMENT_CHART = 0;
    private static final int FRAGMENT_HISTORY = 1;
    boolean wasDeleted = false;
    Budget currentBudget;
    private double[] period;
    private String[] periodNames;
    private ViewPager viewPager;
    private DetailedBudgetChartFragment chartFragment;
    private DetailedBudgetHistoryFragment historyFragment;
    private ArrayList<ExpenseIncome> theList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_budget_tabbed);

        currentBudget = (Budget) Objects.requireNonNull(getIntent().getExtras()).getSerializable("budget");
        if (currentBudget == null)
            return;

        setTitle(currentBudget.getCategory().getName() + " " + getType() + " Budget");
        MyUtils.getExpenseIncomeFromDatabase(this);

        chartFragment = new DetailedBudgetChartFragment();
        historyFragment = new DetailedBudgetHistoryFragment();

        initToolbar();

        initPeriod();

        viewPager = findViewById(R.id.detailedBudgetHistoryViewPager);
        initViewPager(viewPager);

        TabLayout tabLayout = findViewById(R.id.detailedBudgetHistoryTabLayout);
        tabLayout.setupWithViewPager(viewPager);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("SetTextI18n")
    public void initViews() {
        if (currentBudget != null) {
            chartFragment.categImg.setImageResource(currentBudget.getCategory().getIconID());

            chartFragment.nameTv.setText(currentBudget.getCategory().getName());

            chartFragment.statusTv.setText(getType());

            int percentage = (int) (currentBudget.getCurrentAmount() * 100 / currentBudget.getTotalAmount());
            chartFragment.progressBar.setProgress(percentage);
            int[][] states = new int[][]{
                    new int[]{},
            };
            int[] colors;
            ColorStateList colorStateList;

            chartFragment.goalAmountTv.setText(formatDecimalTwoPlaces(currentBudget.getTotalAmount()));

            chartFragment.savedAmountTv.setText(formatDecimalTwoPlaces(currentBudget.getCurrentAmount()));

            chartFragment.remainingAmountTv.setText(formatDecimalTwoPlaces(currentBudget.getTotalAmount() -
                    currentBudget.getCurrentAmount()));

            //chartFragment.averageSpentTv.setText(getString(R.string.averageSpent) + " each day: " + formatDecimalTwoPlaces(getAverageSpent()));

            //chartFragment.recommendedSpentTv.setText(getString(R.string.recommendedSpent) + " each day: " + formatDecimalTwoPlaces(getRecommendedSpent()));

            if (percentage <= 20) {
                int color = ContextCompat.getColor(this, R.color.debtGoalVeryHigh);
                chartFragment.savedAmountTv.setTextColor(color);
                chartFragment.remainingAmountTv.setTextColor(color);
                colors = new int[]{color};
                colorStateList = new ColorStateList(states, colors);
            } else if (percentage <= 40) {
                int color = ContextCompat.getColor(this, R.color.debtGoalHigh);
                chartFragment.savedAmountTv.setTextColor(color);
                chartFragment.remainingAmountTv.setTextColor(color);
                colors = new int[]{color};
                colorStateList = new ColorStateList(states, colors);
            } else if (percentage <= 60) {
                int color = ContextCompat.getColor(this, R.color.debtGoalMedium);
                chartFragment.savedAmountTv.setTextColor(color);
                chartFragment.remainingAmountTv.setTextColor(color);
                colors = new int[]{color};
                colorStateList = new ColorStateList(states, colors);
            } else if (percentage <= 80) {
                int color = ContextCompat.getColor(this, R.color.debtGoalLow);
                chartFragment.savedAmountTv.setTextColor(color);
                chartFragment.remainingAmountTv.setTextColor(color);
                colors = new int[]{color};
                colorStateList = new ColorStateList(states, colors);
            } else {
                int color = ContextCompat.getColor(this, R.color.debtGoalVeryLow);
                chartFragment.savedAmountTv.setTextColor(color);
                chartFragment.remainingAmountTv.setTextColor(color);
                colors = new int[]{color};
                colorStateList = new ColorStateList(states, colors);
            }

            chartFragment.progressBar.setProgressTintList(colorStateList);

            if (currentBudget.getType() == Budget.NONE) {
                chartFragment.averageSpentTv.setVisibility(View.GONE);
                chartFragment.recommendedSpentTv.setVisibility(View.GONE);
            }

        }
    }

    public void initChart() {
        chartFragment.emptyTextView.setVisibility(View.GONE);

        BarChart barChart = chartFragment.barChart;
        barChart.setDrawBarShadow(true);
        barChart.setMaxVisibleValueCount(12);
        barChart.setPinchZoom(false);
        barChart.setDrawBorders(false);
        barChart.setDrawBarShadow(false);
        barChart.setDrawGridBackground(true);

        Description description = new Description();
        description.setText("");
        barChart.setDescription(description);

        Legend legend = barChart.getLegend();
        legend.setEnabled(false);

        if (isPeriodPopulated()) {
            ArrayList<BarEntry> entries = new ArrayList<BarEntry>();
            for (int i = 0; i < period.length; i++) {
                float xValue = (float) i;
                float yValue = (float) period[i];
                entries.add(new BarEntry(xValue, yValue));
            }

            BarDataSet barDataSet = new BarDataSet(entries, "");
            barDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
            barDataSet.setValueTextSize(12f);

            BarData barData = new BarData(barDataSet);
            barData.setBarWidth(0.9f);

            barChart.setData(barData);

            XAxis xAxis = barChart.getXAxis();
            xAxis.setValueFormatter(new MyXAxisValueFormatter(periodNames));
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setGranularity(1f);
        } else {
            barChart.setVisibility(View.GONE);
            chartFragment.emptyTextView.setVisibility(View.VISIBLE);
        }

    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.detailedBudgetHistoryToolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initViewPager(ViewPager viewPager) {
        ExpenseIncomePagerAdapter adapter = new ExpenseIncomePagerAdapter(getSupportFragmentManager());
        adapter.addFragment(chartFragment, "Details");
        adapter.addFragment(historyFragment, "Expense History");
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (wasDeleted) {
                    restartThis();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private void restartThis() {
        Bundle bundle = new Bundle();
        bundle.putSerializable("budget", currentBudget);

        Intent intent = new Intent(DetailedBudgetTabbedActivity.this, DetailedBudgetTabbedActivity.class);
        intent.putExtras(bundle);
        this.startActivity(intent);
        this.overridePendingTransition(0, 0);
    }

    private String getType() {
        switch (currentBudget.getType()) {
            case Budget.NONE:
                return "None";
            case Budget.WEEKLY:
                return "Weekly";
            case Budget.MONTHLY:
                return "Monthly";
            case Budget.YEARLY:
                return "Yearly";
            default:
                return "None";
        }
    }

    private double getAverageSpent() {
        double sum = 0.0;
        for (double item : period)
            sum += item;

        return sum / period.length;
    }

    private double getRecommendedSpent() {
        return currentBudget.getTotalAmount() / currentBudget.getType();
    }

    private void initPeriod() {
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.set(Calendar.HOUR_OF_DAY, 0);

        switch (currentBudget.getType()) {
            case Budget.NONE:
            case Budget.WEEKLY:
                periodNames = new String[]{"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                initSpecificPeriod(calendar.getTime(), 7, Calendar.DATE);
                break;
            case Budget.MONTHLY:
                //get the value spent for each week of the month
                periodNames = new String[]{"Week 1", "Week 2", "Week 3", "Week 4"};
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                initSpecificPeriod(calendar.getTime(), 4, Calendar.WEEK_OF_MONTH);
                break;
            case Budget.YEARLY:
                //get the value spent for each month of the year
                periodNames = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Dec"};
                calendar.set(Calendar.DAY_OF_YEAR, 1);
                initSpecificPeriod(calendar.getTime(), 12, Calendar.MONTH);
                break;
            default:
                break;
        }

    }

    private void initSpecificPeriod(Date startDate, int periodSize, int calendarType) {
        period = new double[periodSize];

        //filter the list based on budget category
        Calendar calendarLower = Calendar.getInstance();
        Calendar calendarUpper = Calendar.getInstance();
        calendarLower.setTime(startDate);
        calendarUpper.setTime(startDate);
        Date lowerBound;
        Date upperBound;

        ArrayList<ExpenseIncome> filteredCategList = filterCategoryExpenseIncomeList(expenseIncomeList, currentBudget.getCategory());
        for (int i = 0; i < periodSize; i++) {
            calendarLower.setTime(startDate);
            calendarUpper.setTime(startDate);
            calendarLower.add(calendarType, i);
            calendarUpper.add(calendarType, i + 1);
            lowerBound = calendarLower.getTime();
            upperBound = calendarUpper.getTime();
            ArrayList<ExpenseIncome> filteredTimeList =
                    new ArrayList<>(filterTimeExpenseIncomeList(filteredCategList, lowerBound, upperBound));
            theList.addAll(filteredTimeList);

            if (!filteredTimeList.isEmpty()) {
                for (ExpenseIncome expinc : filteredTimeList) {
                    period[i] += expinc.getAmount();
                }
            }
        }
    }

    private ArrayList<ExpenseIncome> filterCategoryExpenseIncomeList(ArrayList<ExpenseIncome> list, Category category) {
        ArrayList<ExpenseIncome> filteredList = new ArrayList<>();
        for (ExpenseIncome item : list) {
            if (item.getCategory().equals(category)) {
                filteredList.add(item);
            }
        }
        return filteredList;
    }

    private ArrayList<ExpenseIncome> filterTimeExpenseIncomeList(ArrayList<ExpenseIncome> list, Date lowerBound, Date upperBound) {
        ArrayList<ExpenseIncome> filteredList = new ArrayList<>();
        for (ExpenseIncome item : list) {
            Date date = new Date(item.getDate());
            boolean cond = date.after(lowerBound) && date.before(upperBound);
            if (cond) {
                filteredList.add(item);
            }
        }
        return filteredList;
    }

    private boolean isPeriodPopulated() {
        for (double item : period) {
            if (item > 0.0) {
                return true;
            }
        }

        return false;
    }

    public void updateExpenseList() {
        historyFragment.updateListView(theList);
    }

    public void deleteExpenseIncome(AdapterView<?> adapterView, int i) {
        ExpenseIncome expenseIncome = (ExpenseIncome) adapterView.getItemAtPosition(i);
        DatabaseHelper databaseHelper = new DatabaseHelper(this);

        databaseHelper.deleteExpenseIncome(databaseHelper.getExpenseIncomeID(expenseIncome));
        updateAccount(expenseIncome, databaseHelper);
        updateBudgets(expenseIncome, databaseHelper);

        int index = theList.indexOf(expenseIncome);
        theList.remove(index);

        MyUtils.getExpenseIncomeFromDatabase(this);
        MyUtils.getBudgetsFromDatabase(this);
        updateExpenseList();
        wasDeleted = true;
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

        double newBalance = currentBudget.getCurrentAmount();
        newBalance -= expenseIncome.getAmount();
        currentBudget.setCurrentAmount(newBalance);
    }
}
