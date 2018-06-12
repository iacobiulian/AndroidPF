package com.example.android.personalfinance_v01;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.personalfinance_v01.CustomAdapters.ExpenseIncomePagerAdapter;
import com.example.android.personalfinance_v01.CustomAdapters.MyXAxisValueFormatter;
import com.example.android.personalfinance_v01.DataPersistance.DatabaseHelper;
import com.example.android.personalfinance_v01.Fragments.DetailedDebtChartFragment;
import com.example.android.personalfinance_v01.Fragments.DetailedDebtHistoryFragment;
import com.example.android.personalfinance_v01.MyClasses.Debt;
import com.example.android.personalfinance_v01.MyClasses.Goal;
import com.example.android.personalfinance_v01.MyClasses.HistoryItem;
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
import java.util.Objects;

import static com.example.android.personalfinance_v01.MyClasses.MyUtils.formatDateWithoutTime;
import static com.example.android.personalfinance_v01.MyClasses.MyUtils.formatDecimalTwoPlaces;

public class DetailedDebtTabbedActivity extends AppCompatActivity {

    private static final int FRAGMENT_CHART = 0;
    private static final int FRAGMENT_HISTORY = 1;
    ArrayList<HistoryItem> theList = new ArrayList<>();
    boolean wasDeleted = false;
    Debt currentDebt;
    private int CHART_SIZE;
    private ViewPager viewPager;
    private DetailedDebtChartFragment chartFragment;
    private DetailedDebtHistoryFragment historyFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_debt_tabbed);

        currentDebt = (Debt) Objects.requireNonNull(getIntent().getExtras()).getSerializable("debt");
        if (currentDebt == null) {
            return;
        }

        if (currentDebt.getAddedAmounts().size() >= 5) {
            CHART_SIZE = 5;
        } else {
            CHART_SIZE = currentDebt.getAddedAmounts().size();
        }

        chartFragment = new DetailedDebtChartFragment();
        historyFragment = new DetailedDebtHistoryFragment();

        if (currentDebt.getType() == Debt.I_LEND) {
            setTitle("Me to " + currentDebt.getPayee());
        } else {
            setTitle(currentDebt.getPayee() + " to Me");
        }

        initToolbar();

        initTheList();

        viewPager = findViewById(R.id.detailedDebtHistoryViewPager);
        initViewPager(viewPager);

        TabLayout tabLayout = findViewById(R.id.detailedDebtHistoryTabLayout);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.detailedDebtHistoryToolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initViewPager(ViewPager viewPager) {
        ExpenseIncomePagerAdapter adapter = new ExpenseIncomePagerAdapter(getSupportFragmentManager());
        adapter.addFragment(chartFragment, "Details");
        adapter.addFragment(historyFragment, "History");
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
        bundle.putSerializable("debt", currentDebt);

        Intent intent = new Intent(DetailedDebtTabbedActivity.this, DetailedDebtTabbedActivity.class);
        intent.putExtras(bundle);
        this.startActivity(intent);
        this.overridePendingTransition(0, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("SetTextI18n")
    public void initFields() {
        if (currentDebt != null) {
            if (currentDebt.getType() == Debt.I_LEND) {
                chartFragment.fromTv.setText(R.string.me);
                chartFragment.toTv.setText(currentDebt.getPayee());
            } else {
                chartFragment.fromTv.setText(currentDebt.getPayee());
                chartFragment.toTv.setText(R.string.me);
            }

            chartFragment.initialAmountTv.setText(formatDecimalTwoPlaces(currentDebt.getAmount()));

            TextView paidBackAmountTv = chartFragment.paidBackAmountTv;
            paidBackAmountTv.setText(formatDecimalTwoPlaces(currentDebt.getAmountPaidBack()));

            TextView remainingAmountTv = chartFragment.remainingAmountTv;
            remainingAmountTv.setText(formatDecimalTwoPlaces(currentDebt.getAmount() - currentDebt.getAmountPaidBack()));

            ProgressBar progressBar = chartFragment.progressBar;
            int percentage = (int) (currentDebt.getAmountPaidBack() * 100 / currentDebt.getAmount());
            progressBar.setMax(100);
            progressBar.setProgress(percentage);
            int[][] states = new int[][]{
                    new int[]{},
            };
            int[] colors;
            ColorStateList colorStateList;

            if (percentage <= 20) {
                int color = ContextCompat.getColor(this, R.color.debtGoalVeryLow);
                paidBackAmountTv.setTextColor(color);
                remainingAmountTv.setTextColor(color);
                colors = new int[]{color};
                colorStateList = new ColorStateList(states, colors);
            } else if (percentage <= 40) {
                int color = ContextCompat.getColor(this, R.color.debtGoalLow);
                paidBackAmountTv.setTextColor(color);
                remainingAmountTv.setTextColor(color);
                colors = new int[]{color};
                colorStateList = new ColorStateList(states, colors);
            } else if (percentage <= 60) {
                int color = ContextCompat.getColor(this, R.color.debtGoalMedium);
                paidBackAmountTv.setTextColor(color);
                remainingAmountTv.setTextColor(color);
                colors = new int[]{color};
                colorStateList = new ColorStateList(states, colors);
            } else if (percentage <= 80) {
                int color = ContextCompat.getColor(this, R.color.debtGoalHigh);
                paidBackAmountTv.setTextColor(color);
                remainingAmountTv.setTextColor(color);
                colors = new int[]{color};
                colorStateList = new ColorStateList(states, colors);
            } else {
                int color = ContextCompat.getColor(this, R.color.debtGoalVeryHigh);
                paidBackAmountTv.setTextColor(color);
                remainingAmountTv.setTextColor(color);
                colors = new int[]{color};
                colorStateList = new ColorStateList(states, colors);
            }

            progressBar.setProgressTintList(colorStateList);

            chartFragment.startDate.setText("Start date: " + formatDateWithoutTime(currentDebt.getCreationDate()));

            chartFragment.dueDate.setText("Due date: " + formatDateWithoutTime(currentDebt.getPaybackDate()));

            if (currentDebt.isClosed() == Debt.CLOSED) {
                chartFragment.closed.setText("Closed");
            } else if (currentDebt.isClosed() == Debt.NOT_CLOSED) {
                chartFragment.closed.setText("Open");
            }

        }
    }

    public void initChart() {
        chartFragment.emptyTextView.setVisibility(View.GONE);

        ArrayList<Double> amounts = currentDebt.getAddedAmounts();

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

        if (!amounts.isEmpty()) {
            ArrayList<BarEntry> entries = getChartEntries(amounts);

            BarDataSet barDataSet = new BarDataSet(entries, "");
            barDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
            barDataSet.setValueTextSize(12f);

            BarData barData = new BarData(barDataSet);
            barData.setBarWidth(0.9f);

            barChart.setData(barData);

            XAxis xAxis = barChart.getXAxis();
            xAxis.setValueFormatter(new MyXAxisValueFormatter(getPeriodNames()));
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setGranularity(1f);
        } else {
            barChart.setVisibility(View.GONE);
            chartFragment.emptyTextView.setVisibility(View.VISIBLE);
        }

    }

    private void initTheList() {
        ArrayList<Double> amountsList = currentDebt.getAddedAmounts();
        ArrayList<Long> datesList = currentDebt.getAddedAmountsDates();

        for (int i = 0; i < amountsList.size(); i++) {
            theList.add(new HistoryItem(amountsList.get(i), datesList.get(i)));
        }
    }

    private String[] getPeriodNames() {
        ArrayList<Long> times = currentDebt.getAddedAmountsDates();
        String[] periodNames = new String[CHART_SIZE];
        int size = times.size();


        for (int i = 0; i < CHART_SIZE; i++) {
            periodNames[i] = MyUtils.formatDateDayMonth(times.get(size - 1 - i));
        }

        return periodNames;
    }

    private ArrayList<BarEntry> getChartEntries(ArrayList<Double> amounts) {
        ArrayList<BarEntry> entries = new ArrayList<>();
        int size = amounts.size();

        for (int i = 0; i < CHART_SIZE; i++) {
            float xValue = (float) i;
            float yValue = MyUtils.returnFloat(amounts.get(size - 1 - i));
            entries.add(new BarEntry(xValue, yValue));
        }

        return entries;
    }


    public void updateList() {
        historyFragment.updateListView(theList);
    }

    public void deleteHistoryItem(AdapterView<?> adapterView, int i) {
        HistoryItem historyItem = (HistoryItem) adapterView.getItemAtPosition(i);
        theList.remove(historyItem);

        double removedAmount = currentDebt.getAddedAmounts().get(i);
        double newAmount = currentDebt.getAmountPaidBack() - removedAmount;
        currentDebt.getAddedAmounts().remove(i);
        currentDebt.getAddedAmountsDates().remove(i);
        currentDebt.setAmountPaidBack(newAmount);
        modifyDebtAmountsTimes(DetailedDebtTabbedActivity.this, currentDebt, currentDebt.getAddedAmounts(), currentDebt.getAddedAmountsDates(), newAmount);


        historyFragment.updateListView(theList);
        wasDeleted = true;
    }

    private void modifyDebtAmountsTimes(Context context, Debt debtModified, ArrayList<Double> amounts, ArrayList<Long> times, double newAmount) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        String amountsString = MyUtils.fromDoubleListToString(amounts);
        String timesString = MyUtils.fromLongListToString(times);

        databaseHelper.updateDebtAmountsList(databaseHelper.getDebtID(debtModified), amountsString, timesString);
        databaseHelper.updateDebtAmount(databaseHelper.getDebtID(debtModified), newAmount);
    }
}
