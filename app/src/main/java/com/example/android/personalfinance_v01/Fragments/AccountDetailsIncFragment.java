package com.example.android.personalfinance_v01.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.android.personalfinance_v01.CustomAdapters.ExpenseIncomeAdapter;
import com.example.android.personalfinance_v01.DetailedAccountHistoryActivity;
import com.example.android.personalfinance_v01.MyClasses.ExpenseIncome;
import com.example.android.personalfinance_v01.R;

import java.util.ArrayList;

public class AccountDetailsIncFragment extends Fragment {
    View mainView;

    ListView listView;
    TextView emptyListView;
    ExpenseIncomeAdapter expenseIncomeAdapter;

    DetailedAccountHistoryActivity parentActivity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_detailed_account_history_inc, container, false);

        initListView();

        parentActivity = (DetailedAccountHistoryActivity) getActivity();
        parentActivity.updateIncomeList();

        return mainView;
    }

    private void initListView() {
        listView = mainView.findViewById(R.id.fragmentDetailedAccHistoryIncListView);
        emptyListView = mainView.findViewById(R.id.fragmentDetailedAccIncEmptyTv);
        listView.setEmptyView(emptyListView);
        expenseIncomeAdapter = new ExpenseIncomeAdapter(getContext(), new ArrayList<ExpenseIncome>());
        listView.setAdapter(expenseIncomeAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> adapterView, View view, final int i, long l) {
                final PopupMenu popupMenu = new PopupMenu(getContext(), view);
                popupMenu.getMenuInflater().inflate(R.menu.menu_delete_item, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.menuDelete:
                                parentActivity.deleteExpenseIncome(adapterView, i);
                                break;
                        }
                        return false;
                    }
                });

                popupMenu.show();
            }
        });
    }

    public void updateListView(ArrayList<ExpenseIncome> expenseIncomeList) {
        if (!expenseIncomeAdapter.isEmpty()) {
            expenseIncomeAdapter.clear();
        }
        expenseIncomeAdapter.addAll(expenseIncomeList);
        expenseIncomeAdapter.notifyDataSetChanged();
    }
}
