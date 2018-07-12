package com.example.android.personalfinance_v01;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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
import com.example.android.personalfinance_v01.MyClasses.MyUtils;
import com.github.clans.fab.FloatingActionButton;

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
                                View dialogView = getLayoutInflater().inflate(R.layout.dialog_enter_amount, listView, false);
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
                                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        switch (which) {
                                            case DialogInterface.BUTTON_POSITIVE:
                                                goalAdapter.remove(currentGoal);
                                                goalAdapter.notifyDataSetChanged();
                                                showUndoSnackbar(currentGoal, goalAdapter, i);
                                                break;

                                            case DialogInterface.BUTTON_NEGATIVE:
                                                break;
                                        }
                                    }
                                };

                                AlertDialog.Builder builder = new AlertDialog.Builder(ListGoalActivity.this);
                                builder.setMessage(getString(R.string.delete) + " " + getString(R.string.goal) + "?").setPositiveButton(getString(R.string.delete), dialogClickListener)
                                        .setNegativeButton(getString(R.string.cancel), dialogClickListener).show();
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
                                goalAdapter.remove(currentGoal);
                                goalAdapter.notifyDataSetChanged();
                                showUndoSnackbar(currentGoal, goalAdapter, i);
                                break;
                        }
                        return false;
                    }
                });

                if (currentGoal.getStatus() == Goal.NOT_REACHED) {
                    popupMenu.show();
                } else {
                    popupMenuDelete.show();
                }

            }
        });
    }

    private void showUndoSnackbar(final Goal goal, final GoalAdapter goalAdapter, final int index) {
        Snackbar snackbar = MyUtils.makeSnackbar(findViewById(R.id.goalListRelLay), getString(R.string.goal) + " " + getString(R.string.deleted), Snackbar.LENGTH_LONG);
        snackbar.setAction(getString(R.string.undo), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Undo clicked - add the item back
                goalAdapter.insert(goal, index);
                goalAdapter.notifyDataSetChanged();
            }
        });

        snackbar.addCallback(new Snackbar.Callback() {

            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                if (event != Snackbar.Callback.DISMISS_EVENT_ACTION) {
                    //Undo not clicked - delete item from db
                    deleteGoal(goal);
                }
            }

            @Override
            public void onShown(Snackbar snackbar) {
            }
        });

        snackbar.show();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (alertDialog != null)
            alertDialog.dismiss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (alertDialog != null)
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
                if (amountInput < 0) {
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
