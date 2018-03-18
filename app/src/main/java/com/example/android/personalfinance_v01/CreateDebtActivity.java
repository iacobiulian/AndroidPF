package com.example.android.personalfinance_v01;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.personalfinance_v01.DataPersistance.DatabaseHelper;
import com.example.android.personalfinance_v01.MyClasses.Debt;
import com.example.android.personalfinance_v01.MyClasses.MyUtils;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class CreateDebtActivity extends AppCompatActivity {

    Toolbar toolbar;

    Spinner spinner;
    EditText amountEt;
    EditText payeeEt;
    EditText startDateEt;
    EditText endDateEt;
    TextView payeeTv;

    int type;
    double amount;
    String payee;
    long startDate = MyUtils.getCurrentDateTime();
    long endDate = MyUtils.getCurrentDateTime();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_debt);

        //Toolbar
        toolbar = findViewById(R.id.addDebtToolbar);
        setSupportActionBar(toolbar);

        initViews();

        initSpinner();

        initDatePickers();
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
                insertDebtIntoDb();
                MyUtils.startActivity(CreateDebtActivity.this, DebtListActivity.class);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initViews() {
        spinner = findViewById(R.id.addDebtSpinner);
        amountEt = findViewById(R.id.addDebtAmountEt);
        payeeEt = findViewById(R.id.addDebtPayeeEt);
        startDateEt = findViewById(R.id.addDebtStartDateEt);
        endDateEt = findViewById(R.id.addDebtEndDateEt);
        payeeTv = findViewById(R.id.addDebtPayeeTv);
    }

    private void initSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.debt_type_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void initDatePickers() {
        View.OnClickListener showDatePicker = new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                Calendar cal = Calendar.getInstance();
                final int year = cal.get(Calendar.YEAR);
                final int month = cal.get(Calendar.MONTH);
                final int day = cal.get(Calendar.DAY_OF_MONTH);

                final DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        Calendar calendar = new GregorianCalendar(year, month, day);
                        long selectedTime = calendar.getTimeInMillis();

                        if(view == startDateEt) {
                            startDate = selectedTime;
                        } else if (view == endDateEt) {
                            endDate = selectedTime;
                        }

                        ((EditText) view).setText(MyUtils.formatDateWithoutTime(selectedTime));
                    }
                };

                DatePickerDialog datePickerDialog =  new DatePickerDialog(CreateDebtActivity.this, android.R.style.Theme_Holo_Dialog_MinWidth,
                        dateSetListener, year, month, day);
                datePickerDialog.show();
            }
        };

        startDateEt.setFocusable(false);
        startDateEt.setClickable(true);
        startDateEt.setText(MyUtils.formatDateWithoutTime(MyUtils.getCurrentDateTime()));
        startDateEt.setOnClickListener(showDatePicker);

        endDateEt.setFocusable(false);
        endDateEt.setClickable(true);
        endDateEt.setText(MyUtils.formatDateWithoutTime(MyUtils.getCurrentDateTime()));
        endDateEt.setOnClickListener(showDatePicker);
    }

    private Debt createDebt() {
        if(spinner.getSelectedItemPosition() == 0) {
            type = Debt.I_LEND;
        } else {
            type = Debt.I_BORROW;
        }
        payee = payeeEt.getText().toString().trim();
        amount = MyUtils.getDoubleFromEditText(amountEt);

        return new Debt(type, payee, amount, startDate, endDate);
    }

    private void insertDebtIntoDb() {
        Debt newDebt = createDebt();

        if(newDebt.isValid()) {
            DatabaseHelper databaseHelper =  new DatabaseHelper(CreateDebtActivity.this);
            boolean inserted = databaseHelper.addDebtData(newDebt);

            if (inserted)
                Toast.makeText(this, R.string.debt_added, Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this, "INSERTION FAILED", Toast.LENGTH_SHORT).show();

        } else {
            MyUtils.makeToast(this, getResources().getString(R.string.error_debt));
        }
    }
}
