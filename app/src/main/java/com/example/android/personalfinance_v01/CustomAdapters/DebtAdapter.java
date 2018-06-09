package com.example.android.personalfinance_v01.CustomAdapters;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.personalfinance_v01.MyClasses.Debt;
import com.example.android.personalfinance_v01.R;

import java.util.List;

import static com.example.android.personalfinance_v01.MyClasses.MyUtils.formatDateWithoutTime;
import static com.example.android.personalfinance_v01.MyClasses.MyUtils.formatDecimalTwoPlaces;

/**
 * Created by iacob on 10-Mar
 */

public class DebtAdapter extends ArrayAdapter<Debt> {

    public DebtAdapter(Context context, List<Debt> debtList) {
        super(context, 0, debtList);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_debt, parent, false);
        }

        Debt currentDebt = getItem(position);

        if (currentDebt != null) {
            TextView fromTv = convertView.findViewById(R.id.itemDebtFromTv);
            TextView toTv = convertView.findViewById(R.id.itemDebtToTv);
            if (currentDebt.getType() == Debt.I_LEND) {
                fromTv.setText(R.string.me);
                toTv.setText(currentDebt.getPayee());
            } else {
                fromTv.setText(currentDebt.getPayee());
                toTv.setText(R.string.me);
            }

            TextView initialAmountTv = convertView.findViewById(R.id.itemDebtInitialAmountTv);
            initialAmountTv.setText(formatDecimalTwoPlaces(currentDebt.getAmount()));

            TextView paidBackAmountTv = convertView.findViewById(R.id.itemDebtPaidBackAmountTv);
            paidBackAmountTv.setText(formatDecimalTwoPlaces(currentDebt.getAmountPaidBack()));

            TextView remainingAmountTv = convertView.findViewById(R.id.itemDebtRemainingAmountTv);
            remainingAmountTv.setText(formatDecimalTwoPlaces(currentDebt.getAmount() - currentDebt.getAmountPaidBack()));

            ProgressBar progressBar = convertView.findViewById(R.id.itemDebtProgressBar);
            int percentage = (int) (currentDebt.getAmountPaidBack() * 100/currentDebt.getAmount());
            progressBar.setMax(100);
            progressBar.setProgress(percentage);
            int[][] states = new int[][]{
                    new int[]{},
            };
            int[] colors;
            ColorStateList colorStateList;

            if (percentage <= 20) {
                int color = ContextCompat.getColor(getContext(), R.color.debtGoalVeryLow);
                paidBackAmountTv.setTextColor(color);
                remainingAmountTv.setTextColor(color);
                colors = new int[]{ color };
                colorStateList = new ColorStateList(states, colors);
            } else if (percentage <= 40) {
                int color = ContextCompat.getColor(getContext(), R.color.debtGoalLow);
                paidBackAmountTv.setTextColor(color);
                remainingAmountTv.setTextColor(color);
                colors = new int[]{ color };
                colorStateList = new ColorStateList(states, colors);
            } else if (percentage <= 60) {
                int color = ContextCompat.getColor(getContext(), R.color.debtGoalMedium);
                paidBackAmountTv.setTextColor(color);
                remainingAmountTv.setTextColor(color);
                colors = new int[]{ color };
                colorStateList = new ColorStateList(states, colors);
            } else if (percentage <= 80) {
                int color = ContextCompat.getColor(getContext(), R.color.debtGoalHigh);
                paidBackAmountTv.setTextColor(color);
                remainingAmountTv.setTextColor(color);
                colors = new int[]{ color };
                colorStateList = new ColorStateList(states, colors);
            } else {
                int color = ContextCompat.getColor(getContext(), R.color.debtGoalVeryHigh);
                paidBackAmountTv.setTextColor(color);
                remainingAmountTv.setTextColor(color);
                colors = new int[]{ color };
                colorStateList = new ColorStateList(states, colors);
            }

            progressBar.setProgressTintList(colorStateList);

            TextView startDate = convertView.findViewById(R.id.itemDebtStartDateTv);
            startDate.setText("Start date: " + formatDateWithoutTime(currentDebt.getCreationDate()));

            TextView dueDate = convertView.findViewById(R.id.itemDebtEndDateTv);
            dueDate.setText("Due date: " + formatDateWithoutTime(currentDebt.getPaybackDate()));

            TextView closed = convertView.findViewById(R.id.itemDebtClosed);
            if(currentDebt.isClosed() == Debt.CLOSED) {
                closed.setText("Closed");
            } else if(currentDebt.isClosed() == Debt.NOT_CLOSED) {
                closed.setText("Open");
            }
        }

        return convertView;
    }
}
