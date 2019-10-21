package com.yordan.finance.view.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.preference.PreferenceManager;

import com.yordan.finance.R;
import com.yordan.finance.utils.DateUtils;
import com.yordan.finance.view.callback.ButtonClickListener;

import java.util.Date;

public class TimelyBudgetSetupDialog extends AppCompatDialogFragment {

    public static final String SPK_CURRENT_TIMELY_AMOUNT = "asDasxz12";
    public static final String SPK_CURRENT_TIMELY_DATE = "zz12lnjk";
    public static final String SPK_TIMELY_DATE_FROM = "zz121njk";
    public static final String BUTTON_OK = "ok";
    public static final String BUTTON_CANCEL = "cancel";

    private float currentAmount;
    private int currentBudgetDate;
    private int currentBeginingDate;
    private ButtonClickListener buttonClickListener;

    private EditText etTimelyBudget;
    private DatePicker dpBudgetExpiryDate;
    private Button btConfirm;
    private Button btCancel;

    public TimelyBudgetSetupDialog(ButtonClickListener buttonClickListener) {
        this.buttonClickListener = buttonClickListener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_timely_budget_setup, null);

        etTimelyBudget = view.findViewById(R.id.et_timely_budget);
        dpBudgetExpiryDate = view.findViewById(R.id.dp_timely_budget);
        btConfirm = view.findViewById(R.id.bt_confirm);
        btCancel = view.findViewById(R.id.bt_cancel);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        dpBudgetExpiryDate.setMinDate(System.currentTimeMillis());
        currentAmount = sharedPreferences.getFloat(SPK_CURRENT_TIMELY_AMOUNT, (-1 * Float.MIN_VALUE));
        currentBudgetDate = sharedPreferences.getInt(SPK_CURRENT_TIMELY_DATE, -1);
        currentBeginingDate = sharedPreferences.getInt(SPK_TIMELY_DATE_FROM, -1);

        if(currentAmount == (-1 * Float.MIN_VALUE)){
            etTimelyBudget.setText("");
        }else{
            etTimelyBudget.setText(currentAmount + "");
        }

        int[] dayMonthYear = DateUtils.parseDate(currentBudgetDate);
        dpBudgetExpiryDate.updateDate(dayMonthYear[0], dayMonthYear[1], dayMonthYear[2]);

        btConfirm.setOnClickListener(v -> {
            int[] dateArray = {
                    dpBudgetExpiryDate.getYear(),
                    dpBudgetExpiryDate.getMonth(),
                    dpBudgetExpiryDate.getDayOfMonth()
            };
            Date inputedDate = DateUtils.parseIntArray(dateArray);

            int newBudgetDate = (int) (inputedDate.getTime() / 1000L);

            String amountString = etTimelyBudget.getText().toString();

            float newAmount = amountString.isEmpty() ? (-1 * Float.MIN_VALUE) :  Float.parseFloat(amountString);
            int newBeginningDate;

            if(newAmount == currentAmount && newBudgetDate == currentBudgetDate){
                newBeginningDate = currentBeginingDate;
            }else{
                newBeginningDate = DateUtils.currentDate();
            }

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putFloat(SPK_CURRENT_TIMELY_AMOUNT, newAmount);
            editor.putInt(SPK_CURRENT_TIMELY_DATE, newBudgetDate);
            editor.putInt(SPK_TIMELY_DATE_FROM, newBeginningDate);
            editor.commit();

            buttonClickListener.buttonClicked(BUTTON_OK);
            dismiss();
        });

        btCancel.setOnClickListener(v -> {
            buttonClickListener.buttonClicked(BUTTON_CANCEL);
            dismiss();
        });

        return builder.setView(view).create();
    }


}
