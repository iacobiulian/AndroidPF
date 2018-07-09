package com.example.android.personalfinance_v01.Fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.personalfinance_v01.ChartsActivity;
import com.example.android.personalfinance_v01.MyClasses.MyUtils;
import com.example.android.personalfinance_v01.R;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by iacob on 20-Mar
 */

public class StatsIncomeFragment extends Fragment {

    View mainView;

    PieChart pieChart;
    TextView emptyDataSetTv;

    float total = 0.0f;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_stats_income, container, false);

        pieChart = mainView.findViewById(R.id.fragmentStatsIncPieChart);
        emptyDataSetTv = mainView.findViewById(R.id.fragmentStatsIncEmptyTv);
        emptyDataSetTv.setVisibility(View.GONE);

        initPieChartStyle();

        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                pieChart.setCenterText("" + e.getY());
            }

            @Override
            public void onNothingSelected() {
                pieChart.setCenterText("Total:\n" + MyUtils.formatDecimalOnePlace(total));
            }
        });

        ChartsActivity chartsActivity = (ChartsActivity) getActivity();
        chartsActivity.updateIncomeChart();

        return mainView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    private ArrayList<Integer> chartColors() {
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.BLUE);
        colors.add(getActivity().getResources().getColor(R.color.darkYellow));
        colors.add(getActivity().getResources().getColor(R.color.darkCyan));
        colors.add(Color.RED); //4
        colors.add(getActivity().getResources().getColor(R.color.darkGreen));
        colors.add(Color.DKGRAY);
        colors.add(Color.MAGENTA);
        colors.add(Color.GRAY); //8
        colors.add(Color.YELLOW); //9

        return colors;
    }

    private void initPieChartStyle() {
        pieChart.setExtraOffsets(5, 5, 5, 5);

        pieChart.setUsePercentValues(true);

        pieChart.getDescription().setEnabled(false);

        //Rotate the chart
        pieChart.setDragDecelerationFrictionCoef(0.99f);

        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setCenterTextSize(14f);

        //Ony numbers as labels on the chart
        pieChart.setDrawEntryLabels(false);

        pieChart.animateY(1000, Easing.EasingOption.EaseInOutCubic);

        //Legend
        Legend legend = pieChart.getLegend();
        legend.setWordWrapEnabled(true);
        legend.setForm(Legend.LegendForm.CIRCLE);
    }

    private void initPieDataSetStyle(PieDataSet pieDataSet) {
        pieDataSet.setSliceSpace(1f);
        pieDataSet.setSelectionShift(5f);
        pieDataSet.setColors(chartColors());
        pieDataSet.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {

                return MyUtils.formatDecimalTwoPlaces(value) + " %";
            }
        });
    }

    public void setPieChartValues(HashMap<String, Double> incomeMap) {
        ArrayList<PieEntry> yValues = new ArrayList<>();
        total = 0.0f;

        if(incomeMap.isEmpty()) {
            pieChart.setVisibility(View.GONE);
            emptyDataSetTv.setVisibility(View.VISIBLE);
            return;
        } else {
            emptyDataSetTv.setVisibility(View.GONE);
            pieChart.setVisibility(View.VISIBLE);
        }

        for (Map.Entry<String, Double> entry : incomeMap.entrySet()) {
            yValues.add(new PieEntry(MyUtils.returnFloat(entry.getValue()), entry.getKey()));
            total += entry.getValue();
        }

        PieDataSet pieDataSet = new PieDataSet(yValues, "");
        initPieDataSetStyle(pieDataSet);

        PieData pieData = new PieData(pieDataSet);
        pieData.setValueTextSize(12f);
        pieData.setValueTextColor(Color.WHITE);

        //Set center text
        pieChart.setCenterText("Total:\n" + MyUtils.formatDecimalOnePlace(total));

        pieChart.setData(pieData);

        //Refresh
        pieChart.invalidate();
    }
}
