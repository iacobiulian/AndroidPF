package com.example.android.personalfinance_v01;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
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

        //Floating action buttons
        fab_substract = findViewById(R.id.fabSubstract);
        fab_substract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startExpensesActivity(-1);
            }
        });

        fab_add = findViewById(R.id.fabAdd);
        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startExpensesActivity(MyUtils.INCOME_ACTIVITY);
            }
        });

        //Text View
        cashAmountTextView = findViewById(R.id.cashAmountTv);
        cashAmountTextView.setText(MyUtils.moneyAmount + "");
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

    private void startExpensesActivity(int intentCode) {
        Intent intent =  new Intent(MainActivity.this, AddExpenses.class);
        intent.putExtra("intentCode", intentCode);
        startActivity(intent);
    }
}
