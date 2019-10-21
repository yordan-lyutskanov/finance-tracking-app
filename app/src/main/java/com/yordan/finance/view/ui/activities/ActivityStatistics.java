package com.yordan.finance.view.ui.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.yordan.finance.R;
import com.yordan.finance.view.adapter.DashboardPageAdapter;
import com.yordan.finance.view.ui.fragments.LineFragment;
import com.yordan.finance.view.ui.fragments.PieFragment;

public class ActivityStatistics extends AppCompatActivity {

    public static final String TAG = ActivityStatistics.class.getSimpleName();

    DashboardPageAdapter dashboardPageAdapter;
    ViewPager pager;
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash);

        pager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tabs);

        dashboardPageAdapter = new DashboardPageAdapter(getSupportFragmentManager());
        dashboardPageAdapter.addFragment(new PieFragment(), "Category chart");
        dashboardPageAdapter.addFragment(new LineFragment(), "Daily chart");

        pager.setAdapter(dashboardPageAdapter);
        tabLayout.setupWithViewPager(pager);

    }
}