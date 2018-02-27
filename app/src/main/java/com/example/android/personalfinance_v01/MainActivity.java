package com.example.android.personalfinance_v01;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.personalfinance_v01.MyClasses.MyUtils;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle actionBarDrawerToggle;

    TextView cashAmountTextView;

    com.github.clans.fab.FloatingActionButton fab_add;
    com.github.clans.fab.FloatingActionButton fab_substract;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Toolbar
        Toolbar toolbar = findViewById(R.id.mainToolbar);
        setSupportActionBar(toolbar);

        //Navigation drawer
        drawerLayout = findViewById(R.id.mainDrawerLayout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Drawer items onClicks
        navigationView = findViewById(R.id.mainNavigationView);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()) {
                    case(R.id.navMenuHistory):
                        MyUtils.startActivity(MainActivity.this, HistoryActivity.class);
                        break;
                }
                return true;
            }
        });

        //Floating action buttons
        fab_substract = findViewById(R.id.fabSubstract);
        fab_substract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyUtils.startActivityWithCode(MainActivity.this, AddExpenses.class, MyUtils.EXPENSE_ACTIVITY);
            }
        });

        fab_add = findViewById(R.id.fabAdd);
        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyUtils.startActivityWithCode(MainActivity.this, AddExpenses.class, MyUtils.INCOME_ACTIVITY);
            }
        });

        //Text View
        cashAmountTextView = findViewById(R.id.cashAmountTv);
        cashAmountTextView.setText(MyUtils.formatDecimalTwoPlaces(MyUtils.moneyAmount));
        cashAmountTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: Add onClick to the Money TextView
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //Navigation drawer button
        if(actionBarDrawerToggle.onOptionsItemSelected(item))
            return true;

        int id = item.getItemId();

        switch(id) {
            case R.id.actionSettings:
                Toast.makeText(MainActivity.this, "Settings clicked", Toast.LENGTH_SHORT).show();
                break;
            case R.id.actionExit:
                Toast.makeText(MainActivity.this, "Exit clicked", Toast.LENGTH_SHORT).show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

}
