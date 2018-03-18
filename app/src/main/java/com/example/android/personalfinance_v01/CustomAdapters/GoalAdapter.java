package com.example.android.personalfinance_v01.CustomAdapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
            progressBar.setProgress(percentage);

            TextView goalAmountTv = convertView.findViewById(R.id.itemGoalAmountTv);
            goalAmountTv.setText("Goal: " + formatDecimalTwoPlaces(currentGoal.getGoalAmount()));

            TextView savedAmountTv = convertView.findViewById(R.id.itemGoalSavedAmountTv);
            savedAmountTv.setText("Saved: " + formatDecimalTwoPlaces(currentGoal.getSavedAmount()));

            TextView remainingAmountTv = convertView.findViewById(R.id.itemGoalRemainingAmountTv);
            remainingAmountTv.setText("Remaining: " + formatDecimalTwoPlaces(currentGoal.getGoalAmount() - currentGoal.getSavedAmount()));

            TextView targetDateTv = convertView.findViewById(R.id.itemGoalTargetDateTv);
            targetDateTv.setText("Target date: " + formatDateWithoutTime(currentGoal.getTargetDate()));
        }

        return convertView;
    }
}
