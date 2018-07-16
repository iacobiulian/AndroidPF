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

import com.example.android.personalfinance_v01.CustomAdapters.HistoryItemGoalDebtAdapter;
import com.example.android.personalfinance_v01.DetailedGoalTabbedActivity;
import com.example.android.personalfinance_v01.MyClasses.HistoryItem;
import com.example.android.personalfinance_v01.R;

import java.util.ArrayList;

public class DetailedGoalHistoryFragment extends Fragment {
    View mainView;

    ListView listView;
    TextView emptyListView;
    HistoryItemGoalDebtAdapter historyItemAdapter;

    DetailedGoalTabbedActivity parentActivity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_detailed_goal_history, container, false);

        initListView();

        parentActivity = (DetailedGoalTabbedActivity) getActivity();
        parentActivity.updateList();

        return mainView;
    }

    private void initListView() {
        listView = mainView.findViewById(R.id.fragmentDetailedGoalHistoryListView);
        emptyListView = mainView.findViewById(R.id.fragmentDetailedGoalEmptyTv);
        listView.setEmptyView(emptyListView);
        historyItemAdapter = new HistoryItemGoalDebtAdapter(getContext(), new ArrayList<HistoryItem>());
        listView.setAdapter(historyItemAdapter);

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
                                parentActivity.deleteHistoryItem(adapterView, i);
                                break;
                        }
                        return false;
                    }
                });

                popupMenu.show();
            }
        });
    }

    public void updateListView(ArrayList<HistoryItem> expenseIncomeList) {
        if (!historyItemAdapter.isEmpty()) {
            historyItemAdapter.clear();
        }
        historyItemAdapter.addAll(expenseIncomeList);
        historyItemAdapter.notifyDataSetChanged();
    }
}
