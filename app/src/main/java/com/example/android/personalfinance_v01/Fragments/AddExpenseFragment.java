package com.example.android.personalfinance_v01.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.android.personalfinance_v01.CustomAdapters.CategoryAdapter;
import com.example.android.personalfinance_v01.MyClasses.Category;
import com.example.android.personalfinance_v01.MyClasses.ExpenseIncome;
import com.example.android.personalfinance_v01.MyClasses.MyUtils;
import com.example.android.personalfinance_v01.R;

/**
 * Created by iacob on 18-Mar
 */

public class AddExpenseFragment extends Fragment {

    static final String BASE_VALUE = "0";
    String currentValue = BASE_VALUE;

    View mainView;

    Spinner spinner;
    Category currentCategory;

    //OnClick event for every number button (0-9)
    View.OnClickListener onClickNumber;

    TextView moneyAmountTV;
    Button delBtn;
    Button commaBtn;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_add_expense, container, false);

        //Money Amount TextView
        moneyAmountTV = mainView.findViewById(R.id.fragmentExpenseMoneyAmountTV);

        initSpecialButtons();

        initNumberButtons();

        initSpinner(mainView);

        return mainView;
    }

    private void initSpinner(View view) {
        spinner = view.findViewById(R.id.fragmentExpenseCategorySpinner);

        CategoryAdapter categoryAdapter;

        categoryAdapter = new CategoryAdapter(getContext(), MyUtils.getExpenseCategories());
        spinner.setAdapter(categoryAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                currentCategory = (Category) adapterView.getItemAtPosition(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void updateTextView() {
        moneyAmountTV.setText(currentValue);
    }

    private void initNumberButtons() {
        //OnClick event for every number button (0-9)
        onClickNumber = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button button = (Button) v;
                if (currentValue.length() == 1 && currentValue.contains(BASE_VALUE))
                    currentValue = "";
                currentValue += button.getText().toString().trim();
                updateTextView();
            }
        };

        Button btn0 = mainView.findViewById(R.id.fragmentExpenseBtn0);
        btn0.setOnClickListener(onClickNumber);

        Button btn1 = mainView.findViewById(R.id.fragmentExpenseBtn1);
        btn1.setOnClickListener(onClickNumber);

        Button btn2 = mainView.findViewById(R.id.fragmentExpenseBtn2);
        btn2.setOnClickListener(onClickNumber);

        Button btn3 = mainView.findViewById(R.id.fragmentExpenseBtn3);
        btn3.setOnClickListener(onClickNumber);

        Button btn4 = mainView.findViewById(R.id.fragmentExpenseBtn4);
        btn4.setOnClickListener(onClickNumber);

        Button btn5 = mainView.findViewById(R.id.fragmentExpenseBtn5);
        btn5.setOnClickListener(onClickNumber);

        Button btn6 = mainView.findViewById(R.id.fragmentExpenseBtn6);
        btn6.setOnClickListener(onClickNumber);

        Button btn7 = mainView.findViewById(R.id.fragmentExpenseBtn7);
        btn7.setOnClickListener(onClickNumber);

        Button btn8 = mainView.findViewById(R.id.fragmentExpenseBtn8);
        btn8.setOnClickListener(onClickNumber);

        Button btn9 = mainView.findViewById(R.id.fragmentExpenseBtn9);
        btn9.setOnClickListener(onClickNumber);
    }

    private void initSpecialButtons() {
        delBtn = mainView.findViewById(R.id.fragmentExpenseDelBtn);
        delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Deletes the last character
                if (currentValue.length() == 1 && !currentValue.contains(BASE_VALUE))
                    currentValue = BASE_VALUE;
                else if (currentValue.length() > 1)
                    currentValue = currentValue.substring(0, currentValue.length() - 1);
                updateTextView();

            }
        });
        delBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                currentValue = BASE_VALUE;
                updateTextView();
                return true;
            }
        });

        commaBtn = mainView.findViewById(R.id.fragmentExpenseCommaBtn);
        commaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!currentValue.contains(".")) {
                    currentValue += ".";
                    updateTextView();
                }
            }
        });
    }

    public ExpenseIncome getExpense() {
        double money = 0.0;
        try {
            money = Double.valueOf(moneyAmountTV.getText().toString());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        return new ExpenseIncome(money, ExpenseIncome.TYPE_EXPENSE, currentCategory, MyUtils.getCurrentDateTime(), MyUtils.getSelectedAccount());
    }
}
