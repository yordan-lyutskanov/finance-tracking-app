package com.yordan.finance.view.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.PreferenceManager;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.github.mikephil.charting.charts.BarChart;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.yordan.finance.R;
import com.yordan.finance.model.Expense;
import com.yordan.finance.utils.FilterSortUtils;
import com.yordan.finance.view.ui.dialogs.TimelyBudgetSetupDialog;
import com.yordan.finance.view.ui.fragments.SettingsFragmentDashboard;
import com.yordan.finance.viewmodel.AppViewModel;
import com.yordan.finance.workersAndTasks.CalculateTimelyBudgetAsyncTask;
import com.yordan.finance.workersAndTasks.GetAverageDailyAsyncTask;
import com.yordan.finance.workersAndTasks.GetBudgetAsyncTask;
import com.yordan.finance.workersAndTasks.PrepareBarChartAsyncTask;
import com.yordan.finance.workersAndTasks.PrepareMainDashboardAsyncTask;
import java.util.List;

public class ActivityMainDashboard extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private final String TAG = this.getClass().getSimpleName();
    public static final String AUTHORITY = "";

    CoordinatorLayout mainCoordinatorLayout;
    BottomAppBar bottomAppBar;
    FloatingActionButton mainFab;

    ImageView ivGoHome;
    ImageView ivGoList;
    ImageView ivGoSettings;
    ImageView ivGoStatistics;


    ImageView ivExpandBudget;
    LinearLayout llBudgetContainer;
    LinearLayout llMonthlyBudgetField;


    /*
    Above declared belongs to the bottom bar.
     */

    LinearLayout llTimelyLimit;
    LinearLayout llBarChart;
    ConstraintLayout clAverageDaily;

    BarChart barChart;
    AppViewModel viewModel;
    NotificationManagerCompat notificationManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_dashboard);

        Log.d(TAG, "onCreate: In main activity...");

        notificationManager = NotificationManagerCompat.from(this);
        setNavbarViews();
        setNavbarClicklisteners();

        llBudgetContainer = findViewById(R.id.ll_budget_container);
        llMonthlyBudgetField = findViewById(R.id.ll_monthly_budget_field);
        llBarChart = findViewById(R.id.ll_bar_chart);
        llTimelyLimit = findViewById(R.id.ll_timely_limit);
        clAverageDaily = findViewById(R.id.cl_average_daily);
        ivExpandBudget = findViewById(R.id.iv_expand_budget);
        barChart = findViewById(R.id.bc_main_dashboard);

        //Setup the Dashboard According to Settings:
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if(sharedPreferences.getBoolean(ActivitySettings.KEY_BUDGETING_ENABLED, false)){
            llMonthlyBudgetField.setVisibility(View.VISIBLE);
        }else{
            llMonthlyBudgetField.setVisibility(View.GONE);
        }

        //Setup DashBoard's Click listeners
        llTimelyLimit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new TimelyBudgetSetupDialog((v) -> loadDashboardFromPreferences(sharedPreferences))
                        .show(getSupportFragmentManager(), TimelyBudgetSetupDialog.class.getSimpleName());
            }
        });

        llBudgetContainer.setVisibility(View.GONE);

        ivExpandBudget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (llBudgetContainer.getVisibility() == View.VISIBLE) {
                    collapse(llBudgetContainer);
                    animateRotate(ivExpandBudget, 0, 250);
                } else {
                    expand(llBudgetContainer);
                    animateRotate(ivExpandBudget, 180, 250);
                }

            }
        });

        viewModel = ViewModelProviders.of(this).get(AppViewModel.class);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        loadDashboardFromPreferences(sharedPreferences);
    }

    private void loadDashboardFromPreferences(SharedPreferences sharedPreferences) {
        Log.d(TAG, "loadDashboardFromPreferences: Loading dash from preferences. Categories must be initialized...");
        loadBudgetsFromPreferences(sharedPreferences);
        loadBarchartFromPreferences(sharedPreferences);
        loadStandartDashboardFromPreferences(sharedPreferences);
        loadAverageDailyFromPreferences(sharedPreferences);
    }

    private void loadAverageDailyFromPreferences(SharedPreferences sharedPreferences) {
        if(sharedPreferences.getBoolean(SettingsFragmentDashboard.KEY_SHOW_AVERAGE_DAILY, true)){
            clAverageDaily.setVisibility(View.VISIBLE);

            viewModel.getExpensesApplyFilterSort(FilterSortUtils.FILTER_DATE_LAST_YEAR, FilterSortUtils.ORDER_BY_DATE_DESC)
                    .observe(this, new Observer<List<Expense>>() {
                        @Override
                        public void onChanged(List<Expense> expenses) {
                            new GetAverageDailyAsyncTask(getCurrentActivity()).execute(expenses);
                        }
                    });
        }else{
            clAverageDaily.setVisibility(View.GONE);
        }
    }

    private void loadStandartDashboardFromPreferences(SharedPreferences sharedPreferences) {
        viewModel.getExpensesApplyFilterSort(FilterSortUtils.FILTER_DATE_LAST_MONTH, FilterSortUtils.ORDER_BY_DATE_DESC)
                .observe(this, new Observer<List<Expense>>() {
                    @Override
                    public void onChanged(List<Expense> expenses) {
                        new PrepareMainDashboardAsyncTask(getCurrentActivity()).execute(expenses);
                    }
                });

        viewModel.getExpensesApplyFilterSort(FilterSortUtils.FILTER_DATE_ALL, FilterSortUtils.ORDER_BY_DATE_DESC)
                .observe(this, new Observer<List<Expense>>() {
                    @Override
                    public void onChanged(List<Expense> expenses) {
                        new CalculateTimelyBudgetAsyncTask(getCurrentActivity()).execute(expenses);
                    }
                });
    }

    private void loadBarchartFromPreferences(SharedPreferences sharedPreferences) {
        if(sharedPreferences.getBoolean(SettingsFragmentDashboard.KEY_SHOW_BARCHART, true)){
            llBarChart.setVisibility(View.VISIBLE);
            viewModel.getExpensesApplyFilterSort(FilterSortUtils.FILTER_DATE_LAST_WEEK, FilterSortUtils.ORDER_BY_DATE_DESC)
                    .observe(this, new Observer<List<Expense>>() {
                        @Override
                        public void onChanged(List<Expense> expenses) {
                            new PrepareBarChartAsyncTask(getCurrentActivity()).execute(expenses);
                        }
                    });
        }else{
            llBarChart.setVisibility(View.GONE);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        loadStandartDashboardFromPreferences(sharedPreferences);
    }

    private void loadBudgetsFromPreferences(SharedPreferences sharedPreferences){
        if(sharedPreferences.getBoolean(ActivitySettings.KEY_BUDGETING_ENABLED, true)){
            llMonthlyBudgetField.setVisibility(View.VISIBLE);
            llBudgetContainer.setVisibility(View.GONE);

            viewModel.getExpensesApplyFilterSort(FilterSortUtils.FILTER_DATE_LAST_MONTH, FilterSortUtils.ORDER_BY_DATE_DESC)
                    .observe(this, new Observer<List<Expense>>() {
                        @Override
                        public void onChanged(List<Expense> expenses) {
                            new GetBudgetAsyncTask(getCurrentActivity()).execute(expenses);
                        }
                    });
        }else{
            llMonthlyBudgetField.setVisibility(View.GONE);
            llBudgetContainer.setVisibility(View.GONE);
        }
    }

    private void setNavbarViews() {
        mainCoordinatorLayout = findViewById(R.id.main_coordinator);
        bottomAppBar = findViewById(R.id.bottom_app_bar);
        mainFab = findViewById(R.id.main_fab);
        ivGoHome = findViewById(R.id.iv_go_home);
        ivGoSettings = findViewById(R.id.iv_go_settings);
        ivGoList = findViewById(R.id.iv_go_all_purchases);
        ivGoStatistics = findViewById(R.id.iv_go_statistics);
    }

    private void setNavbarClicklisteners() {
        mainFab.setOnClickListener((view -> {
            Intent intent = new Intent(view.getContext(), ActivityChooseCategory.class);
            startActivity(intent);
        }));

        ivGoHome.setOnClickListener((view -> {
            Intent intent = new Intent(view.getContext(), ActivityMainDashboard.class);
            startActivity(intent);
            finish();
        }));

        ivGoList.setOnClickListener((view -> {
            Intent intent = new Intent(view.getContext(), ActivityExpenseList.class);
            startActivity(intent);
        }));

        ivGoStatistics.setOnClickListener((view -> {
            Intent intent = new Intent(this, ActivityStatistics.class);
            startActivity(intent);
        }));

        ivGoSettings.setOnClickListener((view -> {
            Intent intent = new Intent(this, ActivitySettings.class);
            startActivity(intent);
        }));
    }


    private void animateRotate(View view, float degrees, int duration) {
        view.animate()
                .setDuration(duration)
                .rotation(degrees);
    }

    private Activity getCurrentActivity() {
        return this;
    }

    public static void expand(final View v) {
        int matchParentMeasureSpec = View.MeasureSpec.makeMeasureSpec(((View) v.getParent()).getWidth(), View.MeasureSpec.EXACTLY);
        int wrapContentMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        v.measure(matchParentMeasureSpec, wrapContentMeasureSpec);
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? ViewGroup.LayoutParams.WRAP_CONTENT
                        : (int) (targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // Expansion speed of 1dp/ms
        a.setDuration((int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        a.setDuration((int)(initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);

    }

//    public static Account createSyncAccount(Context context){
//////        Account account = new Account(ACCOUNT, ACCOUNT_TYPE);
////        AccountManager accountManager = (AccountManager) context.getSystemService(ACCOUNT_SERVICE);
////
////        if(accountManager.addAccountExplicitly(account, null, null)){
////
////        }else{
////
////        }
////
////        return account;
//    }
}
