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
import com.example.android.personalfinance_v01.Fragments.DetailedGoalChartFragment;
import com.example.android.personalfinance_v01.Fragments.DetailedGoalHistoryFragment;
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

public class DetailedGoalTabbedActivity extends AppCompatActivity {

    ArrayList<HistoryItem> theList = new ArrayList<>();
    boolean wasDeleted = false;
    Goal currentGoal;
    private int CHART_SIZE;
    private DetailedGoalChartFragment chartFragment;
    private DetailedGoalHistoryFragment historyFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_goal_tabbed);

        currentGoal = (Goal) Objects.requireNonNull(getIntent().getExtras()).getSerializable("goal");
        if (currentGoal == null) {
            return;
        }

        setTitle(currentGoal.getName());

        if (currentGoal.getAddedAmounts().size() >= 5) {
            CHART_SIZE = 5;
        } else {
            CHART_SIZE = currentGoal.getAddedAmounts().size();
        }

        chartFragment = new DetailedGoalChartFragment();
        historyFragment = new DetailedGoalHistoryFragment();

        initToolbar();

        initTheList();

        ViewPager viewPager = findViewById(R.id.detailedGoalHistoryViewPager);
        initViewPager(viewPager);

        TabLayout tabLayout = findViewById(R.id.detailedGoalHistoryTabLayout);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.detailedGoalHistoryToolbar);
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
        bundle.putSerializable("goal", currentGoal);

        Intent intent = new Intent(DetailedGoalTabbedActivity.this, DetailedGoalTabbedActivity.class);
        intent.putExtras(bundle);
        this.startActivity(intent);
        this.overridePendingTransition(0, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("SetTextI18n")
    public void initFields() {
        if (currentGoal != null) {
            chartFragment.nameTv.setText(currentGoal.getName());

            chartFragment.statusTv.setText(getStatusString());

            ProgressBar progressBar = chartFragment.progressBar;
            int percentage = (int) (currentGoal.getSavedAmount() * 100 / currentGoal.getGoalAmount());
            progressBar.setMax(100);
            progressBar.setProgress(percentage);
            int[][] states = new int[][]{
                    new int[]{},
            };
            int[] colors;
            ColorStateList colorStateList;

            TextView goalAmountTv = chartFragment.goalAmountTv;
            goalAmountTv.setText(formatDecimalTwoPlaces(currentGoal.getGoalAmount()));

            TextView savedAmountTv = chartFragment.savedAmountTv;
            savedAmountTv.setText(formatDecimalTwoPlaces(currentGoal.getSavedAmount()));

            TextView remainingAmountTv = chartFragment.remainingAmountTv;
            remainingAmountTv.setText(formatDecimalTwoPlaces(currentGoal.getGoalAmount() - currentGoal.getSavedAmount()));

            if (percentage <= 20) {
                int color = ContextCompat.getColor(this, R.color.debtGoalVeryLow);
                savedAmountTv.setTextColor(color);
                remainingAmountTv.setTextColor(color);
                colors = new int[]{color};
                colorStateList = new ColorStateList(states, colors);
            } else if (percentage <= 40) {
                int color = ContextCompat.getColor(this, R.color.debtGoalLow);
                savedAmountTv.setTextColor(color);
                remainingAmountTv.setTextColor(color);
                colors = new int[]{color};
                colorStateList = new ColorStateList(states, colors);
            } else if (percentage <= 60) {
                int color = ContextCompat.getColor(this, R.color.debtGoalMedium);
                savedAmountTv.setTextColor(color);
                remainingAmountTv.setTextColor(color);
                colors = new int[]{color};
                colorStateList = new ColorStateList(states, colors);
            } else if (percentage <= 80) {
                int color = ContextCompat.getColor(this, R.color.debtGoalHigh);
                savedAmountTv.setTextColor(color);
                remainingAmountTv.setTextColor(color);
                colors = new int[]{color};
                colorStateList = new ColorStateList(states, colors);
            } else {
                int color = ContextCompat.getColor(this, R.color.debtGoalVeryHigh);
                savedAmountTv.setTextColor(color);
                remainingAmountTv.setTextColor(color);
                colors = new int[]{color};
                colorStateList = new ColorStateList(states, colors);
            }

            progressBar.setProgressTintList(colorStateList);

            chartFragment.targetDateTv.setText("Target date: " + formatDateWithoutTime(currentGoal.getTargetDate()));

            chartFragment.recommendedSpentTv.setText(getString(R.string.recommendedSpent) + ": " + formatDecimalTwoPlaces(getRecommendedSpent()));

        }
    }

    public void initChart() {
        chartFragment.emptyTextView.setVisibility(View.GONE);

        ArrayList<Double> amounts = currentGoal.getAddedAmounts();

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
        ArrayList<Double> amountsList = currentGoal.getAddedAmounts();
        ArrayList<Long> datesList = currentGoal.getAddedAmountsDates();

        for (int i = 0; i < amountsList.size(); i++) {
            theList.add(new HistoryItem(amountsList.get(i), datesList.get(i)));
        }
    }

    private String[] getPeriodNames() {
        ArrayList<Long> times = currentGoal.getAddedAmountsDates();
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

    private double getRecommendedSpent() {
        //TODO CALCULATE THIS
        return 0;
    }

    private String getStatusString() {
        if (currentGoal.getStatus() == Goal.REACHED) {
            return "Reached";
        } else if (currentGoal.getStatus() == Goal.NOT_REACHED) {
            return "Ongoing";
        }

        return "";
    }

    public void updateList() {
        historyFragment.updateListView(theList);
    }

    public void deleteHistoryItem(AdapterView<?> adapterView, int i) {
        HistoryItem historyItem = (HistoryItem) adapterView.getItemAtPosition(i);
        theList.remove(historyItem);

        double removedAmount = currentGoal.getAddedAmounts().get(i);
        double newAmount = currentGoal.getSavedAmount() - removedAmount;
        currentGoal.getAddedAmounts().remove(i);
        currentGoal.getAddedAmountsDates().remove(i);
        currentGoal.setSavedAmount(newAmount);
        modifyGoalAmountsTimes(DetailedGoalTabbedActivity.this, currentGoal, currentGoal.getAddedAmounts(), currentGoal.getAddedAmountsDates(), newAmount);


        historyFragment.updateListView(theList);
        wasDeleted = true;
    }

    private void modifyGoalAmountsTimes(Context context, Goal goalModified, ArrayList<Double> amounts, ArrayList<Long> times, double newAmount) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        String amountsString = MyUtils.fromDoubleListToString(amounts);
        String timesString = MyUtils.fromLongListToString(times);

        databaseHelper.updateGoalAmountsList(databaseHelper.getGoalId(goalModified), amountsString, timesString);
        databaseHelper.updateGoalSavedAmount(databaseHelper.getGoalId(goalModified), newAmount);
    }
}
