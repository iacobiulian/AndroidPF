package com.example.android.personalfinance_v01.CustomAdapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.personalfinance_v01.MyClasses.BalanceAccount;
import com.example.android.personalfinance_v01.MyClasses.ExpenseIncome;
import com.example.android.personalfinance_v01.MyClasses.MyUtils;
import com.example.android.personalfinance_v01.R;

import java.util.List;

/**
 * Created by iacob on 27-Feb
 */

public class ExpenseIncomeAdapter extends ArrayAdapter<ExpenseIncome> {
    private Context context;

    public ExpenseIncomeAdapter(Context context, List<ExpenseIncome> expenseIncomeList) {
        super(context, 0, expenseIncomeList);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_expense_income_history, parent, false);
        }

        ExpenseIncome expenseIncome = getItem(position);

        if (expenseIncome != null) {
            TextView amountTv = convertView.findViewById(R.id.historyMoneyAmountTv);
            amountTv.setText(MyUtils.formatDecimalTwoPlaces(expenseIncome.getAmount()));

            BalanceAccount balanceAccount = expenseIncome.getAccount();

            TextView currencyTv = convertView.findViewById(R.id.historyCurrencyTypeTv);
            TextView accountTv = convertView.findViewById(R.id.historyAccountTv);
            if (balanceAccount != null) {
                accountTv.setTypeface(null, Typeface.NORMAL);
                accountTv.setText(balanceAccount.getName());
                currencyTv.setText(balanceAccount.getCurrency());
            } else { //If the account this expense/income belongs to has been deleted
                accountTv.setTypeface(null, Typeface.ITALIC);
                accountTv.setText(R.string.account_not_found);
                currencyTv.setText(R.string.currency);
            }

            TextView signTv = convertView.findViewById(R.id.historySignTv);
            if (expenseIncome.getType() == ExpenseIncome.TYPE_INCOME) {
                signTv.setText(context.getResources().getString(R.string.plusSign));
                signTv.setTextColor(ContextCompat.getColor(getContext(), R.color.colorIncome));
                amountTv.setTextColor(ContextCompat.getColor(getContext(), R.color.colorIncome));
                currencyTv.setTextColor(ContextCompat.getColor(getContext(), R.color.colorIncome));
            } else {
                signTv.setText(context.getResources().getString(R.string.minusSign));
                signTv.setTextColor(ContextCompat.getColor(getContext(), R.color.colorExpense));
                amountTv.setTextColor(ContextCompat.getColor(getContext(), R.color.colorExpense));
                currencyTv.setTextColor(ContextCompat.getColor(getContext(), R.color.colorExpense));
            }

            ImageView iconIv = convertView.findViewById(R.id.hisoryCategoryImageView);
            iconIv.setImageResource(expenseIncome.getCategory().getIconID());

            TextView categoryTv = convertView.findViewById(R.id.historyCategoryTv);
            categoryTv.setText(expenseIncome.getCategory().getName());

            TextView dateTimeTv = convertView.findViewById(R.id.historyDateTimeTv);
            String date = MyUtils.formatDateWithTime(expenseIncome.getDate());
            dateTimeTv.setText(date);
        }

        return convertView;
    }

}
