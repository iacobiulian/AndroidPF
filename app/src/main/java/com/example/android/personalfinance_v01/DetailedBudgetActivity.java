package com.example.android.personalfinance_v01;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.personalfinance_v01.MyClasses.Budget;
import com.github.mikephil.charting.charts.BarChart;

import java.util.ArrayList;

import static com.example.android.personalfinance_v01.MyClasses.MyUtils.formatDecimalTwoPlaces;

public class DetailedBudgetActivity extends AppCompatActivity {
    private Budget currentBudget = null;
    private ArrayList<Double> period = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_budget);

        currentBudget = (Budget) getIntent().getExtras().getSerializable("budget");

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
            recommendedSpentTv.setText("Recommended spent "+ formatDecimalTwoPlaces(getRecommendedSpent()));

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

    private void initChart() {
        BarChart barChart = findViewById(R.id.detailedBudgetExpBarChart);
    }

    public double getAverageSpent() {
        double sum = 0.0;
        for (double item : period)
            sum += item;

        return 0.0;

        //return sum / period.size();
    }

    public double getRecommendedSpent() {
        return currentBudget.getTotalAmount() / currentBudget.getType();
    }
}
