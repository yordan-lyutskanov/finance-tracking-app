package com.yordan.finance.view.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.yordan.finance.R;
import com.yordan.finance.model.Category;
import com.yordan.finance.model.Subcategory;
import com.yordan.finance.utils.CategoriesUtils;
import com.yordan.finance.view.adapter.CategoriesAdapter;

import java.util.HashMap;
import java.util.List;

public class ActivityChooseCategory extends AppCompatActivity {

    private static final String KEY_EXTRA_CATEGORY = "SUBCATEGORY";
    ExpandableListView expandableListView;
    CategoriesAdapter adapter;
    BottomAppBar bottomAppBar;

    CoordinatorLayout mainCoordinatorLayout;
    FloatingActionButton mainFab;

    ImageView ivGoHome;
    ImageView ivGoList;
    ImageView ivGoSettings;
    ImageView ivGoStatistics;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_info);

        expandableListView = findViewById(R.id.expense_category_expandable_listview);
        setNavbarViews();
        setNavBarClickListeners();

        adapter = new CategoriesAdapter(this);
        expandableListView.setAdapter(adapter);
        expandableListView.setGroupIndicator(null);

        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int i) {
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
            }
        });

        expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int i) {
                bottomAppBar.animate()
                        .translationY(0f)
                        .alpha(1.0f)
                        .setDuration(300)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                                super.onAnimationEnd(animation);
                                bottomAppBar.setVisibility(View.VISIBLE);
                            }
                        });
            }
        });

        expandableListView.setOnChildClickListener((parent, view, groupPosition, childPosition, id) -> {
            Intent intent = new Intent(this, ActivityAddExpense.class);

            int category = (groupPosition + 1) * 10 + childPosition + 1;
            intent.putExtra(KEY_EXTRA_CATEGORY, category);
            startActivity(intent);
            finish();
            return true;
        });
        
        initListData();
    }

    private void initListData() {
        adapter.notifyDataSetChanged();
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

        }));
    }

}
