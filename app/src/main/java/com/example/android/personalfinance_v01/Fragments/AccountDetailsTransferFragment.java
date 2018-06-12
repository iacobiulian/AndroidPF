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

import com.example.android.personalfinance_v01.CustomAdapters.TransferAdapter;
import com.example.android.personalfinance_v01.DetailedAccountHistoryActivity;
import com.example.android.personalfinance_v01.MyClasses.Transfer;
import com.example.android.personalfinance_v01.R;

import java.util.ArrayList;

public class AccountDetailsTransferFragment extends Fragment {

    View mainView;

    ListView listView;
    TextView emptyListView;
    TransferAdapter transferAdapter;

    DetailedAccountHistoryActivity parentActivity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_detailed_account_history_transfer, container, false);

        initListView();

        parentActivity = (DetailedAccountHistoryActivity) getActivity();
        parentActivity.updateTransferList();

        return mainView;
    }

    private void initListView() {
        listView = mainView.findViewById(R.id.fragmentDetailedAccHistoryTransListView);
        emptyListView = mainView.findViewById(R.id.fragmentDetailedAccTransEmptyTv);
        listView.setEmptyView(emptyListView);
        transferAdapter = new TransferAdapter(getContext(), new ArrayList<Transfer>());
        listView.setAdapter(transferAdapter);

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
                                parentActivity.deleteTransfer(adapterView, i);
                                break;
                        }
                        return false;
                    }
                });

                popupMenu.show();
            }
        });
    }

    public void updateListView(ArrayList<Transfer> transfers) {
        if (!transferAdapter.isEmpty()) {
            transferAdapter.clear();
        }
        transferAdapter.addAll(transfers);
        transferAdapter.notifyDataSetChanged();
    }
}
