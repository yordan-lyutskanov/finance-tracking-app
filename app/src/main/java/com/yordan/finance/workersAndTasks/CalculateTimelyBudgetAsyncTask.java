package com.yordan.finance.workersAndTasks;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.widget.TextView;

import androidx.preference.PreferenceManager;

import com.yordan.finance.R;
import com.yordan.finance.model.Expense;
import com.yordan.finance.utils.DateUtils;
import com.yordan.finance.utils.PriceUtils;
import com.yordan.finance.view.ui.dialogs.TimelyBudgetSetupDialog;

import java.lang.ref.WeakReference;
import java.util.List;

public class CalculateTimelyBudgetAsyncTask extends AsyncTask<List<Expense>, Void, Double> {

    private WeakReference<Activity> activity;


    public CalculateTimelyBudgetAsyncTask(Activity currentActivity) {
        this.activity = new WeakReference<>(currentActivity);
    }

    @Override
    protected Double doInBackground(List<Expense>... lists) {
        List<Expense> allExpenses = lists[0];

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity.get());
        int beginingOfPeriod = sharedPreferences.getInt(TimelyBudgetSetupDialog.SPK_TIMELY_DATE_FROM, -1);
        double amountSpentSince = 0;
        for(Expense expense : allExpenses){
            if(expense.getDate() > beginingOfPeriod){
                amountSpentSince += expense.getAmount();
            }
        }

        return amountSpentSince;
    }

    @Override
    protected void onPostExecute(Double amountSpentSince) {
        TextView tvTimelyBudgetAmount = this.activity.get().findViewById(R.id.tv_timely_amount);
        TextView tvTimelyBudgetDate = this.activity.get().findViewById(R.id.tv_timely_date);
        TextView tvTimelyBudgetRemainingTime = this.activity.get().findViewById(R.id.tv_timely_remaining);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity.get());
        float budgetAmount = sharedPreferences.getFloat(TimelyBudgetSetupDialog.SPK_CURRENT_TIMELY_AMOUNT, (-1 * Float.MIN_VALUE));
        int date = sharedPreferences.getInt(TimelyBudgetSetupDialog.SPK_CURRENT_TIMELY_DATE, -1);
        double remainingBalance = budgetAmount - amountSpentSince;

        tvTimelyBudgetAmount.setText(PriceUtils.formatPrice(remainingBalance));

        int remainingTimeSeconds = date - DateUtils.currentDate();
        int remainingTimeDays = remainingTimeSeconds / 3600 / 24;

        if(budgetAmount > 0){
            if(remainingTimeDays <= 0){
                tvTimelyBudgetAmount.setTextColor(Color.parseColor("#8BC34A"));
                tvTimelyBudgetDate.setText("Good Job. Time is Up!");
                tvTimelyBudgetRemainingTime.setText("You still have " + remainingBalance + " лв.");
            }else{
                tvTimelyBudgetDate.setText(DateUtils.intDateToString(date));
                tvTimelyBudgetRemainingTime.setText(remainingTimeDays + " days to go.");
            }
        }else if(budgetAmount == (-1 * Float.MIN_VALUE)){
            tvTimelyBudgetAmount.setText("-");
            tvTimelyBudgetDate.setText("Track spending over time.");
            tvTimelyBudgetRemainingTime.setText("Tap to add a new Budget.");
        }else{
            tvTimelyBudgetAmount.setTextColor(Color.parseColor("#F44336"));
            tvTimelyBudgetDate.setText("You've run out of budget.");
            tvTimelyBudgetRemainingTime.setText("Better luck next time.");
        }


    }
}
