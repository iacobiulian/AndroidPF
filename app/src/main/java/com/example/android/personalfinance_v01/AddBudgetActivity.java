package com.example.android.personalfinance_v01;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.android.personalfinance_v01.CustomAdapters.CategoryAdapter;
import com.example.android.personalfinance_v01.MyClasses.Budget;
import com.example.android.personalfinance_v01.MyClasses.Category;
import com.example.android.personalfinance_v01.MyClasses.MyUtils;

public class AddBudgetActivity extends AppCompatActivity {

    Spinner typeSpinner;
    Spinner categorySpinner;
    Category currentCategory;
    private EditText amountEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_budget);

        amountEt = findViewById(R.id.addBudgetAmountEt);

        initToolbar();
        initCategorySpinner();
        initTypeSpinner();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_done, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.actionDone:
                //insertBudgetIntoDb();
                createBudget();
                MyUtils.startActivity(AddBudgetActivity.this, BudgetListActivity.class);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.addBudgetToolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initTypeSpinner() {
        typeSpinner = findViewById(R.id.addBudgetPeriodSpinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.budget_type_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(adapter);
    }

    private void initCategorySpinner() {
        categorySpinner = findViewById(R.id.addBudgetCategorySpinner);

        CategoryAdapter categoryAdapter;

        categoryAdapter = new CategoryAdapter(AddBudgetActivity.this, MyUtils.getExpenseCategories());
        categorySpinner.setAdapter(categoryAdapter);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                currentCategory = (Category) adapterView.getItemAtPosition(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private Budget createBudget() {
        int type = 0;
        switch (typeSpinner.getSelectedItemPosition()) {
            case 0:
                type = Budget.NONE;
                break;
            case 1:
                type = Budget.WEEKLY;
                break;
            case 2:
                type = Budget.BI_WEEKLY;
                break;
            case 3:
                type = Budget.MONTHLY;
                break;
            case 4:
                type = Budget.YEARLY;
                break;
        }

        double amount = MyUtils.getDoubleFromEditText(amountEt);

        MyUtils.budgetList.add(new Budget(type, currentCategory, amount));

        return null;
    }
}
