package com.example.android.personalfinance_v01;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.personalfinance_v01.CustomAdapters.CategoryAdapter;
import com.example.android.personalfinance_v01.DataPersistance.DatabaseHelper;
import com.example.android.personalfinance_v01.MyClasses.BalanceAccount;
import com.example.android.personalfinance_v01.MyClasses.Category;
import com.example.android.personalfinance_v01.MyClasses.ExpenseIncome;
import com.example.android.personalfinance_v01.MyClasses.MyUtils;

import java.util.Calendar;

public class AddExpensesActivity extends AppCompatActivity {

    static final String BASE_VALUE = "0";
    String currentValue = BASE_VALUE;

    Category currentCategory;

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
        if (isIncomeActivity()) {
            signTV.setText(MyUtils.PLUS_SIGN);
        }

        //Spinner
        initSpinner();

        //Special buttons
        initSpecialButtons();

        //Back button on the toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_done, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.actionDone:
                insertExpenseIncomeIntoDb();
                MyUtils.startActivity(AddExpensesActivity.this, MainActivity.class);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initSpinner() {
        spinner = findViewById(R.id.categorySpinner);

        CategoryAdapter categoryAdapter;
        if (isIncomeActivity()) {
            categoryAdapter = new CategoryAdapter(this, MyUtils.getIncomeCategories());
        } else {
            categoryAdapter = new CategoryAdapter(this, MyUtils.getExpenseCategories());
        }
        spinner.setAdapter(categoryAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                currentCategory = (Category) adapterView.getItemAtPosition(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void initSpecialButtons() {
        delBtn = findViewById(R.id.delBtn);
        delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Deletes the last character
                if (currentValue.length() == 1 && !currentValue.contains(BASE_VALUE))
                    currentValue = BASE_VALUE;
                else if (currentValue.length() > 1)
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
                if (!currentValue.contains(".")) {
                    currentValue += ".";
                    updateTextView();
                }
            }
        });
    }

    //OnClick event for every number button (0-9)
    public void onClickNumber(View v) {
        Button button = (Button) v;
        if (currentValue.length() == 1 && currentValue.contains(BASE_VALUE))
            currentValue = "";
        currentValue += button.getText().toString().trim();
        updateTextView();
    }

    /**
     * @return TRUE for INCOME Activity and FALSE for EXPENSE Activity
     */
    private boolean isIncomeActivity() {
        return getIntent().getExtras().getInt(MyUtils.INTENT_KEY) == ExpenseIncome.TYPE_INCOME;
    }

    private void updateTextView() {
        moneyAmountTV.setText(currentValue);
    }

    /**
     * @return new expense or income based on user inputs.
     */
    private ExpenseIncome createExpenseOrIncome() {
        double money = 0.0;
        try {
            money = Double.valueOf(moneyAmountTV.getText().toString());
        } catch (NumberFormatException e) {
            //This should never happen
            e.printStackTrace();
        }

        //Update the account we are substracting/adding money from
        addOrSubstractMoneyFromAccount(MyUtils.getSelectedAccount(), money);

        int type = isIncomeActivity() ? ExpenseIncome.TYPE_INCOME : ExpenseIncome.TYPE_EXPENSE;
        return new ExpenseIncome(money, type, currentCategory, getCurrentDateTime(), MyUtils.getSelectedAccount());
    }

    /**
     * Adds the new expense/income to the database
     */
    private void insertExpenseIncomeIntoDb() {
        ExpenseIncome expenseIncome = createExpenseOrIncome();

        DatabaseHelper databaseHelper = new DatabaseHelper(AddExpensesActivity.this);
        boolean inserted = databaseHelper.addExpenseIncomeData(expenseIncome);

        if (!inserted)
            Toast.makeText(this, R.string.record_created, Toast.LENGTH_SHORT).show();
    }

    /**
     * @param amount of money added or substracted
     */
    private void addOrSubstractMoneyFromAccount(BalanceAccount balanceAccount, double amount) {
        DatabaseHelper databaseHelper = new DatabaseHelper(AddExpensesActivity.this);
        int id = databaseHelper.getAccountID(balanceAccount);

        double newBalanceAmount;

        if (isIncomeActivity()) {
            newBalanceAmount = balanceAccount.getBalance() + amount;
            balanceAccount.addToBalance(amount);
        } else {
            newBalanceAmount = balanceAccount.getBalance() - amount;
            balanceAccount.substractFromBalance(amount);
        }

        databaseHelper.updateAccountBalanceAmount(id, newBalanceAmount);
    }

    /**
     * @return current unix time
     */
    private long getCurrentDateTime() {
        return Calendar.getInstance().getTime().getTime();
    }
}
