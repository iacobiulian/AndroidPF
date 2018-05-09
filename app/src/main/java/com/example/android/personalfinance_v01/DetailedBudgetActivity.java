package com.example.android.personalfinance_v01;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.personalfinance_v01.MyClasses.Budget;
import com.example.android.personalfinance_v01.MyClasses.Category;
import com.example.android.personalfinance_v01.MyClasses.ExpenseIncome;
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
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import static com.example.android.personalfinance_v01.MyClasses.MyUtils.expenseIncomeList;
import static com.example.android.personalfinance_v01.MyClasses.MyUtils.formatDecimalTwoPlaces;

public class DetailedBudgetActivity extends AppCompatActivity {
    private Budget currentBudget = null;
    private double[] period;
    private String[] periodNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_budget);

        MyUtils.getExpenseIncomeFromDatabase(DetailedBudgetActivity.this);

        currentBudget = (Budget) Objects.requireNonNull(getIntent().getExtras()).getSerializable("budget");

        initPeriod();

        initFields();

        initChart();
    }

    @SuppressLint("SetTextI18n")
    private void initFields() {
        if (currentBudget != null) {
            ImageView categImg = findViewById(R.id.detailedBudgetImageView);
            categImg.setImageResource(currentBudget.getCategory().getIconID());

            TextView nameTv = findViewById(R.id.detailedBudgetCategoryTv);
            nameTv.setText(currentBudget.getCategory().getName());

            TextView statusTv = findViewById(R.id.detailedBudgetStatus);
            statusTv.setText(getType());

            ProgressBar progressBar = findViewById(R.id.detailedBudgetProgressBar);
            int percentage = (int) (currentBudget.getCurrentAmount() * 100 / currentBudget.getTotalAmount());
            progressBar.setProgress(percentage);

            TextView goalAmountTv = findViewById(R.id.detailedBudgetAmountTv);
            goalAmountTv.setText("Budget: " + formatDecimalTwoPlaces(currentBudget.getTotalAmount()));

            TextView savedAmountTv = findViewById(R.id.detailedBudgetSavedAmountTv);
            savedAmountTv.setText("Spent: " + formatDecimalTwoPlaces(currentBudget.getCurrentAmount()));

            TextView remainingAmountTv = findViewById(R.id.detailedBudgetRemainingAmountTv);
            remainingAmountTv.setText("Remaining: " + formatDecimalTwoPlaces(currentBudget.getTotalAmount() -
                    currentBudget.getCurrentAmount()));

            TextView averageSpentTv = findViewById(R.id.detailedBudgetAverage);
            averageSpentTv.setText("Average spent " + formatDecimalTwoPlaces(getAverageSpent()));

            TextView recommendedSpentTv = findViewById(R.id.detailedBudgetRecommended);
            recommendedSpentTv.setText("Recommended spent " + formatDecimalTwoPlaces(getRecommendedSpent()));

            if (currentBudget.getType() == Budget.NONE) {
                averageSpentTv.setVisibility(View.GONE);
                recommendedSpentTv.setVisibility(View.GONE);
            }

        }
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

    private void initPeriod() {
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.set(Calendar.HOUR_OF_DAY,0);

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
                MyUtils.makeToast(DetailedBudgetActivity.this, "You should never see this");
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
            //TODO USE BUDGET.ISINDATEAREA() HERE
            calendarLower.setTime(startDate);
            calendarUpper.setTime(startDate);
            calendarLower.add(calendarType, i);
            calendarUpper.add(calendarType, i + 1);
            lowerBound = calendarLower.getTime();
            upperBound = calendarUpper.getTime();
            ArrayList<ExpenseIncome> filteredTimeList =
                    new ArrayList<>(filterTimeExpenseIncomeList(filteredCategList, lowerBound, upperBound));

            if(!filteredTimeList.isEmpty()) {
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
            boolean otherCond = date.after(new Date(currentBudget.getCreationDate()));
            if (cond && otherCond) {
                filteredList.add(item);
            }
        }
        return filteredList;
    }

    private void initChart() {
        TextView emptyTextView = findViewById(R.id.detailedBudgetEmptyTv);
        emptyTextView.setVisibility(View.GONE);

        BarChart barChart = findViewById(R.id.detailedBudgetExpLineChart);
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
        } else {
            barChart.setVisibility(View.GONE);
            emptyTextView.setVisibility(View.VISIBLE);
        }

    }

    private boolean isPeriodPopulated() {
        for (double item : period) {
            if (item > 0.0) {
                return true;
            }
        }

        return false;
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

    public class MyXAxisValueFormatter implements IAxisValueFormatter {

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
