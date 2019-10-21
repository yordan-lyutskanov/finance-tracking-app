package com.yordan.finance.workersAndTasks;

import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.widget.ImageView;
import android.widget.TextView;
import com.yordan.finance.R;
import com.yordan.finance.model.Category;
import com.yordan.finance.utils.CategoriesUtils;
import com.yordan.finance.model.Expense;
import com.yordan.finance.utils.DateUtils;
import com.yordan.finance.utils.PriceUtils;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PrepareMainDashboardAsyncTask extends AsyncTask<List<Expense>, Void, HashMap<String,Double>> {
    private static final String KEY_TOTAL_AMOUNT_THIS_WEEK = "totalWeekly";
    private static final String KEY_TOTAL_AMOUNT_TODAY = "totalDaily";
    private static final String KEY_MAX_CATEGORY_INDEX = "maxCatIndex";
    private static final String KEY_MAX_CATEGORY_VALUE = "maxCatValue";
    private static final String KEY_TOTAL_AMOUNT_YESTERDAY = "totalYesterdaily";
    private static final String KEY_TOTAL_AMOUNT_LAST_WEEK = "totalLastWeek";

    private WeakReference<Activity> activity;

    public PrepareMainDashboardAsyncTask(Activity activity) {
        this.activity = new WeakReference<>(activity);
    }

    @Override
    protected HashMap<String,Double> doInBackground(List<Expense>... lists) {
        List<Expense> expenses = lists[0];
        HashMap<String, Double> results = new HashMap<>();
        int twoWeeksAgo = DateUtils.sevenDaysAgoDateAsInt() - (7 * DateUtils.aDay());
        double totalSpentThisWeek = 0;
        double totalSpentLastWeek = 0;
        double totalSpentToday = 0;
        double totalSpentYesterday = 0;

        HashMap<Integer, Double> byCategory = new HashMap<>();

        for(Expense expense : expenses){
            double amount = expense.getAmount();
            int date = expense.getDate();
            int category = expense.getCategory() / 10;

            if(byCategory.get(category) == null){
                byCategory.put(category, amount);
            }else{
                byCategory.put(category, byCategory.get(category) + amount);
            }

            if(date >= DateUtils.sevenDaysAgoDateAsInt()){
                totalSpentThisWeek += amount;

                if(date > DateUtils.beginningOfTodayAsInt()){
                    totalSpentToday += amount;
                }else if(date < DateUtils.beginningOfTodayAsInt()
                        && date >= DateUtils.beginningOfTodayAsInt() - DateUtils.aDay()){
                    totalSpentYesterday += amount;
                }

            }else if(date >= twoWeeksAgo && date < DateUtils.sevenDaysAgoDateAsInt()){
                totalSpentLastWeek += amount;
            }
        }

        Map.Entry<Integer, Double> maxCategory = null;

        for(Map.Entry<Integer, Double> entry : byCategory.entrySet()){
            if(maxCategory == null || entry.getValue() > maxCategory.getValue()){
                maxCategory = entry;
            }
        }

        results.put(KEY_TOTAL_AMOUNT_TODAY, totalSpentToday);
        results.put(KEY_TOTAL_AMOUNT_YESTERDAY, totalSpentYesterday);

        results.put(KEY_TOTAL_AMOUNT_THIS_WEEK, totalSpentThisWeek);
        results.put(KEY_TOTAL_AMOUNT_LAST_WEEK, totalSpentLastWeek);
            if(maxCategory != null){
                results.put(KEY_MAX_CATEGORY_INDEX, (double) maxCategory.getKey());
                results.put(KEY_MAX_CATEGORY_VALUE, maxCategory.getValue());
            }else{
                results.put(KEY_MAX_CATEGORY_INDEX, -1d);
                results.put(KEY_MAX_CATEGORY_VALUE, -1d);
            }



        return results;
    }

    @Override
    protected void onPostExecute(HashMap<String, Double> stringDoubleHashMap) {
        TextView tvTotalThisWeek = this.activity.get().findViewById(R.id.tv_total_weekly);
        TextView tvTotalToday = this.activity.get().findViewById(R.id.tv_total_today);
        TextView tvMaxCategoryName = this.activity.get().findViewById(R.id.max_category_name);
        TextView tvMaxCategoryAmount = this.activity.get().findViewById(R.id.max_category_amount);
        ImageView ivCategoryMostSpend = this.activity.get().findViewById(R.id.iv_category_most_spend);
        ImageView ivSmallArrowDaily = this.activity.get().findViewById(R.id.iv_small_arrow_daily);
        ImageView ivSmallArrowWeekly = this.activity.get().findViewById(R.id.iv_small_arrow_monthly);


        Double totalSpentToday = stringDoubleHashMap.get(KEY_TOTAL_AMOUNT_TODAY);
        Double totalSpentYesterday = stringDoubleHashMap.get(KEY_TOTAL_AMOUNT_YESTERDAY);
        Double totalSpentThisWeek = stringDoubleHashMap.get(KEY_TOTAL_AMOUNT_THIS_WEEK);
        Double totalSpentLastWeek = stringDoubleHashMap.get(KEY_TOTAL_AMOUNT_LAST_WEEK);

        if(totalSpentToday != null && totalSpentYesterday != null
            && totalSpentLastWeek != null && totalSpentThisWeek != null){
            tvTotalThisWeek.setText(PriceUtils.formatPrice(totalSpentThisWeek));
            tvTotalToday.setText(PriceUtils.formatPrice(totalSpentToday));

            if(totalSpentToday > totalSpentYesterday){
                rotateArrow45(ivSmallArrowDaily);
            }else{
                rotateArrow135(ivSmallArrowDaily);
            }

            if(totalSpentThisWeek > totalSpentLastWeek){
                rotateArrow45(ivSmallArrowWeekly);
            }else{
                rotateArrow135(ivSmallArrowWeekly);
            }
        }

        int index = (int) Math.round(stringDoubleHashMap.get(KEY_MAX_CATEGORY_INDEX));
        double categoryAmount = stringDoubleHashMap.get(KEY_MAX_CATEGORY_VALUE);


        if(index > 0){
            Category category = CategoriesUtils.getCategories().get(index - 1);
            String catName = category.getName();
            setCategoryMostSpendImage(index, ivCategoryMostSpend);
            tvMaxCategoryAmount.setText(PriceUtils.formatPrice(categoryAmount));
            tvMaxCategoryName.setText("This month on: " + catName);
        }else{
            tvMaxCategoryAmount.setText("0.00 лв.");
            ivCategoryMostSpend.setImageResource(R.drawable.ic_no_dolars);
            tvMaxCategoryName.setText("No recent expenses.");
        }
    }

    private void setCategoryMostSpendImage(int categoryIndex, ImageView ivCategoryMostSpend) {
        switch (categoryIndex) {
            case 1:
                ivCategoryMostSpend.setImageResource(R.drawable.ic_category_home);
                break;
            case 2:
                ivCategoryMostSpend.setImageResource(R.drawable.ic_categories_utilities);
                break;
            case 3:
                ivCategoryMostSpend.setImageResource(R.drawable.ic_category_groceries);
                break;
            case 4:
                ivCategoryMostSpend.setImageResource(R.drawable.ic_categories_transportation);
                break;
            case 5:
                ivCategoryMostSpend.setImageResource(R.drawable.ic_categories_personal);
                break;
            case 6:
                ivCategoryMostSpend.setImageResource(R.drawable.ic_categories_entertainment);
                break;
            case 7:
                ivCategoryMostSpend.setImageResource(R.drawable.ic_categories_health);
                break;
            case 8:
                ivCategoryMostSpend.setImageResource(R.drawable.ic_category_savings);
                break;
            case 9:
                ivCategoryMostSpend.setImageResource(R.drawable.ic_categories_others);
                break;
        }
    }

    private void rotateArrow45(ImageView imageView){
        imageView.setRotation(45);
        imageView.setColorFilter(Color.parseColor("#F44336"));
    }

    private void rotateArrow135(ImageView imageView){
        imageView.setRotation(135);
        imageView.setColorFilter(Color.parseColor("#4CAF50"));
    }
}
