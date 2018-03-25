package com.example.android.personalfinance_v01.CustomAdapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.android.personalfinance_v01.MyClasses.BalanceAccount;
import com.example.android.personalfinance_v01.R;

import java.util.List;

/**
 * Created by iacob on 20-Mar
 */

public class BalanceAccountSpinnerAdapter extends ArrayAdapter<BalanceAccount> {

    public BalanceAccountSpinnerAdapter(Context context, List<BalanceAccount> accounts) {
        super(context, 0, accounts);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    private View initView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_balance_account_spinner, parent, false);
        }

        BalanceAccount currentAccount = getItem(position);

        if (currentAccount != null) {
            TextView nameTV = convertView.findViewById(R.id.itemBalAccSpinnerName);
            nameTV.setText(currentAccount.getName());

            TextView currencyTV = convertView.findViewById(R.id.itemBalAccSpinnerCurrency);
            currencyTV.setText(currentAccount.getCurrency());
        }

        return convertView;
    }
}
