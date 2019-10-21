package com.yordan.finance.view.ui.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.yordan.finance.R;
import com.yordan.finance.utils.FilterSortUtils;
import com.yordan.finance.utils.DateUtils;
import com.yordan.finance.utils.DateValueFormatter;
import com.yordan.finance.model.Expense;
import com.yordan.finance.viewmodel.AppViewModel;

import java.util.ArrayList;
import java.util.List;


public class LineFragment extends Fragment {
    private LineChart lineChart;
    private RadioGroup rgPeriod;
    private RadioButton rbWeekly;
    private TextView tvValueDescription;
    private DateValueFormatter formatter;
    private AppViewModel viewModel;
    private ArrayList<Entry> entryArrayList;

    private static final int SIZE_ALL_EXPENSES = -1;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bar_fragment_layout, container, false);

        viewModel = ViewModelProviders.of(this).get(AppViewModel.class);

        lineChart = view.findViewById(R.id.line_chart);
        tvValueDescription = view.findViewById(R.id.tv_value_description);
        rgPeriod = view.findViewById(R.id.rg_period);
        rbWeekly = view.findViewById(R.id.rb_weekly);

        formatter = DateValueFormatter.getInstance(DateValueFormatter.WEEKLY);

        rgPeriod.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                switch (i){
                    case R.id.rb_weekly:
                        viewModel.getExpensesApplyFilterSort(FilterSortUtils.FILTER_DATE_LAST_WEEK, FilterSortUtils.ORDER_BY_DATE_ASC).observe(LineFragment.this, new Observer<List<Expense>>() {
                            @Override
                            public void onChanged(List<Expense> expenses) {
                                populateLineChart(expenses, 7);
                            }
                        });
                        break;
                    case R.id.rbMonthly:
                        viewModel.getExpensesApplyFilterSort(FilterSortUtils.FILTER_DATE_LAST_MONTH, FilterSortUtils.ORDER_BY_DATE_ASC).observe(LineFragment.this, new Observer<List<Expense>>() {
                            @Override
                            public void onChanged(List<Expense> expenses) {
                                populateLineChart(expenses, 30);
                            }
                        });
                        break;
                    case R.id.rbYearly:
                        viewModel.getExpensesApplyFilterSort(FilterSortUtils.FILTER_DATE_LAST_YEAR, FilterSortUtils.ORDER_BY_DATE_ASC).observe(LineFragment.this, new Observer<List<Expense>>() {
                            @Override
                            public void onChanged(List<Expense> expenses) {
                                populateLineChart(expenses, 365);
                            }
                        });
                        break;
                    case R.id.rbAll:
                        viewModel.getExpensesApplyFilterSort(FilterSortUtils.FILTER_DATE_ALL, FilterSortUtils.ORDER_BY_DATE_ASC).observe(LineFragment.this, new Observer<List<Expense>>() {
                            @Override
                            public void onChanged(List<Expense> expenses) {
                                populateLineChart(expenses, SIZE_ALL_EXPENSES);
                            }
                        });
                        break;
                }
            }
        });

        rgPeriod.check(R.id.rb_weekly);
        rbWeekly.toggle();

        lineChart.setDrawGridBackground(false);
        lineChart.setDrawBorders(true);
        lineChart.setDescription(null);

        return view;
    }

    private void addData(){


        LineDataSet dataSet = new LineDataSet(entryArrayList, "Spending in BGN for selected period of time.");

        dataSet.setDrawValues(false);
        dataSet.setLineWidth(2f);
        dataSet.setCircleColor(Color.rgb(216,27,96));
        dataSet.setCircleSize(5);
        dataSet.setColor(Color.rgb(216,27,96));
        dataSet.setValueTextSize(14f);
        dataSet.setFormSize(5f);


        LineData data = new LineData(dataSet);

        lineChart.setData(data);
        lineChart.setScrollContainer(true);

    }

    private void customizeAxis(){
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setLabelRotationAngle(40f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(12f);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);

        YAxis yAxisRight = lineChart.getAxisRight();
        yAxisRight.setDrawLabels(false);
        yAxisRight.setDrawGridLines(false);


        YAxis yAxisLeft = lineChart.getAxisLeft();
        yAxisLeft.setXOffset(10f);
        yAxisLeft.setDrawZeroLine(true);
//        yAxisLeft.setTextColor();
        yAxisLeft.setTextSize(14f);
//        yAxisLeft.setGridColor();


        xAxis.setValueFormatter(formatter);
    }

    private void initializeChartWithWeeklyExpenses(){
        viewModel.getExpensesApplyFilterSort(FilterSortUtils.FILTER_DATE_LAST_WEEK,
                FilterSortUtils.ORDER_BY_DATE_ASC).observe(LineFragment.this, new Observer<List<Expense>>() {
            @Override
            public void onChanged(List<Expense> expenses) {
                populateLineChart(expenses, 7);
            }
        });
    }

    private void populateLineChart(List<Expense> expenses, int size){
        entryArrayList = new ArrayList<>();
        int startDate;

        if(size != SIZE_ALL_EXPENSES){
            startDate = DateUtils.beginningOfTodayAsInt() -
                    (size - 2) * DateUtils.aDay();
        }else{
            int firstExpenseDate = expenses.get(0).getDate();
            startDate = DateUtils.beginingOfADay(firstExpenseDate)
                    + DateUtils.aDay();
        }


        int entryIndex = 0;
        int amount = 0;

        for(Expense e : expenses){
            while(e.getDate() >= startDate){
                entryArrayList.add(new Entry(entryIndex++, amount));
                amount = 0;
                startDate += DateUtils.aDay();
            }

            amount += e.getAmount();
        }

        entryArrayList.add(new Entry(entryIndex++, amount));



//        float amount = 0;
//        int date = DateUtils.beginningOfTodayAsInt();
//        int index = 0;
//
//        for(Expense e : expenses){
//            if(e.getDate() >= date){
//                amount += e.getAmount();
//            }else{
//                entryArrayList.add(new Entry(index++, amount));
//                amount = 0;
//                date -= DateUtils.aDay();
//            }
//        }
//
//        if(index < size){
//            entryArrayList.add(new Entry(index++, amount));
//            while(index < size){
//                entryArrayList.add(new Entry(index++, 0));
//            }
//        }


        LineDataSet dataSet = new LineDataSet(entryArrayList, "Label");
        LineData data = new LineData(dataSet);
        lineChart.setData(data);
        lineChart.invalidate();
    }
}
