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
            initialAmountTv.setText("Initial: " + formatDecimalTwoPlaces(currentDebt.getAmount()));

            TextView paidBackAmountTv = convertView.findViewById(R.id.itemDebtPaidBackAmountTv);
            paidBackAmountTv.setText("Paid: " + formatDecimalTwoPlaces(currentDebt.getAmountPaidBack()));

            TextView remainingAmountTv = convertView.findViewById(R.id.itemDebtRemainingAmountTv);
            remainingAmountTv.setText("Remaining: " + formatDecimalTwoPlaces(currentDebt.getAmount() - currentDebt.getAmountPaidBack()));

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

            ProgressBar progressBar = convertView.findViewById(R.id.itemDebtProgressBar);
            int percentage = (int) (currentDebt.getAmountPaidBack() * 100/currentDebt.getAmount());
            progressBar.setProgress(percentage);
        }

        return convertView;
    }
}
