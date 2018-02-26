package com.example.android.personalfinance_v01;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.personalfinance_v01.MyClasses.Category;
import com.example.android.personalfinance_v01.MyClasses.CategoryAdapter;
import com.example.android.personalfinance_v01.MyClasses.MyUtils;

public class AddExpenses extends AppCompatActivity {

    private static final String BASE_VALUE = "0";

    String currentValue = BASE_VALUE;

    Toolbar toolbar;
    Spinner spinner;
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
        signTV = findViewById(R.id.signTv);
        if(isIncomeActivity())
            signTV.setText(MyUtils.PLUS_SIGN);

        //Spinner
        spinner = findViewById(R.id.categorySpinner);
        CategoryAdapter categoryAdapter;
        if(isIncomeActivity()) {
            categoryAdapter = new CategoryAdapter(this, MyUtils.getIncomeCategories());
        } else {
            categoryAdapter = new CategoryAdapter(this, MyUtils.getExpenseCategories());
        }
        spinner.setAdapter(categoryAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Category currentCategory = (Category) adapterView.getItemAtPosition(i);
                Toast.makeText(AddExpenses.this, currentCategory.getName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

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
                if(isIncomeActivity())
                    MyUtils.moneyAmount += money;
                else
                    MyUtils.moneyAmount -= money;
                MyUtils.startActivity(AddExpenses.this, MainActivity.class);
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

    /**
     * @return TRUE for INCOME Activity and FALSE for EXPENSE Activity
     */
    private boolean isIncomeActivity() {
        return getIntent().getExtras().getInt(MyUtils.INTENT_KEY) == MyUtils.INCOME_ACTIVITY;
    }

    private void updateTextView() {
        moneyAmountTV.setText(currentValue);
    }
}
