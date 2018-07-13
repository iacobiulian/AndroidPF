package com.example.android.personalfinance_v01;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.personalfinance_v01.DataPersistance.DatabaseHelper;
import com.example.android.personalfinance_v01.MyClasses.BalanceAccount;
import com.example.android.personalfinance_v01.MyClasses.MyUtils;

import java.util.Arrays;
import java.util.List;

public class AddAccountActivity extends AppCompatActivity {

    public static final String ACCOUNT_FOR_EDIT = "accForEdit";

    public static final int ERROR_INPUT_NO_NAME = -1;
    public static final int ERROR_UNEXPECTED = -2;
    public static final int SUCCESS_CREATE_ACCOUNT = 10;
    public static final int SUCCESS_UPDATE_ACCOUNT = 20;

    //Becomes true if we are editing an existing account instead of creating a new one
    boolean isEditActivity = false;
    //Index in the account list of the account we are editing
    int idForEdit = -1;

    private static int endCode = 0;
    BalanceAccount accountForEdit = null;

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

        balanceEt.setText("0");
        balanceEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(hasFocus && balanceEt.getText().toString().equals("0")) {
                    balanceEt.setText("");
                } else {
                    if(TextUtils.isEmpty(balanceEt.getText().toString())) {
                        balanceEt.setText("0");
                    }
                }
            }
        });

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
                Bundle bundle = new Bundle();
                bundle.putInt(MyUtils.DONE_CODE, endCode);
                bundle.putSerializable(ACCOUNT_FOR_EDIT, accountForEdit);
                MyUtils.startActivityWithBundle(AddAccountActivity.this, ListAccountActivity.class, bundle);
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

            accountForEdit = (BalanceAccount) getIntent().getExtras().getSerializable("accountForEdit");
            if (accountForEdit != null) {
                nameEt.setText(accountForEdit.getName());
                balanceEt.setText(accountForEdit.getBalanceString());
                spinner.setSelection(Arrays.asList(getResources().getStringArray(R.array.currenciesArr)).indexOf(accountForEdit.getCurrency()));
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

            if (inserted) {
                endCode = SUCCESS_CREATE_ACCOUNT;
            }
            else
                endCode = ERROR_UNEXPECTED;
        } else {
            endCode = ERROR_INPUT_NO_NAME;
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
            endCode = SUCCESS_UPDATE_ACCOUNT;
        } else {
            endCode = ERROR_INPUT_NO_NAME;
        }
    }
}
