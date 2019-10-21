package com.yordan.finance.view.ui.activities;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.yordan.finance.R;
import com.yordan.finance.view.ui.fragments.SettingsFragmentRoot;

public class ActivitySettings extends AppCompatActivity {

    private static final String TAG = "Settings Activity";

    public static final String KEY_NOTIFICATION_INTERVAL = "notifInterval";
    public static final String KEY_NOTIFICATION_ENABLED = "notifkey";
    public static final String KEY_BUDGETING_ENABLED = "budget_pref_switch";
    public static final String KEY_BUDGET_CATEGORY_1 = "preference_category_budget_1";
    public static final String KEY_BUDGET_CATEGORY_2 = "preference_category_budget_2";
    public static final String KEY_BUDGET_CATEGORY_3 = "preference_category_budget_3";
    public static final String KEY_BUDGET_CATEGORY_4 = "preference_category_budget_4";
    public static final String KEY_BUDGET_CATEGORY_5 = "preference_category_budget_5";
    public static final String KEY_BUDGET_CATEGORY_6 = "preference_category_budget_6";
    public static final String KEY_BUDGET_CATEGORY_7 = "preference_category_budget_7";
    public static final String KEY_BUDGET_CATEGORY_8 = "preference_category_budget_8";
    public static final String KEY_BUDGET_CATEGORY_9 = "preference_category_budget_9";
    public static final String[] KEYS_FOR_BUDGET = {KEY_BUDGET_CATEGORY_1, KEY_BUDGET_CATEGORY_2,
            KEY_BUDGET_CATEGORY_3, KEY_BUDGET_CATEGORY_4, KEY_BUDGET_CATEGORY_5,
            KEY_BUDGET_CATEGORY_6,KEY_BUDGET_CATEGORY_7,KEY_BUDGET_CATEGORY_8,
            KEY_BUDGET_CATEGORY_9};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragmentRoot())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }










}