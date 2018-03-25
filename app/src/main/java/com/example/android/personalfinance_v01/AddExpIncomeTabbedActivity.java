package com.example.android.personalfinance_v01;

import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.android.personalfinance_v01.CustomAdapters.AddExpIncomePagerAdapter;
import com.example.android.personalfinance_v01.DataPersistance.DatabaseHelper;
import com.example.android.personalfinance_v01.Fragments.AddExpenseFragment;
import com.example.android.personalfinance_v01.Fragments.AddIncomeFragment;
import com.example.android.personalfinance_v01.MyClasses.BalanceAccount;
import com.example.android.personalfinance_v01.MyClasses.ExpenseIncome;
import com.example.android.personalfinance_v01.MyClasses.MyUtils;

public class AddExpIncomeTabbedActivity extends AppCompatActivity {

    private static final int FRAGMENT_EXPENSE = 0;
    private static final int FRAGMENT_INCOME = 1;

    AddExpenseFragment expenseFragment;
    AddIncomeFragment incomeFragment;

    ViewPager viewPager;

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_exp_income_tabbed);

        //Toolbar
        toolbar = findViewById(R.id.toolbarAddExpIncTabbed);
        setSupportActionBar(toolbar);

        viewPager = findViewById(R.id.addExpIncViewPager);
        initViewPager(viewPager);

        TabLayout tabLayout =  findViewById(R.id.addExpIncTabLayout);
        tabLayout.setupWithViewPager(viewPager);

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
                if(viewPager.getCurrentItem() == FRAGMENT_EXPENSE) {
                    ExpenseIncome expense =  expenseFragment.getExpense();
                    insertExpenseIncomeIntoDb(expense);
                    substractMoneyFromAccount(MyUtils.getSelectedAccount(), expense.getAmount());
                } else {
                    ExpenseIncome income = incomeFragment.getIncome();
                    insertExpenseIncomeIntoDb(incomeFragment.getIncome());
                    addMoneyToAccount(MyUtils.getSelectedAccount(), income.getAmount());
                }
                MyUtils.startActivity(AddExpIncomeTabbedActivity.this, MainActivity.class);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initViewPager(ViewPager viewPager) {
        expenseFragment = new AddExpenseFragment();
        incomeFragment =  new AddIncomeFragment();

        AddExpIncomePagerAdapter adapter =  new AddExpIncomePagerAdapter(getSupportFragmentManager());
        adapter.addFragment(expenseFragment, "Add expense");
        adapter.addFragment(incomeFragment, "Add income");
        viewPager.setAdapter(adapter);

        if(isIncomeActivity()) {
            viewPager.setCurrentItem(1, false);
        } else {
            viewPager.setCurrentItem(0, false);
        }
    }

    /**
     * @return TRUE for INCOME Activity and FALSE for EXPENSE Activity
     */
    private boolean isIncomeActivity() {
        return getIntent().getExtras().getInt(MyUtils.INTENT_KEY) == ExpenseIncome.TYPE_INCOME;
    }

        /**
     * Adds the new expense/income to the database
     */
    private void insertExpenseIncomeIntoDb(ExpenseIncome expenseIncome) {
        DatabaseHelper databaseHelper = new DatabaseHelper(AddExpIncomeTabbedActivity.this);
        boolean inserted = databaseHelper.addExpenseIncomeData(expenseIncome);

        if (!inserted)
            Toast.makeText(this, R.string.record_created, Toast.LENGTH_SHORT).show();
    }

    /**
     * @param amount of money added
     */
    private void addMoneyToAccount(BalanceAccount balanceAccount, double amount) {
        DatabaseHelper databaseHelper = new DatabaseHelper(AddExpIncomeTabbedActivity.this);
        int id = databaseHelper.getAccountID(balanceAccount);

        double newBalanceAmount;

        newBalanceAmount = balanceAccount.getBalance() + amount;
        balanceAccount.addToBalance(amount);

        databaseHelper.updateAccountBalanceAmount(id, newBalanceAmount);
    }

    /**
     * @param amount of money substracted
     */
    private void substractMoneyFromAccount(BalanceAccount balanceAccount, double amount) {
        DatabaseHelper databaseHelper = new DatabaseHelper(AddExpIncomeTabbedActivity.this);
        int id = databaseHelper.getAccountID(balanceAccount);

        double newBalanceAmount;

        newBalanceAmount = balanceAccount.getBalance() - amount;
        balanceAccount.substractFromBalance(amount);

        databaseHelper.updateAccountBalanceAmount(id, newBalanceAmount);
    }
}
