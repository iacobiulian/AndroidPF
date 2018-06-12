package com.example.android.personalfinance_v01.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.personalfinance_v01.DetailedGoalTabbedActivity;
import com.example.android.personalfinance_v01.R;
import com.github.mikephil.charting.charts.BarChart;

public class DetailedGoalChartFragment extends Fragment {

    public DetailedGoalTabbedActivity parentActivity;
    public TextView nameTv;
    public TextView statusTv;
    public ProgressBar progressBar;
    public TextView goalAmountTv;
    public TextView savedAmountTv;
    public TextView remainingAmountTv;
    public TextView targetDateTv;
    public TextView recommendedSpentTv;
    public TextView emptyTextView;
    public BarChart barChart;
    View mainView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_detailed_goal_chart, container, false);

        parentActivity = (DetailedGoalTabbedActivity) getActivity();
        initFields();
        parentActivity.initFields();
        parentActivity.initChart();

        return mainView;
    }

    private void initFields() {
        nameTv = mainView.findViewById(R.id.detailedGoalNameTv);
        statusTv = mainView.findViewById(R.id.detailedGoalStatus);
        progressBar = mainView.findViewById(R.id.detailedGoalProgressBar);
        goalAmountTv = mainView.findViewById(R.id.detailedGoalAmountTv);
        savedAmountTv = mainView.findViewById(R.id.detailedGoalSavedAmountTv);
        remainingAmountTv = mainView.findViewById(R.id.detailedGoalRemainingAmountTv);
        targetDateTv = mainView.findViewById(R.id.detailedGoalTargetDateTv);
        recommendedSpentTv = mainView.findViewById(R.id.detailedGoalRecommended);
        emptyTextView = mainView.findViewById(R.id.detailedGoalEmptyTv);
        barChart = mainView.findViewById(R.id.detailedGoalExpLineChart);
    }
}
