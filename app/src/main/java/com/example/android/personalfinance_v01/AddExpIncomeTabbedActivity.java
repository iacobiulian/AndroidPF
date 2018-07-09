package com.example.android.personalfinance_v01;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.android.personalfinance_v01.CustomAdapters.ExpenseIncomePagerAdapter;
import com.example.android.personalfinance_v01.DataPersistance.DatabaseHelper;
import com.example.android.personalfinance_v01.Fragments.AddExpenseFragment;
import com.example.android.personalfinance_v01.Fragments.AddIncomeFragment;
import com.example.android.personalfinance_v01.Fragments.AddTransferFragment;
import com.example.android.personalfinance_v01.MyClasses.BalanceAccount;
import com.example.android.personalfinance_v01.MyClasses.Budget;
import com.example.android.personalfinance_v01.MyClasses.Category;
import com.example.android.personalfinance_v01.MyClasses.ExpenseIncome;
import com.example.android.personalfinance_v01.MyClasses.MyUtils;
import com.example.android.personalfinance_v01.MyClasses.Transfer;

import java.util.Objects;

public class AddExpIncomeTabbedActivity extends AppCompatActivity {

    public static final String DONE_CODE = "doneCode";
    public static final String NOTIF_BUDGET = "notificationBudget";

    private static final int FRAGMENT_EXPENSE = 0;
    private static final int FRAGMENT_INCOME = 1;
    private static final int FRAGMENT_TRANSFER = 2;
    public static int doneCode = 0;
    private Budget notificationBudget = null;

    private AddExpenseFragment expenseFragment;
    private AddIncomeFragment incomeFragment;
    private AddTransferFragment transferFragment;

    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_exp_income_tabbed);

        MyUtils.getBudgetsFromDatabase(AddExpIncomeTabbedActivity.this);

        //Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarAddExpIncTabbed);
        setSupportActionBar(toolbar);

        viewPager = findViewById(R.id.addExpIncViewPager);
        initViewPager(viewPager);

        TabLayout tabLayout = findViewById(R.id.addExpIncTabLayout);
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
                switch (viewPager.getCurrentItem()) {
                    case FRAGMENT_EXPENSE:
                        ExpenseIncome expense = expenseFragment.getExpense();
                        if (expense.getAmount() > 0) {
                            insertExpenseIncomeIntoDb(expense);
                            subtractMoneyFromAccount(MyUtils.getSelectedAccount(), expense.getAmount());
                            subtractMoneyFromBudgets(expense);
                        } else {
                            doneCode = MainActivity.ERROR_INPUT_ZERO;
                        }
                        break;
                    case FRAGMENT_INCOME:
                        ExpenseIncome income = incomeFragment.getIncome();
                        if (income.getAmount() > 0) {
                            insertExpenseIncomeIntoDb(incomeFragment.getIncome());
                            addMoneyToAccount(MyUtils.getSelectedAccount(), income.getAmount());
                        } else {
                            doneCode = MainActivity.ERROR_INPUT_ZERO;
                        }
                        break;
                    case FRAGMENT_TRANSFER:
                        Transfer transfer = transferFragment.getTransfer();
                        if (transfer.getAmount() > 0) {
                            insertTransferIntoDb(transfer);
                            subtractMoneyFromAccount(transfer.getFromAccount(), transfer.getAmount());
                            addMoneyToAccount(transfer.getToAccount(), transfer.getAmount());
                        } else {
                            doneCode = MainActivity.ERROR_INPUT_ZERO;
                        }
                        break;
                }
                Bundle bundle = new Bundle();
                bundle.putInt(DONE_CODE, doneCode);
                bundle.putSerializable(NOTIF_BUDGET, notificationBudget);
                MyUtils.startActivityWithBundle(AddExpIncomeTabbedActivity.this, MainActivity.class, bundle);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initViewPager(ViewPager viewPager) {
        expenseFragment = new AddExpenseFragment();
        incomeFragment = new AddIncomeFragment();

        ExpenseIncomePagerAdapter adapter = new ExpenseIncomePagerAdapter(getSupportFragmentManager());
        adapter.addFragment(expenseFragment, "Add expense");
        adapter.addFragment(incomeFragment, "Add income");

        if (MyUtils.accountList.size() > 1) {
            transferFragment = new AddTransferFragment();
            adapter.addFragment(transferFragment, "Add transfer");
        }

        viewPager.setAdapter(adapter);

        if (isTransferActivity()) {
            viewPager.setCurrentItem(FRAGMENT_TRANSFER);
            setTitle("Add transfer");
            return;
        }

        if (isIncomeActivity()) {
            viewPager.setCurrentItem(FRAGMENT_INCOME, false);
            setTitle("Add income");
        } else {
            viewPager.setCurrentItem(FRAGMENT_EXPENSE, false);
            setTitle("Add expense");
        }
    }

    /**
     * @return TRUE for INCOME Activity and FALSE for EXPENSE Activity
     */
    private boolean isIncomeActivity() {
        return Objects.requireNonNull(getIntent().getExtras()).getInt(MyUtils.INTENT_KEY) == ExpenseIncome.TYPE_INCOME;
    }

    private boolean isTransferActivity() {
        return Objects.requireNonNull(getIntent().getExtras()).getInt(MyUtils.INTENT_KEY) == MainActivity.TYPE_TRANSFER;
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
     * Adds the new expense/income to the database
     */
    private void insertTransferIntoDb(Transfer transfer) {
        DatabaseHelper databaseHelper = new DatabaseHelper(AddExpIncomeTabbedActivity.this);
        boolean inserted = databaseHelper.addTransferData(transfer);

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
    private void subtractMoneyFromAccount(BalanceAccount balanceAccount, double amount) {
        DatabaseHelper databaseHelper = new DatabaseHelper(AddExpIncomeTabbedActivity.this);
        int id = databaseHelper.getAccountID(balanceAccount);

        double newBalanceAmount;

        newBalanceAmount = balanceAccount.getBalance() - amount;
        balanceAccount.substractFromBalance(amount);

        databaseHelper.updateAccountBalanceAmount(id, newBalanceAmount);
    }

    private void subtractMoneyFromBudgets(ExpenseIncome expense) {
        Category category = expense.getCategory();

        for (Budget item : MyUtils.budgetList) {
            if (item.getCategory().equals(category)) {
                double newAmount = item.getCurrentAmount() + expense.getAmount();
                MyUtils.modifyBudgetCurrentAmount(AddExpIncomeTabbedActivity.this, item, newAmount);
                if (item.isExceeded()) {
                    makeBudgetNotification(item, R.string.notificationAlreadyExceededBudgetTitle, R.string.notificationAlreadyExceededBudgetBody);
                    doneCode = MainActivity.BUDGET_ALREADY_EXCEEDED;
                    notificationBudget = item;
                } else if (item.getTotalAmount() < newAmount) {
                    makeBudgetNotification(item, R.string.notificationExceededBudgetTitle, R.string.notificationExceededBudgetBody);
                    doneCode = MainActivity.BUDGET_EXCEEDED;
                    notificationBudget = item;
                } else if (!item.isExceededHalf() && newAmount > item.getTotalAmount() / 2) {
                    makeBudgetNotification(item, R.string.notificationExceededHalfBudgetTitle, R.string.notificationExceededHalfBudgetBody);
                    doneCode = MainActivity.BUDGET_HALF_SPENT;
                    notificationBudget = item;
                }
            }
        }
    }

    private void makeBudgetNotification(Budget budget, int titleStringId, int bodyStringId) {
        Intent intent = new Intent(getApplicationContext(), DetailedBudgetTabbedActivity.class);
        intent.putExtra("budget", budget);
        String title = getResources().getString(titleStringId);
        String body = String.format(getResources().getString(bodyStringId),
                budget.getTypeString(), budget.getCategory().getName());
        MyUtils.createNotification(AddExpIncomeTabbedActivity.this, intent, title, body, budget.getCategory().getIconID());
    }
}













