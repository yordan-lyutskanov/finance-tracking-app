package com.yordan.finance.workersAndTasks;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.preference.PreferenceManager;

import com.yordan.finance.R;
import com.yordan.finance.model.Budget;
import com.yordan.finance.utils.CategoriesUtils;
import com.yordan.finance.model.Expense;
import com.yordan.finance.view.ui.activities.ActivitySettings;

import java.lang.ref.WeakReference;
import java.util.List;


public class GetBudgetAsyncTask extends AsyncTask<List<Expense>, Void, Budget[]> {
    private WeakReference<Activity> activity;

    public GetBudgetAsyncTask(Activity currentActivity) {
        this.activity = new WeakReference<>(currentActivity);
    }

    @Override
    protected Budget[] doInBackground(List<Expense>... lists) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity.get());
        if(preferences.getBoolean(ActivitySettings.KEY_BUDGETING_ENABLED, false)){
            List<Expense> monthlyExpenses = lists[0];
            Budget[] monthlyBudgets = new Budget[9];
            double[] maximumAmountsAsPerUserSettings = new double[9];


            int index = 0;
            for(String key : ActivitySettings.KEYS_FOR_BUDGET){
                String maxAmount = preferences.getString(key, "0");
                if(maxAmount.equals("")){
                    maxAmount = "0";
                }
                double amount = Double.parseDouble(maxAmount);
                maximumAmountsAsPerUserSettings[index++] = amount;
            }

            for(Expense expense : monthlyExpenses){
                int category = expense.getCategory() / 10;
                double amount = expense.getAmount();
                if(monthlyBudgets[(category - 1)] == null){
                    monthlyBudgets[(category - 1)] =  new Budget(expense.getAmount(),
                            maximumAmountsAsPerUserSettings[category - 1]);
                }else{
                    monthlyBudgets[(category - 1)].incrementCurrentAmount(amount);
                }
            }

            for(int i = 0; i < monthlyBudgets.length; i++){
                if(monthlyBudgets[i] == null){
                    monthlyBudgets[i] = new Budget(0, maximumAmountsAsPerUserSettings[i]);
                }
            }

            return monthlyBudgets;
        }else{
            return new Budget[]{};
        }

    }

    @Override
    protected void onPostExecute(Budget[] budgets) {
        if(budgets.length != 0){
            LinearLayout budgetContainer = this.activity.get().findViewById(R.id.ll_budget_container);
            budgetContainer.removeAllViews();
            boolean hasOneOrMoreBudgets = false;

            int categoryIndex = 0;
            Typeface typefaceOswald = Typeface.createFromAsset(activity.get().getAssets(), "fonts/oswald_regular.ttf");

            for(Budget b : budgets){
                String categoryName = CategoriesUtils.getCategoryNameFromId(++categoryIndex);
                if(b.getMaxAmount() != 0){
                    hasOneOrMoreBudgets = true;

                    //Create a textView for each budget
                    TextView textView = new TextView(activity.get());
                    String currencySuffix = " лв.";
                    String text = categoryName + " - " + b.getCurrentAmount() + currencySuffix + " / " + b.getMaxAmount() + currencySuffix;
                    textView.setText(text);
                    textView.setTextSize(15);
                    textView.setTypeface(typefaceOswald);
                    textView.setGravity(Gravity.RIGHT);
                    budgetContainer.addView(textView);

                    //Create a progress bar for each budget
                    ProgressBar progressBar = new ProgressBar(activity.get(), null, android.R.attr.progressBarStyleHorizontal);
                    int progress = (int) (b.getCurrentAmount() / b.getMaxAmount() * 100);
                    progressBar.setProgress(progress);
                    progressBar.setRotation(180);
                    if(progress >= 75){
                        progressBar.setProgressTintList(ColorStateList.valueOf(Color.rgb(247, 96, 117)));
                    }else if(progress >= 50){
                        progressBar.setProgressTintList(ColorStateList.valueOf(Color.rgb(255, 222, 135)));
                    }
                    budgetContainer.addView(progressBar);
                }
            }

            if(!hasOneOrMoreBudgets){
                TextView textView = new TextView(activity.get());
                textView.setText("Go to settings to set-up budget for each category.");
                budgetContainer.addView(textView);
            }
        }
    }
}
