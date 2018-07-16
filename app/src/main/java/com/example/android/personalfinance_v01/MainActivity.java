package com.example.android.personalfinance_v01;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.android.personalfinance_v01.CustomAdapters.BalanceAccountAdapterMain;
import com.example.android.personalfinance_v01.MyClasses.BalanceAccount;
import com.example.android.personalfinance_v01.MyClasses.Budget;
import com.example.android.personalfinance_v01.MyClasses.Debt;
import com.example.android.personalfinance_v01.MyClasses.ExpenseIncome;
import com.example.android.personalfinance_v01.MyClasses.Goal;
import com.example.android.personalfinance_v01.MyClasses.MyUtils;
import com.example.android.personalfinance_v01.MyClasses.Transfer;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.util.Calendar;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    public static final int TYPE_TRANSFER = 3;
    public static final int ERROR_INPUT_ZERO_EXP = -1;
    public static final int ERROR_INPUT_ZERO_INC = -2;
    public static final int ERROR_INPUT_ZERO_TRA = -3;
    public static final int BUDGET_HALF_SPENT = 10;
    public static final int BUDGET_EXCEEDED = 20;
    public static final int BUDGET_ALREADY_EXCEEDED = 30;

    //Navigation drawer
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle actionBarDrawerToggle;

    //List view
    ListView listView;

    //Fab
    FloatingActionButton fab_add;
    FloatingActionButton fab_subtract;
    FloatingActionButton fab_transfer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initNotificationChannels(MainActivity.this);

        MyUtils.getExpenseIncomeFromDatabase(this);
        MyUtils.getTransfersFromDatabase(this);

        //Toolbar
        Toolbar toolbar = findViewById(R.id.mainToolbar);
        setSupportActionBar(toolbar);

        //Navigation drawer
        initDrawer();
        //Navigation drawer menu items onClicks
        initDrawerItems();

        //ListView
        initListView();

        //Fab
        initFab();

        //Budgets
        runOncePerDay();

        //Jobs
        //scheduleJobs();

        //'Add account' button
        Button button = findViewById(R.id.mainAddAccountBtn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyUtils.startActivity(MainActivity.this, AddAccountActivity.class);
            }
        });

        showSnackbarIfNeeded();
    }

    public void initNotificationChannels(Context context) {
        if (Build.VERSION.SDK_INT < 26) {
            return;
        }
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel channel = new NotificationChannel("default",
                "Channel name",
                NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription("Channel description");
        Objects.requireNonNull(notificationManager).createNotificationChannel(channel);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        //Set the first listview view as 'selected'
        //If you do this in onCreate() you get a nullptr exception
        if (listView.getChildAt(listView.getFirstVisiblePosition()) != null)
            listView.getChildAt(listView.getFirstVisiblePosition()).setSelected(true);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Navigation drawer button
        return actionBarDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);

    }

    private void initDrawer() {
        drawerLayout = findViewById(R.id.mainDrawerLayout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initDrawerItems() {
        navigationView = findViewById(R.id.mainNavigationView);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case (R.id.navMenuHistory):
                        MyUtils.startActivity(MainActivity.this, HistoryTabbedActivity.class);
                        break;
                    case (R.id.navMenuTransferHistory):
                        MyUtils.startActivity(MainActivity.this, TransferHistoryActivity.class);
                        break;
                    case (R.id.navMenuAccounts):
                        MyUtils.startActivity(MainActivity.this, ListAccountActivity.class);
                        break;
                    case (R.id.navMenuStats):
                        MyUtils.startActivity(MainActivity.this, ChartsActivity.class);
                        break;
                    case (R.id.navMenuBudgets):
                        MyUtils.startActivity(MainActivity.this, ListBudgetActivity.class);
                        break;
                    case (R.id.navMenuDebts):
                        MyUtils.startActivity(MainActivity.this, ListDebtActivity.class);
                        break;
                    case (R.id.navMenuGoals):
                        MyUtils.startActivity(MainActivity.this, ListGoalActivity.class);
                        break;
                }
                return true;
            }
        });
    }

    private void initFab() {
        FloatingActionMenu mainFab = findViewById(R.id.fabMain);
        if (MyUtils.accountList.isEmpty()) {
            mainFab.setVisibility(View.GONE);
            return;
        }

        fab_subtract = findViewById(R.id.fabSubstract);
        fab_subtract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MyUtils.accountList.size() < 1) {
                    Snackbar snackbar = MyUtils.makeSnackbar(findViewById(R.id.mainDrawerLayout), getString(R.string.error), Snackbar.LENGTH_LONG);
                    snackbar.show();
                } else {
                    MyUtils.startActivityWithCode(MainActivity.this, AddExpIncomeTabbedActivity.class, ExpenseIncome.TYPE_EXPENSE);
                }
            }
        });

        fab_add = findViewById(R.id.fabAdd);
        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MyUtils.accountList.size() < 1) {
                    Snackbar snackbar = MyUtils.makeSnackbar(findViewById(R.id.mainDrawerLayout), getString(R.string.error), Snackbar.LENGTH_LONG);
                    snackbar.show();
                } else {
                    MyUtils.startActivityWithCode(MainActivity.this, AddExpIncomeTabbedActivity.class, ExpenseIncome.TYPE_INCOME);
                }
            }
        });

        fab_transfer = findViewById(R.id.fabTransfer);
        if (MyUtils.accountList.size() < 2) {
            fab_transfer.setVisibility(View.GONE);
        }
        fab_transfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyUtils.startActivityWithCode(MainActivity.this, AddExpIncomeTabbedActivity.class, TYPE_TRANSFER);
            }
        });
    }

    private void initListView() {
        MyUtils.getBalanceAccountsFromDatabase(MainActivity.this);
        changeSelectedAccountDetails();

        listView = findViewById(R.id.mainAccountListView);
        listView.setEmptyView(findViewById(R.id.mainAddAccountBtn));
        BalanceAccountAdapterMain balanceAccountAdapter = new BalanceAccountAdapterMain(this, MyUtils.accountList);
        listView.setAdapter(balanceAccountAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                view.setSelected(true);
                MyUtils.setSelected(i);
                changeSelectedAccountDetails();
            }
        });
    }

    private void changeSelectedAccountDetails() {
        BalanceAccount balanceAccount = MyUtils.getSelectedAccount();
        if (balanceAccount == null) {
            RelativeLayout relativeLayout = findViewById(R.id.mainAccDetailsRelLay);
            relativeLayout.setVisibility(View.GONE);
            return;
        }

        TextView accName = findViewById(R.id.mainAccNameTv);
        TextView accBalance = findViewById(R.id.mainAccBalanceTv);
        TextView accLastUsed = findViewById(R.id.mainAccLastUsedTv);

        accName.setText(balanceAccount.getName());

        accBalance.setText(MyUtils.formatDecimalOnePlace(balanceAccount.getBalance()));

        long lastUsed = getLastUsedDate(balanceAccount);
        if (lastUsed == 0) {
            accLastUsed.setText(getResources().getString(R.string.neverUsed));
        } else {
            accLastUsed.setText(String.format(getResources().getString(R.string.lastUsed), MyUtils.formatDateWithoutTime(lastUsed)));
        }
    }

    private long getLastUsedDate(BalanceAccount balanceAccount) {

        long dateExp = 0;
        long dateTrans = 0;

        for (int i = MyUtils.expenseIncomeList.size() - 1; i >= 0; i--) {
            ExpenseIncome expenseIncome = MyUtils.expenseIncomeList.get(i);
            if (expenseIncome.getAccount().equals(balanceAccount)) {
                dateExp = expenseIncome.getDate();
            }
        }

        for (int i = MyUtils.transferList.size() - 1; i >= 0; i--) {
            Transfer transfer = MyUtils.transferList.get(i);
            if (transfer.getToAccount().equals(balanceAccount) || transfer.getFromAccount().equals(balanceAccount)) {
                dateTrans = transfer.getCreationDate();
            }
        }

        return dateExp > dateTrans ? dateExp : dateTrans;
    }

    private void showSnackbarIfNeeded() {
        Intent intent = getIntent();
        if (intent == null) {
            return;
        }

        Bundle bundle = getIntent().getExtras();

        if (bundle == null) {
            return;
        }

        int code = bundle.getInt(AddExpIncomeTabbedActivity.DONE_CODE);
        Budget budget = (Budget) bundle.getSerializable(AddExpIncomeTabbedActivity.NOTIF_BUDGET);
        Snackbar snackbar;

        switch (code) {
            case 0:
                return;
            case ERROR_INPUT_ZERO_EXP:
                snackbar = MyUtils.makeSnackbarError(findViewById(R.id.mainDrawerLayout), getString(R.string.errorInputZero), Snackbar.LENGTH_SHORT);
                snackbar.setAction(R.string.tryAgain, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MyUtils.startActivityWithCode(MainActivity.this, AddExpIncomeTabbedActivity.class, ExpenseIncome.TYPE_EXPENSE);
                    }
                });
                snackbar.show();
                break;
            case ERROR_INPUT_ZERO_INC:
                snackbar = MyUtils.makeSnackbarError(findViewById(R.id.mainDrawerLayout), getString(R.string.errorInputZero), Snackbar.LENGTH_SHORT);
                snackbar.setAction(R.string.tryAgain, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MyUtils.startActivityWithCode(MainActivity.this, AddExpIncomeTabbedActivity.class, ExpenseIncome.TYPE_INCOME);
                    }
                });
                snackbar.show();
                break;
            case ERROR_INPUT_ZERO_TRA:
                snackbar = MyUtils.makeSnackbarError(findViewById(R.id.mainDrawerLayout), getString(R.string.errorInputZero), Snackbar.LENGTH_SHORT);
                snackbar.setAction(R.string.tryAgain, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MyUtils.startActivityWithCode(MainActivity.this, AddExpIncomeTabbedActivity.class, TYPE_TRANSFER);
                    }
                });
                snackbar.show();
                break;
            case BUDGET_ALREADY_EXCEEDED:
                //makeBudgetNotificationSnackbar(budget, R.string.notificationAlreadyExceededBudgetBody);
                break;
            case BUDGET_EXCEEDED:
                //makeBudgetNotificationSnackbar(budget, R.string.notificationExceededBudgetBody);
                break;
            case BUDGET_HALF_SPENT:
                //makeBudgetNotificationSnackbar(budget, R.string.notificationExceededHalfBudgetBody);
                break;
        }
    }

    private void runOncePerDay() {
        Calendar calendar = Calendar.getInstance();
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        SharedPreferences sharedPreferences = getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        int lastDay = sharedPreferences.getInt("day", 0);

        if (lastDay != currentDay) {
            //a day has passed
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("day", currentDay);
            editor.apply();

            checkBudgetsReset();
            makeGoalNotification();
            makeDebtNotification();
        }
    }

    private void checkBudgetsReset() {
        MyUtils.getBudgetsFromDatabase(MainActivity.this);

        for (Budget item : MyUtils.budgetList) {
            if (item.isResetBudget()) {
                MyUtils.modifyBudgetCurrentAmount(MainActivity.this, item, 0.0);
                MyUtils.modifyBudgetResetDate(MainActivity.this, item, item.getResetDate());
            }
        }

        MyUtils.getBudgetsFromDatabase(MainActivity.this);
    }

    private void makeGoalNotification() {
        MyUtils.getGoalsFromDatabase(this);

        for (Goal item : MyUtils.goalList) {
            Long lastDateLong = item.getAddedAmountsDates().get(item.getAddedAmountsDates().size() - 1);
            Long rightNow = MyUtils.getCurrentDateTime();

            int daysBetween = MyUtils.daysBetween(lastDateLong, rightNow);

            ListGoalActivity listGoalActivity = new ListGoalActivity();

            if (daysBetween > 29 && daysBetween % 15 == 0) {
                Intent intent = new Intent(getApplicationContext(), DetailedGoalTabbedActivity.class);
                intent.putExtra("goal", item);

                String title = String.format(getString(R.string.rememberGoal), item.getName());
                MyUtils.createNotification(MainActivity.this, listGoalActivity, intent, title, getString(R.string.goalNotAddInAWHile), R.drawable.notif_warning);
            }
        }
    }

    private void makeDebtNotification() {
        MyUtils.getDebtsFromDatabase(this);

        for (Debt item : MyUtils.debtList) {
            Long lastDateLong = item.getAddedAmountsDates().get(item.getAddedAmountsDates().size() - 1);
            Long rightNow = MyUtils.getCurrentDateTime();

            int daysBetween = MyUtils.daysBetween(lastDateLong, rightNow);

            ListDebtActivity listDebtActivity = new ListDebtActivity();

            if (daysBetween > 29 && daysBetween % 15 == 0) {
                Intent intent = new Intent(getApplicationContext(), DetailedDebtTabbedActivity.class);
                intent.putExtra("debt", item);

                String title;
                if (item.getType() == Debt.I_BORROW) {
                    title = String.format(getString(R.string.rememberDebtTo), item.getPayee());
                } else {
                    title = String.format(getString(R.string.rememberDebtFrom), item.getPayee());
                }
                MyUtils.createNotification(MainActivity.this, listDebtActivity, intent, title, getString(R.string.debtNotAddInAWHile), R.drawable.notif_warning);
            }
        }
    }

    private void makeBudgetNotificationSnackbar(Budget budget, int messageStringId) {
        final Intent intent = new Intent(MainActivity.this, DetailedBudgetTabbedActivity.class);
        intent.putExtra("budget", budget);

        String message = String.format(getResources().getString(messageStringId),
                budget.getTypeString(), budget.getCategory().getName());
        Snackbar snackbar = MyUtils.makeSnackbar(findViewById(R.id.mainDrawerLayout), message, Snackbar.LENGTH_LONG);
        snackbar.setAction(getString(R.string.takeMeThere), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.this.startActivity(intent);
            }
        });
        snackbar.show();
    }
}
