package com.example.android.personalfinance_v01;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.example.android.personalfinance_v01.MyClasses.BalanceAccount;
import com.example.android.personalfinance_v01.MyClasses.MyUtils;

import java.util.Arrays;

public class CreateAccountActivity extends AppCompatActivity {

    //Becomes true if we are editing an existing account instead of creating a new one
    boolean isEditActivity = false;
    //Index in the account list of the account we are editing
    int indexForEdit = -1;

    EditText nameEt;
    EditText balanceEt;
    Spinner spinner;

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

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

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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

        switch(id) {
            case R.id.actionDone:
                if(!isEditActivity) {
                    createBalanceAccount();
                } else {
                    editBalanceAccount(MyUtils.accountList.get(indexForEdit));
                }
                MyUtils.startActivity(CreateAccountActivity.this, AccountListActivity.class);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void populateEditTexts() {
        if(getIntent().getExtras() != null) {
            isEditActivity = true;
            indexForEdit = getIntent().getExtras().getInt("indexForEdit");

            getSupportActionBar().setTitle(R.string.editAccount);

            BalanceAccount balanceAccount = (BalanceAccount) getIntent().getExtras().getSerializable("accountForEdit");
            nameEt.setText(balanceAccount.getName());
            balanceEt.setText(balanceAccount.getBalance() + "");
            spinner.setSelection(Arrays.asList(getResources().getStringArray(R.array.currenciesArr)).indexOf(balanceAccount.getCurrency()));
        }
    }

    private void createBalanceAccount() {
        String name = nameEt.getText().toString().trim();

        double balance = -1;
        try{
            balance = Double.valueOf(balanceEt.getText().toString());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        String currency = spinner.getSelectedItem().toString();

        if(!TextUtils.isEmpty(name) && balance != -1) {
            BalanceAccount balanceAccount = new BalanceAccount(name, balance, currency);
            if(MyUtils.accountList.isEmpty())
                balanceAccount.setSelected(true);
            MyUtils.accountList.add(balanceAccount);
        }
    }

    private void editBalanceAccount(BalanceAccount accountForEdit) {
        String name = nameEt.getText().toString().trim();

        double balance = -1;
        try{
            balance = Double.valueOf(balanceEt.getText().toString());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        String currency = spinner.getSelectedItem().toString();

        if(!TextUtils.isEmpty(name) && balance != -1) {
            accountForEdit.setName(name);
            accountForEdit.setBalance(balance);
            accountForEdit.setCurrency(currency);
        }
    }
}
