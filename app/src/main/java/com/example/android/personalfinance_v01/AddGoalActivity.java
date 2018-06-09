package com.example.android.personalfinance_v01;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.personalfinance_v01.DataPersistance.DatabaseHelper;
import com.example.android.personalfinance_v01.MyClasses.Goal;
import com.example.android.personalfinance_v01.MyClasses.MyUtils;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class AddGoalActivity extends AppCompatActivity {

    Toolbar toolbar;

    private EditText nameEt;
    private EditText targetAmountEt;
    private EditText savedAlreadyEt;
    private EditText targetDateEt;

    private long targetDate = MyUtils.getCurrentDateTime();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_goal);

        //Toolbar
        toolbar = findViewById(R.id.addGoalToolbar);
        setSupportActionBar(toolbar);

        initViews();

        initDatePicker();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
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
                insertGoalIntoDb();
                MyUtils.startActivity(AddGoalActivity.this, GoalListActivity.class);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initViews() {
        nameEt = findViewById(R.id.addGoalNameEt);
        targetAmountEt = findViewById(R.id.addGoalTargetAmountEt);
        savedAlreadyEt = findViewById(R.id.addGoalSavedAlreadyEt);
        savedAlreadyEt.setText("0");
        savedAlreadyEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(hasFocus) {
                    savedAlreadyEt.setText("");
                } else {
                    if(TextUtils.isEmpty(savedAlreadyEt.getText().toString())) {
                        savedAlreadyEt.setText("0");
                    }
                }
            }
        });
        targetDateEt = findViewById(R.id.addGoalTargetDateEt);
    }

    private void initDatePicker() {
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

                        if (view == targetDateEt) {
                            targetDate = selectedTime;
                        }

                        ((EditText) view).setText(MyUtils.formatDateWithoutTime(selectedTime));
                    }
                };

                DatePickerDialog datePickerDialog = new DatePickerDialog(AddGoalActivity.this, android.R.style.Theme_Holo_Dialog_MinWidth,
                        dateSetListener, year, month, day);
                datePickerDialog.show();
            }
        };

        targetDateEt.setFocusable(false);
        targetDateEt.setClickable(true);
        targetDateEt.setText(MyUtils.formatDateWithoutTime(MyUtils.getCurrentDateTime()));
        targetDateEt.setOnClickListener(showDatePicker);

    }

    private Goal createGoal() {
        String name = nameEt.getText().toString();
        double targetAmount = MyUtils.getDoubleFromEditText(targetAmountEt);
        double savedAlready = MyUtils.getDoubleFromEditText(savedAlreadyEt);
        if (savedAlready < 0.0)
            savedAlready = 0.0;

        return new Goal(name, targetAmount, savedAlready, targetDate);
    }

    private void insertGoalIntoDb() {
        Goal newGoal = createGoal();

        if (newGoal.isValid()) {
            DatabaseHelper databaseHelper = new DatabaseHelper(AddGoalActivity.this);
            boolean inserted = databaseHelper.addGoalData(newGoal);

            if (inserted)
                Toast.makeText(this, R.string.goalAdded, Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this, "INSERTION FAILED", Toast.LENGTH_SHORT).show();

        } else {
            MyUtils.makeToast(this, getResources().getString(R.string.errorGoal));
        }
    }
}













