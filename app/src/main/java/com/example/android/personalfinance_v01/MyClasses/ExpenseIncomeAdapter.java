package com.example.android.personalfinance_v01.MyClasses;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.personalfinance_v01.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by iacob on 27-Feb-18.
 */

public class ExpenseIncomeAdapter extends ArrayAdapter<ExpenseIncome> {
    public ExpenseIncomeAdapter(Context context, List<ExpenseIncome> expenseIncomeList)
    {
        super(context, 0, expenseIncomeList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_history, parent, false);
        }

        ExpenseIncome expenseIncome = getItem(position);

        TextView amountTv = convertView.findViewById(R.id.historyMoneyAmountTv);
        amountTv.setText(MyUtils.formatDecimalTwoPlaces(expenseIncome.getAmount()));

        TextView currencyTv = convertView.findViewById(R.id.historyCurrencyTypeTv);
        currencyTv.setText(MyUtils.CURRENCY_TYPE);

        TextView signTv = convertView.findViewById(R.id.historySignTv);
        if(expenseIncome.isType() == ExpenseIncome.TYPE_INCOME) {
            signTv.setText(MyUtils.PLUS_SIGN);
            signTv.setTextColor(ContextCompat.getColor(getContext(), R.color.colorIncome));
            amountTv.setTextColor(ContextCompat.getColor(getContext(), R.color.colorIncome));
            currencyTv.setTextColor(ContextCompat.getColor(getContext(), R.color.colorIncome));
        }
        else {
            signTv.setText(MyUtils.MINUS_SIGN);
            signTv.setTextColor(ContextCompat.getColor(getContext(), R.color.colorExpense));
            amountTv.setTextColor(ContextCompat.getColor(getContext(), R.color.colorExpense));
            currencyTv.setTextColor(ContextCompat.getColor(getContext(), R.color.colorExpense));
        }

        ImageView iconIv = convertView.findViewById(R.id.hisoryCategoryImageView);
        iconIv.setImageResource(expenseIncome.getCategory().getIconID());

        TextView categoryTv = convertView.findViewById(R.id.historyCategoryTv);
        categoryTv.setText(expenseIncome.getCategory().getName());

        TextView dateTimeTv = convertView.findViewById(R.id.historyDateTimeTv);
        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
        String date = df.format(expenseIncome.getDate());
        dateTimeTv.setText(date);

        return convertView;
    }
}
