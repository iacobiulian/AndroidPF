package com.example.android.personalfinance_v01;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class AddExpenses extends AppCompatActivity {

    private static final String BASE_VALUE = "0";

    String currentValue = BASE_VALUE;
    int intentCode;

    Toolbar toolbar;
    TextView moneyAmountTV;
    TextView signTV;
    Button delBtn;
    Button commaBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expenses);

        //Toolbar
        toolbar = findViewById(R.id.toolbarAddExp);
        setSupportActionBar(toolbar);

        //Money Amount TextView
        moneyAmountTV = findViewById(R.id.moneyAmountTV);

        //Sign Operator TextView
        intentCode = getIntent().getExtras().getInt("intentCode");
        signTV = findViewById(R.id.signTv);
        if(intentCode == MyUtils.INCOME_ACTIVITY)
            signTV.setText(MyUtils.PLUS_SIGN);

        //Special buttons
        delBtn = findViewById(R.id.delBtn);
        delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Deletes the last character
                if(currentValue.length() == 1 && !currentValue.contains(BASE_VALUE))
                    currentValue = BASE_VALUE;
                else if(currentValue.length() > 1)
                    currentValue = currentValue.substring(0, currentValue.length() - 1);
                updateTextView();

            }
        });
        delBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                currentValue = BASE_VALUE;
                updateTextView();
                return true;
            }
        });

        commaBtn = findViewById(R.id.commaBtn);
        commaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!currentValue.contains(".")) {
                    currentValue += ".";
                    updateTextView();
                }
            }
        });

        //Back button on the toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_expenses, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch(id) {
            case R.id.actionDone:
                double money = 0.0;
                try {
                    money = Double.valueOf(moneyAmountTV.getText().toString());
                } catch(NumberFormatException e) {
                    Toast.makeText(this, R.string.error, Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                if(intentCode == MyUtils.INCOME_ACTIVITY)
                    MyUtils.moneyAmount += money;
                else
                    MyUtils.moneyAmount -= money;
                startMainActivity();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    //OnClick event for every number button (0-9)
    public void onClickNumber(View v) {
        Button button = (Button) v;
        if(currentValue.length() == 1 && currentValue.contains(BASE_VALUE))
            currentValue = "";
        currentValue += button.getText().toString().trim();
        updateTextView();
    }

    private void startMainActivity() {
        Intent intent = new Intent(AddExpenses.this, MainActivity.class);
        startActivity(intent);
    }

    private void updateTextView() {
        moneyAmountTV.setText(currentValue);
    }
}
