package com.example.android.personalfinance_v01.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.android.personalfinance_v01.MyClasses.BalanceAccount;
import com.example.android.personalfinance_v01.MyClasses.MyUtils;
import com.example.android.personalfinance_v01.MyClasses.Transfer;
import com.example.android.personalfinance_v01.R;

public class AddTransferFragment extends Fragment {

    private static final String BASE_VALUE = "0";
    private String currentValue = BASE_VALUE;

    private View mainView;

    private TextView fromAccTv;
    private TextView toAccTv;

    private BalanceAccount fromAcc = MyUtils.accountList.get(0);
    private BalanceAccount toAcc = MyUtils.accountList.get(1);

    private TextView moneyAmountTV;

    private int fromRadioButtonIndex = 0;
    private int toRadioButtonIndex = 1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_add_transfer, container, false);

        //Money Amount TextView
        moneyAmountTV = mainView.findViewById(R.id.fragmentTransferMoneyAmountTV);

        initSelectAccountViews();

        initSpecialButtons();

        initNumberButtons();

        return mainView;
    }

    private void updateTextView() {
        moneyAmountTV.setText(currentValue);
    }

    private void initSelectAccountViews() {
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View accountsDialogView = getLayoutInflater().inflate(R.layout.dialog_choose_account, null);
                AlertDialog accountsAlertDialog = initAccountsAlertDialog(accountsDialogView);
                initAccountsAlertDialogButtons(view, accountsDialogView, accountsAlertDialog);
                accountsAlertDialog.show();
            }
        };

        fromAccTv = mainView.findViewById(R.id.fragmentTransferAccFromTv);
        fromAccTv.setOnClickListener(onClickListener);
        toAccTv = mainView.findViewById(R.id.fragmentTransferAccToTv);
        toAccTv.setOnClickListener(onClickListener);

        updateTextViews();
    }

    private AlertDialog initAccountsAlertDialog(View dialogView) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setView(dialogView);

        return alertDialogBuilder.create();
    }

    private void initAccountsAlertDialogButtons(final View rootView, View dialogView, final AlertDialog alertDialog) {
        RadioGroup radioGroup = dialogView.findViewById(R.id.dialogAccRadioGroup);

        if (rootView.equals(fromAccTv)) {
            for (int i = 0; i < MyUtils.accountList.size(); i++) {
                RadioButton radioButton = createCustomRadioButton(getContext(), MyUtils.accountList.get(i), i);
                radioGroup.addView(radioButton);
            }
            radioGroup.check(radioGroup.getChildAt(fromRadioButtonIndex).getId());
        } else if (rootView.equals(toAccTv)) {
            for (int i = 0; i < MyUtils.accountList.size(); i++) {
                RadioButton radioButton = createCustomRadioButton(getContext(), MyUtils.accountList.get(i), i);
                radioGroup.addView(radioButton);
            }
            radioGroup.getChildAt(fromRadioButtonIndex).setVisibility(View.GONE);
            radioGroup.check(radioGroup.getChildAt(toRadioButtonIndex).getId());
        }

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                int radioButtonID = radioGroup.getCheckedRadioButtonId();
                RadioButton radioButton = radioGroup.findViewById(radioButtonID);
                int selectedIndex = radioGroup.indexOfChild(radioButton);
                BalanceAccount selectedAccount = MyUtils.accountList.get(selectedIndex);

                if (rootView.equals(fromAccTv)) {
                    if (selectedAccount.equals(toAcc)) {
                        switchAccounts();
                    } else {
                        fromAcc = selectedAccount;
                        fromRadioButtonIndex = selectedIndex;
                    }
                } else if (rootView.equals(toAccTv)) {
                    toAcc = selectedAccount;
                    toRadioButtonIndex = selectedIndex;
                }

                updateTextViews();
                alertDialog.dismiss();
            }
        });
    }

    private RadioButton createCustomRadioButton(Context context, BalanceAccount item, int index) {
        LayoutInflater inflater = LayoutInflater.from(context);
        RadioButton radioButton = (RadioButton) inflater.inflate(R.layout.radio_button, null);

        radioButton.setId(index + 100);
        radioButton.setText(item.getName());

        return radioButton;
    }

    private void initNumberButtons() {
        //OnClick event for every number button (0-9)
        View.OnClickListener onClickNumber = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button button = (Button) v;
                if (currentValue.length() == 1 && currentValue.contains(BASE_VALUE))
                    currentValue = "";
                currentValue += button.getText().toString().trim();
                updateTextView();
            }
        };

        Button btn0 = mainView.findViewById(R.id.fragmentTransferBtn0);
        btn0.setOnClickListener(onClickNumber);

        Button btn1 = mainView.findViewById(R.id.fragmentTransferBtn1);
        btn1.setOnClickListener(onClickNumber);

        Button btn2 = mainView.findViewById(R.id.fragmentTransferBtn2);
        btn2.setOnClickListener(onClickNumber);

        Button btn3 = mainView.findViewById(R.id.fragmentTransferBtn3);
        btn3.setOnClickListener(onClickNumber);

        Button btn4 = mainView.findViewById(R.id.fragmentTransferBtn4);
        btn4.setOnClickListener(onClickNumber);

        Button btn5 = mainView.findViewById(R.id.fragmentTransferBtn5);
        btn5.setOnClickListener(onClickNumber);

        Button btn6 = mainView.findViewById(R.id.fragmentTransferBtn6);
        btn6.setOnClickListener(onClickNumber);

        Button btn7 = mainView.findViewById(R.id.fragmentTransferBtn7);
        btn7.setOnClickListener(onClickNumber);

        Button btn8 = mainView.findViewById(R.id.fragmentTransferBtn8);
        btn8.setOnClickListener(onClickNumber);

        Button btn9 = mainView.findViewById(R.id.fragmentTransferBtn9);
        btn9.setOnClickListener(onClickNumber);
    }

    private void initSpecialButtons() {
        Button delBtn = mainView.findViewById(R.id.fragmentTransferDelBtn);
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

        Button commaBtn = mainView.findViewById(R.id.fragmentTransferCommaBtn);
        commaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!currentValue.contains(".")) {
                    currentValue += ".";
                    updateTextView();
                }
            }
        });

        ImageView imageView = mainView.findViewById(R.id.fragmentTransferSwitchImage);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchAccounts();
                updateTextView();
            }
        });
    }

    public Transfer getTransfer() {
        double money = 0.0;
        try {
            money = Double.valueOf(moneyAmountTV.getText().toString());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        return new Transfer(money, fromAcc, toAcc);
    }

    private void updateTextViews() {
        fromAccTv.setText(fromAcc.getName());
        toAccTv.setText(toAcc.getName());
    }

    private void switchAccounts() {
        BalanceAccount temp = fromAcc;
        fromAcc = toAcc;
        toAcc = temp;

        int tempIndex = toRadioButtonIndex;
        toRadioButtonIndex = fromRadioButtonIndex;
        fromRadioButtonIndex = tempIndex;
    }
}
