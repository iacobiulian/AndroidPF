package com.example.android.personalfinance_v01;

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
import android.widget.Toast;

import com.example.android.personalfinance_v01.CustomAdapters.BalanceAccountAdapterMain;
import com.example.android.personalfinance_v01.MyClasses.ExpenseIncome;
import com.example.android.personalfinance_v01.MyClasses.MyUtils;
import com.github.clans.fab.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    //Navigation drawer
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle actionBarDrawerToggle;

    //List view
    ListView listView;

    //Fab
    FloatingActionButton fab_add;
    FloatingActionButton fab_substract;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Toolbar
        Toolbar toolbar = findViewById(R.id.mainToolbar);
        setSupportActionBar(toolbar);

        //Navigation drawer
        initDrawer();
        //Navigation drawer menu items onClicks
        initDrawerItems();

        //Fab
        initFab();

        //ListView
        initListView();

        //'Add account' button
        Button button = findViewById(R.id.mainAddAccountBtn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyUtils.startActivity(MainActivity.this, CreateAccountActivity.class);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        //Set the first listview view as 'selected'
        //If you do this in onCreate() you get a nullptr exception
        if (listView.getChildAt(listView.getFirstVisiblePosition()) != null)
            listView.getChildAt(listView.getFirstVisiblePosition()).setSelected(true);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Navigation drawer button
        if (actionBarDrawerToggle.onOptionsItemSelected(item))
            return true;

        int id = item.getItemId();

        switch (id) {
            case R.id.actionSettings:
                Toast.makeText(MainActivity.this, "Settings clicked", Toast.LENGTH_SHORT).show();
                break;
            case R.id.actionExit:
                Toast.makeText(MainActivity.this, "Exit clicked", Toast.LENGTH_SHORT).show();
                break;
        }

        return super.onOptionsItemSelected(item);
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
                        MyUtils.startActivity(MainActivity.this, HistoryActivity.class);
                        break;
                    case (R.id.navMenuAccounts):
                        MyUtils.startActivity(MainActivity.this, AccountListActivity.class);
                        break;
                    case (R.id.navMenuDebts):
                        MyUtils.startActivity(MainActivity.this, DebtListActivity.class);
                        break;
                }
                return true;
            }
        });
    }

    private void initFab() {
        fab_substract = findViewById(R.id.fabSubstract);
        fab_substract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyUtils.startActivityWithCode(MainActivity.this, AddExpensesActivity.class, ExpenseIncome.TYPE_EXPENSE);
            }
        });

        fab_add = findViewById(R.id.fabAdd);
        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyUtils.startActivityWithCode(MainActivity.this, AddExpensesActivity.class, ExpenseIncome.TYPE_INCOME);
            }
        });
    }

    private void initListView() {
        MyUtils.getBalanceAccountsFromDatabase(MainActivity.this);

        listView = findViewById(R.id.mainAccountListView);
        listView.setEmptyView(findViewById(R.id.mainAddAccountBtn));
        BalanceAccountAdapterMain balanceAccountAdapter = new BalanceAccountAdapterMain(this, MyUtils.accountList);
        listView.setAdapter(balanceAccountAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                view.setSelected(true);
                MyUtils.setSelected(i);
            }
        });
    }
}
