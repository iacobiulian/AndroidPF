package com.example.android.personalfinance_v01;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;

import com.example.android.personalfinance_v01.CustomAdapters.GoalAdapter;
import com.example.android.personalfinance_v01.DataPersistance.DatabaseHelper;
import com.example.android.personalfinance_v01.MyClasses.Goal;
import com.github.clans.fab.FloatingActionButton;
import com.example.android.personalfinance_v01.MyClasses.MyUtils;

public class ListGoalActivity extends AppCompatActivity {

    private ListView listView;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_goal);

        //Floating action button
        FloatingActionButton fab = findViewById(R.id.goalListFab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyUtils.startActivity(ListGoalActivity.this, AddGoalActivity.class);
            }
        });

        //ListView
        MyUtils.getGoalsFromDatabase(ListGoalActivity.this);
        listView = findViewById(R.id.goalListView);
        final GoalAdapter goalAdapter = new GoalAdapter(this, MyUtils.goalList);
        listView.setEmptyView(findViewById(R.id.goalListEmptyView));
        listView.setAdapter(goalAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> adapterView, View view, final int i, long l) {
                final Goal currentGoal = (Goal) adapterView.getItemAtPosition(i);

                final PopupMenu popupMenu = new PopupMenu(ListGoalActivity.this, view);
                popupMenu.getMenuInflater().inflate(R.menu.menu_goal_options, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.goalListMenuSave:
                                View dialogView = getLayoutInflater().inflate(R.layout.dialog_enter_amount, listView,false);
                                alertDialog = initAlertDialog(dialogView);
                                initAlertDialogButtons(dialogView, alertDialog, currentGoal);
                                alertDialog.show();
                                break;
                            case R.id.goalListMenuDetails:
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("goal", currentGoal);
                                MyUtils.startActivityWithBundle(ListGoalActivity.this, DetailedGoalTabbedActivity.class,
                                        bundle);
                                break;
                            case R.id.goalListMenuReached:
                                reachGoal(currentGoal, currentGoal.getGoalAmount());
                                break;
                            case R.id.goalListMenuDelete:
                                deleteGoal(currentGoal);
                                break;
                        }
                        goalAdapter.notifyDataSetChanged();
                        return false;
                    }
                });

                final PopupMenu popupMenuDelete = new PopupMenu(ListGoalActivity.this, view);
                popupMenuDelete.getMenuInflater().inflate(R.menu.menu_delete_item, popupMenuDelete.getMenu());

                popupMenuDelete.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.menuDelete:
                                deleteGoal(currentGoal);
                                goalAdapter.notifyDataSetChanged();
                                break;
                        }
                        return false;
                    }
                });

                if(currentGoal.getStatus() == Goal.NOT_REACHED) {
                    popupMenu.show();
                } else {
                    popupMenuDelete.show();
                }

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(alertDialog != null)
            alertDialog.dismiss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(alertDialog != null)
            alertDialog.dismiss();
    }

    private AlertDialog initAlertDialog(View dialogView) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ListGoalActivity.this);
        alertDialogBuilder.setView(dialogView);

        return alertDialogBuilder.create();
    }

    private void initAlertDialogButtons(View dialogView, final AlertDialog alertDialog, final Goal goalForUpdate) {
        final EditText amountEt = dialogView.findViewById(R.id.dialogAmountEt);
        Button submitBtn = dialogView.findViewById(R.id.dialogBtn);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double amountInput = MyUtils.getDoubleFromEditText(amountEt);
                if(amountInput < 0 ) {
                    return;
                }
                int goalId = new DatabaseHelper(ListGoalActivity.this).getGoalId(goalForUpdate);
                goalForUpdate.getAddedAmounts().add(amountInput);
                goalForUpdate.getAddedAmountsDates().add(MyUtils.getCurrentDateTime());
                String amounts = MyUtils.fromDoubleListToString(goalForUpdate.getAddedAmounts());
                String times = MyUtils.fromLongListToString(goalForUpdate.getAddedAmountsDates());
                double newAmount = goalForUpdate.getSavedAmount() + amountInput;
                modifySavedAmount(goalId, newAmount);
                updateGoalAmountLists(goalId, amounts, times);
                MyUtils.getGoalsFromDatabase(ListGoalActivity.this);
                alertDialog.hide();
            }
        });
    }

    private void updateGoalAmountLists(int goalId, String amounts, String times) {
        DatabaseHelper databaseHelper = new DatabaseHelper(ListGoalActivity.this);

        databaseHelper.updateGoalAmountsList(goalId, amounts, times);
    }

    private void modifySavedAmount(int goalId, double newAmount) {
        DatabaseHelper databaseHelper = new DatabaseHelper(ListGoalActivity.this);

        databaseHelper.updateGoalSavedAmount(goalId, newAmount);
    }

    private void reachGoal(Goal reachedGoal, double targetAmount) {
        DatabaseHelper databaseHelper = new DatabaseHelper(ListGoalActivity.this);

        int goalId = databaseHelper.getGoalId(reachedGoal);

        databaseHelper.updateGoalReached(goalId);
        databaseHelper.updateGoalSavedAmount(goalId, targetAmount);

        MyUtils.getGoalsFromDatabase(ListGoalActivity.this);
    }

    private void deleteGoal(Goal goalForDeletion) {
        DatabaseHelper databaseHelper = new DatabaseHelper(ListGoalActivity.this);

        databaseHelper.deleteGoal(databaseHelper.getGoalId(goalForDeletion));

        MyUtils.getGoalsFromDatabase(ListGoalActivity.this);
    }
}
