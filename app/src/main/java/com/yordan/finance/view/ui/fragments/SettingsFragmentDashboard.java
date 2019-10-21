package com.yordan.finance.view.ui.fragments;

import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.yordan.finance.R;

public class SettingsFragmentDashboard extends PreferenceFragmentCompat {

    public static final String KEY_SHOW_BARCHART = "show_bar_chart";
    public static final String KEY_SHOW_AVERAGE_DAILY = "show_average_daily";

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.dashboard_settings, rootKey);

        findPreference(KEY_SHOW_BARCHART).setSummaryProvider(new Preference.SummaryProvider<SwitchPreferenceCompat>() {
            @Override
            public CharSequence provideSummary(SwitchPreferenceCompat preference) {
                if(preference.isChecked()){
                    return "Barchart is enabled";
                }else{
                    return "Barchart is disabled";
                }
            }
        });

        findPreference(KEY_SHOW_AVERAGE_DAILY).setSummaryProvider(new Preference.SummaryProvider<SwitchPreferenceCompat>() {
            @Override
            public CharSequence provideSummary(SwitchPreferenceCompat preference) {
                if(preference.isChecked()){
                    return "Average daily is enabled";
                }else{
                    return "Average daily is disabled";
                }
            }
        });
    }
}
