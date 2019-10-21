package com.yordan.finance.workersAndTasks;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.TextView;

import com.yordan.finance.R;
import com.yordan.finance.model.Expense;
import com.yordan.finance.utils.DateUtils;
import com.yordan.finance.utils.PriceUtils;

import java.lang.ref.WeakReference;
import java.util.List;

public class GetAverageDailyAsyncTask extends AsyncTask<List<Expense>, Void, Double[]> {
    private WeakReference<Activity> activity;

    public GetAverageDailyAsyncTask(Activity activity) {
        this.activity = new WeakReference<>(activity);
    }

    @Override
    protected Double[] doInBackground(List<Expense>... lists) {
        List<Expense> expenses = lists[0];
        Double[] weeklyMonthkyYearly = new Double[3];

        int sevenDaysAgo = DateUtils.sevenDaysAgoDateAsInt();
        int monthAgo = DateUtils.thirtyDaysAgoAsInt();
        int yearAgo = DateUtils.yearAgoAsInt();
        int today = DateUtils.beginningOfTodayAsInt();

        double sum1 = 0;
        int days1 = 1;
        double sum2 = 0;
        int days2 = 1;
        double sum3 = 0;
        int days3 = 1;


        for(Expense e : expenses){
            int date = e.getDate();
            boolean newDay = false;

            if(date < today){
                today -= DateUtils.aDay();
                newDay = true;
            }

            if(date > sevenDaysAgo){
                sum1 += e.getAmount();
                if(newDay){
                    days1++;
                }
            }

            if(date > monthAgo){
                sum2 += e.getAmount();
                if(newDay){
                    days2++;
                }
            }

            if(date > yearAgo){
                sum3 += e.getAmount();
                if(newDay){
                    days3++;
                }
            }
        }

        weeklyMonthkyYearly[0] = sum1 / days1;
        weeklyMonthkyYearly[1] = sum2 / days2;
        weeklyMonthkyYearly[2] = sum3 / days3;

        return weeklyMonthkyYearly;
    }

    @Override
    protected void onPostExecute(Double[] results) {
        super.onPostExecute(results);

        TextView tvAverageWeekly = activity.get().findViewById(R.id.tv_average_last_7_days);
        TextView tvAverageMonthly = activity.get().findViewById(R.id.tv_average_last_30_days);
        TextView tvAverageYearly = activity.get().findViewById(R.id.tv_average_last_year);

        tvAverageWeekly.setText(PriceUtils.formatPrice(results[0]));
        tvAverageMonthly.setText(PriceUtils.formatPrice(results[1]));
        tvAverageYearly.setText(PriceUtils.formatPrice(results[2]));
    }
}
