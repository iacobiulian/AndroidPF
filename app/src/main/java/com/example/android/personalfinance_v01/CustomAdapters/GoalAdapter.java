package com.example.android.personalfinance_v01.CustomAdapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.personalfinance_v01.MyClasses.Goal;
import com.example.android.personalfinance_v01.R;

import java.util.List;

import static com.example.android.personalfinance_v01.MyClasses.MyUtils.formatDateWithoutTime;
import static com.example.android.personalfinance_v01.MyClasses.MyUtils.formatDecimalTwoPlaces;

/**
 * Created by iacob on 15-Mar
 */

public class GoalAdapter extends ArrayAdapter<Goal> {
    public GoalAdapter(Context context, List<Goal> goalList) {
        super(context, 0, goalList);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_goal, parent, false);
        }

        Goal currentGoal = getItem(position);

        if (currentGoal != null) {
            TextView nameTv = convertView.findViewById(R.id.itemGoalNameTv);
            nameTv.setText(currentGoal.getName());

            TextView statusTv = convertView.findViewById(R.id.itemGoalStatus);
            if (currentGoal.getStatus() == Goal.REACHED) {
                statusTv.setText("Reached");
            } else if (currentGoal.getStatus() == Goal.NOT_REACHED) {
                statusTv.setText("Ongoing");
            }

            ProgressBar progressBar = convertView.findViewById(R.id.itemGoalProgressBar);
            int percentage = (int) (currentGoal.getSavedAmount() * 100 / currentGoal.getGoalAmount());
            progressBar.setMax(100);
            progressBar.setProgress(percentage);
            int[][] states = new int[][]{
                    new int[]{},
            };
            int[] colors;
            ColorStateList colorStateList;

            TextView goalAmountTv = convertView.findViewById(R.id.itemGoalAmountTv);
            goalAmountTv.setText(formatDecimalTwoPlaces(currentGoal.getGoalAmount()));

            TextView savedAmountTv = convertView.findViewById(R.id.itemGoalSavedAmountTv);
            savedAmountTv.setText(formatDecimalTwoPlaces(currentGoal.getSavedAmount()));

            TextView remainingAmountTv = convertView.findViewById(R.id.itemGoalRemainingAmountTv);
            remainingAmountTv.setText(formatDecimalTwoPlaces(currentGoal.getGoalAmount() - currentGoal.getSavedAmount()));

            if (percentage <= 20) {
                int color = ContextCompat.getColor(getContext(), R.color.debtGoalVeryLow);
                savedAmountTv.setTextColor(color);
                remainingAmountTv.setTextColor(color);
                colors = new int[]{color};
                colorStateList = new ColorStateList(states, colors);
            } else if (percentage <= 40) {
                int color = ContextCompat.getColor(getContext(), R.color.debtGoalLow);
                savedAmountTv.setTextColor(color);
                remainingAmountTv.setTextColor(color);
                colors = new int[]{color};
                colorStateList = new ColorStateList(states, colors);
            } else if (percentage <= 60) {
                int color = ContextCompat.getColor(getContext(), R.color.debtGoalMedium);
                savedAmountTv.setTextColor(color);
                remainingAmountTv.setTextColor(color);
                colors = new int[]{color};
                colorStateList = new ColorStateList(states, colors);
            } else if (percentage <= 80) {
                int color = ContextCompat.getColor(getContext(), R.color.debtGoalHigh);
                savedAmountTv.setTextColor(color);
                remainingAmountTv.setTextColor(color);
                colors = new int[]{color};
                colorStateList = new ColorStateList(states, colors);
            } else {
                int color = ContextCompat.getColor(getContext(), R.color.debtGoalVeryHigh);
                savedAmountTv.setTextColor(color);
                remainingAmountTv.setTextColor(color);
                colors = new int[]{color};
                colorStateList = new ColorStateList(states, colors);
            }

            progressBar.setProgressTintList(colorStateList);

            TextView targetDateTv = convertView.findViewById(R.id.itemGoalTargetDateTv);
            targetDateTv.setText("Target date: " + formatDateWithoutTime(currentGoal.getTargetDate()));
        }

        return convertView;
    }
}
