package com.yordan.finance.view.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.yordan.finance.R;
import com.yordan.finance.model.Expense;
import com.yordan.finance.utils.FilterSortUtils;
import com.yordan.finance.view.adapter.PurchaseRvAdapter;
import com.yordan.finance.view.ui.dialogs.ChangeExpenseDialog;
import com.yordan.finance.viewmodel.AppViewModel;

import java.util.List;

public class ActivityExpenseList extends AppCompatActivity {

    public static final String TAG = ActivityExpenseList.class.getSimpleName();
    private static final String NUM_DELETED_EXPENSES = "NumberOfDeletedExpenses" ;
    private static final String NUM_ITEMS_DELETED = "NumberOfItemsDeleted";

    private AppViewModel expenseViewModel;
    RecyclerView mPurchasesRv;
    PurchaseRvAdapter adapter;
    CardView cvFilterTab;
    TextView tvFilterName;
    TextView tvShowing;
    TextView tvTotalPurchases;
    FrameLayout flPurchaseList;
    static String activeFilter = "No active filter";

    BottomAppBar bottomAppBar;

    CoordinatorLayout mainCoordinatorLayout;
    CoordinatorLayout bottomAppBarCoordinator;
    CoordinatorLayout clRoot;
    FloatingActionButton mainFab;

    ImageView ivGoHome;
    ImageView ivGoList;
    ImageView ivGoSettings;
    ImageView ivGoStatistics;

    int mTotalExpenses;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_list);

        setSupportActionBar(findViewById(R.id.toolbar));

        expenseViewModel = ViewModelProviders.of(this).get(AppViewModel.class);

        cvFilterTab = findViewById(R.id.cv_filter_tab);
        tvFilterName = findViewById(R.id.filter_name);
        flPurchaseList = findViewById(R.id.fl_purchase_list);
        tvTotalPurchases = findViewById(R.id.tv_total);
        tvShowing = findViewById(R.id.tv_showing);
        bottomAppBarCoordinator = findViewById(R.id.cl_appbar_coordinator);
        clRoot = findViewById(R.id.cl_root);

        adapter = new PurchaseRvAdapter(this);
        mPurchasesRv = findViewById(R.id.rv_purchases);
        mPurchasesRv.setHasFixedSize(true);
        mPurchasesRv.setLayoutManager(new LinearLayoutManager(this));
        mPurchasesRv.setAdapter(adapter);
        ((SimpleItemAnimator) mPurchasesRv.getItemAnimator()).setSupportsChangeAnimations(false);

        setNavbarViews();
        setNavBarClickListeners();


        expenseViewModel.getSortedExpenses().observe(this, new Observer<List<Expense>>() {
            @Override
            public void onChanged(List<Expense> expenses) {
                mTotalExpenses = expenses.size();
                tvShowing.setText(mTotalExpenses + "");
                tvTotalPurchases.setText(" / " + mTotalExpenses);
                adapter.setExpenses(expenses);
            }
        });

        tvFilterName.setText(activeFilter);

        mPurchasesRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(dy > 0){
                    animateToolbarExit();
                }else{
                    animateToolbarEntry();
                }
            }
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
               removeItemFromList(viewHolder.getAdapterPosition());
            }

        }).attachToRecyclerView(mPurchasesRv);

        registerForContextMenu(mPurchasesRv);
    }

    private void removeItemFromList(int position){
        Expense tempExpense = adapter.getExpenseAt(position);

        expenseViewModel.deleteExpense(tempExpense);
        Snackbar snackbar =  Snackbar.make(clRoot, "Item removed.", Snackbar.LENGTH_LONG);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) snackbar.getView().getLayoutParams();
        params.setMargins(0 , 0, 0, mPurchasesRv.getHeight());
        snackbar.getView().setLayoutParams(params);
        expenseWasDeleted();

        snackbar.setAction("Undo", (v) -> {
            expenseViewModel.addExpense(tempExpense);
            List<Expense> newExpenses = adapter.getExpenses();
            newExpenses.add(position, tempExpense);
            adapter.setExpenses(newExpenses);
            expenseDeleteWasUndone();
        }).show();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.recent_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.item_filter_date_today:
                FilterSortUtils.setActiveFilter(FilterSortUtils.FILTER_DATE_TODAY);
                reloadData();
                Toast.makeText(this, "Today", Toast.LENGTH_SHORT).show();
                break;
            case R.id.item_filter_date_yesterday:
                FilterSortUtils.setActiveFilter(FilterSortUtils.FILTER_DATE_YESTERDAY);
                reloadData();
                break;
            case R.id.item_filter_date_week:
                FilterSortUtils.setActiveFilter(FilterSortUtils.FILTER_DATE_LAST_WEEK);
                reloadData();
                break;
            case R.id.item_filter_date_month:
                FilterSortUtils.setActiveFilter(FilterSortUtils.FILTER_DATE_LAST_MONTH);
                reloadData();
                break;
            case R.id.item_filter_date_year:
                FilterSortUtils.setActiveFilter(FilterSortUtils.FILTER_DATE_LAST_YEAR);
                reloadData();
                break;
            case R.id.item_filter_date_all:
                FilterSortUtils.setActiveFilter(FilterSortUtils.FILTER_DATE_ALL);
                reloadData();
                Toast.makeText(this, "All", Toast.LENGTH_SHORT).show();
                break;

            case R.id.item_filter_category_1:
                FilterSortUtils.setActiveFilter(FilterSortUtils.FILTER_CATEGORY_HOUSING);
                reloadData();
                break;

            case R.id.item_filter_category_2:
                FilterSortUtils.setActiveFilter(FilterSortUtils.FILTER_CATEGORY_UTILITIES);
                reloadData();
                break;

            case R.id.item_filter_category_3:
                FilterSortUtils.setActiveFilter(FilterSortUtils.FILTER_CATEGORY_GROCERIES);
                reloadData();
                break;

            case R.id.item_filter_category_4:
                FilterSortUtils.setActiveFilter(FilterSortUtils.FILTER_CATEGORY_TRANSPORTATION);
                reloadData();
                break;

            case R.id.item_filter_category_5:
                FilterSortUtils.setActiveFilter(FilterSortUtils.FILTER_CATEGORY_PERSONAL);
                reloadData();
                break;

            case R.id.item_filter_category_6:
                FilterSortUtils.setActiveFilter(FilterSortUtils.FILTER_CATEGORY_ENTERTAINMENT);
                reloadData();
                break;
            case R.id.item_filter_category_7:
                FilterSortUtils.setActiveFilter(FilterSortUtils.FILTER_CATEGORY_HEALTH);
                reloadData();
                break;
            case R.id.item_filter_category_8:
                FilterSortUtils.setActiveFilter(FilterSortUtils.FILTER_CATEGORY_SAVINGS);
                reloadData();
                break;
            case R.id.item_filter_category_9:
                FilterSortUtils.setActiveFilter(FilterSortUtils.FILTER_CATEGORY_OTHERS);
                reloadData();
                break;
            case R.id.item_sort_cat_asc:
                FilterSortUtils.setActiveSort(FilterSortUtils.ORDER_BY_CATEGORY_ASC);
                reloadData();
                break;
            case R.id.item_sort_cat_dsc:
                FilterSortUtils.setActiveSort(FilterSortUtils.ORDER_BY_CATEGORY_DESC);
                reloadData();
                break;
            case R.id.item_sort_date_asc:
                FilterSortUtils.setActiveSort(FilterSortUtils.ORDER_BY_DATE_ASC);
                reloadData();
                break;
            case R.id.item_sort_date_dsc:
                FilterSortUtils.setActiveSort(FilterSortUtils.ORDER_BY_DATE_DESC);
                reloadData();
                break;
            case R.id.item_sort_amount_asc:
                FilterSortUtils.setActiveSort(FilterSortUtils.ORDER_BY_AMOUNT_ASC);
                reloadData();
                break;
            case R.id.item_sort_amount_dsc:
                FilterSortUtils.setActiveSort(FilterSortUtils.ORDER_BY_AMOUNT_DESC);
                reloadData();
                break;
        }

        if(FilterSortUtils.getActiveFilter() == null
                || FilterSortUtils.getActiveFilter().isEmpty()){
            activeFilter = "No active filter.";
        }else{
            if(item.getItemId() != R.id.item_filter
            && item.getItemId() != R.id.item_root_date
            && item.getItemId() != R.id.item_root_cat){
                activeFilter = item.getTitle().toString();
            }
        }

        tvFilterName.setText(activeFilter);

        return super.onOptionsItemSelected(item);
    }

    private void reloadData(){
        expenseViewModel.getSortedExpenses().observe(this, new Observer<List<Expense>>() {
            @Override
            public void onChanged(List<Expense> expenses) {
                //Update recycler view
                tvShowing.setText(expenses.size() + "");
                adapter.setExpenses(expenses);
            }
        });
    }



    private void setNavbarViews(){
        mainCoordinatorLayout = findViewById(R.id.main_coordinator);
        bottomAppBar = findViewById(R.id.bottom_app_bar);
        mainFab = findViewById(R.id.main_fab);
        ivGoHome = findViewById(R.id.iv_go_home);
        ivGoSettings = findViewById(R.id.iv_go_settings);
        ivGoList = findViewById(R.id.iv_go_all_purchases);
        ivGoStatistics = findViewById(R.id.iv_go_statistics);
    }

    private void setNavBarClickListeners(){
        mainFab.setOnClickListener((view -> {
            Intent intent = new Intent(this, ActivityChooseCategory.class);
            startActivity(intent);
            finish();
        }));

        ivGoHome.setOnClickListener((view -> {
            Intent intent = new Intent(view.getContext(), ActivityMainDashboard.class);
            startActivity(intent);
            finish();
        }));

        ivGoList.setOnClickListener((view -> {
            Intent intent = new Intent(view.getContext(), ActivityExpenseList.class);
            startActivity(intent);
            finish();
        }));

        ivGoStatistics.setOnClickListener((view -> {
            Intent intent = new Intent(this, ActivityStatistics.class);
            startActivity(intent);
            finish();
        }));

        ivGoSettings.setOnClickListener((view -> {
            Intent intent = new Intent(this, ActivitySettings.class);
            startActivity(intent);
            finish();
        }));
    }

    private void animateToolbarExit(){
        bottomAppBar.animate()
                .translationY(bottomAppBar.getHeight())
                .alpha(0.0f)
                .setDuration(300)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        bottomAppBar.setVisibility(View.GONE);
                    }
                });

        mainFab.animate()
                .alpha(0.0f)
                .setDuration(300)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        bottomAppBar.setVisibility(View.GONE);
                    }
                });
    }

    private void animateToolbarEntry(){
        bottomAppBar.animate()
                .translationY(0f)
                .alpha(1f)
                .setDuration(300)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        bottomAppBar.setVisibility(View.VISIBLE);
                    }
                });

        mainFab.animate()
                .alpha(1f)
                .setDuration(300)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        bottomAppBar.setVisibility(View.VISIBLE);
                    }
                });
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        int position = adapter.getPosition();

        switch (item.getItemId()){
            case R.id.popup_item_edit:
                ChangeExpenseDialog dialog = new ChangeExpenseDialog(adapter.getExpenseAt(position), expenseViewModel);
                dialog.show(getSupportFragmentManager(), "TAG");
                break;
            case R.id.popup_item_remove:
                removeItemFromList(position);
                break;
        }

        return super.onContextItemSelected(item);
    }

    private void expenseWasDeleted(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        int numExpensesDeleted = sharedPreferences.getInt(NUM_DELETED_EXPENSES, 0);
        sharedPreferences.edit().putInt(NUM_DELETED_EXPENSES, ++numExpensesDeleted).apply();

        int numItemsDeleted = sharedPreferences.getInt(NUM_ITEMS_DELETED, 0);
        sharedPreferences.edit().putInt(NUM_ITEMS_DELETED, ++numExpensesDeleted).apply();
    }

    private void expenseDeleteWasUndone(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        int numExpensesDeleted = sharedPreferences.getInt(NUM_DELETED_EXPENSES, 0);
        sharedPreferences.edit().putInt(NUM_DELETED_EXPENSES, --numExpensesDeleted).apply();

        int numItemsDeleted = sharedPreferences.getInt(NUM_ITEMS_DELETED, 0);
        sharedPreferences.edit().putInt(NUM_ITEMS_DELETED, --numExpensesDeleted).apply();
    }
}
