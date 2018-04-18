package com.example.android.personalfinance_v01.CustomAdapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.personalfinance_v01.MyClasses.Budget;
import com.example.android.personalfinance_v01.MyClasses.Goal;
import com.example.android.personalfinance_v01.R;

import java.util.List;

import static com.example.android.personalfinance_v01.MyClasses.MyUtils.formatDateWithoutTime;
import static com.example.android.personalfinance_v01.MyClasses.MyUtils.formatDecimalTwoPlaces;

public class BudgetAdapter extends ArrayAdapter<Budget> {

    public BudgetAdapter(Context context, List<Budget> budgetList) {
        super(context, 0, budgetList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_budget, parent, false);
        }

        Budget currentBudget = getItem(position);

        if (currentBudget != null) {
            ImageView categImg = convertView.findViewById(R.id.itemBudgetCategoryImageView);
            categImg.setImageResource(currentBudget.getCategory().getIconID());

            TextView nameTv = convertView.findViewById(R.id.itemBudgetCategoryTv);
            nameTv.setText(currentBudget.getCategory().getName());

            TextView statusTv = convertView.findViewById(R.id.itemBudgetStatus);
            switch (currentBudget.getType()) {
                case Budget.NONE:
                    statusTv.setText("None");
                    break;
                case Budget.WEEKLY:
                    statusTv.setText("Weekly");
                    break;
                case Budget.BI_WEEKLY:
                    statusTv.setText("Bi-weekly");
                    break;
                case Budget.MONTHLY:
                    statusTv.setText("Monthly");
                    break;
                case Budget.YEARLY:
                    statusTv.setText("Yearly");
                    break;
            }

            ProgressBar progressBar = convertView.findViewById(R.id.itemBudgetProgressBar);
            int percentage = (int) (currentBudget.getCurrentAmount() * 100 / currentBudget.getTotalAmount());
            progressBar.setProgress(percentage);

            TextView goalAmountTv = convertView.findViewById(R.id.itemBudgetAmountTv);
            goalAmountTv.setText("Budget: " + formatDecimalTwoPlaces(currentBudget.getTotalAmount()));

            TextView savedAmountTv = convertView.findViewById(R.id.itemBudgetSavedAmountTv);
            savedAmountTv.setText("Saved: " + formatDecimalTwoPlaces(currentBudget.getCurrentAmount()));

            TextView remainingAmountTv = convertView.findViewById(R.id.itemBudgetRemainingAmountTv);
            remainingAmountTv.setText("Remaining: " + formatDecimalTwoPlaces(currentBudget.getTotalAmount() -
                    currentBudget.getCurrentAmount()));
        }

        return convertView;
    }
}
