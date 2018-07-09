package com.example.android.personalfinance_v01;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.personalfinance_v01.DataPersistance.DatabaseHelper;
import com.example.android.personalfinance_v01.MyClasses.BalanceAccount;
import com.example.android.personalfinance_v01.MyClasses.MyUtils;

import java.util.Arrays;

public class AddAccountActivity extends AppCompatActivity {

    //Becomes true if we are editing an existing account instead of creating a new one
    boolean isEditActivity = false;
    //Index in the account list of the account we are editing
    int idForEdit = -1;

    EditText nameEt;
    EditText balanceEt;
    Spinner spinner;

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_account);

        nameEt = findViewById(R.id.createAccNameEt);
        balanceEt = findViewById(R.id.createAccBalanceEt);

        //Toolbar
        toolbar = findViewById(R.id.createAccToolbar);
        setSupportActionBar(toolbar);

        //Spinner
        spinner = findViewById(R.id.createAccSpinner);
        ArrayAdapter<CharSequence> currencyOptions = ArrayAdapter.createFromResource(this, R.array.currenciesArr, R.layout.support_simple_spinner_dropdown_item);
        currencyOptions.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(currencyOptions);

        populateEditTexts();

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
                if (!isEditActivity) {
                    insertAccountIntoDb();
                } else {
                    updateDb();
                }
                MyUtils.startActivity(AddAccountActivity.this, ListAccountActivity.class);
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * Executes only if we are editing an existing account
     */
    private void populateEditTexts() {
        if (getIntent().getExtras() != null) {
            isEditActivity = true;
            idForEdit = getIntent().getExtras().getInt("idForEdit");

            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(R.string.editAccount);
            }

            BalanceAccount balanceAccount = (BalanceAccount) getIntent().getExtras().getSerializable("accountForEdit");
            if (balanceAccount != null) {
                nameEt.setText(balanceAccount.getName());
                balanceEt.setText(balanceAccount.getBalanceString());
                spinner.setSelection(Arrays.asList(getResources().getStringArray(R.array.currenciesArr)).indexOf(balanceAccount.getCurrency()));
            }
        }
    }

    /**
     * @return a new BalanceAccount based on user input. null if user input is bad
     */
    private BalanceAccount createBalanceAccount() {
        String name = nameEt.getText().toString().trim();

        double balance = -1;
        try {
            balance = Double.valueOf(balanceEt.getText().toString());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        String currency = spinner.getSelectedItem().toString();

        //Input validation
        if (!TextUtils.isEmpty(name) && balance != -1) {
            return new BalanceAccount(name, balance, currency);
        }

        return null;
    }

    /**
     * IF WE CREATE A NEW ACCOUNT
     */
    private void insertAccountIntoDb() {
        BalanceAccount balanceAccount = createBalanceAccount();

        if (balanceAccount != null) {
            DatabaseHelper databaseHelper = new DatabaseHelper(AddAccountActivity.this);
            boolean inserted = databaseHelper.addAccountData(balanceAccount);

            if (inserted)
                Toast.makeText(this, R.string.account_added, Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this, R.string.error_account_create, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R.string.error_account_create, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * IF WE UPDATE AN EXISTING ACCOUNT
     */
    private void updateDb() {
        BalanceAccount balanceAccount = createBalanceAccount();

        if (balanceAccount != null) {
            DatabaseHelper databaseHelper = new DatabaseHelper(AddAccountActivity.this);
            databaseHelper.updateAccount(idForEdit, balanceAccount);
        } else {
            Toast.makeText(this, R.string.error_account_update, Toast.LENGTH_SHORT).show();
        }
    }
}
