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
import com.example.android.personalfinance_v01.MyClasses.MyUtils;
import com.example.android.personalfinance_v01.R;

import java.util.List;

/**
 * Created by iacob on 01-Mar
 */

public class BalanceAccountAdapterMain extends ArrayAdapter<BalanceAccount> {

    public BalanceAccountAdapterMain(Context context, List<BalanceAccount> balanceAccountList) {
        super(context, 0, balanceAccountList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_balance_account_main, parent, false);
        }

        BalanceAccount currentAccount = getItem(position);

        if (currentAccount != null) {
            TextView nameTv = convertView.findViewById(R.id.itemBalAccMainName);
            nameTv.setText(currentAccount.getName());

            TextView currencyTv = convertView.findViewById(R.id.itemBalAccMainCurrency);
            currencyTv.setText(currentAccount.getCurrency());

            TextView balanceTv = convertView.findViewById(R.id.itemBalAccMainBalance);
            balanceTv.setText(MyUtils.formatDecimalTwoPlaces(currentAccount.getBalance()));
        }

        return convertView;
    }
}
