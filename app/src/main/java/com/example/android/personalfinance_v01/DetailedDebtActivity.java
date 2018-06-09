package com.example.android.personalfinance_v01;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.android.personalfinance_v01.MyClasses.Debt;
import com.example.android.personalfinance_v01.MyClasses.Goal;
import com.example.android.personalfinance_v01.MyClasses.MyUtils;

import java.util.Objects;

public class DetailedDebtActivity extends AppCompatActivity {

    Debt currentDebt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_debt);

        currentDebt = (Debt) Objects.requireNonNull(getIntent().getExtras()).getSerializable("debt");

        TextView textView = findViewById(R.id.thisTv);
        if(currentDebt.getAddedAmounts().isEmpty()) {
            textView.setText("addedamountsempty");
        } else {
            textView.setText(MyUtils.fromDoubleListToString(currentDebt.getAddedAmounts()) + " " + MyUtils.fromLongListToString(currentDebt.getAddedAmountsDates()));
        }
    }
}
