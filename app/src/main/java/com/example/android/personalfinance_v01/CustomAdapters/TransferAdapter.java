package com.example.android.personalfinance_v01.CustomAdapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.android.personalfinance_v01.MyClasses.MyUtils;
import com.example.android.personalfinance_v01.MyClasses.Transfer;
import com.example.android.personalfinance_v01.R;

import java.util.List;

public class TransferAdapter extends ArrayAdapter<Transfer> {

    public TransferAdapter(Context context, List<Transfer> transferList) {
        super(context, 0, transferList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_transfer, parent, false);
        }

        Transfer currentTransfer = getItem(position);

        if (currentTransfer != null) {
            TextView fromTv = convertView.findViewById(R.id.itemTransferFromTv);
            TextView toTv = convertView.findViewById(R.id.itemTransferToTv);
            TextView currencyTv = convertView.findViewById(R.id.itemTransferCurrencyTv);
            TextView amountTv = convertView.findViewById(R.id.itemTransferAmountTv);
            TextView dateTv = convertView.findViewById(R.id.itemTransfertartDateTv);

            fromTv.setText(currentTransfer.getFromAccount().getName());
            toTv.setText(currentTransfer.getToAccount().getName());
            currencyTv.setText(currentTransfer.getFromAccount().getCurrency());
            amountTv.setText(MyUtils.formatDecimalOnePlace(currentTransfer.getAmount()));
            dateTv.setText(MyUtils.formatDateWithTime(currentTransfer.getCreationDate()));
        }

        return convertView;
    }

}
