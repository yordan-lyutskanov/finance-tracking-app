package com.yordan.finance.view.ui.fragments;


import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.yordan.finance.R;
import com.yordan.finance.utils.CategoriesUtils;
import com.yordan.finance.model.Expense;
import com.yordan.finance.utils.PriceUtils;
import com.yordan.finance.viewmodel.AppViewModel;


import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PieFragment extends Fragment {

    private PieChart pieChart;
    private AppViewModel viewModel;

    TextView tvLegendEntry1;
    TextView tvLegendEntry2;
    TextView tvLegendEntry3;
    TextView tvLegendEntry4;
    TextView tvLegendEntry5;
    TextView tvLegendEntry6;
    TextView tvLegendEntry7;
    TextView tvLegendEntry8;
    TextView tvLegendEntry9;

    TextView tvLegendValue1;
    TextView tvLegendValue2;
    TextView tvLegendValue3;
    TextView tvLegendValue4;
    TextView tvLegendValue5;
    TextView tvLegendValue6;
    TextView tvLegendValue7;
    TextView tvLegendValue8;
    TextView tvLegendValue9;

    ImageView ivLegendColor1;
    ImageView ivLegendColor2;
    ImageView ivLegendColor3;
    ImageView ivLegendColor4;
    ImageView ivLegendColor5;
    ImageView ivLegendColor6;
    ImageView ivLegendColor7;
    ImageView ivLegendColor8;
    ImageView ivLegendColor9;

    private Switch valuesSwitch;
    private TextView tvValuesSwitchLabel;


    //Initial data
    private float[] yData;
    private List<Expense> expenseData = new ArrayList<>();
    private List<String> xData;

    //Array of initial color for the legend
    private int[] colors = {
            R.color.colorPie,
            R.color.colorPie1,
            R.color.colorPie2,
            R.color.colorPie3,
            R.color.colorPie4,
            R.color.colorPie5,
            R.color.colorPie6,
            R.color.colorPie7,
            R.color.colorPie8 };

    List<TextView> valuesInLegend;
    List<TextView> categoriesInLegend;
    List<ImageView> colorsInLegend;

    private ArrayList<PieEntry> pieEntries = new ArrayList<>();

    //Suffix for the values to be added at the end of each value
    private String suffix = " BGN";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pie_fragment_layout, container, false);

        viewModel = ViewModelProviders.of(this).get(AppViewModel.class);


        tvLegendEntry1 = view.findViewById(R.id.tv_legend_entry1);
        tvLegendEntry2 = view.findViewById(R.id.tv_legend_entry2);
        tvLegendEntry3 = view.findViewById(R.id.tv_legend_entry3);
        tvLegendEntry4 = view.findViewById(R.id.tv_legend_entry4);
        tvLegendEntry5 = view.findViewById(R.id.tv_legend_entry5);
        tvLegendEntry6 = view.findViewById(R.id.tv_legend_entry6);
        tvLegendEntry7 = view.findViewById(R.id.tv_legend_entry7);
        tvLegendEntry8 = view.findViewById(R.id.tv_legend_entry8);
        tvLegendEntry9 = view.findViewById(R.id.tv_legend_entry9);

        tvLegendValue1 = view.findViewById(R.id.tv_legend_value1);
        tvLegendValue2 = view.findViewById(R.id.tv_legend_value2);
        tvLegendValue3 = view.findViewById(R.id.tv_legend_value3);
        tvLegendValue4 = view.findViewById(R.id.tv_legend_value4);
        tvLegendValue5 = view.findViewById(R.id.tv_legend_value5);
        tvLegendValue6 = view.findViewById(R.id.tv_legend_value6);
        tvLegendValue7 = view.findViewById(R.id.tv_legend_value7);
        tvLegendValue8 = view.findViewById(R.id.tv_legend_value8);
        tvLegendValue9 = view.findViewById(R.id.tv_legend_value9);

        ivLegendColor1 = view.findViewById(R.id.iv_legend_color_1);
        ivLegendColor2 = view.findViewById(R.id.iv_legend_color_2);
        ivLegendColor3 = view.findViewById(R.id.iv_legend_color_3);
        ivLegendColor4 = view.findViewById(R.id.iv_legend_color_4);
        ivLegendColor5 = view.findViewById(R.id.iv_legend_color_5);
        ivLegendColor6 = view.findViewById(R.id.iv_legend_color_6);
        ivLegendColor7 = view.findViewById(R.id.iv_legend_color_7);
        ivLegendColor8 = view.findViewById(R.id.iv_legend_color_8);
        ivLegendColor9 = view.findViewById(R.id.iv_legend_color_9);

        tvValuesSwitchLabel = view.findViewById(R.id.tv_values_label);
        valuesSwitch = view.findViewById(R.id.values_switch);

        pieChart = view.findViewById(R.id.pie_chart);


        //Initializing the list of image views and text view in the legend to be able to
        //easily display data in bulk
        if(valuesInLegend == null || valuesInLegend.isEmpty()){
            valuesInLegend = new ArrayList<>();
            valuesInLegend = Arrays.asList(tvLegendValue1, tvLegendValue2, tvLegendValue3,
                    tvLegendValue4, tvLegendValue5, tvLegendValue6,
                    tvLegendValue7, tvLegendValue8, tvLegendValue9);
        }

        if(categoriesInLegend == null || categoriesInLegend.isEmpty()){
            categoriesInLegend = Arrays.asList(tvLegendEntry1, tvLegendEntry2, tvLegendEntry3,
                    tvLegendEntry4, tvLegendEntry5, tvLegendEntry6,
                    tvLegendEntry7, tvLegendEntry8, tvLegendEntry9);
        }

        if(colorsInLegend == null || colorsInLegend.isEmpty()){
            colorsInLegend = Arrays.asList(ivLegendColor1, ivLegendColor2, ivLegendColor3,
                    ivLegendColor4, ivLegendColor5, ivLegendColor6,
                    ivLegendColor7, ivLegendColor8, ivLegendColor9);
        }
        xData = CategoriesUtils.getCategoriesNames();
        initDataAsValues();
        addData();

        //These 3 methods are using the dataSet created by the addData and must therefore be called after addData()
        initLegendColors();
        initLegendText();
        initLegendValues();
        highlighSliceOnLegendClick(colorsInLegend, categoriesInLegend, valuesInLegend);

        valuesSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(valuesSwitch.isChecked()){
                    switchToPercentage();
                    setLegendTextToPercentage();
                }else{
                    switchToValues();
                    setLegendTextToValues();
                }

                //If something is highlighted find its index and re-highlight it to apply the changes:
                if(pieChart.getHighlighted() != null){
                    float index = pieChart.getHighlighted()[0].getX();
                    pieChart.highlightValue(0, 0);
                    pieChart.highlightValue(index, 0);
                }
            }
        });

        viewModel.getExpenses().observe(this, new Observer<List<Expense>>() {
            @Override
            public void onChanged(List<Expense> expenses) {
                expenseData = expenses;
                initDataAsValues();
                addData();
                initLegendValues();
                pieChart.notifyDataSetChanged();
                pieChart.invalidate();
            }
        });

        return view;
    }

    private void addData(){
        pieEntries = new ArrayList<>();

        for(int i = 0; i < yData.length; i++){
            pieEntries.add(new PieEntry(yData[i]));
        }

        //Create dataSet using the pie entries ArrayList
        PieDataSet dataSet = new PieDataSet(pieEntries, "");
        dataSet.setValueTypeface(Typeface.MONOSPACE);
        //Visual setup of the dataSet
        setupDatasetOption(dataSet);

        PieData data = new PieData(dataSet);
        data.setDrawValues(false);
        pieChart.setData(data);

        //Visual setup of the pieChart
        setupPieChart(pieChart);
    }

    private void switchToPercentage(){
        tvValuesSwitchLabel.setText("Percent");
        suffix = " %";
        pieChart.animate();

    }

    private void switchToValues(){
        tvValuesSwitchLabel.setText("Values");
        suffix = " BGN";
        pieChart.notifyDataSetChanged();
    }


    private void setLegendTextToValues(){
        for(int i = 0; i < valuesInLegend.size(); i++){
            float value = pieEntries.get(i).getValue();
            valuesInLegend.get(i).setText(customFormat(value));
        }
    }

    private void setLegendTextToPercentage(){
        for(int i = 0; i < valuesInLegend.size(); i++){
            float valueAsPercent = calculateValueAsPercent(yData[i]);
            valuesInLegend.get(i).setText(customFormat(valueAsPercent));
        }
    }

    private void initLegendColors(){
        for(int i = 0; i < colorsInLegend.size(); i++){
            colorsInLegend.get(i).setImageResource(colors[i]);
        }
    }

    private void initLegendText(){
        for(int i = 0; i < categoriesInLegend.size(); i++){
            categoriesInLegend.get(i).setText(xData.get(i));
        }
    }

    private void initLegendValues(){
        setLegendTextToValues();
    }

    private void setupDatasetOption(PieDataSet dataSet){
        dataSet.setSliceSpace(2f);
        dataSet.setColors(colors, this.getContext());
        dataSet.setValueLinePart1Length(0.4f);
        dataSet.setValueLinePart2Length(0.2f);
        dataSet.setValueLineVariableLength(true);
        dataSet.setSelectionShift(10f);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setHighlightEnabled(true);

        dataSet.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                return PriceUtils.formatPrice(value);
            }
        });
    }

    private void setupPieChart(PieChart pieChart){
        Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/oswald_regular.ttf");
        pieChart.setCenterTextTypeface(typeface);
        pieChart.setEntryLabelTextSize(18f);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setHoleRadius(5f);
        pieChart.setTransparentCircleRadius(10f);

        //Disable legend
        Legend legend = pieChart.getLegend();
        legend.setEnabled(false);

        //Setup the description
        Description description = new Description();
        description.setText("");
        description.setYOffset(100f);
        description.setTextAlign(Paint.Align.RIGHT);
        description.setTextSize(15f);
        pieChart.setDescription(description);


        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                //Setting the label of the selected value to the appropriate label from the
                //xData list whilst keeping the rest of the labels empty ("")
                for(PieEntry p : pieEntries){
                    p.setLabel("");
                }

                PieEntry p = (PieEntry) e;

                if(!valuesSwitch.isChecked()){
                    pieChart.setCenterText(xData.get(pieEntries.indexOf(p)) + "\n" + customFormat(p.getValue()));
                }else{
                    pieChart.setCenterText(xData.get(pieEntries.indexOf(p)) + "\n" + customFormat(calculateValueAsPercent(p.getValue())));
                }
                pieChart.setHoleRadius(80f);
                pieChart.setCenterTextSize(18);

            }

            @Override
            public void onNothingSelected() {
                for(PieEntry p : pieEntries){
                    p.setLabel("");
                    pieChart.setHoleRadius(0f);
                    pieChart.setCenterText("");
                }
            }
        });
    }

    private void highlighSliceOnLegendClick(List<ImageView> iv, List<TextView> tv1, List<TextView> tv2){
        final int[] ints = {0, 1, 2, 3, 4, 5, 6, 7, 8};

        for(int i: ints){
            iv.get(i).setOnClickListener((v) -> pieChart.highlightValue(i, 0));
            tv1.get(i).setOnClickListener((v) -> pieChart.highlightValue(i, 0));
            tv2.get(i).setOnClickListener((v) -> pieChart.highlightValue(i, 0));
        }
    }

    private void initDataAsValues(){
        yData = new float[9];

        for(Expense expense : expenseData){
            switch(expense.getCategory() / 10){
                case 1:
                    yData[0] = yData[0] += expense.getAmount();
                    break;
                case 2:
                    yData[1] = yData[1] += expense.getAmount();
                    break;
                case 3:
                    yData[2] = yData[2] += expense.getAmount();
                    break;
                case 4:
                    yData[3] = yData[3] += expense.getAmount();
                    break;
                case 5:
                    yData[4] = yData[4] += expense.getAmount();
                    break;
                case 6:
                    yData[5] = yData[5] += expense.getAmount();
                    break;
                case 7:
                    yData[6] = yData[6] += expense.getAmount();
                    break;
                case 8:
                    yData[7] = yData[7] += expense.getAmount();
                    break;
                case 9:
                    yData[8] = yData[8] += expense.getAmount();
                    break;
                default:
                    break;
            }
        }

    }


    private String customFormat(float f){
        DecimalFormat format = new DecimalFormat("#.##");
        format.setRoundingMode(RoundingMode.HALF_UP);
        return format.format(f) + suffix;
    }

    private float calculateValueAsPercent(float f){
        float sum = 0;

        for(float i : yData){
            sum += i;
        }

        return f / sum * 100f;
    }


}
