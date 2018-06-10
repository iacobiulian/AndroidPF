package com.example.android.personalfinance_v01;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.personalfinance_v01.MyClasses.Goal;
import com.example.android.personalfinance_v01.MyClasses.MyUtils;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Objects;

import static com.example.android.personalfinance_v01.MyClasses.MyUtils.formatDateWithoutTime;
import static com.example.android.personalfinance_v01.MyClasses.MyUtils.formatDecimalTwoPlaces;

public class DetailedGoalActivity extends AppCompatActivity {

    private int CHART_SIZE;
    private Goal currentGoal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_goal);

        currentGoal = (Goal) Objects.requireNonNull(getIntent().getExtras()).getSerializable("goal");
        if (currentGoal == null) {
            return;
        }

        //textView.setText(MyUtils.fromDoubleListToString(currentGoal.getAddedAmounts()) + " " + MyUtils.fromLongListToString(currentGoal.getAddedAmountsDates()));

        if (currentGoal.getAddedAmounts().size() >= 5) {
            CHART_SIZE = 5;
        } else {
            CHART_SIZE = currentGoal.getAddedAmounts().size();
        }

        initFields();

        initChart();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("SetTextI18n")
    private void initFields() {
        if (currentGoal != null) {
            TextView nameTv = findViewById(R.id.detailedGoalNameTv);
            nameTv.setText(currentGoal.getName());

            TextView statusTv = findViewById(R.id.detailedGoalStatus);
            statusTv.setText(getStatusString());

            ProgressBar progressBar = findViewById(R.id.detailedGoalProgressBar);
            int percentage = (int) (currentGoal.getSavedAmount() * 100 / currentGoal.getGoalAmount());
            progressBar.setMax(100);
            progressBar.setProgress(percentage);
            int[][] states = new int[][]{
                    new int[]{},
            };
            int[] colors;
            ColorStateList colorStateList;

            TextView goalAmountTv = findViewById(R.id.detailedGoalAmountTv);
            goalAmountTv.setText(formatDecimalTwoPlaces(currentGoal.getGoalAmount()));

            TextView savedAmountTv = findViewById(R.id.detailedGoalSavedAmountTv);
            savedAmountTv.setText(formatDecimalTwoPlaces(currentGoal.getSavedAmount()));

            TextView remainingAmountTv = findViewById(R.id.detailedGoalRemainingAmountTv);
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

            TextView targetDateTv = findViewById(R.id.detailedGoalTargetDateTv);
            targetDateTv.setText("Target date: " + formatDateWithoutTime(currentGoal.getTargetDate()));

            TextView recommendedSpentTv = findViewById(R.id.detailedGoalRecommended);
            recommendedSpentTv.setText(getString(R.string.recommendedSpent) + ": " + formatDecimalTwoPlaces(getRecommendedSpent()));

        }
    }

    private void initChart() {
        TextView emptyTextView = findViewById(R.id.detailedGoalEmptyTv);
        emptyTextView.setVisibility(View.GONE);

        ArrayList<Double> amounts = currentGoal.getAddedAmounts();

        BarChart barChart = findViewById(R.id.detailedGoalExpLineChart);
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
            emptyTextView.setVisibility(View.VISIBLE);
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

    private class MyXAxisValueFormatter implements IAxisValueFormatter {

        private String[] values;

        MyXAxisValueFormatter(String[] values) {
            this.values = values;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            return values[(int) value];
        }


    }
}
