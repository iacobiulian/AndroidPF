package com.example.android.personalfinance_v01;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
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
import com.example.android.personalfinance_v01.MyClasses.ExpenseIncome;
import com.example.android.personalfinance_v01.MyClasses.MyUtils;
import com.example.android.personalfinance_v01.MyClasses.Transfer;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    //TODO https://github.com/evernote/android-job

    public static final int TYPE_TRANSFER = 3;

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
        checkBudgetsReset();

        //Jobs
        //scheduleJobs();

        //'Add account' button
        Button button = findViewById(R.id.mainAddAccountBtn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyUtils.startActivity(MainActivity.this, CreateAccountActivity.class);
            }
        });
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
                        MyUtils.startActivity(MainActivity.this, AccountListActivity.class);
                        break;
                    case (R.id.navMenuStats):
                        MyUtils.startActivity(MainActivity.this, ChartsActivity.class);
                        break;
                    case (R.id.navMenuBudgets):
                        MyUtils.startActivity(MainActivity.this, BudgetListActivity.class);
                        break;
                    case (R.id.navMenuDebts):
                        MyUtils.startActivity(MainActivity.this, DebtListActivity.class);
                        break;
                    case (R.id.navMenuGoals):
                        MyUtils.startActivity(MainActivity.this, GoalListActivity.class);
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
                    MyUtils.makeToast(MainActivity.this, getResources().getString(R.string.error));
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
                    MyUtils.makeToast(MainActivity.this, getResources().getString(R.string.error));
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
            accLastUsed.setText("Never used");
        } else {
            accLastUsed.setText("Last used " + MyUtils.formatDateWithoutTime(lastUsed));
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

    private void checkBudgetsReset() {
        MyUtils.getBudgetsFromDatabase(MainActivity.this);

        for (Budget item : MyUtils.budgetList) {
            if (item.isResetBudget()) {
                MyUtils.modifyBudgetCurrentAmount(MainActivity.this, item, 0.0);
            }
        }

        MyUtils.getBudgetsFromDatabase(MainActivity.this);
    }
}









