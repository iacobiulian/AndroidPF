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

import com.example.android.personalfinance_v01.CustomAdapters.MyXAxisValueFormatter;
import com.example.android.personalfinance_v01.MyClasses.Debt;
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

public class DetailedDebtActivity extends AppCompatActivity {

    Debt currentDebt;
    private int CHART_SIZE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_debt);

        currentDebt = (Debt) Objects.requireNonNull(getIntent().getExtras()).getSerializable("debt");
        if (currentDebt == null) {
            return;
        }

        //TODO set the title
        setTitle(currentDebt.getPayee());

        if (currentDebt.getAddedAmounts().size() >= 5) {
            CHART_SIZE = 5;
        } else {
            CHART_SIZE = currentDebt.getAddedAmounts().size();
        }

        initFields();

        initChart();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("SetTextI18n")
    private void initFields() {
        if (currentDebt != null) {
            TextView fromTv = findViewById(R.id.detailedDebtFromTv);
            TextView toTv = findViewById(R.id.detailedDebtToTv);
            if (currentDebt.getType() == Debt.I_LEND) {
                fromTv.setText(R.string.me);
                toTv.setText(currentDebt.getPayee());
            } else {
                fromTv.setText(currentDebt.getPayee());
                toTv.setText(R.string.me);
            }

            TextView initialAmountTv = findViewById(R.id.detailedDebtInitialAmountTv);
            initialAmountTv.setText(formatDecimalTwoPlaces(currentDebt.getAmount()));

            TextView paidBackAmountTv = findViewById(R.id.detailedDebtPaidBackAmountTv);
            paidBackAmountTv.setText(formatDecimalTwoPlaces(currentDebt.getAmountPaidBack()));

            TextView remainingAmountTv = findViewById(R.id.detailedDebtRemainingAmountTv);
            remainingAmountTv.setText(formatDecimalTwoPlaces(currentDebt.getAmount() - currentDebt.getAmountPaidBack()));

            ProgressBar progressBar = findViewById(R.id.detailedDebtProgressBar);
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

            TextView startDate = findViewById(R.id.detailedDebtStartDateTv);
            startDate.setText("Start date: " + formatDateWithoutTime(currentDebt.getCreationDate()));

            TextView dueDate = findViewById(R.id.detailedDebtEndDateTv);
            dueDate.setText("Due date: " + formatDateWithoutTime(currentDebt.getPaybackDate()));

            TextView closed = findViewById(R.id.detailedDebtClosed);
            if (currentDebt.isClosed() == Debt.CLOSED) {
                closed.setText("Closed");
            } else if (currentDebt.isClosed() == Debt.NOT_CLOSED) {
                closed.setText("Open");
            }

        }
    }

    private void initChart() {
        TextView emptyTextView = findViewById(R.id.detailedDebtEmptyTv);
        emptyTextView.setVisibility(View.GONE);

        ArrayList<Double> amounts = currentDebt.getAddedAmounts();

        BarChart barChart = findViewById(R.id.detailedDebtExpLineChart);
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
}
