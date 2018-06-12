package com.example.android.personalfinance_v01.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.personalfinance_v01.DetailedDebtTabbedActivity;
import com.example.android.personalfinance_v01.R;
import com.github.mikephil.charting.charts.BarChart;

public class DetailedDebtChartFragment extends Fragment {

    public DetailedDebtTabbedActivity parentActivity;
    public TextView fromTv;
    public TextView toTv;
    public TextView initialAmountTv;
    public TextView paidBackAmountTv;
    public TextView remainingAmountTv;
    public ProgressBar progressBar;
    public TextView startDate;
    public TextView dueDate;
    public TextView closed;
    public TextView emptyTextView;
    public BarChart barChart;
    View mainView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_detailed_debt_chart, container, false);

        parentActivity = (DetailedDebtTabbedActivity) getActivity();
        initFields();
        parentActivity.initFields();
        parentActivity.initChart();

        return mainView;
    }

    private void initFields() {
        fromTv = mainView.findViewById(R.id.detailedDebtFromTv);
        toTv = mainView.findViewById(R.id.detailedDebtToTv);
        initialAmountTv = mainView.findViewById(R.id.detailedDebtInitialAmountTv);
        paidBackAmountTv = mainView.findViewById(R.id.detailedDebtPaidBackAmountTv);
        remainingAmountTv = mainView.findViewById(R.id.detailedDebtRemainingAmountTv);
        progressBar = mainView.findViewById(R.id.detailedDebtProgressBar);
        startDate = mainView.findViewById(R.id.detailedDebtStartDateTv);
        dueDate = mainView.findViewById(R.id.detailedDebtEndDateTv);
        closed = mainView.findViewById(R.id.detailedDebtClosed);
        emptyTextView = mainView.findViewById(R.id.detailedDebtEmptyTv);
        barChart = mainView.findViewById(R.id.detailedDebtExpLineChart);
    }

}