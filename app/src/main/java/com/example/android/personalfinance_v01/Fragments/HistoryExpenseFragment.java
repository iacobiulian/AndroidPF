package com.example.android.personalfinance_v01.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
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
import com.example.android.personalfinance_v01.HistoryTabbedActivity;
import com.example.android.personalfinance_v01.MyClasses.ExpenseIncome;
import com.example.android.personalfinance_v01.MyClasses.MyUtils;
import com.example.android.personalfinance_v01.R;

import java.util.ArrayList;

/**
 * Created by iacob on 04-Apr
 */

public class HistoryExpenseFragment extends Fragment {

    View mainView;

    ListView listView;
    TextView emptyListView;
    ExpenseIncomeAdapter expenseIncomeAdapter;

    HistoryTabbedActivity parentActivity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_history_expense, container, false);

        initListView();

        parentActivity = (HistoryTabbedActivity) getActivity();
        parentActivity.updateExpenseList();

        return mainView;
    }

    private void initListView() {
        listView = mainView.findViewById(R.id.fragmentHistoryExpListView);
        emptyListView = mainView.findViewById(R.id.fragmentHistoryExpEmptyTv);
        listView.setEmptyView(emptyListView);
        expenseIncomeAdapter = new ExpenseIncomeAdapter(getContext(), new ArrayList<ExpenseIncome>());
        listView.setAdapter(expenseIncomeAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> adapterView, View view, final int i, long l) {
                final ExpenseIncome currentExpInc = (ExpenseIncome) adapterView.getItemAtPosition(i);

                final PopupMenu popupMenu = new PopupMenu(getContext(), view);
                popupMenu.getMenuInflater().inflate(R.menu.menu_delete_item, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.menuDelete:
                                expenseIncomeAdapter.remove(currentExpInc);
                                showUndoSnackbar(currentExpInc, expenseIncomeAdapter, i);
                                break;
                        }
                        return false;
                    }
                });

                popupMenu.show();
            }
        });
    }

    private void showUndoSnackbar(final ExpenseIncome expenseIncome, final ExpenseIncomeAdapter expenseIncomeAdapter, final int index) {
        Snackbar snackbar = MyUtils.makeSnackbar(mainView.findViewById(R.id.fragmentHistoryExpRelLay), getString(R.string.expense) + " " + getString(R.string.deleted), Snackbar.LENGTH_LONG);
        snackbar.setAction(getString(R.string.undo), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Undo clicked - add the item back
                expenseIncomeAdapter.insert(expenseIncome, index);
                expenseIncomeAdapter.notifyDataSetChanged();
            }
        });

        snackbar.addCallback(new Snackbar.Callback() {

            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                if (event != Snackbar.Callback.DISMISS_EVENT_ACTION) {
                    //Undo not clicked - delete item from db
                    parentActivity.deleteExpenseIncome(expenseIncome);
                }
            }

            @Override
            public void onShown(Snackbar snackbar) {
            }
        });

        snackbar.show();
    }

    public void updateListView(ArrayList<ExpenseIncome> expenseIncomeList) {
        if (!expenseIncomeAdapter.isEmpty()) {
            expenseIncomeAdapter.clear();
        }
        expenseIncomeAdapter.addAll(expenseIncomeList);
        expenseIncomeAdapter.notifyDataSetChanged();
    }
}
