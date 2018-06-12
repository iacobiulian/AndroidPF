package com.example.android.personalfinance_v01.CustomAdapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.android.personalfinance_v01.MyClasses.HistoryItem;
import com.example.android.personalfinance_v01.MyClasses.MyUtils;
import com.example.android.personalfinance_v01.R;

import java.util.List;

public class HistoryItemGoalDebtAdapter extends ArrayAdapter<HistoryItem> {

    public HistoryItemGoalDebtAdapter(Context context, List<HistoryItem> historyItemList) {
        super(context, 0, historyItemList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_history_goal_debt, parent, false);
        }

        HistoryItem historyItem = getItem(position);

        if (historyItem != null) {
            TextView amountTv = convertView.findViewById(R.id.historyMoneyAmountTv);
            amountTv.setText(MyUtils.formatDecimalTwoPlaces(historyItem.getAmount()));

            TextView dateTimeTv = convertView.findViewById(R.id.historyDateTimeTv);
            String date = MyUtils.formatDateWithTime(historyItem.getTime());
            dateTimeTv.setText(date);
        }

        return convertView;
    }
}
