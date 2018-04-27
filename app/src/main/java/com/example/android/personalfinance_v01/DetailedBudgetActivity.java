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
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

import static com.example.android.personalfinance_v01.MyClasses.MyUtils.addDaysToDate;
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

        currentBudget = (Budget) Objects.requireNonNull(getIntent().getExtras()).getSerializable("budget");

        initFields();

        initChart();

        initPeriod();
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

        switch (currentBudget.getType()) {
            case Budget.NONE:
            case Budget.WEEKLY:
                periodNames = new String[]{"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                long mondayOfThisWeek = calendar.getTime().getTime();
                initSpecificPeriod(mondayOfThisWeek, 7, 1);
                break;
            case Budget.MONTHLY:
                //get the value spent for each week of the month
                periodNames = new String[]{"Week 1", "Week 2", "Week 3", "Week 4"};
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                long firstDayOfMonth = calendar.getTime().getTime();
                initSpecificPeriod(firstDayOfMonth, 4, 7);
                break;
            case Budget.YEARLY:
                //get the value spent for each month of the year
                periodNames = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Dec"};
                calendar.set(Calendar.DAY_OF_YEAR, 1);
                long firstDayOfYear = calendar.getTime().getTime();
                initSpecificPeriod(firstDayOfYear, 12, 30);
                break;
            default:
                break;
        }

    }

    private void initSpecificPeriod(long startTime, int periodSize, int offset) {
        period = new double[periodSize];

        //filter the list based on budget category
        ArrayList<ExpenseIncome> filteredCategList = filterCategoryExpenseIncomeList(expenseIncomeList, currentBudget.getCategory());
        for (int i = 0; i < periodSize; i++) {
            ArrayList<ExpenseIncome> filteredTimeList =
                    filterTimeExpenseIncomeList(filteredCategList,
                            addDaysToDate(startTime, i * offset), addDaysToDate(startTime, (i + 1) * offset));
            for (ExpenseIncome expinc : filteredTimeList) {
                period[i] += expinc.getAmount();
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

    private ArrayList<ExpenseIncome> filterTimeExpenseIncomeList(ArrayList<ExpenseIncome> list, long lowerBound, long upperBound) {
        ArrayList<ExpenseIncome> filteredList = new ArrayList<>();
        for (ExpenseIncome item : list) {
            boolean lower = item.getDate() > lowerBound;
            boolean upper = item.getDate() < upperBound;
            if (lower && upper) {
                filteredList.add(item);
            }
        }
        return filteredList;
    }

    private void initChart() {
        BarChart barChart = findViewById(R.id.detailedBudgetExpLineChart);

        ArrayList<BarEntry> entries = new ArrayList<BarEntry>();

        //TODO https://www.youtube.com/watch?v=naPRHNfzDk8
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
}
