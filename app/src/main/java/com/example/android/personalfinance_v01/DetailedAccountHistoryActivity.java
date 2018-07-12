package com.example.android.personalfinance_v01;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.AdapterView;

import com.example.android.personalfinance_v01.CustomAdapters.ExpenseIncomePagerAdapter;
import com.example.android.personalfinance_v01.DataPersistance.DatabaseHelper;
import com.example.android.personalfinance_v01.Fragments.AccountDetailsExpFragment;
import com.example.android.personalfinance_v01.Fragments.AccountDetailsIncFragment;
import com.example.android.personalfinance_v01.Fragments.AccountDetailsTransferFragment;
import com.example.android.personalfinance_v01.MyClasses.BalanceAccount;
import com.example.android.personalfinance_v01.MyClasses.Budget;
import com.example.android.personalfinance_v01.MyClasses.ExpenseIncome;
import com.example.android.personalfinance_v01.MyClasses.MyUtils;
import com.example.android.personalfinance_v01.MyClasses.Transfer;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class DetailedAccountHistoryActivity extends AppCompatActivity {

    private static final int FRAGMENT_EXPENSE = 0;
    private static final int FRAGMENT_INCOME = 1;
    private static final int FRAGMENT_TRANSFER = 2;
    BalanceAccount currentAccount;
    private ViewPager viewPager;
    private AccountDetailsExpFragment expenseHistoryFragment;
    private AccountDetailsIncFragment incomeHistoryFragment;
    private AccountDetailsTransferFragment transferHistoryFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_account_history);

        currentAccount = (BalanceAccount) Objects.requireNonNull(getIntent().getExtras()).getSerializable("acc");
        if (currentAccount == null) {
            return;
        }

        setTitle(currentAccount.getName());

        expenseHistoryFragment = new AccountDetailsExpFragment();
        incomeHistoryFragment = new AccountDetailsIncFragment();
        transferHistoryFragment = new AccountDetailsTransferFragment();

        initToolbar();

        viewPager = findViewById(R.id.detailedAccountHistoryViewPager);
        initViewPager(viewPager);

        TabLayout tabLayout = findViewById(R.id.detailedAccountHistoryTabLayout);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.detailedAccountHistoryToolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initViewPager(ViewPager viewPager) {
        ExpenseIncomePagerAdapter adapter = new ExpenseIncomePagerAdapter(getSupportFragmentManager());
        adapter.addFragment(expenseHistoryFragment, "Expense History");
        adapter.addFragment(incomeHistoryFragment, "Income History");
        adapter.addFragment(transferHistoryFragment, "Transfer history");
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (!expenseHistoryFragment.isAdded()) {
                    return;
                }

                //updateLists();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    public void updateLists() {
        if (viewPager.getCurrentItem() == FRAGMENT_EXPENSE) {
            updateExpenseList();
        } else if (viewPager.getCurrentItem() == FRAGMENT_INCOME) {
            updateIncomeList();
        } else if (viewPager.getCurrentItem() == FRAGMENT_TRANSFER) {
            updateTransferList();
        }
    }

    public void updateExpenseList() {
        expenseHistoryFragment.updateListView(filterExpenseList(MyUtils.expenseIncomeList));
    }

    public void updateIncomeList() {
        incomeHistoryFragment.updateListView(filterIncomeList(MyUtils.expenseIncomeList));
    }

    public void updateTransferList() {
        transferHistoryFragment.updateListView(filterTransfersList(MyUtils.transferList));
    }

    private ArrayList<Transfer> filterTransfersList(ArrayList<Transfer> list) {
        ArrayList<Transfer> filteredList = new ArrayList<>();
        for (Transfer item : list) {
            if (item.getFromAccount().equals(currentAccount) || item.getToAccount().equals(currentAccount)) {
                filteredList.add(item);
            }
        }

        return filteredList;
    }

    private ArrayList<ExpenseIncome> filterExpenseList(ArrayList<ExpenseIncome> list) {
        ArrayList<ExpenseIncome> filteredList = new ArrayList<>();
        for (ExpenseIncome item : list) {
            if (item.getType() == ExpenseIncome.TYPE_EXPENSE && item.getAccount().equals(currentAccount)) {
                filteredList.add(item);
            }
        }

        return filteredList;
    }

    private ArrayList<ExpenseIncome> filterIncomeList(ArrayList<ExpenseIncome> list) {
        ArrayList<ExpenseIncome> filteredList = new ArrayList<>();
        for (ExpenseIncome item : list) {
            if (item.getType() == ExpenseIncome.TYPE_INCOME && item.getAccount().equals(currentAccount)) {
                filteredList.add(item);
            }
        }

        return filteredList;
    }

    public void deleteExpenseIncome(AdapterView<?> adapterView, int index) {
        ExpenseIncome expenseIncome = (ExpenseIncome) adapterView.getItemAtPosition(index);
        DatabaseHelper databaseHelper = new DatabaseHelper(DetailedAccountHistoryActivity.this);

        databaseHelper.deleteExpenseIncome(databaseHelper.getExpenseIncomeID(expenseIncome));
        updateAccount(expenseIncome, databaseHelper);
        updateBudgets(expenseIncome, databaseHelper);

        MyUtils.getExpenseIncomeFromDatabase(DetailedAccountHistoryActivity.this);

        updateLists();
    }

    private void updateAccount(ExpenseIncome expenseIncome, DatabaseHelper databaseHelper) {
        double newBalance = currentAccount.getBalance();
        if (expenseIncome.getType() == ExpenseIncome.TYPE_INCOME) {
            newBalance -= expenseIncome.getAmount();
        } else {
            newBalance += expenseIncome.getAmount();
        }

        databaseHelper.updateAccountBalanceAmount(databaseHelper.getAccountID(currentAccount), newBalance);
        currentAccount.setBalance(newBalance);
    }

    private void updateAccounts(BalanceAccount fromAcc, BalanceAccount toAcc, double transferAmount) {
        DatabaseHelper databaseHelper = new DatabaseHelper(DetailedAccountHistoryActivity.this);
        double newFromAccBalance = fromAcc.getBalance() + transferAmount;
        double newToAccBalance = toAcc.getBalance() - transferAmount;

        databaseHelper.updateAccountBalanceAmount(databaseHelper.getAccountID(fromAcc), newFromAccBalance);
        databaseHelper.updateAccountBalanceAmount(databaseHelper.getAccountID(toAcc), newToAccBalance);
    }

    private void updateBudgets(ExpenseIncome expenseIncome, DatabaseHelper databaseHelper) {

        Date date = new Date(expenseIncome.getDate());
        ArrayList<Budget> budgets = new ArrayList<>();

        for (Budget budget : MyUtils.budgetList) {
            if (expenseIncome.getCategory().equals(budget.getCategory())) {
                if (budget.isDateInArea(date)) {
                    budgets.add(budget);
                }
            }
        }

        for (Budget budget : budgets) {
            double newBalance = budget.getCurrentAmount();
            newBalance -= expenseIncome.getAmount();
            databaseHelper.updateBudgetCurrentAmount(databaseHelper.getBudgetId(budget), newBalance);
        }
    }

    public void deleteTransfer(AdapterView<?> adapterView, int i) {
        Transfer transfer = (Transfer) adapterView.getItemAtPosition(i);
        DatabaseHelper databaseHelper = new DatabaseHelper(DetailedAccountHistoryActivity.this);

        databaseHelper.deleteTransfer(databaseHelper.getTransferID(transfer));
        updateAccounts(transfer.getFromAccount(), transfer.getToAccount(), transfer.getAmount());

        MyUtils.getTransfersFromDatabase(DetailedAccountHistoryActivity.this);

        updateLists();
    }
}























