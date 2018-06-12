package com.example.android.personalfinance_v01.Fragments;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.personalfinance_v01.CustomAdapters.MyXAxisValueFormatter;
import com.example.android.personalfinance_v01.DetailedBudgetTabbedActivity;
import com.example.android.personalfinance_v01.MyClasses.Budget;
import com.example.android.personalfinance_v01.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

import static com.example.android.personalfinance_v01.MyClasses.MyUtils.formatDecimalTwoPlaces;

public class DetailedBudgetChartFragment extends Fragment {

    public ImageView categImg;
    public TextView nameTv;
    public TextView statusTv;
    public ProgressBar progressBar;
    public TextView goalAmountTv;
    public TextView savedAmountTv;
    public TextView remainingAmountTv;
    public TextView averageSpentTv;
    public TextView recommendedSpentTv;
    public TextView emptyTextView;
    public BarChart barChart;
    public DetailedBudgetTabbedActivity parentActivity;
    View mainView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_detailed_budget_chart, container, false);

        parentActivity = (DetailedBudgetTabbedActivity) getActivity();
        initFields();
        parentActivity.initViews();
        parentActivity.initChart();

        return mainView;
    }

    private void initFields() {
        categImg = mainView.findViewById(R.id.fragmentDetailedBudgetImageView);
        nameTv = mainView.findViewById(R.id.fragmentDetailedBudgetCategoryTv);
        statusTv = mainView.findViewById(R.id.fragmentDetailedBudgetStatus);
        progressBar = mainView.findViewById(R.id.fragmentDetailedBudgetProgressBar);
        goalAmountTv = mainView.findViewById(R.id.fragmentDetailedBudgetAmountTv);
        savedAmountTv = mainView.findViewById(R.id.fragmentDetailedBudgetSavedAmountTv);
        remainingAmountTv = mainView.findViewById(R.id.fragmentDetailedBudgetRemainingAmountTv);
        averageSpentTv = mainView.findViewById(R.id.fragmentDetailedBudgetAverage);
        recommendedSpentTv = mainView.findViewById(R.id.fragmentDetailedBudgetRecommended);
        emptyTextView = mainView.findViewById(R.id.fragmentDetailedBudgetEmptyTv);
        barChart = mainView.findViewById(R.id.fragmentDetailedBudgetExpLineChart);
    }
}
