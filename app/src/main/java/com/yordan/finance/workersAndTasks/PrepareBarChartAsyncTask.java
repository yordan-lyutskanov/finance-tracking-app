package com.yordan.finance.workersAndTasks;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.yordan.finance.R;
import com.yordan.finance.model.Expense;
import com.yordan.finance.utils.DateUtils;
import com.yordan.finance.utils.PriceUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class PrepareBarChartAsyncTask extends AsyncTask<List<Expense>, Void, BarDataSet> {
    private WeakReference<Activity> activity;



    public PrepareBarChartAsyncTask(Activity activity) {
        this.activity = new WeakReference<>(activity);

    }

    @Override
    protected BarDataSet doInBackground(List<Expense>... lists) {
        List<Expense> expenses = lists[0];
        List<BarEntry> yVals = new ArrayList<>();

        int entryIndex = 6;
        int expenseIndex = 0;
        int dateComparedAgainst = DateUtils.beginningOfTodayAsInt() - 1;
        BarEntry barEntry = new BarEntry(entryIndex, 0);

        //Function to get the expenses from the last 7 days and add them respectivly to new BarEntries
        //in the yVals arraylist

        while(expenseIndex < expenses.size() && entryIndex >= 0){
            Expense e = expenses.get(expenseIndex);

            if(e.getDate() >= dateComparedAgainst){
                barEntry.setY(barEntry.getY() + (float) e.getAmount());
                expenseIndex++;
            }else{
                yVals.add(barEntry);
                barEntry = new BarEntry(--entryIndex, 0);
                dateComparedAgainst -= DateUtils.aDay();
            }
        }

        System.out.println("Entry index is" + entryIndex);

        // If you don't have any expenses something still need to be added to yVals to
        // make sure you have 0 bars displaying on the screen

        if(entryIndex >= 0){
            yVals.add(barEntry);
            --entryIndex;
            while(entryIndex >= 0){
                yVals.add(new BarEntry(entryIndex--, 0f));
            }
        }


        return new BarDataSet(yVals, "Weekly insights");

    }

    @Override
    protected void onPostExecute(BarDataSet data) {
        BarChart barChart = this.activity.get().findViewById(R.id.bc_main_dashboard);

        BarData barData = customizeAndSetData(data, barChart);
        barChart.setData(barData);
        barChart.setDrawBorders(false);
        barChart.getLegend().setEnabled(false);
        barChart.getDescription().setEnabled(false);
        barChart.invalidate();
    }

    private BarData customizeAndSetData(BarDataSet dataSet, BarChart barChart){
        YAxis yAxisLeft = barChart.getAxisLeft();
        yAxisLeft.setDrawLabels(false);
        yAxisLeft.setDrawGridLines(true);
        yAxisLeft.setDrawAxisLine(false);

        YAxis yAxisRight = barChart.getAxisRight();
        yAxisRight.setDrawLabels(false);
        yAxisRight.setDrawGridLines(false);
        yAxisRight.setDrawAxisLine(false);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setDrawLabels(false);
        xAxis.setDrawAxisLine(false);


        dataSet.setDrawValues(true);
        dataSet.setValueTextColor(Color.rgb(69, 90, 100));
        dataSet.setValueTextSize(12f);
        dataSet.setBarBorderColor(Color.BLACK);
        dataSet.setBarBorderWidth(1f);
        dataSet.setValueTypeface(Typeface.SANS_SERIF);
        dataSet.setColor(Color.rgb(69, 90, 100)); //55, 71, 79
        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        dataSet.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                return String.format("%.0f", value) + PriceUtils.getSuffix();
            }
        });

        return new BarData(dataSet);
    }
}
