package com.example.android.personalfinance_v01;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.android.personalfinance_v01.MyClasses.Budget;
import com.example.android.personalfinance_v01.MyClasses.Goal;
import com.example.android.personalfinance_v01.MyClasses.MyUtils;

import java.util.Objects;

public class DetailedGoalActivity extends AppCompatActivity {

    private Goal currentGoal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_goal);

        currentGoal = (Goal) Objects.requireNonNull(getIntent().getExtras()).getSerializable("goal");

        TextView textView = findViewById(R.id.thisTv);
        textView.setText(MyUtils.fromDoubleListToString(currentGoal.getAddedAmounts()) + " " + MyUtils.fromLongListToString(currentGoal.getAddedAmountsDates()));
    }
}
